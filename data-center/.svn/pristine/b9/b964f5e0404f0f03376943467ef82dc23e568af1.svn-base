#月付费
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

#task 71-73,89-90输出每人的付费额、次数
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D param.key=_amt_ \
	-D ext.key=_sstid_ \
	-D calc.type=union \
	-D mapred.reduce.tasks=4 \
	-conf ${HADOOP_CONF} \
	-gameInfo ${GAMEINFO} \
	-input ${DAY_DIR}/${year_month}*/basic/acpay* \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.FloatWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "Month Pay amount,count,acount,dist step1 $date" \
	-mapperClass com.taomee.bigdata.task.segpay.OriExtKeyMapper \
	-reducerClass com.taomee.bigdata.task.segpay.DiffIntReducer \
	-output ${MONTH_DIR}/$year_month/pay-month
	
	#-D distr.pay.amount=6,11,21,31,51,71,101,201,501,1001 \
#付费金额、人数、人次、额度分布、次数分布
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D key.num=5 \
	-D mosname.amount=amount \
	-D mosname.ucount=ucount \
	-D mosname.count=count \
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
	-jobName "Month Pay amount,count,acount,dist step2 $date" \
	-addMos "amount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "ucount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "count,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "amountdistr,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "countdistr,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-input ${MONTH_DIR}/$year_month/pay-month/part* \
	-mapperClass com.taomee.bigdata.task.segpay.MiddleGZSPMapper \
	-reducerClass com.taomee.bigdata.task.segpay.PayAmtCountUcountDistReducer \
	-output ${SUM_DIR}/$year_month/pay-month

#$DB_UPLOAD -type 2 -date ${year_month}01 -task 71  -path ${SUM_DIR}/${year_month}/pay-month/amount*
#$DB_UPLOAD -type 2 -date ${year_month}01 -task 72  -path ${SUM_DIR}/${year_month}/pay-month/ucount*
#$DB_UPLOAD -type 2 -date ${year_month}01 -task 73  -path ${SUM_DIR}/${year_month}/pay-month/count*
#$DB_UPLOAD -type 2 -date ${year_month}01 -task 89  -path ${SUM_DIR}/${year_month}/pay-month/countdistr*
#$DB_UPLOAD -type 2 -date ${year_month}01 -task 90  -path ${SUM_DIR}/${year_month}/pay-month/amountdistr*
