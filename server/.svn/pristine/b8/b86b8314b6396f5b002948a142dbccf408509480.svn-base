#include <iostream>
#include <iomanip>

#include <unistd.h>

#include <async_server.h>
#include <log.h>
#include <i_timer.h>
#include <i_config.h>
#include <stat_protocal.h>

#include "stat_mysql.h"

using std::cout;
using std::endl;
using std::setw;
using std::left;

static const char *g_red_clr = "\e[1m\e[31m";
//static const char *g_grn_clr = "\e[1m\e[32m";
static const char *g_end_clr = "\e[m";

static i_config *g_p_config = NULL;
static i_timer  *g_p_timer = NULL;

// 配置文件列表
static const char g_config_file_list[][PATH_MAX] = {
    "../conf/stat_mysql.ini"
};

// 配置文件个数
static const int g_config_file_count = sizeof(g_config_file_list) / sizeof(*g_config_file_list);

extern "C" int plugin_init(int type)
{
    DEBUG_LOG("stat_mysql.so INIT...");

    if (type == PROC_MAIN)// 主进程
    { 
        int ret = 0;
        do
        {
            if (create_config_instance(&g_p_config) != 0)
            {
                ERROR_LOG("create config instance failed.");
                ret = -1;
                break;
            }

            // 初始化配置接口
            if (g_p_config->init(g_config_file_list, g_config_file_count) != 0)
            {
                ERROR_LOG("init config instance failed.");
                if (g_p_config != NULL)
                {
                    g_p_config->release();
                }

                ret = -1;
                break;
            }

            if (create_timer_instance(&g_p_timer) != 0)
            {
                ERROR_LOG("create timer instance failed.");
                ret = -1;
                break;
            }

            if(g_p_timer->init() != 0)
            {
                ERROR_LOG("init timer instance failed.");
                if (g_p_timer != NULL) 
                {
                    g_p_timer->release();
                }

                ret = -1;
                break;
            }
        }
        while(0);

        if(-1 == ret)
        {
            cout << setw(70) << left << "start stat_mysql, create/init config/timer."
                << g_red_clr << "[ failed ]" << g_end_clr << endl;
        }

        return ret;
    }
    else if (type == PROC_WORK)// 工作进程 
    { 
        if(stat_mysql_init(g_p_timer, g_p_config) != 0)
        {
            cout << setw(70) << left << "init stat_mysql.so "
                 << g_red_clr << "[ failed ]" << g_end_clr << endl;
            return -1;
        }
    } 
    else if (type == PROC_CONN) // 网络进程
    {
        // do nothing
    }

    return 0;
}

extern "C" int plugin_fini(int type)
{
    DEBUG_LOG("FINI...");
    if (type == PROC_WORK) {
        stat_mysql_uninit(); 
    }

    return 0;
}

extern "C" void time_event()
{
    g_p_timer->check();
}

extern "C" int get_pkg_len_cli(const char *buf, uint32_t len)
{
    if (len < sizeof(uint32_t))
        return 0;

    return *(uint32_t *)buf;
}

extern "C" int get_pkg_len_ser(const char *buf, uint32_t len)
{
    return -1;
}

extern "C" void proc_pkg_cli(int fd, const char *buf, uint32_t len)
{
    server_db_request_t *req = (server_db_request_t *)buf;

    int ret = stat_mysql_process(req);

    server_db_response_t res;
    res.len = sizeof(res);
    res.ret = ret;

    net_send_cli(fd, (const char *)&res, sizeof(res));
}

extern "C" void proc_pkg_ser(int fd, const char *buf, uint32_t len)
{
}

extern "C" int select_channel(int fd, const char *buf, uint32_t len, uint32_t ip, uint32_t work_num)
{
    static uint32_t index = 0;

    index = (index + 1) % work_num;

    return index;
}

extern "C" int shmq_pushed(int fd, const char *buf, uint32_t len, int flag)
{
    return 0;
}

