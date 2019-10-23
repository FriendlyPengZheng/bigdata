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

${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/MAX-*
if [[ $? -ne 0 ]]; then
	exit;
fi

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
        -conf ${HADOOP_CONF} \
        -jobName "Max $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.FloatWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.basic.SumMaxCountMapper \
        -combinerClass  com.taomee.bigdata.basic.MaxCombiner \
        -reducerClass com.taomee.bigdata.basic.MaxReducer \
        -input ${DAY_DIR}/$date/basic/MAX* \
        -output ${DAY_DIR}/$date/max
	
#$DB_UPLOAD -type 2 -date $date -path ${DAY_DIR}/$date/max/part* 
