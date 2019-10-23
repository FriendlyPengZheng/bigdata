
#ifndef WORK_PROC_H
#define WORK_PROC_H

#include <vector>
#include <map>
#include <list>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <mysql/mysql.h>

#include "i_timer.h"
#include "i_mysql_iface.h"
#include "i_config.h"
#include "i_proto_so.h"
#include "message.h"
#include "macro.h"

//以下定义的与时间有关的宏的单位均是秒
#define CLIENT_SEQNO_INTERVAL 60            /**< 更新客户端序列号到数据库的时间间隔 */
#define EXPIRE_TIME           10            /**< 子进程异常退出时的超时时间 */
#define TIMER_CHECK_INTERVAL  1             /**< 循环检查定时器的时间间隔 */

typedef struct {
    void *handle;
    int (*proto_init)(i_timer *p_timer, i_config *p_config);
    int (*get_proto_id)(uint32_t *p_proto_id, int *proto_count);
    int (*proto_process)(const ss_message_header_t &ss_msg_hdr, void *p_data);
    int (*proto_uninit)();
} proto_so_t;

typedef struct {
    uint32_t file_num; /**< 客户端的文件编号*/
    uint32_t seq_no;  /**< 客户端的文件内部序列号*/
    uint32_t flag;    /**< 消息处理结果: 0 成功 1 所有都失败 2 第一个数据成功，第二个数据失败 */
} client_seq_t;

typedef struct message_translate {
    struct message_translate *next; /**< 指向下一个结构,用于一个消息带多个report的情况*/
    uint32_t report_id;             /**< 统计项ID*/
    uint8_t arg_index;              /**< 该report_id在消息中的位置*/
    uint32_t proto_id;              /**< 协议ID*/
    uint32_t msg_len;               /**< 本协议对应的消息的长度*/
} message_translate_t;              /**< 消息映射结构,某个消息ID对应的统计项信息结构*/

class c_work_proc
{
public:
    c_work_proc();
    virtual ~c_work_proc();

    virtual int init(const char (*client_list)[16], uint32_t client_count, i_config *p_config);
    virtual int uninit();
    virtual int release();
    int check_timer();
    int process_poped_message(int fd, ps_message_t *ps_message); /**< 处理从环形队列中取出的一条消息*/

protected:
    int init_seqno();                               /**< 初始化客户端文件序列号 */
    int update_seqno_to_db();                       /**< 更新内存中记录的客户端序列号信息到数据库 */
    static int wrapper_update_seqno(void *p_object);/**<对update_seqno_to_db的包装作为回调函数传给定时器 */

    int event_translate_alloc(); // 初始化event_id 与event_type的映射关系.
    int event_translate_new(uint32_t event_id, uint32_t event_type);
    int event_translate_free();
    uint32_t event_get_event_type(uint32_t report_id, char *msg_data);
    int msg_translate_alloc();
    int msg_translate_new(uint32_t msg_id, uint32_t report_id, uint8_t arg_index, uint32_t proto_id, uint32_t msg_len);
    int msg_translate_free();

    int load_proto_so();                           /**< 载入so信息，即初始化m_proto_map */
    int open_mysql();                              /**< 打开mysql的连接*/
    int register_so(const char *so_file);          /**< 注册单个的so文件*/
    int unregister_so();                           /**< 释放so*/

    int message_process(ps_message_t *ps_message, int error_index);/**< 处理消息,将消息分发到相应的so*/

public:
    const char (*m_client_list)[16];
    uint32_t m_client_count;
    i_config *m_p_config;

    std::map<in_addr_t, client_seq_t> m_seq_map;/**< 客户端消息位置序列号的映射 */
    std::map<uint32_t, proto_so_t> m_proto_map;/**< 存放协议与so的映射,多个协议可能对应一个so */
    std::vector<proto_so_t> m_proto_vec;/**< 存放所有的so,不重复*/
    std::map<uint32_t, message_translate_t*> m_msg_translate_map; /**< 存放消息ID到统计项的映射*/
    std::map<uint32_t, uint32_t> m_event_translate_map;

    uint32_t m_page_size;          /**< 系统页大小*/
    i_mysql_iface *m_p_mysql;
	char m_client_seqno_table_name[64];

	i_timer *m_p_timer;
    i_timer::timer_id_t m_client_seq_timer_id;
};

#endif
