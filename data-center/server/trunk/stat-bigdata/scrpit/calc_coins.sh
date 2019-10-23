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

path="${ALL_DIR}/$yesterday/coins/part-*"
${HADOOP_PATH}hadoop fs -ls $path
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput $path,com.taomee.bigdata.task.coins.CoinsMapper "
fi
for p in buyitem getgold usegold
do
	path="${DAY_DIR}/$date/basic/$p-*"
	${HADOOP_PATH}hadoop fs -ls $path
	if [[ $? -eq 0 ]]; then
		inputs="$inputs -addInput $path,com.taomee.bigdata.task.coins.SourceMapper "
	fi
done
if [[ $inputs == "" ]]; then
	echo "empty inputs"
	exit 1
fi

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D mapred.reduce.tasks=4 \
	-D mapred.output.compress=true \
	-D mapred.output.compression.codec=org.apache.hadoop.io.compress.BZip2Codec \
        -conf ${HADOOP_CONF} \
        -jobName "Coins All $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.LongWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	$inputs \
        -reducerClass com.taomee.bigdata.task.coins.CoinsReducer \
	-addMos "userbuy,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.LongWritable" \
	-addMos "useruse,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.LongWritable" \
        -output ${ALL_DIR}/$date/coins

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=3 \
        -conf ${HADOOP_CONF} \
        -jobName "User Buy Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.LongWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass com.taomee.bigdata.task.coins.CoinsBuyMapper \
        -combinerClass com.taomee.bigdata.task.coins.CoinsBuyReducer \
        -reducerClass com.taomee.bigdata.task.coins.CoinsBuyReducer \
        -input ${ALL_DIR}/$date/coins/userbuy-* \
        -output ${SUM_DIR}/$date/coinsbuy

$DB_UPLOAD -type 2 -date $date -task 343 -path ${SUM_DIR}/$date/coinsbuy/part-*

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D mapred.map.tasks=20 \
	-D mapred.reduce.tasks=4 \
        -conf ${HADOOP_CONF} \
        -jobName "Coins Day $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${DAY_DIR}/$date/basic/lgac-*,com.taomee.bigdata.task.pay.SourceActiveMapper \
	-addInput ${ALL_DIR}/$date/coins/part-*,com.taomee.bigdata.task.coins.CoinsActiveMapper \
        -reducerClass com.taomee.bigdata.task.coins.CoinsGrepReducer \
        -output ${DAY_DIR}/$date/coins

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D mapred.reduce.tasks=3 \
        -conf ${HADOOP_CONF} \
        -jobName "User Coins Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.LongWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass com.taomee.bigdata.task.coins.CoinsBuyMapper \
        -combinerClass com.taomee.bigdata.task.coins.CoinsBuyReducer \
        -reducerClass com.taomee.bigdata.task.coins.CoinsBuyReducer \
        -input ${DAY_DIR}/$date/coins/part-* \
        -output ${SUM_DIR}/$date/coinsleft

$DB_UPLOAD -type 2 -date $date -task 346 -path ${SUM_DIR}/$date/coinsleft/part-*

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
	-D "distr=500,1000,2000,3000,4000,5000,6000,7000,8000,9000,10000,15000,20000,30000,50000,100000" \
	-D mapred.reduce.tasks=3 \
        -conf ${HADOOP_CONF} \
        -jobName "Coins Left Distr $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.LongWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.task.coins.CoinsSumMapper \
        -reducerClass com.taomee.bigdata.task.coins.CoinsSumReducer \
        -input ${DAY_DIR}/$date/coins/part-* \
	-addMos "cnt,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.LongWritable" \
        -output ${SUM_DIR}/$date/coins

$DB_UPLOAD -type 2 -date $date -task 344 -path ${SUM_DIR}/$date/coins/part-*
$DB_UPLOAD -type 2 -date $date -task 345 -path ${SUM_DIR}/$date/coins/cnt-*

inputs=""
path="${ALL_DIR}/$yesterday/coinsuse/part-*"
${HADOOP_PATH}hadoop fs -ls $path
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput $path,com.taomee.bigdata.task.coins.CoinsMapper "
fi
path="${ALL_DIR}/$date/coins/useruse-*"
${HADOOP_PATH}hadoop fs -ls $path
if [[ $? -eq 0 ]]; then
	inputs="$inputs -addInput $path,com.taomee.bigdata.task.coins.CoinsMapper "
fi
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D mapred.reduce.tasks=4 \
	-D mapred.output.compress=true \
	-D mapred.output.compression.codec=org.apache.hadoop.io.compress.BZip2Codec \
        -conf ${HADOOP_CONF} \
        -jobName "Coins Use All $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.LongWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	$inputs \
        -reducerClass com.taomee.bigdata.task.coins.CoinsReducer \
        -output ${ALL_DIR}/$date/coinsuse
