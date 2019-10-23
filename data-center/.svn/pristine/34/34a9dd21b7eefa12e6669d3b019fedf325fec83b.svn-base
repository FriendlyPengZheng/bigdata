export LANG=en_US.UTF-8
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

${HADOOP_PATH}hadoop fs -ls ${DAY_DIR}/$date/basic/buyitem-*

if [[ $? -ne 0 ]]; then
	exit;
fi

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
        -conf ${HADOOP_CONF} \
        -jobName "Buy Item $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass  com.taomee.bigdata.items.ItemMapper \
        -combinerClass  com.taomee.bigdata.items.ItemReducer \
        -reducerClass com.taomee.bigdata.items.ItemReducer \
        -input ${DAY_DIR}/$date/basic/buyitem-* \
        -output ${DAY_DIR}/$date/buyitem

${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} \
        com.taomee.bigdata.driver.SimpleJobDriver \
        -conf ${HADOOP_CONF} \
	-D mapred.reduce.tasks=1 \
        -jobName "Buy Item Sum $date" \
        -outKey org.apache.hadoop.io.Text \
        -outValue org.apache.hadoop.io.Text \
        -inFormat org.apache.hadoop.mapred.TextInputFormat \
        -outFormat org.apache.hadoop.mapred.TextOutputFormat \
        -mapperClass com.taomee.bigdata.items.ItemSumMapper \
        -combinerClass com.taomee.bigdata.items.ItemSumReducer \
        -reducerClass com.taomee.bigdata.items.ItemSumReducer \
        -input ${DAY_DIR}/$date/buyitem/part* \
        -output ${SUM_DIR}/$date/buyitem

time=`date -d "${date}" +%s`
$MYSQL_UPLOAD t_item_sale_data \
        "sstid,game_id,platform_id,zone_id,server_id,item_id,vip,salenum,salemoney,buycount,buyucount" \
        ${SUM_DIR}/$date/buyitem/part* \
        $time

#热血道具销售的游戏币数量要*100
mysql -utdconfig -ptdconfig@mysql -h192.168.71.76 -D db_td_config -e "update t_item_sale_data set salemoney = salemoney*100 where game_id = 16 and sstid != '_mibiitem_' and time=$time"
