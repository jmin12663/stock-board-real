package com.kostock.controller;

import java.io.IOException;

import com.kostock.model.dao.PostDAO;
import com.kostock.model.dto.PostDTO;
import com.kostock.model.dto.UserDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/board/write.do")
public class BoardWriteController extends HttpServlet {

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

        String userid = loginUser.getUserid();
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));
        String stockCode = request.getParameter("stockCode");
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        if (stockCode == null || stockCode.trim().isEmpty()) {
            stockCode = null; // 옵션값
        }

        PostDTO dto = new PostDTO(userid, categoryId, stockCode, title, content);
        PostDAO dao = new PostDAO();

        //postDao 메서드
        int result = dao.insertPost(dto);

        if (result > 0) {
            // 등록 성공 → 해당 카테고리 목록으로
            response.sendRedirect(request.getContextPath() + "/board/list.do?categoryId=" + categoryId);
        } else {
            // 실패 → 다시 폼으로
            response.sendRedirect(request.getContextPath() + "/board/writeForm.do?categoryId=" + categoryId);
        }
    }
}
