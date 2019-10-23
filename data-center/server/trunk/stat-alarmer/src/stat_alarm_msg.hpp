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
#ifndef STAT_ALARM_MSG_HPP
#define STAT_ALARM_MSG_HPP

#include <cstdlib>
#include <ctime>
#include <cstring>
#include <string>
#include <sstream>

#include "data_storage.hpp"

using std::string;

class StatAlarmMsg
{
public:
    StatAlarmMsg() : m_ref_count(0)
    {}
    ~StatAlarmMsg()
    {}


public:
    typedef string StatAlarmMsgId;
    static const uint32_t sc_max_content_len = 1024; // 内容最大长度

private:
    void create_msg_id()
    {
        std::stringstream ss;

        ss << time(0) << ":" << ++s_inc_id;

        m_msg_id = ss.str();
    }

private:
    static uint32_t s_inc_id; 

    string m_msg_id; // 唯一标识符, 由时间戳和s_inc_id拼接而成。
    string m_title;
    string m_content;

    uint32_t m_ref_count; // 引用个数，用于记录是否有对象引用。

public:
    void set_alarm_msg(const string& title, const string& content)
    {
        m_title.assign(title, 0, sc_max_content_len);
        m_content.assign(content, 0, sc_max_content_len);
		create_msg_id();
    }
    void set_alarm_msg(const char* title, const char* content)
    {
        m_title.assign(title);
        if(m_title.size() > sc_max_content_len)
            m_title.resize(sc_max_content_len);
        m_content.assign(content);
        if(m_content.size() > sc_max_content_len)
            m_content.resize(sc_max_content_len);
		create_msg_id();
    }
    StatAlarmMsgId get_msg_id() const
    {
        return m_msg_id;
    }
    StatAlarmMsgId get_title() const
    {
        return m_title;
    }
    StatAlarmMsgId get_content() const
    {
        return m_content;
    }
    uint32_t get_ref_count()
    {
        return m_ref_count;
    }
    uint32_t ref_count_up()
    {
        return ++m_ref_count;
    }
    uint32_t ref_count_down()
    {
        return --m_ref_count;
    }
    int get_create_time() const
    {
		char msg_id_buf[32] = {0};// hard code
		strncpy(msg_id_buf, m_msg_id.c_str(), sizeof(msg_id_buf));
		char *p_time = strtok(msg_id_buf, ":");
        return atoi(p_time);
    }
    int serialize_to_file(DataStorage *p_data_storage) const
    {
		if(p_data_storage->save_string(m_msg_id) < 0
				|| p_data_storage->save_string(m_title) < 0
				|| p_data_storage->save_string(m_content) < 0
				|| p_data_storage->save_uint32(m_ref_count) < 0)
		{
			return -1;
		}

		return 0;
    }
    int unserialize_from_file(DataStorage *p_data_storage)
    {
		if(p_data_storage->get_string(m_msg_id) < 0
				|| p_data_storage->get_string(m_title) < 0
				|| p_data_storage->get_string(m_content) < 0
				|| p_data_storage->get_uint32(m_ref_count) < 0)
		{
			return -1;
		}

		return 0;
    }
};

#endif
