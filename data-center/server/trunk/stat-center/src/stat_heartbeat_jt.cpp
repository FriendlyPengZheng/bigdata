#include <cstring>

#include <stat_common.hpp>
#include <fs_utils.hpp>
#include "stat_heartbeat_jt.hpp"

int StatHeartbeatJT::_parse_hb_pkg(const void* hb_pkg)
{
    if (NULL == hb_pkg) 
        return -1;

    const JobTrackerHeartbeatInfoHeader* header = static_cast<const JobTrackerHeartbeatInfoHeader*>(hb_pkg);

    m_active_task_trackers = header->active_task_trackers;
    m_black_listed_task_trackers = header->black_listed_task_trackers;
    m_running_map_tasks = header->running_map_tasks;
    m_max_map_tasks = header->max_map_tasks;
    m_running_reduce_tasks = header->running_reduce_tasks;
    m_max_reduce_tasks = header->max_reduce_tasks;
    m_failed = header->failed;
    m_killed = header->killed;
    m_prep = header->prep;
    m_running = header->running;
    m_failed_job_id.clear();
    if ((uint32_t)header->len > sizeof(JobTrackerHeartbeatInfoHeader))
    {
        m_failed_job_id = std::string(header->failed_job_id,((uint32_t)header->len - sizeof(JobTrackerHeartbeatInfoHeader)));
    }

    return 0;
}

bool StatHeartbeatJT::_alarm(time_t now, StatAlarmMsg& alarm_msg) const
{
    bool ret = false;
    std::ostringstream oss;
    
    if (!m_failed_job_id.empty()) //最近执行失败的任务id，不为空需要报警
    {
        oss << "task fail " << m_failed_job_id << std::endl;
        m_failed_job_id.clear();
        ret = true;
    }

    if (ret && !(get_fbd_onoff())) 
    {
        alarm_msg += oss.str();
        if (alarm_msg.get_alarm_lv() > 1)
            alarm_msg.set_alarm_lv(1);
    }

    return ret;
}

void StatHeartbeatJT::_print_hb_data_html(std::ostringstream& oss, bool& alarm) const
{   
}

void StatHeartbeatJT::_print_hb_data_web_html(char buf[], uint32_t& buf_len, bool& alarm) const
{
    if (!m_failed_job_id.empty())//需要告警的字体设置成红色 
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

    memcpy(buf+buf_len, &m_active_task_trackers, sizeof(m_active_task_trackers));
    buf_len += sizeof(m_active_task_trackers);

    memcpy(buf+buf_len, &m_black_listed_task_trackers, sizeof(m_black_listed_task_trackers));
    buf_len += sizeof(m_black_listed_task_trackers);

    memcpy(buf+buf_len, &m_running_map_tasks, sizeof(m_running_map_tasks));
    buf_len += sizeof(m_running_map_tasks);

    memcpy(buf+buf_len, &m_max_map_tasks, sizeof(m_max_map_tasks));
    buf_len += sizeof(m_max_map_tasks);

    memcpy(buf+buf_len, &m_running_reduce_tasks, sizeof(m_running_reduce_tasks));
    buf_len += sizeof(m_running_reduce_tasks);

    memcpy(buf+buf_len, &m_max_reduce_tasks, sizeof(m_max_reduce_tasks));
    buf_len += sizeof(m_max_reduce_tasks);

    memcpy(buf+buf_len, &m_failed, sizeof(m_failed));
    buf_len += sizeof(m_failed);

    memcpy(buf+buf_len, &m_killed, sizeof(m_killed));
    buf_len += sizeof(m_killed);

    memcpy(buf+buf_len, &m_prep, sizeof(m_prep));
    buf_len += sizeof(m_prep);

    memcpy(buf+buf_len, &m_running, sizeof(m_running));
    buf_len += sizeof(m_running);

    int32_t len = (int32_t)m_failed_job_id.length();
    memcpy(buf+buf_len, &len, sizeof(len));
    buf_len += sizeof(len);

    if (!m_failed_job_id.empty()) 
    {
        memcpy(buf+buf_len, m_failed_job_id.c_str(), m_failed_job_id.length());
        buf_len += m_failed_job_id.length();
    }
}


