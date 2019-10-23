#include <stat_common.hpp>
#include <fs_utils.hpp>
#include "stat_heartbeat_tt.hpp"

#include <cstring>

int StatHeartbeatTT::_parse_hb_pkg(const void* hb_pkg)
{
    if (NULL == hb_pkg)
        return -1;

    const TasktrackerHeartbeatInfoHeader* header = static_cast<const TasktrackerHeartbeatInfoHeader*>(hb_pkg);

    m_maps_running = header->maps_running;
    m_reduce_running = header->reduce_running;
    m_map_task_slots = header->map_task_slots;
    m_reduce_task_slots = header->reduce_task_slots;
    m_task_completed = header->task_completed;

    return 0;
}

bool StatHeartbeatTT::_alarm(time_t now, StatAlarmMsg& alarm_msg) const
{
    return false;
}

void StatHeartbeatTT::_print_hb_data_html(std::ostringstream& oss, bool& alarm) const   // TOFIX
{
}

void StatHeartbeatTT::_print_hb_data_web_html(char buf[], uint32_t& buf_len, bool& alarm) const
{
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

    memcpy(buf+buf_len, &m_maps_running, sizeof(m_maps_running));
    buf_len += sizeof(m_maps_running);

    memcpy(buf+buf_len, &m_reduce_running, sizeof(m_reduce_running));
    buf_len += sizeof(m_reduce_running);

    memcpy(buf+buf_len, &m_map_task_slots, sizeof(m_map_task_slots));
    buf_len += sizeof(m_map_task_slots);

    memcpy(buf+buf_len, &m_reduce_task_slots, sizeof(m_reduce_task_slots));
    buf_len += sizeof(m_reduce_task_slots);

    memcpy(buf+buf_len, &m_task_completed, sizeof(m_task_completed));
    buf_len += sizeof(m_task_completed);
}

void StatHeartbeatTT::_print_hb_data_txt(std::ostringstream& oss, bool& alarm) const
{
    oss << "Maps running count:       " << m_maps_running << std::endl
        << "Reduce running count:     " << m_reduce_running << std::endl
        << "Map task slots count:     " << m_map_task_slots << std::endl
        << "Reduce task slots count:  " << m_reduce_task_slots << std::endl
        << "Task completed count:     " << m_task_completed;
}

int StatHeartbeatTT::_backup(int fd) const
{
    if (::write(fd, &m_maps_running, sizeof(m_maps_running)) != sizeof(m_maps_running) ||
        ::write(fd, &m_reduce_running, sizeof(m_reduce_running)) != sizeof(m_reduce_running) ||
        ::write(fd, &m_map_task_slots, sizeof(m_map_task_slots)) != sizeof(m_map_task_slots) ||
        ::write(fd, &m_reduce_task_slots, sizeof(m_reduce_task_slots)) != sizeof(m_reduce_task_slots) ||
        ::write(fd, &m_task_completed, sizeof(m_task_completed)) != sizeof(m_task_completed) )
    {
        return -1;
    }

    return 0;
}

int StatHeartbeatTT::_restore(int fd)
{
    if (::read(fd, &m_maps_running, sizeof(m_maps_running)) != sizeof(m_maps_running) ||
        ::read(fd, &m_reduce_running, sizeof(m_reduce_running)) != sizeof(m_reduce_running) ||
        ::read(fd, &m_map_task_slots, sizeof(m_map_task_slots)) != sizeof(m_map_task_slots) ||
        ::read(fd, &m_reduce_task_slots, sizeof(m_reduce_task_slots)) != sizeof(m_reduce_task_slots) ||
        ::read(fd, &m_task_completed, sizeof(m_task_completed)) != sizeof(m_task_completed) )
    {
        return -1;
    }

    return 0;
}

