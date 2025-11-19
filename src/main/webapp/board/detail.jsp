<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.kostock.model.dto.PostDTO" %>
<%
    PostDTO post = (PostDTO) request.getAttribute("post");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 상세보기</title>
</head>
<body>

<%
    if (post == null) {
%>
    <h3>존재하지 않는 게시글입니다.</h3>
    <a href="<%= request.getContextPath() %>/board/list.do?categoryId=1">목록으로</a>
<%
    } else {
%>

<h2><%= post.getTitle() %></h2>

<p>
    작성자: <%= post.getUserid() %><br>
    작성일: <%= post.getCreatedAt() %><br>
    조회수: <%= post.getViewCount() %><br>
    카테고리 ID: <%= post.getCategoryId() %><br>
    <% if (post.getStockCode() != null) { %>
        종목코드: <%= post.getStockCode() %><br>
    <% } %>
</p>

<hr>

<pre><%= post.getContent() %></pre>

<hr>

<a href="<%= request.getContextPath() %>/board/list.do?categoryId=<%= post.getCategoryId() %>">
    목록으로
</a>

<%
    }
%>

</body>
</html>
