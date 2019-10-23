WORKDIR=`dirname $0`
WORKDIR=`cd $WORKDIR && pwd`
cd $WORKDIR
echo workdir = $WORKDIR

source config.sh

s=20140516
e=20140516
#date=$1

#if [[ $date == "" ]]; then
#    echo invalid param: date
#    exit
#fi
#
#year_month=`date -d "$date" +%Y%m`
#yesterday=`date -d "$date -1 day" +%Y%m%d`
#
#divide=$2
#if [[ $divide == "" ]]; then
#	divide="false"
#fi

day=$s
while [[ $day -le $e ]]
do
	next=`date -d "+1 day $day" +"%Y%m%d"`
	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
		com.taomee.bigdata.driver.SimpleJobDriver \
		-D divide="true" \
		-D mapred.reduce.tasks=3 \
		-conf ${HADOOP_CONF} \
		-jobName "Basic item vip $day" \
		-outKey org.apache.hadoop.io.Text \
		-outValue org.apache.hadoop.io.Text \
		-inFormat org.apache.hadoop.mapred.TextInputFormat \
		-outFormat org.apache.hadoop.mapred.TextOutputFormat \
		-mapperClass  com.taomee.bigdata.basic.BasicMapper \
		-combinerClass  com.taomee.bigdata.basic.BasicCombiner \
		-reducerClass com.taomee.bigdata.basic.BasicReducer \
		-input /bigdata/tmp/acpay$day \
		-output ${DAY_DIR}/$day/old/pay \
		-addMos "UCOUNT,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
		-addMos "SUM,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
		-addMos "COUNT,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \

	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
		com.taomee.bigdata.driver.SimpleJobDriver \
		-D mapred.reduce.tasks=1 \
		-conf ${HADOOP_CONF} \
		-jobName "Ucount item vip $day" \
		-outKey org.apache.hadoop.io.Text \
		-outValue org.apache.hadoop.io.FloatWritable \
		-inFormat org.apache.hadoop.mapred.TextInputFormat \
		-outFormat org.apache.hadoop.mapred.TextOutputFormat \
		-mapperClass  com.taomee.bigdata.basic.UcountMapper \
		-combinerClass  com.taomee.bigdata.basic.SumCountUcountCombiner \
		-reducerClass com.taomee.bigdata.basic.UcountReducer \
		-input ${DAY_DIR}/$day/old/pay/UCOUNT-* \
		-output ${DAY_DIR}/$day/old/ucount

	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
		com.taomee.bigdata.driver.SimpleJobDriver \
		-D mapred.reduce.tasks=1 \
		-conf ${HADOOP_CONF} \
		-jobName "Count item vip $day" \
		-outKey org.apache.hadoop.io.Text \
		-outValue org.apache.hadoop.io.FloatWritable \
		-inFormat org.apache.hadoop.mapred.TextInputFormat \
		-outFormat org.apache.hadoop.mapred.TextOutputFormat \
		-mapperClass  com.taomee.bigdata.basic.SumMaxCountMapper \
		-combinerClass  com.taomee.bigdata.basic.SumCountUcountCombiner \
		-reducerClass com.taomee.bigdata.basic.CountReducer \
		-input ${DAY_DIR}/$day/old/pay/COUNT-* \
		-output ${DAY_DIR}/$day/old/count

	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
		com.taomee.bigdata.driver.SimpleJobDriver \
		-D mapred.reduce.tasks=1 \
		-conf ${HADOOP_CONF} \
		-jobName "Sum item vip $day" \
		-outKey org.apache.hadoop.io.Text \
		-outValue org.apache.hadoop.io.FloatWritable \
		-inFormat org.apache.hadoop.mapred.TextInputFormat \
		-outFormat org.apache.hadoop.mapred.TextOutputFormat \
		-mapperClass  com.taomee.bigdata.basic.SumMaxCountMapper \
		-combinerClass  com.taomee.bigdata.basic.SumCountUcountCombiner \
		-reducerClass com.taomee.bigdata.basic.SumReducer \
		-input ${DAY_DIR}/$day/old/pay/SUM-* \
		-output ${DAY_DIR}/$day/old/sum
	$DB_UPLOAD -type 2 -date $day -path ${DAY_DIR}/$day/old/ucount/part*
    	$DB_UPLOAD -type 2 -date $day -path ${DAY_DIR}/$day/old/count/part*
	$DB_UPLOAD -type 2 -date $day -path ${DAY_DIR}/$day/old/sum/part* 
	day=$next
done
