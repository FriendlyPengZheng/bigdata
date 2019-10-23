/**
 * =====================================================================================
 *       @file  main.cpp
 *      @brief  
 *
 *     Created  2014-11-19 12:04:38
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#include <string>

#include <stat_proto_defines.hpp>
#include "stat_config.hpp"
#include "stat_calc_custom.hpp"
#include "statlog_control.hpp"

using std::string;

enum 
{
    // 切勿更改
    PROC_CONTROL,
    PROC_CALC_CUSTOM,
    PROC_END
};

static IStatMain* main = NULL;

extern "C" int plugin_init(int type)
{
    if(type == PROC_MAIN)
    {
        BOOT_LOG(0, "StatCalcCustom BuildTime: %s %s", __TIME__, __DATE__);

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
                DEBUG_LOG("StatCalcCustomControl (BuildTime: %s %s) initing...", __TIME__, __DATE__);
                set_title("stat-server-control");

                main = new (std::nothrow) StatLogControl();
                if(main == NULL || main->init() != 0)
                    return -1;

                DEBUG_LOG("StatCalcCustomControl init successfully!");
                break;
            case PROC_CALC_CUSTOM:
            default:
                DEBUG_LOG("StatCalcCustom (BuildTime: %s %s) initing...", __TIME__, __DATE__);
                set_title("stat-calc-custom-WORK");

                main = new (std::nothrow) StatCalcCustom();
                if(main == NULL || main->init() != 0)
                    return -1;

                DEBUG_LOG("StatCalcCustom init successfully!");

                break;
        }
    }

    return 0;
}

extern "C" int plugin_fini()
{
	DEBUG_LOG("StatCalcCustom finiting...");

    if(main)
    {
        main->uninit();
        delete main;
        main = NULL;
    }

	DEBUG_LOG("StatCalcCustom finit successfully!");
	return 0;
}

extern "C" int get_pkg_len_cli(const char *buf, uint32_t len) 
{
    if(len < sizeof(uint32_t))
        return 0;

    return *((uint32_t*)buf);
}

extern "C" int get_pkg_len_ser(const char *buf, uint32_t len) 
{
    return main->get_server_pkg_len(buf, len);
}

extern "C" int check_open_cli(uint32_t ip, uint16_t port) 
{
	return 0;
}

extern "C" int shmq_pushed(int fd, const char *buf, uint32_t len, int flag)
{
    if(flag == 0) // 将包push给worker进程时失败
    {
        //const StatLogLineHeader* pkg = (const StatLogLineHeader*)buf;
        //// 通知stat-client重发。
        //if(pkg->proto_id / 0x1000 == 1 || pkg->proto_id / 0x1000 == 2)
        //{
        //    StatLogLineRet ret;

        //    ret.len = sizeof(ret);
        //    ret.game_id = pkg->game_id;
        //    ret.proto_id = pkg->proto_id;
        //    ret.timestamp = pkg->timestamp;
        //    ret.ret = 1; 

        //    net_send_cli_conn(fd, &ret, sizeof(ret));
        //}
    }

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

extern "C" void link_up_cli(int fd, uint32_t ip)
{
    main->client_connected(fd, ip);
}

extern "C" void link_down_cli(int fd)
{
    main->client_disconnected(fd);
}

extern "C" void proc_pkg_ser(int fd, const char * buf, uint32_t len)
{

}
