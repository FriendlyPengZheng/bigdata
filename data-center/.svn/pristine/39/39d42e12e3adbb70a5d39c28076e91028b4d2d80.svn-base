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

########################################################################
# input: key=game,zone,server,platform,uid  value=0/1,famt,ftime,ltime,cnt,tamt
# output: allrecharge: key=game,zone,server,platform,mimi,famt,ftime,ltime,cnt,tamt
#         firstrecharge: key=game,zone,server,platform,mimi,famt,tamt
########################################################################
${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/mibiconsume-*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${DAY_DIR}/$date/basic/mibiconsume-*,com.taomee.bigdata.task.recharge.RechargeConsumeArppuMapper "
fi

if [[ $inputs == "" ]]; then
	echo "empty inputs"
	exit 1
fi

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
		-D mapred.reduce.tasks=8 \
        -conf ${HADOOP_CONF} \
        -jobName "Recharge Consume Arppu $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
		$inputs \
        -reducerClass com.taomee.bigdata.task.recharge.RechargeConsumeArppuReducer \
        -output ${ALL_DIR}/$date/recharge-consume-arppu

#Recharge Consume Arppu Sum
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
		-D mapred.reduce.tasks=1 \
        -conf ${HADOOP_CONF} \
        -jobName "Recharge Consume Arppu Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.DoubleWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass com.taomee.bigdata.task.recharge.RechargeConsumeArppuSumMapper \
        -combinerClass com.taomee.bigdata.task.recharge.RechargeConsumeArppuSumReducer \
        -reducerClass com.taomee.bigdata.task.recharge.RechargeConsumeArppuSumReducer \
		-input ${ALL_DIR}/$date/recharge-consume-arppu/part-* \
		-output ${SUM_DIR}/$date/recharge-consume-arppu-sum

$DB_UPLOAD -type 2 -date $date -task 396 -path ${SUM_DIR}/$date/recharge-consume-arppu-sum/part*

