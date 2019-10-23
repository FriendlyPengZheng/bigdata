
#include <unistd.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <fcntl.h>
#include <errno.h>
#include <stdio.h>
#include <assert.h>
#include <new>
#include <string.h>
#include <sys/epoll.h>

#include "log.h"
#include "client_thread_impl.h"
#include "../client_ip_filter/c_client_ip_filter_facade.h"

#define MAX_CLIENT_NUM 1024
#define MAX_SERVER_NUM 64

char g_pack_buffer[1024 * 16 + 8];

int create_client_thread_instance2(i_client_thread** pp_instance)
{
    if(NULL == pp_instance)
    {
        return -1;
    }

    c_client_thread_impl* p_instance = new (std::nothrow) c_client_thread_impl();
    if(NULL == p_instance)
    {
        return -1;
    }
    else
    {
        *pp_instance = dynamic_cast<i_client_thread*>(p_instance);
        return 0;
    }
}

c_client_thread_impl::c_client_thread_impl()
{
    m_epoll_fd = -1;
    m_listen_fd = -1;
    m_continue_working = 0;
    m_connection_id_base = 0;
    m_last_error = 0;
    m_work_thread_id = 0;
    m_p_unrouted_queue = NULL;
    m_p_response_queue = NULL;

    m_client_addr_map.clear();
    m_client_id_map.clear();
    m_inited = 0;
}

c_client_thread_impl::~c_client_thread_impl()
{
    uninit();
}

int c_client_thread_impl::init(i_ring_queue* p_unrouted_queue,i_ring_queue* p_response_queue,const char *p_ip,
                                int port,client_item_t *p_client_item_list,int client_item_count)
{
    if (m_inited) {
		ERROR_LOG("ERROR: already inited");
        return -1;
    }

    ///param check
    if(NULL == p_unrouted_queue || NULL == p_response_queue || p_ip == NULL || port <= 0 ||
       NULL == p_client_item_list || client_item_count < 1)
    {
		ERROR_LOG("ERROR: parameter error");
        return -1;
    }

    ///listen to network first
    m_listen_fd = socket(AF_INET,SOCK_STREAM,0);
    if(m_listen_fd < 0)
    {
		ERROR_LOG("ERROR: socket: %s", strerror(errno));
        return -1;
    }

    int opt_value = 1; /**< enable reuse address before bind */
    setsockopt(m_listen_fd,SOL_SOCKET,SO_REUSEADDR,&opt_value,sizeof(opt_value));

    sockaddr_in local_addr;
    memset(&local_addr, 0, sizeof(local_addr));
    local_addr.sin_family = AF_INET;
	if (inet_pton(AF_INET, p_ip, &local_addr.sin_addr) <= 0) {
		ERROR_LOG("inet_pton %s error: %s", p_ip, strerror(errno));
		return -1;
	}
    local_addr.sin_port = htons(port);

    if(bind(m_listen_fd,(sockaddr*)&local_addr,sizeof(local_addr)))
    {
		ERROR_LOG("ERROR: bind at %s:%u: %s", p_ip, port, strerror(errno));
        close(m_listen_fd);
        m_listen_fd = -1;
        return -1;
    }

    set_nonblock(m_listen_fd);

    if(listen(m_listen_fd,5))
    {
		ERROR_LOG("ERROR: listen: %s", strerror(errno));
        close(m_listen_fd);
        m_listen_fd = -1;
        return -1;
    }

    ///init client addr map
    for(int index = 0; index < client_item_count; index++)
    {
        item_info_t item_info;
        memset(&item_info,0,sizeof(item_info));
        item_info.addr = (p_client_item_list + index)->addr;
		strncpy(item_info.remark, (p_client_item_list + index)->remark, MAX_BUFFER_LENGTH - 1);
        item_info.fd = -1;

        m_client_addr_map.insert(std::make_pair<int,item_info_t>(item_info.addr,item_info));
    }

    ///create epoll
    m_epoll_fd = epoll_create(MAX_CLIENT_NUM);
    if(m_epoll_fd < 0)
    {
		ERROR_LOG("ERROR: epoll_create: %s", strerror(errno));
        close(m_listen_fd);
        m_listen_fd = -1;
        m_client_addr_map.clear();

        return -1;
    }

    ///put listen fd into epoll
    epoll_event event;
    memset(&event,0,sizeof(event));
    event.events = EPOLLIN | EPOLLET;
    event.data.ptr = (void*)m_listen_fd;
    epoll_ctl(m_epoll_fd,EPOLL_CTL_ADD,m_listen_fd,&event);

    ///init some variables which work thread will use
    m_client_id_map.clear();
    m_p_unrouted_queue = p_unrouted_queue;
    m_p_response_queue = p_response_queue;
    m_connection_id_base = 0;

    ///init undefined client set
    m_undefined_client_set.clear();
    pthread_mutex_init(&m_undefined_client_set_mutex,NULL);

    ///at last,create work thread
    m_continue_working = 1;
    if(pthread_create(&m_work_thread_id,NULL,work_thread_proc,this))
    {
		ERROR_LOG("ERROR: pthread_create: %s", strerror(errno));
        close(m_listen_fd);
        m_listen_fd = -1;
        m_client_addr_map.clear();
        close(m_epoll_fd);
        m_epoll_fd = -1;
        m_p_unrouted_queue = NULL;
        m_p_response_queue = NULL;
        pthread_mutex_destroy(&m_undefined_client_set_mutex);

        return -1;
    }

    m_last_error = 0;
    m_inited = 1;

    DEBUG_LOG("client_thread2 init successfully!");
    DEBUG_LOG("client_thread2 listen at %s:%d", p_ip, port);
    return 0;
}

