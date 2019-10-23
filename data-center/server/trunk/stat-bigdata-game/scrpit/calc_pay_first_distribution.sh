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

#首付周期，金额分布
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D mapred.reduce.tasks=4 \
	-D today=`date "-d $date" +%s` \
	-conf ${HADOOP_CONF} \
	-gameInfo ${GAMEINFO} \
	-jobName "Pay first interval $date" \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${ALL_DIR}/$date/pay-all/allpay*,com.taomee.bigdata.task.first_pay_distribution.interval_pay_Mapper \
	-addInput ${ALL_DIR}/$date/account-all/part*,com.taomee.bigdata.task.first_pay_distribution.interval_log_Mapper \
	-reducerClass com.taomee.bigdata.task.first_pay_distribution.interval_pay_Reducer \
	-output ${DAY_DIR}/$date/first-pay-interval

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
	-D mosAcpaytime=acpaytime \
	-D mosAcpaycost=acpaycost \
	-D mosVipmonthtime=vipmonthtime \
	-D mosVipmonthcost=vipmonthcost \
	-D mosBuyitemtime=buyitemtime \
	-D mosBuyitemcost=buyitemcost \
	-D timeDistr=1,2,3,4,5,6,7,8,15,22,29,36,43,50,57,85,181,366 \
	-D costDistr=500,1000,1100,1500,2000,3000,4000,5000,6000,7000,8000,9000,10000,10100,12000,12100,15000,20000,30000,50000,100000 \
	-conf ${HADOOP_CONF} \
	-jobName "first pay distribution $date" \
	-gameInfo ${GAMEINFO} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-mapperClass  com.taomee.bigdata.task.first_pay_distribution.pay_distribution_Mapper \
	-reducerClass com.taomee.bigdata.task.first_pay_distribution.pay_distribution_Reducer \
	-input ${DAY_DIR}/$date/first-pay-interval/part* \
	-addMos "acpaytime,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "acpaycost,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "vipmonthtime,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "vipmonthcost,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "buyitemtime,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "buyitemcost,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "costfreeamt,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "costfreecount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "buycoinsamt,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "buycoinscount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-output ${SUM_DIR}/$date/first-pay-distribution

#$DB_UPLOAD -type 2 -date $date -task 148 -path ${SUM_DIR}/$date/first-pay-distribution/buyitemtime*
#$DB_UPLOAD -type 2 -date $date -task 149 -path ${SUM_DIR}/$date/first-pay-distribution/vipmonthtime*
#$DB_UPLOAD -type 2 -date $date -task 152 -path ${SUM_DIR}/$date/first-pay-distribution/buyitemcost*
#$DB_UPLOAD -type 2 -date $date -task 153 -path ${SUM_DIR}/$date/first-pay-distribution/vipmonthcost*

#付费间隔分布
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
	-D itemdistr=0,1,2,3,4,5,7,14,30,60,90,120,150,180,300,365 \
	-D vipdistr=0,1,7,14,30,60,90,120,150,180,300 \
	-conf ${HADOOP_CONF} \
	-jobName "Pay Interval Distribution $date" \
	-gameInfo ${GAMEINFO} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.IntWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-mapperClass  com.taomee.bigdata.task.first_pay_distribution.PayIntervalDistrMapper \
	-reducerClass com.taomee.bigdata.task.first_pay_distribution.PayIntervalDistrReducer \
	-input ${ALL_DIR}/$date/pay-all/payinterval* \
	-addMos "acpay,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
	-addMos "buyitem,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
	-addMos "vipmonth,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
	-output ${SUM_DIR}/$date/pay-interval-distr

#$DB_UPLOAD -type 2 -date $date -task 146 -path ${SUM_DIR}/$date/pay-interval-distr/buyitem*
#$DB_UPLOAD -type 2 -date $date -task 147 -path ${SUM_DIR}/$date/pay-interval-distr/vipmonth*
#首付等级分布
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D mapred.reduce.tasks=4 \
	-conf ${HADOOP_CONF} \
	-jobName "Pay First Level $date" \
	-gameInfo ${GAMEINFO} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${ALL_DIR}/$date/pay-all/part*,com.taomee.bigdata.task.first_pay_distribution.PayFirstMapper \
	-addInput ${DAY_DIR}/$date/level/part*,com.taomee.bigdata.task.first_pay_distribution.PayLevelMapper \
	-reducerClass com.taomee.bigdata.task.first_pay_distribution.PayLevelReducer \
	-output ${DAY_DIR}/$date/pay-first-level

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
	-conf ${HADOOP_CONF} \
	-jobName "Pay Interval Distribution $date" \
	-gameInfo ${GAMEINFO} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.IntWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-mapperClass  com.taomee.bigdata.task.first_pay_distribution.PayLevelDistrMapper \
	-reducerClass com.taomee.bigdata.task.first_pay_distribution.PayLevelDistrReducer \
	-input ${DAY_DIR}/$date/pay-first-level/part* \
	-addMos "buyitem,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
	-addMos "vipmonth,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
	-output ${SUM_DIR}/$date/pay-first-level-distr

#$DB_UPLOAD -type 2 -date $date -task 150 -path ${SUM_DIR}/$date/pay-first-level-distr/buyitem*
#$DB_UPLOAD -type 2 -date $date -task 151 -path ${SUM_DIR}/$date/pay-first-level-distr/vipmonth*
