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
        -conf ${HADOOP_CONF} \
        -jobName "Coins All $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.LongWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${DAY_DIR}/$date/basic/buyitem-*,com.taomee.bigdata.task.coins.SourceMapper \
        -reducerClass com.taomee.bigdata.task.coins.CoinsReducer \
	-addMos "userbuy,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.LongWritable" \
        -output ${TMP_DIR}/$date/coins

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=1 \
        -conf ${HADOOP_CONF} \
        -jobName "User Buy Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.LongWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass com.taomee.bigdata.task.coins.CoinsBuyMapper \
        -combinerClass com.taomee.bigdata.task.coins.CoinsBuyReducer \
        -reducerClass com.taomee.bigdata.task.coins.CoinsBuyReducer \
        -input ${TMP_DIR}/$date/coins/userbuy-* \
        -output ${TMP_DIR}/$date/coinsbuy

$DB_UPLOAD -type 2 -date $date -task 343 -path ${TMP_DIR}/$date/coinsbuy/part-*
