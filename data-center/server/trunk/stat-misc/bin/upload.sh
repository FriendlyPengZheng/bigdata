#!/bin/bash
date=$(date +%Y%m%d)
timestamp=$(date +%Y%m%d%H%M%S)
file_name=misc_server
rm_file=$file_name.$date*
bak_file=$file_name.bak.$timestamp
src_file=$file_name
dst_file=$file_name.$timestamp


rm -f $rm_file
mkdir -p backup
cp $file_name ./backup/$bak_file

cd ..
./stat_misc.sh stop
cd -

scp -P22000 tonyliu@10.1.1.63:~/svn/stat/trunk/stat-misc/bin/$src_file $dst_file

if [ -f $dst_file ];then
    ln -sf $dst_file $file_name
fi

cd ..
./stat_misc.sh start

exit 0
