#include <cstdlib>
#include <stdexcept>
#include <string>

#include <sys/time.h>

#include "interface.h"
#include "global.h"
#include "c_proto_pkg.h"
#include "async_server.h"
#include "log.h"
#include "c_mysql_operator.h"
#include "proto2.h"

using namespace std;

c_proto_pkg* pkg;
c_mysql_operator* mysql_op;

namespace { // 2013-8-6
ProtoHandler* g_proto_hdlr = 0;
}

char uexpr_ip[64];
uint32_t uexpr_port;
int uexpr_fd;

void mysql_get_operator(int fd, const char* buf);
void mysql_set_operator(int fd, const char* buf);
void uexpr_proxy(int fd, const char* buf);

int plugin_init(int type)
{
    if (type == PROC_WORK)  {
		// load_config_file can only be called ONCE for each process, or you are definitely DOOMED!
		if(!load_config_file("../conf/configure.ini")) {
			throw runtime_error(string("failed to parse config file '../conf/configure.ini'"));
		}

        pkg = new c_proto_pkg();
        mysql_op = new c_mysql_operator();
        if (mysql_op->init() != 0) {
			BOOT_LOG(-1, "failed to init c_mysql_operator");
        }
		// ---------
		// 2013-8-6
		g_proto_hdlr = new ProtoHandler();
		// ---------
        DEBUG_LOG("init done");
        strcpy(uexpr_ip, config_get_strval("uexpr_ip", "192.168.71.36"));
        uexpr_port = config_get_intval("uexpr_port", 20001);
        uexpr_fd = -1;
    }
    return 0;
}

int plugin_fini(int type)
{
    if (type == PROC_WORK)  {
        delete pkg;
        mysql_op->uninit();
        delete mysql_op;
    }
    return 0;
}

//void time_event()
//{
//
//}

int get_pkg_len_cli(const char * buf, uint32_t len)
{
    if(len<4)
    {
        return 0;
    }
    return *(int*)(buf);
}

int get_pkg_len_ser(const char * buf, uint32_t len)
{
    if(len<4)
    {
        return 0;
    }
    return *(int32_t*)(buf);
}

//int check_open_cli(uint32_t ip, uint16_t port)
//{
//    return 0;
//}
//
//int select_channel(int fd, const char * buf, uint32_t len, uint32_t ip, uint32_t work_num)
//{
//    return fd % work_num;
//    //return 0;
//}
//
//
//int shmq_pushed(int fd, const char * buf, uint32_t len, int flag)
//{
//    return 0;
//}

//最多保存20天的数据，拉取数据很多的时候 前端会做限制 不过还是要注意此处可能的内存溢出
uint32_t g_time[1440*20];
double   g_value[1440*20];

//bool SubTimeval(timeval &result, timeval &begin, timeval &end)
//// 计算gettimeofday函数获得的end减begin的时间差，并将结果保存在result中。
//{
//    if ( begin.tv_sec>end.tv_sec ) return false;
//
//    if ( (begin.tv_sec == end.tv_sec) && (begin.tv_usec > end.tv_usec) )   
//        return   false;
//
//    result.tv_sec = ( end.tv_sec - begin.tv_sec );   
//    result.tv_usec = ( end.tv_usec - begin.tv_usec );   
//
//    if (result.tv_usec<0) {
//        result.tv_sec--;
//        result.tv_usec+=1000000;}  
//
//        return true;
//}
//
//timeval tBegin, tEnd, tDiff;

void proc_pkg_cli(int fd, const char * buf, uint32_t len)
{
//    gettimeofday(&tBegin, 0);
    if(pkg->recv_pkg(buf) != 0) {
        pkg->set_cmd_id(pkg->get_cmd_id());
        pkg->set_result(ERROR_FOVER);
        net_send_cli(fd, pkg->ret_pkg(), pkg->get_ret_len());
        return ;
    }
    uint32_t cmd = pkg->get_cmd_id();
    switch(cmd) {
        ///去源库、结果库读数据
    case GET_MAX:
    case GET_MIN:
    case GET_SUM:
    case GET_AVG:
    case GET_DLAST:
    case GET_UCOUNT:
    case GET_LAST:
        mysql_get_operator(fd, buf);
        break;
    case SET:
        ///写结果库
        mysql_set_operator(fd, buf);
        break;
    case UEXPR:
        ///调uexpr的接口，sdk仅转发数据
        uexpr_proxy(fd, buf);
        break;
	case GET_STAT_DATA: // 2013-8-7
		g_proto_hdlr->process(fd, buf);
		break;
    default:
        ERROR_LOG("error cmd id 0x%08X", cmd);
        pkg->set_cmd_id(pkg->get_cmd_id());
        pkg->set_result(ERROR_CMD);
        net_send_cli(fd, pkg->ret_pkg(), pkg->get_ret_len());
        break;
    }

}

