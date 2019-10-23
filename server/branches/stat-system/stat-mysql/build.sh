#!/bin/bash

if [ $# -eq 0 ];then
    make -C ./src/
    exit 0
fi

appname=stat-mysql
tarname=${appname}.tar.bz2

# build and create a tarboo for installation
if [ "x$1" == x"dist" -o "x$1" == x"d" ];then
    make clean -C ./src
    make -C ./src

    rm -vfr ./${appname}/*
    mkdir -vp ${appname}/bin ${appname}/conf ${appname}/log

    cp -v bin/async_server bin/stat_mysql.so ${appname}/bin
    cp -v conf/bench.conf conf/bench_work.conf conf/stat_mysql.ini ${appname}/conf
    cp -v start_server.sh stop_server.sh state_server.sh ./${appname}

    tar cjvf $tarname $appname 
    rm -vfr $appname 
elif [ "x$1" == x"clean" -o "x$1" == x"c" ];then
    make clean -C ./src
    rm -vfr ./$appname $tarname 
fi

