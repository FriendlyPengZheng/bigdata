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
        -jobName "Set $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.basic.SetMapper \
        -combinerClass  com.taomee.bigdata.basic.SetCombiner \
        -reducerClass com.taomee.bigdata.basic.SetReducer \
        -input ${DAY_DIR}/$date/custom/SET* \
        -output ${DAY_DIR}/$date/customset

$DB_UPLOAD -type 2 -date $date -path ${DAY_DIR}/$date/customset/part* 
