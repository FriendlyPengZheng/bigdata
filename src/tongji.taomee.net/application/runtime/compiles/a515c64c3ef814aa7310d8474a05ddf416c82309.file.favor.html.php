<?php /*%%TemplateHeaderCode:11819017625c87149e66f858-72944803%%*/ $_valid = $_tpl->decodeProperties(
array (
  'file_dependency' => 
  array (
    'a515c64c3ef814aa7310d8474a05ddf416c82309' => 
    array (
      0 => '/opt/home/sevin/project/tongji.taomee.net/application/view/home/favor.html',
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
    'bb5024472112e268686b9936d0101be9d5411869' => 
    array (
      0 => '/opt/home/sevin/project/tongji.taomee.net/application/view/home/aside.html',
      1 => 1534838549,
      2 => 'file',
    ),
  ),
  'no_cache_hash' => '11819017625c87149e66f858-72944803',
  'variables' => 
  array (
    'response' => 0,
    'd' => 0,
    'top_bar' => 0,
    'key' => 0,
  ),
  'has_no_cache_code' => false,
  'unifunc' => 'content_5c87149e7f6d04_73145990',
),
false); /*/%%TemplateHeaderCode%%*/?>
<?php if ($_valid && !is_callable('content_5c87149e7f6d04_73145990')) { function content_5c87149e7f6d04_73145990($_tpl) {?><!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <meta name="robots" content="nofollow,noindex">
        <title><?php echo $_tpl->tplVars['response']->value['application_info']['name']; ?>
-<?php echo TM::t('tongji','我的收藏'); ?>
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
/data/css/page/home/favor.css?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
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
        <?php /* Call merged included template "layout/sub-nav-blank.html" */
$_tplStack[] = $_tpl;
$_tpl = $_tpl->setupInlineSubTemplate("layout/sub-nav-blank.html", $_tpl->cacheId, $_tpl->compileId, 0, null, array(), 0, '11819017625c87149e66f858-72944803');
content_5c87149e712056_93554626($_tpl);
$_tpl = array_pop($_tplStack);
/* End of included template "layout/sub-nav-blank.html" */?>
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
    <?php /* Call merged included template "home/aside.html" */
$_tplStack[] = $_tpl;
$_tpl = $_tpl->setupInlineSubTemplate("home/aside.html", $_tpl->cacheId, $_tpl->compileId, 0, null, array(), 0, '11819017625c87149e66f858-72944803');
content_5c87149e72edf9_06039584($_tpl);
$_tpl = array_pop($_tplStack);
/* End of included template "home/aside.html" */?>
    <?php if (isset($_tpl->tplVars['response']->value['param']['show'])&&$_tpl->tplVars['response']->value['param']['show']==1) {?>
        <div class="tips" id="J_timeTips">
    <?php } else { ?>
        <div class="tips" id="J_timeTips" style="display: none;">
    <?php }?>
        <a class="tips-close" href="javascript:void(0);">X</a>
        <div class="tips-text"><span class="tit"><?php echo TM::t('tongji','注意：'); ?>
</span><?php echo TM::t('tongji','页面只能显示{1}时间段的数据，下载数据时间段没有限制哦。',array('{1}'=>sprintf('<span class="tit period">%s~%s</span>',$_tpl->tplVars['response']->value['param']['from'],$_tpl->tplVars['response']->value['param']['to']))); ?>
</div>
    </div>
    <div class="content clearfix">
        <input type="hidden" id="J_gR" value="<?php echo $_tpl->tplVars['response']->value['param']['r']; ?>
" />
		<div id="J_test" style="position: absolute; visibility: hidden; height: auto; width: auto; white-space: nowrap">Test string length</div>
        <?php if (isset($_tpl->tplVars['response']->value['favor_info']['game_name'])&&$_tpl->tplVars['response']->value['favor_info']['favor_type']==1) {?>
        <input type="hidden" id="J_gGameId" value="<?php echo $_tpl->tplVars['response']->value['favor_info']['game_id']; ?>
" />
        <input type="hidden" id="J_gGameName" value="<?php echo $_tpl->tplVars['response']->value['favor_info']['game_name']; ?>
" />
        <?php }?>
        <input type="hidden" id="J_gFavorId" value="<?php echo $_tpl->tplVars['response']->value['favor_info']['favor_id']; ?>
" />
        <input type="hidden" id="J_gFavorLayout" value="<?php echo $_tpl->tplVars['response']->value['favor_info']['layout']; ?>
" />
        <input type="hidden" id="J_gFavorName" value="<?php echo $_tpl->tplVars['response']->value['favor_info']['favor_name']; ?>
" />
        <input type="hidden" id="J_gFavorType" value="<?php echo $_tpl->tplVars['response']->value['favor_info']['favor_type']; ?>
" />
        <div class="content-header" id="J_contentHeader">
            <div class="unit w100p clearfix noOverflow">
                <div class="fl pl5 favor-title">
                    <span spellcheck="false" class="glyphName" contenteditable="true"><?php echo $_tpl->tplVars['response']->value['favor_info']['favor_name']; ?>
</span>
                    <span class="f14">
                        <?php if (isset($_tpl->tplVars['response']->value['favor_info']['game_name'])) {?>
                            (<?php echo $_tpl->tplVars['response']->value['favor_info']['game_name']; ?>
)
                        <?php }?>
                    </span>
                </div>
            </div>
            <div class="posr">
                <?php if ($_tpl->tplVars['response']->value['favor_info']['favor_type']==1&&isset($_tpl->tplVars['response']->value['platform'])) {?>
                    <div class="select-con" id="J_zoneServer" tabindex="0">
                        <div class="select-title"><?php echo TM::t('tongji','请选择：'); ?>
</div>
                    </div>
                    <select id="J_platform" class="r">
                    <?php if (!empty($_tpl->tplVars['response']->value['platform'])) {?>
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
                <?php }?>
                <div class="datepicker-trigger radius5-all fr mr5" id="J_from_to" >
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
        </div>
        <div class="toolbar-section">
            <?php if (isset($_tpl->tplVars['response']->value['favor_info']['is_default'])&&$_tpl->tplVars['response']->value['favor_info']['is_default']==1) {?>
                <span class="btn-dft" id="J_dftCenterBtn" title="<?php echo TM::t('tongji','取消设置首次进入页面默认收藏'); ?>
" data-type="1"><?php echo TM::t('tongji','取消默认收藏'); ?>
</span>
            <?php } else { ?>
                <span class="btn-dft" id="J_dftCenterBtn" title="<?php echo TM::t('tongji','设置首次进入页面默认收藏'); ?>
" data-type="0"><?php echo TM::t('tongji','设置为默认收藏'); ?>
</span>
            <?php }?>
            <span class="division">|</span>
            <span class="btn-dft" id="J_download"><?php echo TM::t('tongji','导出'); ?>
</span>
            <span class="division">|</span>				
            <span class="btn-dft" id="J_delCenterBtn" ><?php echo TM::t('tongji','删除我的收藏'); ?>
</span>
            <?php if ($_tpl->tplVars['response']->value['user']['is_super_admin']&&!$_tpl->tplVars['response']->value['shared']) {?>
            <span class="division">|</span>				
            <span class="btn-dft" id="J_sharedCenterBtn" ><?php echo TM::t('tongji','分享我的收藏'); ?>
</span>
            <?php }?>
        </div>
        <div class="content-body mt10 clearfix" id="J_contentBody" >
            <div
            <?php if (isset($_tpl->tplVars['response']->value['favor_info']['layout'])&&$_tpl->tplVars['response']->value['favor_info']['layout']==1) {?>
                class="window-layout-container fl" style="width:100%;"
            <?php } else { ?>
                class="window-layout-container fl mr" style="width:50%;"
            <?php }?>
            ></div>
            <div
            <?php if (isset($_tpl->tplVars['response']->value['favor_info']['layout'])&&$_tpl->tplVars['response']->value['favor_info']['layout']==1) {?>
                class="window-layout-container fl" style="width:100%;"
            <?php } else { ?>
                class="window-layout-container fl mr" style="width:50%;"
            <?php }?>
            ></div>
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
/common/js/jquery.ui.mouse.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/jquery.ui.draggable.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/jquery.ui.droppable.js"></script>
	<script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/jquery.ui.sortable.js"></script>    
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/datepick.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/select.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/draw/highstock.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/tm.draw.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/tm.datatable.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/navigator.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/jquery.choose.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/common.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/stat.table.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/tm.module.ok.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/usage.ok.js"></script>
	<script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/page/home/controller/favor_ajax.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
	<script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/page/home/favor_basic.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
	<script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/page/home/favor_widget.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
	<script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/page/home/favor_speed.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
	<script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/page/home/view/favor_view.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script>
        window.pageConfigure = window.pageConfigure || {};
        window.pageConfigure.shared = <?php if ($_tpl->tplVars['response']->value['shared']) {?>1<?php } else { ?>0<?php }?>;
    </script>
    
    <?php if ($_tpl->tplVars['response']->value['favor_info']['favor_type']==1) {?>
        <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/page/home/bak/single.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <?php } else { ?>
        <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/page/home/bak/multi.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <?php }?>

    </body>
</html>
<?php }} ?>
<?php if ($_valid && !is_callable('content_5c87149e712056_93554626')) { function content_5c87149e712056_93554626($_tpl) {?><!-- 不含任何二级nav -->
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
<?php if ($_valid && !is_callable('content_5c87149e72edf9_06039584')) { function content_5c87149e72edf9_06039584($_tpl) {?><div class="aside" id="J_aside">
    <ul>
        <?php $_tpl->tplVars['aside'] = new TMTemplateVariable; $_tpl->tplVars['aside']->_loop = false;
$_from = $_tpl->tplVars['response']->value['home_aside']; if (!is_array($_from) && !is_object($_from)) { settype($_from, 'array'); }
foreach ($_from as $_tpl->tplVars['aside']->key => $_tpl->tplVars['aside']->value) {
$_tpl->tplVars['aside']->_loop = true;
?>
        <li class="
            <?php if (isset($_tpl->tplVars['aside']->value['current'])&&$_tpl->tplVars['aside']->value['current']) {?>
            cur
            <?php }?>
            " data-id= "<?php echo $_tpl->tplVars['aside']->value['favor_id']; ?>
" data-type="<?php echo $_tpl->tplVars['aside']->value['favor_type']; ?>
" data-game="<?php echo $_tpl->tplVars['aside']->value['game_id']; ?>
" >
            <a href="<?php echo $_tpl->tplVars['aside']->value['url']; ?>
"><span><?php echo $_tpl->tplVars['aside']->value['favor_name']; ?>
</span></a>
        </li>
        <?php } ?>
    </ul>
    <ul class="bt">
    </ul>
</div>
<?php }} ?>