int c_client_thread_impl::uninit()
{
    if(!m_inited)
    {
        return -1;
    }

    ///wait for work thread to exit
    assert(m_work_thread_id != 0);
    m_continue_working = 0;
    pthread_join(m_work_thread_id,NULL);

    ///close all connections
    client_id_map_t::iterator iterator = m_client_id_map.begin();
    for(; iterator != m_client_id_map.end(); ++iterator)
    {
        close(iterator->second->fd);
    }

    ///restore all variables
    m_client_id_map.clear();
    m_client_addr_map.clear();
    m_undefined_client_set.clear();
    close(m_listen_fd);
    close(m_epoll_fd);

    m_epoll_fd = -1;
    m_listen_fd = -1;
    m_continue_working = 0;
    m_connection_id_base = 0;
    m_last_error = 0;
    m_work_thread_id = 0;
    m_p_unrouted_queue = NULL;
    m_p_response_queue = NULL;
    pthread_mutex_destroy(&m_undefined_client_set_mutex);

    m_inited = 0;

    return 0;
}

int c_client_thread_impl::set_nonblock(int fd)
{
    int flags = fcntl(fd,F_GETFL);
    flags |= O_NONBLOCK;
    return fcntl(fd,F_SETFL,flags);
}

int c_client_thread_impl::get_connection_id()
{
    m_connection_id_base++;
    if(m_connection_id_base < 0)
    {
        m_connection_id_base = 1;
    }

    return m_connection_id_base;
}

int c_client_thread_impl::send_to_client(int fd,char* p_data,int data_len)
{
    assert(data_len > 0);
    int bytes_sent = send(fd,p_data,data_len,0);
    if(bytes_sent > 0)
    {
        return bytes_sent;
    }
    else if(bytes_sent == 0)
    {
        assert(false);
        return -1;
    }
    else
    {
        if(errno == EAGAIN || errno == EINTR)
        {
            return 0;
        }
        else
        {
            return -1;
        }
    }
}

int c_client_thread_impl::recv_from_client(int fd,char* p_recv_buffer,int buffer_len)
{
    assert(buffer_len > 0);
    int bytes_recved = recv(fd,p_recv_buffer,buffer_len,0);
    if(bytes_recved > 0)
    {
        return bytes_recved;
    }
    else if(bytes_recved == 0)
    {
        return -1; /**< gracefully closed */
    }
    else
    {
        if(errno == EAGAIN || errno == EINTR)
        {
            return 0;
        }
        else
        {
            return -1;
        }
    }
}

int c_client_thread_impl::add_data_to_send_buffer(item_info_t* p_item_info,char* p_data,int data_len)
{
    if(data_len > (int)sizeof(p_item_info->send_buffer))
    {
        return -1;
    }

    memcpy(p_item_info->send_buffer,p_data,data_len);
    p_item_info->send_buffer_data_len = data_len;
    return data_len;
}

