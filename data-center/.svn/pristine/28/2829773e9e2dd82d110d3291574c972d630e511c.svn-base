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

yesterday=`date -d "${date} -1 day" +%Y%m%d`
no_in_week=`date -d "${date}" +%u`
n=`expr ${no_in_week} - 1`
first_week_day=`date -d "${date} -${n} day" +%Y%m%d`

#multiple input paths
inputs=""
for((i=0;i<=${n};i++));
do
	last_day=`date -d "$date -$i day" +%Y%m%d`
	${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$last_day/basic/acpay-*
	if [[ $? -eq 0 ]]; then
		inputs="$inputs -addInput ${DAY_DIR}/$last_day/basic/acpay-*,com.taomee.bigdata.task.segpay.OriExtKeyMapper"
	fi
done
if [[ $inputs == "" ]]; then
        echo "empty inputs"
        exit 1
fi

#task 68-70,87-88输出每人的付费额、次数
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D param.key=_amt_ \
	-D ext.key=_sstid_ \
	-D calc.type=union \
	-D mapred.reduce.tasks=4 \
	-conf ${HADOOP_CONF} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.FloatWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "Week Pay amount,count,acount,dist step1 $date" \
	-reducerClass com.taomee.bigdata.task.segpay.DiffIntReducer \
	$inputs \
	-output ${WEEK_DIR}/$first_week_day/pay-week

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
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "Week Pay amount,count,acount,dist step2 $date" \
	-addMos "amount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "ucount,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "count,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "amountdistr,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "countdistr,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-input ${WEEK_DIR}/$first_week_day/pay-week/part-* \
	-mapperClass com.taomee.bigdata.task.segpay.MiddleGZSPMapper \
	-reducerClass com.taomee.bigdata.task.segpay.PayAmtCountUcountDistReducer \
	-output ${SUM_DIR}/$first_week_day/pay-week

$DB_UPLOAD -type 2 -date ${first_week_day} -task 68  -path ${SUM_DIR}/$first_week_day/pay-week/amount-*
$DB_UPLOAD -type 2 -date ${first_week_day} -task 69  -path ${SUM_DIR}/$first_week_day/pay-week/ucount-*
$DB_UPLOAD -type 2 -date ${first_week_day} -task 70  -path ${SUM_DIR}/$first_week_day/pay-week/count-*
$DB_UPLOAD -type 2 -date ${first_week_day} -task 87  -path ${SUM_DIR}/$first_week_day/pay-week/countdistr-*
$DB_UPLOAD -type 2 -date ${first_week_day} -task 88  -path ${SUM_DIR}/$first_week_day/pay-week/amountdistr-*
