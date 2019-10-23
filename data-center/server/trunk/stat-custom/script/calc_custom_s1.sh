export LANG=en_US.UTF-8
WORKDIR=`dirname $0`
echo $0
echo $WORKDIR
WORKDIR=`cd $WORKDIR && pwd`
cd $WORKDIR
source config.sh

CUSTOM_OUTPUT='/bigdata/custom'
game=""
stid=""
sstid=""
op_fields=""
range=""
days=""
inputs="";
operand="";
for param in $*
do
    eval $(echo "$param" | awk -F "=" '{print "key="$1";value="$2 }')
    if [ $key"x" = 'gamex' ]; then
        game=$value;
    elif [ $key"x" = 'stidx' ]; then
        stid=$value;
    elif [ $key"x" = 'sstidx' ]; then
        sstid=$value;
    elif [ $key"x" = 'op_fieldsx' ]; then
        op_fields=$value;
    elif [ $key"x" = 'rangex' ]; then
        range=$value;
    elif [ $key"x" = 'daysx' ]; then
        days=$value;
    elif [ $key"x" = 'operandx' ]; then
        operand=$value;
    fi
done

OLDIFS=$IFS
IFS=','
for day in $days
do
    path="${RAW_DIR}/$day/$game/*custom"
    ${HADOOP_PATH}hadoop fs -ls ${path}
    if [[ $? -eq 0 ]]; then
        inputs="$inputs -addInput ${path},com.taomee.bigdata.task.query.QuerySourceMapper "
    fi
done
IFS=$OLDIFS

if [[ $inputs == "" ]]; then
    echo "empty inputs"
    exit 1  #没有输入
fi

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
        -D gid="${game}" \
        -D stid="${stid}" \
        -D sstid="${sstid}" \
        -D op_field="${op_fields}" \
        -D range="${range}" \
        -conf ${HADOOP_CONF} \
        -jobName custom $operand \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.NullWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        $inputs \
        -reducerClass com.taomee.bigdata.task.query.QuerySourceReducer \
        -output ${CUSTOM_OUTPUT}/${operand}

if [[ $? -eq 0 ]]; then
    exit 0
else
    ${HADOOP_PATH}hadoop fs -rmr -skipTrash ${CUSTOM_OUTPUT}/${operand}
    exit 2  #计算出错
fi
