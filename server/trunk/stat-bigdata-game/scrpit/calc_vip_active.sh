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

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D mapred.reduce.tasks=4 \
	-D "ncount=3" \
	-D "stid=_lgac_" \
        -conf ${HADOOP_CONF} \
        -jobName "vip active day $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -addInput ${ALL_DIR}/$date/dayvip/part*,com.taomee.bigdata.task.common.ANDMapper \
        -addInput ${DAY_DIR}/$date/basic/lgac*,com.taomee.bigdata.task.common.SourceDIF2Mapper \
	-combinerClass  com.taomee.bigdata.task.common.SetCombiner \
        -reducerClass com.taomee.bigdata.task.common.SetReducer \
        -output ${DAY_DIR}/$date/vipactive

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D mapred.reduce.tasks=1 \
        -conf ${HADOOP_CONF} \
        -jobName "vip active sum day $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -addInput ${DAY_DIR}/$date/vipactive/part*,com.taomee.bigdata.task.common.SetSumMapper \
	-combinerClass  com.taomee.bigdata.task.common.SetSumReducer \
        -reducerClass com.taomee.bigdata.task.common.SetSumReducer \
        -output ${SUM_DIR}/$date/vipactive

#$DB_UPLOAD -type 2 -date $date -task 351 -path ${SUM_DIR}/$date/vipactive/part*
