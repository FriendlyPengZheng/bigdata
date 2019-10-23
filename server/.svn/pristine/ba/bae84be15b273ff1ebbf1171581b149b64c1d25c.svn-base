export LANG=en_US.UTF-8
WORKDIR=`dirname $0`
WORKDIR=`cd $WORKDIR && pwd`
cd $WORKDIR
source config.sh

CUSTOM_OUTPUT='/bigdata/custom'
game=""
operands="";
operation="";
fileid="";
for param in $*
do
    eval $(echo "$param" | awk -F "=" '{print "key="$1";value="$2 }')
    if [ $key"x" = 'gamex' ]; then
        game=$value;
    elif [ $key"x" = 'operandsx' ]; then
        operands=$value;
    elif [ $key"x" = 'operationx' ]; then
        operation=$value;
    elif [ $key"x" = 'fileidx' ]; then
        fileid=$value;
    fi
done

if [[ $fileid"x" == "x" ]]; then
    echo "no fileid"
    exit 3
fi
if [[ $game"x" == "x" ]]; then
    echo "no game"
    exit 3
fi
if [[ $operands"x" == "x" ]]; then
    echo "no operands"
    exit 3
fi
if [[ $operation"x" == "x" ]]; then
    echo "no operation"
    exit 3
fi

inputs=""
userinputs=""
ncount=0
OLDIFS=$IFS
IFS=','
#根据运算符  给不同的mapper
if [[ $operation == "intersect" ]]; then    #交
    for operand in $operands
    do
        path="${CUSTOM_OUTPUT}/${operand}/part-*"
        ${HADOOP_PATH}hadoop fs -ls ${path}
        if [[ $? -eq 0 ]]; then
            ncount=`expr 1 + $ncount `
            userinputs="$userinputs -addInput ${path},com.taomee.bigdata.task.query.QueryANDMapper "
        fi
    done
elif [[ $operation == "union" ]]; then  #并
    for operand in $operands
    do
        path="${CUSTOM_OUTPUT}/${operand}/part-*"
        ${HADOOP_PATH}hadoop fs -ls ${path}
        if [[ $? -eq 0 ]]; then
            userinputs="$userinputs -addInput ${path},com.taomee.bigdata.task.query.QueryORMapper "
        fi
    done
elif [[ $operation == "setdiff" ]]; then  #补
    for operand in $operands
    do
        path="${CUSTOM_OUTPUT}/${operand}/part-*"
        ${HADOOP_PATH}hadoop fs -ls ${path}
        if [[ $? -eq 0 ]]; then
            ncount=`expr 1 + $ncount `
            if [[ $ncount -eq 1 ]]; then
                userinputs="$userinputs -addInput ${path},com.taomee.bigdata.task.query.QueryDIF1Mapper "
            elif [[ $ncount -eq 2 ]]; then
                userinputs="$userinputs -addInput ${path},com.taomee.bigdata.task.query.QueryDIF1Mapper "
            else
                echo "more than 2 operands in setdiff"
                exit 3
            fi
        fi
    done
    ncount=1
else
    echo "operation error $operation"
    exit 3
fi
IFS=$OLDIFS

echo $userinputs
echo $ncount
exit 0

${HADOOP_PATH}hadoop fs -ls ${MONTH_DIR}/$year_month/topk/sumpay/part*
if [[ $? -eq 0 ]]; then
        inputs="$inputs -addInput ${MONTH_DIR}/$year_month/topk/sumpay/part*,com.taomee.bigdata.task.query.QueryTopkSumpayMapper "
fi
${HADOOP_PATH}hadoop fs -ls ${ALL_DIR}/$date/account-all/part-*
if [[ $? -eq 0 ]]; then
        inputs="$inputs -addInput ${ALL_DIR}/$date/account-all/part-*,com.taomee.bigdata.task.topk.TopK_Firstlog_Mapper "
fi
${HADOOP_PATH}hadoop fs -ls ${ALL_DIR}/$date/level/part-*
if [[ $? -eq 0 ]]; then
        inputs="$inputs -addInput ${ALL_DIR}/$date/level/part-*,com.taomee.bigdata.task.topk.TopK_Level_Mapper "
fi
${HADOOP_PATH}hadoop fs -ls ${MONTH_DIR}/$year_month/topk/sumpay/part-*
if [[ $? -eq 0 ]]; then
        inputs="$inputs -addInput ${MONTH_DIR}/$year_month/topk/sumpay/part-*,com.taomee.bigdata.task.topk.TopK_Numpay_Mapper "
fi
${HADOOP_PATH}hadoop fs -ls ${ALL_DIR}/$date/pay-all/allpay-*
if [[ $? -eq 0 ]]; then
        inputs="$inputs -addInput ${ALL_DIR}/$date/pay-all/allpay-*,com.taomee.bigdata.task.topk.TopK_itemOrvip_Mapper "
fi
${HADOOP_PATH}hadoop fs -ls ${ALL_DIR}/$date/coins/part-*
if [[ $? -eq 0 ]]; then
        inputs="$inputs -addInput ${ALL_DIR}/$date/coins/part-*,com.taomee.bigdata.task.topk.TopK_Coinstock_Mapper "
fi
${HADOOP_PATH}hadoop fs -ls ${ALL_DIR}/$date/coinsuse/part-*
if [[ $? -eq 0 ]]; then
        inputs="$inputs -addInput ${ALL_DIR}/$date/coinsuse/part-*,com.taomee.bigdata.task.topk.TopK_Coinsuse_Mapper "
fi
${HADOOP_PATH}hadoop fs -ls ${ALL_DIR}/$date/account-all/activeDay-*
if [[ $? -eq 0 ]]; then
        inputs="$inputs -addInput ${ALL_DIR}/$date/account-all/activeDay-*,com.taomee.bigdata.task.topk.TopK_Activeday_Mapper "
fi
${HADOOP_PATH}hadoop fs -ls ${ALL_DIR}/$date/dayvip/part*
if [[ $? -eq 0 ]]; then
        inputs="$inputs -addInput ${ALL_DIR}/$date/dayvip/part*,com.taomee.bigdata.task.topk.TopK_ifvip_Mapper "
fi
if [[ $inputs == "" ]]; then
    echo "empty inputs"
    exit 1  #没有输入
fi

date=`date -d 'yesterday' +%Y%m%d`
year_month=`date -d "$date" +%Y%m`

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
        -D ncount=${ncount} \
        -conf ${HADOOP_CONF} \
        -jobName custom $fileid \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        $inputs \
        $userinputs \
        -reducerClass com.taomee.bigdata.task.query.QueryReducer \
        -output ${CUSTOM_OUTPUT}/${fileid}

if [[ $? -eq 0 ]]; then
    exit 0
else
    ${HADOOP_PATH}hadoop fs -rmr -skipTrash ${CUSTOM_OUTPUT}/${fileid}
    exit 2  #计算出错
fi
