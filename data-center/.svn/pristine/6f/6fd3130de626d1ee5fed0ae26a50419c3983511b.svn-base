/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台公共库，各服务模块共享。
 *   @author  Lance<lance@taomee.com>
 *   @date    2014-04-14
 * =====================================================================================
 */

#include <stdlib.h>
#include <string.h>
#include <openssl/md5.h>
#include <map>
#include <sstream>

#include "stat_bdcloud_pusher.hpp"
#include "url_code.h"

using std::map;
using std::ostringstream;
using namespace std;

const string StatBDcloudPusher::s_host = "channel.api.duapp.com";
const string StatBDcloudPusher::s_url = "/rest/2.0/channel/channel";

StatBDcloudPusher::StatBDcloudPusher(const string& api_key, const string& secret_key) 
    : m_api_key(api_key), m_secret_key(secret_key), m_http_request(s_host, 80, 2000000), m_user_id(""), m_tag_id(""), m_push_type(0)
{
}

StatBDcloudPusher::~StatBDcloudPusher()
{
}

int StatBDcloudPusher::push_msg(const string& user_id, const PushMsgRequest& request)
{
    clear();
    if (user_id.empty())
        return -1;
    m_user_id = user_id;
    m_push_type = 1;
    m_request = request;
    return process("push_msg");
}

int StatBDcloudPusher::push_broadcast_msg(const PushMsgRequest& request)
{
    clear();
    m_push_type = 3; // 3 broadcast
    m_request = request;
    return process("push_msg");
}

int StatBDcloudPusher::push_tag_msg(const string& tag, const PushMsgRequest& request)
{
    clear();
    if (tag.empty())
        return -1;
    m_tag_id = tag;
    m_push_type = 2;
    m_request = request;
    return process("push_msg");
}

int StatBDcloudPusher::tag_add(const string& tag)
{
    clear();
    if (tag.empty())
        return -1;
    m_tag_id = tag;
    return process("set_tag");
}

int StatBDcloudPusher::tag_bind_user(const string& tag, const string& user_id)
{
    clear();
    if (tag.empty() || user_id.empty())
        return -1;
    m_tag_id = tag;
    m_user_id = user_id;
    return process("set_tag");
}

int StatBDcloudPusher::tag_del(const string& tag)
{
    clear();
    if (tag.empty())
        return -1;
    m_tag_id = tag;
    return process("delete_tag");
}

int StatBDcloudPusher::tag_unbind_user(const string& tag, const string& user_id)
{
    clear();
    if (tag.empty() || user_id.empty())
        return -1;
    m_tag_id = tag;
    m_user_id = user_id;
    return process("delete_tag");
}

int StatBDcloudPusher::process(const string& method)
{
    map<string, string> param;
    param.insert(make_pair("method", method));
    param.insert(make_pair("apikey", m_api_key));

    if (0 != m_push_type) //0 标签操作
    {
        char push_type[10] = {0};
        snprintf(push_type, 9,"%d", m_push_type); 
        param.insert(make_pair("push_type", string(push_type))); // 1 单人；2 tag组；3 广播
        param.insert(make_pair("device_type", m_request.device_type));// 1-浏览器; 2 PC; 3 Android; 4 IOS; 5 Windows Phone; default : 3
        if (m_request.device_type != "4") {
            param.insert(make_pair("message_type","0")); // 0 自定义；1 通知
            param.insert(make_pair("messages","{\"title\":\"" + m_request.msg_title + "\",\"description\":\"" + m_request.msg_content + "\"}"));//json
        }
        else {
            param.insert(make_pair("message_type","1")); // ios只支持通知
            param.insert(make_pair("messages","{\"aps\":{\"alart\":\"You Have One Or More Alarm Messages\",\"sound\":\"default\",\"badge\":1}}"));
            param.insert(make_pair("deploy_status", "2"));
        }
        param.insert(make_pair("msg_keys", m_request.msg_key)); // 重复的msg_key会被覆盖

    }

    if (!m_user_id.empty())
        param.insert(make_pair("user_id", m_user_id));

    if (!m_tag_id.empty())
        param.insert(make_pair("tag", m_tag_id));

    time_t timestamp;
    time(&timestamp);
    char time_buf[16] = {0};
    snprintf(time_buf, 15, "%lu", timestamp);
    param.insert(make_pair("timestamp", string(time_buf)));//发送请求时的时间戳，请求签名的有效时间为本时间+10min

    string sign = digest("POST", get_integrity_url(), m_secret_key, param);
    param.insert(make_pair("sign", sign));


    string http_param = build_http_param(param);
    int ret = m_http_request.post(s_url, http_param);

    if (200 == ret)//Success
        return 0;
    else if (0 == ret)//Timeout
        return 1;
    else 
        return -1;//Error
}

string StatBDcloudPusher::get_integrity_url() const
{
    string integrity_url = s_host;
    if (string::npos == integrity_url.find("http://") && string::npos == integrity_url.find("https://"))
        integrity_url = "http://" + integrity_url;

    if ((integrity_url.find_last_of("/")+1 < integrity_url.length()) && s_url.find_first_of("/") != 0)
        integrity_url += "/" + s_url;
    else
        integrity_url += s_url;
    
    return integrity_url;
}

string StatBDcloudPusher::urlEncode(const string& str) const
{
    int str_len = str.length();
    char *resultBuff = (char*)malloc(str_len * 3);
    memset(resultBuff, 0, str_len*3);

    int len = url_encode(str.c_str(), str_len, resultBuff, str_len*3);
    string urlString(resultBuff, len);
    free(resultBuff);
    return urlString;
}

string StatBDcloudPusher::toMD5Hex(const string& str) const
{
    char result_buff[32] = {0};
    unsigned char md5_buff[16] = {0};

    MD5((unsigned char*)str.c_str(), str.length(), md5_buff);
    for (int i=0; i<16; ++i)
        sprintf(result_buff + 2*i, "%2.2x", md5_buff[i]);

    return string(result_buff, 32);
}

string StatBDcloudPusher::digest(const string& httpMethod, const string& url, const string& secretKey, map<string,string>& param) const
{
    ostringstream oss;
    oss << httpMethod << url;
    map<string,string>::iterator it = param.begin();
    for(;it != param.end(); ++it)
    {
        oss << it->first << "=" << it->second;
    }
    oss << secretKey;
    string encodestring = urlEncode(oss.str());

    if (!encodestring.empty())
    {
        string::size_type index = encodestring.find("\\*");
        while (index != string::npos)
        {
            encodestring.replace(index, 3, "%2A");
                index = encodestring.find("\\*");
        }
    }
    return toMD5Hex(encodestring);
}

string StatBDcloudPusher::build_http_param(map<string,string> param) const
{
    ostringstream oss;
    map<string,string>::iterator it = param.begin();
    bool isFirst = true;
    while (it != param.end())
    {
        if (!isFirst)
            oss << "&";
        else
            isFirst = false;

        oss << it->first << "=" << urlEncode(it->second);
        ++it;
    }
    return oss.str();
}
