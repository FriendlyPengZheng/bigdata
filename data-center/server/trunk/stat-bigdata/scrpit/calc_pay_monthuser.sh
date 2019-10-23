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

#新增、留存、回流用户
#newer/keeper/backer
user_type=$2
if [[ $user_type != "new" && $user_type != "keep" && $user_type != "back" ]]; then
    echo invalid param: $user_type
    exit
fi

if [[ $user_type == "new" ]]; then
    path=account-new
elif [[ $user_type == "keep" ]]; then
    path=active-keep-1-month
elif [[ $user_type == "back" ]]; then
    path=account-back
fi

#task_id=74-79
#输出每人的付费额、次数
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-conf ${HADOOP_CONF} \
	-addInput ${MONTH_DIR}/$year_month/$path/part-*,com.taomee.bigdata.task.segpay.RangeUserMapper \
	-addInput ${MONTH_DIR}/$year_month/pay-month/part-*,com.taomee.bigdata.task.segpay.MonACPayMapper \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.FloatWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "Month $user_type Pay step1 $year_month" \
	-reducerClass com.taomee.bigdata.task.segpay.DiffIntReducer \
	-output ${MONTH_DIR}/$year_month/pay-month-$user_type
	
#付费金额、人数
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D mosname.amount=amount \
	-D mosname.ucount=ucount \
	-D mosname.arppu=arppu \
	-conf ${HADOOP_CONF} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "Month $user_type Pay step2 $year_month" \
	-addMos "amount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "ucount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "arppu,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-input ${MONTH_DIR}/$year_month/pay-month-$user_type/part* \
	-mapperClass com.taomee.bigdata.task.segpay.MiddleGZSPMapper \
	-reducerClass com.taomee.bigdata.task.segpay.PayAmtCountUcountDistReducer \
	-output ${SUM_DIR}/$year_month/pay-month-$user_type

#输出每人的付费额、次数
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-conf ${HADOOP_CONF} \
	-addInput ${MONTH_DIR}/$year_month/$path/part-*,com.taomee.bigdata.task.common.PercentAMapper \
	-addInput ${MONTH_DIR}/$year_month/pay-month-$user_type/part-*,com.taomee.bigdata.task.common.PercentBMapper \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.IntWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "Month $user_type Pay step3 $year_month" \
	-reducerClass com.taomee.bigdata.task.common.PercentReducer \
	-output ${SUM_DIR}/$year_month/pay-month-$user_type-percent
	
if [[ $user_type == "new" ]]; then
	atask=74
	ctask=75
	ptask=138
	ttask=154
elif [[ $user_type == "keep" ]]; then
	atask=76
	ctask=77
	ptask=139
	ttask=156
elif [[ $user_type == "back" ]]; then
	atask=78
	ctask=79
	ptask=140
	ttask=156
fi

${HADOOP_PATH}hadoop fs -rm -skipTrash ${SUM_DIR}/$year_month/pay-month-$user_type/part-* &
$DB_UPLOAD -type 2 -date ${year_month}01 -task $atask  -path ${SUM_DIR}/$year_month/pay-month-$user_type/amount*
$DB_UPLOAD -type 2 -date ${year_month}01 -task $ctask  -path ${SUM_DIR}/$year_month/pay-month-$user_type/ucount*
$DB_UPLOAD -type 2 -date ${year_month}01 -task $ptask  -path ${SUM_DIR}/$year_month/pay-month-$user_type/arppu*
$DB_UPLOAD -type 2 -date ${year_month}01 -task $ttask  -path ${SUM_DIR}/$year_month/pay-month-$user_type-percent/part*
