<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.kostock.model.dto.UserDTO" %>

<%
	response.sendRedirect(request.getContextPath() + "/board/list.do?categoryId=1");
	UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");

%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>KoStock 메인</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">
    
</head>
<body>
</body>
</html>