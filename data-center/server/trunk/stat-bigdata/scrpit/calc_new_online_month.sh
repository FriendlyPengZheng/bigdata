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

#task_id=38,103,105
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-conf ${HADOOP_CONF} \
	-jobName "New Online Month $date" \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${DAY_DIR}/${year_month}*/day-online/part*,com.taomee.bigdata.task.online.UserOnlineMapper \
	-addInput ${MONTH_DIR}/$year_month/account-new/part-*,com.taomee.bigdata.task.online.GrepUserMapper \
	-reducerClass com.taomee.bigdata.task.online.UserOnlineReducer \
	-output ${MONTH_DIR}/$year_month/new-online
	
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
	-D mosCountDayDistr=countdaydistr \
	-D countDayDistr=2,3,4,5,6,7,8,15,22 \
	-D mosLengthAll=lengthall \
	-D mosCountAll=countall \
	-conf ${HADOOP_CONF} \
        -jobName "New Online Month Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass com.taomee.bigdata.task.online.UserOnlineSumMapper \
        -combinerClass com.taomee.bigdata.task.online.UserOnlineSumCombiner \
        -reducerClass com.taomee.bigdata.task.online.UserOnlineSumReducer \
	-input ${MONTH_DIR}/$year_month/new-online/part-* \
	-addMos "countdaydistr,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "lengthall,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "countall,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-output ${SUM_DIR}/$year_month/new-online-month

$DB_UPLOAD -type 2 -date ${year_month}01 -task 38  -path ${SUM_DIR}/$year_month/new-online-month/countdaydistr-*
$DB_UPLOAD -type 2 -date ${year_month}01 -task 103 -path ${SUM_DIR}/$year_month/new-online-month/lengthall-*
$DB_UPLOAD -type 2 -date ${year_month}01 -task 105 -path ${SUM_DIR}/$year_month/new-online-month/countall-*
