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

#task_id=91,92
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=1 \
        -conf ${HADOOP_CONF} \
        -jobName "ACU PCU $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.olcnt.SourceOnlineMapper \
        -reducerClass com.taomee.bigdata.task.olcnt.OnlineReducer \
        -input ${DAY_DIR}/$date/basic/olcnt* \
	-addMos "ACU,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "PCU,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
        -output ${SUM_DIR}/$date/ACU-PCU

#$DB_UPLOAD -type 2 -date $date -task 91 -path ${SUM_DIR}/$date/ACU-PCU/ACU*
#$DB_UPLOAD -type 2 -date $date -task 92 -path ${SUM_DIR}/$date/ACU-PCU/PCU*

#task_id=331,332
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=1 \
	-D nday=`date -d "$date" +%d` \
        -conf ${HADOOP_CONF} \
        -jobName "Month ACU PCU $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.olcnt.SourceOnlineMapper \
        -reducerClass com.taomee.bigdata.task.olcnt.OnlineReducer \
        -input ${DAY_DIR}/${year_month}*/basic/olcnt* \
	-addMos "ACU,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "PCU,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
        -output ${SUM_DIR}/${year_month}01/month-ACU-PCU

#$DB_UPLOAD -type 2 -date ${year_month}01 -task 331 -path ${SUM_DIR}/${year_month}01/month-ACU-PCU/ACU*
#$DB_UPLOAD -type 2 -date ${year_month}01 -task 332 -path ${SUM_DIR}/${year_month}01/month-ACU-PCU/PCU*
