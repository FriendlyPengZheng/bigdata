/*=========================================================================       
* 
*        Filename: db_server.h 
* 
*         Author: seasondi
*        Created:  2014-02-24 16:55:49 
* 
*        Description: db_server business process module 
* 
* ========================================================================= 
*/ 
#ifndef DB_SERVER_HPP
#define DB_SERVER_HPP

#include "redis.h"
#include "c_proto_pkg.h"
#include "c_sqlcache.h"
#include "proto.h"
#include "tcp_client.hpp"
#include "hash.h"
#include "stat_traffic_monitor.hpp"
#include "c_mysql_connect_auto_ptr.h"

#include <set>
#include <map>
#include <stdint.h>
#include <ctime>

using std::set;
using std::map;

class StatDbServer : public IStatMain
{
    public:
        StatDbServer() : m_alarm_fd(-1), m_alarm_interval(30)
        {
            time(&m_last_send_time);            
        }
        virtual ~StatDbServer();
        /*Inherited from IStatMain*/
        virtual int init();
        virtual int uninit();
        virtual int get_server_pkg_len(const char *buf, uint32_t len);
        virtual void timer_event();
        virtual void process_client_pkg(int fd, const char *buf, uint32_t len);
        virtual void process_server_pkg(int fd, const char *buf, uint32_t len);
        virtual void client_connected(int fd, uint32_t ip);
        virtual void client_disconnected(int fd);
        virtual void server_disconnected(int fd);
        int process_traffic_log(time_t now);

    private:
        uint32_t getGPZSId(redisConnection* redis, TcpClient* config_server, const gpzs_t* gpzs, int fd);
        uint32_t getDataId(redisConnection* redis, TcpClient* config_server, const void* buf, uint32_t len, const c_proto_pkg* pkg, int fd);
        uint32_t getTaskId(redisConnection* redis, TcpClient* config_server, const void* buf, uint32_t len, int fd);
        uint32_t getReportId(redisConnection* redis, TcpClient* config_server, const void* buf, uint32_t len, const c_proto_pkg* pkg, int fd);
        bool getStHash(redisConnection* redis, const char* stid, uint32_t* sthash);
        uint32_t doCmd(uint32_t platform_id, uint32_t zone_id, uint32_t server_id, int fd);
        char getChar(uint8_t c);
        void print_pkg(const char *pbuf, uint32_t len);
        
        // to send the messy-code gameid to stat-center 
        bool is_real_alive();
        int  reconnect();
        void alarm_notutf8();
        void init_alarm_ip_port();

        // get msgid->stid,sstid to cache
        void init_msgid_cache();
        void print(stid_response_t* s);
        stid_response_t* pack_cache(void* buf, MYSQL_ROW row);
        stid_response_t* getStid(uint32_t game_id, uint32_t msg_id, c_data& data);
        //bool is_unused_msgid(uint32_t game_id, uint32_t msg_id);

    private:
        c_proto_pkg pkg;
        redisConnection redis;
        TcpClient config_server;
        c_sqlcache sqlcache;
        c_hash_table hash_table;
        char hashkey[4096];

        StatTrafficLog m_traffic_log;

        c_mysql_connect_auto_ptr db_config;
        map<string, time_t> unused_msgid;
        c_hash_table used_msgid;

        // added by tomli --->
        int    m_alarm_fd;
        string m_alarm_ip;
        string m_alarm_port;
        string m_bind_ip;
        time_t m_last_send_time;
        set<uint32_t> m_gameid_not_utf8;

        int    m_alarm_interval; 
        // <--- added by tomli
};

#endif /*DB_SERVER_H*/
