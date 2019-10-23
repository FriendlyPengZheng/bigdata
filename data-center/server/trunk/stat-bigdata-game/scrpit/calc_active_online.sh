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

#task_id=48,50
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-conf ${HADOOP_CONF} \
	-D "stid=_lgac_" \
	-jobName "Account Online $date" \
	-gameInfo ${GAMEINFO} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -addInput ${DAY_DIR}/$date/day-online/part*,com.taomee.bigdata.task.online.UserOnlineMapper \
	-addInput ${DAY_DIR}/$date/basic/lgac*,com.taomee.bigdata.task.online.SourceGrepUserMapper \
	-reducerClass com.taomee.bigdata.task.online.UserOnlineReducer \
	-output ${DAY_DIR}/$date/account-online
	
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
	-D mosCountDistr=countdistr \
	-D countDistr=2,4,6,11,21,51 \
	-D mosLengthDistr=lengthdistr \
	-D lengthDistr=11,61,181,601,1801,3601,7201,14401 \
	-conf ${HADOOP_CONF} \
        -jobName "Account Online Sum $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.online.UserOnlineSumMapper \
        -combinerClass  com.taomee.bigdata.task.online.UserOnlineSumCombiner \
        -reducerClass com.taomee.bigdata.task.online.UserOnlineSumReducer \
        -input ${DAY_DIR}/$date/account-online/part* \
	-addMos "countdistr,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "lengthdistr,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
        -output ${SUM_DIR}/$date/account-online

#$DB_UPLOAD -type 2 -date $date -task 48 -path ${SUM_DIR}/$date/account-online/countdistr*
#$DB_UPLOAD -type 2 -date $date -task 50 -path ${SUM_DIR}/$date/account-online/lengthdistr*
