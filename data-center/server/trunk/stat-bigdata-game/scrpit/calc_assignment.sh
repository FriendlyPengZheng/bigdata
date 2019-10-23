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

yesterday=`date -d "$date -1 day" +%Y%m%d`

inputs=""
#新手任务
${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/getnbtsk*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${DAY_DIR}/$date/basic/getnbtsk*,com.taomee.bigdata.assignments.GetNbTskMapper "
fi
${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/donenbtsk*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${DAY_DIR}/$date/basic/donenbtsk*,com.taomee.bigdata.assignments.DoneNbTskMapper "
fi
${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/abrtnbtsk*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${DAY_DIR}/$date/basic/abrtnbtsk*,com.taomee.bigdata.assignments.AbrtNbTskMapper "
fi
#主线任务
${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/getmaintsk*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${DAY_DIR}/$date/basic/getmaintsk*,com.taomee.bigdata.assignments.GetMainTskMapper "
fi
${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/donemaintsk*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${DAY_DIR}/$date/basic/donemaintsk*,com.taomee.bigdata.assignments.DoneMainTskMapper "
fi
${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/abrtmaintsk*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${DAY_DIR}/$date/basic/abrtmaintsk*,com.taomee.bigdata.assignments.AbrtMainTskMapper "
fi
#支线任务
${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/getauxtsk*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${DAY_DIR}/$date/basic/getauxtsk*,com.taomee.bigdata.assignments.GetAuxTskMapper "
fi
${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/doneauxtsk*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${DAY_DIR}/$date/basic/doneauxtsk*,com.taomee.bigdata.assignments.DoneAuxTskMapper "
fi
${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/abrtauxtsk*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${DAY_DIR}/$date/basic/abrtauxtsk*,com.taomee.bigdata.assignments.AbrtAuxTskMapper "
fi
#其它任务
${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/getetctsk*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${DAY_DIR}/$date/basic/getetctsk*,com.taomee.bigdata.assignments.GetAuxTskMapper "
fi
${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/doneetctsk*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${DAY_DIR}/$date/basic/doneetctsk*,com.taomee.bigdata.assignments.DoneAuxTskMapper "
fi
${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/abrtetctsk*
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput ${DAY_DIR}/$date/basic/abrtetctsk*,com.taomee.bigdata.assignments.AbrtAuxTskMapper "
fi

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
        -conf ${HADOOP_CONF} \
        -jobName "Assignments $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.NullWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	$inputs \
        -reducerClass com.taomee.bigdata.assignments.AssignReducer \
        -output ${DAY_DIR}/$date/assignments

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=1 \
        -conf ${HADOOP_CONF} \
        -jobName "Assignments Sum $date" \
		-gameInfo ${GAMEINFO} \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass    com.taomee.bigdata.assignments.AssignSumMapper \
        -combinerClass  com.taomee.bigdata.assignments.AssignSumReducer \
        -reducerClass com.taomee.bigdata.assignments.AssignSumReducer \
        -input ${DAY_DIR}/$date/assignments/part* \
        -output ${SUM_DIR}/$date/assignments

#time=`date -d "${date}" +%s`
#$MYSQL_UPLOAD t_gametask_data \
#"type,sstid,game_id,platform_id,zone_id,server_id,getucount,doneucount,abrtucount" \
#        ${SUM_DIR}/$date/assignments/part* \
#        $time
#
#${HADOOP_PATH}hadoop fs -text ${DAY_DIR}/$date/assignments/part* | awk '{print "insert into t_gametask_info set type=\""$1"\",game_id="$3",sstid=\""$2"\",gametask_name=\""$2"\" on duplicate key update gametask_name=gametask_name;"}' | sort | uniq > t_gametask_info_$date.sql
#mysql -utdconfig -ptdconfig@mysql -h192.168.71.76 --default-character-set='utf8' -D db_td_config < t_gametask_info_$date.sql
#rm -f t_gametask_info_$date.sql

for type in nb main
do
	uninputs=""
	${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/get${type}tsk*
	if [[ $? -eq 0 ]]; then
		uninputs="$uninputs -addInput ${DAY_DIR}/$date/basic/get${type}tsk*,com.taomee.bigdata.assignments.SourceUndoneTskMapper "
	fi
	${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/done${type}tsk*
	if [[ $? -eq 0 ]]; then
		uninputs="$uninputs -addInput ${DAY_DIR}/$date/basic/done${type}tsk*,com.taomee.bigdata.assignments.SourceUndoneTskMapper "
	fi
	${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/abrt${type}tsk*
	if [[ $? -eq 0 ]]; then
		uninputs="$uninputs -addInput ${DAY_DIR}/$date/basic/abrt${type}tsk*,com.taomee.bigdata.assignments.SourceUndoneTskMapper "
	fi
	${HADOOP_PATH}hadoop fs -ls ${ALL_DIR}/$yesterday/undone${type}/part*
	if [[ $? -eq 0 ]]; then
		uninputs="$uninputs -addInput ${ALL_DIR}/$yesterday/undone${type}/part*,com.taomee.bigdata.assignments.UndoneTskMapper "
	fi
	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
		com.taomee.bigdata.driver.MultipleInputsJobDriver \
		-D mapred.reduce.tasks=4 \
		-D mapred.output.compress=true \
		-D mapred.output.compression.codec=org.apache.hadoop.io.compress.BZip2Codec \
		-conf ${HADOOP_CONF} \
		-jobName "Undone $type Assignments $date" \
		-gameInfo ${GAMEINFO} \
		-outKey org.apache.hadoop.io.Text \
		-outValue org.apache.hadoop.io.Text \
		-inFormat org.apache.hadoop.mapred.TextInputFormat \
		-outFormat org.apache.hadoop.mapred.TextOutputFormat \
		$uninputs \
		-reducerClass com.taomee.bigdata.assignments.UndoneTskReducer \
		-output ${ALL_DIR}/$date/undone$type
done
