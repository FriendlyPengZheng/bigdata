<%@page import="com.taomee.tms.mgr.entity.TaskInfo"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tmstag" prefix="cc"%>
<cc:overwrite name="pagetitle">任务管理</cc:overwrite>
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
		<!--  {include "layout/aside.html"} -->
		<%@ include file="../layout/aside.jsp"%>
		<div class="content" id="J_tableContainer">
			<div class="content-header clearfix">
				<a class="add-btn" id="J_addTaskBtn">+添加任务</a>
				<div id="J_tableSearchContainer" class="search-wrapper"></div>
			</div>
			<table class="table module-table">
				<thead>
					<tr>
						<th class="th">任务ID</th>
						<th class="th">任务名称</th>
						<th class="th">算法</th>
						<th class="th">时间维度</th>
						<th class="th">结果是否入库</th>
						<th class="th">执行时间</th>
						<th class="th">优先级</th>
						<th class="th">操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${tasks}" var="task">
						<tr>
							<td class="td hd">${task.taskId}</td>
							<td class="td hd">${task.taskName}</td>
							<td class="td hd">${task.op}</td>
							<td class="td hd">
								<%
								TaskInfo task = (TaskInfo) pageContext
											.getAttribute("task");
									String period = " ";
									switch (task.getPeriod()) {
									case 0:
										period = "日";
										break;
									case 1:
										period = "周";
										break;
									case 2:
										period = "月";
										break;
									case 3:
										period = "版本周";
										break;
									case 4:
										period = "分";
										break;
									case 5:
										period = "时";
										break;
									default:
									}
							%> <%=period%>
							</td>
							<td class="td hd">
								<%
									/* task = (TaskInfo) pageContext.getAttribute("task"); */
									String result = " ";
									switch (task.getResult()) {
									case 0:
										result = "否";
										break;
									case 1:
										result = "是";
										break;
									default:
									}
							%> <%=result%>
							</td>
							<td class="td hd">${task.executeTime}</td>
							<td class="td hd">${task.priority}</td>
							<td class="td hd"><a href="#" class="upd-btn btn-green"
								data='<%=com.alibaba.fastjson.JSON.toJSONString(pageContext
						.getAttribute("task"))%>'
								title="修改任务配置">修改</a> <a href="#" class="del-btn btn-green"
								data='<%=com.alibaba.fastjson.JSON.toJSONString(pageContext
						.getAttribute("task"))%>'
								title="删除任务配置">删除</a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</cc:overwrite>
<cc:overwrite name="pagejs">
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
		src="<c:url value='/js/data/page/conf/task.js'/>?v=<%=System.currentTimeMillis()%>"></script>
</cc:overwrite>
<%@ include file="../layout/head.jsp"%>