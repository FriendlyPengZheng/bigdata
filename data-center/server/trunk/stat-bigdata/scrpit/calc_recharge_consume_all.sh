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

yesterday=`date -d "$date -1 day" +%Y%m%d`

# mapper output: key=game,zone,server,platform,uid  value=1,amt,time,time,1,amt
# mapper output: key=game,zone,server,platform,uid  value=0,amt,ftime,ltime,cnt,tamt
# reduce output: consumeall: key=game,zone,server,platform,mimi,famt,ftime,ltime,cnt,tamt
# reduce output: consumefirst: key=game,zone,server,platform,mimi,famt
${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/mibiconsume-*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${DAY_DIR}/$date/basic/mibiconsume-*,com.taomee.bigdata.task.recharge.RechargeSourceConsumeMapper "
fi
${HADOOP_PATH}hadoop fs -ls ${ALL_DIR}/$yesterday/recharge-consume-all/consumeall-*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${ALL_DIR}/$yesterday/recharge-consume-all/consumeall-*,com.taomee.bigdata.task.recharge.RechargePayMapper "
fi
if [[ $inputs == "" ]]; then
	echo "empty inputs"
	exit 1
fi

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
		-D mapred.reduce.tasks=8 \
		-D mapred.output.compress=true \
		-D mapred.output.compression.codec=org.apache.hadoop.io.compress.BZip2Codec \
        -conf ${HADOOP_CONF} \
        -jobName "Recharge Consume All $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
		$inputs \
        -reducerClass com.taomee.bigdata.task.recharge.RechargeConsumeReducer \
		-addMos "consumeall,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
		-addMos "consumefirst,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
        -output ${ALL_DIR}/$date/recharge-consume-all

#Consume First Sum
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
		-D mapred.reduce.tasks=1 \
        -conf ${HADOOP_CONF} \
        -jobName "Consume First Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.DoubleWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass com.taomee.bigdata.task.recharge.RechargePaySumMapper \
        -combinerClass com.taomee.bigdata.task.recharge.RechargePaySumReducer \
        -reducerClass com.taomee.bigdata.task.recharge.RechargePaySumReducer \
		-input ${ALL_DIR}/$date/recharge-consume-all/consumefirst-* \
		-output ${SUM_DIR}/$date/recharge-consume-first-sum

$DB_UPLOAD -type 2 -date $date -task 382 -path ${SUM_DIR}/$date/recharge-consume-first-sum/part*

#Consume First Ucount
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
		-D mapred.reduce.tasks=1 \
        -conf ${HADOOP_CONF} \
        -jobName "Consume First Ucount $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass com.taomee.bigdata.task.recharge.RechargeSetSumMapper \
        -combinerClass com.taomee.bigdata.task.common.SetSumReducer \
        -reducerClass com.taomee.bigdata.task.common.SetSumReducer \
		-input ${ALL_DIR}/$date/recharge-consume-all/consumefirst-* \
		-output ${SUM_DIR}/$date/recharge-consume-first-ucount

$DB_UPLOAD -type 2 -date $date -task 381 -path ${SUM_DIR}/$date/recharge-consume-first-ucount/part*

#Consume All Sum
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
        -D mapred.reduce.tasks=1 \
        -conf ${HADOOP_CONF} \
        -jobName "Consume All Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.DoubleWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass com.taomee.bigdata.task.recharge.RechargePayAllSumMapper \
        -combinerClass com.taomee.bigdata.task.recharge.RechargePayAllSumReducer \
        -reducerClass com.taomee.bigdata.task.recharge.RechargePayAllSumReducer \
        -input ${ALL_DIR}/$date/recharge-consume-all/consumeall-* \
        -output ${SUM_DIR}/$date/recharge-consume-all-sum

$DB_UPLOAD -type 2 -date $date -task 395 -path ${SUM_DIR}/$date/recharge-consume-all-sum/part*
