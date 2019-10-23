WORKDIR=`dirname $0`
WORKDIR=`cd $WORKDIR && pwd`
cd $WORKDIR
echo workdir = $WORKDIR

source config.sh

date=$1

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D "header=/datamining/seerv2/header.arff" \
	-D "classifier.output.path=/datamining/seerv2/classifier/s2" \
        -conf ${HADOOP_CONF} \
        -jobName "classify step1" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput /bigdata/output/day/$date/seerv2/lost-dataset/part*,com.taomee.bigdata.datamining.seerV2.ClassifyDatasetMapper \
        -reducerClass com.taomee.bigdata.datamining.seerV2.ClassifyDatasetReducer \
	-addMos "kl,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "ll,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "kk,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "lk,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
        -output /datamining/classify/$date/s1

exit
${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D "header=/datamining/seerv3/header.arff" \
	-D "classifier.output.path=/datamining/seerv2/classifier/s4" \
        -conf ${HADOOP_CONF} \
        -jobName "classify step2" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput /datamining/classify/$date/s1/k*,com.taomee.bigdata.datamining.seerV2.ClassifyDatasetMapper \
        -reducerClass com.taomee.bigdata.datamining.seerV2.ClassifyDatasetReducer \
	-addMos "k,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
	-addMos "l,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
        -output /datamining/classify/$date/s2
