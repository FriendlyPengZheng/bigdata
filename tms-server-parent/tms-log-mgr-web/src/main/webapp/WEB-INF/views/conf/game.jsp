<%@page import="com.taomee.tms.mgr.entity.GameInfo"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tmstag" prefix="cc"%>
<cc:overwrite name="pagetitle">游戏管理</cc:overwrite>
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
		<div class="content" id="J_tableContainer">
			<div class="content-header clearfix">
				<a class="add-btn" id="J_addGameBtn">+添加游戏</a>
				<div id="J_tableSearchContainer" class="search-wrapper"></div>
			</div>
			<table class="table module-table">
				<thead>
					<tr>
						<th class="th">游戏ID</th>
						<th class="th">游戏名称</th>
						<th class="th">类型</th>
						<th class="th">查看权限ID</th>
						<th class="th">管理权限ID</th>
						<th class="th">online查看权限ID</th>
						<th class="th">功能标志</th>
						<th class="th">忽略项</th>
						<th class="th">状态</th>
						<th class="th" data-disabled="1">操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${games}" var="game">
						<tr>
							<td class="td hd">${game.gameId}</td>
							<td class="td hd">${game.gameName}</td>
							<td class="td hd">${game.gameType}</td>
							<td class="td hd">${game.authId}</td>
							<td class="td hd">${game.mangeAuthId}</td>
							<td class="td hd">${game.onlineAuthId}</td>
							<td class="td hd">${game.funcSlot}</td>
							<td class="td hd">${game.ignoreId}</td>
							<td class="td hd">
								<%
                        	GameInfo game = (GameInfo)pageContext.getAttribute("game");
                            String status = "";
                            switch(game.getStatus()) {
                            case 2: 
                            	status = "已废弃";
                            	break;
                            case 1:
                            	status = "使用中";
                            	break;
                            default:
                            	status = "未使用";
                            }
                        %> <%=status%>
							</td>
							<td class="td hd"><c:if test="${game.status==1}">
									<a href="#" class="del-btn btn-green"
										data='<%=com.alibaba.fastjson.JSON.toJSONString(pageContext.getAttribute("game"))%>'
										title="废弃">废弃</a>
								</c:if> <c:if test="${!(game.status==1)}">
									<a href="#" class="del-btn btn-green"
										data='<%=com.alibaba.fastjson.JSON.toJSONString(pageContext.getAttribute("game"))%>'
										title="开始使用">开始使用</a>
								</c:if> <a href="#" class="upd-btn btn-green"
								data='<%=com.alibaba.fastjson.JSON.toJSONString(pageContext.getAttribute("game"))%>'
								title="修改游戏配置">修改</a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</cc:overwrite>
<cc:overwrite name="pagejs">
	<!-- here you can add some page javascript -->
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
		src="<c:url value='/js/data/page/conf/game.js'/>?v=<%=System.currentTimeMillis()%>"></script>
</cc:overwrite>
<%@ include file="../layout/head.jsp"%>
