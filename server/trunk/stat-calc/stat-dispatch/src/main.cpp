/**
 * =====================================================================================
 *   Compiler   g++
 *   Company    TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2014, TaoMee.Inc, ShangHai.
 *
 *   @brief   统计平台调度系统（测试）
 *   @author  kendy<kendy@taomee.com>
 *   @date    2014-09-24
 * =====================================================================================
 */
#include <stdio.h>
#include <iostream>
#include <stdlib.h>
#include <string>
#include "log.h"
#include "config.h"
#include "stat_job_generate.hpp"
#include "stat_mr_generate.hpp"
#include "stat_shphp_generate.hpp"
#include "c_mysql_connect_auto_ptr.h"

using std::string;
using namespace std;
int main(int argc, char *argv[])
{
	printf("start program!\n");
	printf("argv[] %s \n", argv[1]);
	//DEBUG_LOG("start program!");
    ///***********************************************
	// * Mysql初始化及建立连接
	// ***********************************************/
	//c_mysql_connect_auto_ptr mysql;
    //if(load_config_file("../conf/dispatch.conf")) {
	//	if(mysql.init(config_get_strval("db_host", ""),
	//					config_get_strval("db_user", ""),
	//					config_get_strval("db_passwd", ""),
	//					config_get_strval("db_name", ""),
	//					config_get_intval("db_port", 0), 
	//					CLIENT_INTERACTIVE)) {
	//		printf("can not connect to mysql [%s]\n", mysql.m_error());
	//		sleep(1);
	//		printf("can not connect to mysql [%s]\n", mysql.m_error());
	//	}   
	//}else{
	//	printf("load config file error\n");
	//}  
	StatJobGenerate *p,*q;
	const char* db_host = "10.1.1.60";
	const char* db_user = "root";
	const char* db_passwd = "pwd@60";
	const char* db_name = "db_td_config";
	int port = 3306;

	p = new StatMrGenerate();
	int gid[] = {1,2};
	p->set_mysql(db_host, db_user, db_passwd, db_name, port);

	int i=15;
	int errorCode = -1;
	errorCode = p->process_job(i, "jobfile", "1.sh", gid, 2);
	//errorCode = p->process_job(i, "jobfile", "3.sh", gid, 0);
//	stringstream ss;
//	string name;
//	for(i = 2;i<50;i++)
//	{
//		ss << i;
//		name = ss.str();
//		name += ".sh";
//		p->process_job(i, "jobfile", name , gid, 2);
//		ss.str("");
//	}
	printf("errorCode = %d\n",errorCode);

	const char* jobname = p->get_jobname(i);
	//jobname = p->get_jobname(i);
	cout << "jobname = " << jobname << std::endl;

//	q = new StatShphpGenerate();
//	q->set_mysql(db_host, db_user, db_passwd, db_name, port);
//
//	int j=52;
//	errorCode = q->process_job(j, "jobfile", "2.sh", gid, 0);
//	printf("errorCode = %d\n",errorCode);
//
//	const char* jobname2 = q->get_jobname(j);
	//jobname2 = q->get_jobname(j);
//	cout << "jobname = " << jobname2 << std::endl;
//	for(i=1;i<=49;i++)
//	{		
//		p->process_job(i, "jobfile", "test.sh", gid, 0);
//	}
//	mysql.uninit();
	
	return 0;
}
