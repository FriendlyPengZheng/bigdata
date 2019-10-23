#周付费
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

#task 123,124 输出每人的付费额、次数
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D param.key=_amt_ \
	-D ext.key=_sstid_ \
	-D calc.type=union \
	-D mapred.reduce.tasks=4 \
	-conf ${HADOOP_CONF} \
	-gameInfo ${GAMEINFO} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.FloatWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "Day Pay dist step1 $date" \
	-reducerClass com.taomee.bigdata.task.segpay.DiffIntReducer \
	-addInput ${DAY_DIR}/$date/basic/acpay*,com.taomee.bigdata.task.segpay.OriExtKeyMapper \
	-output ${DAY_DIR}/$date/pay-day

	#-D distr.pay.amount=6,11,21,31,51,71,101,201,501,1001 \
#付费金额、人数、人次、额度分布、次数分布
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D key.num=5 \
	-D mosname.amount.dist=amountdistr \
	-D mosname.count.dist=countdistr \
	-D distr.pay.count=2,3,4,5,6,11,21,31,41,51 \
	-D distr.pay.amount=500,1000,1100,1500,2000,3000,4000,5000,6000,7000,8000,9000,10000,10100,12000,12100,15000,20000,30000,50000,100000 \
	-D mapred.reduce.tasks=2 \
	-conf ${HADOOP_CONF} \
	-gameInfo ${GAMEINFO} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "Day Pay dist step2 $date" \
	-addMos "amountdistr,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "countdistr,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-input ${DAY_DIR}/$date/pay-day/part* \
	-mapperClass com.taomee.bigdata.task.segpay.MiddleGZSPMapper \
	-reducerClass com.taomee.bigdata.task.segpay.PayAmtCountUcountDistReducer \
	-output ${SUM_DIR}/$date/pay-day

#$DB_UPLOAD -type 2 -date ${date} -task 123 -path ${SUM_DIR}/$date/pay-day/amountdistr*
#$DB_UPLOAD -type 2 -date ${date} -task 124 -path ${SUM_DIR}/$date/pay-day/countdistr*
