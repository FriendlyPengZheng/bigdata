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
#include "proto/app_pull_msg.pb.h"
#include "stat_app_push_alarm_fetcher.hpp"

using std::string;

int StatAppPushAlarmFetcher::proc_proto(int fd, const void* pkg)
{
    if(m_stat_app_push == NULL)
        return -1;

    StatPullMsgProto::AppPullMsgRequest req;
    const StatAlarmerHeader* h = static_cast<const StatAlarmerHeader*>(pkg);
	req.ParseFromArray(h->body, h->len - sizeof(StatAlarmerHeader));

	/*
	uint32_t status_code = 1;
    string title;
    string content;
    if(m_stat_app_push->fetch_alarm(req, req.msg_id(), title, content) == 0)
	{
		status_code = 0;
	}
	*/
	// XXX:最好解析出协议内容，把内容传递给app_push_alarmer,而不是传递协议体
	// XXX:最好只在本模块处理pull的协议，app_push_alarmer不该知道pull的协议格式
    StatPullMsgProto::AppPullMsgResponse res;
    if(m_stat_app_push->fetch_alarm(req, res) < 0)
	{
		res.set_ret(1);
	}
	else
	{
		res.set_ret(0);
	}

    int body_len = res.ByteSize();

    uint8_t static_buf[1024] = {0};// hard code
	uint8_t *dynamic_buf = NULL;
    uint8_t *ret_buf = static_buf;
	if(sizeof(static_buf) < body_len + sizeof(StatAlarmerHeader))
	{
		WARN_LOG("buffer is not enough when sending AppPullMsgResponse, need %lu buf buffer size is %lu",
				body_len + sizeof(StatAlarmerHeader), sizeof(static_buf));
		uint8_t *dynamic_buf = (uint8_t *)malloc((body_len + sizeof(StatAlarmerHeader)) * sizeof(uint8_t));
		if(dynamic_buf == NULL)
		{
			// ret返回失败
			WARN_LOG("malloc fetch ret buffer failed. len:%lu",(body_len + sizeof(StatAlarmerHeader)) * sizeof(uint8_t));
			StatPullMsgProto::AppPullMsgResponse error_res;
			error_res.set_ret(1);
			res = error_res;
			body_len = res.ByteSize();
		}
		else
		{
			ret_buf = dynamic_buf;
		}
	}
    StatAlarmerHeader* ret_pkg = (StatAlarmerHeader*)ret_buf;

	ret_pkg->len = body_len + sizeof(StatAlarmerHeader);
	res.SerializeToArray(ret_pkg->body, body_len);
	INFO_LOG("fetch OK.user_name:%s reponse.len:%d ret:%u", req.user_name().c_str(), ret_pkg->len, res.ret());

	int ret = net_send_cli(fd, ret_buf, ret_pkg->len);
	if(dynamic_buf != NULL)
	{
		free(dynamic_buf);
		dynamic_buf = NULL;
	}
	return ret;
}
