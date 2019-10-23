<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="wrapper">
	<ul class="main-sub-nav">
		<li class="sub-nav-li"><select id="J_game">
				<c:forEach var="email" items="${requestScope.emails}">
					<c:if test="${email.current > 0 }">
						<option data-id="${email.emailId}" data-href="${email.url}"
							selected="true">${email.subject}</option>
					</c:if>
					<c:if test="${email.current == 0 }">
						<option data-id="${email.emailId}" data-href="${email.url}">${email.subject}
						</option>
					</c:if>
				</c:forEach>
		</select></li>
		<li class="sub-nav-li fr"><a href="javascript: void(0);"
			class="email-send" data-test="0"><i class="iconfont icon-send"></i>发送</a>
			<a href="javascript: void(0);" class="email-send" data-test="1"><i
				class="iconfont icon-send"></i>测试</a> <a href="javascript: void(0);"
			class="weixin-send">微信</a> <!-- <a href="{$response.aside[key($response.aside)].url}&view_r={$response.param.r}&email_id={$response.param.email_id}" class="email-manage"><i class="iconfont icon-config"></i>Email管理</a> -->
			<a href="${requestScope.manage_url}" class="email-manage"><i
				class="iconfont icon-config"></i>Email管理</a></li>
	</ul>
</div>