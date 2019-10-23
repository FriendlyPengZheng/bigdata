#ifndef  C_MYSQL_CONNECT_MGR_H
#define  C_MYSQL_CONNECT_MGR_H

#include "c_mysql_connect_auto_ptr.h"
#include <map>
#include <stdint.h>

//key-value 方式管理mysql连接
//key为查找索引
//value为对应的mysql连接
class c_mysql_connect_mgr {
private:
    std::map<uint32_t, c_mysql_connect_auto_ptr*> mysql_connect;
    bool inited;

public:
    c_mysql_connect_mgr();
    ~c_mysql_connect_mgr();
    uint32_t    init();
    uint32_t    uninit();
    uint32_t    insert(uint32_t key, const char *host, const char *user, const char *passwd, const char *db, unsigned int port, unsigned int client_flag);
    uint32_t    insert(uint32_t key, c_mysql_connect_auto_ptr* mc);
    c_mysql_connect_auto_ptr* get(uint32_t key);
};

#endif  /*C_MYSQL_CONNECT_MGR_H*/
