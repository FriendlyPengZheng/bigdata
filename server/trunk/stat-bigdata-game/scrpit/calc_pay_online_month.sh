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

#task_id=55,111,113
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-conf ${HADOOP_CONF} \
	-jobName "Pay Online Month $date" \
	-gameInfo ${GAMEINFO} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${DAY_DIR}/${year_month}*/day-online/part*,com.taomee.bigdata.task.online.UserOnlineMapper \
	-addInput ${MONTH_DIR}/$year_month/payer/part*,com.taomee.bigdata.task.online.GrepUserMapper \
	-reducerClass com.taomee.bigdata.task.online.UserOnlineReducer \
	-output ${MONTH_DIR}/$year_month/pay-online
	
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
	-D mosCountDayDistr=countdaydistr \
	-D countDayDistr=2,3,4,5,6,7,8,15,22 \
	-D mosLengthAll=lengthall \
	-D mosCountAll=countall \
	-conf ${HADOOP_CONF} \
        -jobName "Pay Online Month Sum $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass com.taomee.bigdata.task.online.UserOnlineSumMapper \
        -combinerClass com.taomee.bigdata.task.online.UserOnlineSumCombiner \
        -reducerClass com.taomee.bigdata.task.online.UserOnlineSumReducer \
	-input ${MONTH_DIR}/$year_month/pay-online/part* \
	-addMos "countdaydistr,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "lengthall,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "countall,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-output ${SUM_DIR}/$year_month/pay-online-month

#$DB_UPLOAD -type 2 -date ${year_month}01 -task 55  -path ${SUM_DIR}/$year_month/pay-online-month/countdaydistr*
#$DB_UPLOAD -type 2 -date ${year_month}01 -task 111 -path ${SUM_DIR}/$year_month/pay-online-month/lengthall*
#$DB_UPLOAD -type 2 -date ${year_month}01 -task 113 -path ${SUM_DIR}/$year_month/pay-online-month/countall*
