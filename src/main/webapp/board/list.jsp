<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">

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
	    	<span><%= loginUser.getName() %>님 (역할: <%= loginUser.getRole() %>)</span>
	    	<a href="<%= request.getContextPath() %>/member/logout.do">로그아웃</a>
		<% } %>
	
		<a href="<%= request.getContextPath() %>/board/list.do?categoryId=1">홈페이지</a>
	</div>
</div>

<!------------------------------------ 카테고리 -->
<jsp:include page="/common/nav.jsp" />

<div class="board-header">
	<h2 class="board-title">게시글</h2>
</div>

<!------------------------------------ 지수 요약 바 및 차트 -->
<jsp:include page="/common/chart.jsp" />

<!------------------------------------  검색 폼, list.do로 요청 -->
<div class="board-top">
  <form method="get"
        action="<%= request.getContextPath() %>/board/list.do"
        class="search-form">

    <input type="hidden" name="categoryId" value="<%= categoryId %>">
    <input type="hidden" name="page" value="1">

    <select name="field" class="search-select">
      <option value="title"   <%= "title".equals(field)   ? "selected" : "" %>>제목</option>
      <option value="content" <%= "content".equals(field) ? "selected" : "" %>>내용</option>
    </select>

    <input type="text" name="searchWord"
           value="<%= (searchWord == null) ? "" : searchWord %>"
           class="search-input"
           placeholder="검색어 입력">

    <button type="submit" class="btn-search">검색하기</button>
  </form>
</div>

<!------------------------------------  게시글 목록 -->
<table class="post-table">
	<thead>
	    <tr>
	        <th>번호</th>
	        <th>제목</th>
	        <th>작성자</th>
	        <th>작성일</th>
	        <th>조회수</th>
	    </tr>
    </thead>
	<tbody>
	    <%
	        if (postList == null || postList.isEmpty()) {
	    %>
	        <tr>
	            <td colspan="5" class="no-post">등록된 게시글이 없습니다.</td>
	        </tr>
	    <%
	        } else {
	            for (PostDTO dto : postList) {
	    %>
	        <tr>
	            <td><%= dto.getListNo() %></td>
	            <td class="title">
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
    </tbody>
</table>

<!-------------------------------------------------페이징 -->
<div class="board-bottom">

    <div class="pagination">
        <%-- 이전 --%>
        <% if (currentpage > 1) { %>
            <a class="page-nav"
               href="<%= request.getContextPath() %>/board/list.do?categoryId=<%= categoryId %>&page=<%= currentpage-1 %><%= q %>">
                이전
            </a>
        <% } %>

        <%-- 페이지 번호들 --%>
        <%
            for (int p = 1; p <= totalPages; p++) {
                if (p == currentpage) {
        %>
                    <span class="page-number current"><%= p %></span>
        <%
                } else {
        %>
                    <a class="page-number"
                       href="<%= request.getContextPath() %>/board/list.do?categoryId=<%= categoryId %>&page=<%= p %><%= q %>">
                        <%= p %>
                    </a>
        <%
                }
            }
        %>

        <%-- 다음 --%>
        <% if (currentpage < totalPages) { %>
            <a class="page-nav"
               href="<%= request.getContextPath() %>/board/list.do?categoryId=<%= categoryId %>&page=<%= currentpage+1 %><%= q %>">
                다음
            </a>
        <% } %>
    </div>

    <div class="board-actions">
        <a href="<%= request.getContextPath() %>/board/writeForm.do?categoryId=<%= categoryId %>"
           class="btn btn-primary btn-sm">
            글쓰기
        </a>
    </div>

</div>

</body>
</html>
