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

#task_id=62,63,64
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
        -conf ${HADOOP_CONF} \
        -jobName "Pay Rate $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${DAY_DIR}/$date/basic/lgac-*,com.taomee.bigdata.task.pay.SourceActiveMapper \
	-addInput ${DAY_DIR}/$date/basic/acpay-*,com.taomee.bigdata.task.pay.SourcePayMapper \
        -combinerClass  com.taomee.bigdata.task.pay.PayCombiner \
        -reducerClass com.taomee.bigdata.task.pay.PayReducer \
	-output ${DAY_DIR}/$date/pay

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=1 \
        -conf ${HADOOP_CONF} \
        -jobName "Pay Rate Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.pay.PaySumMapper \
        -combinerClass  com.taomee.bigdata.task.pay.PaySumCombiner \
        -reducerClass com.taomee.bigdata.task.pay.PaySumReducer \
	-input ${DAY_DIR}/$date/pay/part* \
	-addMos "arpu,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "arppu,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
        -output ${SUM_DIR}/$date/pay

$DB_UPLOAD -type 2 -date $date -task 62 -path ${SUM_DIR}/$date/pay/part*
$DB_UPLOAD -type 2 -date $date -task 63 -path ${SUM_DIR}/$date/pay/arpu*
$DB_UPLOAD -type 2 -date $date -task 64 -path ${SUM_DIR}/$date/pay/arppu*
