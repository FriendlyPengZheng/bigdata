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

#充值面额充值额分布 充值次数分布 充值人数分布
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D distr=500,1000,1100,1500,2000,3000,4000,5000,6000,7000,8000,9000,10000,10100,12000,12100,15000,20000,30000,50000,100000 \
	-D mapred.reduce.tasks=2 \
	-conf ${HADOOP_CONF} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "Recharge Pay Amt Distr $date" \
	-input ${DAY_DIR}/$date/basic/userbuy-* \
	-mapperClass com.taomee.bigdata.task.recharge.RechargeSourceAmtMapper \
	-reducerClass com.taomee.bigdata.task.recharge.RechargePayAmtReducer \
	-addMos amt,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.DoubleWritable \
	-addMos count,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable \
	-addMos ucount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable \
	-output ${SUM_DIR}/$date/recharge-pay-amt

${HADOOP_PATH}hadoop fs -rm -skipTrash ${SUM_DIR}/$date/recharge-pay-amt/part-* > /dev/null 2>&1 &

$DB_UPLOAD -type 2 -date $date -task 371 -path ${SUM_DIR}/$date/recharge-pay-amt/amt-*
$DB_UPLOAD -type 2 -date $date -task 372 -path ${SUM_DIR}/$date/recharge-pay-amt/count-*
$DB_UPLOAD -type 2 -date $date -task 373 -path ${SUM_DIR}/$date/recharge-pay-amt/ucount-*
