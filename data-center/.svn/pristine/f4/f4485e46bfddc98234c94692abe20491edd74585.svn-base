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

# mapper: today: key=game,zone,server,platform,mimi value=1,amt,time,time,1,amt
#         yesterday: key=game,zone,server,platform,mimi value=0,famt,ftime,ltime,cnt,tamt
# output: rechargeall: key=game,zone,server,platform,mimi,famt,ftime,ltime,cnt,tamt value=null
#         rechargefirst: key=game,zone,server,platform,mimi,famt value=null
#         rechargeinterval:key=game,zone,server,platform,mimi,dayinterval value=null
${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/userbuy-*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${DAY_DIR}/$date/basic/userbuy-*,com.taomee.bigdata.task.recharge.RechargeSourcePayMapper "
fi
${HADOOP_PATH}hadoop fs -ls ${ALL_DIR}/$yesterday/recharge-pay-all/rechargeall-*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${ALL_DIR}/$yesterday/recharge-pay-all/rechargeall-*,com.taomee.bigdata.task.recharge.RechargePayMapper "
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
        -jobName "Recharge Pay All $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
		$inputs \
        -reducerClass com.taomee.bigdata.task.recharge.RechargePayReducer \
		-addMos "rechargeall,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
		-addMos "rechargefirst,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
		-addMos "rechargeinterval,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
        -output ${ALL_DIR}/$date/recharge-pay-all

#Recharge First Sum
#mapper output: key=game,zone,server,platform value=famt
#reduce output: key=game,zone,server,platform value=sum(famt)
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
		-D mapred.reduce.tasks=1 \
        -conf ${HADOOP_CONF} \
        -jobName "Recharge First Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.DoubleWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass com.taomee.bigdata.task.recharge.RechargePaySumMapper \
        -combinerClass com.taomee.bigdata.task.recharge.RechargePaySumReducer \
        -reducerClass com.taomee.bigdata.task.recharge.RechargePaySumReducer \
		-input ${ALL_DIR}/$date/recharge-pay-all/rechargefirst-* \
		-output ${SUM_DIR}/$date/recharge-first-sum

$DB_UPLOAD -type 2 -date $date -task 369 -path ${SUM_DIR}/$date/recharge-first-sum/part*

#Recharge First Ucount
#mapper output: key=game,zone,server,platform value=1
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
		-D mapred.reduce.tasks=1 \
        -conf ${HADOOP_CONF} \
        -jobName "Recharge First Ucount $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass com.taomee.bigdata.task.recharge.RechargeSetSumMapper \
        -combinerClass com.taomee.bigdata.task.common.SetSumReducer \
        -reducerClass com.taomee.bigdata.task.common.SetSumReducer \
		-input ${ALL_DIR}/$date/recharge-pay-all/rechargefirst-* \
		-output ${SUM_DIR}/$date/recharge-first-ucount

$DB_UPLOAD -type 2 -date $date -task 370 -path ${SUM_DIR}/$date/recharge-first-ucount/part*

#Recharge interval distribution
#mapper output: key=game,zone,server,platform,distr[intervaldays]  value=1
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
        -D mapred.reduce.tasks=2 \
        -D distr=0,1,2,3,4,5,7,14,30,60,90,120,150,180,300,365 \
        -conf ${HADOOP_CONF} \
        -jobName "Recharge Interval Distribution $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.recharge.RechargePayIntervalDistrMapper \
        -reducerClass com.taomee.bigdata.task.recharge.RechargePayIntervalDistrReducer \
        -input ${ALL_DIR}/$date/recharge-pay-all/rechargeinterval-* \
        -output ${SUM_DIR}/$date/recharge-interval-distr

$DB_UPLOAD -type 2 -date $date -task 374 -path ${SUM_DIR}/$date/recharge-interval-distr/part*

#Recharge All Sum
#mapper output: key=170,zone,server,platform value=tamt
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
		-D mapred.reduce.tasks=1 \
        -conf ${HADOOP_CONF} \
        -jobName "Recharge All Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.DoubleWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass com.taomee.bigdata.task.recharge.RechargePayAllSumMapper \
        -combinerClass com.taomee.bigdata.task.recharge.RechargePayAllSumReducer \
        -reducerClass com.taomee.bigdata.task.recharge.RechargePayAllSumReducer \
		-input ${ALL_DIR}/$date/recharge-pay-all/rechargeall-* \
		-output ${SUM_DIR}/$date/recharge-all-sum

$DB_UPLOAD -type 2 -date $date -task 394 -path ${SUM_DIR}/$date/recharge-all-sum/part*
