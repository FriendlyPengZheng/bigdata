<?php

error_reporting(E_ALL);
set_time_limit(0);
ini_set('memory_limit', '256M');
ini_set('include_path', ini_get('include_path') . ':../src/third-party/Rmail/:/usr/local/lib/php/');
date_default_timezone_set('Asia/Shanghai');
define('DS', DIRECTORY_SEPARATOR);
define('WORK_DIR' , dirname(__FILE__) . DS );

require_once('wdb_pdo.class.php');

$dbhost = '192.168.71.76';
$dbport = 3306;
$dbuser = 'modelreg';
$dbpass = 'model@pwdtmreg';
$dbname = 'db_td_config';

// 游戏列表
$game_config = array(
    //20141229 停发摩尔庄园
    //'mole' => array('gid'=>1, 'name' => '摩尔庄园'),
    'seer' => array('gid'=>2, 'name' => '赛尔号',),
    'hua' => array('gid'=>5, 'name' => '小花仙'),
    'jl' => array('gid'=>16, 'name' => '热血精灵派'),
    'gongfu' => array('gid'=>6, 'name' => '功夫派'),
    'seer2' => array('gid'=>10, 'name' => '约瑟传说'),
    //'ct' => array('gid'=>19, 'name' => '赤瞳之刃'),
    //'zs' => array('gid'=>14, 'name' => '战神联盟'),
);


// 各工作室的游戏列表
$game_studio = array(
    // 雷伊工作室
    //'雷伊工作室' => array(
        //'seer' => array('gid'=>2, 'name' => '赛尔号',),
        //'seer2' => array('gid'=>10, 'name' => '约瑟传说'),
        //'jl' => array('gid'=>16, 'name' => '热血精灵派'),
        //'mole' => array('gid'=>1, 'name' => '摩尔庄园'),
    //),

    // 零点工作室
    //'零点工作室' => array(
        //'hua' => array('gid'=>5, 'name' => '小花仙'),
        //'gongfu' => array('gid'=>6, 'name' => '功夫派'),
    //),


    // B01淘米游戏事业部
    'B01淘米游戏事业部' => array(
        'seer' => array('gid'=>2, 'name' => '赛尔号',),
        'hua' => array('gid'=>5, 'name' => '小花仙'),
        'jl' => array('gid'=>16, 'name' => '热血精灵派'),
        //'mole' => array('gid'=>1, 'name' => '摩尔庄园'),
        'gongfu' => array('gid'=>6, 'name' => '功夫派'),
        'seer2' => array('gid'=>10, 'name' => '约瑟传说'),
        //'ct' => array('gid'=>19, 'name' => '赤瞳之刃'),
        ),
);

// 工作室收件人正式发送名单
$mail_config_studio_r = array(
    'B01淘米游戏事业部' => array(
        'to' =>'xml@taomee.com, eric@taomee.com,
        lenny@taomee.com,linkin@taomee.com,perfe@taomee.com,jerryqin@taomee.com,
        robert@taomee.com,cristine@taomee.com,sammie@taomee.com,
        miller@taomee.com,lynx@taomee.com' ,

        //'cc' => 'rooney@taomee.com, henry@taomee.com,billy@taomee.com,berry@taomee.com, ping@taomee.com, kendy@taomee.com,
        //lynn@taomee.com, tomli@taomee.com', ),
        'cc' => 'billy@taomee.com, IED-数据平台部 <informplatform@taomee.com>', ),

    //'零点工作室' => array(
        //'to' =>'zeroxue@taomee.com,manafan@taomee.com, cristine@taomee.com' ,

        //'cc' => 'rooney@taomee.com, henry@taomee.com, ping@taomee.com, kendy@taomee.com,
        //berry@taomee.com, lynn@taomee.com', ),
);

// 工作室收件人测试发送名单
$mail_config_studio_t = array(
    'B01淘米游戏事业部' => array(
        //'to' => 'berry@taomee.com, henry@taomee.com, ping@taomee.com, kendy@taomee.com, lynn@taomee.com, tomli@taomee.com' ,
          #'to' => 'ping@taomee.com, kendy@taomee.com, lynn@taomee.com, tomli@taomee.com, shawnluo@taomee.com ' ,
          'to' => 'IED-数据平台部 <informplatform@taomee.com>' ,
        #'to' => 'kendy@taomee.com' ,
        'cc' => '' , ),

    //'雷伊工作室' => array(
        //'to' => 'henry@taomee.com, ping@taomee.com, kendy@taomee.com, berry@taomee.com, lynn@taomee.com,' ,
        //'cc' => '' , ),

    //'零点工作室' => array(
        //'to' => 'henry@taomee.com, ping@taomee.com, kendy@taomee.com, berry@taomee.com, lynn@taomee.com,' ,
        //'cc' => '' , ),
);


