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

#include <climits>
#include <iomanip>
#include <sstream>

#include <set>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>

#include <stat_common.hpp>
#include <stat_config.hpp>
#include "string_utils.hpp"
#include <os_utils.hpp>

#include "stat_module_status.hpp"
#include "stat_heartbeat_hd.hpp"
#include "stat_heartbeat_nor.hpp"
#include "stat_heartbeat_factory.hpp"
#include "proto/alarm_request.pb.h"
#include "../../stat-alarmer/src/stat_alarmer_defines.hpp"
#include "stat_alarm_conf.hpp"

using std::ostringstream;
using std::set;
using std::pair;

StatModuleStatus::StatModuleStatus(AlarmConf& alarm_conf) : m_pkg_buff_len(1024), m_alarm_conf(alarm_conf)
{
    m_map_iterator = m_smi_map.end();
}

StatModuleStatus::~StatModuleStatus()
{
    if (NULL != m_pkg_buff)
        free(m_pkg_buff);
    clear();
}

void StatModuleStatus::init()
{
    string alarmer_ip, alarmer_port, alarmer_contact;
    StatCommon::stat_config_get("alarmer-ip", alarmer_ip);
    StatCommon::stat_config_get("alarmer-port", alarmer_port);
    StatCommon::stat_config_get("alarmer-contact", alarmer_contact);
    int alarm_mobile = StatCommon::stat_config_get("mobile-alarm", 0);
    if (alarm_mobile == 1)
        m_is_alarm_mobile = true;

    if (alarmer_ip.empty() || alarmer_port.empty() || alarmer_contact.empty())
    {
        ERROR_LOG("StatModuleStatus::init failed");
        return;
    }
    DEBUG_LOG("alarmer_ip=%s, alarmer_port=%s, contact=%s, mobile-alarm=%d", alarmer_ip.c_str(), alarmer_port.c_str(), alarmer_contact.c_str(), alarm_mobile);

    m_alarmer_ip = alarmer_ip;
    m_alarmer_port = alarmer_port;
    
    vector<string> items;
    items.clear();
    StatCommon::split(alarmer_contact, '|', items);
    for(uint32_t i=0; i<items.size(); ++i)
    {
        vector<string> contect_item;
        contect_item.clear();
        StatCommon::split(items[i], ':', contect_item);
        if (contect_item.size() == 3)
            m_contact.push_back(contect_item);
        else
            ERROR_LOG("Bad Format Contact Of %d", i+1);
    }
    if (NULL == m_pkg_buff)
    {
        m_pkg_buff = (char *)malloc(m_pkg_buff_len);
        if (NULL == m_pkg_buff)
            ERROR_LOG("StatModuleStatus::init m_pkg_buff malloc failed!");
    }

    string foreign_ip;
    StatCommon::stat_config_get("foreign-ip", foreign_ip);
    items.clear();
    StatCommon::split(foreign_ip, '|', items);

    for(uint32_t i=0; i<items.size(); ++i)
    {
        vector<string> foreign_ip_item;
        foreign_ip_item.clear();
        StatCommon::split(items[i], ':', foreign_ip_item);

        if (foreign_ip_item.size() == 3)
        {
            char ip[20] = {0};
            int  interval = 0, tmp_interval = 0;
            struct in_addr addr_min, addr_max;
            map<uint32_t, uint32_t> ip_map;
        
            vector<string>::iterator iter = foreign_ip_item.begin();
            memcpy(ip, (*iter).c_str(), sizeof(ip));

            inet_pton(AF_INET, ip, (void *)&addr_min);
            memset(ip, 0, sizeof(ip));
            iter++;

            memcpy(ip, (*iter).c_str(), sizeof(ip));
            inet_pton(AF_INET, ip, (void *)&addr_max);
            memset(ip, 0, sizeof(ip));
            iter++;

            ip_map.insert(pair<uint32_t, uint32_t>(htonl(addr_min.s_addr), htonl(addr_max.s_addr)));

            StatCommon::strtodigit((*iter), tmp_interval);
            tmp_interval *= 60;
            interval = std::min(tmp_interval, 3600); // 最大1小时
            interval = std::max(tmp_interval, 60 * 30);  // 最小30分钟
            interval = tmp_interval;

            m_foreign_ip.insert(pair< map<uint32_t, uint32_t>, int>(ip_map, interval));
        }
        else
            ERROR_LOG("Bad Format foreign_ip Of %d", i+1);
    }

    time_t now;
    time(&now);

    int count = m_foreign_ip.size();
    for (int i=0; i<count; i++)
         m_lstch_fgn_time.push_back(now);
}

