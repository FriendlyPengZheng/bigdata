/*=========================================================================       
* 
*        Filename: main.cpp 
* 
*         Author: seasondi
*        Created:  2014-02-25 16:31:55 
* 
*        Description: tongji platform db-server module
* 
* ========================================================================= 
*/ 
#include <cstdlib>
#include <stdexcept>
#include <string>
#include <stdio.h>
#include <string.h>

#include "db_server.hpp"
#include "statlog_control.hpp"

static IStatMain* main = NULL;
extern "C" int plugin_init(int type)
{
    if(type == PROC_MAIN){
        BOOT_LOG(0, "db-server BuildTime: %s %s", __TIME__, __DATE__);
    }else if (type == PROC_WORK)  {
        if(get_work_num() < 2){
            BOOT_LOG(-1,"work num less than 2");
            return -1;
        }
        if(get_work_idx() == 0){
            DEBUG_LOG("stat-db-control BuildTime: %s %s", __TIME__, __DATE__);
            //Init connection to center control
            set_title("stat-db-control");
            main = new (std::nothrow)StatLogControl();
            if(main == NULL || main->init() != 0){
                return -1;
            }
            DEBUG_LOG("stat-db-control init successfully");
        }else{
            DEBUG_LOG("stat-db-server BuildTime: %s %s", __TIME__, __DATE__);
            set_title("stat-db-server");
            main = new (std::nothrow)StatDbServer();
            if(main == NULL || main->init() != 0){
                return -1;
            }
            DEBUG_LOG("stat-db-server init successfully");
        }
    }

    return 0;
}

extern "C" int plugin_fini(int type)
{
    if(main){
        main->uninit(); 
        delete main;
        main = NULL;
    }
    return 0;
}

static uint8_t work_index = 0;
extern "C" int select_channel(int fd, const char * buf, uint32_t len, uint32_t ip, uint32_t work_num)
{
    /**
     * no client send msg to center control process(work_index=0),
     * thus we ignore it when pkg comes,select db process one by one.
     */
    if(work_num < 2){
        return -1;
    }
    ++work_index;
    if(work_index > work_num - 1){
        work_index = 1;
    }
    return work_index;
}

extern "C" void time_event()
{
    main->timer_event(); 
}

extern "C" int get_pkg_len_cli(const char * buf, uint32_t len)
{
    if(len < sizeof(uint16_t))
    {
        return 0;
    }
    return *(uint16_t*)(buf);
}

extern "C" int get_pkg_len_ser(const char * buf, uint32_t len)
{
    return main->get_server_pkg_len(buf,len); 
}

extern "C" void proc_pkg_cli(int fd, const char * buf, uint32_t len)
{
    return main->process_client_pkg(fd,buf,len);
}

extern "C" void proc_pkg_ser(int fd, const char * buf, uint32_t len)
{
    main->process_server_pkg(fd,buf,len);
}

extern "C" void link_up_cli(int fd, uint32_t ip)
{
    main->client_connected(fd,ip);
}

extern "C" void link_down_cli(int fd)
{
    main->client_disconnected(fd);
}

extern "C" void link_down_ser(int fd)
{
    main->server_disconnected(fd);
}
