/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2013, TaoMee.Inc, ShangHai.
 *
 *   @brief   统计平台调度系统（测试）
 *   @author  kendy<kendy@taomee.com>
 *   @date    2014-09-24
 * =====================================================================================
 */

/**
 * 解析数据库中job信息表，生成文件
 * 调度系统主接口
 */
#ifndef STAT_JOB_GENERATE_HPP
#define STAT_JOB_GENERATE_HPP

#include <stdio.h>
#include <stdlib.h>
#include <string>
#include "c_mysql_connect_auto_ptr.h"

using std::string;
using std::ostringstream;
//错误码
#define STAT_JOB_NORMAL                (0) //正常
#define STAT_GET_JOBINFO_FAILED        (1) //数据库中获取job信息失败
#define STAT_GET_JOBINFO_NULL          (2) //查询job信息为空
#define STAT_GET_PARAM_FAILED          (3) //数据库中获取参数失败
#define STAT_GET_PARAM_NULL            (4) //查询参数信息为空
#define STAT_GET_DBUPLOAD_FAILED       (5) //数据库中获取DB入库信息失败
#define STAT_GET_DBUPLOAD_NULL         (6) //查询DB入库信息为空
#define STAT_GET_MYSQLUPLOAD_FAILED    (7) //数据库中获取MYSQL入库信息失败
#define STAT_GET_MYSQLUPLOAD_NULL      (8) //查询MYSQL入库信息为空 
#define STAT_FILE_SUFFIX_NOT_MATCH     (9) //文件名后缀不匹配 
#define STAT_OPEN_FILE_FAILED          (10)//打开文件失败 
#define STAT_WRITE_FILE_FAILED         (11)//写文件失败 

class StatJobGenerate 
{
public:
	//获取Mysql对象
	//void set_mysql(c_mysql_connect_auto_ptr* sql) { this->mysql = sql;}
    void set_mysql(const char* db_host, const char* db_user, const char* db_passwd, const char* db_name, int port)
	{
		mysql.init(db_host, db_user, db_passwd, db_name, port, CLIENT_INTERACTIVE);
	}
	//返回job名称
	virtual const char* get_jobname(int job_id)=0;
	//对外接口，返回成功或失败，每种失败对应不同的错误码
	virtual int process_job(int job_id, const string& fpath, const string& fname, const int gid[], int glen) = 0;
protected:
	//错误码
    int errorCode;
	string jobName;
	c_mysql_connect_auto_ptr mysql;
};

#endif
