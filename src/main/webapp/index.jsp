<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.kostock.model.dto.UserDTO" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">

<%
	response.sendRedirect(request.getContextPath() + "/board/list.do?categoryId=1");
	UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");

%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>KoStock 메인</title>
</head>
<body>
</body>
</html>