void StatModuleStatus::clear()
{
    SmiMap::iterator mit;

    for(mit = m_smi_map.begin(); mit != m_smi_map.end(); ++mit)
    {
        delete mit->second;
        mit->second = NULL;
    }

    m_smi_map.clear();
}

bool StatModuleStatus::exists(const StatModuleInfo& smi)
{
    SmiMap::iterator it = m_smi_map.find(smi);
    if(it != m_smi_map.end())
        return true;

    return false;
}

int StatModuleStatus::add_module_status(StatModuleInfo& smi, StatHeartbeat* shb)
{
    if(smi.is_valid() == false || shb == NULL)
        return -1;

    if(m_smi_map.size() > s_max_modules) // 达到最大值，先清空，所有服务模块重新来注册。
    {
        ERROR_LOG("modules buffer is full, current size %u", s_max_modules + 1);
        clear();
    }

    std::pair<SmiMap::iterator,bool> ret = m_smi_map.insert(std::make_pair(smi, shb));
    if(ret.second == false)
    {
        return -1;
    }
    shb->set_module_info(&(ret.first->first));

    DEBUG_LOG("%s %s registered.", smi.get_module_name().c_str(), smi.get_ip_str().c_str());

    return 0;
}

int StatModuleStatus::delete_module_status(const StatModuleInfo& smi)
{
    SmiMap::iterator it = m_smi_map.find(smi);
    if(it != m_smi_map.end())
    {
        delete it->second;
        it->second = NULL;

        SmiMap::size_type erase_size = m_smi_map.erase(smi);

        if(erase_size == 0)
            return -1;
        else 
        {
            DEBUG_LOG("%s %s deleted.", smi.get_module_name().c_str(), smi.get_ip_str().c_str());
            return 0;
        }
    }

    return -1;
}

int StatModuleStatus::update_module_port(const StatModuleInfo& smi)
{
    SmiMap::iterator it = m_smi_map.find(smi);
    if(it != m_smi_map.end())
    {
        it->first.set_port(smi.get_port());
        return 0;
    }

    return -1;
}

int StatModuleStatus::update_heartbeat_data(const StatModuleInfo& smi, const void* pkg_buf)
{
    SmiMap::iterator it = m_smi_map.find(smi);
    if(it != m_smi_map.end())
    {
        if(it->second)
        {
            return it->second->parse_heartbeat_pkg(pkg_buf);
        }
    }

    return -1;
}

void StatModuleStatus::clean_dead_modules(time_t now)
{
    SmiMap::iterator it = m_smi_map.begin();

    while(it != m_smi_map.end())
    {
        if(it->second && (now - it->second->get_heartbeat_time() > 15 * 24 * 3600)) // 15天都没有心跳的需要删除
        {
            DEBUG_LOG("%s %s deleted.", it->first.get_module_name().c_str(), it->first.get_ip_str().c_str());

            delete it->second;
            it->second = NULL;
            m_smi_map.erase(it++);
        }
        else
            ++it;
    }
}

