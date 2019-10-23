<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="wrapper">
	<ul class="main-sub-nav">
		<li class="sub-nav-li"><select id="J_game">
				<c:forEach var="gameinfo" items="${navigator.game}">
					<c:if test="${game_id == gameinfo.key}">
						<option data-id="${gameinfo.key}"
							data-href="${gameinfo.value['url']}" selected="true"
							data-key="${gameinfo.value['gameType']}">${gameinfo.value['gameName']}<c:if
								test="${gameinfo.value['gameType'] == 'webgame'}">【页游】</c:if><c:if
								test="${gameinfo.value['gameType'] == 'mobilegame'}">【手游】</c:if><c:if
								test="${gameinfo.value['gameType'] == 'site'}">【网站】</c:if><c:if
								test="${gameinfo.value['gameType'] == 'clientgame'}">【端游】</c:if><c:if
								test="${gameinfo.value['gameType'] == 'test'}">【测试】</c:if></option>
					</c:if>
					<c:if test="${game_id != gameinfo.key}">
						<option data-id="${gameinfo.key}"
							data-href="${gameinfo.value['url']}"
							data-key="${gameinfo.value['gameType']}">${gameinfo.value['gameName']}<c:if
								test="${gameinfo.value['gameType'] == 'webgame'}">【页游】</c:if><c:if
								test="${gameinfo.value['gameType'] == 'mobilegame'}">【手游】</c:if><c:if
								test="${gameinfo.value['gameType'] == 'site'}">【网站】</c:if><c:if
								test="${gameinfo.value['gameType'] == 'clientgame'}">【端游】</c:if><c:if
								test="${gameinfo.value['gameType'] == 'test'}">【测试】</c:if></option>
					</c:if>
				</c:forEach>
		</select></li>
		<c:if test="${!admin_auth}">
			<li class="sub-nav-li posr stat-button" title="模板管理"><c:if
					test="${!empty navigator.current_page.child && !empty navigator.current_page.child_name}">
					<a
						href="<c:url value='/common/page/displayPageModule' />?gameId=0&page_url=${page_url}&key=gameanalysis.<c:if test="!empty navigator.current_page.parent">${navigator.current_page.parent}</c:if>${navigator.current_page.child}&module_name=游戏分析-页游-${navigator.current_page.parent_name}-${navigator.current_page.child_name}"
						class="bar-manage">模板管理</a>
				</c:if> <c:if
					test="${empty navigator.current_page.child || empty navigator.current_page.child_name}">
					<a
						href="<c:url value='/common/page/displayPageModule' />?gameId=0&page_url=${page_url}&key=gameanalysis.${navigator.current_page.parent}&module_name=游戏分析-页游-${navigator.current_page.parent_name}"
						class="bar-manage">模板管理</a>
				</c:if></li>
		</c:if>
	</ul>
</div>