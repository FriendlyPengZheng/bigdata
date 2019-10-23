#!/bin/bash

tmpfile="./tmpfile"
tmpidfile="./tmpidfile"
tmpetfile="./tmpetfile"
target="./php/undefined_event_type"
sendmail=0;

touch $tmpfile
touch $tmpidfile
touch $tmpetfile
# 检查是否存在未定义的EventType
data_dir="./data"
dir_to_chk=`date -d '1 days ago' +%Y%m%d`
for dir in `ls -1 $data_dir | xargs`
do
	# 检查目录名是否合法的EventID前缀
	if [[ $dir =~ 16|18|ff ]] ; then
		# do nothing
		echo $dir >> /dev/null
	else
		for sub_dir in `ls -1 $data_dir/$dir | xargs`
		do
			if [ $sub_dir -eq $dir_to_chk ] ; then
				for filename in `ls -1 $data_dir/$dir/$sub_dir | xargs`
				do
					awk '{printf("%X\n",$5)}' $data_dir/$dir/$sub_dir/$filename | sort | uniq >> $tmpetfile
					sendmail=1
				done
			fi
		done
	fi

	for sub_dir in `ls -1 $data_dir/$dir | xargs`
	do
		if [ $sub_dir -eq $dir_to_chk ] ; then
			for filename in `ls -1 $data_dir/$dir/$sub_dir | xargs`
			do
				eventtype=${filename#*-}
				eventtype=${eventtype%%-*}
				if [ $eventtype -lt 1 -o $eventtype -gt 4 ] ; then
					echo $filename >> $tmpfile
					awk '{printf("%X\n",$5)}' $data_dir/$dir/$sub_dir/$filename | sort | uniq >> $tmpidfile
					sendmail=1
				fi
			done
		fi
	done
done

echo "以下文件有未定义EventType的EventID：" > $target
sort $tmpfile | uniq >> $target
echo "==================== 华丽丽的分割线1 ======================" >> $target
echo "以下EventID未定义EventType：" >> $target
sort $tmpidfile | uniq >> $target
echo "==================== 华丽丽的分割线2 ======================" >> $target
echo "以下EventID未定义：" >> $target
sort $tmpetfile | uniq >> $target

if [ $sendmail -eq 1 ] ; then
	cd ./php
	php send_warning.php
	cd ..
fi

rm -rf $tmpfile
rm -rf $tmpidfile
rm -rf $tmpetfile

exit 0
