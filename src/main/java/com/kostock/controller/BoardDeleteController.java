package com.kostock.controller;

import java.io.IOException;

import com.kostock.model.dao.CommentDAO;
import com.kostock.model.dao.PostDAO;
import com.kostock.model.dto.UserDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/board/delete.do")
public class BoardDeleteController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        UserDTO loginUser = (session == null) ? null : (UserDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/member/login.jsp");
            return;
        }

        int postId = Integer.parseInt(request.getParameter("postId"));
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));

        PostDAO pdao = new PostDAO();
        if (!pdao.isOwner(postId, loginUser.getUserid())) {
            response.sendRedirect(request.getContextPath() + "/board/detail.do?postId=" + postId);
            return;
        }

        // 댓글 먼저 삭제 → 글 삭제
        CommentDAO cdao = new CommentDAO();
        cdao.deleteCommentsByPostId(postId);

        pdao.deletePost(postId);

        response.sendRedirect(request.getContextPath() + "/board/list.do?categoryId=" + categoryId);
    }
}