void StatModuleStatus::send_alarm(const StatAlarmMsg& msg)
{
    StatAlarmerProto::StatAlarmRequest request;

    request.set_title("Stat Alarm");
    string full_msg = msg.get_full_msg();
    request.set_content(full_msg);
    ostringstream oss;
    oss << "\n---------------Alarm Message---------------------\n"
        << full_msg
        << "send to : ";

    INFO_LOG("full_msg:  %s", full_msg.c_str());

    for(uint32_t i=0; i<m_contact.size(); ++i) 
    {
        StatAlarmerProto::AlarmContact *contact = request.add_send_to(); 
        contact->set_name(m_contact[i][0]);     //昵称 
        oss << m_contact[i][0] << " ";
        contact->set_email(m_contact[i][1]);    //邮箱
        contact->set_mobile(m_contact[i][2]);   //手机号
    }
    uint32_t pkg_len = sizeof(StatProtoHeader) + request.ByteSize();
    if (pkg_len > m_pkg_buff_len - sizeof(StatProtoHeader))
    {
        char * new_buff = (char*)calloc(pkg_len/m_pkg_buff_len + 1, m_pkg_buff_len);
        if (NULL == new_buff)
        {
            ERROR_LOG("calloc failed!");
            return;
        }
        m_pkg_buff_len = pkg_len / m_pkg_buff_len + 1;
        if (NULL != m_pkg_buff)
            free(m_pkg_buff);
        m_pkg_buff = new_buff;
    }
    if (NULL == m_pkg_buff)
    {
        ERROR_LOG("m_pkg_buff is NULL");
        return;
    }
    memset(m_pkg_buff, 0, m_pkg_buff_len);
    StatProtoHeader *req_pkg = (StatProtoHeader *)m_pkg_buff;
    req_pkg->len = pkg_len;
    request.SerializeToArray(req_pkg->body, request.ByteSize());
    
    oss << "\nSend RTX";
    req_pkg->proto_id = STAT_ALARMER_PROTO_RTX;  // RTX 告警
    if (!send_and_recv(m_pkg_buff, pkg_len))
    {
        ERROR_LOG("Send Rtx alarm request faild !");
        oss << " one or more failed";
    }

    oss << "\nSend e-mail";
    req_pkg->proto_id = STAT_ALARMER_PROTO_EMAIL; // Mail 告警
    if (!send_and_recv(m_pkg_buff, pkg_len))
    {
        ERROR_LOG("Send Mail alarm request faild !");
        oss << " one or more failed";
    }

    time_t now;
    time(&now);
    if (m_alarm_conf.alarm_or_not(now) == AlarmConf::ALARM_WEIXIN)
    {
        oss << "\nSend Weixin";
        req_pkg->proto_id = STAT_ALARMER_PROTO_WEIXIN; // Weixin 告警
        if (!send_and_recv(m_pkg_buff, pkg_len))
        {
            ERROR_LOG("Send Weixin alarm request faild !");
            oss << " one or more failed";
        }
    }
    else if (m_alarm_conf.alarm_or_not(now) == AlarmConf::ALARM_MOBILE)
    {
        oss << "\nSend mobile";
        
        req_pkg->proto_id = STAT_ALARMER_PROTO_MOBILE; // mobile 告警
        if (!send_and_recv(m_pkg_buff, pkg_len))
        {
            ERROR_LOG("Send mobile alarm request faild !");
            oss << " one or more failed";
        }
    }
    // else if (m_alarm_conf.alarm_or_not(now) == AlarmConf::ALARM_NORMAL)
    // {
    //     if (!m_is_alarm_mobile || (m_is_alarm_mobile &&  msg.get_alarm_lv() == 1))
    //     {
    //         oss << "\nSend App";
    //         req_pkg->proto_id = STAT_ALARMER_PROTO_APPPUSH; // App 告警
    //         if (!send_and_recv(m_pkg_buff, pkg_len))
    //         {
    //             ERROR_LOG("Send App alarm request faild !");
    //             oss << " one or more failed";
    //         }
    //     }

    //     if (m_is_alarm_mobile && msg.get_alarm_lv() == 0)
    //         oss << "\nSend SMS(together with other alarm msg)";
    // }

    oss << "\n-------------------------------------------------";
    INFO_LOG("%s",oss.str().c_str());
}

void StatModuleStatus::send_sms_alarm(const string& msg) 
{
    StatAlarmerProto::StatAlarmRequest request;
    request.set_title("StatAlarmSms");
    request.set_content(msg);
    ostringstream oss; 
    oss << "Send Sms Alarm Message ";

    for(uint32_t i=0; i<m_contact.size(); ++i) 
    {    
        StatAlarmerProto::AlarmContact *contact = request.add_send_to();
        contact->set_name(m_contact[i][0]);     //nick name
        oss << m_contact[i][0] << "["; 
        contact->set_email(m_contact[i][1]);    //email address
        contact->set_mobile(m_contact[i][2]);   //cellphone number
        oss << m_contact[i][2] << "] ";
    }    
    uint32_t pkg_len = sizeof(StatProtoHeader) + request.ByteSize();

    if (pkg_len > m_pkg_buff_len - sizeof(StatProtoHeader))
    {    
        char * new_buff = (char*)calloc(pkg_len/m_pkg_buff_len + 1, m_pkg_buff_len);
        if (NULL == new_buff)
        {    
            ERROR_LOG("calloc failed!");
            return;
        }    
        m_pkg_buff_len = pkg_len / m_pkg_buff_len + 1; 
        if (NULL != m_pkg_buff)
            free(m_pkg_buff);
        m_pkg_buff = new_buff;
    }    
    memset(m_pkg_buff, 0, m_pkg_buff_len);
    StatProtoHeader *req_pkg = (StatProtoHeader *)m_pkg_buff;
    req_pkg->len = pkg_len;
    request.SerializeToArray(req_pkg->body, request.ByteSize());

    req_pkg->proto_id = STAT_ALARMER_PROTO_MOBILE;  // SMS 告警
    if (!send_and_recv(m_pkg_buff, pkg_len))
    {    
        ERROR_LOG("Send sms alarm request faild !");
        oss << " one or more faild";
    }    
    INFO_LOG("%s",oss.str().c_str());
}

