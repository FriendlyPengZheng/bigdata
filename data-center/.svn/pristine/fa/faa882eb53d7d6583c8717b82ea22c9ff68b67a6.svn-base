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
        -addInput ${RAW_DIR}/$date/*/*_basic,com.taomee.bigdata.ads.SourceStatMapper \
        -addInput /ads/boss/mb/$date*,com.taomee.bigdata.ads.SourceMBMapper \
        -addInput /ads/boss/vip/$date*,com.taomee.bigdata.ads.SourceVIPMapper \
        -reducerClass com.taomee.bigdata.task.common.SetReducer \
        -output ${DAY_DIR}/$date/adssource \
	-addMos "msremain,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "acpay,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "buyitem,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable"

${HADOOP_PATH}hadoop fs -cat ${DAY_DIR}/$date/adssource/buyitem* > buyitem-$date
${HADOOP_PATH}hadoop fs -cat ${DAY_DIR}/$date/adssource/acpay* > acpay-$date
${HADOOP_PATH}hadoop fs -cat ${DAY_DIR}/$date/adssource/msremain* > msremain-$date
${HADOOP_PATH}hadoop fs -rm -skipTrash ${DAY_DIR}/$date/adssource/part* > /dev/null 2>&1 &
${HADOOP_PATH}hadoop fs -rm -skipTrash ${DAY_DIR}/$date/adssource/ERROR* > /dev/null 2>&1 &
${HADOOP_PATH}hadoop fs -rm -skipTrash ${DAY_DIR}/$date/adssource/DEBUG* > /dev/null 2>&1 &
${HADOOP_PATH}hadoop fs -rm -skipTrash ${DAY_DIR}/$date/adssource/buyitem* > /dev/null 2>&1
${HADOOP_PATH}hadoop fs -rm -skipTrash ${DAY_DIR}/$date/adssource/acpay* > /dev/null 2>&1
${HADOOP_PATH}hadoop fs -rm -skipTrash ${DAY_DIR}/$date/adssource/msremain* > /dev/null 2>&1
${HADOOP_PATH}hadoop fs -copyFromLocal buyitem-$date ${DAY_DIR}/$date/adssource/buyitem-m-merge
${HADOOP_PATH}hadoop fs -copyFromLocal acpay-$date ${DAY_DIR}/$date/adssource/acpay-m-merge
${HADOOP_PATH}hadoop fs -copyFromLocal msremain-$date ${DAY_DIR}/$date/adssource/msremain-m-merge
rm -f buyitem-$date acpay-$date msremain-$date

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D divide=true \
	-D mapred.reduce.tasks=4 \
        -conf ${HADOOP_CONF} \
        -jobName "Ads Basic $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -addInput ${DAY_DIR}/$date/adssource/buyitem*,com.taomee.bigdata.basic.BasicMapper \
        -addInput ${DAY_DIR}/$date/adssource/acpay*,com.taomee.bigdata.basic.BasicMapper \
        -addInput ${DAY_DIR}/$date/adssource/msremain*,com.taomee.bigdata.basic.BasicMapper \
        -combinerClass  com.taomee.bigdata.basic.BasicCombiner \
        -reducerClass com.taomee.bigdata.basic.BasicReducer \
        -output ${DAY_DIR}/$date/adsbasic \
	-addMos "UCOUNT,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "COUNT,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "SUM,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "MAX,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "SET,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "DISTRSUM,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "DISTRMAX,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "DISTRSET,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "IPDISTR,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable"

${HADOOP_PATH}hadoop fs -rm -skipTrash ${DAY_DIR}/$date/adsbasic/part* > /dev/null 2>&1 &
${HADOOP_PATH}hadoop fs -rm -skipTrash ${DAY_DIR}/$date/adsbasic/ERROR* > /dev/null 2>&1 &
${HADOOP_PATH}hadoop fs -rm ${DAY_DIR}/$date/basic/acpay*
${HADOOP_PATH}hadoop fs -rm ${DAY_DIR}/$date/basic/buyitem*
${HADOOP_PATH}hadoop fs -rm ${DAY_DIR}/$date/basic/msremain*
${HADOOP_PATH}hadoop fs -mv ${DAY_DIR}/$date/adsbasic/acpay*   ${DAY_DIR}/$date/basic/
${HADOOP_PATH}hadoop fs -mv ${DAY_DIR}/$date/adsbasic/buyitem* ${DAY_DIR}/$date/basic/ 
${HADOOP_PATH}hadoop fs -mv ${DAY_DIR}/$date/adsbasic/msremain* ${DAY_DIR}/$date/basic/ 

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
        -conf ${HADOOP_CONF} \
        -jobName "Ucount $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.FloatWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.basic.UcountMapper \
        -combinerClass  com.taomee.bigdata.basic.SumCountUcountCombiner \
        -reducerClass com.taomee.bigdata.basic.UcountReducer \
        -input ${DAY_DIR}/$date/adsbasic/UCOUNT* \
        -output ${DAY_DIR}/$date/adsucount

#$DB_UPLOAD -type 2 -date $date -path ${DAY_DIR}/$date/adsucount/part*

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
        -conf ${HADOOP_CONF} \
        -jobName "Count $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.FloatWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.basic.SumMaxCountMapper \
        -combinerClass  com.taomee.bigdata.basic.SumCountUcountCombiner \
        -reducerClass com.taomee.bigdata.basic.CountReducer \
        -input ${DAY_DIR}/$date/adsbasic/COUNT* \
        -output ${DAY_DIR}/$date/adscount

#$DB_UPLOAD -type 2 -date $date -path ${DAY_DIR}/$date/adscount/part*

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
        -conf ${HADOOP_CONF} \
        -jobName "Sum $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.FloatWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.basic.SumMaxCountMapper \
        -combinerClass  com.taomee.bigdata.basic.SumCountUcountCombiner \
        -reducerClass com.taomee.bigdata.basic.SumReducer \
        -input ${DAY_DIR}/$date/adsbasic/SUM* \
        -output ${DAY_DIR}/$date/adssum
