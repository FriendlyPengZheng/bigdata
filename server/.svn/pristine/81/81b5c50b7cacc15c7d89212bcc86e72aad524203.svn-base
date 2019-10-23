/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-alarmer服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */
#ifndef STAT_ALARM_USER_HPP
#define STAT_ALARM_USER_HPP

#include <string>

#include "data_storage.hpp"

using std::string;

class StatAlarmUser
{
public:
    StatAlarmUser()
    {}
    explicit StatAlarmUser(const string& taomee_id, const string& app_id, const string& device_type, const string& mobile) : m_taomee_id(taomee_id), m_app_id(app_id), m_device_type(device_type), m_mobile(mobile)
    {}
    StatAlarmUser(const char* taomee_id, const char* app_id, const char* device_type, const char* mobile) : m_taomee_id(taomee_id), m_app_id(app_id), m_device_type(device_type), m_mobile(mobile)
    {}
    ~StatAlarmUser()
    {}

    bool operator == (const StatAlarmUser& rhs) const
    {
        return m_taomee_id == rhs.m_taomee_id;
    }
    bool operator != (const StatAlarmUser& rhs) const
    {
        return m_taomee_id != rhs.m_taomee_id;
    }
    bool operator > (const StatAlarmUser& rhs) const
    {
        return m_taomee_id > rhs.m_taomee_id;
    }
    bool operator < (const StatAlarmUser& rhs) const
    {
        return m_taomee_id < rhs.m_taomee_id;
    }

    const string& get_taomee_id() const
    {
        return m_taomee_id;
    }
    const string& get_app_id() const
    {
        return m_app_id;
    }
    const string& get_device_type() const
    {
        return m_device_type;
    }
    const string& get_mobile() const
    {
        return m_mobile;
    }
    void update(const string& app_id, const string& device_type, const string& mobile) const
    {
        m_app_id = app_id;
        m_device_type = device_type;
        m_mobile = mobile;
    }
    int serialize_to_file(DataStorage *p_data_storage) const
    {
		if(p_data_storage->save_string(m_taomee_id) < 0
				|| p_data_storage->save_string(m_app_id) < 0
				|| p_data_storage->save_string(m_device_type) < 0
				|| p_data_storage->save_string(m_mobile) < 0)
		{
			return -1;
		}

		return 0;
    }
    int unserialize_from_file(DataStorage *p_data_storage)
    {
		if(p_data_storage->get_string(m_taomee_id) < 0
				|| p_data_storage->get_string(m_app_id) < 0
				|| p_data_storage->get_string(m_device_type) < 0
				|| p_data_storage->get_string(m_mobile) < 0)
		{
			return -1;
		}

		return 0;
    }

private:
    string m_taomee_id; // 淘米域用户名
    mutable string m_app_id;    // 手机客户端app的唯一标识符
    mutable string m_device_type;    // 手机客户端类型 3:android 4:ios
    mutable string m_mobile;    // 手机号
};

#endif

