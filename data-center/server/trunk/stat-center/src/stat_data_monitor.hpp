/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-center服务模块。
 *   @author  tomli<ianguo@taomee.com>
 *   @date    2014-08-18
 * =====================================================================================
 */

#ifndef STAT_DATA_MONITOR_HPP
#define STAT_DATA_MONITOR_HPP

#include <string>
#include <vector>
#include <set>
#include <ctime>

#include <stat_common.hpp>
#include <stat_proto_handler.hpp>
#include "stat_proto_defines.hpp"
#include "stat_alarm_msg.hpp"
#include "stat_module_info.hpp"
#include "stat_alarm_conf.hpp"

#include <tcp_client.hpp>

#include "string_utils.hpp"
#include <os_utils.hpp>

#include "proto/alarm_request.pb.h"
#include "../../stat-alarmer/src/stat_alarmer_defines.hpp"

using std::string;
using std::vector;
using std::map;
using std::set;

class StatDataMonitor : public StatProtoHandler
{
public: 
    StatDataMonitor(uint32_t proto_id, const char* proto_name, AlarmConf& alarm_conf);
    StatDataMonitor(uint32_t proto_id, const char* proto_name, unsigned proto_count, AlarmConf& alarm_conf);
    virtual ~StatDataMonitor();

    void init();

private:
    virtual int proc_proto(int fd, const void* pkg);
    virtual void proc_timer_event();

    // disable copy constructors
    StatDataMonitor(const StatDataMonitor&);
    StatDataMonitor& operator = (const StatDataMonitor&);

    void insert_stat_error_alarm(int fd, const StatModuleInfo& module_info, const void* pkg_buf);
	void upload_info_check_alarm(int fd, const StatModuleInfo& module_info, const void* pkg_buf);
    void stat_calc_error(int fd, const StatModuleInfo& module_info, const void* pkg_buf);
    void stat_reg_alarm(int fd, const StatModuleInfo& module_info, const void* pkg_buf);

    void send_alarm(const StatAlarmMsg& msg, uint8_t flag);
    bool send_and_recv(char * pkg_buff, uint32_t size);
    void send_sms_alarm(const string& msg);
    void send_data_alarm(const StatAlarmMsg& msg, uint8_t flag); // flag  0: email 1: email, RTX

    void notutf8_gameid_collect(int fd, const StatModuleInfo& module_info, const void* pkg_buf);

private:
    bool m_is_alarm_mobile;

    vector<vector<string> > m_contact;
    string m_alarmer_ip;
    string m_alarmer_port;
    char* m_pkg_buff;
    uint32_t m_pkg_buff_len;

    bool m_send_or_not;
    map<string, set<uint32_t> > m_game_id;
    time_t m_time_begin_collect;

    AlarmConf& m_alarm_conf;   // used to extend the weekday and holiday manage in data monitor, restore in init, backup in event
};

#endif