int c_client_thread_impl::append_data_to_send_buffer(item_info_t* p_item_info,char* p_data,int data_len)
{
    if(data_len + p_item_info->send_buffer_data_len > (int)sizeof(p_item_info->send_buffer))
    {
        return -1;
    }

    memcpy(p_item_info->send_buffer + p_item_info->send_buffer_data_len,p_data,data_len);
    p_item_info->send_buffer_data_len += data_len;
    return data_len;
}

int c_client_thread_impl::remove_data_from_send_buffer(item_info_t* p_item_info,int data_len)
{
    if(data_len > p_item_info->send_buffer_data_len)
    {
        return -1;
    }
    else if(data_len == p_item_info->send_buffer_data_len)
    {
        p_item_info->send_buffer_data_len = 0;
        return data_len;
    }
    else
    {
        memmove(p_item_info->send_buffer,p_item_info->send_buffer + data_len,p_item_info->send_buffer_data_len - data_len);
        p_item_info->send_buffer_data_len -= data_len;
        return data_len;
    }
}

int c_client_thread_impl::remove_data_from_recv_buffer(item_info_t* p_item_info,int data_len)
{
    if(data_len > p_item_info->recv_buffer_data_len)
    {
        return -1;
    }
    else if(data_len == p_item_info->recv_buffer_data_len)
    {
        p_item_info->recv_buffer_data_len = 0;
        return data_len;
    }
    else
    {
        memmove(p_item_info->recv_buffer,p_item_info->recv_buffer + data_len,p_item_info->recv_buffer_data_len - data_len);
        p_item_info->recv_buffer_data_len -= data_len;
        return data_len;
    }
}

int c_client_thread_impl::deal_disconnected_client(item_info_t* p_item_info)
{
    //assert(p_item_info->fd >= 0);
    //assert(p_item_info->connection_id > 0);
    if(p_item_info->fd < 0 || p_item_info->connection_id <= 0) {
        ERROR_LOG("p_item_info->fd[%d] < 0 || p_item_info->connection_id[%d] <= 0", p_item_info->fd, p_item_info->connection_id);
		m_client_id_map.erase(p_item_info->connection_id);
        return -1;
    }
    DEBUG_LOG("close connection_id = %d,addr = %d",p_item_info->connection_id,p_item_info->addr);

    ///remove connection_id from client_id_map
    m_client_id_map.erase(p_item_info->connection_id);

    close(p_item_info->fd);/**< automatically removed from epoll */
    p_item_info->fd = -1;
    p_item_info->connection_id = 0;
    p_item_info->send_buffer_data_len = 0;
    p_item_info->recv_buffer_data_len = 0;

    return 0;
}

int c_client_thread_impl::deal_connected_client(int accepted_fd,item_info_t* p_item_info)
{
    assert(accepted_fd >= 0);
    set_nonblock(accepted_fd);

    epoll_event event;
    event.events = EPOLLIN | EPOLLET;
    event.data.ptr = (void*)p_item_info;
    epoll_ctl(m_epoll_fd,EPOLL_CTL_ADD,accepted_fd,&event);

    p_item_info->fd = accepted_fd;
    p_item_info->connection_id = get_connection_id();
    p_item_info->send_buffer_data_len = 0;
    p_item_info->recv_buffer_data_len = 0;
    p_item_info->last_connected_time = time(NULL);

    ///add to client_id_map
    m_client_id_map.insert(std::make_pair<int,item_info_t*>(p_item_info->connection_id,p_item_info));
    DEBUG_LOG("deal_connected_client = %d,addr = %d",p_item_info->connection_id,p_item_info->addr);
    return 0;
}

