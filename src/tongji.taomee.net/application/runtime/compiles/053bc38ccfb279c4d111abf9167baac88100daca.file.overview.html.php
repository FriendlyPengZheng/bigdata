<?php /*%%TemplateHeaderCode:11340697465a95237d4688b2-58894496%%*/ $_valid = $_tpl->decodeProperties(
array (
  'file_dependency' => 
  array (
    '053bc38ccfb279c4d111abf9167baac88100daca' => 
    array (
      0 => '/var/www/tongji.taomee.net/application/view/gameanalysis/overview.html',
      1 => 1484553623,
      2 => 'file',
    ),
    '6d859c8030545903b91d1203b60648f53921593d' => 
    array (
      0 => '/var/www/tongji.taomee.net/application/view/layout/layout.html',
      1 => 1506499568,
      2 => 'file',
    ),
    '033062553f34a0102f501f02084da4fabc839a8d' => 
    array (
      0 => '/var/www/tongji.taomee.net/application/view/layout/sub-nav-blank.html',
      1 => 1506508413,
      2 => 'file',
    ),
    '51021955860397b73f7c912632e3210eb3503e56' => 
    array (
      0 => '/var/www/tongji.taomee.net/application/view/gameanalysis/sub-nav.html',
      1 => 1506508413,
      2 => 'file',
    ),
    '69b83d6ae5c3938eb8a1bce8f17df3ae186b5ff5' => 
    array (
      0 => '/var/www/tongji.taomee.net/application/view/gameanalysis/aside.html',
      1 => 1506499568,
      2 => 'file',
    ),
  ),
  'no_cache_hash' => '11340697465a95237d4688b2-58894496',
  'variables' => 
  array (
    'response' => 0,
    'd' => 0,
    'top_bar' => 0,
    'key' => 0,
  ),
  'has_no_cache_code' => false,
  'unifunc' => 'content_5a95237d9ff6a4_19509417',
),
false); /*/%%TemplateHeaderCode%%*/?>
<?php if ($_valid && !is_callable('content_5a95237d9ff6a4_19509417')) { function content_5a95237d9ff6a4_19509417($_tpl) {?><!DOCTYPE html>
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
/data/css/page/gameanalysis/overview.css?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
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
$_tpl = $_tpl->setupInlineSubTemplate("gameanalysis/sub-nav.html", $_tpl->cacheId, $_tpl->compileId, 0, null, array(), 0, '11340697465a95237d4688b2-58894496');
content_5a95237d635903_81913736($_tpl);
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
$_tpl = $_tpl->setupInlineSubTemplate("gameanalysis/aside.html", $_tpl->cacheId, $_tpl->compileId, 0, null, array(), 0, '11340697465a95237d4688b2-58894496');
content_5a95237d710bf7_41034425($_tpl);
$_tpl = array_pop($_tplStack);
/* End of included template "gameanalysis/aside.html" */?>
    <div class="tips" id="J_timeTips" style="display: none;">
        <a class="tips-close" href="javascript:void(0);">X</a>
        <div class="tips-text"><span class="tit"><?php echo TM::t('tongji','注意：'); ?>
</span><?php echo TM::t('tongji','页面只能显示{1}时间段的数据，下载数据时间段没有限制哦。',array('{1}'=>sprintf('<span class="tit period">%s~%s</span>',$_tpl->tplVars['response']->value['param']['from'],$_tpl->tplVars['response']->value['param']['to']))); ?>
</div>
    </div>
    <div class="progress-wrap" id="J_progress">
        <span class="progress-title"><?php echo TM::t('tongji','数据运算进度：'); ?>
</span>
        <span class="progress-text"></span>
    </div>
    <div class="content clearfix">
        <input type="hidden" id="J_paramGameId" value="<?php echo $_tpl->tplVars['response']->value['param']['game_id']; ?>
" />
        <input type="hidden" id="J_paramGameType" value="<?php echo $_tpl->tplVars['response']->value['param']['game_type']; ?>
" />
        <input type="hidden" id="J_r" value="<?php echo $_tpl->tplVars['response']->value['param']['r']; ?>
" />
        <input type="hidden" id="J_gpzsId" value="<?php echo $_tpl->tplVars['response']->value['gpzs_id']; ?>
" />
        <div class="content-header clearfix" id="J_contentHeader">
            <?php if (isset($_tpl->tplVars['response']->value['datalist'])) {?>
            <ul class="view-data" id="J_viewData">
                <?php $_tpl->tplVars['list'] = new TMTemplateVariable; $_tpl->tplVars['list']->_loop = false;
$_tpl->tplVars['k'] = new TMTemplateVariable;
$_from = $_tpl->tplVars['response']->value['datalist']; if (!is_array($_from) && !is_object($_from)) { settype($_from, 'array'); }
foreach ($_from as $_tpl->tplVars['list']->key => $_tpl->tplVars['list']->value) {
$_tpl->tplVars['list']->_loop = true;
$_tpl->tplVars['k']->value = $_tpl->tplVars['list']->key;
?>
                <li class="view-data<?php echo $_tpl->tplVars['k']->value+1; ?>
">
                    <span class="view-txt"><?php echo $_tpl->tplVars['list']->value['name']; ?>
</span>
                    <?php if (count($_tpl->tplVars['list']->value['data'])==1) {?>
                        <strong class="imp" style="font-size: 24px;">
                        <?php if ($_tpl->tplVars['k']->value==2) {?>
                            <?php echo number_format($_tpl->tplVars['list']->value['data'][0],2,".",","); ?>

                        <?php } else { ?>
                            <?php echo number_format($_tpl->tplVars['list']->value['data'][0]); ?>

                        <?php }?>
                        </strong>
                    <?php } else { ?>
                        <?php $_tpl->tplVars['data'] = new TMTemplateVariable; $_tpl->tplVars['data']->_loop = false;
$_tpl->tplVars['kk'] = new TMTemplateVariable;
$_from = $_tpl->tplVars['list']->value['data']; if (!is_array($_from) && !is_object($_from)) { settype($_from, 'array'); }
foreach ($_from as $_tpl->tplVars['data']->key => $_tpl->tplVars['data']->value) {
$_tpl->tplVars['data']->_loop = true;
$_tpl->tplVars['kk']->value = $_tpl->tplVars['data']->key;
?>
                            <?php if ($_tpl->tplVars['kk']->value==0) {?>
                                <strong class="imp">
                                <?php if ($_tpl->tplVars['k']->value==2) {?>
                                    <?php echo number_format($_tpl->tplVars['data']->value,2,".",","); ?>

                                <?php } else { ?>
                                    <?php echo number_format($_tpl->tplVars['data']->value); ?>

                                <?php }?>
                                </strong>
                            <?php } elseif ($_tpl->tplVars['kk']->value==1) {?>
                                <strong>
                                <?php if ($_tpl->tplVars['k']->value==2) {?>
                                    <?php echo number_format($_tpl->tplVars['data']->value,2,".",","); ?>

                                <?php } else { ?>
                                    <?php echo number_format($_tpl->tplVars['data']->value); ?>

                                <?php }?>
                                </strong>
                            <?php } elseif ($_tpl->tplVars['kk']->value==2&&$_tpl->tplVars['data']->value>=0) {?>
                                <strong class="up">+<?php echo $_tpl->tplVars['data']->value; ?>
%</strong>
                            <?php } elseif ($_tpl->tplVars['kk']->value==2&&$_tpl->tplVars['data']->value<0) {?>
                                <strong class="down"><?php echo $_tpl->tplVars['data']->value; ?>
%</strong>
                            <?php }?>
                        <?php } ?>
                    <?php }?>
                </li>
                <?php } ?>
            </ul>
            <?php }?>
            <?php if (isset($_tpl->tplVars['response']->value['param']['contrast'])&&$_tpl->tplVars['response']->value['param']['contrast']==1) {?>
                <div class="datepicker-trigger single radius5-all fr mr5 mt20" id="J_single">
            <?php } else { ?>
                <div class="datepicker-trigger single radius5-all fr mr5 mt20" id="J_single" style="display: none;">
            <?php }?>
                <input type="hidden" id="J_single_from" value="<?php echo $_tpl->tplVars['response']->value['param']['contrast_from']; ?>
" />
                <i class="datepicker-icon"></i>
                <input class="title" type="text" id="J_single_date" value="<?php echo $_tpl->tplVars['response']->value['param']['contrast_from']; ?>
" />
                <i class="datepicker-arrow"></i>
            </div>
            <label class="fr cstcon mt20">
                <?php if (isset($_tpl->tplVars['response']->value['param']['contrast'])&&$_tpl->tplVars['response']->value['param']['contrast']==1) {?>
                    <input type="checkbox" class="cstchk mr5" id="J_contrast" checked/>
                    <span class="csttxt" style="display: none;"><?php echo TM::t('tongji','选择对比开始时间'); ?>
</span>
                <?php } else { ?>
                    <input type="checkbox" class="cstchk mr5" id="J_contrast"/>
                    <span class="csttxt"><?php echo TM::t('tongji','选择对比开始时间'); ?>
</span>
                <?php }?>
            </label>
            <div class="datepicker-trigger radius5-all fr mr5 mt20" id="J_from_to" >
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
/data/js/page/gameanalysis/addtofavor.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/tm.module.ok.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/common/usage.ok.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>
    <script src="<?php echo $_tpl->tplVars['response']->value['res_server']; ?>
/data/js/page/gameanalysis/overview.js?v=<?php echo $_tpl->tplVars['response']->value['application_info']['version']; ?>
"></script>

    </body>
</html>
<?php }} ?>
<?php if ($_valid && !is_callable('content_5a95237d60dd94_16255857')) { function content_5a95237d60dd94_16255857($_tpl) {?><!-- 不含任何二级nav -->
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
<?php if ($_valid && !is_callable('content_5a95237d635903_81913736')) { function content_5a95237d635903_81913736($_tpl) {?><div class="wrapper">
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
<?php if ($_valid && !is_callable('content_5a95237d710bf7_41034425')) { function content_5a95237d710bf7_41034425($_tpl) {?><div class="aside" id="J_aside">
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
