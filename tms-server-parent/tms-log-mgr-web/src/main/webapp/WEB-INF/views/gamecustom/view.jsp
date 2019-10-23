<%@page import="org.springframework.web.context.request.RequestScope"%>
<%@page import="com.taomee.tms.mgr.entity.Metadata"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/tmstag" prefix="cc"%>
<cc:overwrite name="pagetitle">游戏自定义数据</cc:overwrite>
<cc:overwrite name="pagecss">
	<style type="text/css">
@import
url(
"<c:url value="
/
css
/common/jquery.cartitem.css"/>");
</style>
	<style type="text/css">
@import
url(
"<c:url value="
/
css
/data/page/gamecustom/gamecustom.css"/>");
</style>
</cc:overwrite>
<cc:overwrite name="subnav"><%@ include
		file="./sub-nav-view.jsp"%></cc:overwrite>
<cc:overwrite name="content">
	<div class="main with-aside">
		<div class="aside">
			<span class="search-con" id="J_search"> <span
				class="search-tips" style="display: none;">没有找到匹配项~</span> <input
				type="text" class="srh-txt"><i class="search-tag">&nbsp;</i>
			</span>
			<div id="J_tree"></div>
		</div>
		<div class="content clearfix">
			<input type="hidden" id="J_paramGameId"
				value="${requestScope.game_id}" /> <input type="hidden"
				id="J_paramPlatformId" value="${requestScope.platform_id}" /> <input
				type="hidden" id="J_paramZoneId" value="${requestScope.zone_id}" />
			<input type="hidden" id="J_paramServerId"
				value="${requestScope.server_id}" />
			<div class="content-header clearfix" id="J_contentHeader">
				<div class="datepicker-trigger radius5-all fl mr5" id="J_from_to">
					<input type="hidden" id="J_from" value="${requestScope.from}" /> <input
						type="hidden" id="J_to" value="${requestScope.to}" /> <i
						class="datepicker-icon"></i> <input class="title" type="text"
						id="J_date" value="${requestScope.from}~${requestScope.to}" /> <i
						class="datepicker-arrow"></i>
				</div>
				<a class="a-link" id="J_addFavorBtn"><i class="collect rotate"></i><span>添加到我的收藏</span></a>
				<div class="posr">
					<div class="select-con" id="J_server" tabindex="0"></div>
					<div class="select-con" id="J_zone" tabindex="1"></div>
					<select id="J_platform" class="r">
						<option data-id="${requestScope.platform_id}" selected>全平台</option>
						<c:if test="${!empty requestScope.platform}">
							<!-- {foreach $response.platform as $platform} -->
							<c:forEach var="platform" items="${requestScope.platform}">
								<option data-id="${platform.serverId}">${platform.serverName}</option>
							</c:forEach>
						</c:if>
						<c:if test="${empty requestScope.platform}">
							<option>选择平台</option>
						</c:if>
					</select>
				</div>
			</div>
			<div class="content-body" id="J_content"></div>
		</div>
	</div>
</cc:overwrite>


<!-- 
	<script src="{$response.res_server}/data/js/common/jquery.cart.js?v={$response.application_info.version}"></script>
	<script src="{$response.res_server}/data/js/common/jquery.cartitem.js?v={$response.application_info.version}"></script> -->

<cc:overwrite name="pagejs">
	<script
		src="<c:url value='/js/common/select.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/datepick.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/jquery.cookie.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/jsTree/jquery.jstree.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/tm.datatable.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/Page.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/tm.tabs.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/draw/highstock.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/tm.draw.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/jquery.form.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<%-- <script src="<c:url value='/js/common/jquery.ajaxfileupload.js'/>?v=<%=System.currentTimeMillis()%>"></script> --%>
	<script
		src="<c:url value='/js/data/common/navigator.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/jquery.choose.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/common.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/stat.table.js'/>?v=<%=System.currentTimeMillis() %>">"</script>
	<script
		src="<c:url value='/js/data/common/tm.module.ok.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/usage.ok.min.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/tm.form.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/page/gamecustom/view.js'/>?v=<%=System.currentTimeMillis()%>"></script>
</cc:overwrite>

<%@ include file="../layout/head.jsp"%>