int c_client_thread_impl::accept_all_connections()
{
    while(m_continue_working)
    {
        sockaddr_in peer_addr;
        memset(&peer_addr,0,sizeof(peer_addr));
        socklen_t addr_len = sizeof(peer_addr);
        int accepted_fd = accept(m_listen_fd,(sockaddr*)&peer_addr,&addr_len);
        if(accepted_fd >= 0)
        {
            ///check peer addr
            client_addr_map_t::iterator iterator = m_client_addr_map.find(peer_addr.sin_addr.s_addr);
            if(iterator != m_client_addr_map.end())
            {
                ///check whether the client has connected
                if(iterator->second.fd >= 0)
                {
                    ///close current connection
                    deal_disconnected_client(&iterator->second);
                    DEBUG_LOG("client addr %d already connected,close the first!",iterator->second.addr);
                }

                deal_connected_client(accepted_fd,&iterator->second);
                DEBUG_LOG("accept connection from %d,connection_id = %d",peer_addr.sin_addr.s_addr,iterator->second.connection_id);
            }
            else
            {
                // client ip not in client_addr_map
                // check if the IP is legal, if yes, add it.
                c_client_ip_filter_facade ip_filter;
                if(ip_filter.do_filter(&peer_addr) == 0)
                {
                    DEBUG_LOG("client %d not in addr_map, but it is a legal IP, add it!",peer_addr.sin_addr.s_addr);

                    item_info_t new_cli;
                    std::pair <client_addr_map_t::iterator, bool> ret;
                    ret = m_client_addr_map.insert(std::make_pair<int, item_info_t>(peer_addr.sin_addr.s_addr, new_cli));
                    if(true == ret.second)
                    {
                        deal_connected_client(accepted_fd, &(ret.first->second));
                    }
                }
                else
                {
                    DEBUG_LOG("client %d not in addr_map, and it is a illegal IP, close it!",peer_addr.sin_addr.s_addr);
                    close(accepted_fd);
                }

                ///add to undefined client set
                pthread_mutex_lock(&m_undefined_client_set_mutex);
                m_undefined_client_set.insert(peer_addr.sin_addr.s_addr);
                pthread_mutex_unlock(&m_undefined_client_set_mutex);
            }
        }///if accepted_fd > 0
        else
        {
            if(errno == EAGAIN)
            {
                break;
            }
            else if(errno == EINTR)
            {
                continue;
            }
            else
            {
                return -1;
            }
        }
    }///while

    return 0;
}

int c_client_thread_impl::deal_recved_data(item_info_t* p_item_info)
{
    char* p_data = p_item_info->recv_buffer;
    int data_len = p_item_info->recv_buffer_data_len;
    time_t current_time_tik = time(NULL);
    int total_bytes_dealed = 0;
    uint32_t last_msg_id = 0;
    time_t last_msg_time = 0;

    while(m_continue_working && data_len >= (int)sizeof(cp_message_header_t))
    {
        short* p_pack_len = reinterpret_cast<short*>(p_data);
        if(*p_pack_len > (int)sizeof(p_item_info->recv_buffer) || *p_pack_len < (int)sizeof(cp_message_header_t))
        {
            ///encounter unexpected error,close connection
            deal_disconnected_client(p_item_info);
            return -1;
        }

        if(*p_pack_len > data_len)
        {
            break;
        }

        int body_len = *p_pack_len - sizeof(cp_message_header_t);
        memcpy(g_pack_buffer,p_data,sizeof(cp_message_header_t));
        memcpy(g_pack_buffer + sizeof(cp_message_header_t) + 8,p_data + sizeof(cp_message_header_t),body_len);

        ps_message_header_t* p_ps_msg = reinterpret_cast<ps_message_header_t*>(g_pack_buffer);
        p_ps_msg->len = *p_pack_len + 8;
        p_ps_msg->cli_addr = p_item_info->addr;
        p_ps_msg->connection_id = p_item_info->connection_id;

        //DEBUG_LOG("m_p_unrouted_queue->push_data(%p,%d)", g_pack_buffer,p_ps_msg->len);
        m_p_unrouted_queue->push_data(g_pack_buffer,p_ps_msg->len,1);

        last_msg_id = p_ps_msg->type;
        last_msg_time = p_ps_msg->timestamp;

        p_data += *p_pack_len;
        data_len -= *p_pack_len;
        total_bytes_dealed += *p_pack_len;
    }///while to process data

    ///remove dealed data from recv buffer
    if(total_bytes_dealed > 0)
    {
        remove_data_from_recv_buffer(p_item_info,total_bytes_dealed);
    }

    if(last_msg_id > 0)
    {
        p_item_info->last_msg_id = last_msg_id;
        p_item_info->last_msg_time = last_msg_time;
        p_item_info->last_active_time = current_time_tik;
    }

    return total_bytes_dealed;
}

