<%@page import="org.springframework.web.context.request.RequestScope"%>
<%@page import="com.taomee.tms.mgr.entity.Metadata"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/tmstag" prefix="cc"%>
<cc:overwrite name="pagetitle">游戏分析</cc:overwrite>
<cc:overwrite name="pagecss">
	<style type="text/css">
@import
url(
"<c:url value="
/
css
/data/page/gameanalysis/overview.css"/>");
</style>
</cc:overwrite>
<cc:overwrite name="subnav"><%@ include
		file="./sub-nav.jsp"%></cc:overwrite>
<cc:overwrite name="content">
	<div class="main with-aside">
		<%@ include file="./aside.jsp"%>
		<div class="tips" id="J_timeTips" style="display: none;">
			<a class="tips-close" href="javascript:void(0);">X</a>

			<!--         <div class="tips-text"><span class="tit">{TM::t('tongji', '注意：')}</span>{TM::t('tongji', -->
			<!--             '页面只能显示{1}时间段的数据，下载数据时间段没有限制哦。', ['{1}' => sprintf('<span class="tit period">%s~%s</span>', -->
			<!--             $response.param.from, $response.param.to)])} -->
			<!--         </div> -->
		</div>
		<div class="progress-wrap" id="J_progress">
			<!--         <span class="progress-title">{TM::t('tongji', '数据运算进度：')}</span> -->
			<span class="progress-title">数据运算进度</span> <span
				class="progress-text"></span>
		</div>
		<div class="content clearfix">
			<input type="hidden" id="J_paramGameId"
				value="${requestScope.game_id}" /> <input type="hidden"
				id="J_paramGameType" value="${requestScope.game_type}" /> <input
				type="hidden" id="J_r" value="${response.param.r}" /> <input
				type="hidden" id="J_gpzsId" value="${requestScope.gpzs_id}" />

			<div class="content-header clearfix" id="J_contentHeader">
				<c:if test="${empty response.datalist}">
					<ul class="view-data" id="J_viewData">
						<!-- {foreach $response.datalist as $k => $list} -->
						<c:forEach var="list" items="${response.datalist}">
							<li class="view-data{${list.key}+1}"><span class="view-txt">${list.value.name}</span>

								<!-- 需要修改 --> <c:if test="${fn:length(list.data)}">
									<strong class="imp" style="font-size: 24px;"> <c:if
											test="${list.key == 2}">
											<fmt:formatNumber type="number" maxFractionDigits="2"
												value="${list.data[0]}" />
										</c:if> <c:if test="${list.key != 2}">
											<fmt:formatNumber type="number" value="${list.data[0]}" />
										</c:if>
									</strong>
								</c:if> <c:if test="${empty response.datalist}">
									<c:forEach var="data" items="${list.data}">
										<c:if test="${data.key == 0}">
											<strong class="imp"> <c:if test="${list.key == 2}">
													<fmt:formatNumber type="number" maxFractionDigits="2"
														value="${data.value}" />
												</c:if> <c:if test="${list.key != 2}">
													<fmt:formatNumber type="number" value="${data.value}" />
												</c:if>
											</strong>
										</c:if>
										<c:if test="${data.key == 1}">
											<strong> <c:if test="${list.key == 2}">
													<fmt:formatNumber type="number" maxFractionDigits="2"
														value="${data.value}" />
												</c:if> <c:if test="${list.key == 1}">
													<fmt:formatNumber type="number" value="${data.value}" />
												</c:if>
											</strong>
										</c:if>
										<c:if test="${data.key == 2 && data.value >= 0 }">
											<strong class="up">+${data.value}%</strong>
										</c:if>
										<c:if test="${data.key == 2 && data.value < 0}">
											<strong class="down">${data.value}%</strong>
										</c:if>
									</c:forEach>
								</c:if></li>
						</c:forEach>

					</ul>
				</c:if>

				<c:choose>
					<c:when
						test="${!empty response.param.constast && response.param.contrast == 1}">

						<div class="datepicker-trigger single radius5-all fr mr5 mt20"
							id="J_single">
							<input type="hidden" id="J_single_from" value="2016-12-12" /> <i
								class="datepicker-icon"></i> <input class="title" type="text"
								id="J_single_date" value="2016-12-12" /> <i
								class="datepicker-arrow"></i>
						</div>
					</c:when>
					<c:when
						test="${empty response.param.constast || response.param.contrast != 1}">
						<div class="datepicker-trigger single radius5-all fr mr5 mt20"
							id="J_single" style="display: none;">
							<input type="hidden" id="J_single_from"
								value="${requestScope.constract_from}" /> <i
								class="datepicker-icon"></i> <input class="title" type="text"
								id="J_single_date" value="${requestScope.constract_from}" /> <i
								class="datepicker-arrow"></i>
						</div>
					</c:when>
				</c:choose>
				<label class="fr cstcon mt20"> <c:if
						test="${!empty response.param.contrast && response.param.contrast == 1}">
						<input type="checkbox" class="cstchk mr5" id="J_contrast" checked />
						<!-- 先不考虑国际化 -->

						<!-- <span class="csttxt" style="display: none;">{TM::t('tongji', '选择对比开始时间')}</span>-->
						<span class="csttxt" style="display: none;">选择对比开始时间</span>-->
					</c:if> <c:if
						test="${empty response.param.contrast || response.param.contrast != 1}">
						<input type="checkbox" class="cstchk mr5" id="J_contrast" />
						<!-- <span class="csttxt">{TM::t('tongji', '选择对比开始时间')}</span> -->
						<span class="csttxt">选择对比开始时间</span>
					</c:if>
				</label>

				<div class="datepicker-trigger radius5-all fr mr5 mt20"
					id="J_from_to">
					<input type="hidden" id="J_from" value="${requestScope.from}" /> <input
						type="hidden" id="J_to" value="${requestScope.to}" /> <input
						type="hidden" id="J_showFrom" value="${requestScope.from}" /> <input
						type="hidden" id="J_showTo" value="${requestScope.to}" /> <i
						class="datepicker-icon"></i> <input class="title" type="text"
						id="J_date" value="${requestScope.from}~${requestScope.to}" /> <i
						class="datepicker-arrow"></i>
				</div>
			</div>
			<div class="content-body" id="J_contentBody"></div>
		</div>
	</div>

</cc:overwrite>
<cc:overwrite name="pagejs">
	<script
		src="<c:url value='/js/common/select.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/navigator.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/datepick.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/jquery.form.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/tm.datatable.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/Page.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/tm.tabs.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/Table.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/draw/highstock.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/tm.draw.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/common/Draw.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/common.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/jquery.choose.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/page/conf/conf.module.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/page/conf/conf.usage.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/page/conf/fac.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/page/conf/fac1.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/page/gameanalysis/addtofavor.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<%--     <script src="<c:url value='/js/data/page/conf/module.js'/>?v=<%=System.currentTimeMillis()%>"></script> --%>
	<script
		src="<c:url value='/js/data/common/tm.module.ok.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/common/usage.ok.min.js'/>?v=<%=System.currentTimeMillis()%>"></script>
	<script
		src="<c:url value='/js/data/page/gameanalysis/overview.js'/>?v=<%=System.currentTimeMillis()%>"></script>
</cc:overwrite>
<%@ include file="../layout/head.jsp"%>