<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">

<%
    Integer categoryIdObj = (Integer) request.getAttribute("categoryId");
    int categoryId = (categoryIdObj == null) ? -1 : categoryIdObj;  // ✅ 기본값을 -1로

    String cp = request.getContextPath();

    com.kostock.model.dto.UserDTO loginUser =
            (com.kostock.model.dto.UserDTO) session.getAttribute("loginUser");
%>


<div class="category-bar">
	<a href="<%=cp%>/board/list.do?categoryId=1" class="tab <%= categoryId==1?"active":"" %>">자유게시판</a>
	<a href="<%=cp%>/board/list.do?categoryId=2" class="tab <%= categoryId==2?"active":"" %>">코스피게시판</a>
	<a href="<%=cp%>/board/list.do?categoryId=3" class="tab <%= categoryId==3?"active":"" %>">정보공유게시판</a>

	<a href="<%=cp%>/trade/calendar.do"
   class="tab tab-record <%= request.getRequestURI().contains("/calendar") ? "active" : "" %>">
   매매기록
</a>

</div>
