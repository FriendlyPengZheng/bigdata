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
#include "stat_alarmer_handler.hpp"
#include "stat_common.hpp"
#include "stat_alarmer_defines.hpp"

int StatAlarmerHandler::proc_proto(int fd, const void* pkg)
{
    StatAlarmerProto::StatAlarmRequest req;
    const StatAlarmerHeader* h = static_cast<const StatAlarmerHeader*>(pkg);
    req.ParseFromArray(h->body, h->len - sizeof(StatAlarmerHeader));

    uint8_t ret = send_alarm(req);
    if(ret == 0)
    {
        save_alarm(req);
    }

    return 0;
}

int StatAlarmerHandler::save_alarm(const StatAlarmerProto::StatAlarmRequest& req)
{
    if(!req.has_title() || ! req.has_content())
        return -1;

    string receiver;
    for(int i = 0; i < req.send_to_size(); ++i)
    {
        const StatAlarmerProto::AlarmContact& contact = req.send_to(i);

        receiver += contact.name(); 
        receiver += ":";
        receiver += contact.email();
        receiver += ",";
        receiver += contact.mobile();
        receiver += ";";
    }
    for(int i = 0; i < req.send_cc_size(); ++i)
    {
        const StatAlarmerProto::AlarmContact& contact = req.send_cc(i);

        receiver += contact.name(); 
        receiver += ":";
        receiver += contact.email();
        receiver += ",";
        receiver += contact.mobile();
        receiver += ";";
    }
	if(receiver.size() > 0)
	{
		receiver.erase(receiver.size() - 1, 1); // remove last ','
	}

    // TODO: save alarm to db or file.
    
    INFO_LOG("alarm sent, title: %s, content: %s, receiver: %s", 
            req.title().c_str(), req.content().c_str(), receiver.c_str());

    return 0;
}
