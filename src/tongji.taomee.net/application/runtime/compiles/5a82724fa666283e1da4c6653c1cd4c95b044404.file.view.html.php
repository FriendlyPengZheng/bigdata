<?php /*%%TemplateHeaderCode:7965851925c94be15b3b0f6-12352441%%*/ $_valid = $_tpl->decodeProperties(
array (
  'file_dependency' => 
  array (
    '5a82724fa666283e1da4c6653c1cd4c95b044404' => 
    array (
      0 => '/opt/home/sevin/project/tongji.taomee.net/application/view/gamecustom/view.html',
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
    'fa670c90dd4328307f0c3ca1d1d729d5c8adaab8' => 
    array (
      0 => '/opt/home/sevin/project/tongji.taomee.net/application/view/gamecustom/sub-nav-view.html',
      1 => 1534838549,
      2 => 'file',
    ),
  ),
  'no_cache_hash' => '7965851925c94be15b3b0f6-12352441',
  'variables' => 
  array (
    'response' => 0,
    'd' => 0,
    'top_bar' => 0,
    'key' => 0,
  ),
  'has_no_cache_code' => false,
  'unifunc' => 'content_5c94be15cc73d8_89387321',
),
false); /*/%%TemplateHeaderCode%%*/?>
<?php if ($_valid && !is_callable('content_5c94be15cc73d8_89387321')) { function content_5c94be15cc73d8_89387321($_tpl) {?><!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <meta name="robots" content="nofollow,noindex">
        <title><?php echo TM::t('tongji','数据分析平台'); ?>
-<?php echo TM::t('tongji','游戏自定义数据'); ?>
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
        
     <link rel="stylesheet" type="text/css" href="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/css/page/gamecustom/gamecustom.css?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
">
	 <link rel="stylesheet" type="text/css" href="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/css/common/jquery.cartitem.css?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
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
        <?php /* Call merged included template "gamecustom/sub-nav-view.html" */
$_tplStack[] = $_tpl;
$_tpl = $_tpl->setupInlineSubTemplate("gamecustom/sub-nav-view.html", $_tpl->cacheId, $_tpl->compileId, 0, null, array(), 0, '7965851925c94be15b3b0f6-12352441');
content_5c94be15bf0ed4_41701819($_tpl);
$_tpl = array_pop($_tplStack);
/* End of included template "gamecustom/sub-nav-view.html" */?>
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
    <div class="aside">
        <span class="search-con" id="J_search">
            <span class="search-tips" style="display: none;"><?php echo TM::t('tongji','没有找到匹配项~'); ?>
</span>
            <input type="text" class="srh-txt"><i class="search-tag">&nbsp;</i>
        </span>
        <div id="J_tree"></div>
    </div>
    <div class="content clearfix">
      <input type="hidden" id="J_paramGameId" value="<?php echo $_tpl->tplVars['response']->value['param']['game_id']; ?>
" />
        <input type="hidden" id="J_r" value="<?php echo $_tpl->tplVars['response']->value['param']['r']; ?>
" />
        <div class="content-header clearfix" id="J_contentHeader">
            <div class="datepicker-trigger radius5-all fl mr5" id="J_from_to" >
                <input type="hidden" id="J_from" value="<?php echo $_tpl->tplVars['response']->value['param']['from']; ?>
" />
                <input type="hidden" id="J_to" value="<?php echo $_tpl->tplVars['response']->value['param']['to']; ?>
" />
                <i class="datepicker-icon"></i>
                <input class="title" type="text" id="J_date" value="<?php echo $_tpl->tplVars['response']->value['param']['from']; ?>
~<?php echo $_tpl->tplVars['response']->value['param']['to']; ?>
" />
                <i class="datepicker-arrow"></i>
            </div>
            <a class="a-link" id="J_addFavorBtn"><i class="collect rotate"></i><span><?php echo TM::t('tongji','添加到我的收藏'); ?>
</span></a>
            <div class="posr">
                <div class="select-con" id="J_zoneServer" tabindex="0">
                    <div class="select-title"><?php echo TM::t('tongji','请选择：'); ?>
</div>
                </div>
                <select id="J_platform" class="fr">
                <?php if (isset($_tpl->tplVars['response']->value['platform'])&&!empty($_tpl->tplVars['response']->value['platform'])) {?>
                <?php $_tpl->tplVars['platform'] = new TMTemplateVariable; $_tpl->tplVars['platform']->_loop = false;
$_from = $_tpl->tplVars['response']->value['platform']; if (!is_array($_from) && !is_object($_from)) { settype($_from, 'array'); }
foreach ($_from as $_tpl->tplVars['platform']->key => $_tpl->tplVars['platform']->value) {
$_tpl->tplVars['platform']->_loop = true;
?>
                    <option data-id="<?php echo $_tpl->tplVars['platform']->value['platform_id']; ?>
"><?php echo $_tpl->tplVars['platform']->value['gpzs_name']; ?>
</option>
                <?php } ?>
                <?php } else { ?>
                    <option><?php echo TM::t('tongji','选择平台：'); ?>
</option>
                <?php }?>
                </select>
            </div>
        </div>
        <div class="content-body" id="J_content" >
        </div>
    </div>
</div>
<?php if (isset($_tpl->tplVars['response']->value['cartflag'])&&!empty($_tpl->tplVars['response']->value['cartflag'])) {?>
<div id="J_cart"></div>
<input type="hidden" id="J_authchk" value="<?php echo $_tpl->tplVars['response']->value['cartflag']; ?>
" />
<?php }?>

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
/common/js/select.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/datepick.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/jquery.cookie.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/jsTree/jquery.jstree.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
	<script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/tm.datatable.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>	
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/Page.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/tm.tabs.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/draw/highstock.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/tm.draw.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
	<script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/jquery.form.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
	<script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/jquery.ajaxfileupload.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
	<script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/jquery.cart.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
	<script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/jquery.cartitem.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
	<script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/navigator.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/jquery.choose.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/common.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/stat.table.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/tm.module.ok.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/usage.ok.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
	<script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/tm.form.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/page/gamecustom/view.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>

    </body>
</html>
<?php }} ?>
<?php if ($_valid && !is_callable('content_5c94be15be1482_88166118')) { function content_5c94be15be1482_88166118($_tpl) {?><!-- 不含任何二级nav -->
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
<?php if ($_valid && !is_callable('content_5c94be15bf0ed4_41701819')) { function content_5c94be15bf0ed4_41701819($_tpl) {?><div class="wrapper">
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
" selected="true"><?php echo $_tpl->tplVars['game']->value['game_name']; ?>
<?php if ($_tpl->tplVars['game']->value['game_type']=="webgame") {?><?php echo TM::t('tongji','【页游】'); ?>
<?php } elseif ($_tpl->tplVars['game']->value['game_type']=="mobilegame") {?><?php echo TM::t('tongji','【手游】'); ?>
<?php } elseif ($_tpl->tplVars['game']->value['game_type']=="site") {?><?php echo TM::t('tongji','【网站】'); ?>
<?php } elseif ($_tpl->tplVars['game']->value['game_type']=="clientgame") {?><?php echo TM::t('tongji','【端游】'); ?>
<?php } else { ?><?php echo TM::t('tongji','【测试】'); ?>
<?php }?></option>
                <?php } else { ?>
                <option data-id="<?php echo $_tpl->tplVars['key']->value; ?>
" data-href="<?php echo $_tpl->tplVars['game']->value['url']; ?>
"><?php echo $_tpl->tplVars['game']->value['game_name']; ?>
<?php if ($_tpl->tplVars['game']->value['game_type']=="webgame") {?><?php echo TM::t('tongji','【页游】'); ?>
<?php } elseif ($_tpl->tplVars['game']->value['game_type']=="mobilegame") {?><?php echo TM::t('tongji','【手游】'); ?>
<?php } elseif ($_tpl->tplVars['game']->value['game_type']=="site") {?><?php echo TM::t('tongji','【网站】'); ?>
<?php } elseif ($_tpl->tplVars['game']->value['game_type']=="clientgame") {?><?php echo TM::t('tongji','【端游】'); ?>
<?php } else { ?><?php echo TM::t('tongji','【测试】'); ?>
<?php }?></option>
                <?php }?>
            <?php } ?>
            </select>
        </li>
        <?php if (isset($_tpl->tplVars['response']->value['manage_auth'])&&isset($_tpl->tplVars['response']->value['aside'])&&$_tpl->tplVars['response']->value['aside']) {?>
		<li class="sub-nav-li xmlposr stat-button" title="上传XML">
          <form action='' class='link-blk' enctype='multipart/form-data' method='post'>
            <input name='sstid' type='hidden' value='_coinsbuyitem_'>
            <input class='file-up' id='J_xmlUpload' name='files' type='file'>
            <input id='J_paramGameId' name='game_id' type='hidden' value='<?php echo $_tpl->tplVars['response']->value['param']['game_id']; ?>
'>
            <a class='xml-upload' href='javascript:void(0)' id='J_uploadIcon'>
              <?php echo TM::t('tongji','上传 XML'); ?>

            </a>
          </form>		  
		</li>
        <li class="sub-nav-li posr stat-button" title="配置管理">
        <a href="<?php echo $_tpl->tplVars['response']->value['aside'][key($_tpl->tplVars['response']->value['aside'])]['url']; ?>
&game_id=<?php echo $_tpl->tplVars['response']->value['param']['game_id']; ?>
&view_r=<?php echo $_tpl->tplVars['response']->value['param']['r']; ?>
" class="bar-manage"><i class="iconfont icon-config"></i><?php echo TM::t('tongji','配置管理'); ?>
</a>
		</li>
        <?php }?>
    </ul>
</div>
<?php }} ?>
