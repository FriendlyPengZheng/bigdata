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
        com.taomee.bigdata.driver.SimpleJobDriver \
        -conf ${HADOOP_CONF} \
        -jobName "Max $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.FloatWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.basic.SumMaxCountMapper \
        -combinerClass  com.taomee.bigdata.basic.MaxCombiner \
        -reducerClass com.taomee.bigdata.basic.MaxReducer \
        -input ${DAY_DIR}/$date/custom/MAX-* \
        -output ${DAY_DIR}/$date/custommax
	
$DB_UPLOAD -type 2 -date $date -path ${DAY_DIR}/$date/custommax/part* 
