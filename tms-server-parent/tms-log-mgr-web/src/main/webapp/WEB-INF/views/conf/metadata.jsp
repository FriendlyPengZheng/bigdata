<%@page import="com.taomee.tms.mgr.entity.Metadata"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tmstag" prefix="cc"%>
<cc:overwrite name="pagetitle">元数据管理</cc:overwrite>
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
				<a class="add-btn" id="J_addMetadataBtn">+添加元数据</a>
				<div id="J_tableSearchContainer" class="search-wrapper"></div>
			</div>
			<table class="table module-table">
				<thead>
					<tr>
						<th class="th">ID</th>
						<th class="th">时间维度</th>
						<th class="th">元数据名称</th>
						<!-- <th class="th">类型</th>  -->
						<!-- <th class="th">配置</th> -->
						<th class="th">data_id</th>
						<th class="th">因子</th>
						<th class="th">精度</th>
						<th class="th">单位</th>
						<th class="th">注释</th>
						<th class="th">操作</th>
					</tr>
				</thead>
				<tbody>
					<!-- {foreach $response.metadatas as $metadata} -->
					<c:forEach items="${metadatas}" var="metadata">
						<tr>
							<td class="td hd">${metadata.metadataId}</td>
							<!-- <td class="td hd">${metadata.period}</td> -->
							<td class="td hd">
								<%
                            Metadata metadata = (Metadata)pageContext.getAttribute("metadata");
                            String periodName = "NA";
                            switch(metadata.getPeriod()) {
                            case 1: 
                            	periodName = "天";
                            	break;
                            case 2:
                            	periodName = "周";
                            	break;
                            case 3: 
                            	periodName = "分";
                            	break;
                            case 4: 
                            	periodName = "时";
                            	break;
                            case 5: 
                            	periodName = "版本周";
                            	break;
                            default: 
                            }
                        %> <%=periodName%>
							</td>
							<td class="td hd">${metadata.metadataName}</td>
							<!-- 
                    <td class="td hd">{$metadata.type_name}</td>
                    {if $metadata.type=='1'}
                    <td class="td hd">stid={$metadata.stid}&#38;sstid={$metadata.sstid}&#38;op_type={$metadata.op_type}&#38;op_fields={$metadata.op_fields}&#38;range={$metadata.range}</td>
                    {else}
                    <td class="td hd">task_id={$metadata.task_id}&#38;range={$metadata.range}</td>
                    {/if}
                     -->
							<td class="td hd">${metadata.dataId}</td>
							<td class="td hd">${metadata.factor}</td>
							<td class="td hd">${metadata.precision}</td>
							<td class="td hd">${metadata.unit}</td>
							<td class="td hd">${metadata.comment}</td>
							<td class="td hd"><a href="javascript: void(0);"
								class="upd-btn btn-green"
								data='<%=com.alibaba.fastjson.JSON.toJSONString(pageContext.getAttribute("metadata"))%>'
								title="修改元数据配置">修改</a> <a href="javascript: void(0);"
								class="del-btn btn-green"
								data='<%=com.alibaba.fastjson.JSON.toJSONString(pageContext.getAttribute("metadata"))%>'
								title="删除元数据配置">删除</a></td>
						</tr>
					</c:forEach>
					<!-- {/foreach} -->
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
		src="<c:url value='/js/data/page/conf/metadata.js'/>?v=<%=System.currentTimeMillis()%>"></script>
</cc:overwrite>
<%@ include file="../layout/head.jsp"%>
