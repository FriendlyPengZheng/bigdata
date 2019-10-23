#ifndef  MYSQL_CONNECT_AUTO_PTR_H
#define  MYSQL_CONNECT_AUTO_PTR_H

#include <mysql/mysql.h>
#include <stdint.h>
#include <stdio.h>
#include <string.h>

class c_mysql_connect_auto_ptr
{
public:
    c_mysql_connect_auto_ptr();
    ~c_mysql_connect_auto_ptr();
    uint32_t    init(const char *host, const char *user, const char *passwd, const char *db, unsigned int port, unsigned int client_flag);
    uint32_t    uninit();

    uint32_t    do_sql(const char *query);
    const char* m_error();
    uint32_t    m_errno();

    const char* get_info()          {snprintf(info, sizeof(info), "mysql -u%s -pXXXXXX -h%s --port=%u %s", this->user, this->host, this->port, this->db); return info;}
    uint32_t    get_selected_cnt()  {return selected_cnt;}
    uint32_t    get_affected_cnt()  {return affected_cnt;}
    MYSQL_ROW   get_next_row()      {return result==NULL?NULL:mysql_fetch_row(result);}

private:
    MYSQL       *mysql;
    char        host[64];
    char        user[128];
    char        passwd[128];
    char        db[128];
    uint32_t    port;
    uint32_t    client_flag;

    MYSQL_RES   *result;
    MYSQL_ROW   sql_row;
    uint32_t    selected_cnt;
    uint32_t    affected_cnt;

    char        sql_buf[1024];
    char        info[1024];

    bool        inited;
};

#endif  /*MYSQL_CONNECT_AUTO_PTR_H*/
