#include <sys/time.h>
#include "c_mysql_operator.h"
#include "log.h"
#include "proto.h"
#include "util.h"
#include <stdlib.h>
#include <time.h>
#include <stdio.h>

typedef struct {
    char     db_host[16];
    char     db_user[64];
    char     db_pwd[64];
    uint32_t db_port;
    c_mysql_connect_auto_ptr* p_mc;
} db_info_t;
static db_info_t db_info[100];

c_mysql_operator::c_mysql_operator()
{
    inited = false;
    db.init();
}

c_mysql_operator::~c_mysql_operator()
{
    if(inited) {
        uninit();
    }
}

uint32_t c_mysql_operator::init(const char* db_name, const char* user, const char* pwd, const char* host, uint32_t port)
{
    //c_mysql_connect_auto_ptr config;
    if(config.init(host, user, pwd, db_name, port, CLIENT_INTERACTIVE)) {
        return 1;
    }
    if(config.do_sql("select db_id,db_host,db_user,db_pwd,db_name,db_port from t_db_info where db_id < 100")) {
        return 2;
    }
    MYSQL_ROW row;
    uint32_t db_info_index = 0;
    uint32_t j;
    while((row = config.get_next_row())) {
        for(j=0; j<db_info_index; j++) {
            if(strcmp(db_info[j].db_host, row[1]) == 0 &&
                    strcmp(db_info[j].db_user, row[2]) == 0 &&
                    strcmp(db_info[j].db_pwd, row[3]) == 0 &&
                    db_info[j].db_port == atol(row[5]))
                break;
        }
        if(j == db_info_index) {
            if(db.insert(atoi(row[0]), row[1], row[2], row[3], row[4], atoi(row[5]), CLIENT_INTERACTIVE)) {
                return 3;
            }
            strcpy(db_info[j].db_host, row[1]);
            strcpy(db_info[j].db_user, row[2]);
            strcpy(db_info[j].db_pwd, row[3]);
            db_info[j].db_port = atoi(row[5]);
            db_info[j].p_mc = db.get(atoi(row[0]));
            db_info_index++;
        } else {
            db.insert(atoi(row[0]), db_info[j].p_mc);
        }
    }
    return 0;
}

uint32_t c_mysql_operator::uninit()
{
    db.uninit();
    inited = false;
    return 0;
}

uint32_t c_mysql_operator::updateHadoop(uint32_t gpzs, uint32_t data, uint8_t type, uint32_t time, double value, uint8_t op, uint32_t hash)
{
    uint32_t db_id = (hash/100)%100;
    uint32_t table_id = hash%100;
    c_mysql_connect_auto_ptr* mysql = db.get(db_id);
    if(mysql == NULL) {
        ERROR_LOG("get mysql connection by sthash[%u.%u] error", hash, db_id);
        return 2;
    }
    if((time = getTimeByType(time, type)) == 0) {
        ERROR_LOG("get time from [%u.%u] error", time, type);
        return 3;
    }
    static char sql[1024];
    switch(op) {
        case COUNT:
        case SUM:
        case MAX:
        case SET:
        case UCOUNT:
        case DISTR_SUM:
        case DISTR_MAX:
        case DISTR_MIN:
        case DISTR_SET:
        case IP_DISTR:
        case TASK:
            sprintf(sql, "INSERT INTO db_td_data_%u.t_db_data_day_%u SET gpzs_id = %u, data_id = %u, time = %u, value = %f ON DUPLICATE KEY UPDATE value = %f", db_id, table_id, gpzs, data, time, value, value);
            break;
        default:
            ERROR_LOG("unexcepted op_type[%u]", op);
            return 4;
    }
    //TODO : do sql
    uint32_t ret;
    if((ret = mysql->do_sql(sql))) {
        ERROR_LOG("do sql[%s] return %u", sql, ret);
        return 5;
    }
    sprintf(sql, "UPDATE t_data_info set modify_time = now() where data_id = %u", data);
    config.do_sql(sql);
    return 0;
}

uint32_t c_mysql_operator::updateOnline(uint32_t gpzs, uint32_t data, uint8_t type, uint32_t time, double value, uint8_t op, uint32_t hash)
{
    //if(type != MINUTE) {
    //    ERROR_LOG("unexcepted time_type[%u]", type);
    //    return 1;
    //}
    const char* table = NULL;
    if(type == MINUTE) {
        table = "t_db_data_minute";
    } else if(type == DAY) {
        table = "t_db_data_day";
    }
    uint32_t db_id = (hash/100)%100;
    uint32_t table_id = hash%100;
    c_mysql_connect_auto_ptr* mysql = db.get(db_id);
    if(mysql == NULL) {
        ERROR_LOG("get mysql connection by sthash[%u.%u] error", hash, db_id);
        return 1;
    }
    if((time = getTimeByType(time, type)) == 0) {
        ERROR_LOG("get time from [%u.%u] error", time, type);
        return 2;
    }
    static char sql[1024];
    switch(op) {
        case COUNT:
            sprintf(sql, "INSERT INTO db_td_data_%u.%s_%u SET gpzs_id = %u, data_id = %u, time = %u, value = %f ON DUPLICATE KEY UPDATE value = value + %f", db_id, table, table_id, gpzs, data, time, value, value);
            break;
        case SUM:
            sprintf(sql, "INSERT INTO db_td_data_%u.%s_%u SET gpzs_id = %u, data_id = %u, time = %u, value = %f ON DUPLICATE KEY UPDATE value = value + %f", db_id, table, table_id, gpzs, data, time, value, value);
            break;
        case MAX:
            sprintf(sql, "INSERT INTO db_td_data_%u.%s_%u SET gpzs_id = %u, data_id = %u, time = %u, value = %f ON DUPLICATE KEY UPDATE value = if(value>%f,value,%f)", db_id, table, table_id, gpzs, data, time, value, value, value);
            break;
        case SET:
            sprintf(sql, "INSERT INTO db_td_data_%u.%s_%u SET gpzs_id = %u, data_id = %u, time = %u, value = %f ON DUPLICATE KEY UPDATE value = %f", db_id, table, table_id, gpzs, data, time, value, value);
            break;
        case UCOUNT:
        case DISTR_SUM:
        case DISTR_MAX:
        case DISTR_MIN:
        case DISTR_SET:
        case IP_DISTR:
        default:
            ERROR_LOG("unexcepted op_type[%u]", op);
            return 2;
    }
    //TODO : do sql
    uint32_t ret;
    if((ret = mysql->do_sql(sql))) {
        ERROR_LOG("do sql[%s] return %u", sql, ret);
        return 1;
    }
    return 0;
}
