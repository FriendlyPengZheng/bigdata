<%@page import="org.springframework.web.context.request.RequestScope"%>
<%@page import="com.taomee.tms.mgr.entity.Metadata"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/tmstag" prefix="cc"%>
<cc:overwrite name="pagetitle">核心数据</cc:overwrite>
<cc:overwrite name="pagecss">
	<style type="text/css">
@import
url(
"<c:url value="
/
css
/data/page/kernel/kernel.css"/>");
</style>
</cc:overwrite>
<cc:overwrite name="subnav"><%@ include
		file="./sub-nav-email.jsp"%></cc:overwrite>
<cc:overwrite name="content">
	<div class="main">
		<input type="hidden" id="J_gEmailId" value="${requestScope.email_id}" />
		<input type="hidden" id="J_gEmailFreqId"
			value="${requestScope.frequency_type}" />
		<div class="content clearfix" id="J_content">
			<div class="email-tips" id="J_emailTips">
				<span class="mr20">最后发送：<span class="last-send-time mr10 ">${requestScope.last_send_time}</span><span
					class="last-send-user ">${requestScope.last_send_user}</span></span> <span
					class="mr20">收件人：<span class="receviers">${requestScope.receviers}</span></span>
				<span>测试收件人：<span class="test-receiver">${requestScope.test_receiver}</span></span>
			</div>
			<table class="table item-table mb10" id="J_datalist">
				<tbody>
					<c:forEach var="list" items="${requestScope.datalist}">
						<tr data-id="${list.emailDataId}" data-expr="${list.dataExpr}"
							data-offset="${list.offset}" data-server="${list.serverId}"
							data-name="${list.dataName}" data-period="${list.dataDateType}"
							data-unit="${list.unit}" data-threshold='${list.threshold}'>
							<td class="td hd"><a href="javascript:void(0);" class="item"
								title="点击查看详情">${list.contentTitle}${list.dataName}</a></td>
							<td class="td hd" colspan="7"><span class="row-loading">加载中...</span>
							</td>
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
		src="<c:url value='/js/data/common/navigator.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/draw/highstock.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/tm.draw.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/tm.datatable.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/common.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/stat.table.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/stat.table.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/tm.module.ok.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/usage.ok.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/tm.form.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/page/kernel/kernel.js'/>?v=<%=System.currentTimeMillis()%>"></script>

</cc:overwrite>
<%@ include file="../layout/head.jsp"%>