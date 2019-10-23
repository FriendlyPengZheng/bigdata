date=$1
if [[ $date == "" ]]; then
	date=`date -d "-1 day" +%Y%m%d`
fi
mkdir log/$date -p
exec 1>log/$date/merge 2>&1
echo `date`
HADOOP='/opt/taomee/hadoop/hadoop/bin/hadoop'
DAY_PATH='/stat/output/day'
LOCAL_PATH=result-merge-$date
#uvalue
rm -rf merge-uvalue
mkdir merge-uvalue
for f in `$HADOOP fs -ls $DAY_PATH/$date/uvalue/uvalue*-r-0* | grep -v Found | awk '{print $8}' | awk -F'-' '{print $1}' | awk -F'/' '{print $NF}' | sort | uniq`
do
	$HADOOP fs -text $DAY_PATH/$date/uvalue/$f-r-0* | gzip > merge-uvalue/$f-r-merge.gz
done
$HADOOP fs -moveFromLocal merge-uvalue/uvalue*-r-merge.gz $DAY_PATH/$date/uvalue/
$HADOOP fs -rm -skipTrash $DAY_PATH/$date/uvalue/uvalue*-r-0*
rm -rf merge-uvalue
#result
# 大于256M的文件用bz2压缩
for path in `$HADOOP fs -du $DAY_PATH/$date/result/ | grep -v Found | awk '{if($1>268435456) print $2}'`
do
	id=`echo $path| awk -F'/' '{print $NF}'`
	sh compression.sh $path > log/$date/merge-$id.log 2>&1 &
done
rm -rf $LOCAL_PATH
mkdir $LOCAL_PATH
# 将小于256M的文件压缩并重新上传
for path in `$HADOOP fs -du $DAY_PATH/$date/result/ | awk '{if($1>0 && $1<=268435456) print $2}'`
do
	id=`echo $path| awk -F'/' '{print $NF}'`
	$HADOOP fs -text $path/detail-r-* | gzip > $LOCAL_PATH/detail-merge-$id.gz
	$HADOOP fs -copyFromLocal $LOCAL_PATH/detail-merge-$id.gz $path/ &
	$HADOOP fs -rm -skipTrash $path/detail-r-* &
done
#rm -rf $LOCAL_PATH
echo `date`
