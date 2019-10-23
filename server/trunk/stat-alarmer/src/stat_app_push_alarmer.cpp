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

#include <stat_config.hpp>
#include "base64_utils.hpp"
#include "stat_common.hpp"
#include "stat_app_push_alarmer.hpp"

StatAppPushAlarmer::StatAppPushAlarmer(uint32_t proto_id, const char* proto_name, StatGroupAlarmer* sag, StatMobileAlarmer* sam)
	: StatAlarmerHandler(proto_id, proto_name, STAT_ALARMER_GROUP_APPPUSH, sag),
	m_p_bdcloud(NULL),
	m_sam(sam)
{
	string bdcloud_api_key, bdcloud_secret_key;
	StatCommon::stat_config_get("bdcloud_api_key", bdcloud_api_key);
	StatCommon::stat_config_get("bdcloud_secret_key", bdcloud_secret_key);
	m_p_bdcloud = new (std::nothrow) StatBDcloudPusher(bdcloud_api_key, bdcloud_secret_key);

	m_info_check_interval =  StatCommon::stat_config_get("app_info_check_interval", 60);
	m_info_timeout = StatCommon::stat_config_get("app_info_timeout", 3600);

	m_pull_enabled = StatCommon::stat_config_get("app_pull_enabled", 0);
	if(m_pull_enabled != 0 && m_pull_enabled != 1)
	{
		m_pull_enabled = 0;
	}

	// 数据持久化模块
	m_info_save_interval =  StatCommon::stat_config_get("app_info_save_interval", 60);
	m_info_timeout = StatCommon::stat_config_get("app_info_timeout", 3600);
	string msg_data_file_path, usr_data_file_path;
	StatCommon::stat_config_get("msg_data_file_path", msg_data_file_path);
	StatCommon::stat_config_get("usr_data_file_path", usr_data_file_path);
	if(msg_data_storage.init(msg_data_file_path) < 0)
	{
		ERROR_LOG("msg_data_storage init failed");
	}
	if(usr_data_storage.init(usr_data_file_path) < 0)
	{
		ERROR_LOG("usr_data_storage init failed");
	}
	if(record_get() < 0)
	{
		ERROR_LOG("record_get failed");
	}
}

StatAppPushAlarmer::~StatAppPushAlarmer()
{
	if(m_p_bdcloud != NULL)
	{
		delete m_p_bdcloud;
		m_p_bdcloud = NULL;
	}
}

int StatAppPushAlarmer::uninit()
{
	if(record_save() < 0)
	{
		ERROR_LOG("record_save failed");
		return -1;
	}

	return 0;
}

int StatAppPushAlarmer::add_msg(const string& title, const string& content, StatAlarmMsg &msg)
{
	if(m_alarm_msgs.size() >= sc_max_alarm_msgs)
	{
		if(del_oldest_msg() < 0)
		{
			// TODO
			ERROR_LOG("del_oldest_msg failed");
			return -1;
		}
	}

	msg.set_alarm_msg(title, content);
	std::pair<StatAlarmMsgMap::iterator, bool> ret;
	ret = m_alarm_msgs.insert(make_pair(msg.get_msg_id(), msg));
	if(ret.second)
	{
		// TODO
		DEBUG_LOG("add_msg.msg_id:%s", msg.get_msg_id().c_str());
		return 0;
	}
	else
	{
		// TODO delete
		ERROR_LOG("add_msg.failed.msg_id:%s", msg.get_msg_id().c_str());
		return -1;
	}
}

// 返回map中msg的复本
int StatAppPushAlarmer::get_msg(const AlarmerMsgId& msg_id, StatAlarmMsg& msg)
{
	StatAlarmMsgMap::iterator msg_it = m_alarm_msgs.find(msg_id);
	if(msg_it == m_alarm_msgs.end())
	{
		//ERROR_LOG("find msg failed");
		return -1;
	}

	msg = msg_it->second;

	return 0;
}

