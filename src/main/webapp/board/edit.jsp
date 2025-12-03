<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">

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
<div class="wrapper">

    <div class="header">
        <div class="logo">
            <a href="<%= request.getContextPath() %>/index.jsp">KoStock</a>
        </div>
        
        <div class="nav-right">
        <% if (loginUser == null) { %>
             <a href="<%= request.getContextPath() %>/member/login.jsp">로그인</a>
             <a href="<%= request.getContextPath() %>/member/join.jsp">회원가입</a>
        <% } else { %>
             <span><%= loginUser.getName() %>님 (역할: <%= loginUser.getRole() %>)</span>
             <a href="<%= request.getContextPath() %>/member/logout.do">로그아웃</a>
        <% } %>
            <a href="<%= request.getContextPath() %>/board/list.do?categoryId=1">홈페이지</a>
        </div>
    </div>

    <!-- 제목 -->
    <h2 class="board-title">게시글 수정</h2>

    <!-- 수정 폼 카드 -->
    <div class="post-form-card">
        <form action="<%= request.getContextPath() %>/board/edit.do" method="post">
            <input type="hidden" name="postId" value="<%= post.getPostId() %>">
            <input type="hidden" name="categoryId" value="<%= post.getCategoryId() %>">

            <!-- 제목 -->
            <div class="form-row">
                <label for="title" class="form-label">제목</label>
                <input type="text"
                       id="title"
                       name="title"
                       class="form-input"
                       value="<%= post.getTitle() %>">
            </div>

            <!-- 내용 -->
            <div class="form-row">
                <label for="content" class="form-label">내용</label>
                <textarea id="content"
                          name="content"
                          class="form-textarea"><%= post.getContent() %></textarea>
            </div>

            <!-- 버튼 -->
            <div class="form-actions">
                <!-- 취소: 상세로 돌아가기 -->
                <button type="button"
                        class="btn btn-secondary btn-sm"
                        onclick="location.href='<%=request.getContextPath()%>/board/detail.do?postId=<%=post.getPostId()%>'">
                    취소
                </button>

                <!-- 수정 완료 -->
                <button type="submit" class="btn btn-primary btn-sm">
                    수정완료
                </button>
            </div>
        </form>
    </div>

</div>
</body>
</html>
