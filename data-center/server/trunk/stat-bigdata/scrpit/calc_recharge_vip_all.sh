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
# reduce output: vipall: key=game,zone,server,platform,mimi,famt,ftime,ltime,cnt,tamt
# reduce output: vipfirst: key=game,zone,server,platform,mimi,famt,tamt_today
# reduce output: vipinterval: key=game,zone,server,platform,mimi,days
${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/mibiconsume-*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${DAY_DIR}/$date/basic/mibiconsume-*,com.taomee.bigdata.task.recharge.RechargeSourceVipMapper "
fi
${HADOOP_PATH}hadoop fs -ls ${ALL_DIR}/$yesterday/recharge-vip-all/vipall-*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${ALL_DIR}/$yesterday/recharge-vip-all/vipall-*,com.taomee.bigdata.task.recharge.RechargePayMapper "
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
        -jobName "Recharge Vip All $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
		$inputs \
        -reducerClass com.taomee.bigdata.task.recharge.RechargeVipReducer \
		-addMos "vipall,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
		-addMos "vipfirst,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
		-addMos "vipinterval,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
        -output ${ALL_DIR}/$date/recharge-vip-all

#Vip First Sum
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
		-D mapred.reduce.tasks=1 \
        -conf ${HADOOP_CONF} \
        -jobName "Vip First Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.DoubleWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass com.taomee.bigdata.task.recharge.RechargePaySumMapper \
        -combinerClass com.taomee.bigdata.task.recharge.RechargePaySumReducer \
        -reducerClass com.taomee.bigdata.task.recharge.RechargePaySumReducer \
		-input ${ALL_DIR}/$date/recharge-vip-all/vipfirst-* \
		-output ${SUM_DIR}/$date/recharge-vip-first-sum

$DB_UPLOAD -type 2 -date $date -task 383 -path ${SUM_DIR}/$date/recharge-vip-first-sum/part*

#Vip First Ucount
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
		-D mapred.reduce.tasks=1 \
        -conf ${HADOOP_CONF} \
        -jobName "Vip First Ucount $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass com.taomee.bigdata.task.recharge.RechargeSetSumMapper \
        -combinerClass com.taomee.bigdata.task.common.SetSumReducer \
        -reducerClass com.taomee.bigdata.task.common.SetSumReducer \
		-input ${ALL_DIR}/$date/recharge-vip-all/vipfirst-* \
		-output ${SUM_DIR}/$date/recharge-vip-first-ucount

$DB_UPLOAD -type 2 -date $date -task 382 -path ${SUM_DIR}/$date/recharge-vip-first-ucount/part*

#Vip interval distribution
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
        -D mapred.reduce.tasks=2 \
        -D distr=0,1,2,3,4,5,7,14,30,60,90,120,150,180,300,365 \
        -conf ${HADOOP_CONF} \
        -jobName "vip Interval Distribution $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.recharge.RechargePayIntervalDistrMapper \
        -reducerClass com.taomee.bigdata.task.recharge.RechargePayIntervalDistrReducer \
        -input ${ALL_DIR}/$date/recharge-vip-all/vipinterval-* \
        -output ${SUM_DIR}/$date/recharge-vip-interval-distr

$DB_UPLOAD -type 2 -date $date -task 391 -path ${SUM_DIR}/$date/recharge-vip-interval-distr/part*