bool StatModuleStatus::send_and_recv(char * pkg_buff, uint32_t size)
{
    do {
        TcpClient tc;
        int fd = tc.connect(m_alarmer_ip, m_alarmer_port);
        if (fd < 0)
            break;;
        tc.set_timeout(20);

        int ret = tc.send(pkg_buff, size);
        if (ret < (int)size)
            break;

        char buff[1024] = {0};
        ret = tc.recv(buff, sizeof(StatProtoHeader));
        if (ret < (int)sizeof(StatProtoHeader))
            break;

        StatProtoHeader * rep_header = (StatProtoHeader*)buff;
        char * rep_pkg = (char *)rep_header->body;
        if (rep_header->len > sizeof(buff))
        {
            ERROR_LOG("Alarmer's response is to large, > 1KB !!");
            break;
        }

        ret = tc.recv(rep_pkg, rep_header->len - sizeof(StatProtoHeader));
        if (ret < (int)(rep_header->len - sizeof(StatProtoHeader)))
            break;

        StatAlarmerProto::StatAlarmResponse req;
        req.ParseFromArray(rep_pkg, rep_header->len - sizeof(StatProtoHeader));
        if (req.ret() == 0)
            return true;
    } while(false); 
    
    return false;
}

void StatModuleStatus::check_alarm(time_t now)
{
    SmiMap::iterator it;
    // string mobile_alarm_msg;
    
    for(it = m_smi_map.begin(); it != m_smi_map.end(); ++it)
    {
        if(it->second)
        {
            if (check_foreign_alarm(htonl(it->first.get_ip())))
            { 
                StatAlarmMsg alarm_msg;
                if(it->second->alarm(now, alarm_msg))
                {
                    alarm_msg.set_msg_header(it->first.get_module_name(), it->first.get_ip_str());
                    send_alarm(alarm_msg);

                    // if (m_alarm_conf.alarm_or_not(now) == AlarmConf::ALARM_MOBILE)
                    // {
                    //     DEBUG_LOG("------ AlarmConf::ALARM_MOBILE");
                    //     if (m_is_alarm_mobile && alarm_msg.get_alarm_lv() == 0)
                    //     {
                    //         DEBUG_LOG("------ in  AlarmConf::ALARM_MOBILE");
                    //         // 短信告警内容需要合并，以减少短信条数。
                    //         if (mobile_alarm_msg.empty())
                    //             mobile_alarm_msg = "StatAlarm:";

                    //         string compact_msg = alarm_msg.get_compact_msg();
                    //         if (mobile_alarm_msg.size() > 10 && (mobile_alarm_msg.size() + compact_msg.size() > 60))
                    //         {
                    //             send_sms_alarm(mobile_alarm_msg);
                    //             mobile_alarm_msg = "StatAlarm:" + compact_msg;
                    //         }
                    //         else 
                    //             mobile_alarm_msg += compact_msg;
                    //     }
                    // }
                }
            }
        }
    }

    // if(!mobile_alarm_msg.empty())
    // {
    //     DEBUG_LOG("-------------- send mobile alarm");
    //     if (m_alarm_conf.alarm_or_not(now) == AlarmConf::ALARM_MOBILE)
    //         send_sms_alarm(mobile_alarm_msg);
    // }
}

void StatModuleStatus::start()
{
    m_map_iterator = m_smi_map.begin();
}

bool StatModuleStatus::has_next(StatModuleInfo& smi)
{
    if(m_map_iterator != m_smi_map.end())
    {
        smi = m_map_iterator->first;
        ++m_map_iterator;
        return true;
    }

    return false;
}

void StatModuleStatus::dump_to_web_html(StatModuleType module_type, uint8_t print_flags, const uint32_t param, char buf[], uint32_t& buf_len)
{
    buf_len += 6;

    SmiMap::iterator it;
    uint16_t module_count = 0;
    uint16_t alarm_count = 0;

    string module_name = StatModuleInfo::parse_name_from_type(module_type);
    if(module_name.empty())
        return;

    for(it = m_smi_map.begin(); it != m_smi_map.end(); ++it)
    {
        if(it->first.get_module_type() != module_type)
            continue;

        if ((print_flags&0x40) != 0 && it->first.get_ip() != param) // print_flag的第2位IP_flag
            continue;

        ++module_count;

        uint32_t len_temp = buf_len;

        it->first.print_web_info(0, buf, buf_len);

        bool alarm = false;

        if(it->second)
        {
            it->second->print_heartbeat_web_data(0, buf, buf_len, alarm);
        }

        len_temp = buf_len - len_temp;

        if ((print_flags&0x80) != 0 && !alarm) // print_flag的第1位Alarm_flag
        {
            buf_len -= len_temp; 
            memset(buf + buf_len, 0, len_temp);

            continue;
        }

        if(alarm)
            ++alarm_count;

    }

    memcpy(buf, &module_count, 2);

    memcpy(buf+2, &alarm_count, 2);

    if (print_flags&0x80)
    {
        memcpy(buf+4, &alarm_count, 2);
    }
    else if (print_flags&0x40)
    {
        uint16_t num = 1;
        memcpy(buf+4, &num, 2);
    }
    else
    {
        memcpy(buf+4, &module_count, 2);
    }
}

