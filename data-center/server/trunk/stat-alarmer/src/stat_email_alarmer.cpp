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

#include <stat_config.hpp>
#include "stat_email_alarmer.hpp"

StatEmailAlarmer::StatEmailAlarmer(uint32_t proto_id, const char* proto_name, StatGroupAlarmer* sag)
        : StatAlarmerHandler(proto_id, proto_name, STAT_ALARMER_GROUP_EMAIL, sag)
{
	StatMailSender::SmtpHeader smtp_header;
	StatCommon::stat_config_get("email_sender", smtp_header.h_sender);
	StatCommon::stat_config_get("email_host", smtp_header.h_host);
	m_mail_sender.init(smtp_header);
}

uint8_t StatEmailAlarmer::send_alarm(const StatAlarmerProto::StatAlarmRequest& req) 
{
    DEBUG_LOG("send with email.title:%s content:%s",
			req.title().c_str(), req.content().c_str());

	//获取收件人列表
	vector<string> send_to;
    for(int i = 0; i < req.send_to_size(); ++i)
    {
        const StatAlarmerProto::AlarmContact& contact = req.send_to(i);

		//DEBUG_LOG("send with email i:%d email:%s.", i, contact.email().c_str());
        send_to.push_back(contact.email());
    }

	//获取抄送人列表
	vector<string> send_cc;
    for(int i = 0; i < req.send_cc_size(); ++i)
    {
        const StatAlarmerProto::AlarmContact& contact = req.send_cc(i);

        send_cc.push_back(contact.email());
    }

	int ret = m_mail_sender.send_mail(send_to, send_cc, req.title(), req.content());
	if(ret > 0)
	{
		return 0;
	}
	else
	{
		ERROR_LOG("send email msg failed.ret:%d", ret);
		return 1;
	}
}
