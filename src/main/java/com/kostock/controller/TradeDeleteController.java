package com.kostock.controller;

import java.io.IOException;

import com.kostock.model.dao.TradeRecordDAO;
import com.kostock.model.dto.UserDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/trade/delete.do")
public class TradeDeleteController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/member/login.jsp");
            return;
        }

        int recordId = Integer.parseInt(request.getParameter("recordId"));
        int year = Integer.parseInt(request.getParameter("year"));
        int month = Integer.parseInt(request.getParameter("month"));

        TradeRecordDAO dao = new TradeRecordDAO();
        dao.delete(recordId, loginUser.getUserid());

        response.sendRedirect(request.getContextPath() + "/trade/calendar.do?year=" + year + "&month=" + month);
    }
}