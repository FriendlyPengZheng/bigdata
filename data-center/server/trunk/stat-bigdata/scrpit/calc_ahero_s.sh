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

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.MultipleInputsJobDriver \
	-D mapred.reduce.tasks=0 \
        -conf ${HADOOP_CONF} \
        -jobName "Ahero Source $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.NullWritable \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-addInput ${RAW_DIR}/$date/82/*,com.taomee.bigdata.ahero.AheroMapper \
        -reducerClass com.taomee.bigdata.task.newlog.NewLoginReducer \
        -output ${DAY_DIR}/$date/ahero
${HADOOP_PATH}hadoop fs -rm ${RAW_DIR}/$date/82/*
${HADOOP_PATH}hadoop fs -mv ${DAY_DIR}/$date/ahero/p* ${RAW_DIR}/$date/82/
exit
sh calc_basic.sh $date true > $WORKDIR/log/$date/calc_basic.log 2>&1
for type in count ucount max set sum
do
	sh calc_${type}.sh $date > $WORKDIR/log/$date/calc_${type}.log 2>&1 &
done
for type in max set sum
do
	sh calc_distr.sh $date $type > $WORKDIR/log/$date/calc_distr_${type}.log 2>&1 &
done
for p in {1..8}
do
	wait %$p
done
#TODO 等五个基本项算完 跑calc_ads.sh 再算后面的 
sh merge.sh $date > $WORKDIR/log/$date/merge.log 2>&1
sh calc_ads.sh $date > $WORKDIR/log/$date/calc_ads.log 2>&1
sh calc_tosql.sh $date > $WORKDIR/log/$date/calc_tosql.log 2>&1 &
sh calc_tosql_ads.sh $date > $WORKDIR/log/$date/calc_tosql_ads.log 2>&1 &

#sh calc_activekeep.sh $date > $WORKDIR/log/$date/calc_activekeep.log 2>&1 &

#task 12(先计算新增用户(首次登陆))
sh calc_account_all.sh $date > $WORKDIR/log/$date/calc_account_all.log 2>&1 #等累积算完算新增
#task 13-14,132
sh calc_account_nday.sh $date > $WORKDIR/log/$date/calc_account_nday.log 2>&1 &
#task 7
sh calc_account_new_all.sh $date > $WORKDIR/log/$date/calc_account_new_all.log 2>&1 &
