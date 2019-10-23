/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-client服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#include <string>

#include "statlog_reader.hpp"
#include "statlog_basic_sender.hpp"
#include "statlog_custom_sender.hpp"
#include "statlog_control.hpp"

using std::string;

enum 
{
    // 切勿更改sender的值
    PROC_BASIC_SENDER,
    PROC_CUSTOM_SENDER,
    PROC_CONTROL,
    PROC_READER,
    PROC_END
};

static IStatMain* main = NULL;

extern "C" int plugin_init(int type)
{
    if(type == PROC_MAIN)
    {
        BOOT_LOG(0, "StatClient BuildTime: %s %s", __TIME__, __DATE__);

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
            case PROC_READER:
                DEBUG_LOG("StatClientReader (BuildTime: %s %s) initing...", __TIME__, __DATE__);
                set_title("stat-client-reader");

                main = new (std::nothrow) StatLogReader();
                if(main == NULL || main->init() != 0)
                    return -1;

                DEBUG_LOG("StatClientReader init successfully!");

                break;
            case PROC_BASIC_SENDER:
                DEBUG_LOG("StatClientBasicSender (BuildTime: %s %s) initing...", __TIME__, __DATE__);
                set_title("stat-client-bsender");

                main = new (std::nothrow) StatLogBasicSender();
                if(main == NULL || main->init() != 0)
                    return -1;

                DEBUG_LOG("StatClientBasicSender init successfully!");

                break;
            case PROC_CUSTOM_SENDER:
                DEBUG_LOG("StatClientCustomSender (BuildTime: %s %s) initing...", __TIME__, __DATE__);
                set_title("stat-client-csender");

                main = new (std::nothrow) StatLogCustomSender();
                if(main == NULL || main->init() != 0)
                    return -1;

                DEBUG_LOG("StatClientCustomSender init successfully!");

                break;
            case PROC_CONTROL:
                DEBUG_LOG("StatClientControl (BuildTime: %s %s) initing...", __TIME__, __DATE__);
                set_title("stat-client-control");

                main = new (std::nothrow) StatLogControl();
                if(main == NULL || main->init() != 0)
                    return -1;

                DEBUG_LOG("StatClientControl init successfully!");
                break;
            default:
                return -1;
        }
    }

    return 0;
}

extern "C" int plugin_fini()
{
	DEBUG_LOG("StatClient finiting...");

    if(main)
    {
        main->uninit();
        delete main;
        main = NULL;
    }

	DEBUG_LOG("StatClient finit successfully!");
	return 0;
}

extern "C" int get_pkg_len_cli(const char *buf, uint32_t len) 
{
    return 0;
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
    return PROC_CONTROL;
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
