<?php /*%%TemplateHeaderCode:13141936965b7e1c82d54d28-36677909%%*/ $_valid = $_tpl->decodeProperties(
array (
  'file_dependency' => 
  array (
    '78ae9190a14a4911d9173373cc4b9aa2cade662f' => 
    array (
      0 => '/opt/home/sevin/project/tongji.taomee.net/application/view/topic/kpi.html',
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
    '523d9a30c9210e58cf4c9c535f0b7bdeaf7bd2ea' => 
    array (
      0 => '/opt/home/sevin/project/tongji.taomee.net/application/view/topic/sub-nav.html',
      1 => 1534838549,
      2 => 'file',
    ),
    'a5d4c3a71096a13697de4f445491da8f0079c755' => 
    array (
      0 => '/opt/home/sevin/project/tongji.taomee.net/application/view/layout/aside.html',
      1 => 1534838549,
      2 => 'file',
    ),
  ),
  'no_cache_hash' => '13141936965b7e1c82d54d28-36677909',
  'variables' => 
  array (
    'response' => 0,
    'd' => 0,
    'top_bar' => 0,
    'key' => 0,
  ),
  'has_no_cache_code' => false,
  'unifunc' => 'content_5b7e1c82ecaae2_22863289',
),
false); /*/%%TemplateHeaderCode%%*/?>
<?php if ($_valid && !is_callable('content_5b7e1c82ecaae2_22863289')) { function content_5b7e1c82ecaae2_22863289($_tpl) {?><!DOCTYPE html>
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
        
     <link rel="stylesheet" type="text/css" href="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/css/page/topic/topic.css?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
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
        <?php /* Call merged included template "topic/sub-nav.html" */
$_tplStack[] = $_tpl;
$_tpl = $_tpl->setupInlineSubTemplate("topic/sub-nav.html", $_tpl->cacheId, $_tpl->compileId, 0, null, array(), 0, '13141936965b7e1c82d54d28-36677909');
content_5b7e1c82e0c401_43976418($_tpl);
$_tpl = array_pop($_tplStack);
/* End of included template "topic/sub-nav.html" */?>
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
    <?php /* Call merged included template "layout/aside.html" */
$_tplStack[] = $_tpl;
$_tpl = $_tpl->setupInlineSubTemplate("layout/aside.html", $_tpl->cacheId, $_tpl->compileId, 0, null, array(), 0, '13141936965b7e1c82d54d28-36677909');
content_5b7e1c82e29709_43920388($_tpl);
$_tpl = array_pop($_tplStack);
/* End of included template "layout/aside.html" */?>
    <div class="content clearfix">
        <input type="hidden" id="J_r" value="<?php echo $_tpl->tplVars['response']->value['param']['r']; ?>
" />
        <input type="hidden" id="J_paramFrom" value="<?php echo $_tpl->tplVars['response']->value['param']['from']; ?>
" />
        <input type="hidden" id="J_paramTo" value="<?php echo $_tpl->tplVars['response']->value['param']['to']; ?>
" />
        <div class="content-body" id="J_contentBody" ></div>
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
/common/js/tm.datatable.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/draw/highstock.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/common/js/tm.draw.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/stat.table.js"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/common.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/tm.module.ok.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/usage.ok.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/page/topic/kpi.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>

    </body>
</html>
<?php }} ?>
<?php if ($_valid && !is_callable('content_5b7e1c82dfc378_98575676')) { function content_5b7e1c82dfc378_98575676($_tpl) {?><!-- 不含任何二级nav -->
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
<?php if ($_valid && !is_callable('content_5b7e1c82e0c401_43976418')) { function content_5b7e1c82e0c401_43976418($_tpl) {?><div class="wrapper">
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
<?php if ($_valid && !is_callable('content_5b7e1c82e29709_43920388')) { function content_5b7e1c82e29709_43920388($_tpl) {?><div class="aside" id="J_aside">
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
            ">
                <a href="<?php echo $_tpl->tplVars['aside']->value['url']; ?>
"
            <?php }?>
                <?php if (isset($_tpl->tplVars['aside']->value['key'])&&$_tpl->tplVars['aside']->value['key']) {?>
                class="<?php echo $_tpl->tplVars['aside']->value['key']; ?>
"
                <?php }?>
                ><span><?php echo $_tpl->tplVars['aside']->value['name']; ?>
</span><i class="J-new-function" style="right: 45px; top: 20px;"></i></a>
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
            ">
                <a href="<?php echo $_tpl->tplVars['aside']->value['url']; ?>
"
            <?php }?>
                <?php if (isset($_tpl->tplVars['aside']->value['key'])&&$_tpl->tplVars['aside']->value['key']) {?>
                class="<?php echo $_tpl->tplVars['aside']->value['key']; ?>
"
                <?php }?>
                ><span><?php echo $_tpl->tplVars['aside']->value['name']; ?>
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
                            <li class="child rel
                            <?php if (isset($_tpl->tplVars['child']->value['current'])&&$_tpl->tplVars['child']->value['current']) {?>
                            cur
                            <?php }?>
                            " data-key="<?php echo $_tpl->tplVars['child']->value['key']; ?>
"><a href="<?php echo $_tpl->tplVars['child']->value['url']; ?>
<?php if (isset($_tpl->tplVars['response']->value['param']['debug'])) {?>&debug=<?php echo $_tpl->tplVars['response']->value['param']['debug']; ?>
<?php }?>" class="tit" style=""><span><?php echo $_tpl->tplVars['child']->value['name']; ?>
</span><i class="J-new-function" style="right: 45px; top: 15px;"></i></a></li>
                            <?php } else { ?>
                            <li class="child
                            <?php if (isset($_tpl->tplVars['child']->value['current'])&&$_tpl->tplVars['child']->value['current']) {?>
                            cur
                            <?php }?>
                            " data-key="<?php echo $_tpl->tplVars['child']->value['key']; ?>
"><a href="<?php echo $_tpl->tplVars['child']->value['url']; ?>
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
