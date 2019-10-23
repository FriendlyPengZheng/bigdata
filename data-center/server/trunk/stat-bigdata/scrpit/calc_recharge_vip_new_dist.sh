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
this_month=`date -d "${date}" +%Y%m`
last_month=`date -d "${this_month}01 -1 month" +%Y%m`

########################################################################
# mapper output: key=game,zone,server,platform,uid value = 0, value(包月时长),payamt ->上个月
# mapper output: key=game,zone,server,platform,uid value = 1, value(包月时长),payamt ->本月
# reduce output: vipnew: key=game,zone,server,platform,uid value=value(包月时长),payamt(all),cnt(次数）
# reduce output: viplast: key=game,zone,server,platform,uid value=value(包月时长),payamt(all),cnt(次数）
########################################################################
${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/${this_month}*/basic/mibiconsume-*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${DAY_DIR}/${this_month}*/basic/mibiconsume-*,com.taomee.bigdata.task.recharge.RechargeVipMonthMapper  "
fi
${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/${last_month}*/basic/mibiconsume-*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${DAY_DIR}/${last_month}*/basic/mibiconsume-*,com.taomee.bigdata.task.recharge.RechargeVipLastMapper  "
fi
if [[ $inputs == "" ]]; then
	echo "empty inputs"
	exit 1
fi

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
		-D mapred.reduce.tasks=8 \
        -conf ${HADOOP_CONF} \
        -jobName "Recharge Vip Month Dist Source $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
		$inputs \
		-addMos "vipnew,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
		-addMos "viplast,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
        -reducerClass com.taomee.bigdata.task.recharge.RechargeVipMonthReducer \
        -output ${MONTH_DIR}/${this_month}/recharge-vip-month

# mapper output: key=game,zone,server,platform,value(包月时长)  value=1,payamt(all),cnt(次数）
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
        -D mapred.reduce.tasks=2 \
        -conf ${HADOOP_CONF} \
        -jobName "Recharge Vip New Dist $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.recharge.RechargeVipNewDistMapper \
        -reducerClass com.taomee.bigdata.task.recharge.RechargeVipNewDistReducer \
        -input ${MONTH_DIR}/${this_month}/recharge-vip-month/vipnew-* \
        -addMos "count,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
        -addMos "ucount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
        -addMos "payamt,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
        -output ${SUM_DIR}/$date/recharge-vip-new-dist

$DB_UPLOAD -type 2 -date $date -task 385 -path ${SUM_DIR}/$date/recharge-vip-new-dist/ucount*
$DB_UPLOAD -type 2 -date $date -task 386 -path ${SUM_DIR}/$date/recharge-vip-new-dist/count*
$DB_UPLOAD -type 2 -date $date -task 387 -path ${SUM_DIR}/$date/recharge-vip-new-dist/payamt*

# mapper output: key=game,zone,server,platform,value(包月时长)  value=1,payamt(all),cnt(次数）
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
        -D mapred.reduce.tasks=2 \
        -conf ${HADOOP_CONF} \
        -jobName "Recharge Vip Last Dist $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.recharge.RechargeVipNewDistMapper \
        -reducerClass com.taomee.bigdata.task.recharge.RechargeVipNewDistReducer \
        -input ${MONTH_DIR}/${this_month}/recharge-vip-month/viplast-* \
        -addMos "count,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
        -addMos "ucount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
        -addMos "payamt,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
        -output ${SUM_DIR}/$date/recharge-vip-last-dist

$DB_UPLOAD -type 2 -date $date -task 388 -path ${SUM_DIR}/$date/recharge-vip-last-dist/ucount*
$DB_UPLOAD -type 2 -date $date -task 389 -path ${SUM_DIR}/$date/recharge-vip-last-dist/count*
$DB_UPLOAD -type 2 -date $date -task 390 -path ${SUM_DIR}/$date/recharge-vip-last-dist/payamt*
