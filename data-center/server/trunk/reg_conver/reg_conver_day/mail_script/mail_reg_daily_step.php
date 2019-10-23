<?php
require_once('../conf/common_config.php');
require_once('Rmail.php');


/**
    * @brief do_log 写日志
    *
    * @param $str_log
    *
    * @return
 */
function do_log($str_log)
{
    $log_file = '../log/'.date('Ymd') . '.log';
    $handle = fopen($log_file, 'ab');
    if ($handle) {
        fwrite($handle, '[' . date('Y-m-d H:i:s') . '] ' . print_r($str_log, true) . "\n");
        fclose($handle) ;
    }
}


/**
    * @brief send_mail 发送邮件, 并设置邮件的html的属性
    *
    * @param $to
    * @param $cc
    * @param $title
    * @param $body
    * @param $charts
    * @param $comment
    *
    * @return
 */
function send_mail($to, $cc, $title, $body, $charts, $comment)
{
    $mail = new Rmail();
    $mail->setTextCharset('UTF-8');
    $mail->setHTMLCharset('UTF-8');
    $mail->setHeadCharset('UTF-8');
    $mail->setSMTPParams('mail.shidc.taomee.com', 25, 'helo', false, '', '');
    #$mail->setFrom('stat <stat@taomee.com>');
    $mail->setFrom('IED-数据平台部 <informplatform@taomee.com>');
    $mail->setCc($cc);
    $mail->setSubject($title);
    $style = '
        <style>
        *{font-size:10px;margin:0;padding:0}
        h1{font-size:18px; text-align:center;margin-top:20px}
        h2{font-size:16px; text-align:left}
        .data-date{width:1000px;text-align:center}
        .data-box{margin:10px 0;}
        .data-box h2{font-size:18px;margin:20px 0 0 10px;}
        .image-box{margin:20px 0 0 10px;border:2px dotted #C9D4F4;width:1020px;text-align:center;height:450px}
        .image-box div{width:1000px;margin:0 auto;text-align:left;}
        .desc-msg{border:1px solid #FB9D39;background:#FEF3CE;padding:8px 0;margin:0 8px}
        .data-help{margin-left:10px;line-height:22px;width:1020px;border:2px dotted #C9D4F4;padding:10px;text-align:left;margin-bottom:50px}
        .version{magin:20px 0; width:1020px;border:2px dotted #C9D4F4}
	td{border-top:0;border-right:1px black solid;border-bottom:1px black solid;border-left:0;}
	th{border-top:0;border-right:1px black solid;border-bottom:1px black solid;border-left:0;}
	table{border-top:1px black solid;border-right:0;border-bottom:0;border-left:1px black solid;}
        </style>';

    //$end = '</br><div class="footer" style="margin:20px 0; font-size: 14px; color:red">注：表中为最近7天的注册转化率，若该步骤转化率与最近30天平均值的变化率超过2%，则红色标注</div>';

    $mail->setHTML($style.$body);

    foreach ($charts as $mail_chart) {
        $mail->addEmbeddedImage(new fileEmbeddedImage($mail_chart));
    }

    if (!$mail->send(array($to), 'smtp')) {
        //do_log("ERROR: failed to send mail");
    }
}


/**
    * @brief main_mail_warn 告警邮件的内容
    *
    * @param $result
    *
    * @return
 */
function main_mail_warn($result, $game, $gamename)
{
    // 邮件正文第一行显示该日报的标题
    $mail_body = "<div class='data-date'><h1><font color='red'></font>新用户注册转化：$gamename </h1></div>";

    $mail_body .= "<span>{$result['desc']}</span>";
    return $mail_body;
}


/**
    * @brief mail_header 组织单项目邮件正文的文字描述部分
    *
    * @param $game
    * @param $gamename
    *
    * @return
 */
function mail_header($game, $gamename)
{
    // 邮件正文第一行显示该日报的标题
	$header = "<div class='data-date'><h1><font color='red'></font>新用户注册转化：$gamename </h1></div>";
    return $header;
}


/**
    * @brief mail_studio_header 组织工作室邮件正文的文字描述部分
    *
    * @param $studio_name
    *
    * @return
 */
function mail_studio_header($studio_name)
{
	$header = "<div class='data-date'><h1><font color='red'></font>新用户注册转化(按步骤)：$studio_name </h1></div>";
    $remark = '注：表中为最近10天的注册转化率(可对比上周同期情况)，若该步骤转化率与最近30天平均值的变化率相比, 低于-3%，绿色标注，高于3%，红色标注。';
    $header .= "<div class='footer' style='margin:20px 0; font-size: 12px; color:red'>$remark</div>";
    return $header;
}


/**
    * @brief mail_tablebody 单项目邮件的主体部分、表格等
    *
    * @param $data
    * @param $game
    *
    * @return
 */
function mail_tablebody($data, $game)
{
    // 步骤的描述
    GLOBAL $g_step_desc;

    /// 第一个表格, 展现转化率
    // 表头
    $table_body = "
		<table width='95%' align='center' cellpadding='5' cellspacing='0' style='font-family:Arial;font-size:14px'>
        <tr><th bgcolor='' align='center' colspan=6>各步骤与第一步的转化率</th></th></tr>
		<tr style='font-weight:bold; color:white'>
		<th bgcolor='#4BACC6' align='center'>日期</th>";

    // 表的首行标题, 用于描述该游戏的步骤
    foreach($g_step_desc[$game] as $key => $var){
        $table_body .= "<td bgcolor='#4BACC6' align='center'>$var</td>";
    }
    $table_body .= "</tr>";

    // 转化率表的主体部分
    $table_rate_body = make_table_rate($data);

    $table_body .= $table_rate_body;
    ///============================///


    // 加上对转化率的注释
    #$table_body .= '<div class="footer" style="margin:20px 0; font-size: 12px; color:red">注：表中为最近7天的注册转化率，若该步骤转化率与最近30天平均值的变化率相比, 低于-2%，红色标注，高于2%，绿色标注。</div></br>';



    /// 第二个表格 展现人数
    // 表头
	$table_body .= "
        <table width='95%' align='center' cellpadding='5' cellspacing='0' style='font-family:Arial;font-size:14px'>
        <tr><th bgcolor='' align='center' colspan=6>各步骤人数</th></th></tr>
		<tr style='font-weight:bold; color:white'>
		<th bgcolor='#4BACC6' align='center'>日期</th>";

    // 不同游戏的步骤都不尽相同
    foreach($g_step_desc[$game] as $var){
        $table_body .= "<td bgcolor='#4BACC6' align='center'>$var</td>";
    }
    $table_body .= "</tr>";

    $table_num_body = make_table_num($data);

    $table_body .= $table_num_body;

    return $table_body;
}

/**
    * @brief mail_studio_tablebody工作室邮件的主体部分
    *
    * @param $data
    * @param $game
    * @param $gamename
    *
    * @return
 */
function mail_studio_tablebody($data, $game, $gamename)
{
    // 步骤的描述
    GLOBAL $g_step_desc;

    /// 第一个表格, 展现转化率
    // 表头
    $table_body = "
		<table width='95%' align='center' cellpadding='5' cellspacing='0' style='font-family:Arial;font-size:14px'>
        <tr><th bgcolor='' align='center' colspan=6>$gamename</th></th></tr>
		<tr style='font-weight:bold; color:white'>
		<th bgcolor='#4BACC6' align='center'>日期</th>";

    // 表的首行标题, 用于描述该游戏的步骤
    foreach($g_step_desc[$game] as $key => $var){
        $table_body .= "<td bgcolor='#4BACC6' align='center'>$var</td>";
    }
    $table_body .= "</tr>";

    // 转化率表的主体部分
    $table_rate_body = make_table_rate($data);

    $table_body .= $table_rate_body;
    ///============================///

    return $table_body;

}

/**
    * @brief make_table_rate 组织转化率表格
    *
    * @param $data
    *
    * @return
 */
function make_table_rate($data)
{
    // 红、绿色标注的阈值设定, 当前设定为0.02
    GLOBAL $g_warn_threshold;

    $table_rate_body = '';

    // 组织最近7天的数据, 转化率
    $i_row = 1;
    foreach ($data['data'] as $date => $var)
    {
        //使行颜色交替显示
        if($i_row % 2 == 1) {
            $bgcolor = "bgcolor='#DBEEF3'";
            $bgcolor1 = "bgcolor='#DBEEF3'";
            $bgcolor2 = "bgcolor='#DBEEF3'";
            $bgcolor3 = "bgcolor='#DBEEF3'";
            $bgcolor4 = "bgcolor='#DBEEF3'";
            $bgcolor5 = "bgcolor='#DBEEF3'";
        } else {
            $bgcolor = "";
            $bgcolor1 = "";
            $bgcolor2 = "";
            $bgcolor3 = "";
            $bgcolor4 = "";
            $bgcolor5 = "";
        }

        // 日期的显示转换为2013-12-01
        $date_to_display = date("Y-m-d", strtotime($date));

        // 计算各步骤与30天平均值的变化率
        $i_change_rate_s1 = $var['rate_s1'] / $data['avg_rate']['avg_s1'] - 1;
        $i_change_rate_s2 = $var['rate_s2'] / $data['avg_rate']['avg_s2'] - 1;
        $i_change_rate_s3 = $var['rate_s3'] / $data['avg_rate']['avg_s3'] - 1;
        $i_change_rate_s4 = $var['rate_s4'] / $data['avg_rate']['avg_s4'] - 1;
        $i_change_rate_s5 = $var['rate_s5'] / $data['avg_rate']['avg_s5'] - 1;


        // 根据变化率的值来确定数据颜色
        // 步骤1
        if (abs($i_change_rate_s1) <= $g_warn_threshold)
        {
            $rate_color1 = 'black';
        }
        else
        {
            //$rate_color1 = $i_change_rate_s1 > 0 ? 'green' : 'red';
            $rate_color1 = $i_change_rate_s1 > 0 ? 'red' : 'green';
        }

        // 步骤2
        if (abs($i_change_rate_s2) <= $g_warn_threshold)
        {
            $rate_color2 = 'black';
        }
        else
        {
            //$rate_color2 = $i_change_rate_s2 > 0 ? 'green' : 'red';
            $rate_color2 = $i_change_rate_s2 > 0 ? 'red' : 'green';
        }

        // 步骤3
        if (abs($i_change_rate_s3) <= $g_warn_threshold)
        {
            $rate_color3 = 'black';
        }
        else
        {
            //$rate_color3 = $i_change_rate_s3 > 0 ? 'green' : 'red';
            $rate_color3 = $i_change_rate_s3 > 0 ? 'red' : 'green';
        }

        // 步骤4
        if (abs($i_change_rate_s4) <= $g_warn_threshold)
        {
            $rate_color4 = 'black';
        }
        else
        {
            //$rate_color4 = $i_change_rate_s4 > 0 ? 'green' : 'red';
            $rate_color4 = $i_change_rate_s4 > 0 ? 'red' : 'green';
        }

        // 步骤5
        if (abs($i_change_rate_s5) <= $g_warn_threshold)
        {
            $rate_color5 = 'black';
        }
        else
        {
            //$rate_color5 = $i_change_rate_s5 > 0 ? 'green' : 'red';
            $rate_color5 = $i_change_rate_s5 > 0 ? 'red' : 'green';
        }


        // 转化率以百分比展示
        $rate_s1 = round($var['rate_s1']*100, 2) . '%';
        $rate_s2 = round($var['rate_s2']*100, 2) . '%';
        $rate_s3 = round($var['rate_s3']*100, 2) . '%';
        $rate_s4 = round($var['rate_s4']*100, 2) . '%';
        $rate_s5 = round($var['rate_s5']*100, 2) . '%';

        if($rate_color1 == 'red' || $rate_color1 == 'green')
        {
            $data1 = "<font color=$rate_color1><b>{$rate_s1}</b></font>";
        }
        else
        {
            $data1 = "<font color=$rate_color1>{$rate_s1}</font>";
        }
        if($rate_color2 == 'red' || $rate_color2 == 'green')
        {
            $data2 = "<font color=$rate_color2><b>{$rate_s2}</b></font>";
        }
        else
        {
            $data2 = "<font color=$rate_color2>{$rate_s2}</font>";
        }
        if($rate_color3 == 'red' || $rate_color3 == 'green')
        {
            $data3 = "<font color=$rate_color3><b>{$rate_s3}</b></font>";
        }
        else
        {
            $data3 = "<font color=$rate_color3>{$rate_s3}</font>";
        }
        if($rate_color4 == 'red' || $rate_color4 == 'green')
        {
            $data4 = "<font color=$rate_color4><b>{$rate_s4}</b></font>";
        }
        else
        {
            $data4 = "<font color=$rate_color4>{$rate_s4}</font>";
        }
        if($rate_color5 == 'red' || $rate_color5 == 'green')
        {
            $data5 = "<font color=$rate_color5><b>{$rate_s5}</b></font>";
        }
        else
        {
            $data5 = "<font color=$rate_color5>{$rate_s5}</font>";
        }
        //绿色字体表格底色为黄
        if($rate_color1 == 'green')
        {
            $bgcolor1 = "bgcolor='#FFFF37'";
        }
        if($rate_color2 == 'green')
        {
            $bgcolor2 = "bgcolor='#FFFF37'";
        }
        if($rate_color3 == 'green')
        {
            $bgcolor3 = "bgcolor='#FFFF37'";
        }
        if($rate_color4 == 'green')
        {
            $bgcolor4 = "bgcolor='#FFFF37'";
        }
        if($rate_color5 == 'green')
        {
            $bgcolor5 = "bgcolor='#FFFF37'";
        }
        //在表尾处加入一行
        $table_rate_body .="<tr>
            <td $bgcolor align='center'>{$date_to_display}</td>
            <td $bgcolor1 align='center'>$data1</td>
            <td $bgcolor2 align='center'>$data2</td>
            <td $bgcolor3 align='center'>$data3</td>
            <td $bgcolor4 align='center'>$data4</td>
            <td $bgcolor5 align='center'>$data5</td>
            </tr>";

        #//在表尾处加入一行
        #$table_rate_body .="<tr>
        #    <td $bgcolor align='center'>{$date_to_display}</td>
        #    <td $bgcolor align='center'><font color=$rate_color1>{$rate_s1}</font></td>
        #    <td $bgcolor align='center'><font color=$rate_color2>{$rate_s2}</font></td>
        #    <td $bgcolor align='center'><font color=$rate_color3>{$rate_s3}</font></td>
        #    <td $bgcolor align='center'><font color=$rate_color4>{$rate_s4}</font></td>
        #    <td $bgcolor align='center'><font color=$rate_color5>{$rate_s5}</font></td>
        #    </tr>";

        $i_row ++;

    }

    $table_rate_body .= "</table>";
    return $table_rate_body;

}


/**
    * @brief make_table_num 组织人数表格
    *
    * @param $data
    *
    * @return
 */
function make_table_num($data)
{
    $table_num_body = '';

    // 组织最近7天的数据, 人数
    $i_row = 1;
    foreach ($data['data'] as $date => $var)
    {

        //使行颜色交替显示
        if($i_row % 2 == 1) {
            $bgcolor = "bgcolor='#DBEEF3'";
        } else {
            $bgcolor = "";
        }

        $date_to_display = date("Y-m-d", strtotime($date));
        //在表尾处加入一行
        $table_num_body .="<tr>
            <td $bgcolor align='center'>{$date_to_display}</td>
            <td $bgcolor align='center'>{$var['s1']}</td>
            <td $bgcolor align='center'>{$var['s2']}</td>
            <td $bgcolor align='center'>{$var['s3']}</td>
            <td $bgcolor align='center'>{$var['s4']}</td>
            <td $bgcolor align='center'>{$var['s5']}</td>
            </tr>";

        $i_row ++;

    }


    $table_num_body .= '</table>';

    return $table_num_body;

}



/**
    * @brief get_reg_data 从数据库中拉取数据
    *
    * @param $game_id
    * @param $compute_day
    *
    * @return
 */
function get_reg_data($game_id, $compute_day)
{
    GLOBAL $dbhost;
    GLOBAL $dbport;
    GLOBAL $dbuser;
    GLOBAL $dbpass;
    GLOBAL $dbname;
    $o_db = get_db_conn($dbhost,$dbport, $dbuser, $dbpass, $dbname);
    if( $o_db === FALSE) {
        echo "get db conn failed!\n";
        return false;
    }

    // 取指定日期的前7天数据
    $s_date = date('Ymd', strtotime("-9 day $compute_day"));
    $e_date = date('Ymd', strtotime($compute_day));

    do_log("get register trans data of game: $game_id, start_date: $s_date, end_date: $e_date");
    $sql = 'SELECT time, s1,'
        .' s2,'
        .' s3,'
        .' s4,'
        .' s5,'
        .' s1/s1 as rate_s1,'
        .' s2/s1 as rate_s2,'
        .' s3/s1 as rate_s3,'
        .' s4/s1 as rate_s4,'
        .' s5/s1 as rate_s5'
        .' FROM t_reg_trans'
        .' WHERE gameid = ?'
        .' AND time >= ?'
        .' AND time <= ?'
        .' ORDER BY time DESC';

    $data = $o_db->execute($sql, WDB_PDO::SQL_RETURN_TYPE_ALL, array($game_id, $s_date, $e_date));

    if (FALSE === $data)
    {
        do_log("get data from db failed, sql: $sql");
        return false;
    }

    // 判断取出的数据是否完整正确
    foreach($data as $key => $var)
    {
        $checked_data[$var['time']] = array('s1' => $var['s1'], 's2' => $var['s2'], 's3'=>$var['s3'],
            's4'=>$var['s4'], 's5'=>$var['s5'],
            'rate_s1' => $var['rate_s1'],'rate_s2' => $var['rate_s2'],'rate_s3' => $var['rate_s3'],
            'rate_s4' => $var['rate_s4'],'rate_s5' => $var['rate_s5'],
        );
    }

    // 补足缺失的日期，但一般情况下，不应缺失，应在发送前先检测出缺失的日期并修复
    for($check_date = $e_date; $check_date >= $s_date; $check_date = date('Ymd', strtotime("-1 day $check_date")))
    {
        if(!array_key_exists($check_date, $checked_data))
        {
            $checked_data[$check_date] = array('s1'=>0, 's2'=>0, 's3'=>0, 's4'=>0, 's5'=>0,
                'rate_s1' => 0, 'rate_s2' => 0, 'rate_s3' => 0,'rate_s4' => 0,'rate_s5' => 0, );
        }
    }

    // 补足的日期可能在中间，所以要再次排序, 按日期从近到远的排序
    krsort($checked_data);


    // 取近30天的平均值
    // 取平均值的起始日期
    $s_avg_date = date('Ymd', strtotime("-30 day $compute_day"));
    // 取平均值的结束日期
    $e_avg_date = $e_date;
    $sql = 'SELECT AVG(s1/s1) AS avg_s1,'
        .' AVG(s2/s1) AS avg_s2,'
        .' AVG(s3/s1) AS avg_s3,'
        .' AVG(s4/s1) AS avg_s4,'
        .' AVG(s5/s1) AS avg_s5 '
        .' FROM t_reg_trans'
        .' WHERE gameid = ?'
        .' AND time >= ?'
        .' AND time <= ?';
    $avg_data = $o_db->execute($sql, WDB_PDO::SQL_RETURN_TYPE_ROW, array($game_id,$s_avg_date,$e_avg_date));
    if ( FALSE === $avg_data)
    {
        do_log("get average rate of last 30 days failed, sql: $sql");
        return false;
    }

	return array('data' => $checked_data, 'avg_rate' => $avg_data);
}


/**
 * @brief get_reg_data 从数据库中拉取数据,并监控报警
 *
 *
 * @param $game_id
 * @param $compute_day
 *
 * @return
 */
function get_reg_data_monitor($game_id, $compute_day)
{
    GLOBAL $dbhost;
    GLOBAL $dbport;
    GLOBAL $dbuser;
    GLOBAL $dbpass;
    GLOBAL $dbname;
    $o_db = get_db_conn($dbhost,$dbport, $dbuser, $dbpass, $dbname);
    if( $o_db === FALSE) {
        do_log("get db conn failed!");
        return array('result' => -1, 'desc' => 'get db conn failed!');
    }

    // 取指定日期的前7天数据
    $s_date = date('Ymd', strtotime("-9 day $compute_day"));
    $e_date = date('Ymd', strtotime($compute_day));

    /// 首先取近30天的平均值
    $s_avg_date = date('Ymd', strtotime("-30 day $compute_day"));// 取平均值的起始日期
    $e_avg_date = $e_date;// 取平均值的结束日期

    do_log("start to get average trans rate of game: $game_id, start_date: $s_avg_date, end_date: $e_avg_date");

    $sql = 'SELECT AVG(s1/s1) AS avg_s1,'
        .' AVG(s2/s1) AS avg_s2,'
        .' AVG(s3/s2) AS avg_s3,'
        .' AVG(s4/s3) AS avg_s4,'
        .' AVG(s5/s4) AS avg_s5 '
        .' FROM t_reg_trans'
        .' WHERE gameid = ?'
        .' AND time >= ?'
        .' AND time <= ?';
    $avg_data = $o_db->execute($sql, WDB_PDO::SQL_RETURN_TYPE_ROW, array($game_id,$s_avg_date,$e_avg_date));
    if ( FALSE === $avg_data)
    {
        do_log("DB ERROR: get average rate of last 30 days failed, sql: $sql");
        return array('result' => -1, 'desc' => 'DB ERROR: get average rate of last 30 days failed');
    }


    /// 获取最近7天的数据，并经过一轮检查
    do_log("start to get register trans data of game: $game_id, start_date: $s_date, end_date: $e_date");
    $sql = 'SELECT time, s1,'
        .' s2,'
        .' s3,'
        .' s4,'
        .' s5,'
        .' s1/s1 as rate_s1,'
        .' s2/s1 as rate_s2,'
        .' s3/s2 as rate_s3,'
        .' s4/s3 as rate_s4,'
        .' s5/s4 as rate_s5'
        .' FROM t_reg_trans'
        .' WHERE gameid = ?'
        .' AND time >= ?'
        .' AND time <= ?'
        .' ORDER BY time DESC';

    $data = $o_db->execute($sql, WDB_PDO::SQL_RETURN_TYPE_ALL, array($game_id, $s_date, $e_date));

    if (FALSE === $data)
    {
        do_log("DB ERROR: get data of last 10 days failed, sql: $sql");
        return array('result' => -1, 'desc' => 'DB ERROR: get data of last 7 days failed');
    }

    // 组织数据
    foreach($data as $key => $var)
    {
        $checked_data[$var['time']] = array('s1' => $var['s1'], 's2' => $var['s2'], 's3'=>$var['s3'],
            's4'=>$var['s4'], 's5'=>$var['s5'],
            'rate_s1' => $var['rate_s1'],'rate_s2' => $var['rate_s2'],'rate_s3' => $var['rate_s3'],
            'rate_s4' => $var['rate_s4'],'rate_s5' => $var['rate_s5'],
        );
    }

    /// 开始检查, 数据缺失、数据为0、数据与平均值差距过大
    $less_flag = false;
    $zero_flag = false;
    $wrong_flag = false;
    $less_desc = '';
    $zero_desc = '';
    $wrong_desc = '';

    for($check_date = $e_date; $check_date >= $s_date; $check_date = date('Ymd', strtotime("-1 day $check_date")))
    {
        // 数据是否缺失
        if(!array_key_exists($check_date, $checked_data))
        {
            // 添加临时判断，赤瞳数据缺失不报警
            if($game_id != "19")
            {
                $less_flag = true;
                $less_desc .= $check_date . ',';
            }
        }
        else
        {// 数据存在判断有效性
            $check_steps = array('s1', 's2', 's3', 's4', 's5');
            foreach($check_steps as $step_name)
            {
                if($checked_data[$check_date][$step_name] == 0)
                {
                    $zero_flag = true;
                    $zero_desc .= "[$check_date:$step_name] is 0;";
                }

                //对于异常数据的监控将暂不考虑，改为人为监控，因为一旦降低是可
                //解释的原因，若要解除警报，还需对此警报进行屏蔽，在这里过于复杂
                //$rate_step = 'rate_' . $step_name;
                //$avg_step = 'avg_' . $step_name;

                //if (($checked_data[$check_date][$rate_step] - $avg_data[$avg_step]) < -0.05)
                //{
                    //$wrong_flag = true;
                    //$wrong_desc .= "[$check_date:$rate_step]过低, 低于平均值超过5%,"
                        //."rate: {$checked_data[$check_date][$rate_step]},"
                        //."avg_rate: {$avg_data[$avg_step]};\n";
                //}
            }
        }
    }


    /// 总结检查结果
    if ($less_flag || $zero_flag || $wrong_flag)
    {
        $warn_desc = '';
        if ($less_flag)
        {
            $warn_desc .= "\n" . '错误类型A：该日期数据缺失: ' . $less_desc;
        }

        if ($zero_flag)
        {
            $warn_desc .= "\n" . '错误类型B：该步骤为0: ' . $zero_desc;
        }

        if ($wrong_flag)
        {
            $warn_desc .= "\n" . '错误类型C：该步骤转化率过低: ' . $wrong_desc;
        }

        do_log("start to send warn mail, desc: $warn_desc");
        return array('result' => -1, 'data' => $checked_data, 'avg_rate' => $avg_data, 'desc' => $warn_desc );
    }
    else
    {
        return array('result' => 0, 'data' => $checked_data, 'avg_rate' => $avg_data);
    }


	//return array('data' => $checked_data, 'avg_rate' => $avg_data);
}

$send_msg = 0;

//// start from here
if($argc < 4)
{
    echo "usage: [发送的最大日期] [发送模式：测试:1, 正式:2] [工作室模式:1, 单项目模式:2]\n";
    exit();
}
else
{
    // 要发送的日期，默认是昨天，也可直接指定
    $compute_day = $argv[1];


    // 发送邮件的模式：工作室模式，单项目模式
    if ($argv[3] == 1)
    {
        // 发送的模式：测试为1，正式为2
        if ($argv[2] == 1)
        {
            $mail_config = $mail_config_studio_t;
        }
        else if ($argv[2] == 2)
        {
            $mail_config = $mail_config_studio_r;
        }
        else
        {
            echo "未知的邮件发送模式(测试：1, 正式: 2): {$argv[2]}\n";
            exit();
        }


        // 按工作室来发送整合数据
        foreach ($game_studio as $studio => $studiovar)
        {
            $mail_charts = array();
            $comment = array();

            /// 正常邮件设置
            // 邮件标题
            $mail_title = "新用户注册转化(步骤间)-" . $studio . '/' . $compute_day;

            // 邮件正文的第一句标题
            $mail_header = mail_studio_header($studio);

            // 邮件正文中 整个工作室的body
            $mail_body = '';

            $all_body = '';

            /// 报警邮件设置
            $warn_title = "警报-新用户注册转化(步骤间)-" . $studio .'/' . $compute_day;

            // 报警邮件的正文
            $warn_body = '';

            // 报警标志
            $warn_flag = FALSE;


            // 工作室内部按照游戏来做
            foreach ($studiovar as $game => $gamevar)
            {
                $result = get_reg_data_monitor($gamevar['gid'], $compute_day);

                if ($result['result'] != 0)
                {
                    $send_msg = 1;
                    $warn_flag = TRUE;
                    $mail_body .= "<span>报警：{$gamevar['name']}</span></br>";
                    $mail_body .= "<span>{$result['desc']}</span>" ;
                    $mail_body .= mail_studio_tablebody($result, $game, $gamevar['name']);
                }
                else
                {
                    $mail_body .= mail_studio_tablebody($result, $game, $gamevar['name']);
                }
            }

            // 如果有某个项目出现问题，则将警报内容发送出来，其它项目也暂缓发送
            if ( TRUE === $warn_flag )
            {
                send_mail($mail_config_studio_warn['to'], $mail_config_studio_warn['cc'], $warn_title, $mail_body, $mail_charts, $comment);
            }
            else
            {

                // 加上对转化率的注释
                #$remark = '注：表中为最近10天的注册转化率(可对比上周同期情况)，若该步骤转化率与最近30天平均值的变化率相比, 低于-2%，红色标注，高于2%，绿色标注。';
                #$mail_body .= "<div class='footer' style='margin:20px 0; font-size: 12px; color:red'>$remark</div></br>";
                $all_body = $mail_header . $mail_body;
                send_mail($mail_config[$studio]['to'], $mail_config[$studio]['cc'], $mail_title, $all_body, $mail_charts, $comment);
            }

        }

    }
    else if ($argv[3] == 2)
    {// 单个项目逐个发送

        // 发送的模式：测试为1，正式为2
        if ($argv[2] == 1)
        {
            $mail_config = $mail_config_t;
        }
        else if ($argv[2] == 2)
        {
            $mail_config = $mail_config_r;
        }
        else
        {
            echo "未知的邮件发送模式(测试：1, 正式: 2): {$argv[2]}\n";
            exit();
        }

        // 逐个游戏发送数据
        foreach ($game_config as $game => $gamevar)
        {
            $mail_charts = array();
            $comment = array();
            $mail_header = mail_header($game, $gamevar['name']);
            $mail_body = $mail_header;
            $result = get_reg_data_monitor($gamevar['gid'], $compute_day);

            if ($result['result'] != 0)
            {
                $send_msg = 1;
                $title = "警报-新用户注册转化-" . $gamevar['name'] .'/' . $compute_day;
                $mail_body .= "<span>{$result['desc']}</span>";
                $mail_body .= mail_tablebody($result, $game, $gamevar['name']);
                send_mail($mail_config_warn['to'], $mail_config_warn['cc'], $title, $mail_body, $mail_charts, $comment);
            }
            else
            {
                $title = "新用户注册转化-" . $gamevar['name'] .'/' . $compute_day;
                $mail_body .= mail_tablebody($result, $game, $gamevar['name']);
                send_mail($mail_config[$game]['to'], $mail_config[$game]['cc'], $title, $mail_body, $mail_charts, $comment);
            }

        }

    }
    else
    {
        echo "未知的邮件内容模式(工作室模式: 1, 单项目模式: 2): {$argv[2]}\n";
        exit();
    }

}

if($send_msg == 1) {
    //发送短信告警
}
?>
