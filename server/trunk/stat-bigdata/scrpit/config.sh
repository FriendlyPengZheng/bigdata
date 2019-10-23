WORKDIR=`dirname "$0"`
WORKDIR=`cd "$WORKDIR"; pwd`

echo workdir = ${WORKDIR}
cd ${WORKDIR}

# pig执行文件路径
PIG_PATH=
# pig日志目录
PIG_LOG_DIR=${WORKDIR}/log
# pig运行模式 local|mapreduce
PIG_RUN_MODE=local

MYSQL_HOST=192.168.71.68
MYSQL_USER=srvmgr
MYSQL_PASSWD=srvmgr@pwd
MYSQL_PORT=3306
MYSQL_DBNAME=db_stat_config

MYSQL="mysql -h$MYSQL_HOST -u$MYSQL_USER
-p$MYSQL_PASSWD -P$MYSQL_PORT --database=$MYSQL_DBNAME"

HADOOP_PATH=/opt/taomee/hadoop/hadoop/bin/
HADOOP_JAR_PATH=${WORKDIR}/stat-bigdata.jar
HADOOP_CONF=${WORKDIR}/hadoop-cluster.xml

BASEDIR=/bigdata/output

##########################################################

PIG="${PIG_PATH}pig -x ${PIG_RUN_MODE} -l ${PIG_LOG_DIR}"
# MYSQL_UPLOAD table_name fields file_path
MYSQL_UPLOAD="${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} com.taomee.bigdata.driver.MysqlUpload -conf ${HADOOP_CONF}"
DB_UPLOAD="java -cp ${HADOOP_JAR_PATH}:`${HADOOP_PATH}hadoop classpath` com.taomee.bigdata.client.HdfsDataFetchJob -uri hdfs://192.168.11.128:8020/ -Xmx1024m"
MONITOR="${HADOOP_PATH}hadoop jar ${HADOOP_JAR_PATH} com.taomee.bigdata.monitor.util.MonitorRunner -conf ${HADOOP_CONF}"

RAW_DIR=/bigdata/input
ALL_DIR=${BASEDIR}/all
DAY_DIR=${BASEDIR}/day
WEEK_DIR=${BASEDIR}/week
MONTH_DIR=${BASEDIR}/month
TMP_DIR=${BASEDIR}/tmp
SUM_DIR=${BASEDIR}/sum
BACKUP_DIR=${BASEDIR}/backup

##########################################################

PHONE="15821994882,13774451574"
