/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-center服务模块。
 *   @author  tomli<ianguo@taomee.com>
 *   @date    2014-08-18
 * =====================================================================================
 */

#include <unistd.h>
#include <cerrno>
#include <cstring>
#include <string>
#include <ctime>
#include <set>
#include <map>

#include <sstream>

#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>

#include <stat_common.hpp>
#include <stat_config.hpp>
#include "string_utils.hpp"
#include <os_utils.hpp>

#include "stat_data_monitor.hpp"

#include "proto/alarm_request.pb.h"
#include "../../stat-alarmer/src/stat_alarmer_defines.hpp"

using std::string;
using std::ostringstream;
using std::set;
using std::map;
using std::pair;

StatDataMonitor::StatDataMonitor(uint32_t proto_id, const char* proto_name, AlarmConf& alarm_conf) : StatProtoHandler(proto_id, proto_name), m_pkg_buff_len(1024), m_send_or_not(false), m_time_begin_collect(0), m_alarm_conf(alarm_conf)
{
}

StatDataMonitor::StatDataMonitor(uint32_t proto_id, const char* proto_name, unsigned proto_count, AlarmConf& alarm_conf) : StatProtoHandler(proto_id, proto_name, proto_count), m_pkg_buff_len(1024), m_send_or_not(false), m_time_begin_collect(0), m_alarm_conf(alarm_conf) 
{
}

StatDataMonitor::~StatDataMonitor()
{
    if (NULL != m_pkg_buff)
        free(m_pkg_buff);
}

void StatDataMonitor::init()
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
        ERROR_LOG("StatDataMonitor::init failed");
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
            ERROR_LOG("StatDataMonitor::init m_pkg_buff malloc failed!");
    }   
}

void StatDataMonitor::proc_timer_event()
{
    if (m_send_or_not)
    {
        if (m_time_begin_collect == 0)    // to handle the situation when the first alarm come since stat-center start
            return;

        time_t now;
        time(&now);

        if ((now - m_time_begin_collect) > 90)   // delay 90 seconds to send alarm
        {
            // TODO
            if (m_game_id.empty())
                return;

            map<string, set<uint32_t> >::iterator miter = m_game_id.begin();
            while(miter != m_game_id.end())
            {
                StatAlarmMsg alarm_msg;
                alarm_msg += "Messy code from gameid : ";

                ostringstream oss;
                set<uint32_t>::iterator siter_temp = miter->second.begin();
                while(siter_temp != miter->second.end())
                {
                    oss << *siter_temp << ", ";
                    siter_temp++;
                }

                alarm_msg += oss.str();
                alarm_msg.set_msg_header(string("notutf8-db"), miter->first);

                send_data_alarm(alarm_msg, 0);

                miter++;
            }

            m_send_or_not = false;
            m_game_id.clear();
        }
    }
}

int StatDataMonitor::proc_proto(int fd, const void* pkg_buf)
{
    DEBUG_LOG("data_monitor");
    const StatProtoHeader* header = static_cast<const StatProtoHeader*>(pkg_buf);
    const StatModuleHeader* module_header = static_cast<const StatModuleHeader*>(pkg_buf);

    StatModuleInfo module_info;
    module_info.parse_from_pkg(module_header);
    switch(header->proto_id)
    {
        case STAT_PROTO_NOTUTF8_DB:
            m_send_or_not = true;
            notutf8_gameid_collect(fd, module_info, pkg_buf);
            break;
        case STAT_PROTO_INSERT_STAT_ERROR_CS:
            insert_stat_error_alarm(fd, module_info, pkg_buf);
            break;
        case STAT_PROTO_UPLOAD_INFO:
			upload_info_check_alarm(fd, module_info, pkg_buf);
			break;
        case STAT_PROTO_STAT_CALC:
            stat_calc_error(fd, module_info, pkg_buf);
            break;
        case STAT_PROTO_REG_ALARM:
            DEBUG_LOG(" STAT_PROTO_REG_ALARM:");
            stat_reg_alarm(fd, module_info, pkg_buf);
            break;
        default:
            ERROR_LOG("unsupported proto: %X", header->proto_id);
            break;
    }

    return 0;
}

