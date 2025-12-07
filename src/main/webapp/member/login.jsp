<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.kostock.model.dto.UserDTO" %>

<%
    UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
    String cp = request.getContextPath();
    String error = (String) request.getAttribute("error");   // 로그인 실패 메시지용
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>로그인 - KoStock</title>
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

    <!-- 로그인 카드 -->
    <div class="auth-container">
        <div class="auth-card">
            <h2 class="auth-title">로그인</h2>
            <p class="auth-subtitle">KoStock 계정으로 로그인하세요.</p>

            <% if (error != null) { %>
                <div class="auth-error"><%= error %></div>
            <% } %>
            
            <form action="<%= cp %>/member/login.do" method="post" class="auth-form">
                <div class="auth-form-row">
                    <label for="userid" class="auth-label">아이디</label>
                    <input type="text" id="userid" name="userid"
                           class="auth-input" placeholder="아이디를 입력하세요">
                </div>
                <div class="auth-form-row">
                    <label for="password" class="auth-label">비밀번호</label>
                    <input type="password" id="password" name="password"
                           class="auth-input" placeholder="비밀번호를 입력하세요">
                </div>
                <div class="auth-actions">
                    <button type="submit" class="btn btn-primary">로그인</button>
                </div>
            </form>
            <div class="auth-footer">
                아직 계정이 없다면
                <a href="<%= cp %>/member/join.jsp">회원가입</a>
            </div>
        </div>
    </div>
</div>
</body>
</html>
