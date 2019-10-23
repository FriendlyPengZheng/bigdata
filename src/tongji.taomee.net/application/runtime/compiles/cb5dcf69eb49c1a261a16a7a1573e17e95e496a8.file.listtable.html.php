<?php /*%%TemplateHeaderCode:16224464905b7e12e7a94322-41893253%%*/ $_valid = $_tpl->decodeProperties(
array (
  'file_dependency' => 
  array (
    'cb5dcf69eb49c1a261a16a7a1573e17e95e496a8' => 
    array (
      0 => '/opt/home/sevin/project/tongji.taomee.net/application/view/gameanalysis/listtable.html',
      1 => 1534838549,
      2 => 'file',
    ),
    'c9c37a022201966430ba81cdfeef026bac181013' => 
    array (
      0 => '/opt/home/sevin/project/tongji.taomee.net/application/view/layout/layout.html',
      1 => 1534838549,
      2 => 'file',
    ),
    'ff8cffde5c75877d4512439b1decb394829a21e0' => 
    array (
      0 => '/opt/home/sevin/project/tongji.taomee.net/application/view/layout/sub-nav-blank.html',
      1 => 1534838549,
      2 => 'file',
    ),
    'a19242bc541357e54076540e0ad0d297d189f115' => 
    array (
      0 => '/opt/home/sevin/project/tongji.taomee.net/application/view/gameanalysis/sub-nav.html',
      1 => 1534838549,
      2 => 'file',
    ),
    '055d7b66470f8c13ef8b25449a9d57ee4accb3b3' => 
    array (
      0 => '/opt/home/sevin/project/tongji.taomee.net/application/view/gameanalysis/aside.html',
      1 => 1534838549,
      2 => 'file',
    ),
  ),
  'no_cache_hash' => '16224464905b7e12e7a94322-41893253',
  'variables' => 
  array (
    'response' => 0,
    'd' => 0,
    'top_bar' => 0,
    'key' => 0,
  ),
  'has_no_cache_code' => false,
  'unifunc' => 'content_5b7e12e7c84d20_66826910',
),
false); /*/%%TemplateHeaderCode%%*/?>
<?php if ($_valid && !is_callable('content_5b7e12e7c84d20_66826910')) { function content_5b7e12e7c84d20_66826910($_tpl) {?><!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <meta name="robots" content="nofollow,noindex">
        <title><?php echo $_tpl->tplVars['response']->value['application_info']['name']; ?>
-<?php echo TM::t('tongji','游戏分析'); ?>
</title>
        <link rel="Shortcut Icon" type="image/x-icon" href="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/images/tongji.png">
        <link rel="Bookmark" href="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/images/tongji.png">
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
/data/css/common/common.css?ver=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
">
        <link rel="stylesheet" type="text/css" href="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/fonts/iconfont.css?ver=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
">
        
<!--[if lt IE 8]>
<link rel="stylesheet" type="text/css" href="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/css/layout.ie.css">
<script type="text/javascript">
function calc() {
    document.getElementsByTagName("body")[0].style.width  = document.body.clientWidth < 1000 ? "1000px" : "auto";
}
onbeforeprint = function() { document.getElementsByTagName("body")[0].style.width = "100%"; }
onafterprint = calc;
</script>
<![endif]-->
        <script type="text/javascript">
        window.responseData = {
            userId: <?php echo $_tpl->tplVars['response']->value['user']['current_admin_id']; ?>
,
            userName: "<?php echo $_tpl->tplVars['response']->value['user']['current_admin_name']; ?>
",
            isAdmin: <?php if ($_tpl->tplVars['response']->value['user']['is_super_admin']) {?>1<?php } else { ?>0<?php }?>,
            isRelease: "<?php echo $_tpl->tplVars['response']->value['application_info']['is_release']; ?>
",
            locale: "<?php echo $_tpl->tplVars['response']->value['application_info']['locale']; ?>
",
            resServer: "<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
",
            skin: "<?php echo $_tpl->tplVars['response']->value['current_skin']; ?>
",
            currentPage : {
                ignore : [],
                <?php if (isset($_tpl->tplVars['response']->value['current_page'])) {?>
                parent : "<?php echo $_tpl->tplVars['response']->value['current_page']['parent']; ?>
",
                child : "<?php echo $_tpl->tplVars['response']->value['current_page']['child']; ?>
"
                <?php }?>
            }
        };
        <?php if (isset($_tpl->tplVars['response']->value['ignore'])) {?>
            <?php $_tpl->tplVars['d'] = new TMTemplateVariable; $_tpl->tplVars['d']->_loop = false;
$_from = $_tpl->tplVars['response']->value['ignore']; if (!is_array($_from) && !is_object($_from)) { settype($_from, 'array'); }
foreach ($_from as $_tpl->tplVars['d']->key => $_tpl->tplVars['d']->value) {
$_tpl->tplVars['d']->_loop = true;
?>
                window.responseData.currentPage.ignore.push(<?php echo $_tpl->tplVars['d']->value; ?>
);
            <?php } ?>
        <?php }?>
        window.gLoading = false;
        </script>
        <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/jquery.min.js"></script>
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
                    <?php if (in_array($_tpl->tplVars['top_bar']->value['name'],array())) {?>
                        <li
                        <?php if (isset($_tpl->tplVars['top_bar']->value['current'])&&$_tpl->tplVars['top_bar']->value['current']) {?>
                            class="cur"
                        <?php }?>
                        data-key="<?php echo $_tpl->tplVars['top_bar']->value['key']; ?>
"><i class="J-new-function" style="top:12px;left:505px;"></i><a href="<?php echo $_tpl->tplVars['top_bar']->value['url']; ?>
"><?php echo $_tpl->tplVars['top_bar']->value['name']; ?>
</a></li>
                     <?php } else { ?>
                        <li
                        <?php if (isset($_tpl->tplVars['top_bar']->value['current'])&&$_tpl->tplVars['top_bar']->value['current']) {?>
                            class="cur"
                        <?php }?>
                         data-key="<?php echo $_tpl->tplVars['top_bar']->value['key']; ?>
"><a href="<?php echo $_tpl->tplVars['top_bar']->value['url']; ?>
"><?php echo $_tpl->tplVars['top_bar']->value['name']; ?>
</a></li>
                     <?php }?>
                    <?php }?>
                <?php } ?>
            </ul>
            <?php }?>
            <!--main nav end-->
            <ul class="links">
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
                <?php if ($_tpl->tplVars['response']->value['user']['is_guest']) {?>
                    <li>|</li>
                    <li class="setting"><a href="javascript: void(0);" id="J_passwd"><?php echo TM::t('tongji','更改密码'); ?>
</a></li>
                <?php }?>
                <?php if ($_tpl->tplVars['response']->value['application_info']['localeSwitchable']) {?>
                    <?php if ($_tpl->tplVars['response']->value['application_info']['locale']==='zh_CN') {?>
                        <li>|</li>
                        <li class="setting"><a href="/en/">English</a></li>
                    <?php } elseif ($_tpl->tplVars['response']->value['application_info']['locale']==='en_US') {?>
                        <li>|</li>
                        <li class="setting"><a href="/">中文</a></li>
                    <?php }?>
                <?php }?>
            </ul>
        </div>
        <?php /* Call merged included template "gameanalysis/sub-nav.html" */