// 工作室收件人报警邮件发送名单
$mail_config_studio_warn = array(
        //'to' => 'henry@taomee.com ,ping@taomee.com, kendy@taomee.com, berry@taomee.com, lynn@taomee.com, tomli@taomee.com' ,
        'to' => 'ping@taomee.com, kendy@taomee.com, lynn@taomee.com, looper@taomee.com' ,
        //'to' => 'kendy@taomee.com' ,
    );


// 单项目邮件正式发送名单
$mail_config_r = array(
    //'mole' => array(
    //    'to' => 'xml@taomee.com, cici@taomee.com',
    //    'cc' => 'ping@taomee.com,kendy@taomee.com,lynn@taomee.com',),
    'seer' => array(
        'to' => 'xml@taomee.com',
        'cc' => 'ping@taomee.com,kendy@taomee.com,lynn@taomee.com',),
    'hua' => array(
        'to' => 'zeroxue@taomee.com, cristine@taomee.com',
        'cc' => 'ping@taomee.com,kendy@taomee.com,lynn@taomee.com',),
    'gongfu' => array(
        'to' => 'zeroxue@taomee.com, manafan@taomee.com, mangofu@taomee.com',
        'cc' => 'ping@taomee.com,kendy@taomee.com,lynn@taomee.com',),
    'seer2' => array(
        'to' => 'xml@taomee.com, wangtang@taomee.com',
        'cc' => 'ping@taomee.com,kendy@taomee.com,lynn@taomee.com',),
    'jl' => array(
        'to' => 'xml@taomee.com, lenny@taomee.com, wangtang@taomee.com',
        'cc' => 'ping@taomee.com,kendy@taomee.com,lynn@taomee.com',),
);


// 单项目邮件测试发送名单
$mail_config_t = array(
    //'mole' => array(
    //    'to' => 'ping@taomee.com, kendy@taomee.com, lynn@taomee.com',
    //    'cc' => '',),
    'seer' => array(
        'to' => 'ping@taomee.com, kendy@taomee.com, lynn@taomee.com',
        'cc' => '',),
    'hua' => array(
        'to' => 'ping@taomee.com, kendy@taomee.com, lynn@taomee.com',
        'cc' => '',),
    'gongfu' => array(
        'to' => 'ping@taomee.com, kendy@taomee.com, lynn@taomee.com',
        'cc' => '',),
    'seer2' => array(
        'to' => 'ping@taomee.com, kendy@taomee.com, lynn@taomee.com',
        'cc' => '',),
    'jl' => array(
        'to' => 'ping@taomee.com, kendy@taomee.com, lynn@taomee.com',
        'cc' => '',),
    'zs' => array(
        'to' => 'ping@taomee.com, kendy@taomee.com, lynn@taomee.com',
        'cc' => '',),
);


// 错误告警名单
$mail_config_warn = array(
        'to' => 'ping@taomee.com, kendy@taomee.com, lynn@taomee.com',
        'cc' => '',
    );


$step1 = '注册米米号';
$step2 = '验证密码';
$step3 = '创建角色';
$step4 = '登录online';
$step5 = '落活跃统计';

$g_step_desc = array(
  //'mole' => array($step1, $step2, $step4, $step5, $step3),
  'seer' => array($step1, $step2, $step3, $step4, $step5),
  'hua' => array($step1, $step2, $step3, $step4, $step5),
  'jl' => array($step1, $step2, $step3, $step4, $step5),
  'gongfu' => array($step1, $step2, $step3, $step4, $step5),
  'seer2' => array($step1, $step2, $step3, $step4, $step5),
  //'ct' => array($step1, $step2, $step3, $step4, $step5),

);

// 设置转化率与30天平均值的变化率在超出2%范围内即报警
$g_warn_threshold = 0.03;


function get_db_conn($dbhost, $dbport, $dbuser, $dbpass, $dbname)
{
    // 连接数据库
    $dsn = WDB_PDO::build_dsn($dbname, $dbhost, $dbport);
    $o_db = new WDB_PDO($dsn, $dbuser, $dbpass);
    if (!$o_db->is_right_conn())
    {
        echo "db connect failed!\n";
        return false;
    }

    return $o_db;
}
?>
