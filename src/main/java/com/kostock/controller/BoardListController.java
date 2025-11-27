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
    protected void doGet (HttpServletRequest request, HttpServletResponse response)
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
        
        //searchWord (검색어)
        String searchWord = request.getParameter("searchWord");
        if (searchWord != null) {
            searchWord = searchWord.trim();
            if (searchWord.isEmpty()) searchWord = null;
        }
        //검색 옵션
        String field = request.getParameter("field");
        if (!"title".equals(field) && !"content".equals(field)) {
            field = "title"; // 기본 + 화이트리스트
        }
        
        // paging settings
        int pageSize = 10;

        PostDAO dao = new PostDAO();

        // 총 글 개수 / 총 페이지 수
        int totalCount;
        int totalPages;
        List<PostDTO> list;

        //  검색어 있으면 검색용 메서드, 없으면 기존 메서드
        if (searchWord != null) {
            totalCount = dao.getPostCountByCategorySearch(categoryId, field, searchWord);
            totalPages = (int) Math.ceil((double) totalCount / pageSize);
            if (totalPages < 1) totalPages = 1;
            if (page > totalPages) page = totalPages;

            list = dao.selectPostListByCategorySearchPaging(categoryId, field, searchWord, page, pageSize);

            request.setAttribute("searchWord", searchWord); // list.jsp에서 유지용
            request.setAttribute("field", field);
        } else {
            totalCount = dao.getPostCountByCategory(categoryId);
            totalPages = (int) Math.ceil((double) totalCount / pageSize);
            if (totalPages < 1) totalPages = 1;
            if (page > totalPages) page = totalPages;

            list = dao.selectPostListByCategoryPaging(categoryId, page, pageSize);
        }


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
