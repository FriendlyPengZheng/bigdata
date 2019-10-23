export LANG=en_US.UTF-8
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

year_month=`date -d "$date" +%Y%m`

#sum pay of each user
#Mapper output: key=game,platform,zone,server,uid  value=amt,ifvip,time
#Reduce output: key=game,platform,zone,server,uid  value=sum(amt),num_pay,ifvip
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
        -conf ${HADOOP_CONF} \
        -jobName "sumpay $year_month" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -addInput ${DAY_DIR}/${year_month}*/basic/acpay-*,com.taomee.bigdata.task.topk.TopK_Sumpay_Mapper \
        -reducerClass com.taomee.bigdata.task.topk.TopK_Sumpay_Reducer \
        -output ${MONTH_DIR}/$year_month/topk/sumpay
#topK users
#Mapper output: key=game,platform,zone,server value=uid,sum(amt)
#Reduce output: key=game,platform,zone,server value=topK(uid,sum(amt),percent)
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
    	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-conf ${HADOOP_CONF} \
	-jobName "source topk $year_month" \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${MONTH_DIR}/$year_month/topk/sumpay/part*,com.taomee.bigdata.task.topk.TopK_Mapper \
	-reducerClass com.taomee.bigdata.task.topk.TopK_Reducer \
	-output ${MONTH_DIR}/$year_month/topk/topk_source
#Filtering information
#Home page : uid | firstlog_time | level | amt_period | numpay_period | percent
#Buyitems  : firstlog_time | lastlog_time | firstpay_time |lastpay_time | amt_accumulation |
#            numpay_accumulation | coins_use | coin_stock
#Vipmonth  : ifvip | firsttime_vip | lasttime_vip | amt_accumulation | numvip_accumulation |  
#Custom flag : amt_period[-1] | firstlog_time[-2] | level[-3] | numpay_period[-4]

${HADOOP_PATH}hadoop fs -ls ${MONTH_DIR}/$year_month/topk/topk_source/part-*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${MONTH_DIR}/$year_month/topk/topk_source/part-*,com.taomee.bigdata.task.topk.TopK_Source_Mapper "
fi
${HADOOP_PATH}hadoop fs -ls ${ALL_DIR}/$date/account-all/part-*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${ALL_DIR}/$date/account-all/part-*,com.taomee.bigdata.task.topk.TopK_Firstlog_Mapper "
fi
${HADOOP_PATH}hadoop fs -ls ${ALL_DIR}/$date/level/part-*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${ALL_DIR}/$date/level/part-*,com.taomee.bigdata.task.topk.TopK_Level_Mapper "
fi
${HADOOP_PATH}hadoop fs -ls ${MONTH_DIR}/$year_month/topk/sumpay/part-*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${MONTH_DIR}/$year_month/topk/sumpay/part-*,com.taomee.bigdata.task.topk.TopK_Numpay_Mapper "
fi
${HADOOP_PATH}hadoop fs -ls ${ALL_DIR}/$date/pay-all/allpay-*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${ALL_DIR}/$date/pay-all/allpay-*,com.taomee.bigdata.task.topk.TopK_itemOrvip_Mapper "
fi
${HADOOP_PATH}hadoop fs -ls ${ALL_DIR}/$date/coins/part-*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${ALL_DIR}/$date/coins/part-*,com.taomee.bigdata.task.topk.TopK_Coinstock_Mapper "
fi
${HADOOP_PATH}hadoop fs -ls ${ALL_DIR}/$date/coinsuse/part-*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${ALL_DIR}/$date/coinsuse/part-*,com.taomee.bigdata.task.topk.TopK_Coinsuse_Mapper "
fi
${HADOOP_PATH}hadoop fs -ls ${ALL_DIR}/$date/account-all/activeDay-*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${ALL_DIR}/$date/account-all/activeDay-*,com.taomee.bigdata.task.topk.TopK_Activeday_Mapper "
fi
${HADOOP_PATH}hadoop fs -ls ${ALL_DIR}/$date/dayvip/part*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${ALL_DIR}/$date/dayvip/part*,com.taomee.bigdata.task.topk.TopK_ifvip_Mapper "
fi
if [[ $inputs == "" ]]; then
	echo "empty inputs"
	exit 1
fi


${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-conf ${HADOOP_CONF} \
	-jobName "filtering topk $year_month" \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	$inputs \
	-reducerClass com.taomee.bigdata.task.topk.TopK_Filtering_Reducer \
	-addMos "percent,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "firstlogtime,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "lastlogtime,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "level,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "amtperiod,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "numpayperiod,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "ifvip,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "coinsuse,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "coinstock,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "buyitems,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "vipmonth,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-output ${MONTH_DIR}/$year_month/topk/topk_final

sh topk_mysql_month.sh $year_month
sh topk_mysql_user.sh  $year_month

