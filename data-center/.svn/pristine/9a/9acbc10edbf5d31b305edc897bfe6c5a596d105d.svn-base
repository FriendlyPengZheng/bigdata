#ifndef STAT_HEARTBEAT_TT_HPP
#define STAT_HEARTBEAT_TT_HPP

#include "stat_heartbeat.hpp"

class StatHeartbeatTT : public StatHeartbeat
{
public:
    StatHeartbeatTT() : m_maps_running(0),
                        m_reduce_running(0),
                        m_map_task_slots(0),
                        m_reduce_task_slots(0),
                        m_task_completed(0)
    {
    }

    ~StatHeartbeatTT()
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
    int32_t m_maps_running;
    int32_t m_reduce_running;
    int32_t m_map_task_slots;
    int32_t m_reduce_task_slots;
    int32_t m_task_completed;
};
#endif
