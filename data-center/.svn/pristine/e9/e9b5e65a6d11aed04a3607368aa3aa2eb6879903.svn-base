#ifndef STATSDK_PROTO2_H_
#define STATSDK_PROTO2_H_

// for data-center, 2013-08-06
#include <cstring>
#include <map>
#include <string>

#include <stdint.h>

#include "c_mysql_connect_mgr.h"

#pragma pack(1)

struct ReqInfo { //请求包内的单个event/task请求信息
	int32_t   zone_id;		//游戏分区ID。如果需要查看全区全服，可把zone_id和svr_id同时置为-1。
	int32_t   svr_id;		//游戏服务器ID。
    uint8_t   type;         //id类型：event_id_type==2, task_id_type==3
    uint32_t  id;           //event_id或者task_id
	uint16_t  idtype;       //1请求default库，2请求item库，3请求arithmetic库，4请求等级分布库
	char      column[32];   //请求的列：ucount_value、count_value、sum_value、max_value、min_value、first_value、last_value、all_value
	uint32_t  aux_key;      //辅助主键（item_id），distr和item库需要用到，arithmetic和default库不需要
    uint32_t  start_time;   //请求数据的起始时间：时间戳
    uint32_t  end_time;     //请求数据的结束时间：时间戳
    uint32_t  interval;     //请求数据的间隔时间，以分钟为单位
};

// 命令0xEF000111的请求包包体
struct ReqInfoArray {
	uint32_t  id_cnt; //请求包中event_id和task_id的个数
	ReqInfo   info[];
};

// 请求包和返回包的包头
struct ProtoHeader {
	uint32_t  len;          //包长
	uint32_t  cmd;          //命令号：0xEF000111 - 请求获取新统计平台的数据
	uint32_t  platform_id;  //联运平台ID。淘米0
	uint32_t  result;       //请求结果：0-成功，其它-出错
	uint8_t   body[];       //包体
};

struct TimeValue {  //返回包内的单个“时间-值”
	TimeValue(uint32_t t = 0)
		{ time = t; memset(value, '\0', sizeof(value)); }

	uint32_t  time;        //时间：YYMMDDhhmm或YYMMDD格式
	char      value[24];
};

struct RspInfo {      //返回包的单个event/task信息
	int32_t   zone_id;		//游戏分区ID。如果需要查看全区全服，可把zone_id和svr_id同时置为-1。
	int32_t   svr_id;		//游戏服务器ID。
	uint8_t   type;         //id类型：event_id_type==2, task_id_type==3
	uint32_t  id;           //event_id或者task_id	
	uint16_t  idtype;       //1请求default库，2请求item库，3请求arithmetic库，4请求等级分布库
	uint8_t   column[32];   //请求的列：ucount_value、count_value、sum_value、max_value、min_value、first_value、last_value、all_value
	uint32_t  aux_key;      //辅助主键，distr和item库需要用到，arithmetic和default库不需要
	uint32_t  tv_cnt;       //（时间-值）对的个数
	TimeValue time_value[];
};

// 命令0xEF000111的返回包包体
struct RspInfoArray {
	uint32_t  id_cnt;  //返回包中event_id和task_id的个数
	RspInfo   info[];
};

#pragma pack()

// handle 0xEF000111
class ProtoHandler {
public:
	ProtoHandler();
	void process(int fd, const void* data);

private:
	std::string get_sql(const ProtoHeader& h, const ReqInfo& info) const;
	std::string get_tbl_name(uint32_t id, uint32_t idtype, uint32_t reqtype) const;

	bool chk_req_type(uint32_t type) const
		{ return (type == 2) || (type == 3); }
	bool chk_id_type(uint32_t type) const
		{ return (type > 0) && (type < 5); }

private:
	static const int			sc_dbmgr_num = 10;
	c_mysql_connect_mgr			m_dbmgr[sc_dbmgr_num];  // only 4 mgrs(1~4) are used currently
	std::string					m_buf;                  // for packing rsp
	std::string					m_auxbuf;				// for modifying part of rsp
};

#endif // STATSDK_PROTO2_H_

