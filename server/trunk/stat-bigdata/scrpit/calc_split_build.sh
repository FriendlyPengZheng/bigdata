WORKDIR=`dirname $0`
WORKDIR=`cd $WORKDIR && pwd`
cd $WORKDIR
echo workdir = $WORKDIR

source config.sh

date=$1
n=$2

if [[ $date == "" ]]; then
    echo invalid param: date
    exit
fi
if [[ $n == "" ]]; then
    echo invalid param: n
    exit
fi

CLASSIFIER_PATH='/datamining/seerv2/classifier/s2'
${HADOOP_PATH}hadoop fs -rmr -skipTrash $CLASSIFIER_PATH
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D "split=$n" \
	-D "header=/datamining/seerv2/header.arff" \
	-D "classifier.output.path=$CLASSIFIER_PATH" \
	-D "c=20.5" \
	-D "g=0.2" \
	-D mapred.reduce.tasks=5 \
        -conf ${HADOOP_CONF} \
	-jobName "split and classify first" \
        -outKey org.apache.hadoop.io.IntWritable \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput /bigdata/output/day/$date/seerv2/lost-dataset/part*,com.taomee.bigdata.datamining.seerV2.SplitSetMapper \
        -reducerClass com.taomee.bigdata.datamining.seerV2.SplitClassifyReducer \
        -output /datamining/split/$date/seerv2/s2
	#-addInput ${DAY_DIR}/20150504/lost-dataset/new-r-*,com.taomee.bigdata.datamining.SplitSetMapper \
	#-D "g=0:0.01:0.2" \
exit
CLASSIFIER_PATH='/datamining/seerv2/classifier/s4'
${HADOOP_PATH}hadoop fs -rmr -skipTrash $CLASSIFIER_PATH
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D "split=$n" \
	-D "header=/datamining/seerv3/header.arff" \
	-D "classifier.output.path=$CLASSIFIER_PATH" \
	-D "c=0.1" \
	-D "g=0.2" \
	-D mapred.reduce.tasks=5 \
        -conf ${HADOOP_CONF} \
        -jobName "split and classify second" \
        -outKey org.apache.hadoop.io.IntWritable \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput /bigdata/output/day/$date/seerv2/lost-dataset/second*,com.taomee.bigdata.datamining.seerV2.SplitSetMapper \
        -reducerClass com.taomee.bigdata.datamining.seerV2.SplitClassifyReducer \
        -output /datamining/split/$date/seerv2/s4