void StatModuleStatus::dump_to_html(StatModuleType module_type, uint8_t print_flag, const uint32_t param_ip, std::ostringstream& oss)
{
}

void StatModuleStatus::dump_to_txt(StatModuleType module_type, uint8_t print_flag, const uint32_t param_ip, std::ostringstream& oss)
{
    SmiMap::iterator it;
    uint16_t module_count = 0;
    uint16_t alarm_count = 0;
    uint16_t print_count = 0; //限制在50个以内
    string module_name = StatModuleInfo::parse_name_from_type(module_type);
    if(module_name.empty())
        return;

    oss << "--------------------------------------------------------------" << std::endl;
    for(it = m_smi_map.begin(); it != m_smi_map.end(); ++it)
    {
        if(it->first.get_module_type() != module_type)
            continue;

        if ((print_flag&0x40) != 0 && it->first.get_ip() != param_ip) // print_flag的第2位IP_flag
            continue;

        ++module_count;

        bool alarm = false;
        std::ostringstream oss_check;
        oss_check.str("");
        if(it->second)
        {
            it->second->print_heartbeat_data(0, oss_check, alarm);
        }

        if ((print_flag&0x80) != 0 && !alarm) // print_flag的第1位Alarm_flag
            continue;

        if(alarm)
            ++alarm_count;

        if (print_count < 50)
        {
            it->first.print_info(0, oss);
            oss << oss_check.str();
            oss << std::endl;
            ++print_count;
        }
    }
    oss << "--------------------------------------------------------------" << std::endl;

    oss << module_name << " summary:\n\t"
        << module_count << " registered, " << alarm_count << " alarming, "<< print_count << " printed." << std::endl;
}

void StatModuleStatus::dump_to_string(StatModuleType module_type, uint8_t print_type, uint8_t print_flag, const uint32_t param_ip, string& str)
{
    std::ostringstream oss;

    if(print_type == 0)
        dump_to_txt(module_type, print_flag, param_ip, oss);
    else if(print_type == 1)
        dump_to_html(module_type, print_flag, param_ip, oss);
    else 
        return;

    str = oss.str();
}

void StatModuleStatus::dump_to_web(StatModuleType module_type, uint8_t print_type, uint8_t print_flag, const uint32_t param_ip, char buf[], uint32_t& buf_len)
{
    dump_to_web_html(module_type, print_flag, param_ip, buf, buf_len);
}

int StatModuleStatus::backup(uint8_t type)
{
    map<string, int> fname_fd_map;

    SmiMap::iterator it;
    int ret = 0;
    for(it = m_smi_map.begin(); it != m_smi_map.end(); ++it)
    {
        if(it->second)
        {
            string fname = StatHeartbeatFactory::get_backup_file_name(it->first.get_module_type());
            map<string, int>::iterator fit = fname_fd_map.find(fname);

            int fd = -1;
            if(fit != fname_fd_map.end())
            {
                fd = fit->second;
            }
            else
            {
                fd = ::open(fname.c_str(), O_CREAT | O_TRUNC | O_RDWR | O_APPEND | O_NONBLOCK, S_IRWXU);

                if(fd < 0)
                {
                    ret = -1;
                    break;
                }

                fname_fd_map.insert(std::make_pair(fname, fd));;
            }

            if(it->first.backup(fd) != 0 || it->second->backup(fd) != 0)
            {
                ret = -1;
                break;
            }
        }
    }

    for(map<string, int>::iterator fit = fname_fd_map.begin(); fit != fname_fd_map.end(); ++fit)
    {
        ::close(fit->second);
    }

    if(ret < 0 || m_smi_map.empty())
    {
        // unlink all backup files
        for(int i = STAT_CLIENT; i < STAT_MODULE_END; ++i)
            unlink(StatHeartbeatFactory::get_backup_file_name(i));
    }

    m_alarm_conf.backup();

    return ret;
}

