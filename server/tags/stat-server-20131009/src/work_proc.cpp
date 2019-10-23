#include <stdio.h>
#include <iostream>
#include <algorithm>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>
#include <time.h>
#include <sys/types.h>
#include <sys/mman.h>
#include <signal.h>
#include <dirent.h>
#include <dlfcn.h>
#include <errno.h>
#include <new>
#include <map>
#include <list>

#include <async_server.h>
#include <stat_protocal.h>

#include "log.h"
#include "work_proc.h"


using namespace std;

/**
 * @brief  构造函数
 * @param
 * @return
 */
c_work_proc::c_work_proc(): m_page_size(0), m_p_mysql(NULL), m_p_timer(NULL)
{
    memset(m_client_seqno_table_name, 0, sizeof(m_client_seqno_table_name));
	m_seq_map.clear();
    m_proto_map.clear();
    m_proto_vec.clear();
    m_msg_translate_map.clear();
    m_event_translate_map.clear();
}

/**
 * @brief  析构函数
 * @param
 * @return
 */
c_work_proc::~c_work_proc()
{
    uninit();
}

/**
 * @brief  work_proc的初始化
 * @param  client_list 本进程处理的客户端列表
 * @param  client_count 本进程处理的客户端个数
 * @param  p_request_queue
 * @param  p_response_queue
 * @param  p_config
 * @return  0success -1failed  >0child_pid
 */
int c_work_proc::init(const char (*client_list)[16], uint32_t client_count, i_config *p_config)
{
    if(client_list == NULL || client_count <= 0 || p_config == NULL)
    {
        ERROR_LOG("parameter error. %p %u %p", client_list, client_count, p_config);
        return -1;
    }

    m_client_list = client_list;
    m_client_count = client_count;
    m_p_config = p_config;

	memset(m_client_seqno_table_name, 0, sizeof(m_client_seqno_table_name));
	if (m_p_config->get_config("work-proc", "client_seqno_table_name",
				           m_client_seqno_table_name, sizeof(m_client_seqno_table_name) - 1) != 0) {
        ERROR_LOG("ERROR: config: work-proc: client_seqno_table_name");
		return -1;
	}

    m_page_size = sysconf(_SC_PAGESIZE);
    if (open_mysql() != 0) {
        ERROR_LOG("open_mysql failed.");
        return -1;
    }

    DEBUG_LOG("init client seqno ...");
    if (init_seqno() != 0) {
        ERROR_LOG("init_seqno failed.");
        return -1;
    }

	if (update_seqno_to_db() != 0) {
        ERROR_LOG("ERROR: update_seqno_to_db.");
        return -1;
	}

	if (create_timer_instance(&m_p_timer) != 0) {
        ERROR_LOG("ERROR: create_timer_instance");
        return -1;
	}

	if (m_p_timer->init() != 0) {
        ERROR_LOG("ERROR: m_p_timer->init");
        return -1;
	}

	m_client_seq_timer_id = m_p_timer->add(CLIENT_SEQNO_INTERVAL, c_work_proc::wrapper_update_seqno, this);
	if (m_client_seq_timer_id == -1) {
        ERROR_LOG("ERROR: m_p_timer->add");
        return -1;
	}

    DEBUG_LOG("allocate message translate ...");
    if (msg_translate_alloc() != 0) {
        ERROR_LOG("allocate message translate failed.");
        return -1;
    }

    DEBUG_LOG("allocate event translate ...");
    if (event_translate_alloc() != 0) {
        ERROR_LOG("allocate event translate failed.");
        return -1;
    }

    DEBUG_LOG("load proto so ...");
    if (load_proto_so() != 0) {
        ERROR_LOG("load proto_so failed.");
        return -1;
    }

    return 0;
}

/**
 * @brief  work_proc 的uninit操作
 * @param
 * @return  0success -1failed
 */
