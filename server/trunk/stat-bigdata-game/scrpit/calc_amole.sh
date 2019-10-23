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
        -jobName "Amole Source $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.NullWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -addInput ${RAW_DIR}/$date/*/*,com.taomee.bigdata.ads.SourceStatMapper \
        -reducerClass com.taomee.bigdata.task.common.SetReducer \
        -output ${DAY_DIR}/$date/amolesource \
	-addMos "acpay,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "buyitem,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable"

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D divide=true \
	-D mapred.reduce.tasks=4 \
        -conf ${HADOOP_CONF} \
        -jobName "Ads Basic $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -addInput ${DAY_DIR}/$date/amolesource/buyitem-*,com.taomee.bigdata.basic.BasicMapper \
        -addInput ${DAY_DIR}/$date/amolesource/acpay-*,com.taomee.bigdata.basic.BasicMapper \
        -combinerClass  com.taomee.bigdata.basic.BasicCombiner \
        -reducerClass com.taomee.bigdata.basic.BasicReducer \
        -output ${DAY_DIR}/$date/amolebasic \
	-addMos "UCOUNT,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "COUNT,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "SUM,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "MAX,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "SET,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "DISTRSUM,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "DISTRMAX,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "DISTRSET,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "IPDISTR,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable"

${HADOOP_PATH}hadoop fs -rm -skipTrash ${DAY_DIR}/$date/adsbasic/part-* > /dev/null 2>&1 &
${HADOOP_PATH}hadoop fs -rm -skipTrash ${DAY_DIR}/$date/adsbasic/ERROR-* > /dev/null 2>&1 &

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
        -conf ${HADOOP_CONF} \
        -jobName "Ucount $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.FloatWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.basic.UcountMapper \
        -combinerClass  com.taomee.bigdata.basic.SumCountUcountCombiner \
        -reducerClass com.taomee.bigdata.basic.UcountReducer \
        -input ${DAY_DIR}/$date/amolebasic/UCOUNT-* \
        -output ${DAY_DIR}/$date/amoleucount

$DB_UPLOAD -type 2 -date $date -path ${DAY_DIR}/$date/amoleucount/part*

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
        -conf ${HADOOP_CONF} \
        -jobName "Count $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.FloatWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.basic.SumMaxCountMapper \
        -combinerClass  com.taomee.bigdata.basic.SumCountUcountCombiner \
        -reducerClass com.taomee.bigdata.basic.CountReducer \
        -input ${DAY_DIR}/$date/amolebasic/COUNT-* \
        -output ${DAY_DIR}/$date/amolecount

$DB_UPLOAD -type 2 -date $date -path ${DAY_DIR}/$date/amolecount/part*

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
        -conf ${HADOOP_CONF} \
        -jobName "Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.FloatWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.basic.SumMaxCountMapper \
        -combinerClass  com.taomee.bigdata.basic.SumCountUcountCombiner \
        -reducerClass com.taomee.bigdata.basic.SumReducer \
        -input ${DAY_DIR}/$date/amolebasic/SUM-* \
        -output ${DAY_DIR}/$date/amolesum

$DB_UPLOAD -type 2 -date $date -path ${DAY_DIR}/$date/amolesum/part* 
