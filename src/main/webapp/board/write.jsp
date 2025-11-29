<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.kostock.model.dto.UserDTO" %>
<%
	UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
	int categoryId = (request.getAttribute("categoryId") == null) ? 1
                    : (Integer) request.getAttribute("categoryId");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>글쓰기</title>
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

<a href="<%= request.getContextPath() %>/index.jsp">홈페이지</a>
<hr>

<h2>게시글 작성</h2>
<form action="<%= request.getContextPath() %>/board/write.do" method="post">
    <input type="hidden" name="categoryId" value="<%= categoryId %>">
    종목코드(선택): <input type="text" name="stockCode"><br><br>
    제목: <input type="text" name="title" size="50"><br><br>
    내용:<br>
    <textarea name="content" cols="60" rows="10"></textarea><br><br>
    <button type="submit">작성하기</button>
</form>

</body>
</html>
