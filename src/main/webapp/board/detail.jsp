<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">

<%@ page import="com.kostock.model.dto.PostDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="com.kostock.model.dto.CommentDTO" %>
<%@ page import="com.kostock.model.dto.UserDTO" %>
<%
    PostDTO post = (PostDTO) request.getAttribute("post");
	UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
	List<CommentDTO> comments = (List<CommentDTO>) request.getAttribute("comments");
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 상세보기</title>
</head>
<body>
<div class="wrapper">

<div class="header">
    <div class="logo">
        <a href="<%= request.getContextPath() %>/index.jsp">KoStock</a>
    </div>
    
    <div class="nav-right">
	<% if (loginUser == null) { %>
   	 <!-- 로그인 안된 상태 -->
   	 <a href="<%= request.getContextPath() %>/member/login.jsp">로그인</a>
    	<a href="<%= request.getContextPath() %>/member/join.jsp">회원가입</a>
	<% } else { %>
   	 <!-- 로그인 된 상태 -->
    	<span><%= loginUser.getName() %>님 (<%= loginUser.getRole() %>)</span>
    	<a href="<%= request.getContextPath() %>/member/logout.do">로그아웃</a>
	<% } %>

	<a href="<%= request.getContextPath() %>/board/list.do?categoryId=1">홈페이지</a>
	</div>
</div>

<%
    if (post == null) {
%>
    <div class="post-empty">
        <p>존재하지 않는 게시글입니다.</p>
        <a href="<%= request.getContextPath() %>/board/list.do?categoryId=1"
           class="btn btn-secondary btn-sm">
            목록으로
        </a>
    </div>
<%
    } else {
%>

<div class="post-view">

    <!-- 제목 + 정보 -->
    <div class="post-view-header">
        <h1 class="post-title"><%= post.getTitle() %></h1>

        <div class="post-meta">
            <span class="meta-item">작성자: <b><%= post.getUserid() %></b></span>
            <span class="meta-dot">·</span>
            <span class="meta-item">작성일: <%= post.getCreatedAt() %></span>
            <span class="meta-dot">·</span>
            <span class="meta-item">조회수: <%= post.getViewCount() %></span>

            <% if (post.getStockCode() != null) { %>
                <span class="meta-dot">·</span>
                <span class="meta-item">종목코드: <%= post.getStockCode() %></span>
            <% } %>
        </div>
    </div>

    <!-- 본문 -->
    <div class="post-body">
        <pre class="post-content"><%= post.getContent() %></pre>
    </div>

    <!-- 하단 버튼 (목록 / 수정 / 삭제) -->
    <div class="post-actions">
        <form action="<%= request.getContextPath() %>/board/list.do" method="get">
            <input type="hidden" name="categoryId" value="<%= post.getCategoryId() %>">
            <button type="submit" class="btn btn-secondary btn-sm">목록으로</button>
        </form>

        <% if (loginUser != null && loginUser.getUserid().equals(post.getUserid())) { %>
            <form action="<%= request.getContextPath() %>/board/editForm.do" method="get">
                <input type="hidden" name="postId" value="<%= post.getPostId() %>">
                <button type="submit" class="btn btn-primary btn-sm">수정</button>
            </form>

            <form action="<%= request.getContextPath() %>/board/delete.do" method="post"
                  onsubmit="return confirm('정말 삭제할까요?');">
                <input type="hidden" name="postId" value="<%= post.getPostId() %>">
                <input type="hidden" name="categoryId" value="<%= post.getCategoryId() %>">
                <button type="submit" class="btn btn-danger btn-sm">삭제</button>
            </form>
        <% } %>
    </div>

</div> <!-- /.post-view -->

<!-- 댓글 영역 -->
<div class="comment-section">
    <h3 class="comment-title">댓글</h3>

    <!-- 댓글 목록 -->
    <div class="comment-list">
    <%
        if (comments == null || comments.isEmpty()) {
    %>
        <p class="comment-empty">아직 댓글이 없습니다.</p>
    <%
        } else {
            for (CommentDTO c : comments) {
    %>
        <div class="comment-item">
            <div class="comment-header">
                <span class="comment-author"><%= c.getUserid() %></span>
                <span class="comment-date"><%= c.getCreatedAt() %></span>
            </div>
            <div class="comment-body">
                <%= c.getContent() %>
            </div>
        </div>
    <%
            }
        }
    %>
    </div>

    <!-- 댓글 작성 -->
    <%
        if (loginUser != null) {
    %>
        <form action="<%= request.getContextPath() %>/comment/write.do"
              method="post" class="comment-form">
            <input type="hidden" name="postId" value="<%= post.getPostId() %>">
            <textarea name="content" rows="7" col="10" placeholder="댓글을 입력하세요"></textarea>
            <div class="comment-form-actions">
                <button type="submit" class="btn btn-primary btn-sm">댓글 등록</button>
            </div>
        </form>
    <%
        } else {
    %>
        <p class="comment-login">
            댓글을 작성하려면
            <a href="<%= request.getContextPath() %>/member/login.jsp">로그인</a> 하세요.
        </p>
    <%
        }
    %>
</div> <!-- /.comment-section -->

<%
    } // post != null end
%>

</div>
</body>
</html>
