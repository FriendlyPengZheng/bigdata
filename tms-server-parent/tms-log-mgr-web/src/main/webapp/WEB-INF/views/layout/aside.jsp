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
            ">
                <a href="${aside.value['url']}"
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
								class="child
                            <c:if test="${!empty child.value['current']}">
                            cur
                            </c:if>
                            "
								data-key="${child.value['key']}"><a
								href="${child.value['url']}" class="tit"><span>${child.value['name']}</span></a></li>
						</c:forEach>
					</c:if>
				</ol>
				</li>
		</c:if>
		</c:forEach>
		</c:if>
	</ul>
</div>