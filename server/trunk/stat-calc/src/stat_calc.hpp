/**
 * =====================================================================================
 *       @file  stat_calc.hpp
 *      @brief  
 *
 *     Created  2014-09-25 20:13:26
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#ifndef  STAT_CALC_HPP
#define  STAT_CALC_HPP

#include <stdint.h>
#include "stat_process.hpp"
#include "c_mysql_connect_auto_ptr.h"
#include <map>

#include "stat_calc_config.hpp"
#include "stat_mr_generate.hpp"
#include "stat_shphp_generate.hpp"

class StatCalc {
    public:
        StatCalc(const char* file_name = "../conf/stat-calc.conf");
        ~StatCalc();
        void init();
        int run(int args, char* argv[]);
        static void print_usage();

    private:
        StatProcess* process;

        int doJobById(uint32_t id);
        void jobExitOK(pid_t);
        void jobExitErr(pid_t, int32_t);
        void getJobName(uint32_t, char*);
        void dup2logfile(const char* filename);

        void init_alarm_ip_port();
        void alarm(int status, string& job_name) const;

        void runProcess(int, int, int, int*, int);
        void runPrint(int, int*, int);

	private:
		c_mysql_connect_auto_ptr mysql;

        string m_alarm_ip;
        string m_alarm_port;
        string m_job_log_path;
        string m_pwd;
        string m_db_host;
        string m_db_name;
        string m_db_user;
        string m_db_passwd;
        int m_db_port;
        uint32_t m_local_ip;
        uint32_t m_max_try_again_count;

        StatCalcConfig m_stat_calc_config;
        StatMrGenerate m_mr_gen;
        StatShphpGenerate m_shell_gen;
        StatShphpGenerate m_php_gen;

        char date[16];

        std::map<uint32_t, string> job_name_map;
        std::map<pid_t, uint32_t> pid_set;
        std::map<pid_t, uint32_t> job_try_again_count;
};

#endif  /*STAT_CALC_HPP*/
