#ifndef  GLOBAL_H
#define  GLOBAL_H

#include <stdint.h>

//#define DEBUG

#pragma pack(push)
#pragma pack(1)

typedef struct {    //请求包的单个rs/rp信息
    uint32_t  id;           //rs/rp的id
    uint8_t   type;         //id类型：rs==1,rp==2
    uint32_t  start_time;   //请求数据的起始时间：时间戳
    uint32_t  end_time;     //请求数据的结束时间：时间戳
    uint32_t  gap_time;     //请求数据的间隔时间，以分钟为单位
} recv_id_info_t;

#pragma pack(pop)

#define RESULT_OK   (0)
#define ERROR_CMD   (1)
#define ERROR_FOVER (2)
#define ERROR_MYSQL (3)
#define ERROR_RSYNC (4)
#define ERROR_FOPEN (5)
#define ERROR_UEXPR (6)

#define MAX         (0)
#define MIN         (1)
#define SUM         (2)
#define AVG         (3)
#define DLAST       (4)
#define UCOUNT      (5)
#define LAST        (6)

#define RS (0)
#define RP (1)

#define GET_MAX       (0xEF000100)
#define GET_MIN       (0xEF000101)
#define GET_SUM       (0xEF000102)
#define GET_AVG       (0xEF000103)
#define GET_DLAST     (0xEF000104)
#define GET_UCOUNT    (0xEF000105)
#define GET_LAST      (0xEF000106)
#define GET_STAT_DATA (0xEF000111) // cmd for data-center, 2013-08-06

#define SET         (0xEF000200)

#define UEXPR       (0x10000006)

#endif  /*GLOBAL_H*/
