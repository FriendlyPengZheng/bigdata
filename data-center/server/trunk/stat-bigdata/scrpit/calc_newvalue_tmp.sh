WORKDIR=`dirname $0`
WORKDIR=`cd $WORKDIR && pwd`
cd $WORKDIR
echo workdir = $WORKDIR

source config.sh

date=$1

if [[ $date == "" ]]; then
    echo invalid param: date
    exit
fi

inputs=""
for n in {0..30}
do
	nday=`date -d "$date -${n} day" +%Y%m%d`
	path=${DAY_DIR}/$nday/basic/acpay-*
	#${HADOOP_PATH}hadoop fs -ls $path
	#if [[ $? -eq 0 ]]; then
		inputs="$inputs -addInput $path,com.taomee.bigdata.task.newvalue.SourcePayMapper "
	#fi
done
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D day=`date "-d $date -30 day" +%s` \
	-conf ${HADOOP_CONF} \
	-jobName "LTV $date" \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${ALL_DIR}/$date/account-all/part-*,com.taomee.bigdata.task.newvalue.NewDayMapper \
	$inputs \
	-reducerClass com.taomee.bigdata.task.newvalue.LTVReducer \
	-output ${DAY_DIR}/$date/ltv

mos=""
for n in {0..30}
do
	for t in LTV percent sum cnt
	do
		mos="$mos -addMos $n$t,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.FloatWritable "
	done
done
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=4 \
	-conf ${HADOOP_CONF} \
	-jobName "LTV Sum $date" \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.FloatWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-mapperClass  com.taomee.bigdata.task.newvalue.LTVSumMapper \
	-reducerClass com.taomee.bigdata.task.newvalue.LTVSumReducer \
	-input ${DAY_DIR}/$date/ltv/part-* \
	$mos \
	-output ${SUM_DIR}/$date/ltv

for n in {0..30}
do
	path=${SUM_DIR}/$date/ltv/`expr 30 \- $n`
	d=`date -d "$date -$n day" +%Y%m%d`
	$DB_UPLOAD -type 2 -date $d -task 290 -path ${path}LTV-*
	$DB_UPLOAD -type 2 -date $d -task 291 -path ${path}percent-*
	$DB_UPLOAD -type 2 -date $d -task 292 -path ${path}sum-*
	$DB_UPLOAD -type 2 -date $d -task 293 -path ${path}cnt-*
done