int StatAppPushAlarmer::link_msg(const AlarmerMsgId& msg_id)
{
	StatAlarmMsgMap::iterator msg_it = m_alarm_msgs.find(msg_id);
	if(msg_it == m_alarm_msgs.end())
	{
		// TODO delete
		ERROR_LOG("link_msg.find failed");
		return -1;
	}

	msg_it->second.ref_count_up();

	return 0;
}

int StatAppPushAlarmer::unlink_msg(const AlarmerMsgId& msg_id)
{
	StatAlarmMsgMap::iterator msg_it = m_alarm_msgs.find(msg_id);
	if(msg_it == m_alarm_msgs.end())
	{
		WARN_LOG("unlink_msg.find failed.msg_id:%s", msg_id.c_str());
		return 1;
	}

	if(msg_it->second.ref_count_down() == 0)
	{
		m_alarm_msgs.erase(msg_it);
		// TODO
		DEBUG_LOG("unlink del msg.msg_id:%s", msg_id.c_str());
	}

	return 0;
}

int StatAppPushAlarmer::del_msg(const AlarmerMsgId& msg_id)
{
	StatAlarmMsgMap::iterator msg_it = m_alarm_msgs.find(msg_id);
	if(msg_it == m_alarm_msgs.end())
	{
		ERROR_LOG("del_msg.find failed.msg_id:%s", msg_id.c_str());
		return -1;
	}

	m_alarm_msgs.erase(msg_it);

	return 0;
}

int StatAppPushAlarmer::del_oldest_msg()
{
	StatAlarmMsgMap::iterator msg_it, oldest_msg_it = m_alarm_msgs.begin();
	for(msg_it = m_alarm_msgs.begin(); msg_it != m_alarm_msgs.end(); msg_it++)
	{
		if(msg_it->second.get_create_time() < oldest_msg_it->second.get_create_time())
		{
			oldest_msg_it = msg_it;
		}
	}

	m_alarm_msgs.erase(oldest_msg_it);
	//TODO
	DEBUG_LOG("del oldest msg.msg_id:%s", oldest_msg_it->second.get_msg_id().c_str());

	return 0;
}

int StatAppPushAlarmer::get_expired_msg(StatAlarmMsgUsrList& msg_list)
{
	msg_list.clear();
	StatAlarmMsgMap::iterator msg_it;
	for(msg_it = m_alarm_msgs.begin(); msg_it != m_alarm_msgs.end(); msg_it++)
	{
		if(msg_it->second.get_create_time() < time(NULL) - m_info_timeout)
		{
			msg_list.push_back(msg_it->second.get_msg_id());
		}
	}

	return 0;
}

int StatAppPushAlarmer::add_user(const string& taomee_id, const string& app_id, const string& device_type, const string& mobile)
{
	const StatAlarmUser user(taomee_id, app_id, device_type, mobile);

	StatAlarmMsgUsrMap::iterator user_it = m_alarm_usr_map.find(user);
	if(user_it != m_alarm_usr_map.end())
	{
		return 1;
	}

	if(m_alarm_usr_map.size() >= sc_max_alarm_usr_map_size)
	{
		return -1;
	}

	StatAlarmMsgUsrList user_list;
	user_list.clear();
	std::pair<StatAlarmMsgUsrMap::iterator, bool> ret;
	ret = m_alarm_usr_map.insert(make_pair(user, user_list));
	if(ret.second)
	{
		return 0;
	}
	else
	{
		// TODO delete
		ERROR_LOG("add_user.failed.taomee_id:%s", taomee_id.c_str());
		return -1;
	}

	return 0;
}

int StatAppPushAlarmer::update_user(const string& taomee_id, const string& app_id, const string& device_type, const string& mobile)
{
	const StatAlarmUser user(taomee_id, app_id, device_type, mobile);

	StatAlarmMsgUsrMap::iterator user_it = m_alarm_usr_map.find(user);
	if(user_it != m_alarm_usr_map.end())
	{
		user_it->first.update(app_id, device_type, mobile);
		return 0;
	}
	else
	{
		return -1;
	}
}

