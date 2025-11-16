package com.kostock.controller;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import com.kostock.model.dao.UserDAO;
import com.kostock.model.dto.UserDTO;

@WebServlet("/member/login.do")
public class LoginController extends HttpServlet{
	
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String userid = request.getParameter("userid");
        String pw = request.getParameter("password");

        UserDAO dao = new UserDAO();
        UserDTO loginUser = dao.login(userid, pw);

        if (loginUser != null) {
            // 로그인 성공 → 세션에 저장
            HttpSession session = request.getSession();
            session.setAttribute("loginUser", loginUser);

            // 메인 페이지로 이동
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        } else {
            // 로그인 실패 → 다시 로그인 페이지
            response.sendRedirect(request.getContextPath() + "/member/login.jsp");
        }
    }
}