#include <stat_common.hpp>
#include <stat_config.hpp>
#include <fs_utils.hpp>
#include "stat_heartbeat_nn.hpp"

#include <cstring>

StatHeartbeatNN::StatHeartbeatNN() : m_safe_mode(0),
                         m_configured_capacity(0),
                         m_present_capacity(0),
                         m_dfs_remaining(0),
                         m_dfs_used(0),
                         m_dfs_used_percent(0.0f),
                         m_under_replicated_blocks(0),
                         m_missing_blocks(0),
                         m_total_datanodes(0),
                         m_live_nodes(0),
                         m_dead_nodes(0)
{
    m_max_dfs_used_percent = (float) StatCommon::stat_config_get("nn-dfs-alarm-percent", 70);
    if (m_max_dfs_used_percent < 70.0f)
        m_max_dfs_used_percent = 70.0f;
    else if (m_max_dfs_used_percent > 99.99f)
        m_max_dfs_used_percent = 99.99f;
    m_high_alarm_lv_percent = (float) StatCommon::stat_config_get("nn-dfs-alarm-percent-high-lv", 95);
    if (m_high_alarm_lv_percent < m_max_dfs_used_percent)
        m_high_alarm_lv_percent = m_max_dfs_used_percent;
    else if (m_high_alarm_lv_percent > 99.99f)
        m_high_alarm_lv_percent = 99.99f;

    DEBUG_LOG("StatHeartNN alarm config | Max DFS alarm used percent: %0.2f, high alarm: %0.2f",m_max_dfs_used_percent, m_high_alarm_lv_percent);
}

int StatHeartbeatNN::_parse_hb_pkg(const void* hb_pkg)
{
    if (NULL == hb_pkg)
        return -1;

    const NameNodeHeartbeatInfoHeader* header = static_cast<const NameNodeHeartbeatInfoHeader*>(hb_pkg);

    m_safe_mode = header->safe_mode;
    m_configured_capacity = header->configured_capacity;
    m_present_capacity = header->present_capacity;
    m_dfs_remaining = header->dfs_remaining;
    m_dfs_used = header->dfs_used;
    m_dfs_used_percent = header->dfs_used_percent;
    m_under_replicated_blocks = header->under_replicated_blocks;
    m_missing_blocks = header->missing_blocks;
    m_total_datanodes = header->total_datanodes;
    m_live_nodes = header->live_nodes;
    m_dead_nodes = header->dead_nodes;

    return 0;
}

bool StatHeartbeatNN::_alarm(time_t now, StatAlarmMsg& alarm_msg) const
{
    bool ret = false;
    std::ostringstream oss;
    uint8_t alarm_lv = 1;

    //检查是否需要告警，并给出告警消息
    if ( m_dfs_used_percent > m_max_dfs_used_percent) //已用磁盘空间占比(0.0-100.0)，大于一定比值需要报警
    {
        oss << "Disk used " << std::setprecision(2) << std::fixed  << m_dfs_used_percent << "%" << std::endl;
       if (m_dfs_used_percent > m_high_alarm_lv_percent)
          alarm_lv = 0; 
        ret = true;
    }

    if (m_missing_blocks > 0) //丢失的文件块数量，不为0需要报警
    {
        oss << "Missing block count " << m_missing_blocks << std::endl;
        ret = true;
    }

    if (m_dead_nodes > 0) //挂掉的datanode数量，不为0需要报警
    {
        oss << "Dead node count " << m_dead_nodes << std::endl;
        ret = true;
    }

    if (ret && !(get_fbd_onoff()))
    {
        alarm_msg += oss.str();
        if (alarm_msg.get_alarm_lv() > alarm_lv)
            alarm_msg.set_alarm_lv(alarm_lv);
    }

    return ret;
}

void StatHeartbeatNN::_print_hb_data_html(std::ostringstream& oss, bool& alarm) const
{
}

void StatHeartbeatNN::_print_hb_data_web_html(char buf[], uint32_t& buf_len, bool& alarm) const
{
    if (m_dfs_used_percent > m_max_dfs_used_percent ||
        m_missing_blocks > 0 ||
        m_dead_nodes > 0)
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

    memcpy(buf+buf_len, &m_safe_mode, sizeof(m_safe_mode));
    buf_len += sizeof(m_safe_mode);

    memcpy(buf+buf_len, &m_configured_capacity, sizeof(m_configured_capacity));
    buf_len += sizeof(m_configured_capacity);

    memcpy(buf+buf_len, &m_present_capacity, sizeof(m_present_capacity));
    buf_len += sizeof(m_present_capacity);

    memcpy(buf+buf_len, &m_dfs_remaining, sizeof(m_dfs_remaining));
    buf_len += sizeof(m_dfs_remaining);

    memcpy(buf+buf_len, &m_dfs_used, sizeof(m_dfs_used));
    buf_len += sizeof(m_dfs_used);

    memcpy(buf+buf_len, &m_dfs_used_percent, sizeof(m_dfs_used_percent));
    buf_len += sizeof(m_dfs_used_percent);

    memcpy(buf+buf_len, &m_max_dfs_used_percent, sizeof(m_max_dfs_used_percent));
    buf_len += sizeof(m_max_dfs_used_percent);

    memcpy(buf+buf_len, &m_under_replicated_blocks, sizeof(m_under_replicated_blocks));
    buf_len += sizeof(m_under_replicated_blocks);

    memcpy(buf+buf_len, &m_missing_blocks, sizeof(m_missing_blocks));
    buf_len += sizeof(m_missing_blocks);

    memcpy(buf+buf_len, &m_total_datanodes, sizeof(m_total_datanodes));
    buf_len += sizeof(m_total_datanodes);

    memcpy(buf+buf_len, &m_live_nodes, sizeof(m_live_nodes));
    buf_len += sizeof(m_live_nodes);

    memcpy(buf+buf_len, &m_dead_nodes, sizeof(m_dead_nodes));
    buf_len += sizeof(m_dead_nodes);
}

