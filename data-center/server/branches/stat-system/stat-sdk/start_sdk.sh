#!/bin/bash

red_clr="\033[31m"
grn_clr="\033[32m"
end_clr="\033[0m"

pid=`cat ./bin/daemon.pid`
result=`ps -p $pid | wc -l`

if [ $result -gt 1 ]; then
    printf "$grn_clr%50s$end_clr\n" "stat_sdk is running"
else
    cd ./bin && ./StatSDK ../conf/bench.conf
    sleep 1
    cd ..
    pid=`cat ./bin/daemon.pid`
    result=`ps -p $pid | wc -l`
    if [ $result -gt 1 ]; then
        printf "$grn_clr%50s$end_clr\n" "stat_sdk is running"
    else
        printf "$red_clr%50s$end_clr\n" "stat_sdk is not running"
    fi
    exit
fi