$_tplStack[] = $_tpl;
$_tpl = $_tpl->setupInlineSubTemplate("gameanalysis/sub-nav.html", $_tpl->cacheId, $_tpl->compileId, 0, null, array(), 0, '16224464905b7e12e7a94322-41893253');
content_5b7e12e7b46e01_90972216($_tpl);
$_tpl = array_pop($_tplStack);
/* End of included template "gameanalysis/sub-nav.html" */?>
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
    
<div class="main with-aside">
    <?php /* Call merged included template "gameanalysis/aside.html" */
$_tplStack[] = $_tpl;
$_tpl = $_tpl->setupInlineSubTemplate("gameanalysis/aside.html", $_tpl->cacheId, $_tpl->compileId, 0, null, array(), 0, '16224464905b7e12e7a94322-41893253');
content_5b7e12e7b9be78_36203340($_tpl);
$_tpl = array_pop($_tplStack);
/* End of included template "gameanalysis/aside.html" */?>
    <div class="tips" id="J_timeTips" style="display: none;">
        <a class="tips-close" href="javascript:void(0);">X</a>
        <div class="tips-text"><span class="tit"><?php echo TM::t('tongji','注意：'); ?>
</span><?php echo TM::t('tongji','页面只能显示{1}时间段的数据，下载数据时间段没有限制哦。',array('{1}'=>sprintf('<span class="tit period">%s~%s</span>',$_tpl->tplVars['response']->value['param']['from'],$_tpl->tplVars['response']->value['param']['to']))); ?>
</div>
    </div>
    <div class="content clearfix">
        <input type="hidden" id="J_paramGameId" value="<?php echo $_tpl->tplVars['response']->value['param']['game_id']; ?>
