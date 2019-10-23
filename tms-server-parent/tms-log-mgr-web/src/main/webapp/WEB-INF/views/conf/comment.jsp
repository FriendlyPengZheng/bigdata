<%@page import="com.taomee.tms.mgr.entity.Comment"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tmstag" prefix="cc"%>
<cc:overwrite name="pagetitle">注释管理</cc:overwrite>
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
				<a class="add-btn" id="J_addCommentBtn">+添加注释</a>
				<div id="J_tableSearchContainer" class="search-wrapper"></div>
			</div>
			<table class="table module-table">
				<thead>
					<tr>
						<th class="th">ID</th>
						<th class="th">注释名称</th>
						<th class="th">详情</th>
						<th class="th">操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${comments}" var="comment">
						<tr>
							<td class="td hd">${comment.commentId}</td>
							<td class="td hd">${comment.keyword}</td>
							<td class="td hd">${comment.comment}</td>
							<td class="td hd"><a href="#" class="upd-btn btn-green"
								data='<%=com.alibaba.fastjson.JSON.toJSONString(pageContext.getAttribute("comment"))%>'
								title="修改注释配置">修改</a> <a href="#" class="del-btn btn-green"
								data='<%=com.alibaba.fastjson.JSON.toJSONString(pageContext.getAttribute("comment"))%>'
								title="删除注释配置">删除</a></td>
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
		src="<c:url value='/js/data/page/conf/comment.js'/>?v=<%=System.currentTimeMillis()%>"></script>
</cc:overwrite>
<%@ include file="../layout/head.jsp"%>