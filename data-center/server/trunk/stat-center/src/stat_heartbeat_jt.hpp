#ifndef STAT_HEARTBEAT_JT_HPP
#define STAT_HEARTBEAT_JT_HPP

#include <string>

#include "stat_heartbeat.hpp"

using std::string;

class StatHeartbeatJT : public StatHeartbeat
{
public:
    StatHeartbeatJT() : m_active_task_trackers(0),
                        m_black_listed_task_trackers(0),
                        m_running_map_tasks(0),
                        m_max_map_tasks(0),
                        m_running_reduce_tasks(0),
                        m_max_reduce_tasks(0),
                        m_failed(0),
                        m_killed(0),
                        m_prep(0),
                        m_running(0),
                        m_failed_job_id("")
    {
    }
    virtual ~StatHeartbeatJT()
    {
    }

private:
    virtual int _parse_hb_pkg(const void* hb_pkg);
    virtual bool _alarm(time_t now, StatAlarmMsg& alarm_msg) const;

    virtual void _print_hb_data_txt(std::ostringstream& oss, bool& alarm) const;
    virtual void _print_hb_data_html(std::ostringstream& oss, bool& alarm) const;

    // added by tomli --->
    virtual void _print_hb_data_web_html(char buf[], uint32_t& buf_len, bool& alarm) const;
    // <--- added by tomli

    virtual int _backup(int fd) const;
    virtual int _restore(int fd);

private:
    int32_t m_active_task_trackers;
    int32_t m_black_listed_task_trackers;
    int32_t m_running_map_tasks;
    int32_t m_max_map_tasks;
    int32_t m_running_reduce_tasks;
    int32_t m_max_reduce_tasks;
    int32_t m_failed;
    int32_t m_killed;
    int32_t m_prep;
    int32_t m_running;
    mutable string  m_failed_job_id;  ///< 最近执行失败的任务id，不为空需要报警
};

#endif