int c_work_proc::uninit()
{
	if (update_seqno_to_db() == -1)  {
		ERROR_LOG("ERROR: update_seqno_to_db");
		return -1;
	};

    DEBUG_LOG("unregister so ...");
    unregister_so();

	DEBUG_LOG("free message translate ...");
    msg_translate_free();

    DEBUG_LOG("free event translate ...");
    event_translate_free();

    if(m_client_seq_timer_id > 0)
    {
        if (m_p_timer->del(m_client_seq_timer_id) != 0) {
            ERROR_LOG("ERROR: m_p_timer->del");
            return -1;
        }
        m_client_seq_timer_id = 0;
    }

	if (m_p_timer->uninit() != 0) {
		ERROR_LOG("ERROR: m_p_timer->uninit");
		return -1;
	}
	if (m_p_timer->release() != 0) {
		ERROR_LOG("ERROR: m_p_timer->release");
		return -1;
	}



    m_seq_map.clear();
    m_p_timer = NULL;
    m_proto_map.clear();
    m_proto_vec.clear();
    m_msg_translate_map.clear();

	m_p_mysql->uninit();
    m_p_mysql->release();

    m_page_size = 0;

    m_p_mysql = NULL;

    return 0;
}

int c_work_proc::release()
{
    delete this;
    return 0;
}

int c_work_proc::check_timer()
{
    m_p_timer->check();
    return 0;
}

/**
 * @brief 打开数据库的连接
 * @param   p_config 配置模块
 * @return  0success -1failed
 */
int c_work_proc::open_mysql()
{
    char db_host[16] = {'\0'};
    char db_user[64] = {'\0'};
    char db_pass[64] = {'\0'};
    char db_name[64] = {'\0'};
    char db_port_buffer[6] = "3306";

    if(m_p_config->get_config("database","db_host",db_host,sizeof(db_host)) ||
       m_p_config->get_config("database","db_user",db_user,sizeof(db_user)) ||
       m_p_config->get_config("database","db_passwd",db_pass,sizeof(db_pass)) ||
       m_p_config->get_config("database","db_name",db_name,sizeof(db_name)) ||
       m_p_config->get_config("database","db_port",db_port_buffer,sizeof(db_port_buffer))
      )
    {
        ERROR_LOG("p_config.get_config failed.");
        return -1;
    }
    int db_port = atoi(db_port_buffer);
    if(db_port <0 || db_port > 65536)
    {
        ERROR_LOG("db_port not valid.");
        return -1;
    }

    if(create_mysql_iface_instance(&m_p_mysql) != 0)
    {
        ERROR_LOG("ERROR: create_mysql_iface_instance.");
        return -1;
    }

    if(m_p_mysql->init(db_host, db_port, db_name, db_user, db_pass, "utf8") != 0)
    {
		ERROR_LOG("ERROR: p_mysql->init: %s", m_p_mysql->get_last_errstr());
		m_p_mysql->uninit();
		m_p_mysql->release();
		return -1;

    }
    return 0;
}

/**
 * @brief  初始化客户端序列号,读取数据库的client_seqno
 * @param   client_list 本work进程处理的客户端列表
 * @param   client_count 本work进程处理的客户端个数
 * @return  0success -1failed
 */
