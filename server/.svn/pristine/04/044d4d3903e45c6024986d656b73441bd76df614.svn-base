#!/bin/bash

cd `dirname $0`

function usage()
{
    echo "`basename $0` patch-file-name"
    exit 1
}

function get_version
{
    version=$(awk '/version/{printf "%s", $2}' conf/version.conf)
    echo $version
}

function get_module_name()
{
    echo $(awk '/module-name/{print $2}' ./conf/version.conf)
}

appname=$(get_module_name)
if [ "x$appname" == "x" ];then
    echo "get module-name failed from ./conf/version.conf"
    exit 1
fi

backupfile=./update/${appname}-$(get_version)-bak-$(date +%Y%m%d%H%M%S).tar.gz

# backup old version
# return backup file name
function backup()
{
    echo "backup before updating."
    tar czvf $backupfile bin conf *.sh release-notes.txt
}

# restore old version
function restore()
{
    echo "restore from $backupfile"
    tar xvf $backupfile
}

# update failed, restore and start old version
function process_update_fail()
{
    echo "update failed, restore and start old version."
    ./${appname}.sh stop
    restore
    ./${appname}.sh start 
    rm -frv ./post-update.sh
}

if [ $# -ne 1 ];then
    usage
fi

# log everything
exec &>./install_update.log

patch=$1

if [ ! -e $patch ]
then
    echo "file $patch not found."
    exit 1
fi

./${appname}.sh stop

backup
if [ $(tar tf $backupfile | wc -l) -eq 0 ];then
    echo "backup failed. remove temp file, stop updating."
    rm -vf $backupfile
    ./${appname}.sh start
    exit 1
fi

$patch --target .
if [ $? -ne 0 ];then
    process_update_fail
    exit 1
fi

./${appname}.sh start
ret=$?

if [ $ret -ne 0 ];then
    process_update_fail
fi

rm -frv ./post-update.sh

exit $ret
