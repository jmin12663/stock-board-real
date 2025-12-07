<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.kostock.model.dto.UserDTO" %>

<%
    String cp = request.getContextPath();
    UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
    String error = (String) request.getAttribute("error");   // 가입 실패 메시지 등
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>회원가입 - KoStock</title>
    <link rel="stylesheet" href="<%= cp %>/css/main.css">
</head>
<body>
<div class="wrapper">

    <!-- 공통 헤더 -->
    <div class="header">
        <div class="logo">
            <a href="<%= cp %>/index.jsp">KoStock</a>
        </div>

        <div class="nav-right">
        <% if (loginUser == null) { %>
            <a href="<%= cp %>/member/login.jsp">로그인</a>
            <a href="<%= cp %>/member/join.jsp">회원가입</a>
        <% } else { %>
            <span><%= loginUser.getName() %>님 (<%= loginUser.getRole() %>)</span>
            <a href="<%= cp %>/member/logout.do">로그아웃</a>
        <% } %>
            <a href="<%= cp %>/board/list.do?categoryId=1">홈페이지</a>
        </div>
    </div>

    <!-- 회원가입 카드 -->
    <div class="auth-container">
        <div class="auth-card">
            <h2 class="auth-title">회원가입</h2>
            <p class="auth-subtitle">KoStock에서 함께 투자 기록을 관리해보세요.</p>

            <% if (error != null) { %>
                <div class="auth-error"><%= error %></div>
            <% } %>

            <form action="<%= cp %>/member/join.do" method="post" class="auth-form">
                <!-- 이름 -->
                <div class="auth-form-row">
                    <label for="name" class="auth-label">이름</label>
                    <input type="text" id="name" name="name"
                           class="auth-input" placeholder="이름을 입력하세요">
                </div>
                <!-- 아이디 -->
                <div class="auth-form-row">
                    <label for="userid" class="auth-label">아이디</label>
                    <input type="text" id="userid" name="userid"
                           class="auth-input" placeholder="아이디를 입력하세요">
                </div>
                <!-- 비밀번호 -->
                <div class="auth-form-row">
                    <label for="password" class="auth-label">비밀번호</label>
                    <input type="password" id="password" name="password"
                           class="auth-input" placeholder="비밀번호를 입력하세요">
                </div>
                <!-- 비밀번호 확인 (선택이지만 있으면 UX가 좋아짐) -->
                <div class="auth-form-row">
                    <label for="password2" class="auth-label">비밀번호 확인</label>
                    <input type="password" id="password2" name="password2"
                           class="auth-input" placeholder="비밀번호를 한 번 더 입력하세요">
                </div>
                <div class="auth-actions">
                    <button type="submit" class="btn btn-primary">가입하기</button>
                </div>
            </form>
            <div class="auth-footer">
                이미 계정이 있다면
                <a href="<%= cp %>/member/login.jsp">로그인</a>
            </div>
        </div>
    </div>

</div>
</body>
</html>
