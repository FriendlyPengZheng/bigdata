/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-alarmer服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2014-04-09
 * =====================================================================================
 */
#ifndef STAT_APP_PUSH_ALARMER_HPP
#define STAT_APP_PUSH_ALARMER_HPP

#include "stat_msg_pusher.hpp"
#include "stat_bdcloud_pusher.hpp"
#include "stat_alarmer_defines.hpp"
#include "stat_alarmer_handler.hpp"
#include "stat_alarm_msg.hpp"
#include "stat_alarm_user.hpp"
#include "data_storage.hpp"
#include "stat_mobile_alarmer.hpp"
#include "proto/app_pull_msg.pb.h"

class StatAppPushAlarmer : public StatAlarmerHandler
{
public:
    StatAppPushAlarmer(uint32_t proto_id, const char* proto_name, StatGroupAlarmer* sag, StatMobileAlarmer* sam);
    virtual ~StatAppPushAlarmer();
    int uninit();

    // App推送报警过程：
    // 如果有报警，调用send_alarm()发送报警通知，
    // 手机客户端App收到报警通知，发请求来取报警内容，
    // 此时调用fetch_alarm().
    //int fetch_alarm(const string& who, string& title, string& content);
    int fetch_alarm(StatPullMsgProto::AppPullMsgRequest req, StatPullMsgProto::AppPullMsgResponse& res);
    // 处理app客户端注册
    int app_register(const string& taomee_id, const string& app_id, const string& device_type, const string& mobile);
    // 处理app客户端注销
    int app_unregister(const string& taomee_id, const string& app_id);
    // 定时器
    virtual void proc_timer_event();

private:
	//信息记录
    typedef StatAlarmMsg::StatAlarmMsgId AlarmerMsgId;
    typedef std::list<AlarmerMsgId> StatAlarmMsgUsrList;
    typedef std::map<AlarmerMsgId, StatAlarmMsg> StatAlarmMsgMap;
    typedef std::map<StatAlarmUser, StatAlarmMsgUsrList> StatAlarmMsgUsrMap;

    static const uint16_t sc_max_alarm_msgs = 10000;
    StatAlarmMsgMap m_alarm_msgs;

    static const uint16_t sc_max_alarm_usr_map_size = 5000;
	StatAlarmMsgUsrMap m_alarm_usr_map;
	// 数据持久化模块
	uint32_t m_info_save_interval;
	DataStorage msg_data_storage;
	DataStorage usr_data_storage;

	//百度推送模块
	StatBDcloudPusher *m_p_bdcloud;

	// 业务控制变量
	uint32_t m_info_check_interval;
	uint32_t m_info_timeout;
	int m_pull_enabled;

	// 短信接口
	StatMobileAlarmer* m_sam;

private:
	StatAppPushAlarmer& operator = (const StatAppPushAlarmer& rhs);
	StatAppPushAlarmer(const StatAppPushAlarmer& rhs);

	// message操作 所有对map和msg的写仅通过此完成
	int add_msg(const string& title, const string& content, StatAlarmMsg &msg);
	int get_msg(const AlarmerMsgId& msg_id, StatAlarmMsg& msg);
	int link_msg(const AlarmerMsgId& msg_id);
	int unlink_msg(const AlarmerMsgId& msg_id);
	int del_msg(const AlarmerMsgId& msg_id);
	int del_oldest_msg();
	int get_expired_msg(StatAlarmMsgUsrList& msg_list);
	// user操作
	int add_user(const string& taomee_id, const string& app_id, const string& device_type, const string& mobile);
	int update_user(const string& taomee_id, const string& app_id, const string& device_type, const string& mobile);
	int get_user(const string& taomee_id, StatAlarmUser& user);
	int del_user(const string& taomee_id);
	// user msg 操作
	int add_user_msg(const string& taomee_id, const AlarmerMsgId& msg_id);
	int clear_user_msg(const string& taomee_id);
	int get_user_msgs(const string& taomee_id, StatAlarmMsgUsrList& msg_list);
	int del_user_msg(const string& taomee_id, const AlarmerMsgId& msg_id);
	int del_expired_msg();
	// 信息持久化
	int record_get();
	int record_save();

	string get_push_msg_content(const string& msg_id, const string& title, const string& content);
	int fetch_one_msg(StatPullMsgProto::AppPullMsgResponse& res, const AlarmerMsgId& msg_id, const string& taomee_id);
    virtual uint8_t send_alarm(const StatAlarmerProto::StatAlarmRequest& req);
};

#endif
