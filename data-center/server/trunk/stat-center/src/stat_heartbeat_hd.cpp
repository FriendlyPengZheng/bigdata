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

#include <stat_common.hpp>
#include <stat_config.hpp>
#include <fs_utils.hpp>
#include "stat_heartbeat_hd.hpp"

#include <cstring>

StatHeartbeatHd::StatHeartbeatHd()
    : m_wp_size(-1), // 初始化为uint64_t的最大值，以免在已注册但是未收到heartbeat数据时告警。
      m_if_count(0),
      m_if_size(0),
      m_of_count(0),
      m_of_size(0),
      m_sf_count(0),
      m_sf_size(0),
      m_wp_alarm(1024),
      m_stat_client_hd_alarm(false)
{
    m_wp_alarm = StatCommon::stat_config_get("hd-wp-alarm", 1024);
    if(m_wp_alarm < 100)
        m_wp_alarm = 100;
    if(m_wp_alarm > 30 * 1024)
        m_wp_alarm = 30 * 1024;
    m_wp_alarm_high = StatCommon::stat_config_get("hd-wp-size-high-lv", 100);
    if (m_wp_alarm_high > m_wp_alarm)
        m_wp_alarm_high = m_wp_alarm;
    else if (m_wp_alarm_high < 50)
        m_wp_alarm_high = 50;

    // 默认stat-client硬盘剩余空间不报警 
    m_stat_client_hd_alarm = StatCommon::stat_config_get("stat-client-hd-alarm", 0);

    DEBUG_LOG("working path alarm: %uMB, high alarm: %uMB", m_wp_alarm, m_wp_alarm_high);
}

int StatHeartbeatHd::_parse_hb_pkg(const void* hb_pkg)
{
    if(hb_pkg == NULL)
        return -1;

    const StatHeartbeatHdHeader* header = static_cast<const StatHeartbeatHdHeader*>(hb_pkg);

    m_wp_size = header->wp_size;
    m_if_count = header->if_count;
    m_if_size = header->if_size;
    m_of_count = header->of_count;
    m_of_size = header->of_size;
    m_sf_count = header->sf_count;
    m_sf_size = header->sf_size;

    return 0;
}

bool StatHeartbeatHd::_alarm(time_t now, StatAlarmMsg& alarm_msg) const
{
    bool ret = false;
    std::ostringstream oss;
    uint8_t alarm_lv = 1;

    // 检查硬盘可用空间，并给出告警消息
    if((get_module_info()->get_module_type() != STAT_CLIENT && m_wp_size / 1024 / 1024 < m_wp_alarm) || 
       (get_module_info()->get_module_type() == STAT_CLIENT && m_stat_client_hd_alarm && m_wp_size / 1024 / 1024 < m_wp_alarm))
    {
        oss << "Work path available " << StatCommon::convert_disk_size_unit(m_wp_size) << std::endl;

        if (m_wp_size / 1024 / 1024 < m_wp_alarm_high)  // 小于100M，设置告警级别为0，表明需要发送最高等级报警。
            alarm_lv = 0;
        ret = true;
    }

    // inbox or outbox中文件个数大于一个小时内的最大日志文件数，需要告警
    if(m_if_count > sc_max_files_per_hour)
    {
        oss << "inbox " << m_if_count << " > " << sc_max_files_per_hour << std::endl;
        ret = true;
    }
    if(m_of_count > sc_max_files_per_hour)
    {
        oss << "outbox " << m_of_count << " > " << sc_max_files_per_hour << std::endl;
        ret = true;
    }
    if(m_sf_count >= 3 * sc_max_files_per_hour)
    {
        oss << "sent " << m_sf_count << " >= " << 3 * sc_max_files_per_hour << std::endl;
        ret = true;
    }

    if(ret && (!get_fbd_onoff()))  //   add m_fbd_onoff here to avoid change alarm_msg
    {
        alarm_msg += oss.str();
        if (alarm_msg.get_alarm_lv() < alarm_lv)
            alarm_msg.set_alarm_lv(alarm_lv);
    }

    return ret;
}

void StatHeartbeatHd::_print_hb_data_html(std::ostringstream& oss, bool& alarm) const
{
}

