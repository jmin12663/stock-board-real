<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/css/main.css">

<%@ page import="com.kostock.model.dto.PostDTO" %>
<%@ page import="com.kostock.model.dto.UserDTO" %>
<%
    PostDTO post = (PostDTO) request.getAttribute("post");
	UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 수정</title>
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

<a href="<%= request.getContextPath() %>/board/list.do?categoryId=1">홈페이지</a>
<hr>

<h2>게시글 수정</h2>

<form action="<%= request.getContextPath() %>/board/edit.do" method="post">
    <input type="hidden" name="postId" value="<%= post.getPostId() %>">
    <input type="hidden" name="categoryId" value="<%= post.getCategoryId() %>">

    종목코드(선택): <input type="text" name="stockCode" value="<%= (post.getStockCode()==null?"":post.getStockCode()) %>"><br><br>
    제목: <input type="text" name="title" size="50" value="<%= post.getTitle() %>"><br><br>
    내용:<br>
    <textarea name="content" cols="60" rows="10"><%= post.getContent() %></textarea><br><br>

    <button type="submit">수정완료</button>
</form>

<br>
<!-- <a href="<%= request.getContextPath() %>/board/detail.do?postId=<%= post.getPostId() %>">취소</a> -->

<form action="<%= request.getContextPath() %>/board/detail.do" method="get" style="display:inline;">
    <input type="hidden" name="PostId" value="<%= post.getPostId() %>">
    <button type="submit">취소</button>
</form>

</body>
</html>