int c_client_thread_impl::recv_all_data_from_client(item_info_t* p_item_info)
{
    while(true)
    {
        int empty_buffer_len = sizeof(p_item_info->recv_buffer) - p_item_info->recv_buffer_data_len;
        assert(empty_buffer_len > 0);
        int bytes_recved = recv_from_client(p_item_info->fd,p_item_info->recv_buffer + p_item_info->recv_buffer_data_len,empty_buffer_len);

        if(bytes_recved < 0)
        {
            ///encounter error
            deal_disconnected_client(p_item_info);
            return -1;
        }
        else
        {
            p_item_info->recv_buffer_data_len += bytes_recved;
            deal_recved_data(p_item_info);
        }

        if(bytes_recved < empty_buffer_len)
        {
            break;
        }
    }

    return 0;
}

int c_client_thread_impl::send_data_to_connected_client(item_info_t* p_item_info,char* p_data,int data_len)
{
    assert(p_item_info->fd >= 0 && p_item_info->connection_id > 0);

    if(p_item_info->send_buffer_data_len > 0)
    {
        ///append data to send buffer if has extra data
        if(p_data != NULL && data_len > 0)
        {
            append_data_to_send_buffer(p_item_info,p_data,data_len);
        }

        ///send to client
        int bytes_sent = send_to_client(p_item_info->fd,p_item_info->send_buffer,p_item_info->send_buffer_data_len);
        if(bytes_sent > 0)
        {
            ///remove data just sent
            remove_data_from_send_buffer(p_item_info,bytes_sent);
        }
        else if(bytes_sent < 0)
        {
            ///connection encounter error,close
            deal_disconnected_client(p_item_info);
        }
        else
        {
            ///unable to send data,nothing to do
        }
    }
    else /**< send_buffer is empty */
    {
        if(p_data != NULL && data_len > 0)
        {
            ///send data directly from p_data,avoid extra copy to send_buffer
            int bytes_sent = send_to_client(p_item_info->fd,p_data,data_len);
            if(bytes_sent >= 0)
            {
                if(bytes_sent < data_len)
                {
                    ///not all data has been sent,add to send buffer
                    add_data_to_send_buffer(p_item_info,p_data + bytes_sent,data_len - bytes_sent);
                }
            }
            else
            {
                ///connection encounter error
                deal_disconnected_client(p_item_info);
            }
        }
        else
        {
            ///nothing to do
        }
    }

    return 0;
}

void* c_client_thread_impl::work_thread_proc(void* p_data)
{
    c_client_thread_impl* p_instance = (c_client_thread_impl*)p_data;
    assert(p_data != NULL);
    char temp_buffer[1024 * 4];
    epoll_event event_array[MAX_CLIENT_NUM];

    while(p_instance->m_continue_working)
    {
        ///accept connection or receive data from client
        int fd_num = epoll_wait(p_instance->m_epoll_fd,event_array,sizeof(event_array)/sizeof(epoll_event),1);
        if(fd_num < 0)
        {
            ///error
            DEBUG_LOG("epoll_wait error = %d",errno);
        }
        else if(fd_num == 0)
        {
            ///nothing to do
        }
        else
        {
            for(int index = 0; index < fd_num; index++)
            {
                if(event_array[index].events & EPOLLIN)
                {
                    if(event_array[index].data.ptr == (void *)p_instance->m_listen_fd)
                    {
                        p_instance->accept_all_connections();
                    }
                    else
                    {
                        item_info_t* p_item_info = (item_info_t*)event_array[index].data.ptr;
                        assert(p_item_info != NULL);

                        p_instance->recv_all_data_from_client(p_item_info);
                    }
                }

                if(event_array[index].events & EPOLLERR)
                {
                    if(event_array[index].data.ptr != (void *)p_instance->m_listen_fd)
                    {
                        item_info_t* p_item_info = (item_info_t*)event_array[index].data.ptr;
                        assert(p_item_info != NULL);

                        if(p_item_info->fd >= 0)
                        {
                            p_instance->deal_disconnected_client(p_item_info);
                        }
                    }
                }
            }///for
        }///else fd_num > 0

        ///check whether need exit...
        if(!p_instance->m_continue_working)
        {
            break;
        }

        ///pop data from response queue and send to client
        int poped_item_count = 0;
        while(p_instance->m_continue_working)
        {
            if(poped_item_count > MAX_CLIENT_NUM * 64)
            {
                break;
            }

            int data_len_poped = p_instance->m_p_response_queue->pop_data(temp_buffer,sizeof(temp_buffer));
            if(data_len_poped <= 0)
            {
                break;
            }
            else
            {
                poped_item_count++;
            }

            assert(data_len_poped == sizeof(sp_message_t));
            sp_message_t* p_sp_msg = reinterpret_cast<sp_message_t*>(temp_buffer);
            client_id_map_t::iterator iterator = p_instance->m_client_id_map.find(p_sp_msg->connection_id);
            if(iterator == p_instance->m_client_id_map.end())
            {
                ///cann't found corresponding connection_id,drop the data
                continue;
            }

            item_info_t* p_item_info = iterator->second;
            assert(p_item_info->fd >= 0);

            p_instance->send_data_to_connected_client(p_item_info,(char*)&p_sp_msg->type,sizeof(sp_message_t) - sizeof(p_sp_msg->connection_id));
        }

        ///check whether need exit...
        if(!p_instance->m_continue_working)
        {
            break;
        }

        ///iterate all client to send data
        client_addr_map_t::iterator iterator = p_instance->m_client_addr_map.begin();
        for(; iterator != p_instance->m_client_addr_map.end(); ++iterator)
        {
            item_info_t* p_item_info = &(iterator->second);
            if(p_item_info->send_buffer_data_len > 0 && p_item_info->fd >= 0)
            {
                p_instance->send_data_to_connected_client(p_item_info,NULL,0);
            }
        }

    }///while to working

    return 0;
}

