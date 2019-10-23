<?php
require_once("Mysql.class.php");

# db_td_data_28 | bigdbdata | data@21011420 | 192.168.71.72 |    3306  #sthash = 2895
$time = time();
//$time = 1407665040;
//$time = 1407826320;
$time = $time - $time % 60;
$start = $time - 300;
$end = $time - 240;
$endtime = date("H:i:00", $end);
$ymd = date("Y-m-d", $end);
echo "monitor $ymd $endtime\n";
$mysql = new Mysql("192.168.71.72", "bigdbdata", "data@21011420", "db_td_data_28");

$config=require("game.php");
foreach($config as $gid => $gameinfo) {
	$name = $gameinfo['game_name'];
	$list = $gameinfo['data_list'];
	//总在线
	$info = $list[$gid * 256];
	if(array_key_exists('gpzs_id', $info)) {
		$total = process_info($info);
	} else {
		$total = process_info($info, $list);
	}
	if($total !== false) {
		if(count($total) < 2) {
			if(!file_exists("$name")) {
				send_msg("{$name}在线缺失($endtime)");
				exec("touch \"$name\"");
			}
		} else {
			$percent = round(100-$total[1]['value']/$total[0]['value']*100, 2);
			if(file_exists($name)) {
				if($percent < 0) {
					send_msg("{$name}在线恢复正常");
					exec("rm -f \"$name\"");
				}
				continue;
			}
			if($percent >= 20 || $total[0]['value'] - $total[1]['value'] >= 1000) {
				send_msg("{$name}在线下降至{$total[1]['value']}($percent%)($endtime)");
				exec("touch \"$name\"");
			}
		}
	}
}

function process_info($info, $list = NULL) {
	global $mysql;
	global $start;
	global $end;
	$sql = "select from_unixtime(time) as time, sum(value) as value from t_db_data_minute_95 where time between {$start} and {$end} and ";
	$data = $info['data_id'];
	if(array_key_exists('gpzs_id', $info)) {
		$where .= "(gpzs_id = {$info['gpzs_id']} and data_id = {$info['data_id']}) or ";
	} else {
		if(!is_array($info) || !array_key_exists('data_id', $info))	return false;
		if(is_array($info['data_id'])) {
			foreach($info['data_id'] as $i) {
				//var_dump($list[$i]);
				if(array_key_exists('gpzs_id', $list[$i])) {
					$where .= "(gpzs_id = {$list[$i]['gpzs_id']} and data_id = {$list[$i]['data_id']}) or ";
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}
	$where .= "(1 = 0)";
	$sql .= "($where) group by time;";
	$mysql->query($sql);
	$ret = array();
	while(($row = $mysql->get_next_row()) !== false) {
		$ret[] = $row;
	}
	return $ret;
}

function send_msg($msg) {
	echo $msg."\n";
	//米粒app用utf8，rtx和邮件用gb2312
	$msg_gb2312 = iconv('utf8', 'gb2312', $msg);
	$day_of_week = (int)date("N", time());
	$hour_minute = (int)date("Hi", time());
	exec("export LD_LIBRARY_PATH=./ && ./stat-alarm-sender \"$msg_gb2312\" mail");
	if(is_work_time($day_of_week, $hour_minute)) {//工作时间，发送rtx
		exec("export LD_LIBRARY_PATH=./ && ./stat-alarm-sender \"$msg_gb2312\" rtx");
	} else {
		exec("export LD_LIBRARY_PATH=./ && ./stat-alarm-sender \"$msg\" app");
	}
	//$msg = iconv('utf8', 'gb2312', $msg);
	//exec("export LD_LIBRARY_PATH=./ && ./stat-alarm-sender \"$msg\" app");
}

function is_work_time($day_of_week, $hour_minute) {
	if($day_of_week >= 6)	return false;
	if(0930 <= $hout_minute && $hour_minute <= 1830)	return true;
	return false;
}

?>
