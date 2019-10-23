#!/bin/bash

cwd=`pwd`

function setup_crontab()
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
}

function keep_alive()
{
        # the following directories may be removed by others, always create them.
        mkdir -p /opt/taomee/stat/spool
        mkdir -p /opt/taomee/stat/spool/inbox
        mkdir -p /opt/taomee/stat/spool/outbox
        mkdir -p /opt/taomee/stat/spool/sent
        mkdir -p /opt/taomee/stat/spool/preserve
        mkdir -p /opt/taomee/stat/spool/unrouted
        mkdir -p /opt/taomee/stat/spool/unserved

        chmod 777 /opt/taomee/stat/spool
        chmod 777 /opt/taomee/stat/spool/{inbox,outbox,sent,preserve,unrouted,unserved}

        opt_free_size=$(df /opt/taomee/stat/ | awk 'NR==2 {printf $4}')

        if [ -n $opt_free_size ];then
                ps -elf | fgrep bin/tunnel_client | grep -v grep &>/dev/null
                pid_exist=$?

                # stat-cli-new is not running 
                if [ $pid_exist -ne 0 ];then
                        # hard disk full, the transfer_0 is broken, move it.
                        if [ $opt_free_size -eq 0 ];then
                                mv -f /opt/taomee/stat/spool/outbox/transfer_0 /opt/taomee/stat/spool/transfer_0_err
                        else
                                # stat-cli-new exited abnormally, it may caused by sliding win error
                                echo "stat-cli-new exited abnormally, start stat-cli-new at $(date)"
                                mv -f /opt/taomee/stat/spool/outbox/transfer_0 /opt/taomee/stat/spool/transfer_0_ok
                                cd /opt/taomee/stat/stat-cli-new/ && ./start_client.sh
                                sleep 5
                                mv /opt/taomee/stat/spool/transfer_0_ok /opt/taomee/stat/spool/inbox/stat_ok.log
                        fi
                fi
        fi
}

if [ x$1 == "xsetup" ];then
        setup_crontab
else
        keep_alive
fi

exit 0

