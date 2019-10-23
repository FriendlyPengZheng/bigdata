<?php
require_once('common_config.php');

/**
 * @brief insert_daily 插入每日数据
 *
 *
 * @param $game
 * @param $gameid
 * @param $d
 * @param $o_db
 *
 * @return
 */
function insert_daily($game, $gameid, $d, $o_db)
{

    echo $d . "\n";
    $filepath = WORK_DIR . $game . DS . 'result' . DS;
    $filename = $filepath . "ret_" . "$d" . ".txt";
    echo "data ready: $filename \n";
    $f = fopen($filename, 'r');
    if(FALSE === $f)
    {
        echo "fopen error: $d \n";
        return false;
    }

    // 处理字符串
    $string = fread($f, filesize ($filename));
    fclose($f);

    $arr = explode("\n", $string);
    unset($arr[count($arr)-1]);

    $brr = array();
    foreach ($arr as $key => $var)
    {
        $brr[] = explode(" ", $var);
    }

    $sql = 'INSERT INTO t_reg_trans'
        .' (time, gameid, s1, s2, s3, s4, s5)'
        .' VALUES(?, ?, ?, ?, ?, ?, ?)'
        .' ON DUPLICATE KEY UPDATE'
        .' s1=?, s2=?, s3=?, s4=?, s5=?';
    $ret = $o_db->execute($sql, WDB_PDO::SQL_RETURN_TYPE_EXEC,
        array($d, $gameid, $brr[0][1], $brr[1][1], $brr[2][1], $brr[3][1], $brr[4][1],
        $brr[0][1], $brr[1][1], $brr[2][1], $brr[3][1], $brr[4][1]) );
    if ( (1 !== $ret) && (2 !== $ret) && ( 0 !== $ret))
    {
        echo "exec sql failed: $d, $gameid\n";
        return false;
    }
    else
    {
        echo "exec sql success: $d, $gameid\n";
        return true;
    }

    unset($arr);
    unset($brr);

}


/**
 * @brief insert_curday 插入截止当天上一小时的累计数据
 *
 * @param $game
 * @param $gameid
 * @param $d
 * @param $o_db
 *
 * @return
 */
function insert_curday($game, $gameid, $d, $o_db)
{
    // 插入数据的日期
    echo $d . "\n";
    $filepath = WORK_DIR . $game . DS . 'result' . DS;
    $filename = $filepath . "ret_hour_end_" . "$d" . ".txt";
    echo "data ready: $filename \n";
    $f = fopen($filename, 'r');
    if(FALSE === $f)
    {
        echo "fopen error: $d \n";
        return false;
    }

    // 处理字符串
    $string = fread($f, filesize($filename));
    fclose($f);

    $arr = explode("\n", $string);
    unset($arr[count($arr)-1]);


    // 计算当天转化率时，最后一步忽略
    $sql = 'INSERT INTO t_reg_trans'
        .' (time, gameid, s1, s2, s3, s4, s5)'
        .' VALUES(?, ?, ?, ?, ?, ?, ?)'
        .' ON DUPLICATE KEY UPDATE'
        .' s1=?, s2=?, s3=?, s4=?, s5=?';

    if ($game == 'mole')
    {
        // mole特殊处理为落活跃统计等于登录online人数，最后一个数赋值给创建角色
        $ret = $o_db->execute($sql, WDB_PDO::SQL_RETURN_TYPE_EXEC,
            array($d, $gameid, $arr[0], $arr[1], $arr[2], $arr[2], $arr[3],
            $arr[0], $arr[1], $arr[2], $arr[2], $arr[3]) );
    }
    else
    {
        $ret = $o_db->execute($sql, WDB_PDO::SQL_RETURN_TYPE_EXEC,
            array($d, $gameid, $arr[0], $arr[1], $arr[2], $arr[3], 0,
            $arr[0], $arr[1], $arr[2], $arr[3], 0) );
    }

    if ( (1 !== $ret) && (2 !== $ret) && ( 0 !== $ret))
    {
        echo "exec sql failed: $d, $gameid\n";
        return false;
    }
    else
    {
        echo "exec sql success: $d, $gameid\n";
        return true;
    }

    unset($arr);

}

