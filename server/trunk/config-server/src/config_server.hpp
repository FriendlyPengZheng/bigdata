/*=========================================================================       
* 
*        Filename: config_server.hpp 
* 
*         Author: seasondi
*        Created:  2014-02-26 10:45:22 
* 
*        Description: tongji platform config_server module 
* 
* ========================================================================= 
*/ 
#ifndef CONFIG_SERVER_HPP 
#define CONFIG_SERVER_HPP

#include "stat_main.hpp"
#include "async_server.h"
#include "redis.h"
#include "c_proto_pkg.h"
#include "c_mysql_connect_auto_ptr.h"
#include "stat_traffic_monitor.hpp"

class StatConfigServer : public IStatMain
{
    public:
        StatConfigServer();
        virtual ~StatConfigServer();
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

        // tell stat-center the failure of CMD_INSERT_STAT 
        bool is_real_alive();
        int  reconnect();
        void alarm_insert_error();
        void init_alarm_ip_port();

    private:
        char getChar(uint8_t c);
        void print_pkg(const char *pbuf, uint32_t len);

    private:
        c_proto_pkg pkg;
        redisConnection redis;
        c_mysql_connect_auto_ptr mysql;

        StatTrafficLog m_traffic_log;

        int     m_alarm_interval;
        string  m_alarm_ip;
        string  m_alarm_port;
        string  m_bind_ip;
        int     m_alarm_fd;
        time_t  m_last_send_time;
        uint8_t m_error_type_flag;   // - - - - - - - -       set the corresponding bit when error occur, clear all the bit after calling alarm_insert_error()
                                     // | | | | |
                                     // | | | | + 1: task error           
                                     // | | | + 1: gpzs_id error    
                                     // | | + 1: data_id error
                                     // | + 1: report_id error
                                     // + 1: redis update error
};                                 

#endif /*CONFIG_SERVER_HPP*/
