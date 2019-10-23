/*=========================================================================       
* 
*        Filename: config_server.cpp 
* 
*         Author: seasondi
*        Created:  2014-02-26 10:48:47 
* 
*        Description: tongji platform config_server module 
* 
* ========================================================================= 
*/ 
#include <cstdlib>
#include <stdexcept>
#include <string>
#include <stdio.h>
#include <string.h>
#include <vector>
#include <sstream>

#include "log.h"
#include "config_server.hpp"
#include "mysql_operator.h"
#include "tcp_client.hpp"

#include "../../stat-common/stat_config.hpp"
#include "../../stat-common/string_utils.hpp"

#include "stat_common.hpp"
#include "stat_proto_defines.hpp"

#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>

using std::ostringstream;

StatConfigServer::StatConfigServer() : m_alarm_interval(30), m_alarm_fd(-1), m_last_send_time(0), m_error_type_flag(0) 
{
}

StatConfigServer::~StatConfigServer(){
    this->uninit();
}

int StatConfigServer::init(){
    if(this->redis.connect(config_get_strval("redis_ip", "127.0.0.1"), config_get_intval("redis_port", 6379))) {
        ERROR_LOG("can not connect to redis server [%s]\n", redis.getError());
        sleep(1);
        BOOT_LOG(-1, "can not connect to redis server [%s]\n", redis.getError());
    }

    if(this->mysql.init(config_get_strval("db_host", ""),
                config_get_strval("db_user", ""),
                config_get_strval("db_passwd", ""),
                config_get_strval("db_name", ""),
                config_get_intval("db_port", 0),
                CLIENT_INTERACTIVE)) {
        ERROR_LOG("can not connect to mysql [%s]\n", this->mysql.m_error());
        sleep(1);
        BOOT_LOG(-1, "can not connect to mysql [%s]\n", this->mysql.m_error());
    }
    //流量监控
    if(m_traffic_log.init() != 0){
        ERROR_LOG("traffic log init err");
        return -1;
    }

    StatCommon::stat_config_get("bind_ip", m_bind_ip);
    m_alarm_interval = StatCommon::stat_config_get("alarm_interval", 30);
    DEBUG_LOG("alarm interval: %d min", m_alarm_interval);

    init_alarm_ip_port();
    int port = atoi(m_alarm_port.c_str());
    m_alarm_fd = net_connect_ser(m_alarm_ip.c_str(), port, 1000000);

    return 0;
}

int StatConfigServer::uninit(){
    this->redis.close();
    this->mysql.uninit();

    net_close_ser(m_alarm_fd);
    m_alarm_fd = -1;

    return 0;
}

int StatConfigServer::process_traffic_log(time_t now){
    static time_t last_time = 0;
    if(now - last_time >= 60){
        if(last_time != 0)
        {
            if(m_traffic_log.process_log_traffic("(条数)") == 0)
                m_traffic_log.clear_traffic_value();
        }
        last_time = now;
    }
    return 0;
}

char StatConfigServer::getChar(uint8_t c){
    if(32 <= c && c <= 126) return c;
    return ' ';
}

void StatConfigServer::print_pkg(const char *pbuf, uint32_t len){
    uint32_t lines = len%4==0?len/4:len/4+1;
    uint8_t *buf = (uint8_t*)malloc(lines*4);
    memset(buf, 0, lines<<2);
    memcpy(buf, pbuf, len);
    printf("\n");
    for(uint32_t i=0; i<lines; i++) {
        printf("%02X %02X %02X %02X  |  ", buf[i*4], buf[i*4+1], buf[i*4+2], buf[i*4+3]);
        printf("%03d %03d %03d %03d  |  ", buf[i*4], buf[i*4+1], buf[i*4+2], buf[i*4+3]);
        printf("%c %c %c %c  |\n",    getChar(buf[i*4]), getChar(buf[i*4+1]), getChar(buf[i*4+2]), getChar(buf[i*4+3]));
    }
    free(buf);
}

int StatConfigServer::get_server_pkg_len(const char *buf, uint32_t len){
    if(len<2)
    {
        return 0;
    }
    return *(int16_t*)(buf);
}

