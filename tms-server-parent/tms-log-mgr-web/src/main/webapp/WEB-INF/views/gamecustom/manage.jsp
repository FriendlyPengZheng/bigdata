<%@page import="org.springframework.web.context.request.RequestScope"%>
<%@page import="com.taomee.tms.mgr.entity.Metadata"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/tmstag" prefix="cc"%>
<cc:overwrite name="pagetitle">游戏自定义数据</cc:overwrite>
<cc:overwrite name="pagecss">
	<style type="text/css">
@import
url(
"<c:url value="
/
css
/data/page/gamecustom/managa.css"/>");
</style>
	<style type="text/css">
@import
url(
"<c:url value="
/
css
/data/page/gamecustom/gamecustom.css"/>");
</style>
</cc:overwrite>
<cc:overwrite name="subnav"><%@ include
		file="./sub-nav-manage.jsp"%></cc:overwrite>
<cc:overwrite name="content">
	<div class="main with-aside">
		<div class="aside">
			<span class="search-con" id="J_search"> <span
				class="search-tips" style="display: none;">没有找到匹配项~</span> <input
				type="text" class="srh-txt"><i class="search-tag">&nbsp;</i>
			</span>
			<div id="J_tree"></div>
		</div>
		<div class="content clearfix">
			<input type="hidden" id="J_paramGameId"
				value="${requestScope.game_id}" />
			<div class="content-body">
				<div id="J_contentHead" class="head-tools clearfix">
					<span class="links link-node" style="display: none;"> <a
						class="a-link a-link-node"><i class="node">&nbsp;</i><span>添加节点</span></a>
						<a class="a-link a-link-merge"><i class="merge">&nbsp;</i><span>合并节点</span></a>
						<span class="fr">重命名时，按ESC（退出键）取消。</span>
					</span> <span class="links link-event" style="display: none;"> <span
						class="fr">重命名时，按ESC（退出键）取消。</span>
					</span>
					<div class="batch fl" style="display: none;">
						<a class="a-link a-link-delete"><i class="delete">&nbsp;</i><span>批量删除</span></a>
					</div>
					<div class="report-only batch fl" style="display: none;">
						<a class="a-link a-link-delete"><i class="delete">&nbsp;</i><span>移到回收站</span></a>
					</div>
				</div>
				<div id="J_content"></div>
			</div>
		</div>
	</div>
	<ul class="context-menu" id="J_rMenu" style="display: none;">
		<li type="rename">重命名</li>
		<li type="look" style="display: none;">子项排序及删除</li>
		<li type="set" style="display: none;">设置</li>
		<li type="back" style="display: none;">找回名称</li>
		<li type="delete">删除</li>
	</ul>
</cc:overwrite>

<cc:overwrite name="pagejs">
	<script
		src="<c:url value='/js/common/jquery.ui.mouse.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/jquery.ui.draggable.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/jquery.ui.droppable.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/jquery.ui.sortable.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/select.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/navigator.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/datepick.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/jquery.cookie.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/jsTree/jquery.jstree.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/Page.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/tm.tabs.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/draw/highstock.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/tm.draw.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/jquery.choose.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/stat.table.js'/>?v=<%=System.currentTimeMillis() %>">"</script>
	<script
		src="<c:url value='/js/data/common/tm.module.ok.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/usage.ok.min.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/page/gamecustom/manage.js'/>?v=<%=System.currentTimeMillis()%>"></script>
</cc:overwrite>

<%@ include file="../layout/head.jsp"%>