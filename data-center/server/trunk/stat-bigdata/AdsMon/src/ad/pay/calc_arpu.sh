WORKDIR=`dirname $0`
WORKDIR=`cd $WORKDIR && pwd`
cd $WORKDIR
echo workdir = $WORKDIR

date=$1

source comm_conf.sh

if [[ $date == "" ]]; then
    echo invalid param: date
    exit
fi

yesterday=`date -d "$date -1 day" +%Y%m%d`

#留存用户
${HADOOP_JAR} ad.pay.MultipleInputsJobDriver $CONF_PARAM_DC \
	-D mapred.output.compress=true \
	-D mapred.output.compression.codec=org.apache.hadoop.io.compress.GzipCodec \
	-mapOutKey org.apache.hadoop.io.Text \
	-mapOutValue org.apache.hadoop.io.LongWritable \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.NullWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "keeper $date" \
	-addInput ${TMONLINE_PATH}/$date/*,ad.pay.GetOnlineMap \
	-addInput ${TMONLINE_PATH}/$yesterday/*,ad.pay.GetOnlineYesterdayMap \
	-reducerClass ad.pay.KeepUserReducer \
	-output /ads/today/$date/pay_keeper$date

#回流用户
${HADOOP_JAR} ad.pay.MultipleInputsJobDriver $CONF_PARAM_DC \
	-D mapred.output.compress=true \
	-D mapred.output.compression.codec=org.apache.hadoop.io.compress.GzipCodec \
	-mapOutKey org.apache.hadoop.io.Text \
	-mapOutValue org.apache.hadoop.io.LongWritable \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.NullWritable \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "backer $date" \
	-addInput ${TMONLINE_PATH}/$date/*,ad.pay.GetOnlineMap \
	-addInput ${ROLE_PATH}/$date/*,ad.pay.GetRegisterTagMap \
	-addInput /ads/today/$date/pay_keeper$date/part*,ad.pay.UserTagMap \
	-reducerClass ad.pay.BackUserReducer \
	-output /ads/today/$date/pay_backer$date

#per user pay
${HADOOP_JAR} ad.pay.MultipleInputsJobDriver $CONF_PARAM_DC \
	-D mapred.output.compress=true \
	-D mapred.output.compression.codec=org.apache.hadoop.io.compress.GzipCodec \
	-mapOutKey org.apache.hadoop.io.Text \
	-mapOutValue org.apache.hadoop.io.LongWritable \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "per user pay $date" \
	-addInput /ads/boss/*/$date*,ad.pay.GetMBMap \
	-addInput ${ROLE_PATH}/$date/*,ad.pay.GetRegisterMap \
	-addInput /ads/today/$date/pay_keeper$date/part*,ad.pay.KeepMiddleMap \
	-addInput /ads/today/$date/pay_backer$date/part*,ad.pay.BackMiddleMap \
	-reducerClass ad.pay.UserPayReducer \
	-output /ads/today/$date/pay_user$date
	
#game pay rate arpu
${HADOOP_JAR} ad.pay.MultipleInputsJobDriver $CONF_PARAM_DC \
	$CONF_PRDMAP_FILES \
	-D mapred.output.compress=true \
	-D mapred.output.compression.codec=org.apache.hadoop.io.compress.GzipCodec \
	-mapOutKey org.apache.hadoop.io.Text \
	-mapOutValue org.apache.hadoop.io.Text \
	-outKey org.apache.hadoop.io.Text \
	-outValue org.apache.hadoop.io.Text \
	-inFormat org.apache.hadoop.mapred.TextInputFormat \
	-outFormat org.apache.hadoop.mapred.TextOutputFormat \
	-jobName "arpu $date" \
	-addInput /ads/today/$date/pay_user$date/part*,ad.pay.UserPayMap \
	-reducerClass ad.pay.ArpuReducer \
	-output /ads/today/$date/pay_arpu$date

#upload to db
#mysql -h192.168.71.45 -uhadoop -pHA#2jsOw%x -P3306 --default-character-set='utf8'
${HADOOP_JAR} ad.pay.MysqlUpload $CONF_PARAM_DC \
        t_game_arpu \
        "game_id,user_type,pay_rate,arpu" \
        /ads/today/$date/pay_arpu$date/part* \
        $date
