<?php /*%%TemplateHeaderCode:13558312915bbad28b1c4ea7-34756153%%*/ $_valid = $_tpl->decodeProperties(
array (
  'file_dependency' => 
  array (
    '42d4189ff8afadadd9d8f659c34c2237271d62df' => 
    array (
      0 => '/opt/home/sevin/project/tongji.taomee.net/application/view/gamecustom/manage.html',
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
    '829a30bc44a0e0af80acea1f5d52c01355674937' => 
    array (
      0 => '/opt/home/sevin/project/tongji.taomee.net/application/view/gamecustom/sub-nav-manage.html',
      1 => 1534838549,
      2 => 'file',
    ),
  ),
  'no_cache_hash' => '13558312915bbad28b1c4ea7-34756153',
  'variables' => 
  array (
    'response' => 0,
    'd' => 0,
    'top_bar' => 0,
    'key' => 0,
  ),
  'has_no_cache_code' => false,
  'unifunc' => 'content_5bbad28b2fcfb4_90653841',
),
false); /*/%%TemplateHeaderCode%%*/?>
<?php if ($_valid && !is_callable('content_5bbad28b2fcfb4_90653841')) { function content_5bbad28b2fcfb4_90653841($_tpl) {?><!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <meta name="robots" content="nofollow,noindex">
        <title><?php echo $_tpl->tplVars['response']->value['application_info']['name']; ?>
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
/data/css/page/gamecustom/manage.css?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
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
        <?php /* Call merged included template "gamecustom/sub-nav-manage.html" */
$_tplStack[] = $_tpl;
$_tpl = $_tpl->setupInlineSubTemplate("gamecustom/sub-nav-manage.html", $_tpl->cacheId, $_tpl->compileId, 0, null, array(), 0, '13558312915bbad28b1c4ea7-34756153');
content_5bbad28b27a7d5_60236837($_tpl);
$_tpl = array_pop($_tplStack);
/* End of included template "gamecustom/sub-nav-manage.html" */?>
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
        <div class="content-body">
            <div id="J_contentHead" class="head-tools clearfix">
                <span class="links link-node" style="display:none;">
                    <a class="a-link a-link-node"><i class="node">&nbsp;</i><span><?php echo TM::t('tongji','添加节点'); ?>
</span></a>
                    <a class="a-link a-link-merge"><i class="merge">&nbsp;</i><span><?php echo TM::t('tongji','合并节点'); ?>
</span></a>
                    <span class="fr"><?php echo TM::t('tongji','（注：{1}重命名时，按ESC（退出键）取消。）',array('{1}'=>'')); ?>
</span>
                </span>
                <span class="links link-event" style="display:none;">
                    <a class="a-link a-link-diy"><i class="node">&nbsp;</i><span><?php echo TM::t('tongji','添加自定义加工项'); ?>
</span></a>
                    <span class="fr"><?php echo TM::t('tongji','（注：{1}重命名时，按ESC（退出键）取消。）',array('{1}'=>sprintf('<a class="a-icon basic">%s</a><a class="a-icon distribute">%s</a><a class="a-icon item">%s</a><a class="a-icon diy">%s</a>%s',TM::t('tongji','基本类型'),TM::t('tongji','分布类型'),TM::t('tongji','item类型'),TM::t('tongji','自定义类型'),TM::t('tongji','；')))); ?>
</span>
                </span>
                <div class="batch fl" style="display:none;">
                    <a class="a-link a-link-delete"><i class="delete">&nbsp;</i><span><?php echo TM::t('tongji','批量删除'); ?>
</span></a>
                </div>
                <div class="report-only batch fl" style="display:none;">
                    <a class="a-link a-link-delete"><i class="delete">&nbsp;</i><span><?php echo TM::t('tongji','移到回收站'); ?>
</span></a>
                </div>
            </div>
            <div id="J_content"></div>
        </div>
    </div>
</div>
<ul class="context-menu" id="J_rMenu" style="display:none;">
    <li type="rename"><?php echo TM::t('tongji','重命名'); ?>
</li>
    <li type="look" style="display:none;"><?php echo TM::t('tongji','子项排序及删除'); ?>
</li>
    <li type="set" style="display:none;"><?php echo TM::t('tongji','设置'); ?>
</li>
    <li type="back" style="display:none;"><?php echo TM::t('tongji','找回名称'); ?>
</li>
    <li type="delete"><?php echo TM::t('tongji','删除'); ?>
</li>
</ul>

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
/common/js/jquery.ui.mouse.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/jquery.ui.draggable.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/jquery.ui.droppable.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/jquery.ui.sortable.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/select.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/navigator.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/datepick.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/jquery.cookie.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/jsTree/jquery.jstree.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/jquery.choose.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/Page.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/tm.tabs.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/draw/highstock.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/Draw.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/stat.table.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/tm.module.ok.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/usage.ok.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/page/gamecustom/manage.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>

    </body>
</html>
<?php }} ?>
<?php if ($_valid && !is_callable('content_5bbad28b26b0d5_98009998')) { function content_5bbad28b26b0d5_98009998($_tpl) {?><!-- 不含任何二级nav -->
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
<?php if ($_valid && !is_callable('content_5bbad28b27a7d5_60236837')) { function content_5bbad28b27a7d5_60236837($_tpl) {?><div class="wrapper">
    <ul class="main-sub-nav">
        <li class="sub-nav-li">
            <?php if (isset($_tpl->tplVars['response']->value['game'][$_tpl->tplVars['response']->value['param']['game_id']])) {?>
            <a href="javascript:void(0);"><?php echo $_tpl->tplVars['response']->value['game'][$_tpl->tplVars['response']->value['param']['game_id']]['game_name']; ?>
</a>
            <?php }?>
        </li>
        <?php if (isset($_tpl->tplVars['response']->value['param']['view_r'])) {?>
            <li class="sub-nav-li posr stat-button" title="返回查看">
            <a href="index.php?r=<?php echo $_tpl->tplVars['response']->value['param']['view_r']; ?>
&game_id=<?php echo $_tpl->tplVars['response']->value['param']['game_id']; ?>
" class="bar-view"><i class="iconfont icon-back"></i><?php echo TM::t('tongji','返回查看'); ?>
</a>
			</li>
        <?php }?>
    </ul>
</div>
<?php }} ?>
