#!/bin/bash

red_clr="\033[31m"
grn_clr="\033[32m"
end_clr="\033[0m"

cd `dirname $0`
cwd=`pwd`

function get_module_name()
{
    echo $(awk '/module-name/{print $2}' ./conf/version.conf)
}

appname=$(get_module_name)
if [ "x$appname" == "x" ];then
    echo "get module-name failed from ./conf/version.conf"
    exit 1
fi

function get_lockfile()
{
    f=$(awk '/pid_file/{printf "%s", $2}' conf/bench.conf)
    echo $f
}

lockfile=$(get_lockfile)

function prepare()
{
    tmpfile=crontab-ori.tempXX
    item='*/1 * * * * cd '${cwd}' && ./keep-alive.sh >> keep-alive.log 2>&1'

    crontab -l >$tmpfile 2>/dev/null

    fgrep "${item}" $tmpfile &>/dev/null
    if [ $? -ne 0 ]
    then
        echo "${item}" >> $tmpfile
        crontab $tmpfile
    fi

    rm -f $tmpfile

    # setup working path
    wpath=$(awk '/work-path/{print $2}' ./conf/${appname}.conf)
    if [ "x$wpath" != "x" ];then
        mkdir -p $wpath
    fi
}

function post_install()
{
    # stat-client working path
    if [ "x$appname" == x"stat-client" ];then
        mkdir -p /opt/taomee/stat/data/{log,inbox}
        chmod -R 777 /opt/taomee/stat/data
    fi

    echo ""
    echo "Installation completed, Please check configurations under directory conf, then run ./${appname}.sh setup."
}

function start()
{
    ./bin/check-single $lockfile
    if [ $? -eq 1 ]
    then
        printf "$red_clr%50s$end_clr\n" "$appname is already running"
        exit 1
    fi

    LD_LIBRARY_PATH="${LD_LIBRARY_PATH}:./bin/" ./bin/${appname} ./conf/bench.conf

    sleep 1
    ./bin/check-single $lockfile
    if [ $? -eq 0 ]
    then
        printf "$red_clr%50s$end_clr\n" "start $appname failed."
        exit 1
    fi

    ./set-keep-alive.sh 1
}

function stop()
{
    ./bin/check-single $lockfile
    running=$?
    if [ $running -eq 0 ]
    then
        printf "$red_clr%50s$end_clr\n" "$appname is not running"
        exit 1
    fi

    ./set-keep-alive.sh 0

    while [ $running -eq 1 ]
    do
        kill `cat $lockfile`
        sleep 1
        ./bin/check-single $lockfile
        running=$?
    done

    printf "$grn_clr%50s$end_clr\n" "$appname has been stopped"
}

function restart()
{
    stop
    start
}

function state()
{
    ./bin/check-single $lockfile
    running=$?
    if [ $running -eq 0 ]
    then
        printf "$red_clr%50s$end_clr\n" "$appname is not running"
        exit 1
    fi

    ps -fs `cat $lockfile`
}

function usage()
{
    echo "$0 <start|stop|restart|state|setup>"
}

if [ $# -ne 1 ]; then
    usage
    exit 1
fi

case $1 in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        restart 
        ;;
    state)
        state 
        ;;
    postinstall)
        post_install
        ;;
    setup)
        prepare
        start
        ;;
    *)
        usage 
        ;;
    esac

exit 0
