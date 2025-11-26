package com.kostock.controller;

import java.io.IOException;

import com.kostock.model.dao.CommentDAO;
import com.kostock.model.dto.CommentDTO;
import com.kostock.model.dto.UserDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/comment/write.do")
public class CommentWriteController extends HttpServlet {

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
        String content = request.getParameter("content");

        if (content == null || content.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/board/detail.do?postId=" + postId);
            return;
        }

        CommentDTO dto = new CommentDTO(postId, loginUser.getUserid(), content.trim());
        CommentDAO dao = new CommentDAO();
        dao.insertComment(dto);

        // 댓글 등록 후 상세로 복귀
        response.sendRedirect(request.getContextPath() + "/board/detail.do?postId=" + postId);
    }
}
