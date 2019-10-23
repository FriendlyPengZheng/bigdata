#!/bin/bash

if [ $# -eq 0 ];then
    make -C ./src/
    exit 0
fi

appname=stat-proxy
tarname=${appname}.tar.bz2

# build and create a tarboo for installation
if [ "x$1" == x"dist" -o "x$1" == x"d" ];then
    make clean -C ./src
    make -C ./src

    rm -vfr ./${appname}/*
    mkdir -vp ${appname}/bin ${appname}/conf ${appname}/log ${appname}/db ${appname}/lib

    cp -v bin/stat_proxy ${appname}/bin
    cp -v conf/proxy.ini ${appname}/conf
    cp -v start_proxy.sh stop_proxy.sh state_proxy.sh ./${appname}
    cp -v db/db_create.sh ${appname}/db
    cp -v lib/libstat.so.1 ${appname}/lib

    tar cjvf $tarname $appname 
    rm -vfr $appname 
elif [ "x$1" == x"clean" -o "x$1" == x"c" ];then
    make clean -C ./src
    rm -vfr ./$appname $tarname 
fi

