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

        // categoryId=1 
        String categoryIdParam = request.getParameter("categoryId");
        int categoryId = 1; // 기본값: 자유게시판

        if (categoryIdParam != null) {
            categoryId = Integer.parseInt(categoryIdParam);
        }

        PostDAO dao = new PostDAO();
        List<PostDTO> list = dao.selectPostListByCategory(categoryId);

        // JSP에서 사용할 데이터 세팅
        request.setAttribute("postList", list);
        request.setAttribute("categoryId", categoryId);

        // 화면으로 포워드
        request.getRequestDispatcher("/board/list.jsp")
               .forward(request, response);
    }
}
