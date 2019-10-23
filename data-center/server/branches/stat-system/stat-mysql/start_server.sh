#!/bin/bash

red_clr="\033[31m"
grn_clr="\033[32m"
end_clr="\033[0m"

if test -e ./bin/daemon.pid; then
	pid=`cat ./bin/daemon.pid`
	result=`ps -p $pid | wc -l`
	if test $result -gt 1; then
		printf "$red_clr%50s$end_clr\n" "STAT_MYSQL is running"
		exit -1
	fi
fi

cd ./bin && ./async_server ../conf/bench.conf

# if test $? -ne 0; then
# 	printf "$red_clr%50s$end_clr\n" "failed to start STAT_MYSQL"
# else 
# 	printf "$grn_clr%50s$end_clr\n" "STAT_MYSQL has been started"
# fi

