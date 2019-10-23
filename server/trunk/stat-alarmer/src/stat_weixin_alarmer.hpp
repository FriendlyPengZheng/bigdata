/** =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2014, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-center服务模块。
 *   @author  tomli<tomli@taomee.com>
 *   @date    2014-08-27
 * =====================================================================================
 **/

#ifndef STAT_ALARM_CONF_HPP 
#define STAT_ALARM_CONF_HPP 

#include "stat_alarmer_defines.hpp"
#include "stat_alarmer_handler.hpp"

class StatWeixinAlarmer : public StatAlarmerHandler
{
    public:
        StatWeixinAlarmer(uint32_t proto_id, const char* proto_name, StatGroupAlarmer* sag);
        virtual ~StatWeixinAlarmer()
        {}  

    private:
        void init_weixin_url();
        void init_weixin_corpid();
        void init_weixin_corpsecret();
        void init_weixin_agentid();
        void init_get_url();
        void get_weixin_access_token();
        void curl_post(const string& content);

    private:
        struct str
        {
            char* ptr;
            int len;
        };

    private:
        void init_string(struct str* s);
        static size_t writefunc(void *ptr, size_t size, size_t nmemb, struct str *s);
    private:
        virtual uint8_t send_alarm(const StatAlarmerProto::StatAlarmRequest& req);
        int send_msg(const string& content);

    private:
        string m_weixin_url;
        string m_weixin_corpid;
        string m_weixin_corpsecret;
        string m_weixin_get_url;
        string m_weixin_agentid;
        string m_weixin_access_token;
        string m_weixin_post_url;

        pthread_mutex_t m_stat_mutex;
};


#endif

