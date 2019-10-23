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

year_month=`date -d "$date" +%Y%m`
yesterday=`date -d "$date -1 day" +%Y%m%d`

${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/acpay-m-*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${DAY_DIR}/$date/basic/acpay-m-*,com.taomee.bigdata.task.allpay.SourcePayMapper "
fi
${HADOOP_PATH}hadoop fs -ls ${ALL_DIR}/$yesterday/pay-all/allpay-*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${ALL_DIR}/$yesterday/pay-all/allpay-*,com.taomee.bigdata.task.allpay.PayMapper "
fi
if [[ $inputs == "" ]]; then
	echo "empty inputs"
	exit 1
fi

#task_id=65,66,67
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D mapred.reduce.tasks=4 \
	-D mapred.output.compress=true \
	-D mapred.output.compression.codec=org.apache.hadoop.io.compress.BZip2Codec \
        -conf ${HADOOP_CONF} \
        -jobName "Pay All $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	$inputs \
        -reducerClass com.taomee.bigdata.task.allpay.PayReducer \
	-addMos "allpay,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "acpay,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "payinterval,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
        -output ${ALL_DIR}/$date/pay-all

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=1 \
        -conf ${HADOOP_CONF} \
        -jobName "Pay New Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.DoubleWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass com.taomee.bigdata.task.allpay.PaySumMapper \
        -combinerClass com.taomee.bigdata.task.allpay.PaySumReducer \
        -reducerClass com.taomee.bigdata.task.allpay.PaySumReducer \
        -input ${ALL_DIR}/$date/pay-all/part-* \
        -output ${SUM_DIR}/$date/pay-new-sum

$DB_UPLOAD -type 2 -date $date -task 66 -path ${SUM_DIR}/$date/pay-new-sum/part*

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=1 \
        -conf ${HADOOP_CONF} \
        -jobName "Pay New Ucount $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass com.taomee.bigdata.task.allpay.SetSumMapper \
        -combinerClass com.taomee.bigdata.task.common.SetSumReducer \
        -reducerClass com.taomee.bigdata.task.common.SetSumReducer \
        -input ${ALL_DIR}/$date/pay-all/part-* \
        -output ${SUM_DIR}/$date/pay-new-ucount

$DB_UPLOAD -type 2 -date $date -task 65 -path ${SUM_DIR}/$date/pay-new-ucount/part*

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=1 \
        -conf ${HADOOP_CONF} \
        -jobName "Pay All Ucount $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.allpay.SetSumMapper \
        -combinerClass  com.taomee.bigdata.task.common.SetSumReducer \
        -reducerClass com.taomee.bigdata.task.common.SetSumReducer \
        -input ${ALL_DIR}/$date/pay-all/allpay-* \
        -output ${SUM_DIR}/$date/pay-all-ucount

$DB_UPLOAD -type 2 -date $date -task 67 -path ${SUM_DIR}/$date/pay-all-ucount/part*
