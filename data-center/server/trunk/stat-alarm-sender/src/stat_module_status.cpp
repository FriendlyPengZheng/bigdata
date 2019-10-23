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

#include <stat_common.hpp>
#include "log.h"
#include "string_utils.hpp"

#include "proto/alarm_request.pb.h"
#include "../../stat-alarmer/src/stat_alarmer_defines.hpp"
#include "../../stat-common/stat_proto_defines.hpp"
#include "../../stat-common/tcp_client.hpp"
using std::ostringstream;

struct info_t {
    char    name[20];
    char    email[64];
    char    mobile[20];
} ;

info_t info[] = {
    { "ping",   "ping@taomee.com",   "15821994882" },
    //{ "henry",  "henry@taomee.com",  "13774451574" },
    { "kendy",  "kendy@taomee.com",  "18817597586" },
    //{ "berry",  "berry@taomee.com",  "13524803475" },
    { "tomli",  "tomli@taomee.com",  "15221006581" },
    { "billy",  "billy@taomee.com",  "13564092139" },
    //{ "bryceliu",  "bryceliu@taomee.com",  "15618678801" },
    { "rooney", "rooney@shootao.com", "18616502010" },
};

bool send_and_recv(char * pkg_buff, uint32_t size)
{
    do {
        TcpClient tc;
        int fd = tc.connect("192.168.71.20", "19401");
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

int main(int args, char* argv[])
{
    if(args < 2)    return -1;
    vector<vector<string>> m_contact;

    for(uint8_t i=0; i<sizeof(info)/sizeof(info[0]); i++) {
        vector<string> contect_item;
        contect_item.clear();
        contect_item.push_back(info[i].name);
        contect_item.push_back(info[i].email);
        contect_item.push_back(info[i].mobile);
        m_contact.push_back(contect_item);
    }

    char * m_pkg_buff = (char*)malloc(1024);
    uint32_t m_pkg_buff_len = 1024;
    StatAlarmerProto::StatAlarmRequest request;

    //request.set_title("Stat Alarm");
    char title[] = {
        0xca, 0xfd, 0xbe, 0xdd, 0xc6, 0xbd, 0xcc, 0xa8, 0xb2, 0xbf, 0x0
        //0xb2, 0xc2, 0xb2, 0xc2, 0xce, 0xd2, 0xca, 0xc7, 0xcb, 0xad, 0xa3, 0xbf, 0x0
    };//数据平台部的gb2312编码
    request.set_title(title);
    string full_msg(argv[1]);
    request.set_content(full_msg);

    if(args == 2) {
        for(uint32_t i=0; i<m_contact.size(); ++i) {
            StatAlarmerProto::AlarmContact *contact = request.add_send_to();
            contact->set_name(m_contact[i][0]);     //昵称
            contact->set_email(m_contact[i][1]);    //邮箱
            contact->set_mobile(m_contact[i][2]);   //手机号
        }
    } else {
        for(int i=3; i<args; i++) {
            for(uint32_t j=0; j<m_contact.size(); ++j) 
            {
                if(strcmp(argv[i], m_contact[j][0].c_str()) == 0) {
                    StatAlarmerProto::AlarmContact *contact = request.add_send_to();
                    contact->set_name(m_contact[j][0]);     //昵称
                    contact->set_email(m_contact[j][1]);    //邮箱
                    contact->set_mobile(m_contact[j][2]);   //手机号
                } else {
                    StatAlarmerProto::AlarmContact *contact = request.add_send_to();
                    char buf[128];
                    sprintf(buf, "%s@taomee.com", argv[i]);
                    contact->set_name(argv[i]);     //昵称
                    contact->set_email(argv[i]);    //邮箱
                    contact->set_mobile("0");   //手机号
                }
            }
        }
    }

    uint32_t pkg_len = sizeof(StatProtoHeader) + request.ByteSize();
    if (pkg_len > m_pkg_buff_len - sizeof(StatProtoHeader))
    {
        char * new_buff = (char*)calloc(pkg_len/m_pkg_buff_len + 1, m_pkg_buff_len);
        if (NULL == new_buff)
        {
            ERROR_LOG("calloc failed!");
            return -1;
        }
        m_pkg_buff_len = pkg_len / m_pkg_buff_len + 1;
        if (NULL != m_pkg_buff)
            free(m_pkg_buff);
        m_pkg_buff = new_buff;
    }
    if (NULL == m_pkg_buff)
    {
        ERROR_LOG("m_pkg_buff is NULL");
        return -1;
    }
    memset(m_pkg_buff, 0, m_pkg_buff_len);
    StatProtoHeader *req_pkg = (StatProtoHeader *)m_pkg_buff;
    req_pkg->len = pkg_len;
    request.SerializeToArray(req_pkg->body, request.ByteSize());

    if(strcmp(argv[2], "rtx") == 0) {
        req_pkg->proto_id = STAT_ALARMER_PROTO_RTX;  // RTX 告警
        if (!send_and_recv(m_pkg_buff, pkg_len))
        {
            ERROR_LOG("Send Rtx alarm request faild !");
        }
    } else if(strcmp(argv[2], "mail") == 0) {
        req_pkg->proto_id = STAT_ALARMER_PROTO_EMAIL; // Mail 告警
        if (!send_and_recv(m_pkg_buff, pkg_len))
        {
            ERROR_LOG("Send Mail alarm request faild !");
        }
    } else if(strcmp(argv[2], "app") == 0) {
        req_pkg->proto_id = STAT_ALARMER_PROTO_APPPUSH; // App 告警
        if (!send_and_recv(m_pkg_buff, pkg_len))
        {
            ERROR_LOG("Send App alarm request faild !");
        }
    }
    
    return 0;
}
