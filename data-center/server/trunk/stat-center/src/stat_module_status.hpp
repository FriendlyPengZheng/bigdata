/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-center服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#ifndef STAT_MODULE_STATUS_HPP
#define STAT_MODULE_STATUS_HPP

#include <map>
#include <vector>
#include <sstream>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <time.h>

#include <tcp_client.hpp>

#include "stat_module_info.hpp"
#include "stat_heartbeat.hpp"
#include "stat_alarm_conf.hpp"

using std::map;
using std::vector;

/**
 * 记录所有服务模块的心跳数据。
 * 本类非线程安全。
 */
class StatModuleStatus
{
public:
    StatModuleStatus(AlarmConf& alrm_conf);
    ~StatModuleStatus();

    void init();

    int add_module_status(StatModuleInfo& smi, StatHeartbeat* shb);
    int delete_module_status(const StatModuleInfo& smi);
    bool exists(const StatModuleInfo& smi);

    int update_heartbeat_data(const StatModuleInfo& smi, const void* pkg_buf);

    // 更新StatModuleInfo的port, port不作为key，可以更新。
    // 当服务模块重启时ip不变，但是port会变，所有需要更新。
    int update_module_port(const StatModuleInfo& smi);

    // 迭代模式, 使用has_next()前，必须先调用start()
    void start();
    bool has_next(StatModuleInfo& smi);

    void clear();

    void dump_to_string(StatModuleType module_type, uint8_t print_type, uint8_t print_flag, const uint32_t param_ip, string& str);

    // added by tomli --->
    void dump_to_web(StatModuleType module_type, uint8_t print_type, uint8_t print_flag, const uint32_t param_ip, char buf[], uint32_t& buf_len);
    // <--- added by tomli 

    void clean_dead_modules(time_t now);
    void check_alarm(time_t now);

    int backup(uint8_t type = 0);
    int restore(uint8_t type = 0);

// added by tomli
public:
    void forbid(int fd, const void* pkg_buf);
    void forbid_from_web(int fd, const void* pkg_buf);
    void set_holiday(int fd, const void* pkg_buf);

private:
    typedef map<StatModuleInfo, StatHeartbeat*> SmiMap;

private:

    void send_alarm(const StatAlarmMsg& msg);
    void send_sms_alarm(const string& msg);
    bool send_and_recv(char* pkg_buff, uint32_t size);

    void dump_to_txt(StatModuleType module_type, uint8_t print_flags, const uint32_t param, std::ostringstream& oss);
    void dump_to_html(StatModuleType module_type, uint8_t print_flags, const uint32_t param, std::ostringstream& oss);

    // added by tomli --->
    void dump_to_web_html(StatModuleType module_type, uint8_t print_flags, const uint32_t param, char buf[], uint32_t& buf_len);
    // <--- added by tomli

    // disable copy constructors
    StatModuleStatus(const StatModuleStatus&);
    StatModuleStatus& operator = (const StatModuleStatus&);

    bool check_foreign_alarm(const uint32_t foreign_ip);  

private:
    static const unsigned s_max_modules = 100000;
    SmiMap::iterator m_map_iterator;

    SmiMap  m_smi_map;

    string m_alarmer_ip;
    string m_alarmer_port;
    vector<vector<string> > m_contact;
    bool m_is_alarm_mobile;
    char * m_pkg_buff;
    uint32_t m_pkg_buff_len;

    AlarmConf& m_alarm_conf;

    map< map<uint32_t, uint32_t>, int> m_foreign_ip;  // map<uint32_t, uint32_t> 中保存 ip范围，采用网络字节序，第一个uint32_t小于第二个uint32_t;int表示告警间隔，单位分钟
    vector<time_t>   m_lstch_fgn_time;  // 海外ip上次告警时间
};
#endif