" />
        <input type="hidden" id="J_paramPlatformId" value="<?php echo $_tpl->tplVars['response']->value['param']['platform_id']; ?>
" />
        <input type="hidden" id="J_paramZoneId" value="<?php echo $_tpl->tplVars['response']->value['param']['zone_id']; ?>
" />
        <input type="hidden" id="J_paramServerId" value="<?php echo $_tpl->tplVars['response']->value['param']['server_id']; ?>
" />
        <input type="hidden" id="J_r" value="<?php echo $_tpl->tplVars['response']->value['param']['r']; ?>
" />
        <div class="content-header clearfix" id="J_contentHeader">
            <div class="select-con" id="J_zoneServer" tabindex="0">
                <div class="select-title"><?php echo TM::t('tongji','请选择：'); ?>
</div>
            </div>
            <select id="J_platform" class="r">
            <?php if (isset($_tpl->tplVars['response']->value['platform'])&&!empty($_tpl->tplVars['response']->value['platform'])) {?>
            <?php $_tpl->tplVars['platform'] = new TMTemplateVariable; $_tpl->tplVars['platform']->_loop = false;
$_from = $_tpl->tplVars['response']->value['platform']; if (!is_array($_from) && !is_object($_from)) { settype($_from, 'array'); }
foreach ($_from as $_tpl->tplVars['platform']->key => $_tpl->tplVars['platform']->value) {
$_tpl->tplVars['platform']->_loop = true;
?>
                <?php if ($_tpl->tplVars['response']->value['param']['platform_id']==$_tpl->tplVars['platform']->value['platform_id']) {?>
                <option data-id="<?php echo $_tpl->tplVars['platform']->value['platform_id']; ?>
" selected><?php echo $_tpl->tplVars['platform']->value['gpzs_name']; ?>
</option>
                <?php } else { ?>
                <option data-id="<?php echo $_tpl->tplVars['platform']->value['platform_id']; ?>
"><?php echo $_tpl->tplVars['platform']->value['gpzs_name']; ?>
</option>
                <?php }?>
            <?php } ?>
            <?php } else { ?>
                <option><?php echo TM::t('tongji','选择平台：'); ?>
</option>
            <?php }?>
            </select>
            <div class="datepicker-trigger radius5-all r mr5" id="J_from_to" >
                <input type="hidden" id="J_from" value="<?php echo $_tpl->tplVars['response']->value['param']['from']; ?>
" />
                <input type="hidden" id="J_to" value="<?php echo $_tpl->tplVars['response']->value['param']['to']; ?>
" />
                <input type="hidden" id="J_showFrom" value="<?php echo $_tpl->tplVars['response']->value['param']['from']; ?>
" />
                <input type="hidden" id="J_showTo" value="<?php echo $_tpl->tplVars['response']->value['param']['to']; ?>
" />
                <i class="datepicker-icon"></i>
                <input class="title" type="text" id="J_date" value="<?php echo $_tpl->tplVars['response']->value['param']['from']; ?>
~<?php echo $_tpl->tplVars['response']->value['param']['to']; ?>
" />
                <i class="datepicker-arrow"></i>
            </div>
        </div>
        <div class="content-body" id="J_contentBody" >
        </div>
    </div>
