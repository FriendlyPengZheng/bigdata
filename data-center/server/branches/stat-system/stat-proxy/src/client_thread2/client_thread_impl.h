
#ifndef CLIENT_THREAD_IMPL_H_20091201
#define CLIENT_THREAD_IMPL_H_20091201

#include <map>
#include <set>
#include <pthread.h>
#include "../i_client_thread.h"

#define CLIENT_THREAD_SEND_BUFFER_LEN  (1024 * 32)
#define CLIENT_THREAD_RECV_BUFFER_LEN  (1024 * 16)

class c_client_thread_impl : public i_client_thread
{
public:
    c_client_thread_impl();
    virtual ~c_client_thread_impl();
    virtual int init(i_ring_queue* p_unrouted_queue,i_ring_queue* p_response_queue,const char *p_ip,int port,client_item_t *p_client_item_list,int client_item_count);
    virtual int uninit();
    virtual int enum_clients(client_item_t* p_buffer, int* p_buffer_count);
    virtual int get_client_info(uint32_t connection_id, client_item_t* p_client_item);
    virtual int enum_undefined_clients(int* p_buffer,int buffer_length);
    virtual int clear_undefined_clients();
    virtual int get_last_error();
    virtual int release();

protected:
    typedef struct {
        int addr;
        int fd;
        uint32_t connection_id;
        char send_buffer[CLIENT_THREAD_SEND_BUFFER_LEN];
        int send_buffer_data_len;
        char recv_buffer[CLIENT_THREAD_RECV_BUFFER_LEN];
        int recv_buffer_data_len;
        uint32_t last_msg_id;
        time_t last_msg_time;
        time_t last_connected_time;
        time_t last_active_time;
		char remark[MAX_BUFFER_LENGTH];
    } item_info_t;

protected:
    typedef std::map<int,item_info_t> client_addr_map_t;
    typedef std::map<int,item_info_t*> client_id_map_t;
    typedef std::set<int> undefined_client_set_t;

protected:
    static void* work_thread_proc(void* p_data);
    static int set_nonblock(int fd);
    static int send_to_client(int fd,char* p_data,int data_len);
    static int recv_from_client(int fd,char* p_recv_buffer,int buffer_len);

protected:
    int get_connection_id();
    int add_data_to_send_buffer(item_info_t* p_item_info,char* p_data,int data_len);
    int append_data_to_send_buffer(item_info_t* p_item_info,char* p_data,int data_len);
    int remove_data_from_send_buffer(item_info_t* p_item_info,int data_len);
    int remove_data_from_recv_buffer(item_info_t* p_item_info,int data_len);
    int accept_all_connections();
    int deal_disconnected_client(item_info_t* p_item_info);
    int deal_connected_client(int accepted_fd,item_info_t* p_item_info);
    int deal_recved_data(item_info_t* p_item_info);
    int recv_all_data_from_client(item_info_t* p_item_info);
    int send_data_to_connected_client(item_info_t* p_item_info,char* p_data,int data_len);

protected:
    int m_inited;
    int m_epoll_fd;
    int m_listen_fd;
    int m_continue_working;
    int m_connection_id_base;
    int m_last_error;

    pthread_t m_work_thread_id;
    i_ring_queue* m_p_unrouted_queue;
    i_ring_queue* m_p_response_queue;

    client_addr_map_t m_client_addr_map;
    client_id_map_t m_client_id_map;

    undefined_client_set_t m_undefined_client_set;
    pthread_mutex_t m_undefined_client_set_mutex;
};

#endif//CLIENT_THREAD_IMPL_H_20091201
