<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.kostock.model.dto.PostDTO" %>
<%
    List<PostDTO> postList = (List<PostDTO>) request.getAttribute("postList");
    int categoryId = (request.getAttribute("categoryId") == null) ? 1
                    : (Integer) request.getAttribute("categoryId");
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

<br>

<a href="<%= request.getContextPath() %>/board/writeForm.do?categoryId=<%= categoryId %>">
    글쓰기
</a>

</body>
</html>
