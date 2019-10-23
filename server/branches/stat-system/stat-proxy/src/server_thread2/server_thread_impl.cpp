
#include <unistd.h>
#include <sys/types.h>
#include <string.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <fcntl.h>
#include <errno.h>
#include <stdio.h>
#include <assert.h>
#include <new>
#include <sys/epoll.h>

#include "server_thread_impl.h"
#include "log.h"

#define MAX_SERVER_NUM  64
#define MAX_CLIENT_NUM  1024
#define RESPONSE_QUEUE_PACK_LEN (sizeof(sp_message_t))

int create_server_thread_instance2(i_server_thread** pp_instance)
{
    if(NULL == pp_instance)
    {
        return -1;
    }

    c_server_thread_impl* p_instance = new (std::nothrow) c_server_thread_impl();
    if(NULL == p_instance)
    {
        return -1;
    }
    else
    {
        *pp_instance = dynamic_cast<i_server_thread*>(p_instance);
        return 0;
    }
}

/**
 *@brief make server key according to addr and port
 */
int make_server_key(int addr,short port)
{
    memcpy((char*)&addr,&port,sizeof(port));
    return addr;
}

c_server_thread_impl::c_server_thread_impl()
{
    m_server_key_map.clear();
    m_server_connecting_map.clear();

    m_p_routed_queue = NULL;
    m_p_response_queue = NULL;
    m_work_thread_id = 0;
    m_continue_working = 0;
    m_last_error = 0;
    m_epoll_fd = -1;
    m_inited = 0;
}

c_server_thread_impl::~c_server_thread_impl()
{
    uninit();
}

/**
 *@brief init server_thread module
 *@param p_routed_queue: routed queue instance
 *@param p_response_queue: response queue instance
 *@param p_server_info: all server's info
 *@param server_item_count: server's count
 *@return 0 if success,-1 if error occured
 */
int c_server_thread_impl::init(i_ring_queue* p_routed_queue,i_ring_queue* p_response_queue,const server_item_t* p_server_info,int server_item_count)
{
    if(m_inited)
    {
        return -1;
    }

    ///check params
    if(NULL == p_routed_queue || NULL == p_response_queue || NULL == p_server_info || server_item_count < 1)
    {
        return -1;
    }

    ///init server key map
    m_server_key_map.clear();
    m_server_connecting_map.clear();

    for(int index = 0; index < server_item_count; index++)
    {
        static item_info_t item_info;
        ///memset(&item_info,0,sizeof(item_info));
        item_info.addr = (p_server_info + index)->addr;
        item_info.port = (p_server_info + index)->port;
        item_info.server_key = make_server_key(item_info.addr,item_info.port);
        item_info.fd = -1;
        item_info.status = 0;
        item_info.send_buffer_data_len = 0;
        item_info.recv_buffer_data_len = 0;
        item_info.last_active_time = 0;
        item_info.last_connected_time = 0;

        m_server_key_map.insert(std::make_pair<int,item_info_t>(item_info.server_key,item_info));
    }

    ///create epoll fd
    m_epoll_fd = epoll_create(MAX_SERVER_NUM);
    if(m_epoll_fd < 0)
    {
        m_server_key_map.clear();
        m_server_connecting_map.clear();
        m_inited = 0;
        return -1;
    }

    ///init queue variables
    m_p_routed_queue = p_routed_queue;
    m_p_response_queue = p_response_queue;

    ///at last,create work thread
    m_continue_working = 1; /**< allow continue working first */
    int result = pthread_create(&m_work_thread_id,NULL,work_thread_proc,this);
    if(result != 0)
    {
        m_work_thread_id = 0;
        m_continue_working = 0;
        m_server_key_map.clear();
        m_server_connecting_map.clear();
        close(m_epoll_fd);
        m_epoll_fd = -1;
        m_work_thread_id = 0;
        m_p_routed_queue = NULL;
        m_p_response_queue = NULL;
        m_inited = 0;

        return -1;
    }

    m_last_error = 0;
    m_inited = 1;
    DEBUG_LOG("server_thread2 init successfully!");
    return 0;
}

