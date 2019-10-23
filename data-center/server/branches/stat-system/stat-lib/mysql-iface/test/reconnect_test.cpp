/**
 * @file reconnect_test.cpp
 * @author richard <richard@taomee.com>
 * @date 2010-09-15
 */

#include <stdio.h>
#include <unistd.h>

#include "i_mysql_iface.h"

int main(int argc, char **argv)
{
	i_mysql_iface *p_mysql = NULL;
	if (create_mysql_iface_instance(&p_mysql) != 0) {
		fprintf(stderr, "ERROR: create_mysql_iface_instance.\n");
		return -1;
	}
	if (p_mysql->init("10.1.1.44", 3306, "db_stat_config", "stat", "stat@pwd", "utf8") != 0) {
		fprintf(stderr, "ERROR: p_mysql->init.\n");
		return -1;
	}
	
	if (p_mysql->execsql("DELETE FROM t_ucount;") < 0) {
		fprintf(stderr, "ERROR: p_mysql->execsql: %s: %s\n", "DELETE FROM unique_data_day_all;", 
					p_mysql->get_last_errstr());
		return -1;
	}

	int i = 0;
	while (i != 10000) {
		if (p_mysql->execsql("INSERT INTO t_ucount(report_id, day, count)"
						     "VALUES(%d, 1, 1);", i) < 0) {
			fprintf(stderr, "ERROR: p_mysql->execsql: %s\n", p_mysql->get_last_errstr());
			sleep(1);
			continue;
		}
		++i;
		sleep(1);
	}

	if (p_mysql->uninit() != 0) {
		fprintf(stderr, "ERROR: p_mysql->uninit.\n");
		return -1;
	}
	if (p_mysql->release() != 0) {
		fprintf(stderr, "ERROR: p_mysql->release.\n");
		return -1;
	}

	return 0;
}

