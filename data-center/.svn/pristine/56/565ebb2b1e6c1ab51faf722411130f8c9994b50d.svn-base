#各等级付费额
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

#输出每人的付费额、次数
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-conf ${HADOOP_CONF} \
	-addInput ${DAY_DIR}/$date/basic/lgac-*,com.taomee.bigdata.task.segpay.LgLevelMapper \
	-addInput ${DAY_DIR}/$date/basic/acpay-*,com.taomee.bigdata.task.segpay.PayAmtMapper \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "Pay Level step1 $date" \
	-reducerClass com.taomee.bigdata.task.segpay.PayLevelAmtMiddleReducer \
	-output ${DAY_DIR}/$date/pay-level
	
#付费金额、人数
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-conf ${HADOOP_CONF} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "Pay Level step2 $date" \
	-input ${DAY_DIR}/$date/pay-level/part* \
	-mapperClass com.taomee.bigdata.task.segpay.MiddleGZSPMapper \
	-reducerClass com.taomee.bigdata.task.segpay.PayLevelAmtReducer \
	-output ${SUM_DIR}/$date/pay-level
