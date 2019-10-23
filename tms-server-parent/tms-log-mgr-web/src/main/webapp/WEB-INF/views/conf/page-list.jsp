<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="com.taomee.tms.mgr.entity.Metadata"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tmstag" prefix="cc"%>
<cc:overwrite name="pagetitle">模板管理</cc:overwrite>
<cc:overwrite name="pagecss">
	<style type="text/css">
@import
url(
"<c:url value="
/
css
/data/page/conf/conf.css"/>");
</style>
</cc:overwrite>
<cc:overwrite name="content">

	<!-- <!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head> -->

	<!-- 
{extends "layout/layout.html"}

{block "layout.title"}{$response.application_info.name}-元数据管理{/block}
{block "layout.pagecss"}
     <link rel="stylesheet" type="text/css" href="{$response.res_server}/data/css/page/conf/conf.css?v={$response.application_info.version}">
{/block}
{block "layout.main"}
-->

	<div class="main with-aside">
		<!--  {include "layout/aside.html"} -->
		<%@ include file="../layout/aside.jsp"%>
		<div class="content">
			<div class="content-header clearfix" id="J_contentHeader">
				<div class="select-con" id="J_selGame">
					<div class="select-title">选择游戏：</div>
				</div>
				<!--  	<select id="J_games">
				<c:forEach items="${games}" var="game">
					<c:if test="${game.gameId != 0}">
						<option value="${game.gameId}" >${game.gameName}</option>
					</c:if>
					<c:if test="${game.gameId == 0}">
						<option value="${game.gameId}" selected="selected">${game.gameName}</option>
					</c:if>
				</c:forEach>
			</select>-->
			</div>
			<table class="table module-table">
				<thead>
					<tr>
						<th class="th">一级导航</th>
						<th class="th">二级导航</th>
						<th class="th">三级导航</th>
						<th class="th">四级导航</th>
						<th class="th">key</th>
						<th class="th">操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${pages}" var="page">
						<tr>
							<td class="td hd">${page.name[0]}</td>
							<td class="td hd">${page.name[1]}</td>
							<td class="td hd">${page.name[2]}</td>
							<td class="td hd">${page.name[3]}</td>
							<td class="td hd">${page.key}</td>
							<td class="td hd"><a
								href="${base_url}${page.url}?navi_key=${page.key}"
								class="item mr20" title="查看页面">查看</a> <!-- TODO 包括script_name以及权限管理等-->
								<!-- <a class="item" title="修改页面">修改</a> --> <a
								href="${base_url}common/page/displayPageModule?key=${page.key}&moduleName=${page.title}&pageUrl=${page.url}&gameId=0"
								class="item mr20" title="修改页面">修改</a> <a href="#"
								data-key="${page.key}" class="item J-btn-build" title="生成JSON">生成JSON</a>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</cc:overwrite>
<cc:overwrite name="pagejs">
	<!-- <script src="aaa.js/v=<%=System.currentTimeMillis()%>"></script> -->
	<script
		src="<c:url value='/js/common/util.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/dlg.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/select.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/jquery.form.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/Page.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/tm.datatable.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/tm.form.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/page/conf/page.js'/>?v=<%=System.currentTimeMillis()%>"></script>
</cc:overwrite>
<%@ include file="../layout/head.jsp"%>
