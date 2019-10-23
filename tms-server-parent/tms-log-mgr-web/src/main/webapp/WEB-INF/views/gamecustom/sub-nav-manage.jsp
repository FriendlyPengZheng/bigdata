<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="wrapper">
	<ul class="main-sub-nav">
		<li class="sub-nav-li"><c:forEach var="gameinfo"
				items="${navigator.game}">
				<c:if test="${game_id == gameinfo.key}">
					<a href="javascript:void(0);">${gameinfo.value['gameName']}</a>
				</c:if>
			</c:forEach></li>
		<li class="sub-nav-li posr stat-button" title="返回查看"><a
			href="${base_url}gamecustom/tree/index/01?game_id=${requestScope.game_id}"
			class="bar-view">返回查看</a></li>
	</ul>
</div>