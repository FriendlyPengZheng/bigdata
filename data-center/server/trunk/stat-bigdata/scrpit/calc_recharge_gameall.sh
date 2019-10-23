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


########################################################################
# mapper output: key=170,zone,server,platform  value=amt
# reduce output: key=170,zone,server,platform  value=sum(amt)
########################################################################
${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/mibiconsume-*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${DAY_DIR}/$date/basic/mibiconsume-*,com.taomee.bigdata.task.recharge.RechargeConsumGameMapper "
fi
if [[ $inputs == "" ]]; then
	echo "empty inputs"
	exit 1
fi

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
		-D mapred.reduce.tasks=5 \
        -conf ${HADOOP_CONF} \
        -jobName "Recharge Consume All Game $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.DoubleWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
		$inputs \
        -reducerClass com.taomee.bigdata.task.recharge.RechargeGameReducer \
        -output ${SUM_DIR}/$date/recharge-consume-gameall


$DB_UPLOAD -type 2 -date $date -task 393 -path ${SUM_DIR}/$date/recharge-consume-gameall/part*

########################################################################
# mapper output: key=170,zone,server,platform  value=amt
# reduce output: key=170,zone,server,platform  value=sum(amt)
########################################################################
${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/buyvip-*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${DAY_DIR}/$date/basic/buyvip-*,com.taomee.bigdata.task.recharge.RechargeVipGameMapper "
fi
if [[ $inputs == "" ]]; then
	echo "empty inputs"
	exit 1
fi

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
		-D mapred.reduce.tasks=5 \
        -conf ${HADOOP_CONF} \
        -jobName "Recharge Vip All Game $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.DoubleWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
		$inputs \
        -reducerClass com.taomee.bigdata.task.recharge.RechargeGameReducer \
        -output ${SUM_DIR}/$date/recharge-vip-gameall


$DB_UPLOAD -type 2 -date $date -task 392 -path ${SUM_DIR}/$date/recharge-vip-gameall/part*

