<?php
if(count($argv) < 3) {
	echo $argv[0]." gameid time\n";
	exit;
}
$gameid = $argv[1];
$time = strtotime($argv[2]);
$now = time();
echo $gameid." ".$time."\n";

require_once("Mysql.class.php");
$mysql = new Mysql("192.168.71.76", "tdconfig", "tdconfig@mysql", "db_td_config");
$sql = "insert into t_task_complete_log set game_id = $gameid, time=$time, complete_time = $now on duplicate key update complete_time = $now;";
$mysql->query($sql);
?>
