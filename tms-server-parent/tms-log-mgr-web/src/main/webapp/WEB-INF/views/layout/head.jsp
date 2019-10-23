<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tmstag" prefix="cc"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="robots" content="nofollow,noindex">
<title><cc:block name="pagetitle">数据分析平台</cc:block></title>
<link rel="Shortcut Icon" type="image/x-icon"
	href="<c:url value='/image/common/tongji.png'/>" />
<link rel="Bookmark" href="<c:url value='/image/common/tongji.png'/>" />
<style type="text/css">
@import
url(
"<c:url value="
/
css
/common/common.css"/>");
</style>
<style type="text/css">
@import
url(
"<c:url value="
/
css
/data/common.css"/>");
</style>
<style type="text/css">
@import
url(
"<c:url value="
/
css
/common/layout.css"/>");
</style>
<style type="text/css">
@import
url(
"<c:url value="
/
css
/common/orange/theme.css"/>");
</style>
<cc:block name="pagecss" />
<style type="text/css">
@import
url(
"<c:url value="
/
css
/data/page/conf/conf.css"/>");
</style>

<!--[if lt IE 8]>
<style type="text/css">@import url("<c:url value="/css/common/layout.ie.css"/>");</style>
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
            userId: "${user.current_admin_id}",
            userName: "${user.current_admin_name}",
            isAdmin: "${user.is_super_admin?1:0}",
            isRelease: "${application_info.is_release}",
            locale: "${locale}",
            resServer: "${response.res_server}",
            skin: "${response.current_skin}",
            currentPage : {
                ignore : [],
                <c:if test="${!empty response.current_page}">
                parent : "${response.current_page.parent}",
                child : "${response.current_page.child}"
                </c:if>
            }
        };
        <c:if test="${!empty requestScope.ignore}">
        <c:forEach var="d" items="${requestScope.ignore}">
        window.responseData.currentPage.ignore.push(${d});
    	</c:forEach>
		</c:if>
window.gLoading = false;
</script>
<script src="<c:url value='/js/common/jquery.min.js'/>"></script>
</head>
<body>
	<!-- header begin -->
	<div class="header" id="J_header">
		<div class="wrapper">
			<h1 class="logo">
				<a href="#" title="数据分析平台" class="tit"><span class="ir">数据分析平台</span></a>
			</h1>
			<!--main nav start-->
			<c:if test="${!empty navigator.top_bar }">
				<ul class="main-nav">
					<c:forEach var="top_bar" items="${navigator.top_bar}">
						<c:if test="${top_bar.value['isMain'] == '1'}">
							<li
								<c:if test="${top_bar.value['current'] == '1'}">
					class="cur"
				</c:if>
								data-key="${top_bar.value['key']}"><a
								href="${top_bar.value['url']}">${top_bar.value['name']}</a></li>
						</c:if>
					</c:forEach>
				</ul>
			</c:if>
			<!--main nav end-->
			<ul class="links">
				<li>欢迎你,${user.current_admin_name}</li>
				<li>|</li>
				<c:if test="${!empty navigator.top_bar}">
					<c:forEach var="top_bar" items="${navigator.top_bar}">
						<c:if test="${top_bar.value['isMain'] != '1'}">
							<c:if test="${top_bar.value['name'] == '管理'}">
								<li class="setting"><a href="${top_bar.value['url']}">${top_bar.value['name']}</a></li>
							</c:if>
							<c:if test="${top_bar.value['name'] == '核心数据'}">
								<li class="setting"><a href="${top_bar.value['url']}">${top_bar.value['name']}</a></li>
							</c:if>
						</c:if>
					</c:forEach>
				</c:if>
				<li class="setting"><a href="${logOutPath}">退出</a></li>
			</ul>
		</div>
		<cc:block name="subnav"><%@ include
				file="sub-navi-blank.jsp"%></cc:block>
	</div>
	<div id="J_webSkinSel" class="round-shape web-skin"
		style="display: none;">
		<i class="arrow"></i>
		<div class="b1"></div>
		<div class="b2"></div>
		<div class="b3"></div>
		<div class="b4"></div>
		<ul class="content">
			<c:forEach var="skin" items="${response.skins}">
				<li
					class="skin-option
         	<c:if test="${response.current_skin == skin.key}">
         	current
         	</c:if>
         	"
					data-value="${key}"><i class="skin-color"
					style="backgroud:url(${response.res_server}/${skin.key}_skin.png);"></i></li>
			</c:forEach>
		</ul>
		<div class="b4"></div>
		<div class="b3"></div>
		<div class="b2"></div>
		<div class="b5"></div>
	</div>
	<!-- header end -->
	<cc:block name="content" />
	<script src="<c:url value='/js/common/dlg.js'/>"></script>
	<script src="<c:url value='/js/common/util.js'/>"></script>
	<script src="<c:url value='/js/data/common/layout.js'/>"></script>
	<cc:block name="pagejs" />
</body>
</html>