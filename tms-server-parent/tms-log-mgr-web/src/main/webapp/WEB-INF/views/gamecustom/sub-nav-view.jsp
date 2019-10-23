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
			<li class="sub-nav-li posr stat-button" title="配置管理"><a
				href="${base_url}gamecustom/manage/index/01?game_id=${requestScope.game_id}"
				class="bar-manage">配置管理</a></li>
		</c:if>
	</ul>
</div>