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


#include "stat_shphp_generate.hpp"

StatShphpGenerate::StatShphpGenerate() 
{
	errorCode = STAT_JOB_NORMAL;
	jobName = "";
    printf("this is StatShphpGenerate()\n");
}

StatShphpGenerate::~StatShphpGenerate()
{
	mysql.uninit();
    printf("this is ~statShphpGenerate()\n");
}

const char* StatShphpGenerate::get_jobname(int job_id)
{
    uint32_t ret;
    static char sql[1024];
 
    sprintf(sql, "SELECT file_name FROM t_job_shphp WHERE job_id=%d", job_id);
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
int StatShphpGenerate::process_job(int job_id, const string& fpath, const string& fname, const int gid[], int glen)
{
	printf("this is StatShphpGenerate::process_job\n");
	printf("job_id=%d, fpath=%s, fname=%s, gid=%d, glen=%d\n",
			job_id,fpath.c_str(),fname.c_str(),gid[0],glen);

	//数据库中获取所有job信息
	if(get_jobinfo(job_id) < 0)
	{
		return errorCode;
	}
	//装载文件头部
	load_head_file();
	//装载参数信息
	load_variable_file();
	//装载job信息
	load_jobinfo();
	//装载exit_code信息
	load_exitCode();

	//写文件
	if(write_file(fpath, fname) < 0)
	{
		return errorCode;
	}
	//清空数据输出流
	oss.str("");
	//清空变量空间
	var_set.clear();

	return errorCode;

}

void StatShphpGenerate::load_head_file()
{
	oss << "export LANG=en_US.UTF-8" << '\n';
	oss << "WORKDIR=`dirname $0`" << '\n';
	oss << "WORKDIR=`cd $WORKDIR && pwd`" << '\n';
	oss << "cd $WORKDIR" << '\n';
	oss << "echo workdir = $WORKDIR" << '\n' << '\n';
	oss << "source config.sh" << '\n';
}

void StatShphpGenerate::load_exitCode()
{
	oss << "if [[ $? > 0 ]]; then" << '\n' << '\t';
	oss << "exit 1" << '\n' << "fi" << "\n\n";
}

void StatShphpGenerate::load_variable_file()
{
	string value_all,value_temp;
	int uniq_flag = 0;
	size_t index_first = 0, index_last = 0;
	value_all += jobinfo.params;
	value_all += jobinfo.log_path;

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

void StatShphpGenerate::load_jobinfo()
{
	//根据后缀判断是sh 还是 php
	string name = jobinfo.file_name;
	string last_three_char = name.substr(name.length()-3, 3);
	if(last_three_char.compare(".sh") == 0)
	{
		check_output_shphp("sh");
	}
	else if(last_three_char.compare("php") == 0)
	{
		check_output_shphp("php");
	}
	else
	{
		errorCode = STAT_FILE_SUFFIX_NOT_MATCH;
	}
}

int StatShphpGenerate::get_jobinfo(const int job_id)
{
	uint32_t ret;
	static char fields[1024];
	static char sql[1024];
 
	sprintf(fields, "file_name,params,log_path");
	sprintf(sql, "SELECT %s FROM t_job_shphp WHERE job_id=%d", fields, job_id);
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
		jobinfo.file_name = row[0];
		jobinfo.params = row[1];
		jobinfo.log_path = row[2];
		return 0;
	}
}

int StatShphpGenerate::get_variable(const string& var_name)
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
			return -1;
		}
	}
}

void StatShphpGenerate::check_output_shphp(const string& shphp)
{
	if(jobinfo.params.length() > 0) 
	{
		oss << shphp << " " << jobinfo.file_name << " " << jobinfo.params << "\n\n"; 
	}
	else if(jobinfo.params.length() == 0) 
	{
		oss << shphp << " " << jobinfo.file_name << "\n\n"; 
	}
}

void StatShphpGenerate::check_output_var(const string& var_name)
{
	oss << var_content << '\n';
	if(var_name.compare("date") == 0)
	{
		oss << "if [[ $date == \"\" ]]; then" << "\n\t";
		oss << "echo invalid param: date" << "\n\t";
		oss << "exit 2" << '\n' << "fi" << "\n\n";
	}
}

int StatShphpGenerate::write_file(const string& fpath, const string& fname)
{
	string s = oss.str(); 
	char filename[1024] = {0};
	sprintf(filename, "%s/%s", fpath.c_str(), fname.c_str());
	//以只写的方式打开文件，若文件不存在则创建，若文件存在则清空重新写，设置最高权限
	int fd = open(filename, O_WRONLY|O_CREAT|O_TRUNC, 0777);
	if(fd < 0)
	{   
		printf("open file error!");
		errorCode = STAT_OPEN_FILE_FAILED;
		return -1; 
	}   
	if (write(fd, s.c_str(), s.size()) == -1) 
	{
		printf("write file error!");
		errorCode = STAT_WRITE_FILE_FAILED;
		return -1;
	}
	return 0;
}