/**
 * @brief insert_hourly 插入当天的小时数据
 *
 * @param $game
 * @param $gameid
 * @param $d
 * @param $hour
 * @param $o_db
 *
 * @return
 */
function insert_hourly($game, $gameid, $d, $hour,$o_db)
{
    global $release_filename;
    global $bigdata_filename;
    global $a_game_recver;
    global $a_step_name;
    global $a_game_name;

    // 插入数据的日期
    $date = $d;
    echo $d . "\n";
    $filepath = WORK_DIR . $game . DS . 'result' . DS;
    $filename = $filepath . "ret_en_hour_monitor_" . "$d" . '_' . "$hour" .".txt";
    echo "data ready: $filename \n";
    $f = fopen($filename, 'r');
    if(FALSE === $f)
    {
        echo "fopen error: $d \n";
        return false;
    }

    // 处理字符串
    $string = fread($f, filesize($filename));
    fclose($f);

    $arr = explode("\n", $string);
    unset($arr[count($arr)-1]);


    // 计算此小时对应的时间戳
    $timestamp = strtotime($d) + $hour * 3600;

    // 计算当天转化率时，最后一步忽略，mole比较特殊,中间一步直接删去，由后面步骤代替
    $sql = 'INSERT INTO t_reg_trans_hour'
        .' (time, gameid, s1, s2, s3, s4, s5)'
        .' VALUES(?, ?, ?, ?, ?, ?, ?)'
        .' ON DUPLICATE KEY UPDATE'
        .' s1=?, s2=?, s3=?, s4=?, s5=?';
    $ret = $o_db->execute($sql, WDB_PDO::SQL_RETURN_TYPE_EXEC,
        array($timestamp, $gameid, $arr[0], $arr[1], $arr[2], $arr[3], 0,
        $arr[0], $arr[1], $arr[2], $arr[3], 0) );
    if ( (1 !== $ret) && (2 !== $ret) && ( 0 !== $ret))
    {
        echo "exec sql failed: $d, $gameid\n";
        unset($arr);
        return false;
    }
    else
    {
        echo "exec sql success: $d, $gameid\n";
    }

    if ($hour < 9 || $hour > 19) {
        unset($arr);
        return true;
    }
    $sql = 'select avg(s2/s1*100) as p1,'
        .' avg(s3/s1*100) as p2,'
        .' avg(s4/s1*100) as p3'
        .' from t_reg_trans_hour where'
        .' gameid=?'
        .' and mod(time,86400)=mod( ?, 86400)'
        .' and time < ? and time >= ? - 86400 * 30'
        .' group by gameid;';

    if (false === ($ret = $o_db->execute($sql, WDB_PDO::SQL_RETURN_TYPE_ROW,
        array($gameid, $timestamp, $timestamp, $timestamp))) ) {
        echo "exec sql failed";
    }

    $a_step = array();
    $a_step['p1'] = $arr[1]/$arr[0]*100;
    $a_step['p2'] = $arr[2]/$arr[0]*100;
    $a_step['p3'] = $arr[3]/$arr[0]*100;

    foreach($a_step as $step => $p) {
        /****************清空目标文件****************/
        //清空reg_hour_warn_weixin_release.html
        $fh = fopen($release_filename, 'w');
        fwrite($fh, "");
        fclose($fh);
        //清空reg_hour_warn_weixin_bigdata.html
        $fh = fopen($bigdata_filename, 'w');
        fwrite($fh, "");
        fclose($fh);

        $d = round($ret[$step] - $p, 2);
        $p = round($p, 2);
        $l = round($ret[$step], 2);

        if($p <= ($ret[$step] - 6)) {
            //将HTML文件保存到文件 用于微信发送注册转化日报
            $body = "【{$a_game_name[$gameid]}】 :  {$date}-{$hour}时\n";
            $body .= "{$a_step_name[$step]}单步骤转化率 降至$p%\n";
            $body .= "比最近30天平均值{$l}%低{$d}%";

            if(get_calendar(time()) !== 0) {
                $fh = fopen($bigdata_filename, 'w');
                fwrite($fh, $body);
                fclose($fh);
            } else {
                //给数据平台部人员发rtx
                $body = "{$a_game_name[$gameid]} {$hour}时 {$a_step_name[$step]}单步骤转化率 降至$p%，比最近30天平均值{$l}%低{$d}%，请[点击查看|http://tongji.taomee.com/index.php?r=topic%2Fsigntrans%2Frealtime%2F01]。";
                foreach($a_game_recver['bigdata'] as $recver) {
                    $body_gb2312 = iconv('utf8', 'gb2312', $body);
                    exec("export LD_LIBRARY_PATH=./ && ./stat-alarm-sender \"$body_gb2312\" rtx {$recver}");
                }
            }
        }
        if($p <= ($ret[$step] - 10)) {
            if($p != 0){
                $body = "【{$a_game_name[$gameid]}】 :  {$date}-{$hour}时\n";
                $body .= "{$a_step_name[$step]}单步骤转化率 降至$p%\n";
                $body .= "比最近30天平均值{$l}%低{$d}%";

                if(get_calendar(time()) !== 0) {
                    //假期，发微信
                    //将HTML文件保存到文件 用于微信发送注册转化日报
                    $fh = fopen($release_filename, 'w');
                    fwrite($fh, $body);
                    fclose($fh);
                } else {
                    //工作日，发rtx
                    //给游戏项目人员发rtx
                    foreach($a_game_recver[$gameid] as $recver) {
                        $body_gb2312 = iconv('utf8', 'gb2312', $body);
                        exec("export LD_LIBRARY_PATH=./ && ./stat-alarm-sender \"$body_gb2312\" rtx {$recver}");
                    }
                    //给all人员发rtx
                    foreach($a_game_recver['all'] as $recver) {
                        $body_gb2312 = iconv('utf8', 'gb2312', $body);
                        exec("export LD_LIBRARY_PATH=./ && ./stat-alarm-sender \"$body_gb2312\" rtx {$recver}");
                    }
                    ////给数据平台部人员发rtx,<=6%时已经发过了
                    //$body = "{$a_game_name[$gameid]} {$hour}时 {$a_step_name[$step]}单步骤转化率 降至$p%，比最近30天平均值{$l}%低{$d}%，请[点击查看|http://tongji.taomee.com/index.php?r=topic%2Fsigntrans%2Frealtime%2F01]。";
                    //foreach($a_game_recver['bigdata'] as $recver) {
                    //    $body_gb2312 = iconv('utf8', 'gb2312', $body);
                    //    exec("export LD_LIBRARY_PATH=./ && ./stat-alarm-sender \"$body_gb2312\" rtx {$recver}");
                    //}
                }

            }
        }
    }

    unset($arr);
    return true;

}

