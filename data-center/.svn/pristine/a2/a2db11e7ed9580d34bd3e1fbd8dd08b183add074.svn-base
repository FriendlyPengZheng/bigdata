#!/bin/bash

if [ $# -eq 0 ];then
    make -C ./src/
    make -C ./src/proto_so/proto_file/
    make -C ./src/proto_so/proto_mysql/
    exit 0
fi

appname=stat-server
tarname=${appname}.tar.bz2

# build and create a tarboo for installation
if [ "x$1" == x"dist" -o "x$1" == x"d" ];then
    make clean -C ./src
    make -C ./src
    make -C ./src/proto_so/proto_file/
    make -C ./src/proto_so/proto_mysql/

    rm -vfr ./${appname}/*
    mkdir -vp ${appname}/bin/proto_so ${appname}/conf ${appname}/log ${appname}/db

    cp -v bin/async_server bin/stat_server.so ${appname}/bin
    cp -v bin/proto_so/*.so ${appname}/bin/proto_so
    cp -v conf/bench.conf conf/bench_work.conf conf/stat_server.ini ${appname}/conf
    cp -v start_server.sh stop_server.sh state_server.sh ./${appname}

    tar cjvf $tarname $appname 
    rm -vfr $appname 
elif [ "x$1" == x"clean" -o "x$1" == x"c" ];then
    make clean -C ./src
    rm -vfr ./$appname $tarname 
fi

