<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.kostock.model.dto.PostDTO" %>
<%
    List<PostDTO> postList = (List<PostDTO>) request.getAttribute("postList");
    int categoryId = (request.getAttribute("categoryId") == null) ? 1
                    : (Integer) request.getAttribute("categoryId");
    int currentpage = (request.getAttribute("page") == null) ? 1 : (Integer) request.getAttribute("page");
    int totalPages = (request.getAttribute("totalPages") == null) ? 1 : (Integer) request.getAttribute("totalPages");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 목록</title>
</head>
<body>

<h2>게시판 목록 (카테고리 ID: <%= categoryId %>)</h2>

<table border="1" cellspacing="0" cellpadding="5">
    <tr>
        <th>No</th>
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


<div>
    <%-- 이전 --%>
    <% if (currentpage > 1) { %>
        <a href="<%= request.getContextPath() %>/board/list.do?categoryId=<%= categoryId %>&page=<%= currentpage-1 %>">이전</a>
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
                <a href="<%= request.getContextPath() %>/board/list.do?categoryId=<%= categoryId %>&page=<%= p %>">[<%= p %>]</a>
    <%
            }
        }
    %>

    <%-- 다음 --%>
    <% if (currentpage < totalPages) { %>
        <a href="<%= request.getContextPath() %>/board/list.do?categoryId=<%= categoryId %>&page=<%= currentpage+1 %>">다음</a>
    <% } %>
</div>
<br>

<a href="<%= request.getContextPath() %>/board/writeForm.do?categoryId=<%= categoryId %>">
    글쓰기
</a>

</body>
</html>
