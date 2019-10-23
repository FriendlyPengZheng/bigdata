#include <cstring>

#include <stat_common.hpp>
#include <stat_config.hpp>
#include <fs_utils.hpp>
#include "stat_heartbeat_dn.hpp"

StatHeartbeatDN::StatHeartbeatDN() : m_present_capacity(0),
                         m_dfs_remaining(0),
                         m_dfs_used(0),
                         m_dfs_used_percent(0),
                         m_storage_info("")
{
    m_max_dfs_alarm_used_percent = (float)StatCommon::stat_config_get("dd-dfs-alarm-percent", 70);
    if (m_max_dfs_alarm_used_percent < 70.0f)
        m_max_dfs_alarm_used_percent = 70.0f;
    else if (m_max_dfs_alarm_used_percent > 99.99f)
        m_max_dfs_alarm_used_percent = 99.99f;

    m_max_dfs_alarm_percent_high = (float)StatCommon::stat_config_get("dd-dfs-alarm-percent-high-lv", 95);
    if (m_max_dfs_alarm_percent_high < m_max_dfs_alarm_used_percent)
        m_max_dfs_alarm_percent_high = m_max_dfs_alarm_used_percent;
    else if (m_max_dfs_alarm_percent_high > 99.99f)
        m_max_dfs_alarm_percent_high = 99.99f;
    DEBUG_LOG("StatHeartDN alarm config | Max DFS alarm used percent: %0.2f  high alarm: %0.2f",m_max_dfs_alarm_used_percent, m_max_dfs_alarm_percent_high);
}

int StatHeartbeatDN::_parse_hb_pkg(const void* hb_pkg) 
{
    if (NULL == hb_pkg)
        return -1;

    const DatanodeHeartbeatInfoHeader* header = static_cast<const DatanodeHeartbeatInfoHeader*>(hb_pkg);

    m_present_capacity = header->present_capacity;
    m_dfs_remaining = header->dfs_remaining;
    m_dfs_used = header->dfs_used;
    m_dfs_used_percent = header->dfs_used_percent;
    
    m_storage_info.clear();
    if ((uint32_t)header->len > sizeof(DatanodeHeartbeatInfoHeader))
    {
        m_storage_info = std::string(header->storage_info, (uint32_t)header->len - sizeof(DatanodeHeartbeatInfoHeader));
    }

    return 0;
}

bool StatHeartbeatDN::_alarm(time_t now, StatAlarmMsg& alarm_msg) const
{
    bool ret = false;
    std::ostringstream oss;

    if (m_dfs_used_percent > m_max_dfs_alarm_used_percent) 
    {
        oss << "Dfs used " << std::setprecision(2) << std::fixed 
            <<  m_dfs_used_percent << "%" << std::endl;
        if (m_dfs_used_percent > m_max_dfs_alarm_percent_high)
            alarm_msg.set_alarm_lv(0);
        ret = true;
    }

    if (ret && (!get_fbd_onoff()))
        alarm_msg += oss.str();

    return ret;
}

void StatHeartbeatDN::_print_hb_data_html(std::ostringstream& oss, bool& alarm) const
{
}

void StatHeartbeatDN::_print_hb_data_txt(std::ostringstream& oss, bool& alarm) const
{
    string color_begin, color_end;
    if (m_dfs_used_percent > m_max_dfs_alarm_used_percent)
    {
        color_begin += '\x1B';
        color_begin += "[0;31m";

        color_end += '\x1B';
        color_end += "[0m";

        alarm = true;
    }
    
    oss << "Present capacity size:    " << m_present_capacity << " Bytes (" 
        << StatCommon::convert_disk_size_unit(m_present_capacity) << ")" << std::endl
        << "Dfs remaining size:       " << m_dfs_remaining << " Bytes (" 
        << StatCommon::convert_disk_size_unit(m_dfs_remaining) << ")" << std::endl 
        << "Dfs used size:            " << m_dfs_used << " Bytes (" 
        << StatCommon::convert_disk_size_unit(m_dfs_used) << ")" << std::endl
        << color_begin
        << "Dfs used percent:         " << std::setprecision(2) << std::fixed 
        << m_dfs_used_percent << "%(warning line: " << m_max_dfs_alarm_used_percent << "%)"
        << color_end;
    if (!m_storage_info.empty())
        oss << std::endl
        << "Storage info:             " << m_storage_info;
}

void StatHeartbeatDN::_print_hb_data_web_html(char buf[], uint32_t& buf_len, bool& alarm) const
{
    if (m_dfs_used_percent > m_max_dfs_alarm_used_percent)
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

    memcpy(buf+buf_len, &m_present_capacity, sizeof(m_present_capacity));
    buf_len += sizeof(m_present_capacity);
    
    memcpy(buf+buf_len, &m_dfs_remaining, sizeof(m_dfs_remaining));
    buf_len += sizeof(m_dfs_remaining);

    memcpy(buf+buf_len, &m_dfs_used, sizeof(m_dfs_used));
    buf_len += sizeof(m_dfs_used);

    memcpy(buf+buf_len, &m_dfs_used_percent, sizeof(m_dfs_used_percent));
    buf_len += sizeof(m_dfs_used_percent);

    if (!m_storage_info.empty())
    {
        int32_t len = m_storage_info.length();
        memcpy(buf+buf_len, &len, sizeof(len));
        buf_len += sizeof(len);

        memcpy(buf+buf_len, m_storage_info.c_str(), m_storage_info.length());
        buf_len += m_storage_info.length();
    }
}

int StatHeartbeatDN::_backup(int fd) const
{
    int size = m_storage_info.size();

    if (::write(fd, &m_present_capacity, sizeof(m_present_capacity)) != sizeof(m_present_capacity) ||
        ::write(fd, &m_dfs_remaining, sizeof(m_dfs_remaining)) != sizeof(m_dfs_remaining) ||
        ::write(fd, &m_dfs_used, sizeof(m_dfs_used)) != sizeof(m_dfs_used) ||
        ::write(fd, &m_dfs_used_percent, sizeof(m_dfs_used_percent)) != sizeof(m_dfs_used_percent) ||
        ::write(fd, &size, sizeof(size)) != sizeof(size) ||
        (size > 0 && ::write(fd, m_storage_info.c_str(), size) != size) )
    {
        return -1;
    }

    return 0;
}

int StatHeartbeatDN::_restore(int fd)
{
    int ret=0, size=0;

    if (::read(fd, &m_present_capacity, sizeof(m_present_capacity)) != sizeof(m_present_capacity) ||
        ::read(fd, &m_dfs_remaining, sizeof(m_dfs_remaining)) != sizeof(m_dfs_remaining) ||
        ::read(fd, &m_dfs_used, sizeof(m_dfs_used)) != sizeof(m_dfs_used) ||
        ::read(fd, &m_dfs_used_percent, sizeof(m_dfs_used_percent)) != sizeof(m_dfs_used_percent) ||
        ::read(fd, &size, sizeof(size)) != sizeof(size) )
    {
        return -1;
    }

    if (size > 0)
    {
        char* buffer = (char*)malloc(size+1);
        if (NULL == buffer)
            return -1;
        do
        {
            memset(buffer, 0, size+1);
            if (::read(fd, buffer, size) != size)
                ret = -1;
            else
                m_storage_info = std::string(buffer, size);
        }while(false);
        free(buffer);
    }

    return ret;
}
