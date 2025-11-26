package com.kostock.controller;

import java.io.IOException;

import com.kostock.model.dao.PostDAO;
import com.kostock.model.dto.PostDTO;
import com.kostock.model.dto.UserDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/board/edit.do")
public class BoardEditController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        UserDTO loginUser = (session == null) ? null : (UserDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/member/login.jsp");
            return;
        }

        int postId = Integer.parseInt(request.getParameter("postId"));
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));

        PostDAO dao = new PostDAO();
        if (!dao.isOwner(postId, loginUser.getUserid())) {
            response.sendRedirect(request.getContextPath() + "/board/detail.do?postId=" + postId);
            return;
        }

        String stockCode = request.getParameter("stockCode");
        if (stockCode == null || stockCode.trim().isEmpty()) stockCode = null;

        String title = request.getParameter("title");
        String content = request.getParameter("content");

        PostDTO dto = new PostDTO();
        dto.setPostId(postId);
        dto.setTitle(title);
        dto.setContent(content);
        dto.setStockCode(stockCode);

        dao.updatePost(dto);

        response.sendRedirect(request.getContextPath() + "/board/detail.do?postId=" + postId);
    }
}