int c_work_proc::init_seqno()
{
    if(m_client_list == NULL || m_client_count <= 0)
    {
        ERROR_LOG("client_list is null or client_count less than zero");
        return -1;
    }

    uint32_t index = 0;
    char str[40960] = {'\0'};
    sprintf(str,"SELECT host_ip, file_num, seq_no, flag FROM %s WHERE ", m_client_seqno_table_name);

    client_seq_t client_seq;
    client_seq.file_num = 0;
    client_seq.seq_no = 0;
    client_seq.flag = 0;

    for(index = 0; index < m_client_count; index++)
    {
        struct sockaddr_in cli_addr;
        if(inet_pton(AF_INET, m_client_list[index], &cli_addr.sin_addr) <= 0)
        {
            ERROR_LOG("inet_pton %s error.", m_client_list[index]);
            return -1;
        }
        m_seq_map.insert(pair<in_addr_t, client_seq_t>(cli_addr.sin_addr.s_addr,client_seq));
        char host_ip[32] = {'\0'};
        if(index + 1 == m_client_count)
        {
            sprintf(host_ip,"host_ip=%u;\n",cli_addr.sin_addr.s_addr);
        }
        else
        {
            sprintf(host_ip,"host_ip=%u||",cli_addr.sin_addr.s_addr);
        }
        strcat(str,host_ip);
    }

    in_addr_t client_addr;
    map<in_addr_t, client_seq_t>::iterator iter = m_seq_map.end();

    MYSQL_ROW row = NULL;
    if(m_p_mysql->select_first_row(&row, str) < 0)
    {
        ERROR_LOG("ERROR: %s", str);
        return -1;
    }

    while(row != NULL)
    {
        if(NULL == row[0] || NULL == row[1] || NULL == row[2] || NULL == row[3])
        {
            ERROR_LOG("rows must not be NULL.");
            return -1;
        }

        client_addr = atoi(row[0]);
        iter = m_seq_map.find(client_addr);
        if(iter != m_seq_map.end())
        {
            iter->second.file_num = atoi(row[1]);
            iter->second.seq_no = atoi(row[2]);
            iter->second.flag = atoi(row[3]);
        }
        iter = m_seq_map.end();
        row = m_p_mysql->select_next_row(false);
    }

    return 0;
}

int c_work_proc::event_translate_free()
{
    m_event_translate_map.clear();
    return 0;
}

/** 
 * @brief 新建一个event_id 与event_type的映射.
 * @param
 * @return  0success -1failed
 */
int c_work_proc::event_translate_new(uint32_t event_id, uint32_t event_type)
{
    DEBUG_LOG("new event_id: %u, event_type: %u", event_id, event_type);
    m_event_translate_map.insert(std::make_pair<uint32_t, uint32_t>(event_id, event_type));

    return 0;
}

uint32_t c_work_proc::event_get_event_type(uint32_t report_id, char *msg_data)
{
    if(NULL == msg_data)
        return 0;

    uint32_t event_id = 0;
    uint16_t data_len = *((uint16_t*)msg_data);
    if(data_len == 0)
        return 0;

    if(report_id == AOTEMAN_REPORT_ID)
    {
        if(data_len < sizeof(aoteman_data_t))
            return 0;

        aoteman_data_t *data = (aoteman_data_t *)(msg_data + sizeof(uint16_t));
        event_id = data->event_id; 
    }
    else if(report_id == ZHANSHEN_REPORT_ID)
    {
        if(data_len < sizeof(zhanshen_data_t))
            return 0;

        zhanshen_data_t *data = (zhanshen_data_t *)(msg_data + sizeof(uint16_t));
        event_id = data->event_id;
    }

    std::map<uint32_t, uint32_t>::const_iterator it;
    
    it = m_event_translate_map.find(event_id);
    
    if(it != m_event_translate_map.end())
    {
        return it->second;
    }
    else 
    {
        //DEBUG_LOG("event type not found: %u, event_id: %u", it->second, event_id);
        return 0;
    }
}

/** 
 * @brief 初始化event_id 与event_type的映射关系.
 * @param
 * @return  0success -1failed
 */
int c_work_proc::event_translate_alloc()
{
    const char *select_str = "SELECT event_id,event_type FROM t_event_list;";
    MYSQL *p_tmp_sql = m_p_mysql->get_conn();
    if(p_tmp_sql == NULL)
    {
        ERROR_LOG("lost mysql connection when try to get event translate.");
        return -1;
    }

    MYSQL_ROW row = NULL;
    if(m_p_mysql->select_first_row(&row, select_str) < 0)
    {
        ERROR_LOG("run %s failed.", select_str);
        return -1;
    }

    while(row != NULL)
    {
        if(NULL == row[0] || NULL == row[1])
        {
            ERROR_LOG("row is NULL.");
            return -1;
        }

        if(event_translate_new(atoi(row[0]), atoi(row[1])) != 0)
        {
            ERROR_LOG("event translate new failed.");
            return -1;
        }
        row = m_p_mysql->select_next_row(false);
    }

    return 0;
}

