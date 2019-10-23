#!/bin/bash
timestamp=$(date +%Y%m%d%H%M%S)
src_file=misc_server
dst_file=$src_file.$timestamp
tmp_dir=/home/kendy/new-misc/bin
dst_dir=/home/kendy/new-misc/stat-misc-0

echo $dst_file
cd $dst_dir
./stat_misc.sh stop
cd -

cd $tmp_dir
rm -rf $src_file
cd -
cp $src_file $tmp_dir/$dst_file

cd $dst_dir/bin
rm -rf $src_file
ln -s $tmp_dir/$dst_file $src_file
cd -

cd $dst_dir
./stat_misc.sh start

exit 0
