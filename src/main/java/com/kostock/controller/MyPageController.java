package com.kostock.controller;

import java.io.IOException;
import java.math.BigDecimal;

import com.kostock.model.dao.CommentDAO;
import com.kostock.model.dao.PostDAO;
import com.kostock.model.dao.TradeRecordDAO;
import com.kostock.model.dto.UserDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/member/mypage.do")
public class MyPageController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 세션에서 로그인 사용자 정보 가져오기
        HttpSession session = request.getSession();
        UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
        
        // 로그인 여부 확인
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String userid = loginUser.getUserid();
        
        // DAO 객체 생성
        PostDAO postDAO = new PostDAO();
        CommentDAO commentDAO = new CommentDAO();
        TradeRecordDAO tradeDAO = new TradeRecordDAO();
        
        // 사용자 통계 조회
        int postCount = postDAO.getUserPostCount(userid);           // 작성한 게시글 수
        int totalViews = postDAO.getUserTotalViews(userid);        // 총 조회수
        int commentCount = commentDAO.getUserCommentCount(userid); // 작성한 댓글 수
        int tradeCount = tradeDAO.getUserTradeCount(userid);       // 거래 기록 수
        BigDecimal totalBuy = tradeDAO.getUserTotalBuyAmount(userid);   // 총 매수 금액
        BigDecimal totalSell = tradeDAO.getUserTotalSellAmount(userid); // 총 매도 금액
        
        // request에 데이터 저장
        request.setAttribute("postCount", postCount);
        request.setAttribute("totalViews", totalViews);
        request.setAttribute("commentCount", commentCount);
        request.setAttribute("tradeCount", tradeCount);
        request.setAttribute("totalBuy", totalBuy);
        request.setAttribute("totalSell", totalSell);
        
        // mypage.jsp로 포워딩
        request.getRequestDispatcher("/member/mypage.jsp").forward(request, response);
    }
}