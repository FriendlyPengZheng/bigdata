/*=========================================================================       
* 
*        Filename: stat_traffic_monitor.hpp 
* 
*        Author: seasondi
*        Created:  2014-03-04 10:27:42 
*        Modified by Ian Guo<ianguo@taomee.com>
* 
*        Description: 流量监控 
* 
* ========================================================================= 
*/ 
#ifndef STAT_TRAFFIC_MONITOR_HPP 
#define STAT_TRAFFIC_MONITOR_HPP

#include <string>
#include <stdint.h>
#include <ctime>
#include <vector>
#include <sstream>

using std::string;

class TrafficItem;
std::ostream& operator << (std::ostream& os,const TrafficItem& item);

class TrafficItem{
    public:
        TrafficItem(const string& module,const string& ip,const uint32_t value,
                const time_t time, const string& stid_suffix)
            : m_module(module), m_ip(ip), m_traffic_value(value), m_data_time(time), m_stid_suffix(stid_suffix)
        {}

        friend std::ostream& operator << (std::ostream& os,const TrafficItem& item);
    private:
        string m_module;
        string m_ip;
        uint32_t m_traffic_value;
        time_t m_data_time;
        string m_stid_suffix; // stid后缀，用于指定单位等等
};

class StatTrafficLog{
    public:
        StatTrafficLog():m_traffic_value(0){}
        ~StatTrafficLog() {}
        int init();
        void add_traffic_value(uint32_t value)
        {
            m_traffic_value += value;
        }
        void clear_traffic_value()
        {
            m_traffic_value = 0;
        }
        
        /**
         * @brief  写进buf
         */
        int process_log_traffic(const string& stid_suffix, string& buf);

        /**
         * @brief  全部写进文件
         */
        int process_log_traffic(const string& stid_suffix);
        
    public:
        static const unsigned sc_game_id = 10000;
        static const string sc_traffic_log_path;

    private:
        int get_local_ip();
        int open_log_file();

    private:
        string m_module_name;//for stid
        string m_bind_ip;//for sstid

        uint32_t m_traffic_value;
};

#endif /*STAT_TRAFFIC_MONITOR_HPP*/
