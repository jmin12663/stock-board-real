<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.kostock.model.dto.UserDTO" %>
<%
	response.sendRedirect(request.getContextPath() + "/board/list.do?categoryId=1");
	UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");

%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>KoStock 메인</title>
</head>
<body>

<% if (loginUser == null) { %>
    <!-- 로그인 안된 상태 -->
    <a href="<%= request.getContextPath() %>/member/login.jsp">로그인</a> |
    <a href="<%= request.getContextPath() %>/member/join.jsp">회원가입</a> 
<% } else { %>
    <!-- 로그인 된 상태 -->
    <span><%= loginUser.getName() %>님 (역할: <%= loginUser.getRole() %>)</span>
    <a href="<%= request.getContextPath() %>/member/logout.do">로그아웃</a>
<% } %>

<hr>
<h2>KoStock 메인 화면</h2>
<jsp:include page="/common/nav.jsp" />


</body>
</html>
