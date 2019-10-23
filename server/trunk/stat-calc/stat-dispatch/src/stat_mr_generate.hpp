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
 * function：解析数据库中job信息表，生成文件
 */
#ifndef STAT_MR_GENERATE_HPP
#define STAT_MR_GENERATE_HPP

#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <ctime>
#include <sstream>
#include <sys/fcntl.h>
#include <set>
#include <vector>
//#include "log.h"
//#include "config.h"
//#include "stat_common.hpp"
#include "../../../stat-common/string_utils.hpp"
#include "c_mysql_connect_auto_ptr.h"
#include "stat_job_generate.hpp"

using std::string;
using std::ostringstream;

class StatMrGenerate : public StatJobGenerate 
{
public:
    StatMrGenerate();
    virtual ~StatMrGenerate();

	//对外接口，返回成功或失败，每种失败对应不同的错误码
	virtual int process_job(int job_id, const string& fpath, const string& fname, const int gid[], int glen);
	//返回job名称
	virtual const char* get_jobname(int job_id);
private:
	//查询任务信息表，获取job_info
	int get_jobinfo(const int job_id);
	//查询任务参数表，获取参数内容
	int get_variable(const string& var_name);
	//查询DB_UPLOAD信息表，获取入库信息
	int get_dbUpload_info(const string& upload_id);
	//查询MYSQL_UPLOAD信息表，获取入库信息
	int get_mysqlUpload_info(const string& upload_id);
	//装载文件首部
	void load_head_file();
	//装载变量信息
	void load_variable_file();
	//判断是否有可有可无的路径，若存在则前面添加判断语句
	void load_check_input();
	//装载job信息
	void load_jobinfo();
	//装载DB_UPLOAD入库信息
	void load_db_upload();
	//装载MYSQL_UPLOAD入库信息
	void load_mysql_upload();
	//装载exit_code信息（上一条命令的执行情况）
	void load_exitCode();
	//修完数据后，需要将数据写回（放在入库之前）
	void load_rewrite_file();
	//判断并输出变量
	void check_output_var(const string& var_name);
	//判断并输出-D参数
	void check_output_param();
	//判断并输出addinput(input + mapper)，对可有可无的路径进行判断
	void check_output_input(const string& input);
	//根据gameid对输入路径的格式进行处理
	void process_input(const string& path, const string& mapper, const int flag);
	//输出输入路径
	void process_output_input(const string& path, const string& path_mapper, const int flag);
	//判断并输出mos
	void check_output_mos();
	//判断并输出输出路径
	void check_output_outpath();
	//判断并输出DB_UPLOAD信息
	void check_output_dbUpload();
	//判断并输出MYSQL_UPLOAD信息
	void check_output_mysqlUpload();
	//将mos字段按":"分割，并输出
	void divide_mos(const string& mos, const int flag);
	//将int转化为string
	string toString(const int gid);

	//将数据写文件，传入路径，文件名，数据字符串
	int write_file(const string& fpath, const string& fname);

private:
	struct jobInfo
	{
		string job_id;
		string job_name;
		string gameInfo;
		string jar;
		string params;
		string conf;
		string outKey;
		string outValue;
		string inFormat;
		string outFormat;
		string inputPath;
		string outputPath;
		string combinerClass;
		string reducerClass;
		string mos;
		string dbUpload_id;
		string mysqlUpload_id;
		int fix_flag;
	};
	jobInfo jobinfo;

	struct dbUploadInfo
	{
		string type;
		string time;
		string task_id;
		string path;
	};
	dbUploadInfo db_upload_info;

	struct mysqlUploadInfo
	{
		string table_name;
		string table_field;
		string path;
		string time;
	};
	mysqlUploadInfo mysql_upload_info;

	ostringstream oss;
	std::set<string> var_set;
	string var_content;

	//传入的游戏id
	int gameId_len;
	std::set<int> gameId_set;
	
};

#endif
