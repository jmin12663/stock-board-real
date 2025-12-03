<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/css/main.css">

<%@ page import="java.util.List" %>
<%@ page import="com.kostock.model.dto.PostDTO" %>
<%@ page import="com.kostock.model.dto.UserDTO" %>
<%@ page import="java.net.URLEncoder" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>


<%
	UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");

    List<PostDTO> postList = (List<PostDTO>) request.getAttribute("postList");
    int categoryId = (request.getAttribute("categoryId") == null) ? 1
                    : (Integer) request.getAttribute("categoryId");
    int currentpage = (request.getAttribute("page") == null) ? 1 : (Integer) request.getAttribute("page");
    int totalPages = (request.getAttribute("totalPages") == null) ? 1 : (Integer) request.getAttribute("totalPages");
    
    String searchWord = request.getParameter("searchWord");
    if (searchWord == null) {
        searchWord = (String) request.getAttribute("searchWord");
    }
    
    String field = request.getParameter("field");
    if (field == null) field = (String)request.getAttribute("field");
    if (field == null) field = "title"; // 기본
    
    String q = "";
    if (searchWord != null && !searchWord.isEmpty()) {
    	q = "&field=" + field + "&searchWord=" + URLEncoder.encode(searchWord, "UTF-8"); }
    
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 목록</title>
</head>
<body>

<% if (loginUser == null) { %>
    <!-- 로그인 안된 상태 -->
    <a href="<%= request.getContextPath() %>/member/login.jsp">로그인</a> |
    <a href="<%= request.getContextPath() %>/member/join.jsp">회원가입</a>
<% } else { %>
    <!-- 로그인 된 상태 -->
    <span><%= loginUser.getName() %>님 (역할: <%= loginUser.getRole() %>)</span>
    <a href="<%= request.getContextPath() %>/member/logout.do">로그아웃</a>
<% } %>

<a href="<%= request.getContextPath() %>/board/list.do?categoryId=1">홈페이지</a>
<hr>

<!-- 카테고리 -->
<jsp:include page="/common/nav.jsp" />

<h2>게시판 목록 (카테고리 ID: <%= categoryId %>)</h2>
<!-- 지수 요약 바 및 차트 -->
<jsp:include page="/common/chart.jsp" />

<!--  검색 폼, list.do로 요청 -->
<form method="get" action="<%= request.getContextPath() %>/board/list.do" align="center" style="margin-top:10px;">
  <input type="hidden" name="categoryId" value="<%= categoryId %>">
  <input type="hidden" name="page" value="1">

  <select name="field">
    <option value="title" <%= "title".equals(field) ? "selected" : "" %>>제목</option>
    <option value="content" <%= "content".equals(field) ? "selected" : "" %>>내용</option>
  </select>

  <input type="text" name="searchWord" value="<%= (searchWord == null) ? "" : searchWord %>"  placeholder="검색어 입력" />
  <button type="submit">검색하기</button>
</form>

<table border="1" width="90%" cellspacing="0" cellpadding="5" align="center">
    <tr>
        <th>번호</th>
        <th>제목</th>
        <th>작성자</th>
        <th>작성일</th>
        <th>조회수</th>
    </tr>

    <%
        if (postList == null || postList.isEmpty()) {
    %>
        <tr>
            <td colspan="5">등록된 게시글이 없습니다.</td>
        </tr>
    <%
        } else {
            for (PostDTO dto : postList) {
    %>
        <tr>
            <td><%= dto.getListNo() %></td>
            <td>
                <a href="<%= request.getContextPath() %>/board/detail.do?postId=<%= dto.getPostId() %>">
                    <%= dto.getTitle() %>
                </a>
            </td>
            <td><%= dto.getUserid() %></td>
            <td><%= dto.getCreatedAt() %></td>
            <td><%= dto.getViewCount() %></td>
        </tr>
    <%
            }
        }
    %>
</table>


<div align="center" style="margin-top:10px;">
    <%-- 이전 --%>
    <% if (currentpage > 1) { %>
        <a href="<%= request.getContextPath() %>/board/list.do?categoryId=<%= categoryId %>&page=<%= currentpage-1 %><%= q %>">이전</a>
    <% } %>

    <%-- 페이지 번호들 --%>
    <%
        for (int p = 1; p <= totalPages; p++) {
            if (p == currentpage) {
    %>
                <b>[<%= p %>]</b>
    <%
            } else {
    %>
                <a href="<%= request.getContextPath() %>/board/list.do?categoryId=<%= categoryId %>&page=<%= p %><%= q %>">[<%= p %>]</a>
    <%
            }
        }
    %>

    <%-- 다음 --%>
    <% if (currentpage < totalPages) { %>
        <a href="<%= request.getContextPath() %>/board/list.do?categoryId=<%= categoryId %>&page=<%= currentpage+1 %><%= q %>">다음</a>
    <% } %>
</div>
<table width="90%" align="center">
    <tr>
        <td align="right">
            <button onclick="location.href='<%= request.getContextPath() %>/board/writeForm.do?categoryId=<%= categoryId %>'">
                글쓰기 
            </button>
        </td>
    </tr>
</table>		
</body>
</html>
