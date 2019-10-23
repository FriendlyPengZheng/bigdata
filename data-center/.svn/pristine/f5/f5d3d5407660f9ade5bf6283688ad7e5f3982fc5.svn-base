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


#include "stat_mr_generate.hpp"
#include "log.h"

StatMrGenerate::StatMrGenerate() 
{
	errorCode = STAT_JOB_NORMAL;
	gameId_len = 0;
	jobName = "";
    //printf("this is StatMrGenerate()\n");
}

StatMrGenerate::~StatMrGenerate()
{
	mysql.uninit();
    //printf("this is ~statJobGenerate()\n");
}

const char* StatMrGenerate::get_jobname(int job_id)
{
	uint32_t ret;
	static char sql[1024];
 
	sprintf(sql, "SELECT job_name FROM t_job_mapreducer WHERE job_id=%d", job_id);
	if((ret = mysql.do_sql(sql)) != 0) {
		return NULL;
	}

	MYSQL_ROW row = mysql.get_next_row();
	if(row == NULL)
	{ 
		return NULL;
	}
	else
	{
		jobName = row[0];
		return jobName.c_str();
	}
}

int StatMrGenerate::process_job(int job_id, const string& fpath, const string& fname, const int gid[], int glen)
{
	DEBUG_LOG("job_id=%d, fpath=%s, fname=%s, glen=%d\n",
			job_id,fpath.c_str(),fname.c_str(),glen);
	if(glen > 0)
	{
		gameId_len = glen;
		for(int i=0; i<glen; i++)
		{
			if(gameId_set.find(gid[i]) == gameId_set.end() || gameId_set.empty())
			{
				gameId_set.insert(gid[i]);
			}
		}
	}

	//数据库中获取所有job信息
	if(get_jobinfo(job_id) < 0)
	{
		return errorCode;
	}
	//装载文件头部
	load_head_file();
	//装载参数信息(需要根据glen区分）
	load_variable_file();
	//判断是否有可有可无的路径，若存在则前面添加判断语句
	load_check_input();
	//装载job信息
	load_jobinfo();
	//装载exit_code信息
	load_exitCode();
	//修完数据后，需要将数据写回（放在入库之前）
	if(gameId_len > 0 && jobinfo.fix_flag != 3)
	{
		load_rewrite_file();
	}
	//装载DB_UPLOAD入库信息
	if(jobinfo.dbUpload_id.length() > 0)
	{
		load_db_upload();
	}
	//装载MYSQL_UPLOAD入库信息
	if(jobinfo.mysqlUpload_id.length() > 0)
	{
		load_mysql_upload();
	}

	//写文件
	if(write_file(fpath, fname) < 0)
	{
		return errorCode;
	}
	//清空数据输出流
	oss.str("");
	//清空变量空间
	var_set.clear();
	gameId_set.clear();
	gameId_len = 0;

	return errorCode;

}

void StatMrGenerate::load_head_file()
{
	oss << "export LANG=en_US.UTF-8" << '\n';
	oss << "WORKDIR=`dirname $0`" << '\n';
	oss << "WORKDIR=`cd $WORKDIR && pwd`" << '\n';
	oss << "cd $WORKDIR" << '\n';
	oss << "echo workdir = $WORKDIR" << '\n' << '\n';
	oss << "source config.sh" << '\n';
}

void StatMrGenerate::load_exitCode()
{
	oss << "if [[ $? > 0 ]]; then" << '\n' << '\t';
	oss << "exit 1" << '\n' << "fi" << "\n\n";
}

