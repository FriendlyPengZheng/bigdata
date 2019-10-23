#!/bin/bash

red_clr="\033[31m"
grn_clr="\033[32m"
end_clr="\033[0m"


# kill之后9秒内，程序没有退出则使用kill -9
KILL_SLEEP=1

#server=$(basename $0 .sh)
server="misc_server"
log_dir="./log/"
pid_file="./bin/daemon.pid"

config_file="./conf/misc.conf"

listen_ip="10.1.1.44"
listen_port="9900"


function get_pid()
{
    local server_name="$1"
    local pid_list=$(pgrep -P 1 $server_name)
    local pid=
    local cwd=

    if [ -z "$pid_list" ]; then
        echo 0
        return 0
    fi

    for pid in $pid_list
    do

        cwd=`readlink /proc/$pid/cwd`
        if [ "$cwd" = $(pwd -P) ]; then
            echo $pid
            return 1
        fi
    done


    echo 0
    return 0
}


pid=`get_pid $server`
pids=`cat $pid_file`

function clear_log()
{
    if [ "$1" = "log" -o "$1" = "l" ]; then
        rm $log_dir/* -rf
        printf "$grn_clr%50s$end_clr\n" "log files has been removed"
    fi
}

function is_server_running()
{
    ps_num=`ps -f $pids | wc -l`
    if [ $ps_num -gt 1 ];then
        echo 1
    else
        echo 0
    fi
}

function state_server()
{
    tips=`ps -o user,pid,stat,pcpu,pmem,cmd -s $pids`
    printf "$grn_clr%50s$end_clr\n" "$tips"
    for pid in $pids
    do
        srv_name=$server"_"$pid
        result=`ps -p $pid | wc -l`
        if [ $result -lt 2 ]; then
            printf "$red_clr%50s$end_clr\n" "ERROR: $srv_name is not running"
        fi
    done

}

function start_server()
{
    #if [ -d $lib_path ]; then
    #export LD_LIBRARY_PATH=$lib_path
    #else
    #export LD_LIBRARY_PATH=$lib_path_release
    #fi
    export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:`pwd`/bin"

    is_running=`is_server_running`
    if [ $is_running = "0" ];then
        ./bin/spawn-fcgi -a $listen_ip -p $listen_port -F 8 -P "$pid_file" -f "./bin/$server $config_file"
        cp $pid_file $pid_file.bak
    else
        printf "$red_clr%50s$end_clr\n" "ERROR: $server is still running"
        state_server
    fi
}

function stop_server()
{
    is_running=`is_server_running`
    if [ $is_running = "0" ];then
        printf "$red_clr%50s$end_clr\n" "ERROR: $server is not running"
    else
        for pid in $pids
        do
            srv_name=$server"_"$pid
            result=`ps -p $pid | wc -l`
            if [ $result -le 1 ]; then
                printf "$red_clr%50s$end_clr\n" "ERROR: $srv_name is not running"
            else
                child_pids=`pgrep -P $pid | xargs`
                kill -SIGTERM $pid
                count=0
                while test $result -gt 1; do
                    let count="$count+1"
                    # 9次之后用kill -9
                    if [ $count -gt $KILL_SLEEP ]; then
                        kill -9 $pid $child_pids
                        break
                    fi
                    sleep 1
                    result=`ps -p $pid | wc -l`
                done

                printf "$grn_clr%50s$end_clr\n" "$srv_name has been stopped"
            fi
        done
    fi
}



if [ "$1" = "start" ]; then
    start_server;
    clear_log "$2"

elif [ "$1" = "state" ]; then
    state_server

elif [ "$1" = "stop" ]; then
    stop_server;
    clear_log "$2"

elif [ "$1" = "restart" ]; then
    stop_server;
    clear_log "$2";
    start_server;

elif [ "$1" = "r" ]; then
    stop_server;
    clear_log "$2";
    start_server;

else
    printf "Usage: %s start|state|stop|restart [log]\n" $0
fi


