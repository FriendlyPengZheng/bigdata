export LANG=en_US.UTF-8
WORKDIR=`dirname $0`
WORKDIR=`cd $WORKDIR && pwd`
cd $WORKDIR
echo workdir = $WORKDIR

source config.sh

month=$1

if [[ $month == "" ]]; then
	echo invalid param: month
	exit
fi

TOPK_DIR=${MONTH_DIR}/$month/topk/topk_final
time=`date -d ${month}"01" +%s`
$MYSQL_UPLOAD t_whale_user_month \
	"game_id,zone_id,server_id,platform_id,account_id,total_payments" \
	${TOPK_DIR}/amtperiod-* \
	$time
$MYSQL_UPLOAD t_whale_user_month \
	"game_id,zone_id,server_id,platform_id,account_id,total_count" \
	${TOPK_DIR}/numpayperiod-* \
	$time
$MYSQL_UPLOAD t_whale_user_month \
	"game_id,zone_id,server_id,platform_id,account_id,total_ratio" \
	${TOPK_DIR}/percent-* \
	$time