void StatHeartbeatNN::_print_hb_data_txt(std::ostringstream& oss, bool& alarm) const
{
    string color_begin, color_end;
    if (m_dfs_used_percent > m_max_dfs_used_percent ||
        m_missing_blocks > 0 ||
        m_dead_nodes > 0)
    {
        color_begin += '\x1B';
        color_begin += "[0;31m";
        color_end += '\x1B';
        color_end += "[0m";

        alarm = true;
    }
    
    oss << "Safe mode is:                     " << std::string(m_safe_mode ? "ON" : "OFF") << std::endl
        << "Configured capacity:              " << m_configured_capacity << " Bytes (" 
        << StatCommon::convert_disk_size_unit(m_configured_capacity) << ")" << std::endl
        << "Present capacity:                 " << std::setprecision(2) << std::fixed  << m_present_capacity 
        << " Bytes (" << StatCommon::convert_disk_size_unit(m_present_capacity) << ")" << std::endl
        << "Dfs remaining:                    " << m_dfs_remaining << " Bytes (" 
        << StatCommon::convert_disk_size_unit(m_dfs_remaining) << ")" << std::endl
        << "Dfs used:                         " << m_dfs_used << " Bytes (" 
        << StatCommon::convert_disk_size_unit(m_dfs_used) << ")" << std::endl;
        
    if (m_dfs_used_percent > m_max_dfs_used_percent) oss << color_begin;
    oss << "Dfs used percent:                 " << std::setprecision(2) << std::fixed << m_dfs_used_percent 
        << "% (warning line: " << m_max_dfs_used_percent << "%)" << std::endl;
    if (m_dfs_used_percent > m_max_dfs_used_percent) oss << color_end;
    
    oss << "Under replicated blocks number:   " << m_under_replicated_blocks << std::endl;
    if (m_missing_blocks > 0) oss << color_begin;
    oss << "Missing blocks number:            " << m_missing_blocks << std::endl;
    if (m_missing_blocks > 0) oss << color_end;

    oss << "Total datanodes number:           " << m_total_datanodes << std::endl
        << "Live nodes number:                " << m_live_nodes << std::endl;
    if (m_dead_nodes > 0)  oss << color_begin;
    oss << "Dead nodes number:                " << m_dead_nodes;
    if (m_dead_nodes > 0)  oss << color_end;
}

int StatHeartbeatNN::_backup(int fd) const
{
    if (::write(fd, &m_safe_mode, sizeof(m_safe_mode)) != sizeof(m_safe_mode) ||
        ::write(fd, &m_configured_capacity, sizeof(m_configured_capacity)) != sizeof(m_configured_capacity) ||
        ::write(fd, &m_present_capacity, sizeof(m_present_capacity)) != sizeof(m_present_capacity) ||
        ::write(fd, &m_dfs_remaining, sizeof(m_dfs_remaining)) != sizeof(m_dfs_remaining) ||
        ::write(fd, &m_dfs_used, sizeof(m_dfs_used)) != sizeof(m_dfs_used) ||
        ::write(fd, &m_dfs_used_percent, sizeof(m_dfs_used_percent)) != sizeof(m_dfs_used_percent) ||
        ::write(fd, &m_under_replicated_blocks, sizeof(m_under_replicated_blocks)) != sizeof(m_under_replicated_blocks) ||
        ::write(fd, &m_missing_blocks, sizeof(m_missing_blocks)) != sizeof(m_missing_blocks) ||
        ::write(fd, &m_total_datanodes, sizeof(m_total_datanodes)) != sizeof(m_total_datanodes) ||
        ::write(fd, &m_live_nodes, sizeof(m_live_nodes)) != sizeof(m_live_nodes) ||
        ::write(fd, &m_dead_nodes, sizeof(m_dead_nodes)) != sizeof(m_dead_nodes) )
    {
        return -1;
    }

    return 0;
}

int StatHeartbeatNN::_restore(int fd)
{
    if (::read(fd, &m_safe_mode, sizeof(m_safe_mode)) != sizeof(m_safe_mode) ||
        ::read(fd, &m_configured_capacity, sizeof(m_configured_capacity)) != sizeof(m_configured_capacity) ||
        ::read(fd, &m_present_capacity, sizeof(m_present_capacity)) != sizeof(m_present_capacity) ||
        ::read(fd, &m_dfs_remaining, sizeof(m_dfs_remaining)) != sizeof(m_dfs_remaining) ||
        ::read(fd, &m_dfs_used, sizeof(m_dfs_used)) != sizeof(m_dfs_used) ||
        ::read(fd, &m_dfs_used_percent, sizeof(m_dfs_used_percent)) != sizeof(m_dfs_used_percent) ||
        ::read(fd, &m_under_replicated_blocks, sizeof(m_under_replicated_blocks)) != sizeof(m_under_replicated_blocks) ||
        ::read(fd, &m_missing_blocks, sizeof(m_missing_blocks)) != sizeof(m_missing_blocks) ||
        ::read(fd, &m_total_datanodes, sizeof(m_total_datanodes)) != sizeof(m_total_datanodes) ||
        ::read(fd, &m_live_nodes, sizeof(m_live_nodes)) != sizeof(m_live_nodes) ||
        ::read(fd, &m_dead_nodes, sizeof(m_dead_nodes)) != sizeof(m_dead_nodes) )
    {
        return -1;
    }

    return 0;
}
