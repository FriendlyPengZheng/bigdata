#ifndef  C_MYSQL_OPERATOR_H
#define  C_MYSQL_OPERATOR_H

#include "c_mysql_connect_auto_ptr.h"
#include "c_mysql_connect_mgr.h"
#include <map>

class c_mysql_operator {

private:
    c_mysql_connect_mgr      db;
    c_mysql_connect_auto_ptr config;

    bool                     inited;

public:
    uint32_t init(const char*, const char*, const char*, const char*, uint32_t);
    uint32_t uninit();
    c_mysql_operator();
    ~c_mysql_operator();

    uint32_t updateOnline(uint32_t gpzs, uint32_t data, uint8_t type, uint32_t time, double value, uint8_t op, uint32_t hash);
    uint32_t updateHadoop(uint32_t gpzs, uint32_t data, uint8_t type, uint32_t time, double value, uint8_t op, uint32_t hash);
    
};

#endif  /*C_MYSQL_OPERATOR_H*/