int StatAppPushAlarmer::get_user(const string& taomee_id, StatAlarmUser& user)
{
	StatAlarmUser tmp_user(taomee_id.c_str(), "", "", "");
	StatAlarmMsgUsrMap::iterator user_it = m_alarm_usr_map.find(tmp_user);
	if(user_it == m_alarm_usr_map.end())
	{
		ERROR_LOG("user info not found.taomee_id:%s", taomee_id.c_str());
		return -1;
	}

	user = user_it->first;

	return 0;
}

int StatAppPushAlarmer::del_user(const string& taomee_id)
{
	StatAlarmUser tmp_user(taomee_id.c_str(), "", "", "");
	StatAlarmMsgUsrMap::iterator user_it = m_alarm_usr_map.find(tmp_user);
	if(user_it == m_alarm_usr_map.end())
	{
		// TODO change position
		WARN_LOG("del_user.user not found.taomee_id:%s", taomee_id.c_str());
		return 1;
	}

	// 清空该用户的信息
	if(clear_user_msg(taomee_id) != 0)
	{
		ERROR_LOG("del_user.clear user msg failed.taomee_id:%s", taomee_id.c_str());
		return -1;
	}

	m_alarm_usr_map.erase(user_it);

	return 0;
}

int StatAppPushAlarmer::add_user_msg(const string& taomee_id, const AlarmerMsgId& msg_id)
{
	StatAlarmUser tmp_user(taomee_id.c_str(), "", "", "");

	StatAlarmMsgUsrMap::iterator user_it = m_alarm_usr_map.find(tmp_user);
	if(user_it == m_alarm_usr_map.end())
	{
		ERROR_LOG("add_user_msg.user info not found.taomee_id:%s", taomee_id.c_str());
		return -1;
	}

	if(StatAppPushAlarmer::link_msg(msg_id) < 0)
	{
		ERROR_LOG("add_user_msg.link_msg failed.msg_id:%s", msg_id.c_str());
		return -1;
	}

	user_it->second.push_back(msg_id);

	return 0;
}

int StatAppPushAlarmer::clear_user_msg(const string& taomee_id)
{
	const StatAlarmUser tmp_user(taomee_id, "", "", "");

    StatAlarmMsgUsrMap::iterator user_it = m_alarm_usr_map.find(tmp_user);
    if(user_it == m_alarm_usr_map.end())
    {
        return 1;
    }

    StatAlarmMsgUsrList msg_list = user_it->second;
    StatAlarmMsgUsrList::iterator list_it;
    for(list_it = msg_list.begin(); list_it != msg_list.end(); list_it++)
    {
		if(unlink_msg(*list_it) < 0)
		{
			return -1;
		}
    }

    user_it->second.clear();

    return 0;
}

int StatAppPushAlarmer::get_user_msgs(const string& taomee_id, StatAlarmMsgUsrList & msg_list)
{
	const StatAlarmUser tmp_user(taomee_id, "", "", "");

    StatAlarmMsgUsrMap::iterator user_it = m_alarm_usr_map.find(tmp_user);
    if(user_it == m_alarm_usr_map.end())
    {
        return -1;
    }

    msg_list = user_it->second;

    return 0;
}

int StatAppPushAlarmer::del_user_msg(const string& taomee_id, const AlarmerMsgId& msg_id)
{
	const StatAlarmUser tmp_user(taomee_id, "", "", "");

	StatAlarmMsgUsrMap::iterator user_it = m_alarm_usr_map.find(tmp_user);
	if(user_it == m_alarm_usr_map.end())
	{
		return -1;
	}
	user_it->second.remove(msg_id);

	if(unlink_msg(msg_id) < 0)
	{
		return -1;
	}

	return 0;
}

