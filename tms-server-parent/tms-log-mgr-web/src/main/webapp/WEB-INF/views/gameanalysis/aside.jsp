<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="aside" id="J_aside">
	<ul class="bt">
		<c:if test="${!empty navigator.aside}">
			<c:forEach var="aside" items="${navigator.aside}">
				<c:if test="${empty response.ignore}">
					<li data-key="${aside.value['key']}"
						class="parent
            <c:if test="${!empty aside.value['current'] && aside.value['current'] == '1'}">
            cur clicked
            </c:if>
            <c:if test="${!empty aside.value['children']}">
            more-icon
            ">
						<a
				</c:if>
				<c:if test="${empty aside.value['children']}">
            stat-module" common="游戏分析" stid="${aside.value['name']}" sstid="${aside.value['name']}">
                <a href="${aside.value['url']}?game_id=${game_id}"
				</c:if>
				<c:if test="${!empty aside.value['key']}">
                class="${aside.value['key']}"
                </c:if>
                ><span>${aside.value['name']}</span>
				</a>
				<ol>
					<c:if test="${!empty aside.value['children']}">
						<c:forEach var="child" items="${aside.value['children']}">
							<li
								class="child stat-module
                            <c:if test="${!empty child.value['current']}">
                            cur
                            </c:if>
                            "
								data-key="${child.value['key']}" common="游戏分析"
								stid="${aside.value['name']}" sstid="${child.value['name']}"><a
								href="${child.value['url']}?game_id=${game_id}" class="tit"><span>${child.value['name']}</span></a></li>
						</c:forEach>
					</c:if>
				</ol>
				</li>
		</c:if>
		</c:forEach>
		</c:if>
	</ul>
</div>