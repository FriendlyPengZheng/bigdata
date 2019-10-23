<?php
require_once("Mysql.class.php");
if(count($argv) == 1) {
	echo "no time\n";
	exit;
}
$time=$argv[1];
$mysql = new Mysql("192.168.71.76", "tdconfig", "tdconfig@mysql", "db_td_config");
$sql = "select game_id from t_whale_user_month where time = $time and game_id != 0 group by game_id";
$mysql->query($sql);
$index++;
while(($row = $mysql->get_next_row()) !== false) {
	$a_gp[$index]['game'] = $row['game_id'];
	$index++;
}
if(count($a_gp) == 0) {
	echo "none\n";
	exit;
}
foreach($a_gp as $gp) {
	var_dump($gp);
	$total_amt = getPayAmt($mysql, $gp, $time);
	echo $total_amt . "\n";
	$sql = "update t_whale_user_month set total_ratio_by_platform=total_payments*100/$total_amt where game_id = {$gp['game']};";
	$mysql->query($sql);
}

function getPayAmt($mysql, $gp, $time) {
	$sql = "select gpzs_id from t_gpzs_info where game_id = {$gp['game']} and platform_id = -1 and zone_id = -1 and server_id = -1";
	$mysql->query($sql);
	if(($row = $mysql->get_next_row()) !== false) {
		$gpzs = $row['gpzs_id'];
	} else {
		return 0;
	}
	$sql = "select data_id,floor((sthash%10000)/100) as d, sthash%100 as t from t_data_info where range = '_acpay_' and type = 'result' and r_id = (select result_id from t_common_result where task_id = 71 and game_id = {$gp['game']})";
	$mysql->query($sql);
	if(($row = $mysql->get_next_row()) !== false) {
		$data_id = $row['data_id'];
		$d = $row['d'];
		$t = $row['t'];
	} else {
		return 0;
	}
	$sql = "select value from db_td_data_$d.t_db_data_day_$t where data_id = $data_id and time = $time and gpzs_id = $gpzs";
	$mysql->query($sql);
	if(($row = $mysql->get_next_row()) !== false) {
		return $row['value'];
	} else {
		return 0;
	}
}
?>
