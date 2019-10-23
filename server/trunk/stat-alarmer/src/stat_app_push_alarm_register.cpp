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
#include <string>

#include "stat_common.hpp"
#include "proto/app_login_request.pb.h"
#include "stat_app_push_alarm_register.hpp"

using std::string;

int StatAppPushAlarmRegister::proc_proto(int fd, const void* pkg)
{

    if(m_stat_app_push == NULL)
        return -1;

    StatAppLoginProto::StatAppLoginRequest req;
    const StatAlarmerHeader* h = static_cast<const StatAlarmerHeader*>(pkg);
    req.ParseFromArray(h->body, h->len - sizeof(StatAlarmerHeader));

	DEBUG_LOG("register.user_name:%s, password:%s, token:%s, device_type:%s",
			req.user_name().c_str(), req.password().c_str(), req.token().c_str(), req.device_type().c_str());

	uint32_t status_code = 1;
	int ret = -1;
	// vevify user 
	if(!m_stat_alarmer_verifier.verify(req.user_name(), req.password()))
	{
		ERROR_LOG("user verify failed.taomee_id:%s, passwd:%s", req.user_name().c_str(), req.password().c_str());
		goto DONE;
	}

    ret = m_stat_app_push->app_register(req.user_name(), req.token().c_str(), req.device_type(), req.mobile());
	if(ret == 0)
	{
		status_code = 0;
	}
	else
	{
		status_code = 1;
	}

DONE:
    StatAppLoginProto::StatAppLoginResponse res;
    res.set_ret(status_code);
    int body_len = res.ByteSize();

    uint8_t ret_buf[4096] = {0};
	if(sizeof(ret_buf) < body_len + sizeof(StatAlarmerHeader))
	{
		ERROR_LOG("buffer is not enough when sending StatAppLoginResponse, need %lu buf buffer size is %lu",
				body_len + sizeof(StatAlarmerHeader), sizeof(ret_buf));
		return -1;
	}
    StatAlarmerHeader* ret_pkg = (StatAlarmerHeader*)ret_buf;

	ret_pkg->len = body_len + sizeof(StatAlarmerHeader);
	res.SerializeToArray(ret_pkg->body, body_len);
	DEBUG_LOG("register reponse.len:%d ret:%u", ret_pkg->len, res.ret());

	return net_send_cli(fd, ret_buf, ret_pkg->len);
}
