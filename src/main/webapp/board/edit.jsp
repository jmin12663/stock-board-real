<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.kostock.model.dto.PostDTO" %>
<%
    PostDTO post = (PostDTO) request.getAttribute("post");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 수정</title>
</head>
<body>

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
<a href="<%= request.getContextPath() %>/board/detail.do?postId=<%= post.getPostId() %>">취소</a>

</body>
</html>
