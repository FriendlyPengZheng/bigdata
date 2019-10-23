<%@page import="org.springframework.web.context.request.RequestScope"%>
<%@page import="com.taomee.tms.mgr.entity.Metadata"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/tmstag" prefix="cc"%>
<link rel="stylesheet" type="text/css"
	href="../../../../css/data/page/gameanalysis/conf1.css">
<link rel="stylesheet" type="text/css"
	href="../../../../css/data/iconfont.css">
<cc:overwrite name="pagetitle">游戏分析</cc:overwrite>
<cc:overwrite name="subnav"><%@ include
		file="./sub-nav.jsp"%></cc:overwrite>
<cc:overwrite name="content">
	<div class="main with-aside">
		<%@ include file="./aside.jsp"%>

		<div class="content clearfix">
			<!-- START -->
			<div class="tips" id="J_timeTips" style="display: none;">
				<a class="tips-close" href="javascript:void(0);">X</a>
				<div class="tips-text">
					<span class="tit">注意：</span>页面只能显示<span class="tit period">${response.param.from}~${response.param.to}</span>时间段的数据，下载数据时间段没有限制哦。
				</div>
			</div>
			<div class="tips">
				<a class="tips-close" href="javascript:void(0);">X</a>
				<div class="tips-text">
					<p>可通过以下两种方式上传道具名称：</p>
					<p>
						1、从商品管理系统中，点击【<span class="fb">导出item表</span>】按钮下载得到item的xml文件，再在此处上传该文件即可；
					</p>
					<p>
						2、从本地excel上传道具表：先将道具id、道具名称复制到给定的<a href="doc/道具模板.xlsx"
							title="%s" class="fb underline">【模版Excel】</a>上<span class="tit">（注意第一行的名称不能更改，且保证两列）</span>，另存为xml文件后，选择上传即可。
					</p>
				</div>
			</div>
			<%-- <input type="hidden" id="J_r" value="${requestScope.r}" /> --%>
			<div class="content-header clearfix">
				<div id="J_headerTools" class="header-tools">
					<form class="line-blk" action="common/economy/importItem?ajax=1"
						method="post" enctype="multipart/form-data">
						<input type="hidden" name="sstid" value="_coinsbuyitem_" /> <input
							type="file" class="file-up" name="files" id="J_file" /> <input
							type="hidden" id="J_paramGameId" name="game_id"
							value="${requestScope.game_id}"> <a
							href="javascript:void(0);" class="add-btn" id="J_addMb" />上传道具名称</a>
					</form>
					<a href="javascript:void(0);" class="add-btn" id="J_editMb">批量分配类别</a>
				</div>
			</div>
			<!--END  -->
			<input type="hidden" id="J_paramGameId"
				value="${requestScope.game_id}" /> <input type="hidden"
				id="J_paramPlatformId" value="${requestScope.platform_id}" />
			<%--<input type="hidden" id="J_paramZoneId" value="${requestScope.zone_id}" />
        <input type="hidden" id="J_paramServerId" value="${requestScope.server_id}" />--%>

			<%-- <div class="content-header clearfix" id="J_contentHeader">
       		<div class="select-con" id="J_zoneServer" tabindex="0"></div>
            <select id="J_platform" class="r">
            	<option data-id="${requestScope.platform_id}" selected>全平台</option>
            	<c:if test="${!empty requestScope.platform}">
            		<c:forEach var="platform" items="${requestScope.platform}">
            			<option data-id="${platform.platformId}">${platform.platformName}</option>
            		</c:forEach>
            	</c:if>
            </select>
            <div class="datepicker-trigger radius5-all r mr5" id="J_from_to" >
                <input type="hidden" id="J_from" value="${response.param.from}" />
                <input type="hidden" id="J_to" value="${response.param.to}" />
                <input type="hidden" id="J_showFrom" value="${response.param.from}" />
                <input type="hidden" id="J_showTo" value="${response.param.to}" />
                
                <input type="hidden" id="J_from" value="${requestScope.from}" />
                <input type="hidden" id="J_to" value= "${requestScope.to}" />
                <input type="hidden" id="J_showFrom" value="${requestScope.from}"/>
                <input type="hidden" id="J_showTo" value="${requestScope.to}"/>
                <i class="datepicker-icon"></i>
                <input class="title" type="text" id="J_date" value= "${requestScope.from}~${requestScope.to}" />
                <i class="datepicker-arrow"></i>
            </div>
        </div> --%>

			<div class="content-body" id="J_contentBody"></div>
		</div>
	</div>
</cc:overwrite>

<cc:overwrite name="pagejs">
	<script
		src="<c:url value='/js/data/page/conf/fac1.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/util.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/dlg.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/select.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/navigator.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/datepick.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/tm.datatable.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/Page.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/tm.tabs.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/draw/highstock.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/tm.draw.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/common.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/heatmap.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/jquery.choose.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/stat.table.js'/>?v=<%=System.currentTimeMillis() %>"></script>
	<script
		src="<c:url value='/js/data/page/gameanalysis/addtofavor.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<%-- 
    <script src="<c:url value='/js/data/common/tm.module.ok.js'/>?v=<%=System.currentTimeMillis()%>"></script> --%>
	<script
		src="<c:url value='/js/data/common/tm.module.ok1.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/usage.ok.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/page/gameanalysis/mbsale.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/page/gameanalysis/mbmanage.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/page/gameanalysis/period-gpzs.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script src="<c:url value='/js/common/jquery.form.js'/>"></script>
</cc:overwrite>
<%@ include file="../layout/head.jsp"%>