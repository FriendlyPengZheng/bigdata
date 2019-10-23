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

type=$2
if [[ $type == "" ]]; then
    echo type is empty, default is test
    type=test
fi

time=`date -d "$date" +%s`
input=""
if [[ $type == 'train' ]]; then
    #前8周活跃和付费情况
    for n in {8..35}
    do
	nday=`date -d "$date -$n day" +%Y%m%d`
	input="${input} -addInput ${DAY_DIR}/$nday/basic/logoutG2-*,com.taomee.bigdata.datamining.seerV2.LogoutMapper"
    done

    #累积付费情况
    nday=`date -d "$date -7 day" +%Y%m%d`
    input="${input} -addInput ${ALL_DIR}/$nday/pay-all/allpayG2-*,com.taomee.bigdata.datamining.seerV2.PayallMapper"
    #后一周留存情况
    nday=`date -d "$date -7 day" +%Y%m%d`
    input="${input} -addInput ${WEEK_VERSION_DIR}/$nday/account/part*,com.taomee.bigdata.datamining.seerV2.RangeKeepMapper"
    #后两周留存情况
    input="${input} -addInput ${WEEK_VERSION_DIR}/$date/account/part*,com.taomee.bigdata.datamining.seerV2.RangeKeepLabelMapper"
else
    #前8周活跃和付费情况
    for n in {1..28}
    do
	nday=`date -d "$date -$n day" +%Y%m%d`
	input="${input} -addInput ${DAY_DIR}/$nday/basic/logoutG2-*,com.taomee.bigdata.datamining.seerV2.LogoutMapper"
    done

    #累积付费情况
    nday=`date -d "$date -1 day" +%Y%m%d`
    input="${input} -addInput ${ALL_DIR}/$nday/pay-all/allpayG2-*,com.taomee.bigdata.datamining.seerV2.PayallMapper"
fi

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D "firstday=$time" \
	-D "n=28" \
	-D "type=$type" \
	-D mapred.reduce.tasks=30 \
        -conf ${HADOOP_CONF} \
        -jobName "Lost Data SeerV2 Train Set $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	$input \
        -reducerClass com.taomee.bigdata.datamining.seerV2.TrainSetReducer \
	-addMos "first,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
	-addMos "second,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.NullWritable" \
        -output ${DAY_DIR}/$date/seerv2/lost-dataset
