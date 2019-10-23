#ifndef  PROTO_PKG_H
#define  PROTO_PKG_H

#include <stdint.h>
#include <string.h>
#include "log.h"
#include "global.h"

#pragma pack(push)
#pragma pack(1)

class c_proto_pkg {
    private:
        typedef struct {    //请求包的包头
            uint32_t  pkg_len;      //包长
            uint32_t  cmd_id;       //命令号
            uint32_t  id_cnt;       //请求包中rs/rp_id的个数
            recv_id_info_t recv_id_info[0];
        } recv_get_info_t;

        typedef struct {    //时间-值
            uint32_t  time;         //时间：YYMMDDhhmm或YYMMDD格式
            double    value;        //值，双精度浮点数
        } time_value_t;

        typedef struct {    //返回包的单个rs/rp信息
            uint32_t  id;           //rs/rp的id
            uint8_t   type;         //id类型：rs==0,rp==1
            uint32_t  tv_cnt;       //（时间-值）对的个数
            time_value_t time_value[0];
        } ret_id_info_t;

        typedef struct {    //返回包的包头
            uint32_t  pkg_len;      //包长
            uint32_t  cmd_id;       //命令号
            uint32_t  result;       //请求结果：0-成功，其它-出错
            uint32_t  id_cnt;       //返回包中rs/rp_id的个数，只有在result!=0是才为非0值
            ret_id_info_t ret_id_info[0];
        } ret_get_info_t;

        typedef struct {
            uint32_t  pkg_len;      //包长
            uint32_t  cmd_id;       //命令号
            uint32_t  id;           //rs/rp的id          
            uint8_t   type;         //id类型：rs==0,rp==1
            uint32_t  time;         //时间戳
        } recv_ucount_info_t;

        typedef struct {
            uint32_t  pkg_len;      //包长
            uint32_t  cmd_id;       //命令号
            uint32_t  result;       //请求结果：0-成功，其它-出错
            uint32_t  mimi_cnts;    //米米号个数
            uint32_t  mimi_id[0];   //米米号列表
        } ret_ucount_info_t;

        typedef struct {
            uint32_t  pkg_len;      //包长
            uint32_t  cmd_id;       //命令号
            uint32_t  id;           //rs_id
            uint32_t  cnt;          //数量
            char      data[0];      //数据段
        } recv_set_info_t;

        typedef struct {
            uint32_t  pkg_len;      //包长
            uint32_t  cmd_id;       //命令号
            int32_t   cli_fd;       //cli_fd, 忽略
            char      uexpr[0];     //uexpr表达式
        } recv_uexpr_info_t;

        typedef struct {
            uint32_t  pkg_len;      //包长
            int32_t   result;
            uint32_t  cli_fd;
            char      value[0];
            char      error[0];
        } ret_uexpr_info_t;

        char buf_recv[(1024<<10)];
        char buf_ret[(1024<<10)*5];

        recv_get_info_t * p_recv_get_info;
        ret_get_info_t *  p_ret_get_info;

        recv_id_info_t * p_recv_id_info;
        ret_id_info_t *  p_ret_id_info;

        recv_set_info_t * p_recv_set_info;
        recv_uexpr_info_t * p_recv_uexpr_info;
        ret_uexpr_info_t * p_ret_uexpr_info;

        time_value_t *   p_time_value;

        uint32_t pkg_len;
        uint32_t done_len;
        uint32_t done_cnt;

        recv_ucount_info_t * p_recv_ucount_info;
        ret_ucount_info_t * p_ret_ucount_info;
        uint32_t*   mimi_id;

    public:
        c_proto_pkg();
        ~c_proto_pkg();

        //接受请求包：
        uint32_t recv_pkg(const char * buf);
        uint32_t get_cmd_id() {return p_recv_get_info->cmd_id;}
        uint32_t get_id_cnt() {return p_recv_get_info->id_cnt;}
        const recv_id_info_t * get_next_id_info();

        //打包返回包：
        uint32_t start_pack();
        void     set_cmd_id(uint32_t cmd_id) {p_ret_get_info->cmd_id = cmd_id;}
        void     set_result(uint32_t result) {p_ret_get_info->result = result;}
        uint32_t pop_id(uint32_t id, uint8_t type);
        uint32_t pop_tv(uint32_t time, double value);
        uint32_t pop_mimiid(uint32_t mimiid);

        const char * ret_pkg();

        uint32_t get_ret_len()        {DEBUG_LOG("ret_len=%u", p_ret_get_info->pkg_len);return p_ret_get_info->pkg_len;}
        uint32_t get_ucount_id()      {return p_recv_ucount_info->id;}
        uint32_t get_ucount_type()    {return p_recv_ucount_info->type;}
        uint32_t get_set_id()         {return p_recv_set_info->id;}
        uint32_t get_set_cnt()        {return p_recv_set_info->cnt;}
        const char *  get_set_data()  {return p_recv_set_info->data;}
        void     set_clifd(int fd)    {p_recv_uexpr_info->cli_fd = fd;}
        int      get_clifd()          {return p_recv_uexpr_info->cli_fd;}
        const char * recv_pkg()       {return buf_recv;}
        uint32_t get_pkglen()         {return p_recv_uexpr_info->pkg_len;}
        void     set_uexpr_ret(int r) {p_ret_uexpr_info->result = r;}
        void     set_uexpr_error(const char * string);
};

#pragma pack(pop)

#endif  /*PROTO_PKG_H*/
