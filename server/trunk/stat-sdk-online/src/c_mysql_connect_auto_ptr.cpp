#include "c_mysql_connect_auto_ptr.h"
#include "log.h"
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>

c_mysql_connect_auto_ptr::c_mysql_connect_auto_ptr()
{
    mysql = NULL;
    result = NULL;
    memset(host, 0, sizeof(host));
    memset(user, 0, sizeof(user));
    memset(passwd, 0, sizeof(passwd));
    memset(db, 0, sizeof(db));
    port = 0;
    client_flag = 0;
    selected_cnt = 0;
    affected_cnt = 0;
    inited = false;
}

c_mysql_connect_auto_ptr::~c_mysql_connect_auto_ptr()
{
    if(inited) {
        uninit();
    }
}

uint32_t c_mysql_connect_auto_ptr::init(const char *host, const char *user, const char *passwd, const char *db, unsigned int port, unsigned int client_flag) {
    uninit();
    strcpy(this->host, host);
    strcpy(this->user, user);
    strcpy(this->passwd, passwd);
    strcpy(this->db, db);
    this->port = port;
    this->client_flag = client_flag;

    mysql = mysql_init(NULL);
    if(mysql != NULL) {
        my_bool is_auto_reconnect = 1;
        mysql_options(mysql, MYSQL_OPT_RECONNECT, &is_auto_reconnect);
        if(mysql_real_connect(mysql, host, user, passwd, db, port, NULL, client_flag) != NULL) {
            mysql_set_character_set(mysql, "utf8");
            inited = true;
            DEBUG_LOG("%s", this->get_info());
            return 0;
        } else {
            ERROR_LOG("error : %s", this->get_info());
            return 2;
        }
    } else {
        ERROR_LOG("mysql_init(NULL) error");
        return 1;
    }
}

uint32_t c_mysql_connect_auto_ptr::uninit()
{
    if(result != NULL) {
        mysql_free_result(result);
        result = NULL;
    }

    if(mysql != NULL) {
        mysql_close(mysql);
        mysql = NULL;
        inited = false;
        memset(host, 0, sizeof(host));
        memset(user, 0, sizeof(user));
        memset(passwd, 0, sizeof(passwd));
        memset(db, 0, sizeof(db));
        port = 0;
        client_flag = 0;
        selected_cnt = 0;
        affected_cnt = 0;
        return 0;
    } else {
        return 1;
    }
}

uint32_t c_mysql_connect_auto_ptr::do_sql(const char *query)
{
    //check and release last query result
    uint32_t ret = 0;
    if(mysql == NULL || mysql_ping(mysql) != 0) {
        return 1;
    }
    if(result != NULL) {
        mysql_free_result(result);
        result = NULL;
    }

    //escape_string
    //char * tmp = NULL;
    //uint32_t len = strlen(query);
    //if(len > (sizeof(sql_buf)>>1)) {
    //    tmp = (char *)malloc((len<<1)+1);
    //} else {
    //    tmp = sql_buf;
    //}

    //mysql_escape_string(tmp, query, len);

    const char * tmp = query;
    //do sql
    //#ifdef DEBUG
    //DEBUG_LOG("%s %s", this->get_info(), query);
    //#endif
    if(mysql_query(mysql, query) != 0) {
    //if(mysql_query(mysql, tmp) != 0) {
        ERROR_LOG("%s : %s : error %s", this->get_info(), tmp, this->m_error());
        ret = 2;
        goto exit;
    } else {
        selected_cnt = 0;
        affected_cnt = 0;
        result = mysql_store_result(mysql);
        if(result != NULL) { //select
            if((selected_cnt = mysql_num_rows(result)) < 0) { //select return < 0
                ERROR_LOG("%s : %s : error %s", this->get_info(), tmp, this->m_error());
                ret = 3;
                goto exit;
            } else if(selected_cnt == 0) { //select 0 rows
            //    DEBUG_LOG("%s : %s 0 rows", this->get_info(), tmp);
            //} else {
            //    DEBUG_LOG("%s : %s %u rows", this->get_info(), tmp, selected_cnt);
            }
        } else {
            if(mysql_field_count(mysql) == 0) { //it's not select
                affected_cnt = mysql_affected_rows(mysql);
            } else { //it's select but result is NULL
                ERROR_LOG("%s : %s : error %s", this->get_info(), tmp, this->m_error());
                ret = 4;
                goto exit;
            }
        }
    }

exit:
    //free memory if necessary
    //if(tmp != sql_buf && tmp != NULL) {
    //    free(tmp);
    //}
    return ret;
}

const char error_str[] = "mysql_headle == NULL.";

const char * c_mysql_connect_auto_ptr::m_error()
{
    if(mysql == NULL) {
        return error_str;
    } else {
        return mysql_error(mysql);
    }
}

uint32_t c_mysql_connect_auto_ptr::m_errno()
{
    if(mysql == NULL) {
        return -1;
    } else {
        return mysql_errno(mysql);
    }
}
