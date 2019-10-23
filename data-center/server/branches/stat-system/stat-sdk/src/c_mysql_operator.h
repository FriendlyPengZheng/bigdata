#ifndef  C_MYSQL_OPERATOR_H
#define  C_MYSQL_OPERATOR_H

#include "c_mysql_connect_auto_ptr.h"
#include "c_mysql_connect_mgr.h"
#include "global.h"
#include <config.h>
#include <map>

#pragma pack(push)
#pragma pack(1)

class c_mysql_operator {

private:
    //db_stat_config的数据库操作对象
    c_mysql_connect_auto_ptr db_stat_config;
    //对源库和结果库的数据库操作对象进行管理的对象
    c_mysql_connect_mgr      db[2];

    uint32_t                 db_range_min[2];
    uint32_t                 db_range_max[2];

    std::map<uint32_t, uint8_t>  rs_operator;

    bool                     inited;

    typedef struct {
        uint32_t  time;
        double    value;
    } time_value_t;

public:
    uint32_t init();
    uint32_t uninit();
    c_mysql_operator();
    ~c_mysql_operator();

    int32_t get(uint8_t op_type, uint8_t type, uint32_t id, uint32_t start, uint32_t end, uint32_t gap, uint32_t* time, double* value);
    
    uint32_t set(uint32_t id, uint32_t cnt, const char * data);
    
};

#pragma pack(pop)

#endif  /*C_MYSQL_OPERATOR_H*/
