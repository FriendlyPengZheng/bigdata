<?php
require_once("Mysql.class.php");
$mysql = new Mysql("192.168.71.76", "tdconfig", "tdconfig@mysql", "db_td_config");
$sql = "select gpzs_id as id, game_id as g, platform_id as p, zone_id as z, server_id as s from t_gpzs_info where status = 0;";
$mysql->query($sql);
$row = $mysql->get_next_row();
$aGPZS = array();
while(($row = $mysql->get_next_row()) !== false) {
	$aGPZS[$row['id']]['g'] = $row['g'];
	$aGPZS[$row['id']]['p'] = $row['p'];
	$aGPZS[$row['id']]['z'] = $row['z'];
	$aGPZS[$row['id']]['s'] = $row['s'];
}
foreach($aGPZS as $id => $gpzs) {
	$sql = "update t_item_sale_data set gpzs_id={$id} where game_id={$gpzs['g']} and platform_id={$gpzs['p']} and zone_id={$gpzs['z']} and server_id={$gpzs['s']};";
	$mysql->query($sql);
}
?>