int StatModuleStatus::restore(uint8_t type)
{
    for(int module_type = STAT_CLIENT; module_type < STAT_MODULE_END; ++module_type)
    {
        const char* const file_name = StatHeartbeatFactory::get_backup_file_name(module_type);
        int fd = ::open(file_name, O_RDONLY);
        if(fd < 0)
        {
            DEBUG_LOG("open file %s failed", file_name);
            continue;
        }

        DEBUG_LOG("restore heartbeat data from file %s", file_name);

        while(1)
        {
            StatModuleInfo smi;
            if(smi.restore(fd) != 0)
            {
                break;
            }

            DEBUG_LOG("restore heartbeat data of ip: %s", smi.get_ip_str().c_str());

            StatHeartbeat* hb = StatHeartbeatFactory::create_heartbeat(smi);

            if(hb == NULL || hb->restore(fd) != 0)
            {
                ERROR_LOG("restore StatHeartbeat failed.");
                delete hb;
                break;
            }

            std::pair<SmiMap::iterator, bool> insert_ret = m_smi_map.insert(std::make_pair(smi, hb));
            if(insert_ret.second == false)
            {
                ERROR_LOG("insert heartbeat to map failed");
                delete hb;
                break;
            }
            hb->set_module_info(&(insert_ret.first->first));
        }

        ::close(fd);
    }

    m_alarm_conf.restore();

    return 0;
}
 
void StatModuleStatus::forbid_from_web(int fd, const void* pkg_buf)
{
    const StatProtoHeader* header = static_cast<const StatProtoHeader*>(pkg_buf);
    const StatForbidHeader* forbid_header = static_cast<const StatForbidHeader*>(pkg_buf);
    statHeartbeatPrintRet print_ret = {0};
    print_ret.len = sizeof(statHeartbeatPrintRet);
    print_ret.proto_id = header->proto_id;
    string str;

    uint32_t ip = 0;
    uint32_t disable_time = 0;
    time_t start_time;
    time(&start_time);

    do
    {
        if (forbid_header->fbd_flag & 0x40) // module_type
        {
            disable_time = (uint32_t)forbid_header->minutes;
            if (forbid_header->fbd_flag & 0x80)  // on
            {
                SmiMap::iterator iter = m_smi_map.begin();
                set<StatModuleType> info_flag;
                while (iter != m_smi_map.end())
                {
                    if (iter->first.get_module_type() == StatModuleType(forbid_header->module_type))
                    {
                        iter->second->set_fbd_onoff((uint8_t)1);
                        iter->second->set_fbd_disable_starttime(start_time);
                        iter->second->set_fbd_disable_insistseconds(disable_time * 60);
                        if (info_flag.find(StatModuleType(forbid_header->module_type)) == info_flag.end())
                        {
                            INFO_LOG("forbid alarm from %s for %d minutes.", 
                                        iter->first.get_module_name().c_str(), 
                                        disable_time);
                        }
                        info_flag.insert(StatModuleType(forbid_header->module_type));
                    }
                    iter ++;
                }

            }
            else  // off
            {
                SmiMap::iterator iter = m_smi_map.begin();
                set<StatModuleType> info_flag;
                while (iter != m_smi_map.end())
                {
                    if (iter->first.get_module_type() == StatModuleType(forbid_header->module_type))
                    {
                        iter->second->set_fbd_onoff((uint8_t)0);
                        if (info_flag.find(StatModuleType(forbid_header->module_type)) == info_flag.end())
                        {
                            INFO_LOG("cancel forbidding alarm from %s.", 
                                        iter->first.get_module_name().c_str());
                        }
                        info_flag.insert(StatModuleType(forbid_header->module_type));
                    }
                    iter ++;
                }
            }
        }
        else // ip
        {
            bool flag;
            flag = false;
            disable_time = (uint32_t)forbid_header->minutes;
            ip = (uint32_t)(forbid_header->ip);
            if (forbid_header->fbd_flag & 0x80)  // on
            {
                SmiMap::iterator iter = m_smi_map.begin();
                while (iter != m_smi_map.end())
                {
                    if ((iter->first.get_ip() == ip) &&
                            (iter->first.get_module_type() == (StatModuleType)forbid_header->module_type))
                    {
                        iter->second->set_fbd_onoff((uint8_t)1);
                        iter->second->set_fbd_disable_starttime(start_time);
                        iter->second->set_fbd_disable_insistseconds(disable_time * 60);
                        flag = true;
                        struct sockaddr_in addr;
                        addr.sin_addr.s_addr = ip;
                        char fdip[16];
                        inet_ntop(AF_INET, &ip, fdip, sizeof(fdip)/sizeof(char));
                        INFO_LOG("forbid %s for %d minutes.", fdip, disable_time);
                    }
                    iter ++;
                }
                if((iter == m_smi_map.end()) && !flag)
                {
                    print_ret.ret = -1;
                    str = "please input correct ip.";
                    break;
                }
            }
            else  // off
            {
                SmiMap::iterator iter = m_smi_map.begin();
                while (iter != m_smi_map.end())
                {
                    if ((iter->first.get_ip() == ip) &&
                            (iter->first.get_module_type() == (StatModuleType)forbid_header->module_type))
                    {
                        iter->second->set_fbd_onoff((uint8_t)0);
                        flag = true;
                        struct sockaddr_in addr;
                        addr.sin_addr.s_addr = ip;
                        char fdip[16];
                        inet_ntop(AF_INET, &ip, fdip, sizeof(fdip)/sizeof(char));
                        INFO_LOG("cancel forbidding %s.", fdip);
                    }
                    iter ++;
                }
                if((iter == m_smi_map.end()) && !flag)
                {
                    print_ret.ret = -1;
                    str = "please input correct ip.";
                    break;
                }

            }
        }
        print_ret.ret = 0;
    }
    while(0);

    net_send_cli(fd, &print_ret, sizeof(print_ret));
}