void StatDataMonitor::send_alarm(const StatAlarmMsg& msg, uint8_t flag)
{
    StatAlarmerProto::StatAlarmRequest request;

    request.set_title("Stat Alarm");
    string full_msg = msg.get_full_msg();
    request.set_content(full_msg);
    ostringstream oss; 
    oss << "\n---------------Alarm Message---------------------\n"
        << full_msg
        << "send to : ";

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

    if (flag == 1)
    {
        oss << "\nSend RTX";
        req_pkg->proto_id = STAT_ALARMER_PROTO_RTX;  // RTX 告警
        if (!send_and_recv(m_pkg_buff, pkg_len))
        {
            ERROR_LOG("Send Rtx alarm request faild !");
            oss << " one or more failed";
        }
    }

    oss << "\nSend e-mail";
    req_pkg->proto_id = STAT_ALARMER_PROTO_EMAIL; // Mail 告警
    if (!send_and_recv(m_pkg_buff, pkg_len))
    {
        ERROR_LOG("Send Mail alarm request faild !");
        oss << " one or more failed";
    }

    if (flag == 2)
    {
        oss << "\nSend RTX";
        req_pkg->proto_id = STAT_ALARMER_PROTO_RTX;  // RTX 告警
        if (!send_and_recv(m_pkg_buff, pkg_len))
        {
            ERROR_LOG("Send Rtx alarm request faild !");
            oss << " one or more failed";
        }

        oss << "\nSend mobile";

        req_pkg->proto_id = STAT_ALARMER_PROTO_MOBILE; // mobile 告警
        if (!send_and_recv(m_pkg_buff, pkg_len))
        {    
            ERROR_LOG("Send mobile alarm request faild !");
            oss << " one or more failed";
        }    

        oss << "\nSend Weixin";
        req_pkg->proto_id = STAT_ALARMER_PROTO_WEIXIN; // Weixin 告警
        if (!send_and_recv(m_pkg_buff, pkg_len))
        {    
            ERROR_LOG("Send Weixin alarm request faild !");
            oss << " one or more failed";
        }    
    }

    oss << "\n-------------------------------------------------";
    INFO_LOG("%s",oss.str().c_str());
}

bool StatDataMonitor::send_and_recv(char * pkg_buff, uint32_t size)
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

void StatDataMonitor::upload_info_check_alarm(int fd, const StatModuleInfo& module_info, const void* pkg_buf)
{
	const StatUploadInfo* uploadinfo = static_cast<const StatUploadInfo*>(pkg_buf);

	StatAlarmMsg alarm_msg;

	StatAlarmRet alarm_ret;
	alarm_ret.len = sizeof(alarm_ret);
	alarm_ret.proto_id = uploadinfo->proto_id;

	ostringstream oss;
	oss << "upload ";
	
	if (uploadinfo->len < 33)
	    alarm_ret.ret = -1;
	else
	    alarm_ret.ret = 0;

	switch (uploadinfo->e_flag)
	{
		case 1:
			oss << "close_exp";
			break;
		case 2:
			oss << "close_null";
			break;
		case 3:
			oss << "local_file_not_found";
			break;
		case 4:
			oss << "local_ioexp";
			break;
		case 5:
			oss << "local_close_exp";
			break;
		case 6:
			oss << "hdfs_remote_exp";
			break;
		case 7:
			oss << "hdfs_not_utf8";
            break;
		case 8:
			oss << "hdfs_ioexp";
			break;
		default:
			oss << "error_code=" << uploadinfo->e_flag;
			break;
	}
	oss << " from game " << uploadinfo->game_id;
	alarm_msg += oss.str();

	alarm_msg.set_msg_header(module_info.get_module_name(), module_info.get_ip_str());
	if (alarm_ret.ret == 0 && uploadinfo->e_flag != 0)
	{
		send_data_alarm(alarm_msg, 1);
	}
	//返回包
	net_send_cli(fd, &alarm_ret, sizeof(alarm_ret));
}

void StatDataMonitor::stat_calc_error(int fd, const StatModuleInfo& module_info, const void* pkg_buf)
{
    const StatCalcError* stat_calc = static_cast<const StatCalcError*>(pkg_buf);

    StatAlarmMsg alarm_msg;
    string mobile_alarm_msg;

    StatAlarmRet alarm_ret;
    alarm_ret.len = sizeof(alarm_ret);
    alarm_ret.proto_id = stat_calc->proto_id;

    alarm_msg += "Stat calc error: ";
    
    if(stat_calc->len < sizeof(struct StatCalcError))
        alarm_ret.ret = -1;
    else
        alarm_ret.ret = 0;

    char buf_temp[100];
    memset(buf_temp, 0, sizeof(buf_temp));
    memcpy(buf_temp, stat_calc->body, stat_calc->body_len);
    //string str_temp = buf_temp;
    //alarm_msg += str_temp;
    alarm_msg += buf_temp;

    alarm_msg.set_msg_header(module_info.get_module_name(), module_info.get_ip_str());

    if (alarm_ret.ret == 0)
    {
        send_data_alarm(alarm_msg, 0);
    }

    net_send_cli(fd, &alarm_ret, sizeof(alarm_ret));
}

