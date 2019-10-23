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

divide=$2
if [[ $divide == "" ]]; then
	divide="false"
fi

	#-D mapred.output.compress=true \
	#-D mapred.output.compression.codec=org.apache.hadoop.io.compress.BZip2Codec \
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D divide=$divide \
	-D mapred.reduce.tasks=30 \
        -conf ${HADOOP_CONF} \
        -jobName "Basic $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.basic.BasicMapper \
        -combinerClass  com.taomee.bigdata.basic.BasicCombiner \
        -reducerClass com.taomee.bigdata.basic.BasicReducer \
        -input ${RAW_DIR}/$date/*/*basic \
        -output ${DAY_DIR}/$date/basic \
	-addMos "UCOUNT,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "COUNT,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "SUM,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "MAX,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "SET,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "DISTRSUM,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "DISTRMAX,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "DISTRSET,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "IPDISTR,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \

#${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/
#${HADOOP_PATH}hadoop fs -rm -skipTrash ${DAY_DIR}/$date/basic/part* &
