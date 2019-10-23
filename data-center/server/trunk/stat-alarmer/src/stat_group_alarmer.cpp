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

#include "stat_common.hpp"
#include "stat_alarmer_defines.hpp"
#include "stat_alarmer_handler.hpp"
#include "stat_group_alarmer.hpp"
#include "proto/app_login_request.pb.h"

int StatGroupAlarmer::proc_proto(int fd, const void* pkg)
{
    StatAlarmerProto::StatAlarmRequest req;
    const StatAlarmerHeader* h = static_cast<const StatAlarmerHeader*>(pkg);
    req.ParseFromArray(h->body, h->len - sizeof(StatAlarmerHeader));
    uint8_t group_id = (uint8_t )req.group_id();

    list<StatAlarmerHandler*>::iterator it;
	int ret = 0;
    for(it = m_alarmers.begin(); it != m_alarmers.end(); ++it)
    {
        if((*it)->get_group_id() >= group_id)
        {
            if((*it)->do_alarm(req) != 0)
			{
				ret = 1;
			}
        }
    }
    StatAlarmerProto::StatAlarmResponse res;
    res.set_ret(ret);

    const uint32_t buf_len = 64;
    uint8_t ret_buf[buf_len] = {0}; // hard code
    StatAlarmerHeader* ret_pkg = (StatAlarmerHeader*)ret_buf;
    ret_pkg->len = sizeof(StatAlarmerHeader);

    int body_len = res.ByteSize();
    if(sizeof(StatAlarmerHeader) + body_len > buf_len)
    {
        ERROR_LOG("buffer is not enough when sending StatAlarmResponse, need %lu but buffer size is %d", 
                sizeof(StatAlarmerHeader) + body_len, buf_len);
        goto DONE;
    }

    ret_pkg->len += body_len;

    res.SerializeToArray(ret_pkg->body, body_len);

DONE:
    return net_send_cli(fd, ret_buf, ret_pkg->len);
}
