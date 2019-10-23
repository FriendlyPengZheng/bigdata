#月活跃用户付费（上月付费，当月再付费）
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

#month date
year_month=`date -d "$date" +%Y%m`

last_month_first=`date -d "${year_month}01 -1 day" +%Y%m%d`
last_month=`date -d "$last_month_first" +%Y%m`

#输出每人的付费额、次数
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-conf ${HADOOP_CONF} \
	-gameInfo ${GAMEINFO} \
	-addInput ${MONTH_DIR}/$last_month/pay-month/part*,com.taomee.bigdata.task.segpay.MonACPayAssistMapper \
	-addInput ${MONTH_DIR}/$year_month/pay-month/part*,com.taomee.bigdata.task.segpay.MonACPayMapper \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.FloatWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "Month Active Pay step1 $date" \
	-reducerClass com.taomee.bigdata.task.segpay.DiffIntReducer \
	-output ${MONTH_DIR}/$year_month/pay-month-cts
	
#付费金额、人数
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D mosname.amount=amount \
	-D mosname.ucount=ucount \
	-D mosname.arppu=arppu \
	-conf ${HADOOP_CONF} \
	-gameInfo ${GAMEINFO} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "Month Active Pay step2 $date" \
	-addMos "amount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "ucount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "arppu,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-input ${MONTH_DIR}/$year_month/pay-month-cts/part* \
	-mapperClass com.taomee.bigdata.task.segpay.MiddleGZSPMapper \
	-reducerClass com.taomee.bigdata.task.segpay.PayAmtCountUcountDistReducer \
	-output ${SUM_DIR}/$year_month/pay-month-cts
	
#$DB_UPLOAD -type 2 -date ${year_month}01 -task 82 -path ${SUM_DIR}/$year_month/pay-month-cts/amount*
#$DB_UPLOAD -type 2 -date ${year_month}01 -task 83 -path ${SUM_DIR}/$year_month/pay-month-cts/ucount*
#$DB_UPLOAD -type 2 -date ${year_month}01 -task 142 -path ${SUM_DIR}/$year_month/pay-month-cts/arppu*
