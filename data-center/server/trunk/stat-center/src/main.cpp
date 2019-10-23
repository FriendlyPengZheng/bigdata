/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-center服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#include <string>

#include "stat_proto_defines.hpp"
#include "statcenter_control.hpp"
#include "stat_config.hpp"

using std::string;

enum 
{
    PROC_CONTROL,
    PROC_END
};

static IStatMain* main = NULL;

extern "C" int plugin_init(int type)
{
    if(type == PROC_MAIN)
    {
        BOOT_LOG(0, "StatCenter BuildTime: %s %s", __TIME__, __DATE__);

        // 目前限制worker进程个数。
        // 如遇到压力大，可去掉该限制。
        if(get_work_num() != PROC_END)
        {
            ERROR_LOG("work process number must be %d.", PROC_END);
            return -1;
        }
    }
    else if(type == PROC_WORK)
    {
        switch(get_work_idx())
        {
            case PROC_CONTROL:
                DEBUG_LOG("StatCenterControl (BuildTime: %s %s) initing...", __TIME__, __DATE__);
                set_title("stat-center-control");

                main = new (std::nothrow) StatCenterControl();
                if(main == NULL || main->init() != 0)
                    return -1;

                DEBUG_LOG("StatCenterControl init successfully!");
                break;
            default:
                return -1;
        }
    }

    return 0;
}

extern "C" int plugin_fini()
{
	DEBUG_LOG("StatCenter finiting...");

    if(main)
    {
        main->uninit();
        delete main;
        main = NULL;
    }

	DEBUG_LOG("StatCenter finit successfully!");
	return 0;
}

extern "C" int get_pkg_len_cli(const char *buf, uint32_t len) 
{
    const StatProtoHeader* pkg = (const StatProtoHeader*)buf;

    if(len < sizeof(pkg->len))
        return 0;

    return pkg->len;
}

extern "C" int get_pkg_len_ser(const char *buf, uint32_t len) 
{
    return main->get_server_pkg_len(buf, len);
}

extern "C" int check_open_cli(uint32_t ip, uint16_t port) 
{
	return 0;
}

extern "C" int select_channel(int fd, const char *buf, uint32_t len, uint32_t ip, uint32_t work_num) 
{
    // 目前只有一个worker进程，返回0即可。
    // 如支持多个worker进程，可按ip分配。
    // 按ip分配后，将心跳数据持久化将变得困难。
    //return ip % work_num;
    return 0;
}

extern "C" int shmq_pushed(int fd, const char *buf, uint32_t len, int flag)
{
    return 0;
}
 
extern "C" void time_event()
{
    main->timer_event();
}

extern "C" void proc_pkg_cli(int fd, const char *buf, uint32_t len)
{
    main->process_client_pkg(fd, buf, len);
}

extern "C" void proc_pkg_ser(int fd, const char *buf, uint32_t len)
{
    main->process_server_pkg(fd, buf, len);
}

extern "C" void link_up_cli(int fd, uint32_t ip)
{
    main->client_connected(fd, ip);
}

extern "C" void link_down_cli(int fd)
{
    main->client_disconnected(fd);
}

extern "C" void link_down_ser(int fd)
{
    main->server_disconnected(fd);
}