void StatMrGenerate::load_rewrite_file()
{
	string outpath_org = jobinfo.outputPath;
	string outpath_game = jobinfo.outputPath + "-game";
	string outpath_game_file = outpath_game + "/*-*";
	string hadoop = "${HADOOP_PATH}hadoop fs ";
	string command = "| grep -v Found | awk -F \"/\" '{print $NF}' |awk -F \"-\" '{print $1}' |sort -u`";
	oss << "for f in `" << hadoop << "-ls " << outpath_game_file << command << '\n';
	oss << "do" << "\n\t";
	string::size_type idx = outpath_org.find("${DAY_DIR}/${date}/basic");
    if(idx != string::npos)
    {   
        for(std::set<int>::iterator it = gameId_set.begin(); it != gameId_set.end(); it++) 
        {   
            string gameid = toString(*it);
			oss << hadoop << "-rm -skipTrash " << outpath_org << "/${f}G" << gameid << "-*" << "\n\t";
			oss << hadoop << "-rm -skipTrash " << outpath_org << "/$f-*" << "\n\t";
        }   
    }else{
		oss << hadoop << "-rm -skipTrash " << outpath_org << "/$f-*" << "\n\t";
	}	
	//oss << hadoop << "-cp " << outpath_game_file << " " << outpath_org << "\n";
	oss << hadoop << "-mv " << outpath_game << "/$f-*" << " " << outpath_org << "\n";
	oss << "done" << "\n\n";
}

void StatMrGenerate::load_variable_file()
{
	string value_all,value_temp;
	int uniq_flag = 0;
	size_t index_first = 0, index_last = 0;
	value_all += jobinfo.job_name;
	value_all += jobinfo.jar;
	value_all += jobinfo.params;
	value_all += jobinfo.conf;
	value_all += jobinfo.inputPath;
	value_all += jobinfo.outputPath;

	index_first = value_all.find_first_of("$");
	while(index_first != std::string::npos)
	{
		if(value_all[index_first+1] == '{')
		{
			uniq_flag = 0;
			index_last = value_all.find_first_of("}", index_first+1);
			value_temp = value_all.substr(index_first + 2, index_last - index_first - 2);
			if(var_set.find(value_temp) == var_set.end() || var_set.empty()) 
			{
				var_set.insert(value_temp); 
				if(get_variable(value_temp) == 0)
				{
					check_output_var(value_temp);
				}
			}
			index_first = value_all.find_first_of("$", index_last+1);
		}
	}
}

void StatMrGenerate::load_check_input()
{
	//string input;
	string input_all = jobinfo.inputPath;
	printf("input_all = %s\n",input_all.c_str());
	std::vector<string> input_vec;
	StatCommon::split(input_all, '|', input_vec);
	for(std::vector<string>::iterator input = input_vec.begin(); input != input_vec.end(); input++) 
	{
		check_output_input(*input);
	}
	oss << "if [[ $inputs == \"\" ]]; then" << '\n' << '\t';
	oss << "echo \"empty inputs\"" << '\n' << '\t';
	//oss << "exit 3" << '\n';
	oss << "exit" << '\n';
	oss << "fi" << "\n\n";
}

void StatMrGenerate::load_jobinfo()
{
	oss << "${HADOOP_PATH}hadoop jar " << jobinfo.jar << " \\\n" << '\t';
	oss << jobinfo.driver << " \\\n" << '\t';
	if(jobinfo.params.length() > 0)
	{
		check_output_param();
	}
	oss << "-conf " << jobinfo.conf << " \\\n" << '\t';
	oss << "-jobName " << jobinfo.job_name << " \\\n" << '\t';
	oss << "-gameInfo " << jobinfo.gameInfo << " \\\n" << '\t';
	oss << "-outKey org.apache.hadoop.io." << jobinfo.outKey << " \\\n" << '\t';
	oss << "-outValue org.apache.hadoop.io." << jobinfo.outValue << " \\\n" << '\t';
	oss << "-inFormat org.apache.hadoop.mapred." << jobinfo.inFormat << " \\\n" << '\t';
	oss << "-outFormat org.apache.hadoop.mapred." << jobinfo.outFormat << " \\\n" << '\t';
	oss << "$inputs" << " \\\n" << '\t';
	if(jobinfo.combinerClass.length() > 0)
	{
		oss << "-combinerClass " << jobinfo.combinerClass << " \\\n" << '\t';
	}
	oss << "-reducerClass " << jobinfo.reducerClass << " \\\n" << '\t';
	check_output_outpath();
	if(jobinfo.mos.length() > 0)
	{
		check_output_mos();
	}
}

void StatMrGenerate::load_db_upload()
{
	string db_id_all = jobinfo.dbUpload_id;
	std::vector<string> db_id_vec;
	StatCommon::split(db_id_all, '|', db_id_vec);
	for(std::vector<string>::iterator id = db_id_vec.begin(); id != db_id_vec.end(); id++)
	{
		if(get_dbUpload_info(*id) == 0)
		{
			check_output_dbUpload();
		}
	}
}

