<?php /*%%TemplateHeaderCode:8850009795a08f95fcefad8-31586002%%*/ $_valid = $_tpl->decodeProperties(
array (
  'file_dependency' => 
  array (
    '883863ee6892e3cba2398f0309acf10885e591a7' => 
    array (
      0 => '/var/www/tongji.taomee.net/application/view/user/forbidden.html',
      1 => 1484553623,
      2 => 'file',
    ),
  ),
  'no_cache_hash' => '8850009795a08f95fcefad8-31586002',
  'variables' => 
  array (
    'response' => 0,
    'top_bar' => 0,
    'key' => 0,
  ),
  'has_no_cache_code' => false,
  'unifunc' => 'content_5a08f95fdf2b81_01725282',
),
false); /*/%%TemplateHeaderCode%%*/?>
<?php if ($_valid && !is_callable('content_5a08f95fdf2b81_01725282')) { function content_5a08f95fdf2b81_01725282($_tpl) {?><!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <title><?php echo TM::t('tongji','数据分析平台'); ?>
</title>
        <link rel="stylesheet" type="text/css" href="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/css/common.css?ver=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
">
        <link rel="stylesheet" type="text/css" href="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/css/layout.css?ver=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
">
        <link rel="stylesheet" type="text/css" href="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/css/common/themes/<?php echo $_tpl->tplVars['response']->value['current_skin']; ?>
/theme.css?ver=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
">
        <link rel="stylesheet" type="text/css" href="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/css/common/fobbiden.css?ver=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
">
    </head>
    <body>
    <!-- header begin -->
    <div class="header" id="J_header">
        <div class="wrapper">
            <h1 class="logo"><a href="#" title="<?php echo TM::t('tongji','数据分析平台'); ?>
" class="tit"><span class="ir"><?php echo TM::t('tongji','数据分析平台'); ?>
</span></a></h1>
            <!--main nav start-->
            <?php if (!empty($_tpl->tplVars['response']->value['top_bar'])) {?>
            <ul class="main-nav">
                <?php $_tpl->tplVars['top_bar'] = new TMTemplateVariable; $_tpl->tplVars['top_bar']->_loop = false;
$_from = $_tpl->tplVars['response']->value['top_bar']; if (!is_array($_from) && !is_object($_from)) { settype($_from, 'array'); }
foreach ($_from as $_tpl->tplVars['top_bar']->key => $_tpl->tplVars['top_bar']->value) {
$_tpl->tplVars['top_bar']->_loop = true;
?>
                    <?php if (isset($_tpl->tplVars['top_bar']->value['is_main'])&&$_tpl->tplVars['top_bar']->value['is_main']) {?>
                        <li
                        <?php if (isset($_tpl->tplVars['top_bar']->value['current'])&&$_tpl->tplVars['top_bar']->value['current']) {?>
                            class="cur"
                        <?php }?>
                        ><a href="<?php echo $_tpl->tplVars['top_bar']->value['url']; ?>
"><?php echo $_tpl->tplVars['top_bar']->value['name']; ?>
</a></li>
                    <?php }?>
                <?php } ?>
            </ul>
            <?php }?>
            <!--main nav end-->
            <ul class="links">
                <?php if (isset($_tpl->tplVars['response']->value['user']['current_admin_name'])&&!empty($_tpl->tplVars['response']->value['user']['current_admin_name'])) {?>
                <li><?php echo TM::t('tongji','欢迎你，'); ?>
<?php echo $_tpl->tplVars['response']->value['user']['current_admin_name']; ?>
</li>
                <li>|</li>
                <?php if (!empty($_tpl->tplVars['response']->value['top_bar'])) {?>
                    <?php $_tpl->tplVars['top_bar'] = new TMTemplateVariable; $_tpl->tplVars['top_bar']->_loop = false;
$_from = $_tpl->tplVars['response']->value['top_bar']; if (!is_array($_from) && !is_object($_from)) { settype($_from, 'array'); }
foreach ($_from as $_tpl->tplVars['top_bar']->key => $_tpl->tplVars['top_bar']->value) {
$_tpl->tplVars['top_bar']->_loop = true;
?>
                        <?php if (isset($_tpl->tplVars['top_bar']->value['is_main'])&&!$_tpl->tplVars['top_bar']->value['is_main']) {?>
                        <li class="setting"><a href="<?php echo $_tpl->tplVars['top_bar']->value['url']; ?>
"><?php echo $_tpl->tplVars['top_bar']->value['name']; ?>
</a></li>
                        <li>|</li>
                        <?php }?>
                    <?php } ?>
                <?php }?>
                <li class="setting"><a href="index.php?r=user/logout"><?php echo TM::t('tongji','退出'); ?>
</a></li>
                <?php } else { ?>
                <li class="setting"><a href="index.php"><?php echo TM::t('tongji','登录'); ?>
</a></li>
                <?php }?>
            </ul>
        </div>
        <?php if (isset($_tpl->tplVars['response']->value['second_bar'])&&!empty($_tpl->tplVars['response']->value['second_bar'])) {?>
            
        <?php }?>
    </div>
    <div id="J_webSkinSel" class="round-shape web-skin" style="display: none;">
        <i class="arrow"></i>
        <div class="b1"></div>
        <div class="b2"></div>
        <div class="b3"></div>
        <div class="b4"></div>
        <ul class="content">
            <?php $_tpl->tplVars['info'] = new TMTemplateVariable; $_tpl->tplVars['info']->_loop = false;
$_tpl->tplVars['key'] = new TMTemplateVariable;
$_from = $_tpl->tplVars['response']->value['skins']; if (!is_array($_from) && !is_object($_from)) { settype($_from, 'array'); }
foreach ($_from as $_tpl->tplVars['info']->key => $_tpl->tplVars['info']->value) {
$_tpl->tplVars['info']->_loop = true;
$_tpl->tplVars['key']->value = $_tpl->tplVars['info']->key;
?>
            <li class="skin-option
            <?php if ($_tpl->tplVars['response']->value['current_skin']==$_tpl->tplVars['key']->value) {?>
             current
            <?php }?>
            " data-value="<?php echo $_tpl->tplVars['key']->value; ?>
"><i class="skin-color" style="background:url(<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/stat/images/<?php echo $_tpl->tplVars['key']->value; ?>
_skin.png);"></i></li>
            <?php } ?>
        </ul>
        <div class="b4"></div>
        <div class="b3"></div>
        <div class="b2"></div>
        <div class="b5"></div>
    </div>
    <!-- header end -->
    <!-- main begin -->
    <div class="wrapper auth-wrapper">
        <div class="auth-main">
            <span class="title f14b"><?php echo TM::t('tongji','亲，你还没有数据分析平台这一模块的权限哟~~(>_<)~~'); ?>
</span>
            <div class="main">
                <ul class="auth-ul">
                    <li><?php echo TM::t('tongji','请先联系"内审组"的美眉申请相关权限。'); ?>
</li>
                    <li><?php echo TM::t('tongji','权限申请完毕，点击"返回"查看数据。'); ?>
</li>
                </ul>
                <a class="btn-l-dft" href="javascript:window.history.go(-1);"><span><?php echo TM::t('tongji','返回'); ?>
</span></a>
            </div>
        </div>
    </div>
    </body>
</html>
<?php }} ?>
