#!/bin/bash

red_clr="\033[31m"
grn_clr="\033[32m"
end_clr="\033[0m"

pid=`cat ./bin/daemon.pid`
result=`ps -p $pid | wc -l`

if [ $result -gt 1 ]; then
        kill $pid
        sleep 1
        pid=`cat ./bin/daemon.pid`
        result=`ps -p $pid | wc -l`
        if [ $result -gt 1 ]; then
                printf "$red_clr%50s$end_clr\n" "stat_sdk is still running"
        else
                printf "$grn_clr%50s$end_clr\n" "stat_sdk has been stopped"
        fi
else
        printf "$red_clr%50s$end_clr\n" "stat_sdk is not running"
fi      
exit 
