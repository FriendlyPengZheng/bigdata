/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-alarmer服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2014-04-09
 * =====================================================================================
 */

#include <cstddef>
#include <openssl/md5.h>
#include <stat_config.hpp>
#include "url_code.h"
#include "stat_common.hpp"
#include "stat_mobile_alarmer.hpp"

#define MOBILE_MSG_MAX_LEN 69

StatMobileAlarmer::StatMobileAlarmer(uint32_t proto_id, const char* proto_name, StatGroupAlarmer* sag)
	: StatAlarmerHandler(proto_id, proto_name, STAT_ALARMER_GROUP_MOBILE, sag)
{
	string mobile_gate_enabled, mobile_host, mobile_port, mobile_url, mobile_sign;
	StatCommon::stat_config_get("mobile_gate_enabled", mobile_gate_enabled);
	
	if(atoi(mobile_gate_enabled.c_str()) == 1)
	{
		// 短信网关1
		StatCommon::stat_config_get("mobile_1_host", mobile_host);
		StatCommon::stat_config_get("mobile_1_port", mobile_port);
		StatCommon::stat_config_get("mobile_1_url", mobile_url);
		StatCommon::stat_config_get("mobile_1_sign", mobile_sign);
	}
	else if(atoi(mobile_gate_enabled.c_str()) == 2)
	{
		// 短信网关2
		StatCommon::stat_config_get("mobile_2_host", mobile_host);
		StatCommon::stat_config_get("mobile_2_port", mobile_port);
		StatCommon::stat_config_get("mobile_2_url", mobile_url);
		StatCommon::stat_config_get("mobile_2_sign", mobile_sign);
		// mobile_sign.clear();
	}
	DEBUG_LOG("mobile conf.host:%s, port:%d, url:%s, sign:%s",
			mobile_host.c_str(), atoi(mobile_port.c_str()), mobile_url.c_str(), mobile_sign.c_str());

	m_mobile_gate_enabled = atoi(mobile_gate_enabled.c_str());
	m_http_url = mobile_url;
	m_http_param_sign = mobile_sign;
	m_http_request.init(mobile_host, atoi(mobile_port.c_str()));
}

string StatMobileAlarmer::urlEncode(const string& str) const
{

	int str_len = str.length();                                       
	char *resultBuff = (char*)malloc(str_len * 3);                    
	memset(resultBuff, 0, str_len*3);

	int len = url_encode(str.c_str(), str_len, resultBuff, str_len*3);
	string urlString(resultBuff, len);
	free(resultBuff);                                                 
	return urlString;
}

// 字符串md5方法
static string string_md5(string str)
{
	unsigned char md[16];
	char tmp[33] = {};
	string hash = ""; 

	MD5((const unsigned char*)str.c_str(), str.size(), md); 

	for(int i = 0; i < 16; i++)
	{
		sprintf(tmp, "%02x", md[i]); 
		hash+=(string)tmp; 
	} 

	return hash; 
} 

int StatMobileAlarmer::send_msg(const string& mobile, const string& content)
{
	string http_param;
	if(m_mobile_gate_enabled == 1)
	{
		http_param = "sign=" + urlEncode(m_http_param_sign) + "&msg=" + urlEncode(content.substr(0, MOBILE_MSG_MAX_LEN)) + "&mobile=";
	}
	else if(m_mobile_gate_enabled == 2)
	{
		string sign = string_md5(mobile + content.substr(0, MOBILE_MSG_MAX_LEN) + m_http_param_sign);
		http_param = "sms_sign=" + sign + "&msg=" + urlEncode(content.substr(0, MOBILE_MSG_MAX_LEN)) + "&mobile=";
	}
	else
	{
		ERROR_LOG("m_mobile_gate_enabled not allowed.m_mobile_gate_enabled:%d", m_mobile_gate_enabled);
		return -1;
	}
	http_param += urlEncode(mobile);
	//DEBUG_LOG("mobile send url:%s", m_http_url.c_str());
	//DEBUG_LOG("mobile send param:%s", http_param.c_str());
	int ret = m_http_request.post(m_http_url, http_param);
	if(ret != 200)
	{
		ERROR_LOG("send mobile msg failed.ret:%d mobile:%s", ret, mobile.c_str());
		return -1;
	}

	return 0;
}

uint8_t StatMobileAlarmer::send_alarm(const StatAlarmerProto::StatAlarmRequest& req) 
{
    DEBUG_LOG("send with mobile.title:%s content:%s",
			req.title().c_str(), req.content().c_str());

    for(int i = 0; i < req.send_to_size(); ++i)
    {
        const StatAlarmerProto::AlarmContact& contact = req.send_to(i);

		int ret = send_msg(contact.mobile(), req.content());
		if(ret < 0)
		{
			return 1;
		}
    }

	return 0;
}
