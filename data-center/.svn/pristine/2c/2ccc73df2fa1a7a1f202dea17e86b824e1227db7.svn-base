/**
 * =====================================================================================
 *       @file  sdk_server.cpp
 *      @brief  
 *
 *     Created  2014-06-24 17:41:53
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#include "config.h"
#include "global.h"
#include "log.h"
#include "tcp_writer.hpp"
#include <sdk_server.hpp>
#include <time.h>
#include <sys/time.h>

bool SubTimeval(timeval &result, timeval &begin, timeval &end)
// 计算gettimeofday函数获得的end减begin的时间差，并将结果保存在result中。
{
    if ( begin.tv_sec>end.tv_sec ) return false;

    if ( (begin.tv_sec == end.tv_sec) && (begin.tv_usec > end.tv_usec) )   
        return   false;

    result.tv_sec = ( end.tv_sec - begin.tv_sec );   
    result.tv_usec = ( end.tv_usec - begin.tv_usec );   

    if (result.tv_usec<0) {
        result.tv_sec--;
        result.tv_usec+=1000000;}  

        return true;
}

timeval tBegin, tEnd, tDiff;

StatSdkServer::StatSdkServer() {
    db.init();
    inited = false;
}

StatSdkServer::~StatSdkServer() {
    if(inited) {
        uninit();
    }
}

int StatSdkServer::init() {
    //../conf/configure.ini
    c_mysql_connect_auto_ptr config;
    db_info_t db_info[100];
    if(load_config_file("./conf/configure.conf")) {
        if(config.init(config_get_strval("db_config_host", "192.168.71.76"),
                    config_get_strval("db_config_user", "srvmgr"),
                    config_get_strval("db_config_passwd", "srvmgr@pwd"),
                    config_get_strval("db_config_db", "db_td_config"),
                    config_get_intval("db_config_port", 3306), CLIENT_INTERACTIVE) == 0) {
            char sql_buf[4096];
            snprintf(sql_buf, sizeof(sql_buf), "select db_id, db_host, db_user, db_pwd, db_name, db_port from t_db_info");
            if(config.do_sql(sql_buf) == 0) {
                MYSQL_ROW row;
                uint32_t db_info_index = 0;
                uint32_t j = 0;
                while((row = config.get_next_row()) != NULL) {
                    for(j=0; j<db_info_index; j++) {
                        if(strcmp(db_info[j].db_host, row[1]) == 0 &&
                                strcmp(db_info[j].db_user, row[2]) == 0 &&
                                strcmp(db_info[j].db_pwd, row[3]) == 0 &&
                                db_info[j].db_port == atol(row[5]))
                            break;
                    }
                    if(j == db_info_index) {
                        if(db.insert(atol(row[0]), row[1], row[2], row[3], row[4], atol(row[5]), CLIENT_INTERACTIVE) == 0) {
                            strcpy(db_info[j].db_host, row[1]);
                            strcpy(db_info[j].db_user, row[2]);
                            strcpy(db_info[j].db_pwd, row[3]);
                            db_info[j].db_port = atol(row[5]);
                            db_info[j].p_mc = db.get(atol(row[0]));
                            db_info_index++;
                        } else { //db.insert
                            ERROR_LOG("insert %s failed", row[4]);
                            return 4;
                        }
                    } else { // f==db_info_index
                        db.insert(atol(row[0]), db_info[j].p_mc);
                    }
                }
            } else { //do_sql
                ERROR_LOG("db_report info return failed");
                return 3;
            }
        } else { //config.init
            ERROR_LOG("db_stat_config.init() failed");
            return 2;
        }
    } else { //load_config_file
        ERROR_LOG("load config file");
        return 1;
    }

    config.uninit();
    inited = true;
    return 0;
}

int StatSdkServer::uninit() {
    db.uninit();
    inited = false;
    return 0;
}

int StatSdkServer::get_client_pkg_len(const char *buf, uint32_t len) {
    if(len < 4) {
        return 0;
    }
    return *(int*)buf;
}

int StatSdkServer::get_server_pkg_len(const char *buf, uint32_t len) {
    if(len < 4) {
        return 0;
    }
    return *(int*)buf;
}

void StatSdkServer::process_client_pkg(int fd, const char *buf, uint32_t len) {
    DEBUG_LOG("recv %u bytes", len);
    pkg_ret_body_t* ret_buf;
    uint32_t total_len = 0;
    uint32_t data_len = 0;
    uint32_t ret;
    uint32_t start;
    uint32_t end;// = pkg_recv->header.end;
    uint32_t linecnt;// = pkg_recv->header.linecnt;
    uint32_t i;
    int r;

    //解包
    //gettimeofday(&tBegin, 0);
    pkg_recv_t* pkg_recv = (pkg_recv_t*)buf;
    if(pkg_recv->header.cmd != ONLINE_CMD) {
        ret = ERROR_CMD;
        goto error;
    }

    start = pkg_recv->header.start;
    end = pkg_recv->header.end;
    linecnt = pkg_recv->header.linecnt;
    if(start % 86400 != 57600) {
        ret = ERROR_START;
        goto error;
    }
    if(end - start >= 86400) {
        ret = ERROR_END;
        goto error;
    }
    if(linecnt > MAX_LINE) {
        ret = ERROR_CNT;
        goto error;
    }

    //gettimeofday(&tEnd, 0);
    //SubTimeval(tDiff, tBegin, tEnd);
    //DEBUG_LOG("unpack %lu %f", tDiff.tv_sec, tDiff.tv_usec / 1000.0); 

    send_pkg[0].iov_base = (void*)(&pkg_recv->header);
    send_pkg[0].iov_len  = sizeof(pkg_recv->header);
    total_len += sizeof(pkg_recv->header);

    for(i=0; i<pkg_recv->header.linecnt; i++) {
        //gettimeofday(&tBegin, 0);
        datainfo_t* datainfo = &pkg_recv->datainfo[i];
        DEBUG_LOG("line %u : data=%u gpzs=%u", i, datainfo->dataid, datainfo->gpzsid);

        if((ret_buf = getDayData(datainfo->dataid, datainfo->sthash, datainfo->gpzsid, start, end)) == 0) {
            ret = ERROR_GET;
            goto error;
        }

        data_len = sizeof(datainfo_t) + sizeof(ret_buf->data.cnt) + sizeof(tv_t) * ret_buf->data.cnt;
        total_len += data_len;
        send_pkg[i+1].iov_base = ret_buf;
        send_pkg[i+1].iov_len  = data_len;

        //gettimeofday(&tEnd, 0);
        //SubTimeval(tDiff, tBegin, tEnd);
        //DEBUG_LOG("get line %lu %f", tDiff.tv_sec, tDiff.tv_usec / 1000.0); 
    }

    //gettimeofday(&tBegin, 0);

    pkg_recv->header.pkg_len = total_len;
    pkg_recv->header.error = 0;

    //gettimeofday(&tEnd, 0);
    //SubTimeval(tDiff, tBegin, tEnd);
    //DEBUG_LOG("pack %lu %f", tDiff.tv_sec, tDiff.tv_usec / 1000.0); 

    //gettimeofday(&tBegin, 0);
    //net_send_cli(fd, data_buf, swap32(ret_pkg->pkg_len));
    //DEBUG_LOG("head len = %u, pkg len = %u", send_pkg[0].iov_len, total_len);
    r = net_send_cli(fd, send_pkg[0].iov_base, send_pkg[0].iov_len);
    //DEBUG_LOG("send %d bytes", r);
    print((pkg_header_t*)send_pkg[0].iov_base);
    for(uint32_t ii=0; ii<i; ii++) {
        //DEBUG_LOG("line %u len = %u", ii, send_pkg[ii+1].iov_len);
        print((pkg_ret_body_t*)send_pkg[ii+1].iov_base);
        r = net_send_cli(fd, send_pkg[ii+1].iov_base, send_pkg[ii+1].iov_len);
        //DEBUG_LOG("send %d bytes", r);
    }
    //TcpWriter::writev(fd, send_pkg, i + 1);
    //gettimeofday(&tEnd, 0);
    //SubTimeval(tDiff, tBegin, tEnd);
    //DEBUG_LOG("send %lu %f", tDiff.tv_sec, tDiff.tv_usec / 1000.0); 
    return ;

error:
    ERROR_LOG("%u", ret);
    pkg_recv->header.pkg_len = sizeof(pkg_header_t);
    pkg_recv->header.error = ret;
    pkg_recv->header.linecnt = 0;
    net_send_cli(fd, pkg_recv, sizeof(pkg_header_t));
}

pkg_ret_body_t* StatSdkServer::getDayDataFromCache(uint32_t data_id, uint32_t sthash, uint32_t gpzs_id, uint32_t start, uint32_t end) {
    char key[64];
    getCacheKey(data_id, gpzs_id, start, end, key);
    pkg_ret_body_t* ret = (pkg_ret_body_t*)cache.search(key);
    return ret;
}

pkg_ret_body_t* StatSdkServer::getDayData(uint32_t data_id, uint32_t sthash, uint32_t gpzs_id, uint32_t start, uint32_t end) {
    pkg_ret_body_t* cache_data = getDayDataFromCache(data_id, sthash, gpzs_id, start, end);
    int ret;
    if(cache_data != NULL) {//get data from cache
        //DEBUG_LOG("1 => get from cache %u", cache_data->data.cnt);
        //获得当前分钟第0秒的时间戳
        uint32_t now = time(NULL);
        now -= (now % 60);
        //uint32_t get_start = cache_data->data[0].time;
        uint32_t get_end;
        if(cache_data->data.cnt <= 10) {
            get_end = start - 60;
        } else {
            get_end = cache_data->data.data[cache_data->data.cnt-9].time;
            cache_data->data.cnt -= 8;
        }
        if(start <= now && now < end) {//当天数据更新最后8分钟
            ret = getDayDataFromSql(data_id, sthash, gpzs_id, get_end + 60, now, &cache_data->data.data[cache_data->data.cnt]);
            //DEBUG_LOG("2 => get today %u %u", get_end + 60, now);
        } else if((now + 28800) % 86400 < 25200 && start + 86400 <= now && now < end + 86400) {//当天7点以前拉前一天数据更新最后8分钟
            ret = getDayDataFromSql(data_id, sthash, gpzs_id, get_end + 60, end, &cache_data->data.data[cache_data->data.cnt]);
            //DEBUG_LOG("2 => get other day %u %u", get_end + 60, end);
        } else {
            if(cache_data->data.cnt > 10) {
                cache_data->data.cnt += 8;
            }
            ret = 0;
        }
        if(ret < 0) {
            //ERROR_LOG("sql return %d", ret);
            return NULL;
        }
        cache_data->data.cnt += ret;
        return cache_data;
    } else {//no cache
        //DEBUG_LOG("1 => get from sql");
        ret = getDayDataFromSql(data_id, sthash, gpzs_id, start, end, day_data.data.data);
        if(ret < 0) {
            ERROR_LOG("sql return %d", ret);
            return NULL;
        }

        day_data.datainfo.dataid = data_id;
        day_data.datainfo.sthash = sthash;
        day_data.datainfo.gpzsid = gpzs_id;
        day_data.data.cnt = (uint16_t)ret;
        insertIntoCache(data_id, gpzs_id, start, end, &day_data);
        return getDayDataFromCache(data_id, sthash, gpzs_id, start, end);
    }
}

void StatSdkServer::insertIntoCache(uint32_t data_id, uint32_t gpzs_id, uint32_t start, uint32_t end, pkg_ret_body_t* buf) {
    char key[64];
    getCacheKey(data_id, gpzs_id, start, end, key);
    cache.insert(sizeof(pkg_ret_body_t), buf, key);
}

void StatSdkServer::getCacheKey(uint32_t data_id, uint32_t gpzs_id, uint32_t start, uint32_t end, char* key) {
    sprintf(key, "%u_%u_%u", data_id, gpzs_id, start);
}

int StatSdkServer::getDayDataFromSql(uint32_t data_id, uint32_t sthash, uint32_t gpzs_id, uint32_t start, uint32_t end, tv_t* buf) {
    uint32_t db_id = (sthash/100)%100;
    c_mysql_connect_auto_ptr* mysql = db.get(db_id);
    if(mysql == NULL)   return -1;
    char sql[4096];
    snprintf(sql, sizeof(sql), "select time, value from db_td_data_%u.t_db_data_minute_%u where data_id=%u and gpzs_id=%u and time between %u and %u order by time", db_id, sthash%100, data_id, gpzs_id, start, end);
    if(mysql->do_sql(sql) != 0) return -2;
    MYSQL_ROW row;
    int cnt = 0;
    while((row = mysql->get_next_row()) != NULL) {
        buf[cnt].time = atol(row[0]);
        buf[cnt].value = atof(row[1]);
        cnt++;
    }
    return cnt;
}

void StatSdkServer::timer_event() { }

void StatSdkServer::client_connected(int fd, uint32_t ip) { }

void StatSdkServer::client_disconnected(int fd) { }

void StatSdkServer::server_disconnected(int fd) { }

void StatSdkServer::process_server_pkg(int fd, const char *buf, uint32_t len) { }

void StatSdkServer::print(pkg_header_t* buf) {
    DEBUG_LOG("len = %u bytes", buf->pkg_len);
    DEBUG_LOG("cmd = 0x%08x", buf->cmd);
    DEBUG_LOG("ver = %u", buf->version);
    DEBUG_LOG("err = %u", buf->error);
    DEBUG_LOG("srt = %u", buf->start);
    DEBUG_LOG("end = %u", buf->end);
    DEBUG_LOG("cnt = %u", buf->linecnt);
}

void StatSdkServer::print(pkg_ret_body_t* buf) {
    print(&buf->datainfo);
    print(&buf->data);
}

void StatSdkServer::print(datainfo_t* buf) {
    DEBUG_LOG("\tdata = %u", buf->dataid);
    DEBUG_LOG("\thash = %u", buf->sthash);
    DEBUG_LOG("\tgpzs = %u", buf->gpzsid);
}

void StatSdkServer::print(dv_t* buf) {
    DEBUG_LOG("\tcnt  = %u", buf->cnt);
    //for(uint32_t i=0; i<buf->cnt; i++) {
    //    DEBUG_LOG("\t\ttime = %u, value = %f", buf->data[i].time, buf->data[i].value);
    //}
}
