<?php
require_once("Mysql.class.php");
$mysql = new Mysql("192.168.71.76", "tdconfig", "tdconfig@mysql", "db_td_config");
$sql = "select r_id, type, range, data_name from t_distr_range_info";
$mysql->query($sql);
$a_sql = array();
while(($row = $mysql->get_next_row()) !== false) {
	$a_sql[] = "update t_data_info set data_name='{$row['data_name']}' where r_id = {$row['r_id']} and type = '{$row['type']}' and range = '{$row['range']}';";
}
foreach($a_sql as $sql) {
	$mysql->query($sql);
}
?>
