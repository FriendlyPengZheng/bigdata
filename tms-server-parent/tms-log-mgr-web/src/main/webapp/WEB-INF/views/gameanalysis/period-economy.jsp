<%@page import="org.springframework.web.context.request.RequestScope"%>
<%@page import="com.taomee.tms.mgr.entity.Metadata"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/tmstag" prefix="cc"%>
<!-- 
<link rel="stylesheet" type="text/css" href="../../../../css/data/page/gameanalysis/conf.css">  -->
<link rel="stylesheet" type="text/css"
	href="../../../../css/data/iconfont.css?v=<%=System.currentTimeMillis()%>"">
<cc:overwrite name="pagetitle">游戏分析</cc:overwrite>
<cc:overwrite name="subnav"><%@ include
		file="./sub-nav.jsp"%></cc:overwrite>
<cc:overwrite name="content">
	<div class="main with-aside">
		<%@ include file="./aside.jsp"%>
		<div class="content clearfix">
			<input type="hidden" id="J_paramGameId"
				value="${requestScope.game_id}" /> <input type="hidden"
				id="J_paramPlatformId" value="${requestScope.platform_id}" />
			<%--<input type="hidden" id="J_paramZoneId" value="${requestScope.zone_id}" />
        <input type="hidden" id="J_paramServerId" value="${requestScope.server_id}" />--%>
			<div class="content-header clearfix" id="J_contentHeader">
				<div class="select-con" id="J_zoneServer" tabindex="0"></div>
				<select id="J_platform" class="r">
					<option data-id="${requestScope.platform_id}" selected>全平台</option>
					<c:if test="${!empty requestScope.platform}">
						<c:forEach var="platform" items="${requestScope.platform}">
							<option data-id="${platform.platformId}">${platform.platformName}</option>
						</c:forEach>
					</c:if>
				</select>
				<div class="datepicker-trigger radius5-all r mr5" id="J_from_to">
					<%-- <input type="hidden" id="J_from" value="${response.param.from}" />
                <input type="hidden" id="J_to" value="${response.param.to}" />
                <input type="hidden" id="J_showFrom" value="${response.param.from}" />
                <input type="hidden" id="J_showTo" value="${response.param.to}" /> --%>

					<input type="hidden" id="J_from" value="${requestScope.from}" /> <input
						type="hidden" id="J_to" value="${requestScope.to}" /> <input
						type="hidden" id="J_showFrom" value="${requestScope.from}" /> <input
						type="hidden" id="J_showTo" value="${requestScope.to}" /> <i
						class="datepicker-icon"></i> <input class="title" type="text"
						id="J_date" value="${requestScope.from}~${requestScope.to}" /> <i
						class="datepicker-arrow"></i>
				</div>
			</div>
			<div class="content-body" id="J_contentBody"></div>
		</div>
	</div>
</cc:overwrite>

<cc:overwrite name="pagejs">
	<script
		src="<c:url value='/js/common/util.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script src="<c:url value='/js/common/jquery.form.js'/>"></script>
	<script
		src="<c:url value='/js/common/dlg.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/select.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/navigator.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/datepick.js'/>?v=<%=System.currentTimeMillis()%>"></script>
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
		src="<c:url value='/js/data/common/common.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/heatmap.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/jquery.choose.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/stat.table.js'/>?v=<%=System.currentTimeMillis() %>"></script>
	<script
		src="<c:url value='/js/data/page/gameanalysis/addtofavor.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/tm.module.ok1.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/usage.ok.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/page/gameanalysis/mbsale.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<%-- 
    <script src="<c:url value='/js/data/page/gameanalysis/mbmanage.js'/>?v=<%=System.currentTimeMillis()%>"></script> --%>
	<script
		src="<c:url value='/js/data/page/gameanalysis/period-gpzs.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<%-- <script src="<c:url value='/js/data/page/gameanalysis/category.js'/>?v=<%=System.currentTimeMillis()%>"></script> --%>
</cc:overwrite>
<%@ include file="../layout/head.jsp"%>