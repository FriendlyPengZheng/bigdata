/**
 * =====================================================================================
 *       @file  stat_calc_custom.cpp
 *      @brief  
 *
 *     Created  2014-11-19 14:43:24
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#include "stat_calc_custom.hpp"
#include <stat_common.hpp>
#include <stat_config.hpp>
#include <sys/wait.h>

int
StatCalcCustom::init() {
    StatCommon::stat_config_get("calc_shell", shell);
    StatCommon::stat_config_get("log_dir", log_dir);

    string host, user, passwd, db;
    StatCommon::stat_config_get("db_config_host", host);
    StatCommon::stat_config_get("db_config_user", user);
    StatCommon::stat_config_get("db_config_passwd", passwd);
    StatCommon::stat_config_get("db_config_db", db);
    if(mysql.init(host.c_str(),
                user.c_str(), 
                passwd.c_str(),
                db.c_str(),
                StatCommon::stat_config_get("db_config_port", 3306), CLIENT_INTERACTIVE) == 0) {
        return 0;
    } else {
        ERROR_LOG("connect to mysql error\n");
        return -1;
    }
}

int
StatCalcCustom::uninit() {
    return 0;
}

int
StatCalcCustom::get_server_pkg_len(const char *buf, uint32_t len) {
    return 0;
}

void
StatCalcCustom::process_client_pkg(int fd, const char *buf, uint32_t len) {
    //TODO:干活
    // 1.查找，是否有对应的job需要做
    // 2.fork一个子进程，调用脚本
    const StatCalcCustomHeader * uf = (const StatCalcCustomHeader*)buf;
    if(uf->proto_id != PROTO_CALC_CUSTOM) {
        ERROR_LOG("error cmd id %x", uf->proto_id);
        return;
    }
    do_work(uf->file_id);
}

void
StatCalcCustom::do_work(int file_id) {
    MYSQL_ROW row;
    static char sql[1024];
    static char f[16];
    snprintf(sql, sizeof(sql), "select count(1) from t_web_file where file_id=%d and file_source = 1 and status = 0", file_id);
    int r = mysql.do_sql(sql);
    if(r != 0) {
        ERROR_LOG("do sql[%s] error: %s", sql, mysql.m_error());
    } else {
        if((row = mysql.get_next_row()) != NULL &&
                atoi(row[0]) == 1) {
            //fork
            int pid = fork();
            if(pid == 0) {
                snprintf(f, sizeof(f), "%d", file_id);
                static char pathname[1024];
                sprintf(pathname, "%s/%d.log", log_dir.c_str(), getpid());//m_job_log_path.c_str(), jobname);
                FILE *std_fp;
                if((std_fp = fopen(pathname,"w+")) == NULL){
                    exit(6);
                }
                if(dup2(fileno(std_fp),STDOUT_FILENO) == -1){
                    fclose(std_fp);
                    exit(5);
                }
                if(dup2(fileno(std_fp),STDERR_FILENO) == -1){
                    fclose(std_fp);
                    exit(5);
                }
                fclose(std_fp);
                execlp("php", shell.c_str(), shell.c_str(), f, (char*)0);
                ERROR_LOG("Could not open input file: %s", shell.c_str());
                exit(7);
            } else if(pid < 0) {
                ERROR_LOG("fork error");
            } else {
                int status, p;
                DEBUG_LOG("do %d", file_id);
                DEBUG_LOG("[%d]php %s %s", pid, shell.c_str(), f);
                if((p = wait(&status)) == pid) {
                    if(WIFEXITED(status)) {
                        if(WEXITSTATUS(status) == 0) {
                            DEBUG_LOG("%d return ok", pid);
                        } else {
                            ERROR_LOG("%d return %d", pid, WEXITSTATUS(status));
                        }
                    } else if(WIFSIGNALED(status)) {
                        ERROR_LOG("%d exit by signal %d%s", pid, WTERMSIG(status),
#ifdef WCOREDUMP
                                WCOREDUMP(status) ? " (core file generated)" : "");
#else
                        "");
#endif
                    }
                } else {
                    DEBUG_LOG("wait child %d return %d", pid, p);
                }
            }
        } else {
            ERROR_LOG("file=%d not found", file_id);
        }
    }
}

void
StatCalcCustom::process_server_pkg(int fd, const char *buf, uint32_t len) {
}

void
StatCalcCustom::client_connected(int fd, uint32_t ip) {
}

void
StatCalcCustom::client_disconnected(int fd) {
}

void
StatCalcCustom::server_disconnected(int fd) {
}

void
StatCalcCustom::timer_event() {
}
