<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.kostock.model.dto.UserDTO" %>
<%
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
    <span><%= loginUser.getName() %>님 환영합니다! (역할: <%= loginUser.getRole() %>)</span>
    <a href="<%= request.getContextPath() %>/member/logout.do">로그아웃</a>
<% } %>

<hr>

<h2>KoStock 메인 화면</h2>
	<p>
    	<a href="<%= request.getContextPath() %>/board/list.do?categoryId=1">
        	자유게시판 바로가기
    	</a>
	</p>
</body>
</html>
