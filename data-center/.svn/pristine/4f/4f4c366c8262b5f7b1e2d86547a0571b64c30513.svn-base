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

#task_id=34,35,36,37,41,43,44
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-conf ${HADOOP_CONF} \
	-jobName "New Online $date" \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -addInput ${DAY_DIR}/$date/day-online/part-*,com.taomee.bigdata.task.online.UserOnlineMapper \
	-addInput ${ALL_DIR}/$date/account-all/firstLog-*,com.taomee.bigdata.task.online.GrepUserMapper \
	-reducerClass com.taomee.bigdata.task.online.UserOnlineReducer \
	-output ${DAY_DIR}/$date/new-online
	
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
	-D mosCountDistr=countdistr \
	-D countDistr=2,4,6,11,21,51 \
	-D mosLengthDistr=lengthdistr \
	-D lengthDistr=11,61,181,601,1801,3601,7201,14401 \
	-D mosLengthAll=lengthall \
	-D mosLengthAvg=lengthavg \
	-D mosCountAll=countall \
	-D mosCountAvg=countavg \
	-D mosTimeDistr=timedistr \
	-D timeDistr=4,11,31,61,181,601,1801,3601 \
	-conf ${HADOOP_CONF} \
        -jobName "New Online Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.online.UserOnlineSumMapper \
        -combinerClass  com.taomee.bigdata.task.online.UserOnlineSumCombiner \
        -reducerClass com.taomee.bigdata.task.online.UserOnlineSumReducer \
        -input ${DAY_DIR}/$date/new-online/part-* \
	-addMos "countdistr,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "lengthdistr,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "lengthall,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "lengthavg,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "countall,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "countavg,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "timedistr,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
        -output ${SUM_DIR}/$date/new-online

$DB_UPLOAD -type 2 -date $date -task 34 -path ${SUM_DIR}/$date/new-online/lengthall-*
$DB_UPLOAD -type 2 -date $date -task 35 -path ${SUM_DIR}/$date/new-online/lengthavg-*
$DB_UPLOAD -type 2 -date $date -task 36 -path ${SUM_DIR}/$date/new-online/countall-*
$DB_UPLOAD -type 2 -date $date -task 37 -path ${SUM_DIR}/$date/new-online/countavg-*
$DB_UPLOAD -type 2 -date $date -task 41 -path ${SUM_DIR}/$date/new-online/countdistr-*
$DB_UPLOAD -type 2 -date $date -task 43 -path ${SUM_DIR}/$date/new-online/lengthdistr-*
$DB_UPLOAD -type 2 -date $date -task 44 -path ${SUM_DIR}/$date/new-online/timedistr-*
