#!/bin/bash

# 统计数据按照新老平台分成两个目录存储，
# old_stat_data目录存储老平台的数据，
# data目录存储新平台的数据

# 删除老平台的数据文件
data_dir="./old_stat_data"
dir_to_del=`date -d '7 days ago' +%Y%m%d`
for dir in `ls -1 $data_dir | xargs`
do
	if [ $dir -lt $dir_to_del ] ; then
		rm -rf "$data_dir/$dir"
	else
		break
	fi
done

# 删除新平台的数据文件
data_dir="./data"
dir_to_del=`date -d '7 days ago' +%Y%m%d`
for dir in `ls -1 $data_dir | xargs`
do
	for sub_dir in `ls -1 $data_dir/$dir | xargs`
	do
		if [ $sub_dir -lt $dir_to_del ] ; then
			rm -rf "$data_dir/$dir/$sub_dir"
		else
			break
		fi
	done
done

exit 0
