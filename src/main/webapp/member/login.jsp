<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/css/main.css">
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>로그인</title>
</head>
<body>
	<a href="<%= request.getContextPath() %>/member/join.jsp">회원가입</a>
	<a href="<%= request.getContextPath() %>/board/list.do?categoryId=1">홈페이지</a>
	
	<hr>
	<h3>로그인</h3>
	<form action="/KoStock/member/login.do" method="post">
	아이디: <input type="text" name="userid"> <br>
	비밀번호: <input type="password" name="password"><br>
	<button type="submit">로그인</button>
	</form>
	
</body>
</html>