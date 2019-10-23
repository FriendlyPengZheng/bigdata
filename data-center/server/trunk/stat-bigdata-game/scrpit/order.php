<?php
require_once("Mysql.class.php");
$mysql = new Mysql("192.168.71.76", "tdconfig", "tdconfig@mysql", "db_td_config");
//$sql = "select count(*) as low from t_data_info";
//$mysql->query($sql);
//$row = $mysql->get_next_row();
//$index = $row['low'];
$index = 1;
$sql = "select data_id from t_data_info where display_order = 0 and hide = 0 and type = 'report' and r_id in (select report_id from t_report_info where stid not like '\_%\_') and range != '' order by range";
$mysql->query($sql);
$a_order = array();
while(($row = $mysql->get_next_row()) !== false) {
	$a_order[++$index] = $row['data_id'];
}
$sql = "select data_id from t_data_info where display_order != 0 and hide = 0 and type = 'report' and r_id in (select report_id from t_report_info where stid not like '\_%\_') and range != '' and order_by_name = 1 order by range";
$mysql->query($sql);
while(($row = $mysql->get_next_row()) !== false) {
	$a_order[++$index] = $row['data_id'];
}
foreach($a_order as $order => $id) {
	$sql = "update t_data_info set display_order = $order, order_by_name=1 where data_id = $id";
	$mysql->query($sql);
}
?>
