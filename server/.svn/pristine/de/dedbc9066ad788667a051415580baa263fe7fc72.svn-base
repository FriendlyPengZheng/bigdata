source config.sh
MYSQL="mysql -ustat -pdb_pwdstat_csdf -h192.168.71.45 --port=3306"
HADOOP="/opt/taomee/hadoop/hadoop/bin/hadoop"
date=$1
#活跃
#$MYSQL -D db_stat_default_data -e "select 'UCOUNT','14',zone_id,server_id,'-1','_lgac_','_lgac_',ucount_value from t_default_source_41 where event_id = 402653441 and time=$date" | grep -v zone_id > $date
#新增角色
#$MYSQL -D db_stat_default_data -e "select 'UCOUNT','14',zone_id,server_id,'-1','_newac_','_newac_',ucount_value from t_default_source_43 where event_id = 402653443 and time=$date" | grep -v zone_id > $date
#$HADOOP fs -rm /bigdata/tmp/$date
#$HADOOP fs -copyFromLocal $date /bigdata/tmp/
#$DB_UPLOAD -type 2 -date $date -path /bigdata/tmp/$date
#次日留存
#nday=`date -d "$date +1 day" +%Y%m%d`
#$MYSQL -D db_stat_default_data -e "select '14',zone_id,server_id,'-1','1',ucount_value from t_default_task_3 where task_id = 3 and time=$nday" | grep -v zone_id > $date
#+2日留存
#nday=`date -d "$date +2 day" +%Y%m%d`
#$MYSQL -D db_stat_default_data -e "select '14',zone_id,server_id,'-1','2',ucount_value from t_default_task_9 where task_id = 9 and time=$nday" | grep -v zone_id > $date
#$HADOOP fs -rm /bigdata/tmp/$date
#$HADOOP fs -copyFromLocal $date /bigdata/tmp/
#$DB_UPLOAD -type 2 -date $date -task 21 -path /bigdata/tmp/$date
#月活跃
#year_month=`date -d "$date" +%Y%m`
#$MYSQL -D db_stat_default_data -e "select '14',zone_id,server_id,'-1',ucount_value from t_default_task_12 where task_id = 12 and time=$date" | grep -v zone_id > $date
#$HADOOP fs -rm /bigdata/tmp/$date
#$HADOOP fs -copyFromLocal $date /bigdata/tmp/
#$DB_UPLOAD -type 2 -date ${year_month}01 -task 11 -path /bigdata/tmp/$date
#PCU
$DB_UPLOAD -type 2 -date $date -task 92 -path  /bigdata/tmp/$date
