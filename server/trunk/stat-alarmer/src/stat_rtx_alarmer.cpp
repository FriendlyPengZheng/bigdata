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
#include <stat_config.hpp>
#include "url_code.h"
#include "stat_common.hpp"
#include "stat_rtx_alarmer.hpp"

using std::map;

StatRtxAlarmer::StatRtxAlarmer(uint32_t proto_id, const char* proto_name, StatGroupAlarmer* sag)
	: StatAlarmerHandler(proto_id, proto_name, STAT_ALARMER_GROUP_RTX, sag)
{
	string rtx_host, rtx_port, rtx_url;
	StatCommon::stat_config_get("rtx_host", rtx_host);
	StatCommon::stat_config_get("rtx_port", rtx_port);
	StatCommon::stat_config_get("rtx_url", rtx_url);
	DEBUG_LOG("rtx port:%d", atoi(rtx_port.c_str()));

	m_http_request.init(rtx_host, atoi(rtx_port.c_str()));
	m_http_url = rtx_url;
}

//应该作成共用模块
string StatRtxAlarmer::urlEncode(const string& str) const
{

	int str_len = str.length();                                       
	char *resultBuff = (char*)malloc(str_len * 3);                    
	memset(resultBuff, 0, str_len*3);

	int len = url_encode(str.c_str(), str_len, resultBuff, str_len*3);
	string urlString(resultBuff, len);
	free(resultBuff);                                                 
	return urlString;
}

uint8_t StatRtxAlarmer::send_alarm(const StatAlarmerProto::StatAlarmRequest& req) 
{
    DEBUG_LOG("send with rxt.title:%s content:%s",
			req.title().c_str(), req.content().c_str());

	//拼接联系人字符串
    string receiver;
    for(int i = 0; i < req.send_to_size(); ++i)
    {
        const StatAlarmerProto::AlarmContact& contact = req.send_to(i);

        receiver += contact.name();
        receiver += ",";
    }

	//组织rtx网关接口参数
	string http_param = "title=" + urlEncode(req.title()) + "&msg=" + urlEncode(req.content()) + "&receiver=" + urlEncode(receiver);

	//DEBUG_LOG("rtx send param:%s", http_param.c_str());
	//DEBUG_LOG("rtx send url:%s", m_http_url.c_str());

	//发送rtx信息
	int ret = m_http_request.post(m_http_url, http_param);
	if(ret == 200)
	{
		return 0;
	}
	else
	{
		ERROR_LOG("send rtx msg failed.ret:%d", ret);
		return 1;
	}
}