/**
 * @brief  初始化消息到统计项的映射
 * @param
 * @return  0success -1failed
 */
int c_work_proc::msg_translate_alloc()
{
    char select_str[512] = {0};
    sprintf(select_str,"%s","SELECT mt.msg_id, mt.report_id, mt.arg_index, mp.id, mp.data_len "
                       "FROM t_message_protocol AS mp INNER JOIN t_report AS r ON r.proto_id = mp.id "
                        "INNER JOIN t_message_translate AS mt ON mt.report_id = r.id ORDER BY mt.arg_index");
    MYSQL *p_tmp_sql = m_p_mysql->get_conn();
    if(p_tmp_sql == NULL)
    {
        ERROR_LOG("p_tmp_sql is NULL.");
        return -1;
    }

    MYSQL_ROW row = NULL;
    if(m_p_mysql->select_first_row(&row, select_str) < 0)
    {
        ERROR_LOG("ERROR: %s", select_str);
        return -1;
    }

    while(row != NULL)
    {
        if(NULL == row[0] || NULL == row[1] || NULL == row[2] || NULL == row[3] || NULL == row[4])
        {
            ERROR_LOG("row must not be NULL.");
            return -1;
        }

        if(msg_translate_new(atoi(row[0]), atoi(row[1]), atoi(row[2]), atoi(row[3]),
                        atoi(row[4])) != 0)
        {
            ERROR_LOG("msg_translate_new failed.");
            return -1;
        }
        row = m_p_mysql->select_next_row(false);
    }

    return 0;
}

/**
 * @brief  释放消息与统计项的映射结构
 * @param
 * @return
 */
int c_work_proc::msg_translate_free()
{
    map<uint32_t, message_translate_t*>::iterator iter = m_msg_translate_map.begin();
    for(; iter != m_msg_translate_map.end(); iter++)
    {
        message_translate_t *p_tmp_mt = iter->second;
        if(p_tmp_mt)
        {
            while(p_tmp_mt->next)
            {
                message_translate_t *p_next = p_tmp_mt->next;
                delete p_tmp_mt;
                p_tmp_mt = p_next;
            }

            delete p_tmp_mt;
        }

        iter->second = NULL;
    }

    return 0;
}

/**
 * @brief  载入所有的so信息
 * @param
 * @return  0success -1failed
 */
int c_work_proc::load_proto_so()
{
    char proto_so_path[PATH_MAX] = {'\0'};
    if(m_p_config->get_config("file_path", "proto_so_path", proto_so_path, sizeof(proto_so_path)))
    {
        ERROR_LOG("Get proto so path failed");
        return -1;
    }

    if(access(proto_so_path, R_OK|W_OK|X_OK))
    {
        ERROR_LOG("proto path [%s] must exist and have rwx permission.\nreason: %s.",
                    proto_so_path, strerror(errno));
        return -1;
    }

    DIR *dir = NULL;
    struct dirent *de = NULL;
    dir = opendir(proto_so_path);
    if(dir == NULL)
    {
        ERROR_LOG("Dir %s open failed,Error:%s",proto_so_path, strerror(errno));
        return -1;
    }

    char so_flag[] = ".so";
    uint32_t register_flag = 1;
    while((de = readdir(dir)) != NULL)
    {
        if(strcmp(de->d_name,".") == 0 || strcmp(de->d_name, "..") == 0)
        {
            continue;
        }
        if(strcmp(de->d_name + strlen(de->d_name) - strlen(so_flag), so_flag) == 0)
        {
            char so_path_name[PATH_MAX] = {'\0'};
            strcpy(so_path_name, proto_so_path);
            strcat(so_path_name, de->d_name);
            if(register_so(so_path_name) != 0)
            {
                register_flag = 0;
                break;
            }
        }
    }
    closedir(dir);

    if(!register_flag)
    {
        return -1;
    }
    return 0;
}

