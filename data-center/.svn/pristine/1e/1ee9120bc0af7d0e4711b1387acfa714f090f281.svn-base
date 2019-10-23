/**
 * =====================================================================================
 *       @file  stat_calc_custom.hpp
 *      @brief  
 *
 *     Created  2014-11-19 14:28:00
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#ifndef  STAT_CALC_CUSTOM_HPP
#define  STAT_CALC_CUSTOM_HPP

#include <stdint.h>
#include <stat_proto_defines.hpp>
#include <stat_main.hpp>
#include "c_mysql_connect_auto_ptr.h"
#include <string>

using std::string;

class StatCalcCustom : public IStatMain
{
    public:
        int init();
        int uninit();
        int get_server_pkg_len(const char *buf, uint32_t len);
        void timer_event();
        void process_client_pkg(int fd, const char *buf, uint32_t len);
        void process_server_pkg(int fd, const char *buf, uint32_t len);
        void client_connected(int fd, uint32_t ip);
        void client_disconnected(int fd);
        void server_disconnected(int fd);

    private:
        c_mysql_connect_auto_ptr mysql;
        string shell;
        string log_dir;

        void do_work(int);
};

#endif  /*STAT_CALC_CUSTOM_HPP*/
