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


#include <curl/curl.h>
#include <curl/types.h>
#include <curl/easy.h>
#include <stdio.h>

#include "stat_weixin_alarmer.hpp"
#include <stat_config.hpp>
#include "stat_common.hpp"

#include <iostream>

using namespace std;

StatWeixinAlarmer::StatWeixinAlarmer(uint32_t proto_id, const char* proto_name, StatGroupAlarmer* sag)
        : StatAlarmerHandler(proto_id, proto_name, STAT_ALARMER_GROUP_WEIXIN, sag)
{
    init_weixin_url();
    init_weixin_corpid();
    init_weixin_corpsecret();
    init_weixin_agentid();
    init_get_url();
    get_weixin_access_token();
}

void StatWeixinAlarmer::init_string(struct str *s) 
{
    s->len = 0;
    s->ptr = (char*)malloc(s->len+1);
    if (s->ptr == NULL)
    {
        ERROR_LOG("malloc() failed in StatWeixinAlarmer::init_string");
        exit(EXIT_FAILURE);
    }
    s->ptr[0] = '\0';
}

void StatWeixinAlarmer::init_weixin_url()
{
    StatCommon::stat_config_get("weixin_url", m_weixin_url);
    DEBUG_LOG("weixin_url: %s", m_weixin_url.c_str());
}

void StatWeixinAlarmer::init_weixin_corpid()
{
    StatCommon::stat_config_get("weixin_corpid", m_weixin_corpid);
    DEBUG_LOG("weixin_corpid: %s", m_weixin_corpid.c_str());
}

void StatWeixinAlarmer::init_weixin_corpsecret()
{
    StatCommon::stat_config_get("weixin_corpsecret", m_weixin_corpsecret);
    DEBUG_LOG("weixin_corpsecret: %s", m_weixin_corpsecret.c_str());
}

void StatWeixinAlarmer::init_weixin_agentid()
{
    StatCommon::stat_config_get("weixin_agentid", m_weixin_agentid);
    DEBUG_LOG("weixin_agentid: %s", m_weixin_agentid.c_str());
}

void StatWeixinAlarmer::init_get_url()
{
    m_weixin_get_url += m_weixin_url;
    m_weixin_get_url += "corpid=";
    m_weixin_get_url += m_weixin_corpid;
    m_weixin_get_url += "&corpsecret=";
    m_weixin_get_url += m_weixin_corpsecret;
    DEBUG_LOG("m_weixin_get_url: %s", m_weixin_get_url.c_str());
}

void StatWeixinAlarmer::get_weixin_access_token()
{
    m_weixin_access_token.clear();

    CURL *curl;
    CURLcode res;

    curl = curl_easy_init();    // 初始化
    if (curl)
    {   
        struct str s;
        init_string(&s);

        curl_easy_setopt(curl, CURLOPT_URL, m_weixin_get_url.c_str());
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, writefunc);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, &s);

        res = curl_easy_perform(curl);   // 执行
        if (res != CURLE_OK) 
        {   
            ERROR_LOG("curl_east_perform failed in init_weixin_access_token");
            curl_easy_cleanup(curl);
            return;
        }   

        char* temp_p = strtok(s.ptr, ",");
        char* temp_q = strtok(temp_p, ":");
        temp_q = strtok(NULL, ":");

        string access_token;
        access_token.assign(temp_q+1, strlen(temp_q)-2);

        m_weixin_access_token += access_token;
        DEBUG_LOG("m_weixin_access_token: %s", m_weixin_access_token.c_str());

        free(s.ptr);
        curl_easy_cleanup(curl);
    }   
    else
    {
        ERROR_LOG("curl_easy_init failed in init_weixin_access_token");
    }
}


void StatWeixinAlarmer::curl_post(const string& content)
{
    // 此处加锁是因为多个线程同时使用curl执行http请求
    pthread_mutex_lock(&m_stat_mutex);

    get_weixin_access_token();

    StatCommon::stat_config_get("weixin_post_url", m_weixin_post_url);
    m_weixin_post_url += "access_token=";
    m_weixin_post_url += m_weixin_access_token;

    string post_data;
    post_data += "{\"totag\": \"10\", \"msgtype\":\"text\", \"agentid\": \"";
    post_data += m_weixin_agentid.c_str();
    post_data += "\", \"text\": { \"content\": \"";
    post_data += content;
    post_data += "\"}}";

    CURL *curl;
    CURLcode res;

    curl = curl_easy_init();
    struct curl_slist *headers = NULL;
    if (curl)
    {   
        struct str s;
        init_string(&s);

        curl_easy_setopt(curl, CURLOPT_COOKIEFILE, "cookie.txt"); // 指定cookie文件
        curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, post_data.length());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, post_data.c_str());    // 指定post内容
        curl_easy_setopt(curl, CURLOPT_URL, m_weixin_post_url.c_str());   // 指定url
        curl_easy_setopt(curl, CURLOPT_POST,1);
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, writefunc);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, &s);

        res = curl_easy_perform(curl);
        DEBUG_LOG("%s", s.ptr);

        free(s.ptr);
        curl_easy_cleanup(curl);
    }   

    pthread_mutex_unlock(&m_stat_mutex);
}

uint8_t StatWeixinAlarmer::send_alarm(const StatAlarmerProto::StatAlarmRequest& req)
{
    DEBUG_LOG("send with weixin.title:%s content:%s",
                         req.title().c_str(), req.content().c_str());

    int ret = send_msg(req.content());
    if (ret < 0)
        return 1;
    return 0;
}

int StatWeixinAlarmer::send_msg(const string& content)
{
    curl_post(content);
}

size_t StatWeixinAlarmer::writefunc(void *ptr, size_t size, size_t nmemb, struct str *s)
{
    size_t new_len = s->len + size*nmemb;
    s->ptr = (char*)realloc(s->ptr, new_len+1);
    if (s->ptr == NULL) {
        ERROR_LOG("realloc() failed in StatWeixinAlarmer::writefunc");
        exit(EXIT_FAILURE);
    }   
    memcpy(s->ptr+s->len, ptr, size*nmemb);
    s->ptr[new_len] = '\0';
    s->len = new_len;

    return size*nmemb;

}
