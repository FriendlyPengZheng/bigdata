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

n=14
nday=`date -d "$date -${n} day" +%Y%m%d`
inputs=""
for((i=0;i<=`expr ${n} - 1`;i++));
do
	last_day=`date -d "$date -$i day" +%Y%m%d`
	inputs="$inputs -addInput ${DAY_DIR}/$last_day/basic/lgac*,com.taomee.bigdata.task.nday.SourceNDay${n}Mapper "
done
#task_id=28,96
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
	com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-conf ${HADOOP_CONF} \
	-D "stid=_lgac_" \
	-D "nday=${n}" \
	-jobName "Active ${n} day Lost $date" \
	-gameInfo ${GAMEINFO} \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.IntWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${DAY_DIR}/$nday/basic/lgac*,com.taomee.bigdata.task.common.SourceActiveMapper \
	$inputs \
	-reducerClass com.taomee.bigdata.task.lost.LostReducer \
	-output ${DAY_DIR}/$date/active-lost-${n}
