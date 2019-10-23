#ifndef STAT_HEARTBEAT_DN_HPP
#define STAT_HEARTBEAT_DN_HPP

#include<string>

#include "stat_heartbeat.hpp"

using std::string;

//DatanodeHeartbeatInfoHeader处理类
class StatHeartbeatDN : public StatHeartbeat
{
public:
    StatHeartbeatDN();

    ~StatHeartbeatDN()
    {
    }

private:
    virtual int _parse_hb_pkg(const void* hb_pkg);
    virtual bool _alarm(time_t now, StatAlarmMsg& alarm_msg) const;

    virtual void _print_hb_data_txt(std::ostringstream& oss, bool& alarm) const;
    virtual void _print_hb_data_html(std::ostringstream& oss, bool& alarm) const;

    // added by tomli --->
    virtual void _print_hb_data_web_html(char buf[], uint32_t& buf_len, bool& alarm) const;
    // <--- addedby tomli

    virtual int _backup(int fd) const;
    virtual int _restore(int fd);

private:
    int64_t m_present_capacity;    //可用磁盘空间
    int64_t m_dfs_remaining;       //剩余磁盘空间
    int64_t m_dfs_used;            //已用磁盘空间
    float   m_dfs_used_percent;    //已用磁盘空间占比(0.0-100.0)，大于一定比值需要报警
    string  m_storage_info;         //挂载目录

    float   m_max_dfs_alarm_used_percent;
    float   m_max_dfs_alarm_percent_high;
};
#endif