void StatMrGenerate::load_mysql_upload()
{
	string db_id_all = jobinfo.mysqlUpload_id;
	std::vector<string> db_id_vec;
	StatCommon::split(db_id_all, '|', db_id_vec);
	for(std::vector<string>::iterator id = db_id_vec.begin(); id != db_id_vec.end(); id++)
	{
		if(get_mysqlUpload_info(*id) == 0)
		{
			check_output_mysqlUpload();
		}
	}
}

int StatMrGenerate::get_dbUpload_info(const string& upload_id)
{
	uint32_t ret;
	static char fields[1024];
	static char sql[1024];

	sprintf(fields, "type,time,task_id,path");
	sprintf(sql, "SELECT %s FROM t_job_db_upload WHERE upload_id='%s'", fields, upload_id.c_str());
	if((ret = mysql.do_sql(sql)) != 0) {
		errorCode = STAT_GET_DBUPLOAD_FAILED;
		return -1;
	}

	MYSQL_ROW row = mysql.get_next_row();
	if(row == NULL)
	{ 
		errorCode = STAT_GET_DBUPLOAD_NULL;
		return -1;
	}
	else
	{
		db_upload_info.type = row[0];
		db_upload_info.time = row[1];
		db_upload_info.task_id = row[2];
		db_upload_info.path = row[3];
		return 0;
	}
}

int StatMrGenerate::get_mysqlUpload_info(const string& upload_id)
{
	uint32_t ret;
	static char fields[1024];
	static char sql[1024];

	sprintf(fields, "table_name,table_field,path,time");
	sprintf(sql, "SELECT %s FROM t_job_mysql_upload WHERE upload_id='%s'", fields, upload_id.c_str());
	if((ret = mysql.do_sql(sql)) != 0) {
		errorCode = STAT_GET_MYSQLUPLOAD_FAILED;
		return -1;
	}

	MYSQL_ROW row = mysql.get_next_row();
	if(row == NULL)
	{ 
		errorCode = STAT_GET_MYSQLUPLOAD_NULL;
		return -1;
	}
	else
	{
		mysql_upload_info.table_name = row[0];
		mysql_upload_info.table_field = row[1];
		mysql_upload_info.path = row[2];
		mysql_upload_info.time = row[3];
		return 0;
	}
}

int StatMrGenerate::get_jobinfo(const int job_id)
{
	uint32_t ret;
	static char fields[1024];
	static char sql[1024];
 
	sprintf(fields, "job_id,job_name,gameInfo,jar,driver,params,conf,outKey,outValue, \
				inFormat,outFormat,inputPath,outputPath,combinerClass,reducerClass,\
				mos,dbUpload_id,mysqlUpload_id,fix_flag");
	sprintf(sql, "SELECT %s FROM t_job_mapreducer WHERE job_id=%d", fields, job_id);
	if((ret = mysql.do_sql(sql)) != 0) {
		errorCode = STAT_GET_JOBINFO_FAILED;
		return -1;
	}

	MYSQL_ROW row = mysql.get_next_row();
	if(row == NULL)
	{ 
		errorCode = STAT_GET_JOBINFO_NULL;
		return -1;
	}
	else
	{
		jobinfo.job_id = row[0];
		jobinfo.job_name = row[1];
		jobinfo.gameInfo = row[2];
		jobinfo.jar = row[3];
        jobinfo.driver = row[4];
		jobinfo.params = row[5];
		jobinfo.conf = row[6];
		jobinfo.outKey = row[7];
		jobinfo.outValue = row[8];
		jobinfo.inFormat = row[9];
		jobinfo.outFormat = row[10];
		jobinfo.inputPath = row[11];
		jobinfo.outputPath = row[12];
		jobinfo.combinerClass = row[13];
		jobinfo.reducerClass = row[14];
		jobinfo.mos = row[15];
		jobinfo.dbUpload_id = row[16];
		jobinfo.mysqlUpload_id = row[17];
		jobinfo.fix_flag = atoi(row[18]);
		return 0;
	}
}

