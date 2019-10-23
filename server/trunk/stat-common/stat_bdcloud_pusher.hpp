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

#ifndef STAT_BDCLOUD_PUSHER_HPP
#define STAT_BDCLOUD_PUSHER_HPP

#include <string>
#include <map>

#include "stat_http_request.hpp"
#include "stat_msg_pusher.hpp"

using std::string;
using std::map;

class StatBDcloudPusher : public StatMsgPusher
{
public:
    StatBDcloudPusher(const string& api_key, const string& secret_key);
    ~StatBDcloudPusher();

    virtual int push_msg(const string& user_id, const PushMsgRequest& request);
    virtual int push_broadcast_msg(const PushMsgRequest& request);
    virtual int push_tag_msg(const string& tag, const PushMsgRequest& request);

    virtual int tag_add(const string& tag);
    virtual int tag_bind_user(const string& tag, const string& user_id);
    virtual int tag_del(const string& tag);
    virtual int tag_unbind_user(const string& tag, const string& user_id); 

private:
    void clear();
    int process(const string& method);
    string urlEncode(const string& str) const;
    string toMD5Hex(const string& str) const;
    string digest(const string& httpMethod, const string& url, const string& secretKey, map<string,string>& param) const;
    string build_http_param(map<string,string> param) const;
    string get_integrity_url() const;

private:
    static const string s_host;
    static const string s_url;

private:
    const string m_api_key;
    const string m_secret_key;

    StatHttpRequest m_http_request;

    string m_user_id;
    string m_tag_id;
    uint8_t m_push_type;
    PushMsgRequest m_request;
};

inline void StatBDcloudPusher::clear() 
{
    m_user_id.clear();
    m_tag_id.clear();
    m_push_type = 0;
}
#endif