int StatAppPushAlarmer::del_expired_msg()
{
	StatAlarmMsgUsrList expired_msg_list;
	if(get_expired_msg(expired_msg_list) < 0)
	{
		ERROR_LOG("del_expired_msg.get expired msg failed");
		return -1;
	}

	// XXX:可封装下面代码为一个函数
	// del msg
    StatAlarmMsgUsrList::iterator expired_list_it;
    for(expired_list_it = expired_msg_list.begin(); expired_list_it != expired_msg_list.end(); expired_list_it++)
    {
		// 获取消息内容
		StatAlarmMsg msg;
		if(get_msg(*expired_list_it, msg) < 0)
		{
			return -1;
		}

		// 删除所有用户中的该消息
		StatAlarmMsgUsrMap::iterator user_it;
		for(user_it = m_alarm_usr_map.begin(); user_it != m_alarm_usr_map.end(); user_it++)
		{
			// 查询该用户是否存在本消息
			StatAlarmMsgUsrList::iterator usr_list_it;
			for(usr_list_it = user_it->second.begin(); usr_list_it != user_it->second.end(); usr_list_it++)
			{
				if(*usr_list_it == *expired_list_it)
					break;
			}
			if(usr_list_it == user_it->second.end())
			{
				continue;
			}

			// 发送短信
			if(m_sam->send_msg(user_it->first.get_mobile(), msg.get_content()) < 0)
			{
				ERROR_LOG("send mobile msg failed.mobile:%s", user_it->first.get_mobile().c_str());
			}
			INFO_LOG("del_expired_msg.send mobile msg OK.mobile:%s, content:%s", user_it->first.get_mobile().c_str(), msg.get_content().c_str());

			// 删除user消息列表中的该消息
			user_it->second.remove(*expired_list_it);
			//debug code
			//DEBUG_LOG("del_expired_msg.delete user msg.user_id:%s, msg_id:%s",
					//user_it->first.get_taomee_id().c_str(), (*expired_list_it).c_str());
		}

		// 删除该消息
		if(del_msg(*expired_list_it) < 0)
		{
			ERROR_LOG("del_expired_msg.del msg failed");
			return -1;
		}
		DEBUG_LOG("del_expired_msg.del_msg.msg_id:%s", (*expired_list_it).c_str());
    }

	return 0;
}

int StatAppPushAlarmer::record_get()
{
	// get msg map
	int ret = msg_data_storage.get_start();
	if(ret < 0)
	{
		ERROR_LOG("msg_data_storage.get_start failed");
		return -1;
	}
	else if(ret == 1)
	{
		return 1;
	}
	// 读取m_alarm_msgs大小
	uint32_t msg_map_size = 0;
	if(msg_data_storage.get_uint32(msg_map_size) < 0)
	{
		ERROR_LOG("record_get.msg_data_storage get_uint32 failed");
		return -1;
	}
	// 读取每个消息
	for(uint32_t i = 0; i < msg_map_size; i++)
	{
		string msg_id;
		if(msg_data_storage.get_string(msg_id) < 0)
		{
			ERROR_LOG("record_get.msg_data_storage get_string failed");
			return -1;
		}
		StatAlarmMsg msg;
		if(msg.unserialize_from_file(&msg_data_storage) < 0)
		{
			ERROR_LOG("record_get.msg unserialize_from_file failed");
			return -1;
		}
		// insert into msg map
		std::pair<StatAlarmMsgMap::iterator, bool> insert_ret;
		insert_ret = m_alarm_msgs.insert(make_pair(msg_id, msg));
		if(!insert_ret.second)
		{
			WARN_LOG("record_get.inset msg.failed.msg_id:%s", msg.get_msg_id().c_str());
		}
	}
	
	// get user map
	ret = usr_data_storage.get_start();
	if(ret < 0)
	{
		ERROR_LOG("usr_data_storage.get_start failed");
		return -1;
	}
	else if(ret == 1)
	{
		return 1;
	}
	// 读取m_alarm_usr_map大小
	uint32_t usr_map_size = 0;
	if(usr_data_storage.get_uint32(usr_map_size) < 0)
	{
		ERROR_LOG("record_get.usr_data_storage get_uint32 failed");
		return -1;
	}
	// 读取每个user记录
	for(uint32_t i = 0; i < usr_map_size; i++)
	{
		// get user
		StatAlarmUser user;
		if(user.unserialize_from_file(&usr_data_storage) < 0)
		{
			ERROR_LOG("record_get.user unserialize_from_file failed");
			return -1;
		}

		// get user list
		StatAlarmMsgUsrList user_list;
		user_list.clear();
		// read msg list
		uint32_t usr_list_size = 0;
		if(usr_data_storage.get_uint32(usr_list_size) < 0)
		{
			ERROR_LOG("record_get.usr_data_storage get_uint32 failed");
			return -1;
		}
		for(uint32_t j = 0; j < usr_list_size; j++)
		{
			// XXX:msg_id可独立封装,完全隐藏具体类型
			string msg_id;
			if(usr_data_storage.get_string(msg_id) < 0)
			{
				ERROR_LOG("record_get.usr_data_storage get_string failed");
				return -1;
			}
			user_list.push_back(msg_id);
		}

		// insert into user map
		std::pair<StatAlarmMsgUsrMap::iterator, bool> ret;
		ret = m_alarm_usr_map.insert(make_pair(user, user_list));
		if(!ret.second)
		{
			WARN_LOG("record_get.add_user.failed.user:%s", user.get_taomee_id().c_str());
		}
	}

	return 0;
}

