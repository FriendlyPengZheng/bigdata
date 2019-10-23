<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tmstag" prefix="cc"%>
<cc:overwrite name="pagetitle">测试页面标题</cc:overwrite>
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
		<%@ include file="gameanalysis/aside.jsp"%>
	</div>
</cc:overwrite>
<cc:overwrite name="pagejs">
	<script src="<c:url value='/js/common/select.js'/>"></script>
	<script src="<c:url value='/js/data/common/navigator.js'/>"></script>
</cc:overwrite>
<%@ include file="layout/head.jsp"%>