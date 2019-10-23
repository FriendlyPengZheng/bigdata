<?php
require_once("Mysql.class.php");
define("HADOOP", "/opt/taomee/hadoop/hadoop/bin/hadoop");
define("OUTPUT", "/bigdata/custom/");

$mysql = new Mysql("10.1.1.60", "root", "pwd@60", "db_td_config");
$log = new Log("custom_");
$file = $argv[1];

$sql = "select params from t_web_file where file_id = {$file} and file_source = 1 and status = 0";
$mysql->query($sql);
if(($row = $mysql->get_next_row()) !== false) {
        $param = json_decode($row['params'], true);
} else {
    echo "not found {$file}\n";
    exit(1);
}
if(check_param($param)) {
    //开始执行，设置标志为生成中
    start($file, $mysql);
    $game = "game=" . $param['game_id'];
    $key_string = "";
    foreach($param['operands'] as $key => $operand) {
        $stid = "stid='" . $operand['stid'] . "'";
        $sstid = "sstid='" . $operand['sstid'] . "'";
        $op_fields = $operand['op_fields'];
        $range = $operand['range'];
        if(strlen($op_fields) == 0){
            $op_fields = "";
        } else {
            $op_fields = "op_fields='" . $op_fields . "'";
        }
        if(strlen($range) == 0) {
            $range = "";
        } else {
            $range = "range='" . $range . "'";
        }
        list($days, $day_string) = getDays($operand);
        $operand_key = md5("gpzs={$param['gpzs_id']},data={$operand['data_id']},periods={$day_string}");
        $key_string .= $operand_key;
        $key_string .= ',';
        $log->debug_log("file {$file} operand {$key} as {$operand_key}");
        $todo = 0;
        while(true) {
            var_dump($operand_key);
            $e = check_operand_exists($operand_key);
            if($e == 0) {
                $todo = 0;
                break;
            } else if($e == 1) {
                var_dump("sleep 60");
                sleep(60);
                continue;
            } else if($e == 2) {
                $todo = 1;
                break;
            } else {
                error($file, $mysql, "检查路径 $operand_key 时错误");
                exit(2);
            }
        }
        if($todo == 1) {
            var_dump("do work for $operand_key");
            $a = array();
            var_dump("sh calc_custom_s1.sh {$game} {$stid} {$sstid} {$op_fields} {$range} days={$day_string} 2>&1");
            exec("sh calc_custom_s1.sh {$game} {$stid} {$sstid} {$op_fields} {$range} days={$day_string} operand={$operand_key} 2>&1", $a, $ret);
            if($ret == 0) {
                operand_status($file, $mysql, floor(($key+1)/(count($param['operands'])+1)*100));
                foreach($a as $l) {
                    $log->debug_log("$l");
                }
            } else if($ret == 1) {
                error($file, $mysql, "计算 $operand_key 时原数据不存在");
                exit(3);
            } else {
                error($file, $mysql, "计算 $operand_key 时错误");
                $log->error_log("calc $operand_key ret :");
                foreach($a as $l) {
                    $log->error_log("$l");
                }
                exit(4);
            }
        } else {
            operand_status($file, $mysql, floor(($key+1)/(count($param['operands'])+1)*100));
        }
    }
    $a = array();
    echo("sh calc_custom_s2.sh {$game} fileid={$file} operands={$key_string} operation={$param['operation']}");
    exec("sh calc_custom_s2.sh {$game} fileid={$file} operands={$key_string} operation={$param['operation']} 2>&1", $a, $ret);
    if($ret == 0) {
        done_status($file, $mysql);
        foreach($a as $l) {
            $log->debug_log("$l");
        }
    } else if($ret == 1) {
        error($file, $mysql, "计算用户数据时原数据不存在");
        $log->error_log("计算用户数据时原数据不存在");
        exit(3);
    } else if($ret == 3) {
        error($file, $mysql, "计算用户数据时参数错误");
        $log->error_log("计算用户数据时参数错误");
        exit(4);
    } else {
        error($file, $mysql, "计算用户数据时错误 file={$file}");
        $log->error_log("calc file={$file} ret :");
        foreach($a as $l) {
            $log->error_log("$l");
        }
        exit(5);
    }
} else {
    error($file, $mysql, "参数检查错误");
}

function check_param($param) {
    $a_keys = array(
        "game_id", "operation", "operands"
    );
    $a_operations = array(
        "setdiff"=>1, "intersect"=>1, "union"=>1
    );
    $a_data_keys = array(
        "stid", "sstid", "op_fields", "range", "periods"
    );
    foreach($a_keys as $key) {
        if(!array_key_exists($key, $param))    return false;
    }
    if(!array_key_exists($param['operation'], $a_operations))   return false;
    if(strcmp($param['operation'], 'setdiff') == 0 &&
        count($param['operands']) != 2) return false;
    if(count($param['operands']) <= 0)  return false;
    foreach($param['operands'] as $operand) {
        foreach($a_data_keys as $data_key) {
            if(!array_key_exists($data_key, $operand))    return false;
        }
        foreach($operand['periods'] as $period) {
            if(!array_key_exists('from', $period))  return false;
            if(!array_key_exists('to', $period))  return false;
            if(strcmp($period['from'],  date("Y-m-d", strtotime($period['from']))) != 0)    return false;
            if(strcmp($period['to'],  date("Y-m-d", strtotime($period['to']))) != 0)    return false;
        }
    }
    return true;
}

function start($file, $mysql) {
    update_status($file, $mysql, 0, 1);
}

function error($file, $mysql, $message) {
    update_status($file, $mysql, -1, 3, $message);
}

function done_status($file, $mysql) {
    update_status($file, $mysql, 100, 2);
}

function operand_status($file, $mysql, $process) {
    update_status($file, $mysql, $process, 1);
}

function update_status($file, $mysql, $process, $status, $message = "") {
    if($process >= 0) {
        $sql = "update t_web_file set progress={$process},status={$status},message='$message' where file_id = {$file}";
    } else {
        $sql = "update t_web_file set status={$status},message='$message' where file_id = {$file}";
    }
    $mysql->query($sql);
}

function getDays($operand) {
    $days = array();
    $day_string = "";
    foreach($operand['periods'] as $perios) {
        $start = strtotime($perios['from']);
        $end = strtotime($perios['to']);
        for($i = $start; $i <= $end; $i+=86400) {
            $days[date("Ymd", $i)] = 1;
            $day_string .= date("Ymd", $i);
            $day_string .= ',';
        }
    }
    return array($days, $day_string);
}

function check_operand_exists($key) {
    $e = check_file_exists(OUTPUT.$key);
    if($e == 0) {
        if(check_file_exists(OUTPUT.$key."/_SUCCESS") == 0) {
            return 0;//done
        } else {
            return 1;//sleep until done
        }
    } else if($e == 1) {
        return 2;//do work
    } else {
        return -1;
    }
}

function check_file_exists($file) {
    global $log;
    $cmd = HADOOP . " fs -test -e {$file}";
    for($i = 0; $i < 10; $i++) {
        exec($cmd, $a, $ret);
        switch($ret) {
        case 0:
        case 1:
            $log->debug_log("{$cmd} return {$ret}");
            return $ret;
        case 127:
            $log->error_log("{$cmd} execute file not exists");
            return $ret;
        case 255:
            $log->error_log("{$cmd} execute error");
            sleep(60);
        }
    }
    return -1;
}