void StatConfigServer::timer_event(){
    process_traffic_log(time(NULL));

    time_t now;
    time(&now);

    // to send alarm_msg to stat-center
    if ((now - m_last_send_time) > 60 * m_alarm_interval)
    {
        if (m_error_type_flag)
        {
            reconnect();
            alarm_insert_error();
            m_error_type_flag &= 0;
        }
        m_last_send_time = now;
    }
}

void StatConfigServer::process_client_pkg(int fd, const char *buf, uint32_t len){
    redisReply* reply;
    string stid, sstid, field, key, range;
    //print_pkg(buf, len);
	//接收数据包并检查包体的合法性
    uint32_t r = pkg.recv_pkg(buf);
    if(r != 0) {
        goto error;
    }

    m_traffic_log.add_traffic_value(1);
    switch(pkg.getCmdID()) {
		//添加统计项
        case CMD_INSERT_STAT:
            //if((r = insertTree(&mysql, pkg.getGame(), pkg.getStid(), pkg.getSstid())) = 0) {
            //    r = E_GET_NODE_ID; 
            //    goto error;
            //}
            if(pkg.getOpType() > SET && strlen(pkg.getKey()) == 0) { //分布的report_id先去redis中取
                if((reply = redis.doCommand("HGET report_info %b", pkg.getStat(), pkg.getPkgLen() - sizeof(header_t) - strlen(pkg.getKey()))) == 0) {
                    //server down 或者此key在redis中已存在
                    ERROR_LOG("redis %s", redis.getError());
                    r = E_UPDATE_REDIS;

                    goto error;
                } else {
                    switch(reply->type) {
                        case REDIS_REPLY_STRING:
                            r = atoi(reply->str);
                            break;
                        case REDIS_REPLY_INTEGER:
                            r = reply->integer;
                            break;
                        case REDIS_REPLY_NIL://没有找到
                            r = 0;
                            break;
                        default :
                            ERROR_LOG("unexcepted type[%d] str[%s] array[%zu]", reply->type, reply->str, reply->elements);
                            r = 0;
                            break;
                    }
                    freeReplyObject(reply);
                    if(r != 0) {
                        if(strchr(pkg.getStid(), '%') != NULL || 
                                strchr(pkg.getSstid(), '%') != NULL ||
                                strchr(pkg.getField(), '%') != NULL ||
                                strchr(pkg.getKey(), '%') != NULL ||
                                strchr(pkg.getRange(), '%') != NULL) {
                            INFO_LOG("game=%u,stid='%s',sstid='%s',opfield='%s',key='%s',range='%s'",
                                    pkg.getGame(), pkg.getStid(), pkg.getSstid(), pkg.getField(), pkg.getKey(), pkg.getRange());
                        }
                        goto ok;
                    }
                }
            }
            if((r = insertReport(&mysql, pkg.getGame(), pkg.getOpType(), pkg.getStid(), pkg.getSstid(), pkg.getField())) == 0) {
                r = E_GET_REPORT_ID;

                goto error;
            }
            if(pkg.getOpType() > SET && strlen(pkg.getKey()) == 0) { //分布的保存report_id 
                if((reply = redis.doCommand("HSET report_info %b %u", pkg.getStat(), pkg.getPkgLen() - sizeof(header_t) - strlen(pkg.getKey()), r)) == 0) {
                    //server down 或者此key在redis中已存在
                    r = E_UPDATE_REDIS;
                    ERROR_LOG("redis %s\n", redis.getError());
                } else {
                    freeReplyObject(reply);
                }
            } else {
                if((r = insertData(&mysql, pkg.getGame(), pkg.getOpType(), pkg.getStid(), pkg.getSstid(), pkg.getField(), pkg.getKey())) == 0) {

                    r = E_GET_DATA_ID;

                    goto error;
                }
                if((reply = redis.doCommand("HSET data_info %b %u", pkg.getStat(), pkg.getPkgLen() - sizeof(header_t), r)) == 0) {
                    //server down 或者此key在redis中已存在
                    r = E_UPDATE_REDIS;
                    ERROR_LOG("redis %s\n", redis.getError());
                } else {
                    freeReplyObject(reply);
                }
            }
            if(strchr(pkg.getStid(), '%') != NULL || 
                    strchr(pkg.getSstid(), '%') != NULL ||
                    strchr(pkg.getField(), '%') != NULL ||
                    strchr(pkg.getKey(), '%') != NULL ||
                    strchr(pkg.getRange(), '%') != NULL ) {
                INFO_LOG("game=%u,stid='%s',sstid='%s',opfield='%s',key='%s',range='%s'",
                        pkg.getGame(), pkg.getStid(), pkg.getSstid(), pkg.getField(), pkg.getKey(), pkg.getRange());
            }
            break;
		//添加GPZS
        case CMD_INSERT_GPZS:
            if((r = getGPZS(&mysql, pkg.getGame(), pkg.getPlatform(), pkg.getZone(), pkg.getServer())) == 0) {
                if((r = insertGPZS(&mysql, pkg.getGame(), pkg.getPlatform(), pkg.getZone(), pkg.getServer())) == 0) {
                    ERROR_LOG("insert GPZS[%u.%d.%d.%d] error", pkg.getGame(), pkg.getPlatform(), pkg.getZone(), pkg.getServer());
                    r = E_GET_GPZS_ID;

                    goto error;
                }
            }
            //update redis
            if((reply = redis.doCommand("HSET gpzs %b %u", pkg.getGPZS(), sizeof(gpzs_t), r)) == 0) {
                //server down 或者此key在redis中已存在
                r = E_UPDATE_REDIS;
                ERROR_LOG("redis %s", redis.getError());
            } else {
                freeReplyObject(reply);
            }
            break;
		//添加加工项
        case CMD_INSERT_TASK:
            range.assign(pkg.getRange());
            if((r = getTask(&mysql, pkg.getGame(), pkg.getTask(), pkg.getRange())) == 0) {
                if((r = insertTask(&mysql, pkg.getGame(), pkg.getTask(), pkg.getRange())) == 0) {
                    ERROR_LOG("insert task[%u.%u.%s] error", pkg.getGame(), pkg.getTask(), range.c_str());
                    r = E_GET_TASK;

                    goto error;
                }
            }
            //update redis
            if((reply = redis.doCommand("HSET data_info %b %u", pkg.getStat(), pkg.getPkgLen() - sizeof(header_t), r)) == 0) {
                r = E_UPDATE_REDIS;
                ERROR_LOG("reids %s", redis.getError());
            } else {
                freeReplyObject(reply);
            }
            break;
    }

    ret_pkg_t ret_pkg;
ok:
    ret_pkg.head.pkg_len = sizeof(ret_pkg);
    ret_pkg.head.cmd_id = pkg.getCmdID();
    ret_pkg.head.version = pkg.getVersion();
    ret_pkg.head.seq_no = pkg.getSeqNo();
    ret_pkg.head.return_value = 0;
    ret_pkg.head.fd = pkg.getFd();
    ret_pkg.result = r;
    net_send_cli(fd, &ret_pkg, sizeof(ret_pkg));
    return;
error:
    ERROR_LOG("return %d", r);
    ret_pkg.head.pkg_len = sizeof(ret_pkg);
    ret_pkg.head.cmd_id = pkg.getCmdID();
    ret_pkg.head.version = pkg.getVersion();
    ret_pkg.head.seq_no = pkg.getSeqNo();
    ret_pkg.head.return_value = r;
    ret_pkg.head.fd = pkg.getFd();
    ret_pkg.result = 0;
    net_send_cli(fd, &ret_pkg, sizeof(ret_pkg));

    switch (r)
    {
		//错误码
        case E_UPDATE_REDIS:
            m_error_type_flag |= 0b10000000;
            break;
        case E_GET_REPORT_ID:
            m_error_type_flag |= 0b01000000;
            break;
        case E_GET_DATA_ID:
            m_error_type_flag |= 0b00100000;
            break;
        case E_GET_GPZS_ID:
            m_error_type_flag |= 0b00010000;
            break;
        case E_GET_TASK:
            m_error_type_flag |= 0b00001000;
            break;
        default:
            break;
    }
}

