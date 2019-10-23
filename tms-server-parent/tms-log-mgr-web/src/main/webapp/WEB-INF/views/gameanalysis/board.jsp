<%@page import="org.springframework.web.context.request.RequestScope"%>
<%@page import="com.taomee.tms.mgr.entity.Metadata"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/tmstag" prefix="cc"%>
<cc:overwrite name="pagetitle">游戏分析</cc:overwrite>
<cc:overwrite name="pagecss">
	<style type="text/css">
@import
url(
"<c:url value="
/
css
/data/page/gameanalysis/board.css"/>");
</style>
</cc:overwrite>
<cc:overwrite name="subnav"><%@ include
		file="./sub-nav.jsp"%></cc:overwrite>
<cc:overwrite name="content">
	<div class="main with-aside">
		<%@ include file="./aside.jsp"%>
		<div class="content clearfix">
			<input type="hidden" id="J_paramGameId"
				value="${requestScope.game_id}" /> <input type="hidden"
				id="J_paramPlatformId" value="${requestScope.platform_id}" />
			<%--         <input type="hidden" id="J_paramZoneId" value="${requestScope.zone_id}" />
        <input type="hidden" id="J_paramServerId" value="${requestScope.server_id}" /> --%>
			<div class="content-header clearfix" id="J_contentHeader">
				<!-- <div class="select-con" id="J_server" tabindex="0"></div>
            <div class="select-con" id="J_zone" tabindex="1"></div> -->
				<div class="select-con" id="J_zoneServer" tabindex="0"></div>
				<select id="J_platform" class="r">
					<option data-id="${requestScope.platform_id}" selected>全平台</option>
					<c:if test="${!empty requestScope.platform}">
						<!-- {foreach $response.platform as $platform} -->
						<c:forEach var="platform" items="${requestScope.platform}">
							<option data-id="${platform.platformId}">${platform.platformName}</option>
						</c:forEach>
					</c:if>
					<c:if test="${empty requestScope.platform}">
						<option>选择平台</option>
					</c:if>
				</select>
				<div class="datepicker-trigger radius5-all single fr mr5"
					id="J_toTime">
					<input type="hidden" id="J_to" value="${requestScope.to}" /> <i
						class="datepicker-icon"></i> <input class="title" type="text"
						id="J_date" value="${requestScope.to}" /> <i
						class="datepicker-arrow"></i>
				</div>
			</div>
			<div class="content-body" id="J_contentBody">
				<div class="content-item">
					<h2 class="title new-player">新玩家</h2>
					<div class="container clearfix" id="J_contentNewPlayer"></div>
				</div>
				<div class="content-item">
					<h2 class="title active-player">活跃玩家</h2>
					<div class="container clearfix" id="J_contentActivePlayer"></div>
				</div>
				<div class="content-item">
					<h2 class="title revenue">收入</h2>
					<div class="container clearfix" id="J_contentRevenue"></div>
				</div>
				<div class="content-item">
					<h2 class="title first-pay">首次付费</h2>
					<div class="container clearfix" id="J_contentFirstPay"></div>
				</div>
			</div>
		</div>
	</div>
</cc:overwrite>

<cc:overwrite name="pagejs">
	<script
		src="<c:url value='/js/common/select.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/navigator.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/datepick.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/Page.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/tm.tabs.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/draw/highstock.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/tm.draw.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/jquery.choose.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/tm.datatable.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/common.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/stat.table.js'/>?v=<%=System.currentTimeMillis() %>">"</script>
	<script
		src="<c:url value='/js/data/page/gameanalysis/addtofavor.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/tm.module.ok.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/usage.ok.min.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/page/gameanalysis/board.js'/>?v=<%=System.currentTimeMillis()%>"></script>
</cc:overwrite>

<%@ include file="../layout/head.jsp"%>