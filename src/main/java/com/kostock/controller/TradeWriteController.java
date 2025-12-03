package com.kostock.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

import com.kostock.model.dao.TradeRecordDAO;
import com.kostock.model.dto.TradeRecordDTO;
import com.kostock.model.dto.UserDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/trade/write.do")
public class TradeWriteController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserDTO loginUser = (UserDTO) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/member/login.jsp");
            return;
        }
        request.setCharacterEncoding("UTF-8");

        String userid = loginUser.getUserid();
        String stockName = request.getParameter("stockName");
        String stockCode = request.getParameter("stockCode"); // 선택
        String tradeType = request.getParameter("tradeType"); // BUY/SELL
        String memo = request.getParameter("memo");

        LocalDate tradeDateLocal = LocalDate.parse(request.getParameter("tradeDate"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        BigDecimal price = new BigDecimal(request.getParameter("price"));

        TradeRecordDTO dto = new TradeRecordDTO(
                userid,
                (stockCode == null || stockCode.isBlank()) ? null : stockCode,
                stockName,
                Date.valueOf(tradeDateLocal),
                tradeType,
                quantity,
                price,
                memo
        );

        TradeRecordDAO dao = new TradeRecordDAO();
        dao.insert(dto);

        // 다시 캘린더로 (해당 월 유지)
        response.sendRedirect(request.getContextPath()
                + "/trade/calendar.do?year=" + tradeDateLocal.getYear()
                + "&month=" + tradeDateLocal.getMonthValue()
                + "&date=" + tradeDateLocal);
    }
}
