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

for type in main nb
do
	uninputs=""
	${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/get${type}tsk-*
	if [[ $? -eq 0 ]]; then
		uninputs="$uninputs -addInput ${DAY_DIR}/$date/basic/get${type}tsk-*,com.taomee.bigdata.assignments.SourceUndoneTskMapper "
	fi
	${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/done${type}tsk-*
	if [[ $? -eq 0 ]]; then
		uninputs="$uninputs -addInput ${DAY_DIR}/$date/basic/done${type}tsk-*,com.taomee.bigdata.assignments.SourceUndoneTskMapper "
	fi
	${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/abrt${type}tsk-*
	if [[ $? -eq 0 ]]; then
		uninputs="$uninputs -addInput ${DAY_DIR}/$date/basic/abrt${type}tsk-*,com.taomee.bigdata.assignments.SourceUndoneTskMapper "
	fi
	${HADOOP_PATH}hadoop fs -ls ${ALL_DIR}/$yesterday/undone${type}/part-*
	if [[ $? -eq 0 ]]; then
		uninputs="$uninputs -addInput ${ALL_DIR}/$yesterday/undone${type}/part-*,com.taomee.bigdata.assignments.UndoneTskMapper "
	fi
	${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
		com.taomee.bigdata.driver.MultipleInputsJobDriver \
		-D mapred.reduce.tasks=4 \
		-conf ${HADOOP_CONF} \
		-jobName "Undone Assignments $date" \
		-outKey org.apache.hadoop.io.Text \
		-outValue org.apache.hadoop.io.Text \
		-inFormat org.apache.hadoop.mapred.TextInputFormat \
		-outFormat org.apache.hadoop.mapred.TextOutputFormat \
		$uninputs \
		-reducerClass com.taomee.bigdata.assignments.UndoneTskReducer \
		-output ${ALL_DIR}/$date/undone$type
done