int StatAppPushAlarmer::record_save()
{
	// 保存m_alarm_msgs
	int ret = msg_data_storage.save_start();
	if(ret < 0)
	{
		ERROR_LOG("msg_data_storage.save_start failed");
		return -1;
	}
	// 保存m_alarm_msgs大小
	if(msg_data_storage.save_uint32(m_alarm_msgs.size()) < 0)
	{
		ERROR_LOG("record_save.msg_data_storage save_uint32 failed");
		return -1;
	}
	StatAlarmMsgMap::iterator msg_it;
	for(msg_it = m_alarm_msgs.begin(); msg_it != m_alarm_msgs.end(); msg_it++)
	{
		if(msg_data_storage.save_string(msg_it->first) < 0)
		{
			ERROR_LOG("record_save.msg_data_storage save_string failed");
			return -1;
		}
		if(msg_it->second.serialize_to_file(&msg_data_storage) < 0)
		{
			ERROR_LOG("record_get.msg unserialize_from_file failed");
			return -1;
		}
	}

	// 保存m_alarm_usr_map
	ret = usr_data_storage.save_start();
	if(ret < 0)
	{
		ERROR_LOG("user_data_storage.save_start failed");
		return -1;
	}
	// 保存m_alarm_usr_map大小
	if(usr_data_storage.save_uint32(m_alarm_usr_map.size()) < 0)
	{
		ERROR_LOG("record_save.usr_data_storage save_uint32 failed");
		return -1;
	}

	// 保存内容
	StatAlarmMsgUsrMap::iterator user_it;
	for(user_it = m_alarm_usr_map.begin(); user_it != m_alarm_usr_map.end(); user_it++)
	{
		if(user_it->first.serialize_to_file(&usr_data_storage) < 0)
		{
			ERROR_LOG("record_get.user unserialize_from_file failed");
			return -1;
		}
		// save msg list
		// XXX:user list 可以独立出来，自身实现序列化存储方法
		if(usr_data_storage.save_uint32(user_it->second.size()) < 0)
		{
			ERROR_LOG("record_save.usr_data_storage save_uint32 failed");
			return -1;
		}
		StatAlarmMsgUsrList::iterator list_it;
		for(list_it = user_it->second.begin(); list_it != user_it->second.end(); list_it++)
		{
			if(usr_data_storage.save_string(*list_it) < 0)
			{
				ERROR_LOG("record_save.usr_data_storage save_string failed");
				return -1;
			}
			// TODO
			//DEBUG_LOG("recode_save.user msg.msg_id:%s", (*list_it).c_str());
		}
	}

	return 0;
}

