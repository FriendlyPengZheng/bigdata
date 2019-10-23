#ifndef STAT_HEARTBEAT_NN_HPP
#define STAT_HEARTBEAT_NN_HPP

#include "stat_heartbeat.hpp"

//NameNodeHeartbeatInfoHeader处理类
class StatHeartbeatNN : public StatHeartbeat
{
public:
    StatHeartbeatNN();
    virtual ~StatHeartbeatNN()
    {
    }

private:
    virtual int _parse_hb_pkg(const void* hb_pkg);
    virtual bool _alarm(time_t now, StatAlarmMsg& alarm_msg) const;

    virtual void _print_hb_data_txt(std::ostringstream& oss, bool& alarm) const;
    virtual void _print_hb_data_html(std::ostringstream& oss, bool& alarm) const;

    // added by tomli --->
    virtual void _print_hb_data_web_html(char buf[], uint32_t& buf_len, bool& alarm) const;
    // <--- added byt tomli

    virtual int _backup(int fd) const;
    virtual int _restore(int fd);

private:
    int32_t m_safe_mode;  //0-OFF, 1-ON
    int64_t m_configured_capacity; //最大磁盘空间
    int64_t m_present_capacity;    //可用磁盘空间
    int64_t m_dfs_remaining;       //剩余磁盘空间
    int64_t m_dfs_used;            //已用磁盘空间
    float   m_dfs_used_percent;    //已用磁盘空间占比(0.0-100.0)，大于一定比值需要报警
    int64_t m_under_replicated_blocks;    //需要拷贝的文件块数量
    int64_t m_missing_blocks;             //丢失的文件块数量，不为0需要报警
    int32_t m_total_datanodes;            //datanode数量
    int32_t m_live_nodes;                 //存活datanode数量
    int32_t m_dead_nodes;                 //挂掉的datanode数量，不为0需要报警

    float   m_max_dfs_used_percent;
    float   m_high_alarm_lv_percent;
};
#endif
