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

param=""

for i in $*; do
    echo $i
    if [[ $i == "-h" ]]; then
	param="$param -D hour=h"
    elif [[ $i == "-hour" ]]; then
	param="$param -D hour=h"
    elif [[ $i == "-p" ]]; then
	param="$param -D province=p"
    elif [[ $i == "-province" ]]; then
	param="$param -D province=p"
    elif [[ $i == "-c" ]]; then
	param="$param -D city=c"
    elif [[ $i == "-city" ]]; then
	param="$param -D city=c"
    elif [[ $i == "-i" ]]; then
	param="$param -D isp=i"
    elif [[ $i == "-isp" ]]; then
	param="$param -D isp=i"
    fi
done

tasks=`echo $param | awk -F'D' '{print NF==0?2:NF*2}'`

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsBasicDriver \
	$param \
	-D "ip.table=t_city_ip_2015_Q3" \
        -conf ${HADOOP_CONF} \
        -jobName "Register Transfer $date $param" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
	-addInput /ads/account/register/${date}*,com.taomee.bigdata.task.register_transfer.RegisterMapper \
	-addInput /ads/account/login/$date/*,com.taomee.bigdata.task.register_transfer.LoginMapper \
	-addInput /ads/account/role/$date/*,com.taomee.bigdata.task.register_transfer.RoleMapper \
	-addInput /ads/account/online/$date/*,com.taomee.bigdata.task.register_transfer.OnlineMapper \
	-addInput ${DAY_DIR}/$date/basic/lgac*,com.taomee.bigdata.task.register_transfer.ActiveMapper \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -reducerClass com.taomee.bigdata.task.register_transfer.RTBasicReducer \
        -output ${DAY_DIR}/$date/register-transfer


${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsBasicDriver \
	-D mapred.reduce.tasks=${tasks} \
        -conf ${HADOOP_CONF} \
        -jobName "Register Transfer Sum $date $param" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.IntWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${DAY_DIR}/$date/register-transfer/part*,com.taomee.bigdata.task.register_transfer.RTSumMapper \
        -reducerClass com.taomee.bigdata.task.register_transfer.RTSumReducer \
	-addMos "percent,org.apache.hadoop.mapred.TextOutputFormat,org.apache.hadoop.io.Text,org.apache.hadoop.io.Text" \
        -output ${SUM_DIR}/$date/register-transfer

if [[ $tasks -eq 2 ]]; then
    echo dump into mysql
    ${HADOOP_PATH}hadoop fs -cat ${SUM_DIR}/$date/register-transfer/part* 2>/dev/null | \
    awk -v date=$date '{print "insert into t_reg_trans (time, gameid, s1, s2, s3, s4, s5) values ("date","$1","$2","$3","$4","$5","$6") on DUPLICATE KEY UPDATE gameid="$1",s1="$2",s2="$3",s3="$4",s4="$5",s5="$6";"}' | \
    mysql -utdconfig -ptdconfig@mysql -h192.168.71.76 db_td_config
fi
