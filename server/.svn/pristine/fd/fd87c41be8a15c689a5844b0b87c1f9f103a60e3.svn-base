#!/bin/bash

set -x

cd `dirname $0`

if [ $# -eq 0 ];then
    make -C ./src/
    exit 0
fi

function get_version
{
    version=$(awk '/version/{printf "%s", $2}' conf/version.conf)
    echo $version
}

function rebuild()
{
    make clean -C ../stat-common
    make -C ../stat-common

    make clean -C ../tool
    make -C ../tool

    make clean -C ./src
    make -C ./src
}

function remove_log()
{
    rm -vfr ./log/*
}

function build_setup()
{
    appname=$1

    rm -fr ./bin/${appname} ./bin/${appname}.so ./bin/check-singal

    make clean -C ../async_server/src
    make -C ../async_server/src
    cp -v ../async_server/bin/async_server ./bin/${appname}

    make clean -C ../tool/
    make -C ../tool/
    cp -v ../tool/check-single ./bin/
}

function build_dist()
{
    appname=$1

    build_setup $appname

    rebuild

    rm -vfr ./${appname}/*
    mkdir -vp ${appname}/bin ${appname}/conf ${appname}/log ${appname}/update

    cp -v bin/${appname} bin/*.so ${appname}/bin
    cp -v bin/${appname} bin/libprotobuf.so.8 ${appname}/bin
    cp -v bin/alarm-sender ${appname}/bin
    cp -v ../tool/check-single ${appname}/bin
    cp -v ../tool/set-keep-alive.sh ./${appname}
    cp -v conf/*.conf ${appname}/conf
    cp -v ${appname}.sh ./${appname}
    cp -v update.sh ./${appname}
    cp -v keep-alive.sh ./${appname}
    cp -v release-notes.txt ./${appname}

    # nobody can modify version.conf.
    chmod -w ${appname}/conf/version.conf

    version=$(get_version)
    if [ x"$version" == "x" ];then
        echo "get version from conf/version.conf failed."
        exit 1
    fi
    pkgname="./update/release/${appname}-${version}-bz2.run"

    ../tool/makeself-2.1.5/makeself.sh --bzip2 --notemp ./${appname} $pkgname "${appname} version $version" ./${appname}.sh postinstall 

    echo "build $pkgname OK, removing temp files."
    rm -vfr $appname 
}

function build_patch()
{
    appname=$1

    build_setup $appname

    rebuild

    rm -vfr ./${appname}/*

    # always install version.conf and release notes
    files="$2 conf/version.conf release-notes.txt"

    for f in $files
    do
        install -D $f ./${appname}/$f
        if [ $? -ne 0 ];then
            echo "install $f failed. exiting ..."
            rm -vfr ./${appname}/*
            exit 1
        fi
    done

    # nobody can modify version.conf and release notes.
    chmod -w ./${appname}/conf/version.conf
    chmod -x ./${appname}/conf/version.conf
    chmod -w ./${appname}/release-notes.txt
    chmod -x ./${appname}/release-notes.txt

    version=$(get_version)
    if [ x"$version" == "x" ];then
        echo "get version from conf/version.conf failed."
        exit 1
    fi
    pkgname="./update/patch/${appname}-${version}-patch-bz2.run"

    post_update_script="./post-update/post-update-default.sh"
    if [ -e ./post-update/post-update-${version}.sh ];then
        post_update_script="./post-update/post-update-${version}.sh"
    fi

    chmod +x $post_update_script
    cp -v $post_update_script ./${appname}/post-update.sh

    ../tool/makeself-2.1.5/makeself.sh --bzip2 --notemp ./${appname} $pkgname "${appname} version $version" ./post-update.sh

    echo "build $pkgname OK, removing temp files."
    rm -vfr $appname 
}

function build_clean()
{
    appname=$1

    make clean -C ./src
    remove_log
    rm -vfr ./$appname update/release/*-bz2.run 
    rm -vfr ./$appname update/patch/*-bz2.run 
}

function get_module_name()
{
    echo $(awk '/module-name/{print $2}' ./conf/version.conf)
}

module_name=$(get_module_name)
if [ "x$module_name" == "x" ];then
    echo "get module-name failed from ./conf/version.conf"
    exit 1
fi

if [ "x$1" == x"log" -o "x$1" == x"l" ];then
    remove_log
fi

# build and create a tarboo for installation
if [ "x$1" == x"dist" -o "x$1" == x"d" ];then
    build_dist $module_name
elif [ "x$1" == x"patch" -o "x$1" == x"p" ];then
    shift 1
    if [ x"$*" != "x" ];then
        build_patch $module_name "$*"
    else
        echo "no file to pack."
    fi
elif [ "x$1" == x"clean" -o "x$1" == x"c" ];then
    build_clean $module_name
elif [ "x$1" == x"setup" -o "x$1" == x"s" ];then
    build_setup $module_name
elif [ "x$1" == x"rebuild" -o "x$1" == x"r" ];then
    build_clean $module_name
    rebuild
fi

shift 1

if [ "x$1" == x"log" -o "x$1" == x"l" ];then
    remove_log
fi

exit 0
