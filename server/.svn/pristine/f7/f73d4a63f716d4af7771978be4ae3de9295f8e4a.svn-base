#周付费
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

#付费金额、人数、人次、额度分布、次数分布
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D distr=500,1000,1100,1500,2000,3000,4000,5000,6000,7000,8000,9000,10000,10100,12000,12100,15000,20000,30000,50000,100000 \
	-D mapred.reduce.tasks=2 \
	-conf ${HADOOP_CONF} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "Pay Amt Distr $date" \
	-input ${DAY_DIR}/$date/basic/acpay-* \
	-mapperClass com.taomee.bigdata.task.pay.SourceAmtMapper \
	-reducerClass com.taomee.bigdata.task.pay.PayAmtReducer \
	-addMos amt,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.DoubleWritable \
	-addMos count,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable \
	-addMos ucount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable \
	-output ${SUM_DIR}/$date/pay-amt

${HADOOP_PATH}hadoop fs -rm -skipTrash ${SUM_DIR}/$date/pay-amt/part-* > /dev/null 2>&1 &

$DB_UPLOAD -type 2 -date $date -task 340 -path ${SUM_DIR}/$date/pay-amt/amt-*
$DB_UPLOAD -type 2 -date $date -task 341 -path ${SUM_DIR}/$date/pay-amt/count-*
$DB_UPLOAD -type 2 -date $date -task 342 -path ${SUM_DIR}/$date/pay-amt/ucount-*