void uexpr_proxy(int fd, const char* buf)
{
    if(uexpr_fd == -1) {
        uexpr_fd = net_connect_ser(uexpr_ip, uexpr_port, 1000);
        if(uexpr_fd == -1) {
             char error_info[64];
             snprintf(error_info, sizeof(error_info), "can't connect %s:%u", uexpr_ip, uexpr_port);
             ERROR_LOG("%s", error_info);
             //todo : 给客户端返回错误
             pkg->set_uexpr_ret(ERROR_UEXPR);
             pkg->set_uexpr_error(error_info);
             net_send_cli(fd, pkg->ret_pkg(), pkg->get_ret_len());
             return;
        }
    }

    pkg->set_clifd(fd);
    net_send_ser(uexpr_fd, pkg->recv_pkg(), pkg->get_pkglen());
}

void mysql_set_operator(int fd, const char* buf)
{
    pkg->set_cmd_id(pkg->get_cmd_id());
    pkg->set_result(mysql_op->set(pkg->get_set_id(), pkg->get_set_cnt(), pkg->get_set_data()));
    net_send_cli(fd, pkg->ret_pkg(), pkg->get_ret_len());
}

void mysql_get_operator(int fd, const char* buf)
{
    const recv_id_info_t * tmp;
    uint32_t* p_time = NULL;
    double* p_value = NULL;
    uint32_t old_cnt = 0;
    uint32_t new_cnt = 0;
    uint32_t start_time;
    uint32_t end_time;
    uint32_t id;
    uint8_t  type;
    uint32_t gap_time;
    while((tmp = pkg->get_next_id_info()) != NULL)
    {
        start_time = tmp->start_time;
        end_time = tmp->end_time;
        id = tmp->id;
        type = tmp->type;
        gap_time = tmp->gap_time * 60;
        new_cnt = gap_time == 0 ? 1 : (end_time - start_time) / gap_time + 1;
        if(new_cnt > sizeof(g_time)/sizeof(g_time[0])) {
            if(new_cnt > old_cnt) {
                free(p_time);
                free(p_value);
                p_time = (uint32_t*)malloc(new_cnt * sizeof(uint32_t));
                p_value = (double*)malloc(new_cnt * sizeof(double));
                if(p_time == NULL || p_value == NULL) {
                    ERROR_LOG("malloc failed. p_time[%p], p_value[%p]", p_time, p_value);
                    break;
                }
                old_cnt = new_cnt;
            }
        } else {
            if(p_time != g_time) {
                free(p_time);
            }
            if(p_value != g_value) {
                free(p_value);
            }
            p_time = g_time;
            p_value = g_value;
        }
        pkg->pop_id(id, type);
        int ret;
        if((ret = mysql_op->get(pkg->get_cmd_id()%10, type, id, start_time, end_time, gap_time, p_time, p_value )) > 0) {
            for(int i=0; i<ret; i++) {
                pkg->pop_tv(p_time[i], p_value[i]);
            }
        }
    }
    pkg->set_cmd_id(pkg->get_cmd_id());
    pkg->set_result(0);
    net_send_cli(fd, pkg->ret_pkg(), pkg->get_ret_len());

    if(p_time != g_time) {
        free(p_time);
    }
    if(p_value != g_value) {
        free(p_value);
    }

    //gettimeofday(&tEnd, 0);
    //SubTimeval(tDiff, tBegin, tEnd);
    //DEBUG_LOG("process package %lu.%06lu", tDiff.tv_sec, tDiff.tv_usec); 
}

void proc_pkg_ser(int fd, const char * buf, uint32_t len)
{
    uint32_t* tmp = (uint32_t*)buf;
    net_send_cli(tmp[2], buf, tmp[0]);
}

//void link_up_cli(int fd, uint32_t ip)
//{
//
//}
//
//
//void link_down_cli(int fd)
//{
//
//}

void link_down_ser(int fd)
{
    uexpr_fd = -1;
}
