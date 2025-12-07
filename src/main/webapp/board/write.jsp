<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">


<%@ page import="com.kostock.model.dto.UserDTO" %>
<%@ page import="com.kostock.model.dto.PostDTO" %>

<%
	PostDTO post = (PostDTO) request.getAttribute("post");
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

<!-- 제목 -->
    <h2 class="board-title">게시글 작성</h2>

    <!-- 글쓰기 폼 카드 -->
    <div class="post-form-card">
        <form action="<%= request.getContextPath() %>/board/write.do" method="post">
            <input type="hidden" name="categoryId" value="<%= categoryId %>">
            <div class="form-row">
                <label for="title" class="form-label">제목</label> <!-- 제목 -->
                <input type="text"
                       id="title"
                       name="title"
                       class="form-input"
                       placeholder="제목을 입력하세요">
            </div>           
            <div class="form-row"><!-- 내용 -->
                <label for="content" class="form-label">내용</label>
                <textarea id="content"
                          name="content"
                          class="form-textarea"
                          placeholder="내용을 입력하세요"></textarea>
            </div>          
            <div class="form-actions"><!-- 버튼 -->
                <a href="<%= request.getContextPath() %>/board/list.do?categoryId=1"
           class="btn btn-secondary btn-sm">
            목록으로 </a>
                <button type="submit" class="btn btn-primary btn-sm">
                    작성하기
                </button>                
            </div>
        </form>
    </div>  <!-- .post-form-card 끝 -->
</div>      <!-- .wrapper 끝 -->
</body>
</html>