string StatAppPushAlarmer::get_push_msg_content(const string& msg_id, const string& title, const string& content)
{
	StatPullMsgProto::AppPullMsgResponse res;
	res.set_ret(m_pull_enabled ? 3 : 2);
	StatPullMsgProto::MsgBody *msg = res.add_msg();
	msg->set_msg_id(msg_id);
	msg->set_title(title);
	msg->set_content(m_pull_enabled ? msg_id : content);

	string proto_str;
	if(!res.SerializeToString(&proto_str))
	{
		ERROR_LOG("SerializeToString failed");
	}
	string proto_base64;
	if(StatCommon::encode_base64(proto_str, proto_base64) < 0)
	{
		ERROR_LOG("encode_base64 failed");
		return "";
	}

	return proto_base64;
}

uint8_t StatAppPushAlarmer::send_alarm(const StatAlarmerProto::StatAlarmRequest& req) 
{
	//DEBUG_LOG("send with app.title:%s content:%s",
			//req.title().c_str(), req.content().c_str());

	if(req.send_to_size() < 1)
	{
		ERROR_LOG("send_to_size < 1");
		return 1;
	}

	if(m_p_bdcloud == NULL)
	{
		ERROR_LOG("m_p_bdcloud not available.");
		return 1;
	}

	// 记录消息内容
	StatAlarmMsg msg;
	if(add_msg(req.title(), req.content(), msg) < 0)
	{
		ERROR_LOG("add msg failed");
		return 1;
	}

	// 组织push内容
	StatMsgPusher::PushMsgRequest push_req;
	push_req.msg_key = msg.get_msg_id();
	push_req.msg_title = req.title();
	push_req.msg_content = get_push_msg_content(msg.get_msg_id(), req.title(), req.content());
	//DEBUG_LOG("msg_key:%s content:%s", push_req.msg_key.c_str(), push_req.msg_content.c_str());

	int count = 0;
	uint8_t status_code = 1;
	for(int i = 0; i < req.send_to_size(); ++i)
	{
		const StatAlarmerProto::AlarmContact& contact = req.send_to(i);
		StatAlarmUser user;
		if(get_user(contact.name(), user) < 0)
		{
			WARN_LOG("get user info failed.taomme_id:%s", contact.name().c_str());
			// 发送短信
			if(m_sam->send_msg(contact.mobile(), req.content()) < 0)
			{
				ERROR_LOG("send mobile msg failed.mobile:%s", contact.mobile().c_str());
			}
			INFO_LOG("send_alarm.send mobile msg OK.mobile:%s, content:%s", contact.mobile().c_str(), req.content().c_str());
			continue;
		}

		push_req.device_type = user.get_device_type();
		//DEBUG_LOG("sent to app.app_id:%s, device_type:%s", app_id.c_str(), push_req.device_type.c_str());
		// DEBUG_LOG("bdcloud push_msg start time:%ld", time(NULL));
		int ret = m_p_bdcloud->push_msg(user.get_app_id(), push_req); 
		// DEBUG_LOG("bdcloud push_msg end time:%ld", time(NULL));
		if(ret == 0)
		{
			INFO_LOG("send app msg success.taomee_id:%s", contact.name().c_str());
		}
		else if(ret == 1)
		{
			// timeout
			WARN_LOG("send app msg timeout.ret:%d taomee_id:%s", ret, contact.name().c_str());
		}
		else
		{
			ERROR_LOG("send app msg failed.ret:%d taomee_id:%s", ret, contact.name().c_str());
			// 发送短信
			if(m_sam->send_msg(contact.mobile(), req.content()) < 0)
			{
				ERROR_LOG("send mobile msg failed.mobile:%s", contact.mobile().c_str());
			}
			INFO_LOG("send_alarm.send mobile msg OK.mobile:%s, content:%s", contact.mobile().c_str(), req.content().c_str());
			continue;
		}

		// 添加用户消息
		if(StatAppPushAlarmer::add_user_msg(contact.name(), msg.get_msg_id()) < 0)
		{
			ERROR_LOG("add user msg failed");
			goto DONE;
		}
		count++;
	}
	status_code = 0;

DONE:
	if(count == 0)
	{
		if(del_msg(msg.get_msg_id()) < 0)
		{
			ERROR_LOG("send alarm.del msg failed");
			return 1;
		}
		// TODO
		DEBUG_LOG("send_alarm.del_msg.msg_id:%s", msg.get_msg_id().c_str());
	}
	return status_code;
}

