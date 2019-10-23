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

year_month=`date -d "${date}" +%Y%m`

for n in {1..6}
do
	inputs=""
	nday=`date -d "${year_month}01 -${n} month" +%Y%m`
	#task_id=23
	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-conf ${HADOOP_CONF} \
	-jobName "Active ${n} day Keep Month $date" \
	-gameInfo ${GAMEINFO} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.IntWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${MONTH_DIR}/$nday/account/part*,com.taomee.bigdata.task.nday.NDay0Mapper \
	-addInput ${MONTH_DIR}/$year_month/account/part*,com.taomee.bigdata.task.nday.NDay${n}Mapper \
	-reducerClass com.taomee.bigdata.task.keep.KeepReducer \
	-output ${MONTH_DIR}/$year_month/active-keep-${n}-month

	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=2 \
	-conf ${HADOOP_CONF} \
	-jobName "Active ${n} Keep Month Sum $date" \
	-gameInfo ${GAMEINFO} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-mapperClass  com.taomee.bigdata.task.nday.NDaySumMapper \
	-combinerClass  com.taomee.bigdata.task.nday.NDaySumCombiner \
	-reducerClass com.taomee.bigdata.task.nday.NDaySumReducer \
	-input ${MONTH_DIR}/$year_month/active-keep-${n}-month/part* \
	-output ${SUM_DIR}/$year_month/active-keep-${n}-month

	#$DB_UPLOAD -type 2 -date ${nday}01 -task 23 -path ${SUM_DIR}/$year_month/active-keep-${n}-month/part* 
done
#$DB_UPLOAD -type 2 -date ${year_month}01 -task 145 -path ${SUM_DIR}/$year_month/active-keep-1-month/part* 