</div>

    <!-- main end -->
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/dlg.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/util.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <?php if ('zh_CN'!==$_tpl->tplVars['response']->value['application_info']['locale']) {?>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/lang/<?php echo $_tpl->tplVars['response']->value['application_info']['locale']; ?>
.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <?php }?>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/layout.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>	
    <!-- here you can add some page javascript -->
    
    <!-- here you can add some page javascript -->
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/select.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/navigator.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/datepick.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/Page.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/tm.tabs.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/draw/highstock.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/tm.draw.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/jquery.choose.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/tm.datatable.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/common.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/stat.table.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/page/gameanalysis/addtofavor.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/tm.module.ok.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/usage.ok.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/page/gameanalysis/listtable.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>

    </body>
</html>
<?php }} ?>
<?php if ($_valid && !is_callable('content_5b7e12e7b36cd1_84240397')) { function content_5b7e12e7b36cd1_84240397($_tpl) {?><!-- 不含任何二级nav -->
<div class="wrapper">
    <ul class="main-sub-nav">
        <?php if (isset($_tpl->tplVars['response']->value['admin_auth'])) {?>
        <li class="sub-nav-li posr stat-button" title="模板管理">
        <a href="index.php?r=admin/manage/displayPage&page_url=<?php echo $_tpl->tplVars['response']->value['param']['r']; ?>
&navi_key=<?php echo $_tpl->tplVars['response']->value['current']['pageKey']; ?>
&module_name=<?php echo $_tpl->tplVars['response']->value['current']['pageTitle']; ?>
" class="bar-manage"><i class="iconfont icon-config"></i><?php echo TM::t('tongji','模板管理'); ?>
</a>
        </li>
        <?php }?>
     </ul>
</div>
<?php }} ?>
<?php if ($_valid && !is_callable('content_5b7e12e7b46e01_90972216')) { function content_5b7e12e7b46e01_90972216($_tpl) {?><div class="wrapper">
    <ul class="main-sub-nav">
        <li class="sub-nav-li">
            <select id="J_game">
            <?php $_tpl->tplVars['game'] = new TMTemplateVariable; $_tpl->tplVars['game']->_loop = false;
$_tpl->tplVars['key'] = new TMTemplateVariable;
$_from = $_tpl->tplVars['response']->value['game']; if (!is_array($_from) && !is_object($_from)) { settype($_from, 'array'); }
foreach ($_from as $_tpl->tplVars['game']->key => $_tpl->tplVars['game']->value) {
$_tpl->tplVars['game']->_loop = true;
$_tpl->tplVars['key']->value = $_tpl->tplVars['game']->key;
?>
                <?php if ($_tpl->tplVars['response']->value['param']['game_id']==$_tpl->tplVars['key']->value) {?>
                <option data-id="<?php echo $_tpl->tplVars['key']->value; ?>
" data-href="<?php echo $_tpl->tplVars['game']->value['url']; ?>
" selected="true" data-key="<?php echo $_tpl->tplVars['game']->value['game_type']; ?>
"><?php echo $_tpl->tplVars['game']->value['game_name']; ?>
<?php if ($_tpl->tplVars['game']->value['game_type']=="webgame") {?><?php echo TM::t('tongji','【页游】'); ?>
<?php } elseif ($_tpl->tplVars['game']->value['game_type']=="mobilegame") {?><?php echo TM::t('tongji','【手游】'); ?>
<?php } elseif ($_tpl->tplVars['game']->value['game_type']=="site") {?><?php echo TM::t('tongji','【网站】'); ?>
<?php } elseif ($_tpl->tplVars['game']->value['game_type']=="clientgame") {?><?php echo TM::t('tongji','【端游】'); ?>
<?php } else { ?><?php echo TM::t('tongji','【测试】'); ?>
<?php }?></option>
                </option>
                <?php } else { ?>
                <option data-id="<?php echo $_tpl->tplVars['key']->value; ?>
" data-href="<?php echo $_tpl->tplVars['game']->value['url']; ?>
" data-key="<?php echo $_tpl->tplVars['game']->value['game_type']; ?>
"><?php echo $_tpl->tplVars['game']->value['game_name']; ?>
<?php if ($_tpl->tplVars['game']->value['game_type']=="webgame") {?><?php echo TM::t('tongji','【页游】'); ?>
<?php } elseif ($_tpl->tplVars['game']->value['game_type']=="mobilegame") {?><?php echo TM::t('tongji','【手游】'); ?>
<?php } elseif ($_tpl->tplVars['game']->value['game_type']=="site") {?><?php echo TM::t('tongji','【网站】'); ?>
<?php } elseif ($_tpl->tplVars['game']->value['game_type']=="clientgame") {?><?php echo TM::t('tongji','【端游】'); ?>
<?php } else { ?><?php echo TM::t('tongji','【测试】'); ?>
<?php }?></option>
                </option>
                 <?php }?>
            <?php } ?>
            </select>
        </li>
        <?php if (isset($_tpl->tplVars['response']->value['admin_auth'])) {?>
        <li class="sub-nav-li posr stat-button" title="模板管理">
        <a href="index.php?r=admin/manage/displayPage&page_url=<?php echo $_tpl->tplVars['response']->value['param']['r']; ?>
&navi_key=<?php echo $_tpl->tplVars['response']->value['current']['pageKey']; ?>
&module_name=<?php echo $_tpl->tplVars['response']->value['current']['pageTitle']; ?>
" class="bar-manage"><i class="iconfont icon-config"></i><?php echo TM::t('tongji','模板管理'); ?>
</a>
		</li>
        <?php }?>
    </ul>
</div>
<?php }} ?>
<?php if ($_valid && !is_callable('content_5b7e12e7b9be78_36203340')) { function content_5b7e12e7b9be78_36203340($_tpl) {?><div class="aside" id="J_aside">
    <ul class="bt">
    <?php $_tpl->tplVars['aside'] = new TMTemplateVariable; $_tpl->tplVars['aside']->_loop = false;
$_from = $_tpl->tplVars['response']->value['aside']; if (!is_array($_from) && !is_object($_from)) { settype($_from, 'array'); }
foreach ($_from as $_tpl->tplVars['aside']->key => $_tpl->tplVars['aside']->value) {
$_tpl->tplVars['aside']->_loop = true;
?>
        <?php if (!isset($_tpl->tplVars['response']->value['ignore'])||!in_array($_tpl->tplVars['aside']->value['navi_id'],$_tpl->tplVars['response']->value['ignore'])) {?>
            <?php if (in_array($_tpl->tplVars['aside']->value['name'],array())) {?>
                <li data-key="<?php echo $_tpl->tplVars['aside']->value['key']; ?>
" class="parent rel
                <?php if (isset($_tpl->tplVars['aside']->value['current'])&&$_tpl->tplVars['aside']->value['current']) {?>
                    cur clicked
                <?php }?>
                <?php if (isset($_tpl->tplVars['aside']->value['children'])&&$_tpl->tplVars['aside']->value['children']) {?>
                    more-icon
                    ">
                    <a
                <?php } else { ?>
                    stat-module" common="游戏分析" stid="<?php echo $_tpl->tplVars['aside']->value['name']; ?>
" sstid="<?php echo $_tpl->tplVars['aside']->value['name']; ?>
">
                    <a href="<?php echo $_tpl->tplVars['aside']->value['url']; ?>
&game_id=<?php echo $_tpl->tplVars['response']->value['param']['game_id']; ?>
"
                <?php }?>
                >
                <?php if (isset($_tpl->tplVars['aside']->value['key'])&&$_tpl->tplVars['aside']->value['key']) {?>
                    <i class="iconfont icon-<?php echo $_tpl->tplVars['aside']->value['key']; ?>
"></i>
                <?php }?>
                <span><?php echo $_tpl->tplVars['aside']->value['name']; ?>
</span><i class="J-new-function" style="right: 66px; top: 22px;"></i></a>
            <?php } else { ?>
            <li data-key="<?php echo $_tpl->tplVars['aside']->value['key']; ?>
" class="parent
            <?php if (isset($_tpl->tplVars['aside']->value['current'])&&$_tpl->tplVars['aside']->value['current']) {?>
            cur clicked
            <?php }?>
            <?php if (isset($_tpl->tplVars['aside']->value['children'])&&$_tpl->tplVars['aside']->value['children']) {?>
            more-icon
            ">
                <a
            <?php } else { ?>
            stat-module" common="游戏分析" stid="<?php echo $_tpl->tplVars['aside']->value['name']; ?>
" sstid="<?php echo $_tpl->tplVars['aside']->value['name']; ?>
">
                <a href="<?php echo $_tpl->tplVars['aside']->value['url']; ?>
&game_id=<?php echo $_tpl->tplVars['response']->value['param']['game_id']; ?>
"
            <?php }?>
                >
                <?php if (isset($_tpl->tplVars['aside']->value['key'])&&$_tpl->tplVars['aside']->value['key']) {?>
                    <i class="iconfont icon-<?php echo $_tpl->tplVars['aside']->value['key']; ?>
"></i>
                <?php }?>
                <span><?php echo $_tpl->tplVars['aside']->value['name']; ?>
</span></a>
            <?php }?>
                <ol>
                    <?php if (isset($_tpl->tplVars['aside']->value['children'])) {?>
                    <?php $_tpl->tplVars['child'] = new TMTemplateVariable; $_tpl->tplVars['child']->_loop = false;
$_from = $_tpl->tplVars['aside']->value['children']; if (!is_array($_from) && !is_object($_from)) { settype($_from, 'array'); }
foreach ($_from as $_tpl->tplVars['child']->key => $_tpl->tplVars['child']->value) {
$_tpl->tplVars['child']->_loop = true;
?>
                        <?php if (!isset($_tpl->tplVars['response']->value['ignore'])||!in_array($_tpl->tplVars['child']->value['navi_id'],$_tpl->tplVars['response']->value['ignore'])) {?>
                            <?php if (in_array($_tpl->tplVars['child']->value['name'],array())) {?>
                            <li class="child rel stat-module
                            <?php if (isset($_tpl->tplVars['child']->value['current'])&&$_tpl->tplVars['child']->value['current']) {?>
                            cur
                            <?php }?>
                            " data-key="<?php echo $_tpl->tplVars['child']->value['key']; ?>
" common="游戏分析" stid="<?php echo $_tpl->tplVars['aside']->value['name']; ?>
" sstid="<?php echo $_tpl->tplVars['child']->value['name']; ?>
"><a href="<?php echo $_tpl->tplVars['child']->value['url']; ?>
&game_id=<?php echo $_tpl->tplVars['response']->value['param']['game_id']; ?>
<?php if (isset($_tpl->tplVars['response']->value['param']['debug'])) {?>&debug=<?php echo $_tpl->tplVars['response']->value['param']['debug']; ?>
<?php }?>" class="tit" style=""><span><?php echo $_tpl->tplVars['child']->value['name']; ?>
</span><i class="J-new-function" style="right: 45px; top: 15px;"></i></a></li>
                            <?php } else { ?>
                            <li class="child stat-module
                            <?php if (isset($_tpl->tplVars['child']->value['current'])&&$_tpl->tplVars['child']->value['current']) {?>
                            cur
                            <?php }?>
                            " data-key="<?php echo $_tpl->tplVars['child']->value['key']; ?>
" common="游戏分析" stid="<?php echo $_tpl->tplVars['aside']->value['name']; ?>
" sstid="<?php echo $_tpl->tplVars['child']->value['name']; ?>
"><a href="<?php echo $_tpl->tplVars['child']->value['url']; ?>
&game_id=<?php echo $_tpl->tplVars['response']->value['param']['game_id']; ?>
<?php if (isset($_tpl->tplVars['response']->value['param']['debug'])) {?>&debug=<?php echo $_tpl->tplVars['response']->value['param']['debug']; ?>
<?php }?>" class="tit"><span><?php echo $_tpl->tplVars['child']->value['name']; ?>
</span></a></li>
                            <?php }?>
                        <?php }?>
                    <?php } ?>
                    <?php }?>
                </ol>
            </li>
        <?php }?>
    <?php } ?>
    </ul>
</div>
<?php }} ?>
