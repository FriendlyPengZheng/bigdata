#月历史转化付费（当月非新增，且当月新付费）
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
last_month=`date -d "${year_month}01 -1 day" +%Y%m%d`

#没有上月累计付费、本月新增数据
${HADOOP_PATH}hadoop fs -test -e ${MONTH_DIR}/$year_month/account-new/
if [[ $? -ne 0 ]]; then
	${HADOOP_PATH}hadoop fs -mkdir ${MONTH_DIR}/$year_month/account-new/
	${HADOOP_PATH}hadoop fs -touchz  ${MONTH_DIR}/$year_month/account-new/part-00000
fi

${HADOOP_PATH}hadoop fs -test -e ${ALL_DIR}/$last_month/pay-all/
if [[ $? -ne 0 ]]; then
	${HADOOP_PATH}hadoop fs -mkdir ${ALL_DIR}/$last_month/pay-all/
	${HADOOP_PATH}hadoop fs -touchz  ${ALL_DIR}/$last_month/pay-all/allpay-00000
fi

#输出每人的付费额、次数
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D calc.type=dif \
	-conf ${HADOOP_CONF} \
	-addInput ${MONTH_DIR}/$year_month/account-new/part-*,com.taomee.bigdata.task.segpay.RangeUserMapper \
	-addInput ${ALL_DIR}/$last_month/pay-all/allpay-*,com.taomee.bigdata.task.segpay.RangeUserMapper \
	-addInput ${MONTH_DIR}/$year_month/pay-month/part-*,com.taomee.bigdata.task.segpay.MonACPayMapper \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.FloatWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "Month Pay His User first pay step1 $date" \
	-reducerClass com.taomee.bigdata.task.segpay.DiffIntReducer \
	-output ${MONTH_DIR}/$year_month/pay-month-tran

#付费金额、人数
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D mosname.amount=amount \
	-D mosname.ucount=ucount \
	-D mosname.arppu=arppu \
	-conf ${HADOOP_CONF} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "Month Pay His User first pay step2 $date" \
	-addMos "amount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "ucount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "arppu,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-input ${MONTH_DIR}/$year_month/pay-month-tran/part* \
	-mapperClass com.taomee.bigdata.task.segpay.MiddleGZSPMapper \
	-reducerClass com.taomee.bigdata.task.segpay.PayAmtCountUcountDistReducer \
	-output ${SUM_DIR}/$year_month/pay-month-tran

$DB_UPLOAD -type 2 -date ${year_month}01 -task 80 -path ${SUM_DIR}/$year_month/pay-month-tran/amount*
$DB_UPLOAD -type 2 -date ${year_month}01 -task 81 -path ${SUM_DIR}/$year_month/pay-month-tran/ucount*
$DB_UPLOAD -type 2 -date ${year_month}01 -task 141 -path ${SUM_DIR}/$year_month/pay-month-tran/arppu*
