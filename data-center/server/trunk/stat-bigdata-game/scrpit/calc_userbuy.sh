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
	-D mapred.reduce.tasks=0 \
	-D "channel=32:1;34:2;36:5;42:6;74:10;145:10;144:16" \
	-D "item=100001:199999;200000:299999;340000:349999;320000:329999;510000:519999;334360:334369" \
	-D "vipchannel=90;91;99;100" \
        -conf ${HADOOP_CONF} \
        -jobName "Ads Source $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.NullWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -addInput /ads/boss/mb/$date*,com.taomee.bigdata.ads.SourceMBMapper \
        -reducerClass com.taomee.bigdata.task.common.SetReducer \
        -output ${TMP_DIR}/$date/adssource \
	-addMos "acpay,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "buyitem,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable"

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D mapred.reduce.tasks=4 \
        -conf ${HADOOP_CONF} \
        -jobName "Coins All $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.LongWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${TMP_DIR}/$date/adssource/buyitem*,com.taomee.bigdata.task.coins.SourceMapper \
        -reducerClass com.taomee.bigdata.task.coins.CoinsReducer \
	-addMos "userbuy,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.LongWritable" \
        -output ${TMP_DIR}/$date/coins

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=1 \
        -conf ${HADOOP_CONF} \
        -jobName "User Buy Sum $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.LongWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass com.taomee.bigdata.task.coins.CoinsBuyMapper \
        -combinerClass com.taomee.bigdata.task.coins.CoinsBuyReducer \
        -reducerClass com.taomee.bigdata.task.coins.CoinsBuyReducer \
        -input ${TMP_DIR}/$date/coins/userbuy* \
        -output ${TMP_DIR}/$date/coinsbuy

#$DB_UPLOAD -type 2 -date $date -task 343 -path ${TMP_DIR}/$date/coinsbuy/part*