/**
 *@brief uninit server_thread module
 *@return 0 if success,-1 if error occured
 *@note stop work thread and release all resources,restore all variables at last
 */
int c_server_thread_impl::uninit()
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
    server_key_map_t::iterator iterator = m_server_key_map.begin();
    for(; iterator != m_server_key_map.end(); ++iterator)
    {
        if(iterator->second.fd >= 0)
        {
            close(iterator->second.fd);
        }
    }

    ///restore all variables
    m_server_key_map.clear();
    m_server_connecting_map.clear();

    close(m_epoll_fd);
    m_epoll_fd = -1;
    m_p_routed_queue = NULL;
    m_p_response_queue = NULL;
    m_work_thread_id = 0;
    m_continue_working = 0;
    m_last_error = 0;
    m_inited = 0;

    return 0;
}

/**
 *@brief create socket,set nonblock mode and connect to server
 *@param addr: server ip addr in network byte order
 *@param port: server port in host byte order
 *@return fd if create socket and init connection successfully, -1 if failed
 */
int c_server_thread_impl::connect_to_server(int addr,short port)
{
    int fd = socket(AF_INET,SOCK_STREAM,0);
    if(fd < 0)
    {
        return -1;
    }

    ///mark non-block
    int flags = fcntl(fd,F_GETFL);
    flags |= O_NONBLOCK;
    fcntl(fd,F_SETFL,flags);

    sockaddr_in server_addr;
    memset(&server_addr,0,sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = addr;
    server_addr.sin_port = htons(port);

    int result = connect(fd,(sockaddr*)&server_addr,sizeof(server_addr));
    if(result < 0)
    {
        if(errno == EINPROGRESS || errno == EISCONN)
        {
            return fd;
        }
        else
        {
            close(fd);
            return -1;
        }
    }
    else if(result == 0)
    {
        return fd;
    }
    else
    {
        close(fd);
        return -1;
    }
}

/**
 *@brief send data and deal special error code
 *@param fd: socket file descriptor
 *@param p_data: data to send
 *@param data_len: data length to send
 *@return bytes sent or -1 if error occured or 0 if EAGAIN or EINTR
 */
int c_server_thread_impl::send_to_server(int fd,char* p_data,int data_len)
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

/**
 *@brief receive data and deal special error code
 *@param fd: socket file descriptor
 *@param p_recv_buffer: receive buffer
 *@param buffer_len: receive buffer length
 *@return bytes received or -1 if error occured or 0 if connection gracelly colsed
 */
int c_server_thread_impl::recv_from_server(int fd,char* p_recv_buffer,int buffer_len)
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

/**
 *@brief place data into send buffer atomically
 *@param p_item_info: server item info
 *@param p_data: data to place
 *@param data_len: data length to place
 *@return data length actually place into send buffer or -1 if data length large than total buffer length
 *@note operation are atomical and assume send buffer is empty
 */
int c_server_thread_impl::add_data_to_send_buffer(item_info_t* p_item_info,char* p_data,int data_len)
{
    if(data_len > (int)sizeof(p_item_info->send_buffer))
    {
        return -1;
    }

    memcpy(p_item_info->send_buffer,p_data,data_len);
    p_item_info->send_buffer_data_len = data_len;
    return data_len;
}

/**
 *@brief append data into send buffer atomically
 *@param p_item_info: server item info
 *@param p_data: data to append
 *@param data_len: data length to append
 *@return data length actually appended into send buffer or -1 if data length large than empty buffer length
 *@note operation are atomical
 */
int c_server_thread_impl::append_data_to_send_buffer(item_info_t* p_item_info,char* p_data,int data_len)
{
    if(data_len + p_item_info->send_buffer_data_len > (int)sizeof(p_item_info->send_buffer))
    {
        return -1;
    }

    memcpy(p_item_info->send_buffer + p_item_info->send_buffer_data_len,p_data,data_len);
    p_item_info->send_buffer_data_len += data_len;
    return data_len;
}

/**
 *@brief remove data from send buffer,start from header
 *@param p_item_info: server item info
 *@param data_len: data length to remove
 *@param data length actually removed or -1 if data length to removed large than data length in send buffer
 */
int c_server_thread_impl::remove_data_from_send_buffer(item_info_t* p_item_info,int data_len)
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

/**
 *@brief remove data from recv buffer,start from header
 *@param p_item_info: server item info
 *@param data_len: data length to remove
 *@param data length actually removed or -1 if data length to removed large than data length in recv buffer
 */
int c_server_thread_impl::remove_data_from_recv_buffer(item_info_t* p_item_info,int data_len)
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

/**
 *@brief init connection and insert into server_connecting_map for later process
 *@param p_item_info: server item info
 *@return 0 if success or -1 if error occured
 */
int c_server_thread_impl::deal_unconnected_server(item_info_t* p_item_info)
{
    assert(p_item_info-> fd < 0);

    ///connect to server
    p_item_info->fd = connect_to_server(p_item_info->addr,p_item_info->port);
    if(p_item_info->fd < 0)
    {
        return -1;
    }

    ///add fd to server_connecting_map
    m_server_connecting_map.insert(std::make_pair<int,item_info_t*>(p_item_info->fd,p_item_info));

    ///init item info
    p_item_info->status = 0;
    p_item_info->send_buffer_data_len = 0;
    p_item_info->recv_buffer_data_len = 0;

    DEBUG_LOG("connecting to server! => %d:%d",p_item_info->addr,p_item_info->port);
    return 0;
}

/**
 *@brief check connect result and deal it according to result
 *@param p_item_info: server item info
 *@return 0 if success or -1 if error occured
 *@note it's caller's responsbility to check whether fd is writable and
 *      fd is not removed from server_connecting_map in this function
 */
int c_server_thread_impl::deal_connecting_server(item_info_t* p_item_info)
{
    assert(p_item_info->fd >= 0);

    ///check connect result
    int opt = -1;
    socklen_t opt_len = sizeof(opt);
    getsockopt(p_item_info->fd,SOL_SOCKET,SO_ERROR,&opt,&opt_len);

    if(opt)
    {
        ///connect failed
        close(p_item_info->fd);
        p_item_info->fd = -1;
        p_item_info->status = 0;
        p_item_info->send_buffer_data_len = 0;
        p_item_info->recv_buffer_data_len = 0;

        DEBUG_LOG("connect to server failed! => %d:%d",p_item_info->addr,p_item_info->port);
    }
    else
    {
        ///connect success
        epoll_event event;
        event.events = EPOLLIN | EPOLLET;
        event.data.ptr = (void*)p_item_info;
        if(epoll_ctl(m_epoll_fd,EPOLL_CTL_ADD,p_item_info->fd,&event))
        {
            close(p_item_info->fd);
            p_item_info->fd = -1;
            p_item_info->status = 0;
            p_item_info->send_buffer_data_len = 0;
            p_item_info->recv_buffer_data_len = 0;

            return -1;
        }

        p_item_info->status = 1;
        p_item_info->recv_buffer_data_len = 0;
        p_item_info->last_connected_time = time(NULL);

        DEBUG_LOG("connect to server success! => %d:%d",p_item_info->addr,p_item_info->port);
    }

    return 0;
}

/**
 *@brief release resources after connection closed
 *@param p_item_info: server item info
 *@return always 0
 */
int c_server_thread_impl::deal_disconnected_server(item_info_t* p_item_info)
{
    assert(p_item_info->fd >= 0);
    assert(p_item_info->status != 0);

    close(p_item_info->fd); /**< automatically removed from epoll */
    p_item_info->fd = -1;
    p_item_info->status = 0;
    p_item_info->send_buffer_data_len = 0;
    p_item_info->recv_buffer_data_len = 0;

    DEBUG_LOG("connection disconnected! => %d:%d",p_item_info->addr,p_item_info->port);
    return 0;
}

/**
 *@brief extract package from received data and push into response queue
 *@param p_item_info: server item info
 *@return item count pushed into response queue, 0 is valid
 */
int c_server_thread_impl::deal_recved_data(item_info_t* p_item_info)
{
    ///get package count
    int item_num = p_item_info->recv_buffer_data_len / RESPONSE_QUEUE_PACK_LEN;
    if(item_num <= 0)
    {
        return 0;
    }

    ///push to response queue
    for(int index = 0; index < item_num; index++)
    {
        m_p_response_queue->push_data(p_item_info->recv_buffer + (index * RESPONSE_QUEUE_PACK_LEN),RESPONSE_QUEUE_PACK_LEN,1);
    }

    ///remove dealed data
    remove_data_from_recv_buffer(p_item_info,item_num * RESPONSE_QUEUE_PACK_LEN);
    assert(p_item_info->recv_buffer_data_len < (int)RESPONSE_QUEUE_PACK_LEN);

    return item_num;
}

/**
 *@brief check connect result by iterate connecting_server_map
 *@return -1 if encounter unexpected error else 0
 */
int c_server_thread_impl::check_connecting_server_map()
{
    if(m_server_connecting_map.empty())
    {
        return 0;
    }

    ///check whether fd is writable
    fd_set write_set,error_set;
    FD_ZERO(&write_set);
    FD_ZERO(&error_set);

    server_connecting_map_t::iterator iterator = m_server_connecting_map.begin();
    for(; iterator != m_server_connecting_map.end(); ++iterator)
    {
        assert(iterator->second->fd >= 0);
        FD_SET(iterator->second->fd,&write_set);
        FD_SET(iterator->second->fd,&error_set);
    }

    timeval time_interval;
    memset(&time_interval,0,sizeof(time_interval));/**< no wait for select */

    int result = select(MAX_CLIENT_NUM + 1,NULL,&write_set,&error_set,&time_interval);
    if(result < 0)
    {
        if(errno == EINTR)
        {
            return 0;
        }
        else
        {
            return -1;
        }
    }
    else if(result == 0)
    {
        ///nothing to do
        return 0;
    }
    else
    {
        iterator = m_server_connecting_map.begin();
        for(; iterator != m_server_connecting_map.end(); ++iterator)
        {
            if(FD_ISSET(iterator->second->fd,&write_set) || FD_ISSET(iterator->second->fd,&error_set))
            {
                deal_connecting_server(iterator->second);

                server_connecting_map_t::iterator temp_iterator = iterator;
                m_server_connecting_map.erase(temp_iterator);/**< after item been erased,++iterator may encounter error */
            }
        }

        return 0;
    }
}

/**
 *@brief send data to connected server smartly
 *@param p_item_info: server item info
 *@param p_data: extra data to send
 *@param data_len: extra data length to send
 *@return always 0
 *@note if send buffer is empty,data is sent directly from p_data,avoid extra copy to send buffer
 */
int c_server_thread_impl::send_data_to_connected_server(item_info_t* p_item_info,char* p_data,int data_len)
{
    assert(p_item_info->fd >= 0 && p_item_info->status);

    if(p_item_info->send_buffer_data_len > 0)
    {
        ///append data to send buffer if has extra data
        if(p_data != NULL && data_len > 0)
        {
            append_data_to_send_buffer(p_item_info,p_data,data_len);
        }

        ///send to server
        int bytes_sent = send_to_server(p_item_info->fd,p_item_info->send_buffer,p_item_info->send_buffer_data_len);
        if(bytes_sent > 0)
        {
            ///remove data just sent
            remove_data_from_send_buffer(p_item_info,bytes_sent);
        }
        else if(bytes_sent < 0)
        {
            ///connection encounter error,close
            deal_disconnected_server(p_item_info);
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
            int bytes_sent = send_to_server(p_item_info->fd,p_data,data_len);
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
                deal_disconnected_server(p_item_info);
            }
        }
        else
        {
            ///nothing to do
        }
    }

    return 0;
}