void StatConfigServer::process_server_pkg(int fd, const char *buf, uint32_t len){

    StatAlarmRet* ret_pkgs = (StatAlarmRet*)(buf);
    if (ret_pkgs->proto_id == STAT_PROTO_INSERT_STAT_ERROR_CS)
    {   
        return;
    }   

}

void StatConfigServer::client_connected(int fd, uint32_t ip){
}

void StatConfigServer::client_disconnected(int fd){
}

void StatConfigServer::server_disconnected(int fd)
{
    reconnect();
}

bool StatConfigServer::is_real_alive()
{
    if(m_alarm_fd < 0)
        return false;

    char test[1];

    ssize_t ret = ::recv(m_alarm_fd, test, 1, MSG_PEEK | MSG_DONTWAIT);

    if(ret > 0)
        return true;
    else if(ret == 0)
    {   
        return false;
    }   
    else
    {   
        if(errno == EAGAIN || errno == EWOULDBLOCK || errno == EINTR)
            return true;
        else
        {
            return false;
        }
    }   
}

int StatConfigServer::reconnect()
{
    if(is_real_alive() == false)
    {   
        DEBUG_LOG("disconnected to %s:%s, try to reconnect.", m_alarm_ip.c_str(), m_alarm_port.c_str());
        m_alarm_fd = -1;

        int port = atoi(m_alarm_port.c_str());
        m_alarm_fd = net_connect_ser(m_alarm_ip.c_str(), port, 1000000);  
    }  

    return 0;
}