/**
 * @brief  对函数update_seqno_to_db的包装，以便作为定时器的回调函数
 * @param   p_obj 指向c_work_proc类的实例
 * @return
 */
int c_work_proc::wrapper_update_seqno(void *p_obj)
{
    c_work_proc *work_proc = (c_work_proc*)p_obj;
    return work_proc->update_seqno_to_db();
}

/**
 * @brief  更新内存中记录的客户端消息文件的序列号到数据库
 * @param
 * @return  0success -1failed
 */
int c_work_proc::update_seqno_to_db()
{
    char update_str[512] = {'\0'};
    map<in_addr_t, client_seq_t>::iterator iter = m_seq_map.begin();
    for(; iter != m_seq_map.end(); iter++)
    {
            sprintf(update_str, "INSERT INTO %s(host_ip, file_num, seq_no, flag) "
						        "VALUES(%u, %u, %u, %d) "
                                "ON DUPLICATE KEY UPDATE file_num = %u, seq_no = %u, flag = %d;",
								m_client_seqno_table_name,
								iter->first, (iter->second).file_num, (iter->second).seq_no,
								(iter->second).flag, (iter->second).file_num, (iter->second).seq_no,
								(iter->second).flag);

            if(m_p_mysql->execsql(update_str) < 0)
            {
                ERROR_LOG("update_str:%s", update_str);
                return -1;
            }
    }
    return 0;
}

/**
 * @brief  向消息映射结构中加入一个新的映射关系
 * @param  msg_id 消息ID
 * @param  report_id 统计项ID
 * @param  arg_index  统计项在消息中的位置
 * @param  proto_id  协议ID
 * @param  msg_len  消息长度
 * @return
 */
int c_work_proc::msg_translate_new(uint32_t msg_id, uint32_t report_id, uint8_t arg_index,
                                    uint32_t proto_id, uint32_t msg_len)
{
    message_translate_t *p_msg_tr = new message_translate_t; /**< 在msg_translate_free()函数统一释放*/
    p_msg_tr->next = NULL;
    p_msg_tr->report_id = report_id;
    p_msg_tr->arg_index = arg_index;
    p_msg_tr->proto_id = proto_id;
    p_msg_tr->msg_len = msg_len;

    map<uint32_t, message_translate_t*>::iterator iter = m_msg_translate_map.find(msg_id);
    if(iter == m_msg_translate_map.end())
    {
        m_msg_translate_map.insert(pair<uint32_t, message_translate_t*>(msg_id, p_msg_tr));
    }
    else
    {
        message_translate_t *p_tmp_mt = iter->second;
        if(NULL == p_tmp_mt) // the first one
        {
            iter->second = p_msg_tr;
            return 0;
        }

        // go to tail and append one at the end.
        while(p_tmp_mt->next)
        {
            p_tmp_mt = p_tmp_mt->next;
        }

        p_tmp_mt->next = p_msg_tr;
    }

    return 0;
}

/**
 * @brief 注册单个so,将其加入so map中，并调用各自的init函数，完成so的初始化
 * @param   so_file so文件所在的位置
 * @return  0success -1 failed
 */
