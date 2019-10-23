#月用户付费
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

#month date
year_month=`date -d "$date" +%Y%m`

#task_id=125,126
#输出每人的付费额、次数
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D mapred.reduce.tasks=4 \
	-conf ${HADOOP_CONF} \
	-addInput ${ALL_DIR}/$date/account-all/firstLog*,com.taomee.bigdata.task.segpay.RangeUserMapper \
	-addInput ${DAY_DIR}/$date/pay-day/part-*,com.taomee.bigdata.task.segpay.MonACPayMapper \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.FloatWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "New Pay Day step1 $date" \
	-reducerClass com.taomee.bigdata.task.segpay.DiffIntReducer \
	-output ${DAY_DIR}/$date/new-pay
	
#付费金额、人数
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=1 \
	-D mosname.amount=amount \
	-D mosname.ucount=ucount \
	-conf ${HADOOP_CONF} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "New Pay Day step2 $date" \
	-addMos "amount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "ucount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-input ${DAY_DIR}/$date/new-pay/part-* \
	-mapperClass com.taomee.bigdata.task.segpay.MiddleGZSPMapper \
	-reducerClass com.taomee.bigdata.task.segpay.PayAmtCountUcountDistReducer \
	-output ${SUM_DIR}/$date/new-pay-day
	
${HADOOP_PATH}hadoop fs -rm -skipTrash ${SUM_DIR}/$date/new-pay-day/part-* &
$DB_UPLOAD -type 2 -date $date -task 125  -path ${SUM_DIR}/$date/new-pay-day/amount*
$DB_UPLOAD -type 2 -date $date -task 126  -path ${SUM_DIR}/$date/new-pay-day/ucount*

#task_id=127,128
n=6
first_day=`date -d "$date -$n day" +%Y%m%d`
input=""
for((i=0;i<=`expr ${n}`;i++));
do
	last_day=`date -d "$date -$i day" +%Y%m%d`
	${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$last_day/pay-day/part-*
	if [[ $? -eq 0 ]]; then
		input="$input -addInput ${DAY_DIR}/$last_day/pay-day/part-*,com.taomee.bigdata.task.segpay.MonACPayMapper "
	fi
done
#输出每人的付费额、次数
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D mapred.reduce.tasks=4 \
	-conf ${HADOOP_CONF} \
	-addInput ${ALL_DIR}/$first_day/account-all/firstLog*,com.taomee.bigdata.task.segpay.RangeUserMapper \
	$input \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.FloatWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "New Pay Day $n step1 $first_day" \
	-reducerClass com.taomee.bigdata.task.segpay.DiffIntReducer \
	-output ${DAY_DIR}/$first_day/new-pay-$n
	
#付费金额、人数
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=1 \
	-D mosname.amount=amount \
	-D mosname.ucount=ucount \
	-conf ${HADOOP_CONF} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "New Pay Day $n step2 $first_day" \
	-addMos "amount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "ucount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-input ${DAY_DIR}/$first_day/new-pay-$n/part-* \
	-mapperClass com.taomee.bigdata.task.segpay.MiddleGZSPMapper \
	-reducerClass com.taomee.bigdata.task.segpay.PayAmtCountUcountDistReducer \
	-output ${SUM_DIR}/$first_day/new-pay-$n-day
	
${HADOOP_PATH}hadoop fs -rm -skipTrash ${SUM_DIR}/$first_day/new-pay-$n-day/part-* &
$DB_UPLOAD -type 2 -date $first_day -task 127  -path ${SUM_DIR}/$first_day/new-pay-$n-day/amount*
$DB_UPLOAD -type 2 -date $first_day -task 128  -path ${SUM_DIR}/$first_day/new-pay-$n-day/ucount*

#task_id=130,131
n=29
first_day=`date -d "$date -$n day" +%Y%m%d`
input=""
for((i=0;i<=`expr ${n}`;i++));
do
	last_day=`date -d "$date -$i day" +%Y%m%d`
	${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$last_day/pay-day/part-*
	if [[ $? -eq 0 ]]; then
		input="$input -addInput ${DAY_DIR}/$last_day/pay-day/part-*,com.taomee.bigdata.task.segpay.MonACPayMapper "
	fi
done
#输出每人的付费额、次数
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D mapred.reduce.tasks=4 \
	-conf ${HADOOP_CONF} \
	-addInput ${ALL_DIR}/$first_day/account-all/firstLog*,com.taomee.bigdata.task.segpay.RangeUserMapper \
	$input \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.FloatWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "New Pay Day $n step1 $first_day" \
	-reducerClass com.taomee.bigdata.task.segpay.DiffIntReducer \
	-output ${DAY_DIR}/$first_day/new-pay-$n
	
#付费金额、人数
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=1 \
	-D mosname.amount=amount \
	-D mosname.ucount=ucount \
	-conf ${HADOOP_CONF} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "New Pay Day $n step2 $first_day" \
	-addMos "amount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "ucount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-input ${DAY_DIR}/$first_day/new-pay-$n/part-* \
	-mapperClass com.taomee.bigdata.task.segpay.MiddleGZSPMapper \
	-reducerClass com.taomee.bigdata.task.segpay.PayAmtCountUcountDistReducer \
	-output ${SUM_DIR}/$first_day/new-pay-$n-day
	
${HADOOP_PATH}hadoop fs -rm -skipTrash ${SUM_DIR}/$first_day/new-pay-$n-day/part-* &
$DB_UPLOAD -type 2 -date $first_day -task 130  -path ${SUM_DIR}/$first_day/new-pay-$n-day/amount*
$DB_UPLOAD -type 2 -date $first_day -task 131  -path ${SUM_DIR}/$first_day/new-pay-$n-day/ucount*
