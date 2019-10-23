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

type=`echo $2 | tr '[A-Z]' '[a-z]'`
if [[ $type != "set" && $type != "max" && $type != "sum" ]]; then
    echo invalid param: $type
    exit
fi
type_upper=`echo $type | tr '[a-z]' '[A-Z]'`

year_month=`date -d "$date" +%Y%m`
yesterday=`date -d "$date -1 day" +%Y%m%d`

${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/DISTR${type_upper}-*
if [[ $? -ne 0 ]]; then
	exit;
fi

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
        -conf ${HADOOP_CONF} \
	-D "op_type=distr_$type" \
        -jobName "Distr $type $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.LongWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.basic.DistrMapper \
        -combinerClass  com.taomee.bigdata.basic.DistrCombiner \
        -reducerClass com.taomee.bigdata.basic.DistrReducer \
        -input ${DAY_DIR}/$date/basic/DISTR${type_upper}-* \
        -output ${DAY_DIR}/$date/distr${type}

$DB_UPLOAD -type 2 -date $date -path ${DAY_DIR}/$date/distr${type}/part* &
