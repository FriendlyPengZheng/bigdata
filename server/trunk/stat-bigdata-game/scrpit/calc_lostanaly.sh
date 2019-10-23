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

activebasic=170
newbasic=210
payerbasic=250

for t in active new payer
do
	bid=${t}basic
	bid=${!bid}
	b=0
	#7日，14日
	for n in 7 14
	do
		inputs=""
		nday=`date -d "$date -${n} day" +%Y%m%d`
		path=${DAY_DIR}/$nday/level/part*
		${HADOOP_PATH}hadoop fs -ls $path
		if [[ $? -eq 0 ]]; then
			inputs="$inputs -addInput $path,com.taomee.bigdata.task.lost.LostLevelMapper "
		fi
		path=${ALL_DIR}/$nday/undonemain/part*
		${HADOOP_PATH}hadoop fs -ls $path
		if [[ $? -eq 0 ]]; then
			inputs="$inputs -addInput $path,com.taomee.bigdata.task.lost.LostUndoMainMapper "
		fi
		path=${ALL_DIR}/$nday/undonenb/part*
		${HADOOP_PATH}hadoop fs -ls $path
		if [[ $? -eq 0 ]]; then
			inputs="$inputs -addInput $path,com.taomee.bigdata.task.lost.LostUndoNbMapper "
		fi
		path=${ALL_DIR}/$nday/account-all/active*
		${HADOOP_PATH}hadoop fs -ls $path
		if [[ $? -eq 0 ]]; then
			inputs="$inputs -addInput $path,com.taomee.bigdata.task.lost.LostPdayMapper "
		fi
		path=${ALL_DIR}/$nday/pay-all/allpay*
		${HADOOP_PATH}hadoop fs -ls $path
		if [[ $? -eq 0 ]]; then
			inputs="$inputs -addInput $path,com.taomee.bigdata.task.lost.LostPayAnalyMapper "
		fi
		${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
			com.taomee.bigdata.driver.MultipleInputsJobDriver \
			-conf ${HADOOP_CONF} \
			-jobName "$t Lost Analysis $n day $date" \
			-gameInfo ${GAMEINFO} \
			-outKey org.apache.hadoop.io.Text \
			-outValue org.apache.hadoop.io.Text \
			-inFormat org.apache.hadoop.mapred.TextInputFormat \
			-outFormat org.apache.hadoop.mapred.TextOutputFormat \
			-addInput ${DAY_DIR}/$date/$t-lost-$n/part*,com.taomee.bigdata.task.lost.LostAnalyMapper \
			$inputs \
			-reducerClass com.taomee.bigdata.task.lost.LostAnalyReducer \
			-output ${DAY_DIR}/$date/lostanaly-$t-$n

		${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
			com.taomee.bigdata.driver.SimpleJobDriver \
			-D mapred.reduce.tasks=2 \
			-conf ${HADOOP_CONF} \
			-D sumdistr=0,100,500,1000,1100,1500,2000,3000,4000,5000,6000,7000,8000,9000,10000,10100,12000,12100,15000,20000,30000,50000,100000 \
			-D cntdistr=0,1,2,3,4,5,6,11,21,31,41,51 \
			-D daydistr=2,4,8,15,31,91,181,366 \
			-jobName "$t Lost Analysis $n day Sum $date" \
			-gameInfo ${GAMEINFO} \
			-outKey org.apache.hadoop.io.Text \
			-outValue org.apache.hadoop.io.IntWritable \
			-inFormat org.apache.hadoop.mapred.TextInputFormat \
			-outFormat org.apache.hadoop.mapred.TextOutputFormat \
			-mapperClass  com.taomee.bigdata.task.lost.LostAnalySumMapper \
			-reducerClass com.taomee.bigdata.task.lost.LostAnalySumReducer \
			-input ${DAY_DIR}/$date/lostanaly-$t-$n/part* \
			-addMos "level,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
			-addMos "undomain,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
			-addMos "undonb,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
			-addMos "pday,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
			-addMos "psum,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
			-addMos "pcnt,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
			-output ${SUM_DIR}/$date/lostanaly-$t-$n

		#$DB_UPLOAD -type 2 -date $date -task `expr $bid + $b + 0` -path ${SUM_DIR}/$date/lostanaly-$t-$n/level*
		#$DB_UPLOAD -type 2 -date $date -task `expr $bid + $b + 1` -path ${SUM_DIR}/$date/lostanaly-$t-$n/pday*
		#$DB_UPLOAD -type 2 -date $date -task `expr $bid + $b + 2` -path ${SUM_DIR}/$date/lostanaly-$t-$n/psum*
		#$DB_UPLOAD -type 2 -date $date -task `expr $bid + $b + 3` -path ${SUM_DIR}/$date/lostanaly-$t-$n/pcnt*
		#$DB_UPLOAD -type 2 -date $date -task `expr $bid + $b + 4` -path ${SUM_DIR}/$date/lostanaly-$t-$n/undomain*
		#$DB_UPLOAD -type 2 -date $date -task `expr $bid + $b + 5` -path ${SUM_DIR}/$date/lostanaly-$t-$n/undonb*
		b=`expr $b + 10`
	done

	#周
	b=20
	no_in_week=`date -d "${date}" +%u`
	n=`expr ${no_in_week} - 1`
	this_monday=`date -d "${date} -${n} day" +%Y%m%d`
	last_monday=`date -d "$this_monday -7 day" +%Y%m%d`
	last_sunday=`date -d "$this_monday -1 day" +%Y%m%d`
	inputs=""
	for((i=0;i<=6;i++));
	do
		last_day=`date -d "$last_monday +$i day" +%Y%m%d`
		path=${DAY_DIR}/$last_day/level/part*
		${HADOOP_PATH}hadoop fs -ls $path
		if [[ $? -eq 0 ]]; then
			inputs="$inputs -addInput $path,com.taomee.bigdata.task.lost.LostLevelMapper "
		fi
	done
	path=${ALL_DIR}/$last_sunday/undonemain/part*
	${HADOOP_PATH}hadoop fs -ls $path
	if [[ $? -eq 0 ]]; then
		inputs="$inputs -addInput $path,com.taomee.bigdata.task.lost.LostUndoMainMapper "
	fi
	path=${ALL_DIR}/$last_sunday/undonenb/part*
	${HADOOP_PATH}hadoop fs -ls $path
	if [[ $? -eq 0 ]]; then
		inputs="$inputs -addInput $path,com.taomee.bigdata.task.lost.LostUndoNbMapper "
	fi
	path=${ALL_DIR}/$last_sunday/account-all/active*
	${HADOOP_PATH}hadoop fs -ls $path
	if [[ $? -eq 0 ]]; then
		inputs="$inputs -addInput $path,com.taomee.bigdata.task.lost.LostPdayMapper "
	fi
	path=${ALL_DIR}/$last_sunday/pay-all/allpay*
	${HADOOP_PATH}hadoop fs -ls $path
	if [[ $? -eq 0 ]]; then
		inputs="$inputs -addInput $path,com.taomee.bigdata.task.lost.LostPayAnalyMapper "
	fi
	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
		com.taomee.bigdata.driver.MultipleInputsJobDriver \
		-conf ${HADOOP_CONF} \
		-jobName "$t Lost Analysis Week $date" \
		-gameInfo ${GAMEINFO} \
		-outKey org.apache.hadoop.io.Text \
		-outValue org.apache.hadoop.io.Text \
		-inFormat org.apache.hadoop.mapred.TextInputFormat \
		-outFormat org.apache.hadoop.mapred.TextOutputFormat \
		-addInput ${WEEK_DIR}/$this_monday/$t-lost-week/part*,com.taomee.bigdata.task.lost.LostAnalyMapper \
		$inputs \
		-reducerClass com.taomee.bigdata.task.lost.LostAnalyReducer \
		-output ${WEEK_DIR}/$this_monday/lostanaly-$t-week

	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
		com.taomee.bigdata.driver.SimpleJobDriver \
		-D mapred.reduce.tasks=2 \
		-conf ${HADOOP_CONF} \
		-D sumdistr=0,100,500,1000,1100,1500,2000,3000,4000,5000,6000,7000,8000,9000,10000,10100,12000,12100,15000,20000,30000,50000,100000 \
		-D cntdistr=0,1,2,3,4,5,6,11,21,31,41,51 \
		-D daydistr=2,4,8,15,31,91,181,366 \
		-jobName "$t Lost Analysis Week Sum $date" \
		-gameInfo ${GAMEINFO} \
		-outKey org.apache.hadoop.io.Text \
		-outValue org.apache.hadoop.io.IntWritable \
		-inFormat org.apache.hadoop.mapred.TextInputFormat \
		-outFormat org.apache.hadoop.mapred.TextOutputFormat \
		-mapperClass  com.taomee.bigdata.task.lost.LostAnalySumMapper \
		-reducerClass com.taomee.bigdata.task.lost.LostAnalySumReducer \
		-input ${WEEK_DIR}/$this_monday/lostanaly-$t-week/part* \
		-addMos "level,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
		-addMos "undomain,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
		-addMos "undonb,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
		-addMos "pday,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
		-addMos "psum,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
		-addMos "pcnt,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
		-output ${SUM_DIR}/$this_monday/lostanaly-$t-week

#	$DB_UPLOAD -type 2 -date $this_monday -task `expr $bid + $b + 0` -path ${SUM_DIR}/$this_monday/lostanaly-$t-week/level*
#	$DB_UPLOAD -type 2 -date $this_monday -task `expr $bid + $b + 1` -path ${SUM_DIR}/$this_monday/lostanaly-$t-week/pday*
#	$DB_UPLOAD -type 2 -date $this_monday -task `expr $bid + $b + 2` -path ${SUM_DIR}/$this_monday/lostanaly-$t-week/psum*
#	$DB_UPLOAD -type 2 -date $this_monday -task `expr $bid + $b + 3` -path ${SUM_DIR}/$this_monday/lostanaly-$t-week/pcnt*
#	$DB_UPLOAD -type 2 -date $this_monday -task `expr $bid + $b + 4` -path ${SUM_DIR}/$this_monday/lostanaly-$t-week/undomain*
#	$DB_UPLOAD -type 2 -date $this_monday -task `expr $bid + $b + 5` -path ${SUM_DIR}/$this_monday/lostanaly-$t-week/undonb*

	#月
	b=30
	year_month=`date -d "$date" +%Y%m`
	last_month=`date -d "${year_month}01 -1 day" +%Y%m%d`
	last_year_month=`date -d "$last_month" +%Y%m`
	inputs=""
	path=${DAY_DIR}/${last_year_month}*/level/part*
	${HADOOP_PATH}hadoop fs -ls $path
	if [[ $? -eq 0 ]]; then
		inputs="$inputs -addInput $path,com.taomee.bigdata.task.lost.LostLevelMapper "
	fi
	path=${ALL_DIR}/$last_month/undonemain/part*
	${HADOOP_PATH}hadoop fs -ls $path
	if [[ $? -eq 0 ]]; then
		inputs="$inputs -addInput $path,com.taomee.bigdata.task.lost.LostUndoMainMapper "
	fi
	path=${ALL_DIR}/$last_month/undonenb/part*
	${HADOOP_PATH}hadoop fs -ls $path
	if [[ $? -eq 0 ]]; then
		inputs="$inputs -addInput $path,com.taomee.bigdata.task.lost.LostUndoNbMapper "
	fi
	path=${ALL_DIR}/$last_month/account-all/active*
	${HADOOP_PATH}hadoop fs -ls $path
	if [[ $? -eq 0 ]]; then
		inputs="$inputs -addInput $path,com.taomee.bigdata.task.lost.LostPdayMapper "
	fi
	path=${ALL_DIR}/$last_month/pay-all/allpay*
	${HADOOP_PATH}hadoop fs -ls $path
	if [[ $? -eq 0 ]]; then
		inputs="$inputs -addInput $path,com.taomee.bigdata.task.lost.LostPayAnalyMapper "
	fi
	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
		com.taomee.bigdata.driver.MultipleInputsJobDriver \
		-conf ${HADOOP_CONF} \
		-jobName "$t Lost Analysis Month $date" \
		-gameInfo ${GAMEINFO} \
		-outKey org.apache.hadoop.io.Text \
		-outValue org.apache.hadoop.io.Text \
		-inFormat org.apache.hadoop.mapred.TextInputFormat \
		-outFormat org.apache.hadoop.mapred.TextOutputFormat \
		-addInput ${MONTH_DIR}/$year_month/$t-lost-month/part*,com.taomee.bigdata.task.lost.LostAnalyMapper \
		$inputs \
		-reducerClass com.taomee.bigdata.task.lost.LostAnalyReducer \
		-output ${MONTH_DIR}/$year_month/lostanaly-$t-month

	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
		com.taomee.bigdata.driver.SimpleJobDriver \
		-D mapred.reduce.tasks=4 \
		-conf ${HADOOP_CONF} \
		-D sumdistr=0,100,500,1000,1100,1500,2000,3000,4000,5000,6000,7000,8000,9000,10000,10100,12000,12100,15000,20000,30000,50000,100000 \
		-D cntdistr=0,1,2,3,4,5,6,11,21,31,41,51 \
		-D daydistr=2,4,8,15,31,91,181,366 \
		-jobName "$t Lost Analysis Month Sum $date" \
		-gameInfo ${GAMEINFO} \
		-outKey org.apache.hadoop.io.Text \
		-outValue org.apache.hadoop.io.IntWritable \
		-inFormat org.apache.hadoop.mapred.TextInputFormat \
		-outFormat org.apache.hadoop.mapred.TextOutputFormat \
		-mapperClass  com.taomee.bigdata.task.lost.LostAnalySumMapper \
		-reducerClass com.taomee.bigdata.task.lost.LostAnalySumReducer \
		-input ${MONTH_DIR}/$year_month/lostanaly-$t-month/part* \
		-addMos "level,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
		-addMos "undomain,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
		-addMos "undonb,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
		-addMos "pday,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
		-addMos "psum,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
		-addMos "pcnt,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.IntWritable" \
		-output ${SUM_DIR}/$year_month/lostanaly-$t-month

#	$DB_UPLOAD -type 2 -date ${year_month}01 -task `expr $bid + $b + 0` -path ${SUM_DIR}/$year_month/lostanaly-$t-month/level*
#	$DB_UPLOAD -type 2 -date ${year_month}01 -task `expr $bid + $b + 1` -path ${SUM_DIR}/$year_month/lostanaly-$t-month/pday*
#	$DB_UPLOAD -type 2 -date ${year_month}01 -task `expr $bid + $b + 2` -path ${SUM_DIR}/$year_month/lostanaly-$t-month/psum*
#	$DB_UPLOAD -type 2 -date ${year_month}01 -task `expr $bid + $b + 3` -path ${SUM_DIR}/$year_month/lostanaly-$t-month/pcnt*
#	$DB_UPLOAD -type 2 -date ${year_month}01 -task `expr $bid + $b + 4` -path ${SUM_DIR}/$year_month/lostanaly-$t-month/undomain*
#	$DB_UPLOAD -type 2 -date ${year_month}01 -task `expr $bid + $b + 5` -path ${SUM_DIR}/$year_month/lostanaly-$t-month/undonb*
done
