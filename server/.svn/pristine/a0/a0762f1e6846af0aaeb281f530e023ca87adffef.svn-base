#! /bin/bash

username="root"
passwd="ta0mee"
host="10.1.1.44"

sql="CREATE DATABASE db_stat_config_tw;"
echo $sql
#	mysql -u$username -p$passwd -h$host -e "$sql"

for db_num in `seq 1 100`; do
	sql="CREATE DATABASE db_stat_report_tw_$db_num;"
	echo $sql
#	mysql -u$username -p$passwd -h$host -e "$sql"
done

for db_num in `seq 1 100`; do
	for tb_num in `seq 0 99`; do
		sql="use db_stat_report_tw_$db_num; CREATE TABLE t_report_$tb_num (
			id int(11) NOT NULL,
			time int(10) NOT NULL,
			value decimal(20,2) default '0.00',
			PRIMARY KEY  (id,time)
			) ENGINE=InnoDB DEFAULT CHARSET=utf8;"
		echo $sql
#	mysql -u$username -p$passwd -h$host -e "$sql"
	done
done

for db_num in `seq 1001 1010`; do
	sql="CREATE DATABASE db_stat_result_tw_$db_num;"
echo $sql
#	mysql -u$username -p$passwd -h$host -e "$sql"
done

for db_num in `seq 1001 1010`; do
	for tb_num in `seq 0 99`; do
		sql="use db_stat_result_tw_$db_num; CREATE TABLE t_result_$tb_num (
			id int(11) NOT NULL,
			time int(10) NOT NULL,
			value decimal(20,2) default '0.00',
			PRIMARY KEY  (id,time)
			) ENGINE=InnoDB DEFAULT CHARSET=utf8;"
		echo $sql
#	mysql -u$username -p$passwd -h$host -e "$sql"
	done
done
