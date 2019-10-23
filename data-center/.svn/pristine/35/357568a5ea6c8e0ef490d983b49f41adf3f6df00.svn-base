
#ifndef SERVER_THREAD_IMPL_H_20091118
#define SERVER_THREAD_IMPL_H_20091118

#include <pthread.h>
#include <map>
#include "../i_server_thread.h"
#include "i_ring_queue.h"

#define SERVER_THREAD_RECV_BUFFER_LEN  (1024 * 64)
#define SERVER_THREAD_SEND_BUFFER_LEN  (1024 * 1024 * 2)

class c_server_thread_impl : public i_server_thread
{
public:
    c_server_thread_impl();
    virtual ~c_server_thread_impl();
    virtual int init(i_ring_queue* p_routed_queue,i_ring_queue* p_response_queue,const server_item_t* p_server_info,int server_item_count);
    virtual int uninit();
    virtual int enum_servers(server_item_t* p_buffer, int* p_buffer_count);
    virtual int get_server_info(int server_key,server_item_t* p_buffer);
    virtual int get_last_error();
    virtual int release();

protected:
    typedef struct {
        int addr; /**< server addr,in network byte order */
        short port; /**< server port,in host byte order */
        int server_key; /**< server identity make up of addr and port */
        int fd; /**< socket fd, -1 if not connected */
        int status; /**< indicate connection status; 0 indicate not connected else connected when fd >=0 */
        char send_buffer[SERVER_THREAD_SEND_BUFFER_LEN]; /**< send buffer for this server */
        int send_buffer_data_len; /**< data length in send buffer */
        char recv_buffer[SERVER_THREAD_RECV_BUFFER_LEN]; /**< receive buffer for this server */
        int recv_buffer_data_len; /**< data length in receive buffer */
        time_t last_active_time; /**< server's last active time, when received data from server,update it */
        time_t last_connected_time; /**< server's last connected time,when connected to server,update it */
    } item_info_t;

protected:
    typedef std::map<int,item_info_t> server_key_map_t;
    typedef std::map<int,item_info_t*> server_connecting_map_t; /**< servers which are in connecting status */

protected:
    static void* work_thread_proc(void* p_data);
    static int connect_to_server(int addr,short port);
    static int send_to_server(int fd,char* p_data,int data_len);
    static int recv_from_server(int fd,char* p_recv_buffer,int buffer_len);

protected:
    int add_data_to_send_buffer(item_info_t* p_item_info,char* p_data,int data_len);
    int append_data_to_send_buffer(item_info_t* p_item_info,char* p_data,int data_len);
    int remove_data_from_send_buffer(item_info_t* p_item_info,int data_len);
    int remove_data_from_recv_buffer(item_info_t* p_item_info,int data_len);
    int deal_unconnected_server(item_info_t* p_item_info);
    int deal_connecting_server(item_info_t* p_item_info);
    int deal_disconnected_server(item_info_t* p_item_info);
    int deal_recved_data(item_info_t* p_item_info);
    int check_connecting_server_map();
    int send_data_to_connected_server(item_info_t* p_item_info,char* p_data,int data_len);
    int recv_all_data_from_server(item_info_t* p_item_info);

protected:
    int m_inited; /**< module status */
    int m_continue_working; /**< indicate whether the work thread should continue to execute */
    int m_last_error;
    int m_epoll_fd; /**< epoll fd */

    i_ring_queue* m_p_routed_queue;
    i_ring_queue* m_p_response_queue;

    pthread_t m_work_thread_id;

    server_key_map_t m_server_key_map;
    server_connecting_map_t m_server_connecting_map;
};

#endif//SERVER_THREAD_IMPL_H_20091118
