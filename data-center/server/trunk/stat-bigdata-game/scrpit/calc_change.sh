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
        -jobName "Change $date" \
        -outKey org.apache.hadoop.io.LongWritable \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.cplan_acpay.ChangeSourceFileMapper \
        -reducerClass com.taomee.bigdata.cplan_acpay.BasicReducer \
        -input ${RAW_DIR}/$date/16/* \
        -output ${TMP_DIR}/$date/16/ \
