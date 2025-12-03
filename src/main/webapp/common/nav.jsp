<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/css/main.css">

<%
    int categoryId = (request.getAttribute("categoryId") == null) ? 1
                    : (Integer) request.getAttribute("categoryId");
    String cp = request.getContextPath();
    
    com.kostock.model.dto.UserDTO loginUser =
            (com.kostock.model.dto.UserDTO) session.getAttribute("loginUser");
%>

<div style="border-bottom:1px solid #ddd; padding:10px 0; margin-bottom:10px;">
	<a href="<%=cp%>/board/list.do?categoryId=1" style="margin-right:10px; <%= (categoryId==1?"font-weight:bold;":"") %>">자유게시판</a>
	<a href="<%=cp%>/board/list.do?categoryId=2" style="margin-right:10px; <%= (categoryId==2?"font-weight:bold;":"") %>">코스피게시판</a>
	<a href="<%=cp%>/board/list.do?categoryId=3" style="margin-right:10px; <%= (categoryId==3?"font-weight:bold;":"") %>">정보공유게시판</a>
    <a href="<%= request.getContextPath() %>/trade/calendar.do">매매기록</a>

</div>
