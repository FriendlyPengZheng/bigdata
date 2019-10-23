<?php
require_once("Mysql.class.php");
$date = $argv[1];
if(!checkDateFormat($date)) {
	echo "invalid date\n";
	exit -1;
}
//$sql = "select id, name, table_id from t_web_tree where parent_id = 13618 and id not in (14368, 13778, 13635, 13633, 13631, 13634, 13636)";

printByParentId(14279, $date);
printById(13664, $date, "道具\t道具输出\titem");
printAllByParentId(13678, $date, "功能", NULL, "item");
printById(13677, $date);
printById(13676, $date);
printById(13675, $date);
printById(13674, $date);
printAllByParentId(13775, $date, "运营活动", NULL, "item");
#printById(13659, $date, "精灵获得"); # grep -v UCOUNT  #到2013-12-12
printById(13783, $date, "精灵出战"); # grep -v UCOUNT
printById(13638, $date);
printById(13655, $date);

function printAllByParentId($node_id, $date, $s = NULL, $ss = NULL, $i = NULL) {
	$sql = "select id, name, table_id from t_web_tree where parent_id = $node_id";
	$mysql = new Mysql("192.168.71.68:3307", "stat", "db_pwdstat_csdf", "db_stat_config");
	$mysql->query($sql);
	$aId = array();
	while(($row = $mysql->get_next_row()) !== false) {
		$aId[$row['id']] = array(
			'name'     => $row['name'],
			'table_id' => (int)$row['table_id']);
	}
	foreach($aId as $id => $info) {
		printById($id, $date, $s, $info['name'], $i);
	}
}

function printByParentId($node_id, $date, $s = NULL, $ss = NULL, $i = NULL) {
	$sql = "select id, name, table_id from t_web_tree where parent_id = $node_id";
	printBySql($sql, $date, $s, $ss, $i);
}

function printById($node_id, $date, $s = NULL, $ss = NULL, $i = NULL) {
	$sql = "select id, name, table_id from t_web_tree where id = $node_id";
	printBySql($sql, $date, $s, $ss, $i);
}

function printBySql($sql, $date, $s = NULL, $ss = NULL, $i = NULL) {
	$mysql = new Mysql("192.168.71.68:3307", "stat", "db_pwdstat_csdf", "db_stat_config");
	$mysql->query($sql);
	$aId = array();
	while(($row = $mysql->get_next_row()) !== false) {
		$aId[$row['id']] = array(
			'name'     => $row['name'],
			'table_id' => (int)$row['table_id']);
	}
	foreach($aId as $id => $info) {
	       	$stid = $s == NULL ? $info['name'] : $s;
		if($info['table_id'] === 0) {
			$sql = "select id, name, table_id from t_web_tree where parent_id = $id";
			$mysql->query($sql);
			$aSubTable = array();
			while(($row = $mysql->get_next_row()) !== false) {
				$aSubTable[$row['id']] = array(
					'name'     =>	$row['name'],
					'table_id' =>   (int)$row['table_id']);
			}
			foreach($aSubTable as $subId => $subInfo) {
				$ss == NULL ? $sstid = $subInfo['name'] : $ss;
				if($subInfo['table_id'] !== 0) {
					$aResultInfo = getResultIdFromTable($mysql, $subInfo['table_id']);
					foreach($aResultInfo as $rId => $rInfo) {
						$item = $rInfo['name'];
						$type = $rInfo['type'];
						$value = getValueById($rId, $date);
						if($value == 0) continue;
						printf("%s\t16\t-1\t-1\t-1\t%s\t%s\t%s\t%s\n", $type, $stid, $sstid, $item, $value);
					}
				}
			}
		} else {
			$aResultInfo = getResultIdFromTable($mysql, $info['table_id']);
			foreach($aResultInfo as $rId => $rInfo) {
				$sstid = $ss == NULL ? $rInfo['name'] : $ss;
				$type  = $rInfo['type'];
				$value = getValueById($rId, $date);
				if($value == 0) continue;
				if($i == NULL) {
					printf("%s\t16\t-1\t-1\t-1\t%s\t%s\t%s\n", $type, $stid, $sstid, $value);
				} else {
					printf("%s\t16\t-1\t-1\t-1\t%s\t%s\t%s\t%s\t%s\n", $type, $stid, $sstid, $i, $rInfo['name'], $value);
				}
			}
		}
	}
}

function getResultIdFromTable($mysql, $table) {
	$sql = "select rule from t_web_table where id = $table";
	$mysql->query($sql);
	if(($row = $mysql->get_next_row()) === false) {
		return false;
	}
	$aRule = split("\|", $row['rule']);
	$aRet = array();
	foreach($aRule as $resultId) {
		$id = str_replace("rs_", "", $resultId, $count);
		if($count === 1) {
			$sql = "select id, name from t_result where id = $id";
			$mysql->query($sql);
			if(($row = $mysql->get_next_row()) !== false) {
				$aRet[$row['id']] = getResultTypeByName($row['name']);
			}
		}
	}
	return $aRet;
}

function getResultTypeByName($name) {
	$name = str_replace("免费|", "", $name, $count);
	$name = str_replace("收费|", "", $name, $count);
	$name = str_replace("获得道具", "", $name, $count);

	$name = str_replace("获得精灵", "", $name, $count);
	if($count === 1) {
		$name = str_replace(array(0,1,2,3,4,5,6,7,8,9), "", $name, $count);
	}
	$rName = str_replace("精灵", "", $name, $count);
	if($count === 1) {
		$rName = str_replace(array(0,1,2,3,4,5,6,7,8,9), "", $rName, $count);
		if($count >= 1)	$name = $rName;
	}
	$rName = str_replace("人数", "", $name, $count);
	if($count === 1) {
		return array("name" => $rName, "type" => "UCOUNT");
	}
	$rName = str_replace("人次", "", $name, $count);
	if($count === 1) {
		return array("name" => $rName, "type" => "COUNT");
	}
	return array("name" => $rName, "type" => "SUM");
}

function getValueById($rId, $date) {
	//http://192.168.71.57/lock-db/api/api.php?action=getMaxData&ids=rs_656503&start_time=1492652800&end_time=1492652800&interval=1440
	$unixTime = strtotime($date);
	$url = "http://192.168.71.57/lock-db/api/api.php?action=getMaxData&ids=rs_$rId&start_time=$unixTime&end_time=$unixTime&interval=1440";
	$retData = json_decode(file_get_contents($url), true);
	if(isset($retData[0]['value'][0]['value'])) {
		return $retData[0]['value'][0]['value'];
	} else {
		return 0;
	}
}

function checkDateFormat($date) {
	$unixTime = strtotime($date);
	if(!$unixTime) return false;
	if(date("Ymd", $unixTime) == $date)	return true;
	return false;
}
?>
