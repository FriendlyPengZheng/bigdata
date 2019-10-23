<?php

require_once('Mysql.class.php');
require_once("Log.class.php");

function debug($str) {
	global $log;
	$log->debug_log($str);
}

function error($str) {
	global $log;
	$log->error_log($str);
}

function init() {
	global $db_config;
	global $db_rp;
	global $db_rs;
	$db_config = new Mysql("192.168.71.68:3307", "srvmgr", "srvmgr@pwd", "db_stat_config");
	$a_db_cache = array();

	$db_config->query("select db_id  as db_id, concat(db_host,':', db_port) as db_url, db_user , db_pswd  , db_name from t_db_info where db_name like '%report%';");
	while(($row = $db_config->get_next_row()) !== false) {
		if(!isset($a_db_cache[$row['db_url']])) {
			$a_db_cache[$row['db_url']] = new Mysql($row['db_url'], $row['db_user'], $row['db_pswd'], $row['db_name']);
		}
		$db_rp[$row['db_id']] = $a_db_cache[$row['db_url']];
	}

	$db_config->query("select db_id as db_id, concat(db_host,':', db_port) as db_url, db_user , db_pswd  , db_name from t_db_info where db_name like '%result%';");
	while(($row = $db_config->get_next_row()) !== false) {
		if(!isset($a_db_cache[$row['db_url']])) {
			$a_db_cache[$row['db_url']] = new Mysql($row['db_url'], $row['db_user'], $row['db_pswd'], $row['db_name']);
		}
		$db_rs[$row['db_id']] = $a_db_cache[$row['db_url']];
	}
}

function get_rp_db_table_id($id) {
	$ret['db_id'] = (int)($id%10000/100) + 1;
	$ret['table_id'] = $id%100;
	return $ret;
}

function get_rs_db_table_id($id) {
	$ret['db_id'] = (int)($id%1000/100) + 1001;
	$ret['table_id'] = $id%100;
	return $ret;
}

function check_rp($id) {
	debug("check rp $id begin");
	global $rp_start_time;
	global $db_rp;
	$db_table_id = get_rp_db_table_id($id);
	$db_id = $db_table_id['db_id'];
	$table_id = $db_table_id['table_id'];

	$sql = "select count(1) as c from db_stat_report_{$db_id}.t_report_{$table_id} where id = $id and time > $rp_start_time and value != 0";
	$db_rp[$db_id]->query($sql);
	if(($row = $db_rp[$db_id]->get_next_row()) !== false) {
		if($row['c'] != 0) {
			debug("check rp $id end");
			return true;	//有值
		} else {
			debug("check rp $id end");
			return false;
		}
	}
	debug("check rp $id end");
	return false;
}

function check_rs($id) {
	debug("check rs $id begin");
	global $rs_start_time;
	global $db_rs;
	$db_table_id = get_rs_db_table_id($id);
	$db_id = $db_table_id['db_id'];
	$table_id = $db_table_id['table_id'];

	$sql = "select count(1) as c from db_stat_result_{$db_id}.t_result_{$table_id} where id = $id and time > $rs_start_time and value != 0";
	$db_rs[$db_id]->query($sql);
	if(($row = $db_rs[$db_id]->get_next_row()) !== false) {
		if($row['c'] != 0) {
			debug("check rs $id end");
			return true;	//有值
		} else {
			debug("check rs $id end");
			return false;
		}
	}
	debug("check rs $id end");
	return false;
}

function check_table($id) {
	debug("check table $id begin");
	global $db_config;
	$b_value = false;

	$sql = "select rule from t_web_table where id = $id";
	$db_config->query($sql);
	if(($row = $db_config->get_next_row()) !== false) {
		$rule = $row['rule'];
		preg_match_all('/rs_(\d+)/', $rule, $r, PREG_PATTERN_ORDER);
		if(isset($r[1])) {
			$r = $r[1];
			foreach($r as $rid) {
				if(check_rs($rid)) {
					$b_value = true;
				} else {
					debug("rs $rid no value");
				}
			}
		}

		preg_match_all('/rp_(\d+)/', $rule, $r, PREG_PATTERN_ORDER);
		if(isset($r[1])) {
			$r = $r[1];
			foreach($r as $rid) {
				if(check_rs($rid)) {
					$b_value = true;
				} else {
					debug("rs $rid no value");
				}
			}
		}

		preg_match_all('/t_(\d+)/', $rule, $r, PREG_PATTERN_ORDER);
		if(isset($r[1])) {
			$r = $r[1];
			foreach($r as $rid) {
				if(check_rs($rid)) {
					$b_value = true;
				} else {
					debug("rs $rid no value");
				}
			}
		}
	}

	debug("check table $id end");
	return $b_value;

}

function check_tree($id) {
	debug("check tree $id begin");
	global $db_config;
	$b_value = false;

	$sql = "select table_id, auth_id is not NULL as is_node from t_web_tree where id = $id and flag = 0";
	$db_config->query($sql);
	if(($row = $db_config->get_next_row()) !== false) {
		if($row['is_node'] == 1) {
			//is node
			if($row['table_id'] != 0) { 
				$table_ids = split(',', $row['table_id']);
				foreach($table_ids as $table_id) {
					if(check_table($table_id)) {
						$b_value = true;
					} else {
						debug("table $table_id no value");
					}
				}
			}
		} else {
			$sql = "select id from t_web_tree where parent_id = $id and flag = 0";
			$db_config->query($sql);
			while(($row = $db_config->get_next_row()) !== false) {
				$nodes[] = $row['id'];
			}
			if(isset($nodes)) {
				foreach($nodes as $node) {
					if(check_tree($node)) {
						$b_value = true;
					} else {
						debug("tree $node no value");
						delete_node($node);
					}
				}
			}
		}
	}
	debug("check tree $id end");
	return $b_value;
}

function delete_node($node) {
	global $db_config;
	$sql = "update t_web_tree set flag=2 where id = $node";
	//$db_config->query($sql);
	debug($sql);
	$sql = "select name from t_web_tree where id = $node";
	$db_config->query($sql);
	$row = $db_config->get_next_row();
	debug("delete $node {$row['name']}");
}

$rp_start_time = "1412312359";
$rs_start_time = "141231";

if(!isset($argv[1])) {
	echo "php {$argv[0]} t_web_table.id\n";
	exit;
}
$node = $argv[1];
$log = new Log("{$node}_");
$db_config;
$db_rp = array();
$db_rs = array();

init();
if(!check_tree($node)) {
	delete_node($node);
}
