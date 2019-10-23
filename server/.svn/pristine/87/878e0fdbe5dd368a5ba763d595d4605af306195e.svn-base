date=$1
if [[ $date == "" ]]; then
	echo invalid param: date
	exit
fi
rm -rf merge$date
mkdir merge$date
for f in `/opt/taomee/hadoop/hadoop/bin/hadoop fs -ls /bigdata/output/day/$date/basic/*-m-0* | grep -v Found | grep -v bz2 | awk '{print $8}' | awk -F'-' '{print $1}' | awk -F'/' '{print $NF}' | sort | uniq`
do
	#2014.07.22 改为不压缩，测试耗时减少多少
	#/opt/taomee/hadoop/hadoop/bin/hadoop fs -cat /bigdata/output/day/$date/basic/$f-m-0* | bzip2 > merge$date/$f-m-megre.bz2
	/opt/taomee/hadoop/hadoop/bin/hadoop fs -cat /bigdata/output/day/$date/basic/$f-m-0* > merge$date/$f-m-megre
done
#/opt/taomee/hadoop/hadoop/bin/hadoop fs -moveFromLocal merge$date/*-m-megre.bz2 /bigdata/output/day/$date/basic/
/opt/taomee/hadoop/hadoop/bin/hadoop fs -moveFromLocal merge$date/*-m-megre /bigdata/output/day/$date/basic/
/opt/taomee/hadoop/hadoop/bin/hadoop fs -rm -skipTrash /bigdata/output/day/$date/basic/*-m-0*
rm -rf merge$date
