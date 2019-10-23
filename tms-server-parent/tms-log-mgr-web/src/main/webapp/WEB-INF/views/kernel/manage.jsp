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
		file="./nav-email-manage.jsp"%></cc:overwrite>
<cc:overwrite name="content">
	<div class="main">
		<div class="content clearfix" id="J_content">
			<div class="head-tools clearfix">
				<a class="add-btn" id="J_addEmailBtn">+添加Email</a>
			</div>
			<table class="table item-table mb10" id="J_datalist">
				<thead>
					<tr class="cur">
						<th class="th hd w50">编号</th>
						<th class="th hd">主题</th>
						<th class="th hd">收件人</th>
						<th class="th hd">抄送</th>
						<th class="th hd">测试收件人</th>
						<th class="th hd">微信收件人</th>
						<th class="th hd">Media_Id</th>
						<th class="th hd w400">说明</th>
						<th class="th hd">依赖</th>
						<th class="th hd">操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="list" items="${requestScope.emaillist}">
						<!-- {foreach $response.emaillist as $list} -->
						<tr data-id="${list.emailId}" data-status="${list.status}">
							<td class="td hd">${list.emailId}</td>
							<td class="td hd">${list.subject}</td>
							<td class="td hd">${list.receviers}</td>
							<td class="td hd">${list.cc}</td>
							<td class="td hd">${list.testReceiver}</td>
							<td class="td hd">${list.weixinRecev}</td>
							<td class="td hd">${list.weixinMediaId}</td>
							<td class="td hd">${list.remarks}</td>
							<td class="td hd">${list.dependencies}</td>
							<td class="td hd"><a href="javascript: void(0);"
								class="email-edit mr10" title="修改">修改</a> <a
								href="javascript: void(0);" class="email-del mr10" title="删除">删除</a>
								<!-- {if $list.status === '1'} --> <c:if
									test="${list.status == 1 }">
									<a href="javascript: void(0);" class="email-auto" title="取消自动">取消自动</a>
								</c:if> <c:if test="${list.status != 1 }">
									<a href="javascript: void(0);" class="email-auto" title="设为自动">设为自动</a>
								</c:if></td>
						</tr>
					</c:forEach>
					<!-- {/foreach} -->
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
		src="<c:url value='/js/data/common/stat.table.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/jquery.choose.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/page/kernel/manage.js'/>?v=<%=System.currentTimeMillis()%>"></script>
</cc:overwrite>
<%@ include file="../layout/head.jsp"%>