int StatMrGenerate::get_variable(const string& var_name)
{
	uint32_t ret;
	string var_type;
	static char fields[1024];
	static char sql[1024];

	sprintf(fields, "param_content,param_type");
	sprintf(sql, "SELECT %s FROM t_job_parameter WHERE param_name='%s'", fields, var_name.c_str());
	if((ret = mysql.do_sql(sql)) != 0) {
		errorCode = STAT_GET_PARAM_FAILED;
		return -1;
	}

	MYSQL_ROW row = mysql.get_next_row();
	if(row == NULL)
	{ 
		errorCode = STAT_GET_PARAM_NULL;
		return -1;
	}
	else
	{
		var_type = row[1];
		if(var_type.compare("0") == 0)
		{
			var_content = row[0];
			return 0;
		}
		else
		{
			//这里表示查询到的参数为config.sh中的参数，不用返回错误码
			return -1;
		}
	}
}

//判断并输出DB_UPLOAD信息
void StatMrGenerate::check_output_dbUpload()
{
	if(db_upload_info.task_id.compare("-1") == 0)
	{
		oss << "$DB_UPLOAD -type " << db_upload_info.type;
		oss << " -date " << db_upload_info.time;
		oss << " -path " << db_upload_info.path << '\n';
	}
	else
	{
		oss << "$DB_UPLOAD -type " << db_upload_info.type;
		oss << " -date " << db_upload_info.time;
		oss << " -task " << db_upload_info.task_id;
		oss << " -path " << db_upload_info.path << '\n';
	}

}

//判断并输出MYSQL_UPLOAD信息
void StatMrGenerate::check_output_mysqlUpload()
{
	oss << '\n' << "time=`date -d \"${date}\" +%s`" << '\n';
	oss << "$MYSQL_UPLOAD " << mysql_upload_info.table_name << " \\\n" << '\t';
	oss << mysql_upload_info.table_field << " \\\n" << '\t';
	oss << mysql_upload_info.path << " \\\n" << '\t';
	oss << mysql_upload_info.time << "\n\n";
}

//解析mos结构并输出, 格式如下：
//acpaycnt:TextOutputFormat:Text:DoubleWritable|acpaysum:TextOutputFormat:Text:DoubleWritable
void StatMrGenerate::check_output_mos()
{
	string mos_all = jobinfo.mos;
	std::vector<string> mos_vec;
	StatCommon::split(mos_all, '|', mos_vec);
	for(std::vector<string>::iterator mos = mos_vec.begin(); mos != mos_vec.end(); mos++)
	{
		if(mos != (mos_vec.end()-1))
		{
			divide_mos(*mos, 0);
		}
		else
		{
			//最后一个mos，含标志1，最后不会加连接符和制表符
			divide_mos(*mos, 1);
		}
	}
}

//将mos字段按":"分割，并输出
void StatMrGenerate::divide_mos(const string& mos, const int flag)
{
	string mos_value_all = mos;
	std::vector<string> mos_value_vec;
	StatCommon::split(mos_value_all, ':', mos_value_vec);
	string mos_name = mos_value_vec[0];
	string out_format = mos_value_vec[1];
	string in_type = mos_value_vec[2];
	string out_type = mos_value_vec[3];

	oss << "-addMos \"" << mos_name;
	oss << ",org.apache.hadoop.mapred." << out_format;
	oss << ",org.apache.hadoop.io." << in_type;
	if(flag == 0)
	{
		oss << ",org.apache.hadoop.io." << out_type << "\" \\" << '\n' << '\t';
	}
	else
	{
		oss << ",org.apache.hadoop.io." << out_type << "\"\n\n";
	}
}

//根据gameid设置输出路径
void StatMrGenerate::check_output_outpath()
{
	string output_path;

	if(gameId_len > 0 && jobinfo.fix_flag != 3)
	{
		output_path = jobinfo.outputPath + "-game";
	}
	else
	{
		output_path = jobinfo.outputPath;
	}

	if(jobinfo.mos.length() == 0)
	{
		oss << "-output " << output_path << " \n\n";
	}
	else
	{
		oss << "-output " << output_path << " \\\n" << '\t';
	}
}