/**
 *@brief receive data from server until no more data(ensure epoll ET mode work properly),and push into response queue
 *@param p_item_info: server item info
 *@return -1 if connection disconnected else 0
 */
int c_server_thread_impl::recv_all_data_from_server(item_info_t* p_item_info)
{
    while(true)
    {
        int empty_buffer_len = sizeof(p_item_info->recv_buffer) - p_item_info->recv_buffer_data_len;
        assert(empty_buffer_len > 0);
        int bytes_recved = recv_from_server(p_item_info->fd,p_item_info->recv_buffer + p_item_info->recv_buffer_data_len,empty_buffer_len);

        if(bytes_recved < 0)
        {
            ///encounter error
            deal_disconnected_server(p_item_info);
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

void* c_server_thread_impl::work_thread_proc(void* p_data)
{
    c_server_thread_impl* p_instance = (c_server_thread_impl*)p_data;
    assert(p_instance != NULL);

    int routed_queue_item_header_len = sizeof(route_message_header_t);
    epoll_event event_array[MAX_SERVER_NUM];
    char temp_buffer[1024 * 4];

    while(p_instance->m_continue_working)
    {
        time_t current_time_tik = time(NULL);
        int poped_item_count = 0;

        ///while to pop data from routed queue and send to server
        while(p_instance->m_continue_working)
        {
            ///each loop deal MAX_CLIENT_NUM package at most
            if(poped_item_count > MAX_CLIENT_NUM * 32)
            {
                break;
            }

            ///pop data from routed queue
            int data_len_poped = p_instance->m_p_routed_queue->pop_data(temp_buffer,sizeof(temp_buffer));
            if(data_len_poped <= 0)
            {
                break;
            }
            else
            {
                poped_item_count++;
            }

            routed_queue_item_t* p_routed_queue_item = reinterpret_cast<routed_queue_item_t*>(temp_buffer);

            ///check message type
            if(p_routed_queue_item->ps_header.type >= 0xFF000000)
            {
                ///directly send response package to client
                sp_message_t sp_pack;
                memset(&sp_pack,0,sizeof(sp_pack));
				sp_pack.channel_id = p_routed_queue_item->ps_header.channel_id;
                sp_pack.connection_id = p_routed_queue_item->ps_header.connection_id;
                sp_pack.type = p_routed_queue_item->ps_header.type;
                sp_pack.file_num = p_routed_queue_item->ps_header.file_num;
                sp_pack.seqno = p_routed_queue_item->ps_header.seqno;

                p_instance->m_p_response_queue->push_data((char*)&sp_pack,sizeof(sp_pack),1);
                continue;
            }

            ///find corresponding server
            server_key_map_t::iterator iterator = p_instance->m_server_key_map.find(p_routed_queue_item->route_header.server_key);
            if(iterator == p_instance->m_server_key_map.end())
            {
                ERROR_LOG("corrsponding server not found! server_key = %d",p_routed_queue_item->route_header.server_key);
                continue; /**< no corrsponding server exist,drop the package */
            }

            ///send data to server
            item_info_t* p_item_info = &(iterator->second);

            if(p_item_info->fd < 0)
            {
                if(p_instance->deal_unconnected_server(p_item_info))
                {
                    continue;
                }

                ///add data to send buffer
                p_instance->add_data_to_send_buffer(p_item_info,(char*)&p_routed_queue_item->ps_header,data_len_poped - routed_queue_item_header_len);
            }
            else
            {
                if(p_item_info->status)
                {
                    ///already connected to server,send data
                    p_instance->send_data_to_connected_server(p_item_info,(char*)&p_routed_queue_item->ps_header,data_len_poped - routed_queue_item_header_len);
                }
                else
                {
                    ///now connecting,append data to send buffer
                    p_instance->append_data_to_send_buffer(p_item_info,(char*)&p_routed_queue_item->ps_header,data_len_poped - routed_queue_item_header_len);
                }
            }
        }//while to pop data and send to server

        ///check whether need exit...
        if(!p_instance->m_continue_working)
        {
            break;
        }

        ///recv data from server and push into response queue
        do
        {
            int fd_num = epoll_wait(p_instance->m_epoll_fd,event_array,sizeof(event_array)/sizeof(epoll_event),1);
            if(fd_num <= 0)
            {
                break;
            }
            else
            {
                ///while to recv data from fds
                for(int index = 0; index < fd_num; index++)
                {
                    item_info_t* p_item_info = (item_info_t*)event_array[index].data.ptr;
                    assert(p_item_info != NULL);

                    if(event_array[index].events & EPOLLIN)
                    {
                        ///while to recv data until no more data or error occured,then push into response queue
                        if(p_instance->recv_all_data_from_server(p_item_info) == 0)
                        {
                            p_item_info->last_active_time = current_time_tik;
                        }
                    }

                    if(event_array[index].events & EPOLLERR || event_array[index].events & EPOLLHUP)
                    {
                        ///error occured
                        if(p_item_info->fd >= 0 && p_item_info->status)
                        {
                            DEBUG_LOG("epoll error event! server_key = %d",p_item_info->server_key);
                            p_instance->deal_disconnected_server(p_item_info);
                        }
                    }
                }//while to recv data from fds
            }
        }while(0);

        ///check whether need exit...
        if(!p_instance->m_continue_working)
        {
            break;
        }

        ///check connecting server map to find out connecting result
        p_instance->check_connecting_server_map();

        ///check whether need exit...
        if(!p_instance->m_continue_working)
        {
            break;
        }

        ///iterate server_key_map to send data
        server_key_map_t::iterator iterator = p_instance->m_server_key_map.begin();
        for(; iterator != p_instance->m_server_key_map.end(); ++iterator)
        {
            item_info_t* p_item_info = &(iterator->second);
            if(p_item_info->send_buffer_data_len > 0 && p_item_info->fd >= 0 && p_item_info->status )
            {
                p_instance->send_data_to_connected_server(p_item_info,NULL,0); /**< no extra data to send */
            }
        }

    }//while continue working

    return 0;
}

/**
 *@brief enumerate all servers
 *@param p_buffer: receive buffer
 *@param p_buffer_count[in/out] buffer length
 *@return server count in receive buffer
 */
int c_server_thread_impl::enum_servers(server_item_t* p_buffer, int* p_buffer_count)
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
    server_key_map_t::const_iterator iterator = m_server_key_map.begin();
    for(; iterator != m_server_key_map.end(); ++iterator)
    {
        if(item_count >= *p_buffer_count)
        {
            break;
        }

        (p_buffer + item_count)->addr = iterator->second.addr;
        (p_buffer + item_count)->port = iterator->second.port;
        (p_buffer + item_count)->last_active_time = iterator->second.last_active_time;
        item_count++;
    }

    *p_buffer_count = item_count;
    return item_count;
}


/**
 *@brief get server info according to server key
 *@param server_key: server key
 *@param p_buffer: receive buffer
 *@return 0 if success or -1 if encounter error
 */
int c_server_thread_impl::get_server_info(int server_key,server_item_t* p_buffer)
{
    if(!m_inited)
    {
        return -1;
    }

    server_key_map_t::const_iterator iterator = m_server_key_map.find(server_key);
    if(iterator == m_server_key_map.end())
    {
        return -1;
    }

    if(p_buffer)
    {
        p_buffer->addr = iterator->second.addr;
        p_buffer->port = iterator->second.port;
        p_buffer->last_active_time = iterator->second.last_active_time;
    }

    return 0;
}

int c_server_thread_impl::get_last_error()
{
    return m_last_error;
}

int c_server_thread_impl::release()
{
    delete this;
    return 0;
}
