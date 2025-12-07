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
public class LoginController extends HttpServlet {
	
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String userid = request.getParameter("userid");
        String pw = request.getParameter("password");

        // 입력값과 일치하는 유저가 있으면 DTO 리턴
        UserDAO dao = new UserDAO();
        UserDTO loginUser = dao.login(userid, pw);

        if (loginUser != null) {
            // 로그인 성공 → 세션에 저장
            HttpSession session = request.getSession();
            session.setAttribute("loginUser", loginUser);

            // 메인 페이지로 이동 (원하는 곳으로)
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        } else {
            // 로그인 실패 → 에러 메시지 담고 login.jsp로 forward
            request.setAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");

            request.getRequestDispatcher("/member/login.jsp")
                   .forward(request, response);
        }
    }
}
