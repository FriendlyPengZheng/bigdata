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
        -jobName "Ucount $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.FloatWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.basic.UcountMapper \
        -combinerClass  com.taomee.bigdata.basic.SumCountUcountCombiner \
        -reducerClass com.taomee.bigdata.basic.UcountReducer \
        -input ${DAY_DIR}/$date/custom/UCOUNT-* \
        -output ${DAY_DIR}/$date/customucount

$DB_UPLOAD -type 2 -date $date -path ${DAY_DIR}/$date/customucount/part*
