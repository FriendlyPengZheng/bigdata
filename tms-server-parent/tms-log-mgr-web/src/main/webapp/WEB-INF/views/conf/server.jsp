<%@page import="com.taomee.tms.mgr.entity.ServerInfo"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tmstag" prefix="cc"%>
<cc:overwrite name="pagetitle">区服管理</cc:overwrite>
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
	<div class="main with-aside">
		<%@ include file="../layout/aside.jsp"%>
		<!--  {include "layout/aside.html"} -->

		<div class="content" id="J_tableContainer">
			<div class="content-header clearfix">
				<a class="add-btn" id="J_addServerBtn">+添加区服</a>
				<div id="J_tableSearchContainer" class="search-wrapper"></div>
			</div>
			<table class="table module-table">
				<thead>
					<tr>
						<th class="th">serverID</th>
						<th class="th">serverName</th>
						<th class="th">gameId</th>
						<th class="th">parentId</th>
						<th class="th">是否是叶子节点</th>
						<th class="th">状态</th>
						<th class="th">操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${servers}" var="server">
						<tr>
							<td class="td hd">${server.serverId}</td>
							<td class="td hd">${server.serverName}</td>
							<td class="td hd">${server.gameId}</td>
							<td class="td hd">${server.parentId}</td>
							<td class="td hd">
								<%
								ServerInfo server = (ServerInfo) pageContext
											.getAttribute("server");
									String isLeaf = " ";
									switch (server.getIsLeaf()) {
									case 0:
										isLeaf = "是";
										break;
									case 1:
										isLeaf = "否";
										break;
									default:
									}
							%> <%=isLeaf%>
							</td>
							<td class="td hd">
								<%
								String status = " ";
									switch (server.getStatus()) {
									case 0:
										status = "使用中";
										break;
									case 1:
										status = "已废弃";
										break;
									default:
									}
							%> <%=status%>
							</td>
							<td class="td hd"><c:if test="${server.status==0}">
									<a href="#" class="del-btn btn-green"
										data='<%=com.alibaba.fastjson.JSON.toJSONString(pageContext.getAttribute("server"))%>'
										title="废弃">废弃</a>
								</c:if> <c:if test="${!(server.status==0)}">
									<a href="#" class="del-btn btn-green"
										data='<%=com.alibaba.fastjson.JSON.toJSONString(pageContext.getAttribute("server"))%>'
										title="开始使用">开始使用</a>
								</c:if> <a href="#" class="upd-btn btn-green"
								data='<%=com.alibaba.fastjson.JSON.toJSONString(pageContext.getAttribute("server"))%>'
								title="修改游戏配置">修改</a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</cc:overwrite>
<cc:overwrite name="pagejs">
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
		src="<c:url value='/js/data/page/conf/server.js'/>?v=<%=System.currentTimeMillis()%>"></script>
</cc:overwrite>
<%@ include file="../layout/head.jsp"%>