package com.kostock.controller;

import java.io.IOException;

import com.kostock.model.dao.PostDAO;
import com.kostock.model.dto.PostDTO;
import com.kostock.model.dto.UserDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/board/editForm.do")
public class BoardEditFormController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        UserDTO loginUser = (session == null) ? null : (UserDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/member/login.jsp");
            return;
        }

        int postId = Integer.parseInt(request.getParameter("postId"));

        PostDAO dao = new PostDAO();
        PostDTO post = dao.selectPostById(postId);

        if (post == null) {
            response.sendRedirect(request.getContextPath() + "/board/list.do?categoryId=1");
            return;
        }

        // 작성자 본인만 수정 가능하며 일치하지 않을때 상세보기로 돌아감 (수정 안보여줌)
        if (!post.getUserid().equals(loginUser.getUserid())) {
            response.sendRedirect(request.getContextPath() + "/board/detail.do?postId=" + postId);
            return;
        }
        // 위 if문이 거짓일때 edit 으로 이동
        request.setAttribute("post", post);
        request.getRequestDispatcher("/board/edit.jsp").forward(request, response);
    }
}
