<%@page import="com.taomee.tms.mgr.entity.DisplayPageModule"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tmstag" prefix="cc"%>
<!-- 游戏分析模块-二级导航-游戏类型 -->
<cc:overwrite name="subnav">
	<div class="wrapper">
		<ul class="main-sub-nav">
			<li class="sub-nav-li"><a href="javascript:void(0);">${displayPageModule.moduleName}</a>
			</li>
			<li class="sub-nav-li posr "><a
				href="${base_url}${displayPageModule.pageUrl}" class="bar-view">查看模板页面</a>
				<a href="${base_url}common/page/index/01" class="bar-view">返回导航列表</a>
			</li>
		</ul>
	</div>
</cc:overwrite>
<cc:overwrite name="content">
	<div class="main">
		<div class="content">
			<!-- <input type="hidden" value="{$response.param.navi_key}" id="J_gNaviKey"> -->
			<!-- <input type="hidden" value="gameanalysis-overview-keymetrics" id="J_gNaviKey"> -->
			<input type="hidden" value="${displayPageModule.key}" id="J_gNaviKey">
			<input type="hidden" value="${displayPageModule.gameId}"
				id="J_gGameId">
			<div class="content-header clearfix">
				<a class="add-btn" id="J_addModuleBtn">+添加模块</a> <a class="add-btn"
					id="J_json">生成JSON</a>
			</div>
			<div class="content-body" id="J_contentBody"></div>
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
		src="<c:url value='/js/common/jquery.form.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/Page.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/tm.tabs.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/Table.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/draw/highstock.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/Draw.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/common.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/jquery.choose.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/page/conf/conf.module.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/page/conf/conf.usage.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/page/conf/fac.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/page/conf/fac1.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/page/conf/module.js'/>?v=<%=System.currentTimeMillis()%>"></script>
</cc:overwrite>
<%@ include file="../layout/head.jsp"%>