int c_client_thread_impl::enum_clients(client_item_t* p_buffer, int* p_buffer_count)
{
    if(!m_inited)
    {
        return -1;
    }

    if(NULL == p_buffer || NULL == p_buffer_count || *p_buffer_count < 1)
    {
        return -1;
    }

    int item_count = 0;
    client_addr_map_t::iterator iterator = m_client_addr_map.begin();
    for(; iterator != m_client_addr_map.end(); ++iterator)
    {
        if(item_count >= *p_buffer_count)
        {
            break;
        }

        //(p_buffer + item_count)->addr = iterator->second.addr;
        (p_buffer + item_count)->addr = iterator->first;
        (p_buffer + item_count)->connection_id = iterator->second.connection_id;
        (p_buffer + item_count)->last_msg_id = iterator->second.last_msg_id;
        (p_buffer + item_count)->last_msg_time = iterator->second.last_msg_time;
        (p_buffer + item_count)->last_active_time = iterator->second.last_active_time;
		strncpy((p_buffer + item_count)->remark, iterator->second.remark, MAX_BUFFER_LENGTH - 1);
        item_count++;
    }

    *p_buffer_count = item_count;
    return item_count;
}

int c_client_thread_impl::get_client_info(uint32_t connection_id, client_item_t* p_client_item)
{
    if(!m_inited)
    {
        return -1;
    }

    client_id_map_t::iterator iterator = m_client_id_map.find(connection_id);
    if(iterator == m_client_id_map.end())
    {
        return -1;
    }

    if(p_client_item)
    {
        p_client_item->connection_id = connection_id;
        p_client_item->last_msg_id = iterator->second->last_msg_id;
        p_client_item->last_msg_time = iterator->second->last_msg_time;
        p_client_item->last_active_time = iterator->second->last_active_time;
		strncpy(p_client_item->remark, iterator->second->remark, MAX_BUFFER_LENGTH - 1);
    }

    return 0;
}

int c_client_thread_impl::enum_undefined_clients(int* p_buffer,int buffer_length)
{
    if(!m_inited)
    {
        return -1;
    }

    if(NULL == p_buffer || buffer_length < 1)
    {
        return -1;
    }

    pthread_mutex_lock(&m_undefined_client_set_mutex);

    int item_count = 0;
    undefined_client_set_t::iterator iterator = m_undefined_client_set.begin();
    for(; iterator != m_undefined_client_set.end(); ++iterator)
    {
        if(item_count >= buffer_length)
        {
            break;
        }

        *(p_buffer + item_count) = *iterator;
        item_count++;
    }

    pthread_mutex_unlock(&m_undefined_client_set_mutex);
    return item_count;
}

int c_client_thread_impl::clear_undefined_clients()
{
    if(!m_inited)
    {
        return -1;
    }

    pthread_mutex_lock(&m_undefined_client_set_mutex);
    m_undefined_client_set.clear();
    pthread_mutex_unlock(&m_undefined_client_set_mutex);

    return 0;
}

int c_client_thread_impl::get_last_error()
{
    return m_last_error;
}

int c_client_thread_impl::release()
{
    delete this;
    return 0;
}
