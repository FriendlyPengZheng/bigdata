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

#task_id=134-137
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
        -conf ${HADOOP_CONF} \
        -jobName "Pay Level $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${DAY_DIR}/$date/basic/acpay-*,com.taomee.bigdata.task.paylevel.AcpayMapper \
	-addInput ${DAY_DIR}/$date/level/part-*,com.taomee.bigdata.task.paylevel.LevelMapper \
        -reducerClass com.taomee.bigdata.task.paylevel.PayLevelReducer \
        -output ${DAY_DIR}/$date/paylevel

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
        -conf ${HADOOP_CONF} \
        -jobName "Pay Level Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${DAY_DIR}/$date/paylevel/p*,com.taomee.bigdata.task.paylevel.PayLevelSumMapper \
        -reducerClass com.taomee.bigdata.task.paylevel.PayLevelSumReducer \
	-addMos "buyitemamt,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "buyitemcount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "vipmonthamt,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "vipmonthcount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "costfreeamt,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "costfreecount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "buycoinsamt,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "buycoinscount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
        -output ${SUM_DIR}/$date/paylevel

${HADOOP_PATH}hadoop fs -rm -skipTrash ${SUM_DIR}/$date/paylevel/part-*
$DB_UPLOAD -type 2 -date $date -task 134 -path ${SUM_DIR}/$date/paylevel/buyitemamt-*
$DB_UPLOAD -type 2 -date $date -task 135 -path ${SUM_DIR}/$date/paylevel/buyitemcount-*
$DB_UPLOAD -type 2 -date $date -task 136 -path ${SUM_DIR}/$date/paylevel/vipmonthamt-*
$DB_UPLOAD -type 2 -date $date -task 137 -path ${SUM_DIR}/$date/paylevel/vipmonthcount-*