int c_work_proc::register_so(const char *so_file)
{
    if (access(so_file,F_OK) != 0) {
        ERROR_LOG("ERROR: access %s: %s", so_file, strerror(errno));
        return -1;
    }
    proto_so_t proto_so = {0};
    char *error = NULL;
    proto_so.handle = dlopen(so_file, RTLD_NOW);
    if (!proto_so.handle) {
        ERROR_LOG("ERROR: dlopen %s: %s", so_file, dlerror());
        return -1;
    }

    dlerror();                                             /* Clear any existing error */
    *(void**)(&proto_so.proto_init) = dlsym(proto_so.handle, "proto_init");
    if ((error = dlerror()) != NULL) {
        ERROR_LOG("ERROR: dlsym %s: %s", so_file, error);
        return -1;
    }
    *(void**)(&proto_so.get_proto_id) = dlsym(proto_so.handle, "get_proto_id");
    if ((error = dlerror()) != NULL) {
        ERROR_LOG("ERROR: dlsym %s: %s", so_file, error);
        return -1;
    }
    *(void**)(&proto_so.proto_process) = dlsym(proto_so.handle, "proto_process");
    if ((error = dlerror()) != NULL) {
        ERROR_LOG("ERROR: dlsym %s: %s", so_file, error);
        return -1;
    }
    *(void**)(&proto_so.proto_uninit) = dlsym(proto_so.handle, "proto_uninit");
    if ((error = dlerror()) != NULL) {
        ERROR_LOG("ERROR: dlsym %s: %s", so_file, error);
        return -1;
    }

    int i;
    for(i=0; i<3; i++) {
        if (proto_so.proto_init(m_p_timer, m_p_config) == 0) {
            break;
        } else {
            sleep(2);
        }
    }
    if(i == 3) {
        ERROR_LOG("ERROR: proto_init: %s", so_file);
        return -1;
    }

    uint32_t p_proto_id[64];
    int proto_count = 0;
    if (proto_so.get_proto_id(p_proto_id,&proto_count)) {
        ERROR_LOG("ERROR: get_proto_id: %s", so_file);
        return -1;
    }

    m_proto_vec.push_back(proto_so);
    for (int i = 0; i < proto_count; i++) {
		DEBUG_LOG("m_proto_map: %s: %u", so_file, p_proto_id[i]);
		m_proto_map.insert(pair<uint32_t, proto_so_t>(p_proto_id[i], proto_so));
    }

    return 0;
}

/**
 * @brief  释放各个so
 * @param
 * @return  0success -1failed
 */
int c_work_proc::unregister_so()
{
    vector<proto_so_t>::iterator iter = m_proto_vec.begin();
    for(; iter != m_proto_vec.end(); iter++)
    {
        iter->proto_uninit();
        dlclose((iter->handle));
        iter->handle = NULL;
    }
    return 0;
}

/**
 * @brief  处理从环形队列中取出的一条消息
 * @param   p_sn_msg_hdr 指向消息
 * @return  0success -1failed
 */
