<%@page import="com.taomee.tms.mgr.entity.LogInfo"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tmstag" prefix="cc"%>
<cc:overwrite name="pagetitle">日志管理</cc:overwrite>
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
				<a class="add-btn" id="J_addLogBtn">+添加日志</a>
				<div id="J_tableSearchContainer" class="search-wrapper"></div>
			</div>
			<table class="table module-table">
				<thead>
					<tr>
						<th class="th">ID</th>
						<th class="th">日志名称</th>
						<th class="th">类型</th>
						<th class="th">状态</th>
						<th class="th">操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${logs}" var="log">
						<tr>
							<td class="td hd">${log.logId}</td>
							<td class="td hd">${log.logName}</td>
							<td class="td hd">
								<%
                        	LogInfo log = (LogInfo)pageContext.getAttribute("log");
                            String type = " ";
                            switch(log.getType()) {
                            	case 0: 
                            		type = "注册类型";
                            		break;
                           	 	case 1:
                            		type = "自定义类型";
                            		break;
                            	default: 
                            }
                        %> <%=type%>
							</td>
							<td class="td hd">
								<%
                    		String status = "";
                    		switch(log.getStatus()) {
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
							<td class="td hd"><c:if test="${log.status==0}">
									<a href="#" class="del-btn btn-green"
										data='<%=com.alibaba.fastjson.JSON.toJSONString(pageContext.getAttribute("log"))%>'
										title="废弃">废弃</a>
								</c:if> <c:if test="${!(log.status==0)}">
									<a href="#" class="del-btn btn-green"
										data='<%=com.alibaba.fastjson.JSON.toJSONString(pageContext.getAttribute("log"))%>'
										title="开始使用">开始使用</a>
								</c:if> <a href="#" class="upd-btn btn-green"
								data='<%=com.alibaba.fastjson.JSON.toJSONString(pageContext.getAttribute("log"))%>'
								title="修改日志配置">修改</a></td>
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
		src="<c:url value='/js/data/page/conf/log.js'/>?v=<%=System.currentTimeMillis()%>"></script>
</cc:overwrite>
<%@ include file="../layout/head.jsp"%>

