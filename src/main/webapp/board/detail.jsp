<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/css/main.css">

<%@ page import="com.kostock.model.dto.PostDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="com.kostock.model.dto.CommentDTO" %>
<%@ page import="com.kostock.model.dto.UserDTO" %>
<%
    PostDTO post = (PostDTO) request.getAttribute("post");
	UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 상세보기</title>
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
<%
    List<CommentDTO> comments = (List<CommentDTO>) request.getAttribute("comments");
    
%>

<hr>
<h3>댓글</h3>

<!-- 댓글 목록 -->
<%
    if (comments == null || comments.isEmpty()) {
%>
    <p>아직 댓글이 없습니다.</p>
<%
    } else {
        for (CommentDTO c : comments) {
%>
    <div style="border:1px solid #ddd; padding:8px; margin:6px 0;">
        <b><%= c.getUserid() %></b>
        <span style="color:gray;">(<%= c.getCreatedAt() %>)</span><br>
        <%= c.getContent() %>
    </div>
<%
        }
    }
%>

<!-- 댓글 작성 -->
<%
    if (loginUser != null) {
%>
    <form action="<%= request.getContextPath() %>/comment/write.do" method="post" style="margin-top:10px;">
        <input type="hidden" name="postId" value="<%= post.getPostId() %>">
        <textarea name="content" rows="3" cols="60" placeholder="댓글을 입력하세요"></textarea><br>
        <button type="submit">댓글 등록</button>
    </form>
<%
    } else {
%>
    <p>
        댓글을 작성하려면
        <a href="<%= request.getContextPath() %>/member/login.jsp">로그인</a> 하세요.
    </p>
<%
    }
%>

<hr>
<form action="<%= request.getContextPath() %>/board/list.do" method="get" style="display:inline;">
    <input type="hidden" name="categoryId" value="<%= post.getCategoryId() %>">
    <button type="submit">목록으로</button>
</form>

<% if (loginUser != null && loginUser.getUserid().equals(post.getUserid())) { %>
    
    <form action="<%= request.getContextPath() %>/board/editForm.do" method="get" style="display:inline;">
        <input type="hidden" name="postId" value="<%= post.getPostId() %>">
        <button type="submit">수정</button>
    </form>

    <form action="<%= request.getContextPath() %>/board/delete.do" method="post" style="display:inline;">
        <input type="hidden" name="postId" value="<%= post.getPostId() %>">
        <input type="hidden" name="categoryId" value="<%= post.getCategoryId() %>">
        <button type="submit" onclick="return confirm('정말 삭제할까요?');">삭제</button>
    </form>
    
<% } %>
<%
    }
%>

</body>
</html>