function get_calendar($time) {
    $cmd = "mysql -ubigdbdata -pdata@21011420 -h192.168.71.76 --default-character-set='utf8' db_td_config -e \"select count(1) from t_web_calendar_event where \`from\` <= ${time} and  ${time} <= \`to\` and event_name = '假期'\" | grep -v count";
    exec($cmd, $a);
    return ((int)$a[0]);
}

/// 获取参数
if ($argc < 4)
{
    echo "usage [day] [gameid] [mode] [可选：模式是3时，需填小时, 如00,01等]\n";
    exit();
}

$d      =   $argv[1];
$gid    =   $argv[2];
$mode   =   $argv[3];

if ($mode == 3)
{
    if($argc != 5)
    {
        echo "less param: hour\n";
        exit();
    }
    else
    {
        $hour = $argv[4];
    }
}


$game   =   $gids[$gid];

/// 初始化db实例
$o_db   = get_db_conn($dbhost, $dbport, $dbuser, $dbpass, $dbname);
if (FALSE === $o_db)
{
    echo "get_db_conn failed!\n";
    exit();
}


/// 判断插入的模式
echo "$game:$gid:$d\n";
if ( $mode == 1)
{
    if (false === insert_daily($game, $gid, $d, $o_db)) {
        echo "insert daily data error!!\n";
        exit();
    }
}
else if ($mode == 2)
{
    if(false === insert_curday($game, $gid, $d, $o_db)) {
        echo "insert curday data error!!\n";
        exit();
    }
}
else if ($mode == 3)
{
    if(false === insert_hourly($game, $gid, $d, $hour, $o_db)) {
        echo "insert hourly data error!!\n";
        exit();
    }
}
else
{
    echo "invalid mode: $mode\n";
    exit();
}

?>