//input格式：input,mapper 或 input,mapper,0 
//最后带0表示路径可能没有,需要判断
void StatMrGenerate::check_output_input(const string& input)
{
	std::vector<string> value_vec;
	StatCommon::split(input, ',', value_vec);
	string path = value_vec[0];
	string mapper = value_vec[1];

        printf("path=%s, map=%s\n", path.c_str(), mapper.c_str());
	string last_two_char = input.substr(input.length()-2, 2);
	//printf("input = %s\n",input.c_str());
	//printf("path = %s\n",path.c_str());
	//printf("mapper = %s\n",mapper.c_str());
	//printf("last_two_char = %s\n",last_two_char.c_str());
	if(last_two_char.compare(",0") == 0)
	{
		process_input(path, mapper, 0);
	}
	else
	{
		process_input(path, mapper, 1);
	}
}

void StatMrGenerate::process_input(const string& path, const string& mapper, const int flag)
{
	string path_game;
	if(gameId_len > 0 && jobinfo.fix_flag != 3)
	{
		for(std::set<int>::iterator it = gameId_set.begin(); it != gameId_set.end(); it++) 
		{
			string gameid = toString(*it);
			if(jobinfo.fix_flag == 0)
			{
				path_game = path.substr(0,path.length()-1) + gameid + "-*";
			}
			else if(jobinfo.fix_flag == 1)
			{
				path_game = path.substr(0,path.length()-9) + gameid + "/*_basic";
			}
			else if(jobinfo.fix_flag == 2)
			{
				path_game = path.substr(0,path.length()-10) + gameid + "/*_custom";
			}
			string path_game_mapper = path_game + "," + mapper;
			process_output_input(path_game, path_game_mapper, flag);
		}
	}
	else
	{
		string path_mapper = path + "," + mapper;
		process_output_input(path, path_mapper, flag);
	}
}

void StatMrGenerate::process_output_input(const string& path, const string& path_mapper, const int flag)
{
	if(flag == 0)
	{
		oss << "${HADOOP_PATH}hadoop fs -ls " << path << '\n';
		oss << "if [[ $? -eq 0 ]]; then" << '\n' << '\t';
		oss << "inputs=\"$inputs -addInput " << path_mapper << " \"" << '\n';
		oss << "fi" << "\n\n";
	}
	else
	{
		oss << "inputs=\"$inputs -addInput " << path_mapper << " \"" << '\n';
	}
}

//params格式：param1|param2|param3
void StatMrGenerate::check_output_param()
{
	string params_all = jobinfo.params;
	std::vector<string> params_vec;
	StatCommon::split(params_all, '|', params_vec);
	for(std::vector<string>::iterator param = params_vec.begin(); param != params_vec.end(); param++)
	{
		oss << "-D " << *param << " \\" << '\n' << '\t';
	}
}

void StatMrGenerate::check_output_var(const string& var_name)
{
	oss << var_content << '\n';
	if(var_name.compare("date") == 0)
	{
		oss << "if [[ $date == \"\" ]]; then" << "\n\t";
		oss << "echo invalid param: date" << "\n\t";
		oss << "exit 2" << '\n' << "fi" << "\n\n";
	}
	else if(var_name.compare("divide") == 0)
	{
		oss << "if [[ $divide == \"\" ]]; then" << "\n\t";
		oss << "divide=\"true\"" << '\n';
		oss << "fi" << "\n\n";
	}
}

string StatMrGenerate::toString(const int gid)
{
	ostringstream os_value;
	os_value << gid;
	return os_value.str();
}

int StatMrGenerate::write_file(const string& fpath, const string& fname)
{
	string s = oss.str(); 
	char filename[1024] = {0};
	sprintf(filename, "%s/%s", fpath.c_str(), fname.c_str());
	//以只写的方式打开文件，若文件不存在则创建，若文件存在则清空重新写，设置最高权限
	int fd = open(filename, O_WRONLY|O_CREAT|O_TRUNC, 0777);
	if(fd < 0)
	{   
		ERROR_LOG("open file %s error!", filename);
		errorCode = STAT_OPEN_FILE_FAILED;
		return -1; 
	}   
	if (write(fd, s.c_str(), s.size()) == -1) 
	{
		errorCode = STAT_WRITE_FILE_FAILED;
		ERROR_LOG("write file %s error!", filename);
		return -1;
	}
	return 0;
}
