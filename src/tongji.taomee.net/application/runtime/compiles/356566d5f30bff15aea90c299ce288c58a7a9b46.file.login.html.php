<?php /*%%TemplateHeaderCode:12436299059f2aa5c532fb9-35488532%%*/ $_valid = $_tpl->decodeProperties(
array (
  'file_dependency' => 
  array (
    '356566d5f30bff15aea90c299ce288c58a7a9b46' => 
    array (
      0 => '/var/www/tongji.taomee.net/application/view/mobile/login.html',
      1 => 1509074362,
      2 => 'file',
    ),
  ),
  'no_cache_hash' => '12436299059f2aa5c532fb9-35488532',
  'has_no_cache_code' => false,
  'unifunc' => 'content_59f2aa5c538318_80504939',
),
false); /*/%%TemplateHeaderCode%%*/?>
<?php if ($_valid && !is_callable('content_59f2aa5c538318_80504939')) { function content_59f2aa5c538318_80504939($_tpl) {?><!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	<!--<meta name="viewport" content="initial-scale=1, maximum-scale=1, minimum-scale=1, user-scalable=no">-->
	<meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="keywords" content="">
    <meta name="description" content="">
    <title>淘米数据分析平台手机版</title>
	<link rel="stylesheet" href="css/pure/pure-min.css">
	<link rel="stylesheet" href="css/mtj/public.css">
	<link rel="stylesheet" href="css/mtj/mtj.css">
	<link rel="stylesheet" href="css/font-awesome/css/font-awesome.min.css">
</head>
<body>

<div class="login-div">
	<form class="pure-form pure-form-aligned">
		<fieldset class="w80per login-field pure-group">
			<legend class="f20">淘米数据分析平台手机版<i class="fa fa-sign-in"></i></legend>
			<div class="err-hint"><i class="fa fa-exclamation-triangle"></i><span class="err-text"><span></div>
			<input type="text"     id="J_userName" name="user" class="pure-input-1-2" placeholder="你的RTX英文全名"  />
			<input type="password" id="J_pwd"      name="pwd"  class="pure-input-1-2" placeholder="你的电脑开机密码" />
			<button id="J_loginBtn" class="pure-button pure-input-1-2 pure-button-primary submit-btn">登&nbsp;&nbsp;录</button>
		</fieldset>
		
	</form>
</div>

<script src="https://code.jquery.com/jquery-2.2.4.min.js" type="text/javascript" charset="utf-8"></script>
<script src="js/public.js" type="text/javascript" charset="utf-8"></script>
<script src="js/login.js" type="text/javascript" charset="utf-8"></script>
</body>
</html>
<?php }} ?>
