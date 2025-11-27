package com.kostock.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import com.kostock.model.dao.UserDAO;
import com.kostock.model.dto.UserDTO;


@WebServlet("/member/join.do")
public class JoinController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.sendRedirect("/KoStock/member/join.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String userid = request.getParameter("userid");
        String pw = request.getParameter("password");
        String name = request.getParameter("name");

        String role = "USER";
        
        UserDTO dto = new UserDTO(userid, pw, name, role);
        UserDAO dao = new UserDAO();
               
        int result = dao.insertUser(dto);

        if (result > 0) {
            response.sendRedirect("/KoStock/member/login.jsp");
        } else {
            response.sendRedirect("/KoStock/member/join.jsp");
        }
    }
}