void StatConfigServer::alarm_insert_error()
{
    char buf_send[100];
    uint32_t len = 0;

    len = sizeof(struct InsertStatErrorCsHeader); 
    memcpy(buf_send, &len, sizeof(len));
    uint32_t offset = 0;
    offset += sizeof(len);

    uint32_t proto_id = STAT_PROTO_INSERT_STAT_ERROR_CS;
    memcpy(buf_send+offset, &proto_id, sizeof(proto_id));
    offset += sizeof(proto_id);

    uint8_t module_type = 10; 
    memcpy(buf_send+offset, &module_type, sizeof(module_type)); 
    offset += sizeof(module_type);

    struct sockaddr_in addr;
    addr.sin_family = AF_INET;
    inet_pton(AF_INET, m_bind_ip.c_str(), &addr.sin_addr);
    uint32_t ip = addr.sin_addr.s_addr;

    memcpy(buf_send+offset, &ip, sizeof(ip)); 
    offset += sizeof(ip);

    int res = 0;
    uint8_t error_type = 0;
    if (m_error_type_flag & 0b10000000)
    {
        error_type = 1;
        memcpy(buf_send+offset, &error_type, sizeof(error_type)); 
        res = net_send_ser(m_alarm_fd, buf_send, len);
    }

    if (m_error_type_flag & 0b01000000)
    {
        error_type = 2;
        memcpy(buf_send+offset, &error_type, sizeof(error_type)); 
        res = net_send_ser(m_alarm_fd, buf_send, len);
    }

    if (m_error_type_flag & 0b00100000)
    {
        error_type = 3;
        memcpy(buf_send+offset, &error_type, sizeof(error_type)); 
        res = net_send_ser(m_alarm_fd, buf_send, len);
    }

    if (m_error_type_flag & 0b00010000)
    {
        error_type = 4;
        memcpy(buf_send+offset, &error_type, sizeof(error_type)); 
        res = net_send_ser(m_alarm_fd, buf_send, len);
    }

    if (m_error_type_flag & 0b00001000)
    {
        error_type = 5;
        memcpy(buf_send+offset, &error_type, sizeof(error_type)); 
        res = net_send_ser(m_alarm_fd, buf_send, len);
    }
    
    if (res == -1)
        ERROR_LOG("send InsertStatError failed");
}

void StatConfigServer::init_alarm_ip_port()
{
    int proxy_count = StatCommon::stat_config_get("stat-center-count", 1); 

    for(int id = 0; id < proxy_count; ++id)
    {   
        ostringstream oss;
        oss.str("");

        oss << "stat-center" << "-host" << id; 
        string ip_key = oss.str();
        string ipp;
        StatCommon::stat_config_get(ip_key, ipp);
        if(ipp.empty())
        {
            ERROR_LOG("can not get %s from conf.", ip_key.c_str());
            continue;
        }   

        vector<string> elems;
        elems.clear();

        StatCommon::split(ipp, ':', elems);
        if(elems.size() != 2)
        {
            ERROR_LOG("bad format of ip:port, %s", ipp.c_str());
            continue;
        }   

        m_alarm_ip = elems[0];
        m_alarm_port = elems[1];
    }
}

