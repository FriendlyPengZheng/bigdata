#!/bin/bash

red_clr="\033[31m"
grn_clr="\033[32m"
end_clr="\033[0m"

if [ -e ./bin/daemon.pid ];then
    pid=`cat ./bin/daemon.pid`
    result=`ps -p $pid | wc -l`
    if test $result -gt 1; then
        printf "$grn_clr%50s$end_clr\n" "stat_proxy is running"
        exit 1
    fi
fi

export LD_LIBRARY_PATH=../lib:$LD_LIBRARY_PATH && 
cd ./bin/ && ./stat_proxy