int c_work_proc::process_poped_message(int fd, ps_message_t *ps_message)
{
    sp_message_t sp_msg;/**< 回复给中转的消息*/
    memset(&sp_msg, 0, sizeof(sp_msg));

    sp_msg.connect_id = ps_message->connect_id;


    map<in_addr_t, client_seq_t>::iterator iter = m_seq_map.find(ps_message->cli_addr);
    if(iter == m_seq_map.end())
    {
        struct in_addr cli_addr;
        memcpy(&cli_addr, &(ps_message->cli_addr),4);
        WARN_LOG("client [%s] not configured, insert one.", inet_ntoa(cli_addr));
        //ps_message->type = MSG_IP_DENIED;/**< 客户端IP地址未配置*/
        //不存在则插入一条记录
        client_seq_t client_seq;
        client_seq.file_num = 0;
        client_seq.seq_no = 0;
        client_seq.flag = 0;
        pair<map<in_addr_t, client_seq_t>::iterator, bool> insert_result = 
            m_seq_map.insert(pair<in_addr_t, client_seq_t>(ps_message->cli_addr,client_seq));
        iter = insert_result.first;
    }
    //else
    //{
        uint32_t file_num = ps_message->file_num;
        uint32_t seqno = ps_message->seqno;

        if(file_num != (iter->second).file_num)
        {/**< 不同的文件*/
            (iter->second).file_num = ps_message->file_num;
            (iter->second).seq_no = ps_message->seqno;
            int error_index = (iter->second).flag;
            (iter->second).flag = message_process(ps_message, error_index);

            if((iter->second).flag != 0)
            {
                ERROR_LOG("message process failed.");
                return 0; /**< 消息处理失败,客户端重发 */
            }
        }
        else
        {/**< 相同的文件*/
            if(0 == (iter->second).flag)
            {/**< 上次全部正确处理了*/
                if((iter->second).seq_no < seqno)
                {/**< 未处理的消息*/
                    (iter->second).file_num = ps_message->file_num;
                    (iter->second).seq_no = ps_message->seqno;
                    int error_index = (iter->second).flag;
                    (iter->second).flag = message_process(ps_message, error_index);

                    if((iter->second).flag != 0)
                    {
                        ERROR_LOG("message process failed.");
                        return 0; /**< 消息处理失败,客户端重发 */
                    }
                }
                else
                {/**< 收到已经处理过的消息*/
                    //do nothing
                }
            }
            else
            {/**< 上次没有完全正确处理*/
                if((iter->second).seq_no == seqno)
                {/**< 未处理的消息*/
                    (iter->second).file_num = ps_message->file_num;
                    (iter->second).seq_no = ps_message->seqno;
                    int error_index = (iter->second).flag;
                    (iter->second).flag = message_process(ps_message, error_index);

                    if((iter->second).flag != 0)
                    {
                        ERROR_LOG("message process failed.");
                        return 0; /**< 消息处理失败,客户端重发 */
                    }
                }
                else if((iter->second).seq_no > seqno)
                {
                    //do nothing
                }
                else
                {
                    //前面一条消息处理失败，后面的消息不处理
                    return 0;
                }

            }
        }
    //}/**< 相同的文件*/

    sp_msg.type = ps_message->type;
    sp_msg.file_num = ps_message->file_num;
    sp_msg.seqno = ps_message->seqno;
	sp_msg.channel_id = ps_message->channel_id;
    net_send_cli(fd, (char *)&sp_msg, sizeof(sp_msg)); // 给客户端回包
    return 0;
}

/**
 * @brief  处理消息,将消息分发给相应的so
 * @param   p_sn_msg_hdr 指向消息
 * @param   error_index  >1 上次处理到的错误位置 0 没有错误 1全部错误
 * @return  0success -1failed
 */
