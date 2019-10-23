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

#games=`${HADOOP_PATH}hadoop fs -ls /bigdata/input/$date | grep input | awk -F '/' '{print $5}'`

#for game in $games
for game in {0..9}
do
	echo "calc $game*"
		#-D mapred.output.compress=true \
		#-D mapred.output.compression.codec=org.apache.hadoop.io.compress.BZip2Codec \
	${HADOOP_PATH}hadoop fs -ls ${RAW_DIR}/$date/$game*/*_custom
	if [[ $? -ne 0 ]]; then
		continue
	fi
	${HADOOP_PATH}hadoop jar basic-check.jar \
		com.taomee.bigdata.driver.SimpleJobDriver \
		-D divide=$divide \
		-D mapred.reduce.tasks=30 \
		-conf ${HADOOP_CONF} \
		-jobName "Custom game $game $date" \
		-gameInfo ${GAMEINFO} \
		-outKey org.apache.hadoop.io.Text \
		-outValue org.apache.hadoop.io.Text \
		-inFormat org.apache.hadoop.mapred.TextInputFormat \
		-outFormat org.apache.hadoop.mapred.TextOutputFormat \
		-mapperClass  com.taomee.bigdata.basic.CustomBasicMapper \
		-combinerClass  com.taomee.bigdata.basic.BasicCombiner \
		-reducerClass com.taomee.bigdata.basic.BasicReducer \
		-input ${RAW_DIR}/$date/$game*/*_custom \
		-output ${DAY_DIR}/$date/custom \
		-addMos "UCOUNT,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
		-addMos "COUNT,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
		-addMos "SUM,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
		-addMos "MAX,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
		-addMos "SET,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
		-addMos "DISTRSUM,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
		-addMos "DISTRMAX,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
		-addMos "DISTRSET,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
		-addMos "IPDISTR,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \

	${HADOOP_PATH}hadoop fs -rm -skipTrash ${DAY_DIR}/$date/custom/part-* &
	pids=""
	for type in count ucount max set sum
	do
		sh calc_${type}_custom.sh $date > $WORKDIR/log/$date/calc_${type}_custom.log 2>&1 &
		pids="$pids $!"
	done
	for type in max set sum
	do
		sh calc_distr_custom.sh $date $type > $WORKDIR/log/$date/calc_distr_${type}_custom.log 2>&1 &
		pids="$pids $!"
	done
	for p in $pids
	do
		wait $p
		echo $p
	done
done
