<?php /*%%TemplateHeaderCode:12547651015cb6a3ac6005a7-13633297%%*/ $_valid = $_tpl->decodeProperties(
array (
  'file_dependency' => 
  array (
    'feb8e3b5c4eda0036632ba6d7e9e3c68430601d6' => 
    array (
      0 => '/opt/home/sevin/project/tongji.taomee.net/application/view/user/login.html',
      1 => 1534838549,
      2 => 'file',
    ),
  ),
  'no_cache_hash' => '12547651015cb6a3ac6005a7-13633297',
  'variables' => 
  array (
    'response' => 0,
  ),
  'has_no_cache_code' => false,
  'unifunc' => 'content_5cb6a3ac636187_43154596',
),
false); /*/%%TemplateHeaderCode%%*/?>
<?php if ($_valid && !is_callable('content_5cb6a3ac636187_43154596')) { function content_5cb6a3ac636187_43154596($_tpl) {?><!doctype html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <meta name="robots" content="nofollow,noindex">
    <title><?php echo TM::t('tongji','数据分析平台'); ?>
</title>
    <link rel="Shortcut Icon" type="image/x-icon" href="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/images/tongji.png">
    <link rel="stylesheet" type="text/css" href="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/css/common.css?ver=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
">
    <link rel="stylesheet" href="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/css/page/user/login.css">
    <script type="text/javascript">
    window.responseData = {
        isRelease: "<?php echo $_tpl->tplVars['response']->value['application_info']['is_release']; ?>
",
        locale: "<?php echo $_tpl->tplVars['response']->value['application_info']['locale']; ?>
"
    };
    </script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/jquery.min.js"></script>
</head>
<body>
<div class="banner">
    <div class="wrapper">
        <a href="/" class="logo"><img src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/images/title.png" alt="<?php echo TM::t('tongji','数据分析平台'); ?>
" width="244" height="42"></a>
        <div class="login-container">
            <form class="frm login-form" autocomplete="on" id="J_loginForm" name="loginform" method="post">
                <div class="frm-ln frm-name">
                    <label for="uid" id="label_uid" data-dft="<?php echo TM::t('tongji','请输入用户名'); ?>
" class="frm-txt-dft"><?php echo TM::t('tongji','请输入用户名'); ?>
</label><input id="uid" type="text" name="uid" tabindex="1" class="frm-txt">
                </div>
                <div class="frm-ln frm-pwd">
                    <label for="passwd" id="label_passwd" data-dft="<?php echo TM::t('tongji','请输入密码'); ?>
" class="frm-txt-dft"><?php echo TM::t('tongji','请输入密码'); ?>
</label><input id="passwd" type="password" name="passwd" tabindex="3" class="frm-txt">
                </div>
                <input type="hidden" id="J_isVericode" value=" <?php if ($_tpl->tplVars['response']->value['need_vericode']) {?> 1 <?php } else { ?> 0 <?php }?> " />
                <div id="J_vericodeContainer" class="frm-ln clearfix" style="display: none;">
                    <div class="vericode-input">
                        <label for="vericode" id="label_vericode" data-dft=<?php echo TM::t('tongji','"验证码'); ?>
" class="frm-txt-dft vericode"><?php echo TM::t('tongji','验证码'); ?>
</label><input id="vericode" type="text" name="vericode" tabindex="4" class="frm-txt">
                    </div>
                    <div class="vericode-img">
                        <img id="imgvericode" alt="<?php echo TM::t('tongji','验证码'); ?>
" src="" title="<?php echo TM::t('tongji','点击换一个'); ?>
"></div>
                </div>
                <div class="frm-ln">
                    <input id="J_loginBtn" type="submit" name="loginBtn" tabindex="5" class="login-btn" value="<?php echo TM::t('tongji','登录'); ?>
">
                </div>
            </form>
            <p class="lang-switcher"><a href="/">中文</a><span>|</span><a href="/en/">English</a></p>
        </div>
    </div>
</div>
<div class="horizon"></div>
<div class="footer">
    <div class="wrapper">
        <a title="淘米网" class="ft-logo" href="http://www.taomee.com/" rel="nofollow" target="_blank">Taomee 淘米</a>
        <div class="ft-remark">
            <div class="ft-link">
                <a title="关于淘米" href="http://www.taomee.com/info/" rel="nofollow" target="_blank">关于淘米</a><i class="divide">|</i>
                <a title="联系我们" href="http://www.taomee.com/contact/" rel="nofollow" target="_blank">联系我们</a><i class="divide">|</i>
                <a title="淘米招聘" href="http://www.taomee.com/hr/tech/" rel="nofollow" target="_blank">淘米招聘</a><i class="divide">|</i>
                <a title="父母须知" href="http://www.61.com/about/parents.html" rel="nofollow" target="_blank">父母须知</a><i class="divide">|</i>
                <a title="儿童安全上网" href="http://www.61.com/about/children.html" rel="nofollow" target="_blank">儿童安全上网</a><i class="divide">|</i>
                <a title="绿色宣言" href="http://www.61.com/about/green.html" rel="nofollow" target="_blank">绿色宣言</a><i class="divide">|</i>
                <a title="意见反馈" href="http://service.61.com/user" rel="nofollow" target="_blank">意见反馈</a><i class="divide">|</i>
                <a title="淘米客服" href="http://service.61.com/" rel="nofollow" target="_blank">淘米客服</a>
            </div>
            <div class="ft-icp">增值电信业务许可经营证：沪B2-20090070  文网文[2009]093号  互联网出版许可证 新出网证(沪)字023号</div>
            <div class="ft-copyright">服务热线：021-61130888 上海淘米网络科技有限公司 Copyright©2008-2012 TaoMee Inc. All Rights Reserved</div>
        </div>
        <a title="上海网警" class="ft-netpolice" href="http://sh.cyberpolice.cn/" rel="nofollow" target="_blank">上海网警</a>
    </div>
</div>
<script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/util.js?ver=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
<?php if ('zh_CN'!==$_tpl->tplVars['response']->value['application_info']['locale']) {?>
<script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/lang/<?php echo $_tpl->tplVars['response']->value['application_info']['locale']; ?>
.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
<?php }?>
<script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/page/user/tm.login.js"></script>
</body>
</html>
<?php }} ?>