void StatModuleStatus::forbid(int fd, const void* pkg_buf)
{
    const StatProtoHeader* header = static_cast<const StatProtoHeader*>(pkg_buf);
    const StatHeartbeatPrintHeader* print_header = static_cast<const StatHeartbeatPrintHeader*>(pkg_buf);
    statHeartbeatPrintRet print_ret = {0};
    print_ret.len = sizeof(statHeartbeatPrintRet);
    print_ret.proto_id = header->proto_id;
    string str;

    uint32_t offset = 0;
    uint32_t ip = 0;
    uint32_t disable_time = 0;
    time_t start_time;
    time(&start_time);

    do  
    {   
        if (print_header->print_flag & 0x40) // module_type
        {
            disable_time = *((uint32_t*)print_header->print_param);
            if (print_header->print_flag & 0x80)  // on
            {
                SmiMap::iterator iter = m_smi_map.begin();
                set<StatModuleType> info_flag;
                while (iter != m_smi_map.end())
                {
                    if (iter->first.get_module_type() == StatModuleType(print_header->module_type))
                    {
                        iter->second->set_fbd_onoff((uint8_t)1);
                        iter->second->set_fbd_disable_starttime(start_time);
                        iter->second->set_fbd_disable_insistseconds(disable_time * 60);
                        if (info_flag.find(StatModuleType(print_header->module_type)) == info_flag.end())
                        {
                            INFO_LOG("forbid alarm from %s for %d minutes.", 
                                    iter->first.get_module_name().c_str(), 
                                    disable_time);
                        }
                        info_flag.insert(StatModuleType(print_header->module_type));
                    }
                    iter ++; 
                }

            }
            else  // off
            {
                SmiMap::iterator iter = m_smi_map.begin();
                set<StatModuleType> info_flag;
                while (iter != m_smi_map.end())
                {
                    if (iter->first.get_module_type() == StatModuleType(print_header->module_type))
                    {
                        iter->second->set_fbd_onoff((uint8_t)0);
                        if (info_flag.find(StatModuleType(print_header->module_type)) == info_flag.end())
                        {
                            INFO_LOG("cancel forbidding alarm from %s.",
                                    iter->first.get_module_name().c_str());
                        }
                        info_flag.insert(StatModuleType(print_header->module_type));
                    }
                    iter ++;
                }
            }
        }
        else // ip
        {
            bool flag;
            flag = false;
            disable_time = *((uint32_t*)print_header->print_param);
            offset += sizeof(uint32_t);
            ip = *((uint32_t*)(print_header->print_param + offset));
            if (print_header->print_flag & 0x80)  // on
            {
                SmiMap::iterator iter = m_smi_map.begin();
                while (iter != m_smi_map.end())
                {
                    if ((iter->first.get_ip() == ip) &&
                            (iter->first.get_module_type() == (StatModuleType)print_header->module_type))
                    {
                        iter->second->set_fbd_onoff((uint8_t)1);
                        iter->second->set_fbd_disable_starttime(start_time);
                        iter->second->set_fbd_disable_insistseconds(disable_time * 60);
                        flag = true;
                        struct sockaddr_in addr;
                        addr.sin_addr.s_addr = ip;
                        char fdip[16];
                        inet_ntop(AF_INET, &ip, fdip, sizeof(fdip)/sizeof(char));
                        INFO_LOG("forbid %s for %d minutes.", fdip, disable_time);
                    }
                    iter ++;
                }
                if((iter == m_smi_map.end()) && !flag)
                {
                    print_ret.ret = -1;
                    str = "please input correct ip.";
                    break;
                }
            }
            else  // off
            {
                SmiMap::iterator iter = m_smi_map.begin();
                while (iter != m_smi_map.end())
                {
                    if ((iter->first.get_ip() == ip) &&
                            (iter->first.get_module_type() == (StatModuleType)print_header->module_type))
                    {
                        iter->second->set_fbd_onoff((uint8_t)0);
                        flag = true;
                        struct sockaddr_in addr;
                        addr.sin_addr.s_addr = ip;
                        char fdip[16];
                        inet_ntop(AF_INET, &ip, fdip, sizeof(fdip)/sizeof(char));
                        INFO_LOG("cancel forbidding %s.", fdip);
                    }
                    iter ++;
                }
                if((iter == m_smi_map.end()) && !flag)
                {
                    print_ret.ret = -1;
                    str = "please input correct ip.";
                    break;
                }

            }
        }
        print_ret.ret = 0;
    }
    while(0);
    if(str.empty())
        print_ret.ret = -1;
    else
    {
        print_ret.len += str.size();
    }

    net_send_cli(fd, &print_ret, sizeof(print_ret));
    if(!str.empty())
    {
        net_send_cli(fd, str.c_str(), str.size());

    }
}