void StatDataMonitor::insert_stat_error_alarm(int fd, const StatModuleInfo& module_info, const void* pkg_buf)
{
    const InsertStatErrorCsHeader* insert_error_header = static_cast<const InsertStatErrorCsHeader*>(pkg_buf);

    StatAlarmMsg alarm_msg;
    string mobile_alarm_msg;

    StatAlarmRet alarm_ret;
    alarm_ret.len = sizeof(alarm_ret);
    alarm_ret.proto_id = insert_error_header->proto_id;

    alarm_msg += "Insert stat error: ";

    if (insert_error_header->len < 14)
        alarm_ret.ret = -1;
    else
        alarm_ret.ret = 0;

    switch (insert_error_header->error_type)
    {
        case 1:
            alarm_msg += "redis update failed";
            break;
        case 2:
            alarm_msg += "E_GET_REPORT_ID";
            break;
        case 3:
            alarm_msg += "E_GET_DATA_ID";
            break;
        case 4:
            alarm_msg += "E_GET_GPZS_ID";
            break;
        case 5:
            alarm_msg += "E_GET_TASK";
            break;
        default:
            break;
    }

    alarm_msg.set_msg_header(module_info.get_module_name(), module_info.get_ip_str());

    if (alarm_ret.ret == 0)
    {
        send_data_alarm(alarm_msg, 0);
    }

    net_send_cli(fd, &alarm_ret, sizeof(alarm_ret));
}

void StatDataMonitor::send_sms_alarm(const string& msg) 
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

void StatDataMonitor::send_data_alarm(const StatAlarmMsg& alarm_msg, uint8_t flag)
{
    send_alarm(alarm_msg, flag);
}

void StatDataMonitor::notutf8_gameid_collect(int fd, const StatModuleInfo& module_info, const void* pkg_buf)
{
    time(&m_time_begin_collect);

    const StatNOTUTF8DbHeader* notutf8_header = static_cast<const StatNOTUTF8DbHeader*>(pkg_buf);

    StatAlarmRet alarm_ret;
    alarm_ret.len = sizeof(alarm_ret);
    alarm_ret.proto_id = notutf8_header->proto_id;

    if (notutf8_header->len <= 17)
        alarm_ret.ret = -1;
    else
        alarm_ret.ret = 0;

    uint32_t num = notutf8_header->num;

    map<string, set<uint32_t> >::iterator iter = m_game_id.find(module_info.get_ip_str());

    if (iter == m_game_id.end())
    {
        set<uint32_t> st_temp;
        for (uint32_t i=0; i<num; ++i)
        {
            uint32_t game_id = *(uint32_t*)(notutf8_header->body + (i * sizeof(uint32_t)));
            st_temp.insert(game_id);
        }
        m_game_id.insert(pair<string, set<uint32_t> > (module_info.get_ip_str(), st_temp));
    }
    else
    {
        for (uint32_t i=0; i<num; ++i)
        {
            uint32_t game_id = *(uint32_t*)(notutf8_header->body + (i * sizeof(uint32_t)));
            iter->second.insert(game_id);
        }

    }

    net_send_cli(fd, &alarm_ret, sizeof(alarm_ret));
}

void StatDataMonitor::stat_reg_alarm(int fd, const StatModuleInfo& module_info, const void* pkg_buf)
{
    const StatRegAlarm* reg_alarm = static_cast<const StatRegAlarm*>(pkg_buf);

    StatAlarmMsg alarm_msg;
    string mobile_alarm_msg;

    StatAlarmRet alarm_ret;
    alarm_ret.len = sizeof(alarm_ret);
    alarm_ret.proto_id = reg_alarm->proto_id;

    alarm_msg += "注册转化异常";
    
    if(reg_alarm->len < sizeof(struct StatRegAlarm))
        alarm_ret.ret = -1;
    else
        alarm_ret.ret = 0;

    alarm_msg.set_msg_header(module_info.get_module_name(), "");

    if (alarm_ret.ret == 0)
    {
        send_data_alarm(alarm_msg, 2);
    }

    net_send_cli(fd, &alarm_ret, sizeof(alarm_ret));
}


