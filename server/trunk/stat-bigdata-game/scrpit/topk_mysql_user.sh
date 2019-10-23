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

time=0
TOPK_DIR=${MONTH_DIR}/$month/topk/topk_final
$MYSQL_UPLOAD t_whale_user \
	"game_id,zone_id,server_id,platform_id,account_id,ctime" \
	${TOPK_DIR}/firstlogtime-* \
	$time
$MYSQL_UPLOAD t_whale_user \
	"game_id,zone_id,server_id,platform_id,account_id,last_login_time" \
	${TOPK_DIR}/lastlogtime-* \
	$time
$MYSQL_UPLOAD t_whale_user \
	"game_id,zone_id,server_id,platform_id,account_id,current_level" \
	${TOPK_DIR}/level-* \
	$time
$MYSQL_UPLOAD t_whale_user \
	"game_id,zone_id,server_id,platform_id,account_id,vip" \
	${TOPK_DIR}/ifvip-* \
	$time
$MYSQL_UPLOAD t_whale_user \
	"game_id,zone_id,server_id,platform_id,account_id,consume_golds" \
	${TOPK_DIR}/coinsuse-* \
	$time
$MYSQL_UPLOAD t_whale_user \
	"game_id,zone_id,server_id,platform_id,account_id,left_golds" \
	${TOPK_DIR}/coinstock-* \
	$time
$MYSQL_UPLOAD t_whale_user \
	"game_id,zone_id,server_id,platform_id,account_id,first_buyitem_time,last_buyitem_time,buyitem_total_count,buyitem_total_amount" \
	${TOPK_DIR}/buyitems-* \
	$time
$MYSQL_UPLOAD t_whale_user \
	"game_id,zone_id,server_id,platform_id,account_id,first_vip_time,last_vip_time,vip_total_count,vip_total_amount" \
	${TOPK_DIR}/vipmonth-* \
	$time
