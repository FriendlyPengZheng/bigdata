#ifndef  GLOBAL_H
#define  GLOBAL_H

#include <stdint.h>
#include "c_mysql_connect_mgr.h"

//#define DEBUG

#define ONLINE_CMD  (0x0001)

#define FLUSH_INTERVAL  (60)

#define MAX_LINE    (1024)

enum RET_CODE {
    RESULT_OK  = 0,
    ERROR_CMD,
    ERROR_START,
    ERROR_END,
    ERROR_CNT,
    ERROR_GET,
};

enum TCP_CODE {
    TC_ERROR = -1,
    TC_TIMEOUT = 0,
    TC_OK = 1
};

typedef struct {
    char     db_host[16];
    char     db_user[64];
    char     db_pwd[64];
    uint32_t db_port;
    c_mysql_connect_auto_ptr* p_mc;
} db_info_t;

#pragma pack(push)
#pragma pack(1)

typedef struct {
    uint32_t    time;
    double      value;
} tv_t; //time_value

typedef struct {
    uint16_t  cnt;
    tv_t      data[1440];
} dv_t; //day_value

typedef struct {
    uint32_t pkg_len;
    uint32_t cmd;
    uint32_t version;
    uint32_t error;
    uint32_t start;
    uint32_t end;
    uint32_t linecnt;
} pkg_header_t;

typedef struct {
    uint32_t dataid;
    uint32_t sthash;
    uint32_t gpzsid;
} datainfo_t;

typedef struct {
    datainfo_t datainfo;
    dv_t    data;
} pkg_ret_body_t;

typedef struct {
    pkg_header_t header;
    datainfo_t  datainfo[MAX_LINE];
} pkg_recv_t;

#pragma pack(pop)

#endif  /*GLOBAL_H*/
