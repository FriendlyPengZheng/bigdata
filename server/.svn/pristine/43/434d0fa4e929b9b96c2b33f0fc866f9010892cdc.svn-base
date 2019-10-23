/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   淘米统计平台stat-server服务模块。
 *   @author  ianguo<ianguo@taomee.com>
 *   @date    2013-12-10
 * =====================================================================================
 */

#include <string>

#include <stat_proto_defines.hpp>
#include "statlog_cur_writer_proc.hpp"
#include "statlog_oth_writer_proc.hpp"
#include "statlog_basic_sender.hpp"
#include "statlog_custom_sender.hpp"
#include "statlog_control.hpp"
#include "stat_config.hpp"

using std::string;

enum 
{
    // 切勿更改sender的值
    PROC_BASIC_SENDER,
    PROC_CUSTOM_SENDER,
    PROC_CONTROL,
    PROC_CUR_WRITER,
    PROC_OTH_WRITER,
    PROC_END
};

static IStatMain* main = NULL;

extern "C" int plugin_init(int type)
{
    if(type == PROC_MAIN)
    {
        BOOT_LOG(0, "StatServer BuildTime: %s %s", __TIME__, __DATE__);

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
                DEBUG_LOG("StatServerControl (BuildTime: %s %s) initing...", __TIME__, __DATE__);
                set_title("stat-server-control");

                main = new (std::nothrow) StatLogControl();
                if(main == NULL || main->init() != 0)
                    return -1;

                DEBUG_LOG("StatServerControl init successfully!");
                break;
            case PROC_CUR_WRITER:
                DEBUG_LOG("StatServerCurWriter (BuildTime: %s %s) initing...", __TIME__, __DATE__);
                set_title("stat-server-curwriter");

                main = new (std::nothrow) StatLogCurWriterProc();
                if(main == NULL || main->init() != 0)
                    return -1;

                DEBUG_LOG("StatServerCurWriter init successfully!");

                break;
            case PROC_OTH_WRITER:
                DEBUG_LOG("StatServerOthWriter (BuildTime: %s %s) initing...", __TIME__, __DATE__);
                set_title("stat-server-othwriter");

                main = new (std::nothrow) StatLogOthWriterProc();
                if(main == NULL || main->init() != 0)
                    return -1;

                DEBUG_LOG("StatServerOthWriter init successfully!");

                break;
            case PROC_BASIC_SENDER:
                DEBUG_LOG("StatServerBasicSender (BuildTime: %s %s) initing...", __TIME__, __DATE__);
                set_title("stat-server-bsender");

                main = new (std::nothrow) StatLogBasicSender();
                if(main == NULL || main->init() != 0)
                    return -1;

                DEBUG_LOG("StatServerBasicSender init successfully!");

                break;
            case PROC_CUSTOM_SENDER:
                DEBUG_LOG("StatServerCustomSender (BuildTime: %s %s) initing...", __TIME__, __DATE__);
                set_title("stat-server-csender");

                main = new (std::nothrow) StatLogCustomSender();
                if(main == NULL || main->init() != 0)
                    return -1;

                DEBUG_LOG("StatServerCustomSender init successfully!");

                break;
            default:
                return -1;
        }
    }

    return 0;
}

extern "C" int plugin_fini()
{
	DEBUG_LOG("StatServer finiting...");

    if(main)
    {
        main->uninit();
        delete main;
        main = NULL;
    }

	DEBUG_LOG("StatServer finit successfully!");
	return 0;
}

extern "C" int get_pkg_len_cli(const char *buf, uint32_t len) 
{
    if(len < sizeof(uint32_t))
        return 0;

    const StatLogLineHeader* pkg = (const StatLogLineHeader*)buf;
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
    const StatProtoHeader *h = reinterpret_cast<const StatProtoHeader *>(buf);
    switch(h->proto_id/0x1000)
    {
        case 1: // 0x1000 ~ 0x1fff
            return PROC_CUR_WRITER;
        case 2: // 0x2000 ~ 0x2fff
            return PROC_OTH_WRITER;
        case 0xA: // 0xA000 ~ 0xAFFF
            return PROC_CONTROL;
        default:
            return -2; // unsurpported
    }
}

extern "C" int shmq_pushed(int fd, const char *buf, uint32_t len, int flag)
{
    if(flag == 0) // 将包push给worker进程时失败
    {
        const StatLogLineHeader* pkg = (const StatLogLineHeader*)buf;
        // 通知stat-client重发。
        if(pkg->proto_id / 0x1000 == 1 || pkg->proto_id / 0x1000 == 2)
        {
            StatLogLineRet ret;

            ret.len = sizeof(ret);
            ret.game_id = pkg->game_id;
            ret.proto_id = pkg->proto_id;
            ret.timestamp = pkg->timestamp;
            ret.ret = 1; 

            net_send_cli_conn(fd, &ret, sizeof(ret));
        }
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