int c_work_proc::message_process(ps_message_t *ps_message, int error_index)
{
    uint32_t msg_id = ps_message->type;
    message_translate_t *p_msg_trans = NULL;

    map<uint32_t, message_translate_t*>::iterator mt_iter = m_msg_translate_map.find(msg_id);
    if(mt_iter == m_msg_translate_map.end())
    {
        struct in_addr cli_addr;
        memcpy(&cli_addr, &(ps_message->cli_addr),4);
        //WARN_LOG("0x0%x was not in configured,from %s", ps_message->.type, inet_ntoa(cli_addr));
        ps_message->type = MSG_NOTIN_SERVER;
        /**收到未配置的消息ID,利用系统消息通知客户端，由客户端处理 */
        return 0;
    }

    p_msg_trans = mt_iter->second;
    uint32_t off_set = sizeof(ps_message_t);/**< 消息体在消息中的偏移量*/

    // do not support STAT_REPORT_COMBINE from now on.
    if(p_msg_trans->proto_id == STAT_REPORT_COMBINE)
    {
        ps_message->type = MSG_NOTIN_SERVER;

        return 0;
    }

    char msg_buffer[MAX_MSG_LEN];/**< 存放消息体*/
    uint16_t data_len = 0;/**< 消息体的长度*/
    int index = 1;
    while(p_msg_trans != NULL) {
        data_len = 0;
        bool reset = false;

        if(p_msg_trans->msg_len == 0) { /**< 长度等于0 表示是不定长的消息*/
			if ((off_set != sizeof(ps_message_t)) || p_msg_trans->next) {
				EMERG_LOG("YMF-1: next_trans=%p msgid=%u len=%u report=%u len=0 but offset=%u(>%lu)",
							p_msg_trans->next, ps_message->type, ps_message->len,
							p_msg_trans->report_id,	off_set, sizeof(ps_message_t));
				return 0;
			}

			data_len = ps_message->len - sizeof(ps_message_t);
			if (data_len == 0) {
				EMERG_LOG("YMF-2: msgid=%u len=%u report=%u len=0",
							ps_message->type, ps_message->len, p_msg_trans->report_id);
				return 0;
			}

			if (p_msg_trans->proto_id == 19) {
				reset = true;
			} else if ((data_len != *(uint16_t*)((char*)ps_message + off_set)) || (data_len < 3)) {
				EMERG_LOG("YMF-3: msgid=%u report=%u len=%u blen=%u",
							ps_message->type, p_msg_trans->report_id,
							data_len, *(uint16_t*)((char*)ps_message + off_set));
				return 0;
			}
        } else {
            data_len = p_msg_trans->msg_len;
        }

		// verify if the receiving packet is big enough
		if ((off_set + data_len) > ps_message->len) {
			ERROR_LOG("invalid len: msgid=%u report=%u available=%u expected=%u",
						ps_message->type, p_msg_trans->report_id, ps_message->len, off_set + data_len);
			return 0;
		}

        if(index < error_index) {
            index++;
            off_set += data_len;
            p_msg_trans = p_msg_trans->next;
            continue;
        }

        map<uint32_t, proto_so_t>::iterator pro_iter = m_proto_map.find(p_msg_trans->proto_id);
        if (pro_iter == m_proto_map.end()) {
            ERROR_LOG("proto %d not configure", p_msg_trans->proto_id);
        } else {
			if (data_len > MAX_MSG_LEN - 3) {
			    ERROR_LOG("msgid=%u report=%u datalen=%d maxlen=%u",
							msg_id, p_msg_trans->report_id, data_len, MAX_MSG_LEN - 3);
                index++;
                off_set += data_len;
                p_msg_trans = p_msg_trans->next;
                continue;
			}

            if (!reset) {
                memcpy(msg_buffer, (char*)(ps_message) + off_set, data_len);
            } else {
                *(uint16_t*)msg_buffer = data_len;
                memcpy(msg_buffer + sizeof(uint16_t), (char*)(ps_message) + off_set, data_len);
                msg_buffer[sizeof(uint16_t) + data_len] = 0;
            }
			off_set += data_len;

            ss_message_header_t ss_msg_hdr;
            ss_msg_hdr.len = sizeof(ss_message_header_t) + data_len;
            ss_msg_hdr.report_id = p_msg_trans->report_id;
            ss_msg_hdr.timestamp = ps_message->timestamp;
            ss_msg_hdr.cli_addr = ps_message->cli_addr;
            ss_msg_hdr.proto_id = p_msg_trans->proto_id;

            // 战神联盟和奥特曼游戏，需要获取event_id, event_type. 为了避免频繁函数调用，先作判断。
            // TODO: refactor code here, make code flexiable to support all games.
            if(ss_msg_hdr.report_id == AOTEMAN_REPORT_ID || ss_msg_hdr.report_id == ZHANSHEN_REPORT_ID) {
                ss_msg_hdr.event_type = event_get_event_type(ss_msg_hdr.report_id, msg_buffer);
            } else {
                ss_msg_hdr.event_type = 0;
            }

            if((pro_iter->second).proto_process(ss_msg_hdr, msg_buffer) != 0) {
                ERROR_LOG("proto process failed, proto_id: %d, report_id: %d",
            	            p_msg_trans->proto_id, p_msg_trans->report_id);
                return p_msg_trans->arg_index + 1;
            }
        }
        p_msg_trans = p_msg_trans->next;
    }

    return 0;
}

