<?php
//单引号表示字符串，双引号表示包含的符号可以解析
//debine函数定义一个变量
$DB_HOST = '192.168.71.76';
$DB_PORT = '3306';
$DB_USER = 'bigdbdata';
$DB_PWD = 'data@21011420';
$DB_NAME = 'db_td_config';
//建立数据库连接
$sDsn = 'mysql:dbname=' . $DB_NAME . ';host=' . $DB_HOST . ';port=' . $DB_PORT . ':charset=utf8';
try{
	$oPdo = new PDO($sDsn, $DB_USER, $DB_PWD, array(
		PDO::MYSQL_ATTR_INIT_COMMAND => 'SET NAMES utf8',
		PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION
	));}catch(PDOException $e){
		echo "连接错误：".$e->getMessage()."\n行号：".$e->getLine()."\n";
		die();
}
echo "have linked!\n";

/****************************
 * 根据task_id找数据
 * *************************/
//三个mysql服务器
$mySqlHost = array(
	'192.168.71.72',
	'192.168.71.43',
	'192.168.71.76'
);
$data_id = '';
$db = '';
$tb = '';
$time = '1550281600'; #20151217

//选择数据库
$oPdo->exec('USE ' . $DB_NAME);
//确定taskId 和 gameId
$taskId = 14;
$gameId = 650;
#$GPZS_ID = '6863';

//查找gpzs值
$sSql = "(select gpzs_id from t_gpzs_info where game_id = $gameId) ";
echo "$sSql\n";
//生成一个查询对象
$oStatment = $oPdo->prepare($sSql);
//处理一个准备好的查询对象 === 是指变量值和类型完全相等
if ($oStatment->execute(array('unknown')) === false) {
    die('Could not execute ' . $sSql . ': [' . $oStatment->errorCode() . ']' . $oStatment->errorInfo());
}
//$row = $oStatment->fetch();
//这个地方取得数据不一定唯一，不唯一的时候怎么搞？
while ($row = $oStatment->fetch(PDO::FETCH_ASSOC)) {
    printf("gpzs_id: %s\n", $row['gpzs_id']);
	//截取sthash最后四个字符，得到库和表
    #echo "sthash = {$row['sthash']}\n"; //输出数组时加上大括号
    $GPZS_ID = $row['gpzs_id'];
	echo "GPZS_ID = $GPZS_ID\n";
    process($oPdo, $gameId, $GPZS_ID, $time, $mySqlHost);
    ############################
   
}

$oStatment->closeCursor();
//断开连接
$oPdo = null;
function process($oPdo, $gameId, $GPZS_ID, $time, $mySqlHost)
{
	//查找data_id和hash值
	$sSql = "(select data.data_id, data.sthash, report.game_id from t_data_info data left join t_report_info report on data.r_id = report.report_id where data.type = 'report' and report.game_id = $gameId) ";
	$sSql.=	"union all ";
	$sSql.= "(select data.data_id, data.sthash, result.game_id from t_data_info data left join t_common_result result on data.r_id = result.result_id where data.type = 'result' and result.game_id = $gameId)";
	#$sSql.= "WHERE data.type = 2 AND result.game_id = $gameId AND result.task_id = $taskId";
	echo "$sSql\n";
	//生成一个查询对象
	$oStatment = $oPdo->prepare($sSql);
	//处理一个准备好的查询对象 === 是指变量值和类型完全相等
	if ($oStatment->execute(array('unknown')) === false) {
	    die('Could not execute ' . $sSql . ': [' . $oStatment->errorCode() . ']' . $oStatment->errorInfo());
	}
	//$row = $oStatment->fetch();
	//这个地方取得数据不一定唯一，不唯一的时候怎么搞？
	while ($row = $oStatment->fetch(PDO::FETCH_ASSOC)) {
	    //printf("data_id: %s  sthash: %s game: %s\n", $row['data_id'],$row['sthash'],$row['game_id']);
		//截取sthash最后四个字符，得到库和表
	    #echo "sthash = {$row['sthash']}\n"; //输出数组时加上大括号
		$data_id = $row['data_id'];
		$db_tb = $row['sthash'] % 10000;
		$db = (int)($db_tb / 100);
		$tb = $db_tb % 100;
		//echo "db_tb = $db_tb  db = $db  tb = $tb\n";
		//根据库表查询相应的数据
		$temp = (int)($db / 30);
		if($temp == 3)
		{
			$temp = 2;
		}
	
		#$SqlHost = $mySqlHost[(int)($db / 30)];
		$SqlHost = $mySqlHost[$temp];
		if($db>=30 and $db<=44)
		{
			$SqlHost ='192.168.71.20';
		}
		//echo "SqlHost = $SqlHost\n";
		update_value($SqlHost,$db,$tb,$GPZS_ID,$data_id,$time);

		//echo $mySqlHost[(int)($td / 30)];
	}

	$oStatment->closeCursor();
}
#####################################################################
function update_value($SqlHost,$db,$tb,$GPZS_ID,$data_id,$time)
{
	$DB_HOST = $SqlHost;
	$DB_PORT = '3306';
	$DB_USER = 'bigdbdata';
	$DB_PWD = 'data@21011420';
	$DB_NAME = 'db_td_data_'.$db;
	$TB_NAME = 't_db_data_day_'.$tb;
	//建立数据库连接
	$sDsn = 'mysql:dbname=' . $DB_NAME . ';host=' . $DB_HOST . ';port=' . $DB_PORT . ':charset=utf8';
	try{
		$oPdo = new PDO($sDsn, $DB_USER, $DB_PWD, array(
			PDO::MYSQL_ATTR_INIT_COMMAND => 'SET NAMES utf8',
			PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION
		));}catch(PDOException $e){
			echo "连接错误：".$e->getMessage()."\n行号：".$e->getLine()."\n";
			die();
	}
	#echo "have linked!\n";

	/****************************
	 * 根据task_id找数据
	 * *************************/
	//选择数据库
	$oPdo->exec('USE ' . $DB_NAME);
	/*
	//查找数据
	$sSql = "SELECT gpzs_id,data_id,time,value FROM $TB_NAME ";
	$sSql.=	"WHERE gpzs_id= $GPZS_ID and data_id= $data_id and time <= $time";
	#echo "$sSql\n";
	//生成一个查询对象
	$oStatment = $oPdo->prepare($sSql);
	//处理一个准备好的查询对象 === 是指变量值和类型完全相等
	if ($oStatment->execute(array('unknown')) === false) {
	    die('Could not execute ' . $sSql . ': [' . $oStatment->errorCode() . ']' . $oStatment->errorInfo());
	}
	//$row = $oStatment->fetch();
	//这个地方取得数据不一定唯一，不唯一的时候怎么搞？
	while ($row = $oStatment->fetch(PDO::FETCH_ASSOC)) {
	    printf("gpzs_id: %s  data_id: %s time: %s value: %s\n", $row['gpzs_id'],$row['data_id'],$row['time'],$row['value']);

	}

	$oStatment->closeCursor();
	 */
	//删除数据就没有权限，更新数据
	$dSql = "update $TB_NAME set value = 0 ";
	$dSql.= "WHERE gpzs_id= $GPZS_ID and data_id= $data_id and time <= $time";
	echo "$dSql\n";
	$oPdo->query($dSql);
	//断开连接
	$oPdo = null;
 
}
