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

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
        -conf ${HADOOP_CONF} \
        -jobName "Spirit All $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.LongWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${DAY_DIR}/$date/basic/obtainspirit-*,com.taomee.bigdata.task.spirit.SourceGetMapper \
	-addInput ${DAY_DIR}/$date/basic/losespirit-*,com.taomee.bigdata.task.spirit.SourceLoseMapper \
	-addInput ${ALL_DIR}/$yesterday/spirit/part-*,com.taomee.bigdata.task.spirit.SpiritMapper \
        -combinerClass com.taomee.bigdata.task.spirit.SpiritReducer \
        -reducerClass com.taomee.bigdata.task.spirit.SpiritReducer \
        -output ${ALL_DIR}/$date/spirit

$DB_UPLOAD -type 2 -date $date -path ${ALL_DIR}/$date/spirit/part-*
