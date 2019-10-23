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
yesterday=`date -d "$date -1 day" +%Y%m%d`

#task_id=13,14
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D distr=2,4,8,15,31,91,181,366 \
	-D mapred.reduce.tasks=2 \
        -conf ${HADOOP_CONF} \
        -jobName "Account Day Sum $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.newlog.NewLoginSumMapper \
        -reducerClass com.taomee.bigdata.task.newlog.NewLoginSumReducer \
        -input ${ALL_DIR}/$date/account-all/active* \
	-addMos "firstlog,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
        -output ${SUM_DIR}/$date/account-nday

#$DB_UPLOAD -type 2 -date $date -task 13 -path ${SUM_DIR}/$date/account-nday/part*
#$DB_UPLOAD -type 2 -date $date -task 14 -path ${SUM_DIR}/$date/account-nday/firstlog*

#task_id=132
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D distr=2,3,4,5,6,7,8,11,16,31,61,91,181,361 \
	-D mapred.reduce.tasks=2 \
        -conf ${HADOOP_CONF} \
        -jobName "Account Day Sum $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.newlog.NewLoginSumMapper \
        -reducerClass com.taomee.bigdata.task.newlog.NewLoginSumReducer \
        -input ${ALL_DIR}/$date/account-all/login* \
	-addMos "firstlog,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
        -output ${SUM_DIR}/$date/login-nday

#$DB_UPLOAD -type 2 -date $date -task 132 -path ${SUM_DIR}/$date/login-nday/part*