void StatHeartbeatHd::_print_hb_data_web_html(char buf[], uint32_t& buf_len, bool& alarm) const
{
    if(m_wp_size / 1024 / 1024 < m_wp_alarm || 
            m_if_count >= sc_max_files_per_hour ||
            m_of_count >= sc_max_files_per_hour ||
            m_sf_count >= 3 * sc_max_files_per_hour) // 需要告警的，字体变红色。
    {
        alarm = true;
    }

    uint8_t red_flag = 0;
    if (alarm)
    {
        red_flag = 1;
    }

    memcpy(buf+buf_len, &red_flag, sizeof(red_flag));
    buf_len += sizeof(red_flag);

    uint8_t fbd_onoff = get_fbd_onoff();
    memcpy(buf+buf_len, &fbd_onoff, sizeof(fbd_onoff));
    buf_len += sizeof(fbd_onoff);

    time_t last_hb_time = get_heartbeat_time();
    memcpy(buf+buf_len, &last_hb_time, sizeof(last_hb_time));
    buf_len += sizeof(last_hb_time);

    memcpy(buf+buf_len, &m_wp_size, sizeof(m_wp_size));
    buf_len += sizeof(m_wp_size);

    memcpy(buf+buf_len, &m_if_count, sizeof(m_if_count));
    buf_len += sizeof(m_if_count);

    memcpy(buf+buf_len, &m_if_size, sizeof(m_if_size));
    buf_len += sizeof(m_if_size);

    memcpy(buf+buf_len, &m_of_count, sizeof(m_of_count));
    buf_len += sizeof(m_of_count);

    memcpy(buf+buf_len, &m_of_size, sizeof(m_of_size));
    buf_len += sizeof(m_of_size);

    memcpy(buf+buf_len, &m_sf_count, sizeof(m_sf_count));
    buf_len += sizeof(m_sf_count);

    memcpy(buf+buf_len, &m_sf_size, sizeof(m_sf_size));
    buf_len += sizeof(m_sf_size);
}

void StatHeartbeatHd::_print_hb_data_txt(std::ostringstream& oss, bool& alarm) const
{
    string color_begin, color_end;
    if(m_wp_size / 1024 / 1024 < m_wp_alarm || 
            m_if_count >= sc_max_files_per_hour ||
            m_of_count >= sc_max_files_per_hour ||
            m_sf_count >= 3 * sc_max_files_per_hour) // 需要告警的，字体变红色。
    {
        color_begin += '\x1B';
        color_begin += "[0;31m";

        color_end += '\x1B';
        color_end += "[0m";

        alarm = true;
    }
    string if_more, of_more, sf_more;
    if(m_if_count >= sc_max_files_per_hour)
        if_more = ">= ";
    if(m_of_count >= sc_max_files_per_hour)
        of_more = ">= ";
    if(m_sf_count >= 3 * sc_max_files_per_hour)
        sf_more = ">= ";

    oss << color_begin;

    oss << "Working path free size: " << m_wp_size << " Bytes" << " (" << StatCommon::convert_disk_size_unit(m_wp_size) << ")" << std::endl
        << "Files in inbox:         " << if_more << m_if_count << std::endl
        << "Directory inbox size:   " << if_more << m_if_size << " Bytes" << " (" << StatCommon::convert_disk_size_unit(m_if_size) << ")" << std::endl
        << "Files in outbox:        " << of_more << m_of_count << std::endl
        << "Directory outbox size:  " << of_more << m_of_size << " Bytes" << " (" << StatCommon::convert_disk_size_unit(m_of_size) << ")" << std::endl
        << "Files in sent:          " << sf_more << m_sf_count << std::endl
        << "Directory sent size:    " << sf_more << m_sf_size << " Bytes" << " (" << StatCommon::convert_disk_size_unit(m_sf_size) << ")";

    oss << color_end;
}

int StatHeartbeatHd::_backup(int fd) const
{
    if(::write(fd, &m_wp_size, sizeof(m_wp_size)) != sizeof(m_wp_size) || 
            ::write(fd, &m_if_count, sizeof(m_if_count)) != sizeof(m_if_count) || 
            ::write(fd, &m_if_size, sizeof(m_if_size)) != sizeof(m_if_size) || 
            ::write(fd, &m_of_count, sizeof(m_of_count)) != sizeof(m_of_count) || 
            ::write(fd, &m_of_size, sizeof(m_of_size)) != sizeof(m_of_size) || 
            ::write(fd, &m_sf_count, sizeof(m_sf_count)) != sizeof(m_sf_count) || 
            ::write(fd, &m_sf_size, sizeof(m_sf_size)) != sizeof(m_sf_size))
    {
        return -1;
    }

    return 0;
}

int StatHeartbeatHd::_restore(int fd)
{
    if(::read(fd, &m_wp_size, sizeof(m_wp_size)) != sizeof(m_wp_size) || 
            ::read(fd, &m_if_count, sizeof(m_if_count)) != sizeof(m_if_count) || 
            ::read(fd, &m_if_size, sizeof(m_if_size)) != sizeof(m_if_size) || 
            ::read(fd, &m_of_count, sizeof(m_of_count)) != sizeof(m_of_count) || 
            ::read(fd, &m_of_size, sizeof(m_of_size)) != sizeof(m_of_size) || 
            ::read(fd, &m_sf_count, sizeof(m_sf_count)) != sizeof(m_sf_count) || 
            ::read(fd, &m_sf_size, sizeof(m_sf_size)) != sizeof(m_sf_size))
    {
        return -1;
    }

    return 0;
}
