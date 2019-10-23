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

year_month=`date -d "$date" +%Y%m`
yesterday=`date -d "$date -1 day" +%Y%m%d`

${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/SUM-*
if [[ $? -ne 0 ]]; then
	exit;
fi

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
        -conf ${HADOOP_CONF} \
        -jobName "Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.FloatWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.basic.SumMaxCountMapper \
        -combinerClass  com.taomee.bigdata.basic.SumCountUcountCombiner \
        -reducerClass com.taomee.bigdata.basic.SumReducer \
        -input ${DAY_DIR}/$date/basic/SUM-* \
        -output ${DAY_DIR}/$date/sum

#$DB_UPLOAD -type 2 -date $date -path ${DAY_DIR}/$date/sum/part* 
