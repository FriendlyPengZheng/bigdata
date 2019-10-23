/*=========================================================================       
* 
*        Filename: stat_traffic_monitor.cpp 
* 
*        Author: seasondi
*        Created:  2014-03-04 11:30:39 
*        Modified by Ian Guo<ianguo@taomee.com>
* 
*        Description:  流量监控
* 
* ========================================================================= 
*/ 
#include <fcntl.h>
#include <arpa/inet.h>
#include <net/if.h>
#include <sys/types.h>
#include <sys/socket.h>
#include<sys/ioctl.h>

#include <cstring>
#include <cstdlib>
#include "stat_traffic_monitor.hpp"
#include "fs_utils.hpp"
#include "stat_config.hpp"
#include "stat_common.hpp"

std::ostream& operator << (std::ostream& os,const TrafficItem& item){
    if(item.m_traffic_value <= 0)   return os;
    os << "_hip_=" << item.m_ip << "\t_stid_=" << "_" << item.m_module << "流量监控" << item.m_stid_suffix << "_" 
       << "\t_sstid_=" << item.m_ip << "\t_gid_=" << StatTrafficLog::sc_game_id 
       << "\t_zid_=-1\t_sid_=-1\t_pid_=-1\t_ts_=" << item.m_data_time
       << "\t_acid_=-1\t_plid_=-1\ttraffic_value=" << item.m_traffic_value 
       << "\t_op_=sum:traffic_value|max:traffic_value\n";

    return os;
}

const string StatTrafficLog::sc_traffic_log_path = "/opt/taomee/stat/data/inbox/";

int StatTrafficLog::init(){
    StatCommon::stat_config_get("module-name",m_module_name);

    get_local_ip();

    if(m_module_name.empty()){
        ERROR_LOG("get module-name failed.");
        return -1;
    }
    
    if(m_bind_ip.empty())
    {
        DEBUG_LOG("cannot get local ip, use \"unknown\" instead");
        m_bind_ip = "unknown";
    }

    if(StatCommon::makedir(sc_traffic_log_path) == false){
        ERROR_LOG("create path failed: %s",sc_traffic_log_path.c_str());
        return -1;
    }

    return 0;
}

int StatTrafficLog::open_log_file(){
    std::stringstream ss;
    ss << sc_traffic_log_path << sc_game_id << "_game_basic_" << time(NULL);
    int fd = open(ss.str().c_str(), O_CREAT|O_RDWR|O_APPEND, S_IRWXU | S_IRWXG | S_IRWXO);

    return fd;
}

int StatTrafficLog::get_local_ip(){
    int sock_get_ip;
    struct sockaddr_in *sin;
    struct ifreq    ifr_ip;

    if((sock_get_ip = socket(AF_INET,SOCK_STREAM,0)) == -1){
        ERROR_LOG("get local ip err");
        return -1;
    }

    const char* if_list[] = {"eth0", "eth1", "eth2", "eth3"};

    for(unsigned i = 0; i < sizeof(if_list)/sizeof(const char*); ++i)
    {
        memset(&ifr_ip,0,sizeof(ifr_ip));
        strncpy(ifr_ip.ifr_name, if_list[i], sizeof(ifr_ip.ifr_name)-1);
        if(ioctl(sock_get_ip, SIOCGIFADDR, &ifr_ip) < 0)
        {
            continue;
        }

        sin = (struct sockaddr_in *)&ifr_ip.ifr_addr;

        string ip = inet_ntoa(sin->sin_addr);

        if(i == 0) // 将eth0作为默认ip。
        {
            DEBUG_LOG("eth0 ip: %s", ip.c_str());
            m_bind_ip = ip;
        }

        if(ip.find("192.") != string::npos || ip.find("10.") != string::npos) // 如有内网地址，则使用内网地址
        {
            DEBUG_LOG("local ip: %s", ip.c_str());
            m_bind_ip = ip;
            break;
        }
    }

    return 0;
}

int StatTrafficLog::process_log_traffic(const string& stid_suffix, string& buf){
    TrafficItem traffic_item(m_module_name, m_bind_ip, m_traffic_value,time(NULL), stid_suffix);
    std::ostringstream oss;
    oss << traffic_item;
    buf += oss.str();

    return 0;
}

int StatTrafficLog::process_log_traffic(const string& stid_suffix){
    TrafficItem traffic_item(m_module_name, m_bind_ip, m_traffic_value, time(NULL), stid_suffix);

    int fd = open_log_file();
    if(fd == -1){
        return -1;
    }

    std::ostringstream oss;
    oss << traffic_item;
    if(write(fd,oss.str().c_str(),oss.str().size()) == -1){
        close(fd);
        return -1;
    }

    close(fd);
    return 0;
}

