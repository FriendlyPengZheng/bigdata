/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-alarmer服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#include <cerrno>
#include <cstring>
#include <sstream>
#include <utility>
#include <vector>

#include <fcntl.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>

#include <string_utils.hpp>
#include <fs_utils.hpp>
#include <stat_config.hpp>

#include "stat_proto_handler.hpp"
#include "stat_proto_defines.hpp"
#include "stat_alarmer.hpp"
#include "stat_group_alarmer.hpp"
#include "stat_rtx_alarmer.hpp"
#include "stat_mobile_alarmer.hpp"
#include "stat_weixin_alarmer.hpp"
#include "stat_email_alarmer.hpp"
#include "stat_app_push_alarmer.hpp"
#include "stat_app_push_alarm_fetcher.hpp"
#include "stat_app_push_alarm_register.hpp"
#include "stat_app_push_alarm_unregister.hpp"

#include "stat_alarmer_defines.hpp"
#include "proto/alarm_request.pb.h"

#include "data_buffer.hpp"

extern int g_stop;

/**
 * 协议处理有两种定义方式：
 * 1. 在匿名空间内定义全局变量，适合自动处理，不需要代码访问的情况。
 * 2. 在函数内定义静态变量，适合需要代码访问的情况。
 */
namespace // 匿名空间，隐藏全局变量
{
    StatGroupAlarmer stat_group_alarmer(STAT_ALARMER_PROTO_GROUP, "StatGroupAlarmer");

    StatRtxAlarmer stat_rtx_alarmer(STAT_ALARMER_PROTO_RTX, "StatRtxAlarmer", &stat_group_alarmer);
    StatMobileAlarmer stat_mobile_alarmer(STAT_ALARMER_PROTO_MOBILE, "StatMobileAlarmer", &stat_group_alarmer);
    StatEmailAlarmer stat_email_alarmer(STAT_ALARMER_PROTO_EMAIL, "StatEmailAlarmer", &stat_group_alarmer);
    StatAppPushAlarmer stat_app_push_alarmer(STAT_ALARMER_PROTO_APPPUSH, "StatAppPushAlarmer", &stat_group_alarmer, &stat_mobile_alarmer);
    StatWeixinAlarmer stat_weixin_alarmer(STAT_ALARMER_PROTO_WEIXIN, "StatWeixinAlarmer", &stat_group_alarmer);

    StatAppPushAlarmFetcher stat_app_push_alarm_fetcher(STAT_ALARMER_PROTO_APPPUSH_FETCH, "StatAppPushAlarmFetcher", &stat_app_push_alarmer);
    StatAppPushAlarmRegister stat_app_push_alarm_register(STAT_ALARMER_PROTO_APPPUSH_REGISTER, "StatAppPushAlarmRegister", &stat_app_push_alarmer);
    StatAppPushAlarmUnregister stat_app_push_alarm_unregister(STAT_ALARMER_PROTO_APPPUSH_UNREGISTER, "StatAppPushAlarmUnregister", &stat_app_push_alarmer);
}

//--------------------------------------
// Public Methods
//--------------------------------------
StatAlarmer::StatAlarmer() : m_archiver(NULL)
{
}

StatAlarmer::~StatAlarmer()
{
    uninit();
}

int StatAlarmer::init()
{
    StatCommon::stat_config_get("work-path", m_work_path);
    char pwd[512] = {'\0'};
    getcwd(pwd, sizeof(pwd)/sizeof(char) - 1);
    m_cwd.assign(pwd);

    if(m_work_path.empty() || m_cwd.empty())
    {
        ERROR_LOG("work-path not found: %s. or getcwd failed.", m_work_path.c_str());
        return -1;
    }

    DEBUG_LOG("work-path: %s", m_work_path.c_str());
    DEBUG_LOG("process working path: %s", m_cwd.c_str());

    StatCommon::makedir(m_work_path);

    if(m_archiver == NULL)
    {
        m_archiver = new (std::nothrow) StatLogArchive(m_work_path + "/sent", "basic", "custom", "statlog_sent");
        if(m_archiver == NULL)
        {
            ERROR_LOG("new StatLogArchive failed.");
            return -1;
        }
    }

    StatProtoHandler::print_supported_proto();

    DataBuffer::init();

    // read threadnum , init threadpool
    int thread_num = StatCommon::stat_config_get("thread-num", 20);
    m_threadpool.init(thread_num);

    return 0;
}

int StatAlarmer::uninit()
{
    if(m_archiver)
    {
        delete m_archiver;
        m_archiver = NULL;
    }

    if(stat_app_push_alarmer.uninit() < 0)
	{
        ERROR_LOG("stat_app_push_alarmer uninit failed.");
        return -1;
	}

    m_threadpool.uninit();

    return 0;
}

int StatAlarmer::get_client_pkg_len(const char *buf, uint32_t len)
{
    return 0;
}

int StatAlarmer::get_server_pkg_len(const char *buf, uint32_t len)
{
    return 0;
}

void StatAlarmer::timer_event()
{
    time_t now = time(0);

    // 每天固定时间清理目录，时间写死，不支持配置。
    const unsigned archive_reserve = 30 * 24 * 3600;
    if((now + (8 * 60 * 60)) % (24 * 3600) == 3600) // 凌晨1:00
    {
        m_archiver->add_clear_path(m_cwd + "/log");
        m_archiver->rm_archive(archive_reserve); // 清理框架的log。
    }
	StatProtoHandler::timer_event();
}

void StatAlarmer::process_client_pkg(int fd, const char *buf, uint32_t len)
{
    //  给stat-center返回
    // StatAlarmerProto::StatAlarmRequest req;
    // //const void* temp_buf = static_cast<const void*>
    // const StatAlarmerHeader* h = static_cast<const StatAlarmerHeader*>(static_cast<const void*>(buf));
    // req.ParseFromArray(h->body, h->len - sizeof(StatAlarmerHeader));

    uint8_t ret = 0;

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
    net_send_cli(fd, ret_buf, ret_pkg->len);

    TaskData task_data(len, buf);
    m_threadpool.add_task(task_data);
}

void StatAlarmer::process_server_pkg(int fd, const char *buf, uint32_t len)
{
}

void StatAlarmer::client_connected(int fd, uint32_t ip)
{
}

void StatAlarmer::client_disconnected(int fd)
{
}

void StatAlarmer::server_disconnected(int fd)
{
}
