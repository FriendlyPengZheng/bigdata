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

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D mapred.reduce.tasks=0 \
        -conf ${HADOOP_CONF} \
        -jobName "Ahero Source $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.NullWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${RAW_DIR}/$date/82/*,com.taomee.bigdata.ahero.AheroMapper \
        -reducerClass com.taomee.bigdata.task.newlog.NewLoginReducer \
        -output ${DAY_DIR}/$date/ahero
${HADOOP_PATH}hadoop fs -rm ${RAW_DIR}/$date/82/*
${HADOOP_PATH}hadoop fs -mv ${DAY_DIR}/$date/ahero/p* ${RAW_DIR}/$date/82/

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D divide=true \
	-D mapred.reduce.tasks=30 \
	-D mapred.output.compress=true \
	-D mapred.output.compression.codec=org.apache.hadoop.io.compress.BZip2Codec \
        -conf ${HADOOP_CONF} \
        -jobName "Basic $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.basic.BasicMapper \
        -combinerClass  com.taomee.bigdata.basic.BasicCombiner \
        -reducerClass com.taomee.bigdata.basic.BasicReducer \
        -input ${RAW_DIR}/$date/*/* \
        -output ${TMP_DIR}/$date/basic \
	-addMos "UCOUNT,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "COUNT,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "SUM,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "MAX,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "SET,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "DISTRSUM,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "DISTRMAX,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "DISTRSET,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "IPDISTR,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \

/opt/taomee/hadoop/hadoop/bin/hadoop fs -cat ${TMP_DIR}/$date/basic/lgac-* > lgac-m-merge.bz2
${HADOOP_PATH}hadoop fs -rm ${DAY_DIR}/$date/basic/lgac*
${HADOOP_PATH}hadoop fs -moveFromLocal lgac-m-merge.bz2 ${DAY_DIR}/$date/basic/

#task_id=12
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D mapred.reduce.tasks=30 \
	-D mapred.output.compress=true \
	-D mapred.output.compression.codec=org.apache.hadoop.io.compress.BZip2Codec \
        -conf ${HADOOP_CONF} \
        -jobName "Account All $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${DAY_DIR}/$date/basic/lgac-*,com.taomee.bigdata.task.newlog.SourceNewLoginMapper \
	-addInput ${ALL_DIR}/$yesterday/account-all/part-*,com.taomee.bigdata.task.newlog.NewLoginMapper \
        -reducerClass com.taomee.bigdata.task.newlog.NewLoginReducer \
	-addMos "activeDay,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "loginDay,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "firstLog,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
        -output ${ALL_DIR}/$date/account-all

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D distr=2,4,8,15,31,91,181,366 \
	-D mapred.reduce.tasks=2 \
        -conf ${HADOOP_CONF} \
        -jobName "Account Day Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.newlog.NewLoginSumMapper \
        -reducerClass com.taomee.bigdata.task.newlog.NewLoginSumReducer \
        -input ${ALL_DIR}/$date/account-all/active* \
	-addMos "firstlog,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
        -output ${SUM_DIR}/$date/account-nday

$DB_UPLOAD -type 2 -date $date -task 13 -path ${SUM_DIR}/$date/account-nday/part*
$DB_UPLOAD -type 2 -date $date -task 14 -path ${SUM_DIR}/$date/account-nday/firstlog*

#task_id=132
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D distr=2,3,4,5,6,7,8,11,16,31,61,91,181,361 \
	-D mapred.reduce.tasks=2 \
        -conf ${HADOOP_CONF} \
        -jobName "Account Day Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.newlog.NewLoginSumMapper \
        -reducerClass com.taomee.bigdata.task.newlog.NewLoginSumReducer \
        -input ${ALL_DIR}/$date/account-all/login* \
	-addMos "firstlog,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
        -output ${SUM_DIR}/$date/login-nday
$DB_UPLOAD -type 2 -date $date -task 132 -path ${SUM_DIR}/$date/login-nday/part*

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=4 \
        -conf ${HADOOP_CONF} \
        -jobName "Account All Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.common.SetSumMapper \
        -combinerClass  com.taomee.bigdata.task.common.SetSumReducer \
        -reducerClass com.taomee.bigdata.task.common.SetSumReducer \
        -input ${ALL_DIR}/$date/account-all/part-* \
        -output ${SUM_DIR}/$date/account-all

$DB_UPLOAD -type 2 -date $date -task 12 -path ${SUM_DIR}/$date/account-all/part*