void StatModuleStatus::set_holiday(int fd, const void* pkg_buf)
{
    //struct sockaddr_in m_address;
    //memset(&m_address, 0, sizeof(m_address));
    //socklen_t addr_len = sizeof(m_address);

    //int ret;
    //if (ret = getpeername(fd, (struct sockaddr*)&m_address, &addr_len) != 0)
    //{
    //    ERROR_LOG(" get ip failed, fd:%d, ret: %d, errno: %d, error: %s", fd, ret, errno, strerror(errno));
    //}

    //DEBUG_LOG("client ip: %s", inet_ntoa(m_address.sin_addr));



    const StatProtoHeader* header = static_cast<const StatProtoHeader*>(pkg_buf);
    const StatSetHolidayHeader* set_holiday_header = static_cast<const StatSetHolidayHeader*>(pkg_buf);
    StatSetHolidayRet set_holiday_ret = {0};
    set_holiday_ret.len = sizeof(StatSetHolidayRet);
    set_holiday_ret.proto_id = header->proto_id;

    //ERROR_LOG("set_holiday.len: %d", set_holiday_header->len);
    //ERROR_LOG("set_holiday.proto_id: %d", set_holiday_header->proto_id);
    //ERROR_LOG("set_holiday.num: %d", set_holiday_header->num);

    //if (set_holiday_header->len == (sizeof(StatSetHolidayHeader) + set_holiday_header->num * 8))
    //{

    uint32_t offset = 0;
    for (uint32_t i=0; i<(set_holiday_header->num); i++)
    {
        switch(*(uint8_t*)(set_holiday_header->body+offset)) 
        {
            case 1:
                offset += 1;
                m_alarm_conf.add_holiday(*(uint64_t*)(set_holiday_header->body+offset));
                offset += 8;
                break;
            case 2:
                offset += 1;
                m_alarm_conf.add_weekday(*(uint64_t*)(set_holiday_header->body+offset));
                offset+= 8;
                break;
            default:
                break;
        }
    }
    //}
    set_holiday_ret.ret = 0;

    net_send_cli(fd, &set_holiday_ret, sizeof(set_holiday_ret));
}

bool StatModuleStatus::check_foreign_alarm(const uint32_t foreign_ip)
{
    time_t now;
    time(&now);

    map< map<uint32_t, uint32_t>, int>::iterator iter = m_foreign_ip.begin();
    vector<time_t>::iterator viter = m_lstch_fgn_time.begin();

    while ((iter != m_foreign_ip.end()))
    {
        map<uint32_t, uint32_t> ip_map = iter->first;
        map<uint32_t, uint32_t>::iterator ip_iter = ip_map.begin();

        if ((foreign_ip >= ip_iter->first) && (foreign_ip <= ip_iter->second))
            break;
        iter++;
        viter++;
    }

    if (iter != m_foreign_ip.end())
    {
        if ((now - *viter) > iter->second)
        {
            *viter = now;
            return true;
        }
        else
        {
            return false;
        }
    }

    return true;
}
