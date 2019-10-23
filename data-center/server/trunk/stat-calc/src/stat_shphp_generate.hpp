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
 * function：解析数据库中job信息表，生成sh 或者 php文件
 */
#ifndef STAT_SHPHP_GENERATE_HPP
#define STAT_SHPHP_GENERATE_HPP

#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <ctime>
#include <sstream>
#include <sys/fcntl.h>
#include <set>
//#include "log.h"
//#include "config.h"
#include "c_mysql_connect_auto_ptr.h"
#include "stat_job_generate.hpp"

using std::string;
using std::ostringstream;

class StatShphpGenerate : public StatJobGenerate 
{
public:
    StatShphpGenerate();
    virtual ~StatShphpGenerate();

	//对外接口，返回成功或失败，每种失败对应不同的错误码
	virtual int process_job(int job_id, const string& fpath, const string& fname, const int gid[], int glen);
    //返回job名称
    virtual const char* get_jobname(int job_id);

private:
	//查询任务信息表，获取job_info
	int get_jobinfo(const int job_id);
	//查询任务参数表，获取参数内容
	int get_variable(const string& var_name);
	//装载文件首部
	void load_head_file();
	//装载变量信息
	void load_variable_file();
	//装载job信息
	void load_jobinfo();
	//装载exit_code信息（上一条命令的执行情况 0：正确 1：错误）
	void load_exitCode();
	//判断并输出变量
	void check_output_var(const string& var_name);
	//判断并输出job信息
	void check_output_shphp(const string& shphp);

	//将数据写文件，传入路径，文件名，数据字符串
	int write_file(const string& fpath, const string& fname);

private:
	struct jobInfo
	{
		string job_id;
		string file_name;
		string params;
		string log_path;
	};
	jobInfo jobinfo;

	ostringstream oss;
	std::set<string> var_set;
	string var_content;
};

#endif
