package com.kostock.controller;

import java.io.IOException;
import java.util.List;

import com.kostock.model.dao.PostDAO;
import com.kostock.model.dto.PostDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/board/list.do")
public class BoardListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // categoryId
        int categoryId = 1;
        String categoryIdParam = request.getParameter("categoryId");
        if (categoryIdParam != null) {
            categoryId = Integer.parseInt(categoryIdParam);
        }

        // page
        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            page = Integer.parseInt(pageParam);
        }
        if (page < 1) page = 1;

        // paging settings
        int pageSize = 10;

        PostDAO dao = new PostDAO();

        // 총 글 개수 / 총 페이지 수
        int totalCount = dao.getPostCountByCategory(categoryId);
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        if (totalPages < 1) totalPages = 1;

        if (page > totalPages) page = totalPages;

        // 4) 페이지에 해당하는 글 목록
        List<PostDTO> list = dao.selectPostListByCategoryPaging(categoryId, page, pageSize);

        // 5) JSP로 전달
        request.setAttribute("postList", list);
        request.setAttribute("categoryId", categoryId);

        request.setAttribute("page", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("/board/list.jsp").forward(request, response);
    }
}