int StatAppPushAlarmer::fetch_one_msg(StatPullMsgProto::AppPullMsgResponse& res, const AlarmerMsgId& msg_id, const string& taomee_id)
{
	// get msg
	StatAlarmMsg msg;
	if(get_msg(msg_id, msg) < 0)
	{
		ERROR_LOG("get msg failed.msg_id:%s", msg_id.c_str());
		return -1;
	}

	StatPullMsgProto::MsgBody *msgbody = res.add_msg();
	msgbody->set_msg_id(msg_id);
	msgbody->set_title(msg.get_title());
	msgbody->set_content(msg.get_content());

	// delele user msg
	if(del_user_msg(taomee_id, msg_id) < 0)
	{
		ERROR_LOG("set_msg_proto.del_user_msg.msg_id:%s", msg_id.c_str());
		return -1;
	}

	return 0;
}

int StatAppPushAlarmer::fetch_alarm(StatPullMsgProto::AppPullMsgRequest req, StatPullMsgProto::AppPullMsgResponse& res)
{
	StatAlarmMsgUsrList msg_list;
	if(get_user_msgs(req.user_name(), msg_list) < 0)
	{
		ERROR_LOG("fethch_alarm.get_user_msgs failed");
		return -1;
	}
	if(msg_list.size() == 0)
	{
		return 0;
	}

	if(req.msg_id_size() == 0)
	{
		// fetch all msg
		StatAlarmMsgUsrList::iterator list_it;
		for(list_it = msg_list.begin(); list_it != msg_list.end(); list_it++)
		{
			if(fetch_one_msg(res, *list_it, req.user_name()) < 0)
			{
				ERROR_LOG("fetch alarm for all msg.fetch_one_msg failed.msg_id:%s, taomee_id:%s",
						(*list_it).c_str(), req.user_name().c_str());
				continue;
			}
		}
	}
	else if(req.msg_id_size() > 0)
	{
		// fetch specific msg
		for(int i = 0; i < req.msg_id_size(); ++i)
		{
			if(fetch_one_msg(res, req.msg_id(i), req.user_name()) < 0)
			{
				ERROR_LOG("fetch alarm for specific msg.fetch_one_msg failed.msg_id:%s, taomee_id:%s",
						req.msg_id(i).c_str(), req.user_name().c_str());
				continue;
			}
		}
	}
	else
	{
		return -1;
	}

	return 0;
}

int StatAppPushAlarmer::app_register(const string& taomee_id, const string& app_id, const string& device_type, const string& mobile)
{
	//更新用户信息
	int ret = add_user(taomee_id, app_id, device_type, mobile);
	if(ret < 0)
	{
		ERROR_LOG("add user failed");
		return -1;
	}
	else if(ret == 1)
	{
		if(update_user(taomee_id, app_id, device_type, mobile) < 0)
		{
			ERROR_LOG("update user failed");
			return -1;
		}
	}

	return 0;
}

int StatAppPushAlarmer::app_unregister(const string& taomee_id, const string& app_id)
{
	if(del_user(taomee_id) < 0)
	{
		ERROR_LOG("del user failed");
		return -1;
	}

	return 0;
}

void StatAppPushAlarmer::proc_timer_event()
{
	// check msg expired
	static int last_check_time = time(NULL);
	if(time(NULL) >= last_check_time + m_info_check_interval)
	{
		if(del_expired_msg() < 0)
		{
			ERROR_LOG("del_expired_msg failed");
		}
		last_check_time = time(NULL);
	}

	// record save
	static int last_save_time = time(NULL);
	if(time(NULL) >= last_save_time + m_info_save_interval)
	{
		if(record_save() < 0)
		{
			ERROR_LOG("record_save failed");
		}
		//DEBUG_LOG("record_save OK");
		last_save_time = time(NULL);
	}
}
