<%@page import="com.taomee.tms.mgr.entity.SchemaInfo"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tmstag" prefix="cc"%>
<cc:overwrite name="pagetitle">模式管理</cc:overwrite>
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
				<a class="add-btn" id="J_addSchemaBtn">+添加计算模式</a>
				<div id="J_tableSearchContainer" class="search-wrapper"></div>
			</div>
			<table class="table module-table">
				<thead>
					<tr>
						<th class="th">模式ID</th>
						<th class="th">日志ID</th>
						<th class="th">原料ID</th>
						<th class="th">原料名称</th>
						<th class="th">算法</th>
						<th class="th">级联字段</th>
						<th class="th">操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${schemas}" var="schema">
						<tr>
							<td class="td hd">${schema.schemaId}</td>
							<td class="td hd">${schema.logId}</td>
							<td class="td hd">${schema.materialName}</td>
							<td class="td hd">${schema.op}</td>
							<td class="td hd">${schema.cascadeFields}</td>
							<td class="td hd">
								<%
                    		SchemaInfo schema = (SchemaInfo)pageContext.getAttribute("schema");
                    		String status = "";
                    		switch(schema.getStatus()) {
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
							<td class="td hd"><c:if test="${schema.status==0}">
									<a href="#" class="del-btn btn-green"
										data='<%=com.alibaba.fastjson.JSON.toJSONString(pageContext.getAttribute("schema"))%>'
										title="废弃">废弃</a>
								</c:if> <c:if test="${!(schema.status==0)}">
									<a href="#" class="del-btn btn-green"
										data='<%=com.alibaba.fastjson.JSON.toJSONString(pageContext.getAttribute("schema"))%>'
										title="开始使用">开始使用</a>
								</c:if> <a href="#" class="upd-btn btn-green"
								data='<%=com.alibaba.fastjson.JSON.toJSONString(pageContext.getAttribute("schema"))%>'
								title="修改计算模式配置">修改</a></td>
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
		src="<c:url value='/js/data/page/conf/schema.js'/>?v=<%=System.currentTimeMillis()%>"></script>
</cc:overwrite>
<%@ include file="../layout/head.jsp"%>