void StatHeartbeatJT::_print_hb_data_txt(std::ostringstream& oss, bool& alarm) const
{
    string color_begin, color_end;
    if (!m_failed_job_id.empty())//需要告警的字体设置成红色 
    {
        color_begin += '\x1B';
        color_begin += "[0;31m";

        color_end += '\x1B';
        color_end += "[0m";

        alarm = true;
    }

    oss << "Active task trackers number:          " << m_active_task_trackers << std::endl
        << "Black listed task trackers number:    " << m_black_listed_task_trackers << std::endl
        << "Running map tasks number:             " << m_running_map_tasks << std::endl
        << "Max map tasks number:                 " << m_max_map_tasks << std::endl
        << "Running reduce tasks number:          " << m_running_reduce_tasks << std::endl
        << "Max reduce tasks number:              " << m_max_reduce_tasks << std::endl
        << "Failed:                               " << m_failed << std::endl
        << "Killed:                               " << m_killed << std::endl
        << "Prep:                                 " << m_prep << std::endl
        << "Running:                              " << m_running;

    if (!m_failed_job_id.empty()) {
        oss << color_begin;
        oss << std::endl 
        << "Last task is failed, job id:          " << m_failed_job_id;
        oss << color_end;
    }
}

int StatHeartbeatJT::_backup(int fd) const
{
    int size = m_failed_job_id.size();

    if (::write(fd, &m_active_task_trackers, sizeof(m_active_task_trackers)) != sizeof(m_active_task_trackers) ||
        ::write(fd, &m_black_listed_task_trackers, sizeof(m_black_listed_task_trackers)) != sizeof(m_black_listed_task_trackers) ||
        ::write(fd, &m_running_map_tasks, sizeof(m_running_map_tasks)) != sizeof(m_running_map_tasks) ||
        ::write(fd, &m_max_map_tasks, sizeof(m_max_map_tasks)) != sizeof(m_max_map_tasks) ||
        ::write(fd, &m_running_reduce_tasks, sizeof(m_running_reduce_tasks)) != sizeof(m_running_reduce_tasks) ||
        ::write(fd, &m_max_reduce_tasks, sizeof(m_max_reduce_tasks)) != sizeof(m_max_reduce_tasks) ||
        ::write(fd, &m_failed, sizeof(m_failed)) != sizeof(m_failed) ||
        ::write(fd, &m_killed, sizeof(m_killed)) != sizeof(m_killed) ||
        ::write(fd, &m_prep, sizeof(m_prep)) != sizeof(m_prep) ||
        ::write(fd, &m_running, sizeof(m_running)) != sizeof(m_running) ||
        ::write(fd, &size, sizeof(size)) != sizeof(size) ||
        (size > 0 && ::write(fd, m_failed_job_id.c_str(), size) != size))
    {
        return -1;
    }
    
    return 0;
}

int StatHeartbeatJT::_restore(int fd)
{
    int ret = 0, size = 0;
    if (::read(fd, &m_active_task_trackers, sizeof(m_active_task_trackers)) != sizeof(m_active_task_trackers) ||
        ::read(fd, &m_black_listed_task_trackers, sizeof(m_black_listed_task_trackers)) != sizeof(m_black_listed_task_trackers) ||
        ::read(fd, &m_running_map_tasks, sizeof(m_running_map_tasks)) != sizeof(m_running_map_tasks) ||
        ::read(fd, &m_max_map_tasks, sizeof(m_max_map_tasks)) != sizeof(m_max_map_tasks) ||
        ::read(fd, &m_running_reduce_tasks, sizeof(m_running_reduce_tasks)) != sizeof(m_running_reduce_tasks) ||
        ::read(fd, &m_max_reduce_tasks, sizeof(m_max_reduce_tasks)) != sizeof(m_max_reduce_tasks) ||
        ::read(fd, &m_failed, sizeof(m_failed)) != sizeof(m_failed) ||
        ::read(fd, &m_killed, sizeof(m_killed)) != sizeof(m_killed) ||
        ::read(fd, &m_prep, sizeof(m_prep)) != sizeof(m_prep) ||
        ::read(fd, &m_running, sizeof(m_running)) != sizeof(m_running) ||
        ::read(fd, &size, sizeof(size)) != sizeof(size) )
    {
        return -1;
    }
    if (size > 0)
    {
        char *buffer = (char *)malloc(size+1);
        if (NULL == buffer)
            return -1;
        do
        {
            memset(buffer, 0, size+1);
            if (::read(fd, buffer, size) == size)
                m_failed_job_id = std::string(buffer, size); 
            else
                ret = -1;
        }while(false);
        free(buffer);
    }
    return ret; 
}

