package com.kostock.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.kostock.model.dao.TradeRecordDAO;
import com.kostock.model.dto.TradeRecordDTO;
import com.kostock.model.dto.UserDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/trade/calendar.do")
public class TradeCalendarController extends HttpServlet {

    private final TradeRecordDAO tradeRecordDAO = new TradeRecordDAO();

    // GET = 달력 화면 조회
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 로그인 체크
        HttpSession session = request.getSession();
        UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/member/login.jsp");
            return;
        }

        //  year/month 파라미터(없으면 오늘 기준)
        LocalDate now = LocalDate.now();
        int year = parseIntOr(request.getParameter("year"), now.getYear());
        int month = parseIntOr(request.getParameter("month"), now.getMonthValue());
        if (month < 1) month = 1;
        if (month > 12) month = 12;
        LocalDate firstDay = LocalDate.of(year, month, 1);
        int daysInMonth = firstDay.lengthOfMonth(); // 첫 1일 저장
        // JSP에서 달력 만들 때 쓸 “시작 요일(일요일=1)”
        int firstDowSun1 = (firstDay.getDayOfWeek().getValue() % 7) + 1;

        // flash 메시지(POST 에러 메시지)
        String flashError = (String) session.getAttribute("flashError");
        if (flashError != null) {
            session.removeAttribute("flashError");
            request.setAttribute("error", flashError);
        }

        String userid = loginUser.getUserid();
        //  월 단위 매수/매도 금액 합계
        BigDecimal monthBuy  = nvl(tradeRecordDAO.sumAmountByMonth(userid, year, month, "BUY"));
        BigDecimal monthSell = nvl(tradeRecordDAO.sumAmountByMonth(userid, year, month, "SELL"));

        // 해당 월 거래 내역 전체 + 날짜별로 묶기
        List<TradeRecordDTO> monthTrades =
                tradeRecordDAO.selectByMonth(userid, year, month);
        //Map에 날짜,거래기록 리스트 저장
        Map<Integer, List<TradeRecordDTO>> dayTradesMap = new java.util.HashMap<>();
        for (TradeRecordDTO tr : monthTrades) {
            // java.util.Date → LocalDate 변환
            LocalDate tradeDate = // 날짜를 추출
                    new java.sql.Date(tr.getTradeDate().getTime()).toLocalDate();
            int day = tradeDate.getDayOfMonth(); //추출한 날짜의 일만 추출
            dayTradesMap //추출된 날짜르 빈 리스트에 반환하여 없는 날짜면 (tr)을 추가해 그룹화함
                    .computeIfAbsent(day, k -> new java.util.ArrayList<>())
                    .add(tr);
        }

        //  JSP로 전달
        request.setAttribute("year", year);
        request.setAttribute("month", month);
        request.setAttribute("daysInMonth", daysInMonth);
        request.setAttribute("firstDow", firstDowSun1);
        // 월 합계
        request.setAttribute("monthBuy", monthBuy);
        request.setAttribute("monthSell", monthSell);
        // 일별 합계 + 개별 내역
        request.setAttribute("monthTrades", monthTrades);
        request.setAttribute("dayTradesMap", dayTradesMap);

        request.getRequestDispatcher("/trade/calendar.jsp")
               .forward(request, response);
    }

    // POST = 기록 저장
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/member/login.jsp");
            return;
        }
        // 달력 날짜 유지용
        int year = parseIntOr(request.getParameter("year"), LocalDate.now().getYear());
        int month = parseIntOr(request.getParameter("month"), LocalDate.now().getMonthValue());

        String tradeDateStr = request.getParameter("tradeDate"); // yyyy-MM-dd
        String tradeType    = request.getParameter("tradeType"); // BUY/SELL
        String qtyStr       = request.getParameter("quantity");
        String priceStr     = request.getParameter("price");
        String stockCode = request.getParameter("stockCode"); // optional
        String stockName = request.getParameter("stockName"); // optional
        String memo      = request.getParameter("memo");      // optional

        // ===========================기본 검증 및 에러 처리하기
        String error = null;
        // ==========================날짜,수량,단가,거래유형 필드가 비어있는지 검증
        if (tradeDateStr == null || tradeDateStr.isBlank()) {
            error = "날짜를 선택해주세요.";
        }
        if (error == null && (qtyStr == null || qtyStr.isBlank())) {
            error = "수량(quantity)을 입력해주세요.";
        }
        if (error == null && (priceStr == null || priceStr.isBlank())) {
            error = "단가(price)를 입력해주세요.";
        }
        if (error == null) {
            if (!"BUY".equals(tradeType) && !"SELL".equals(tradeType)) {
                error = "거래유형은 BUY 또는 SELL만 가능합니다.";
            }
        }
        // 수량/단가 숫자 검증
        int quantity = 0;
        BigDecimal price = null;
        // 수량 에러 처리
        if (error == null) {
            try {
                quantity = Integer.parseInt(qtyStr.trim());
                if (quantity <= 0) error = "수량은 1 이상이어야 합니다.";} 
            catch (Exception e) {
                error = "수량은 숫자로 입력해주세요.";}
        }
        // 단가 에러 처리
        if (error == null) {
            try {
                price = new BigDecimal(priceStr.trim());
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    error = "단가는 0보다 커야 합니다.";}} 
            catch (Exception e) {
                error = "단가는 숫자로 입력해주세요.";}
        }
        // 에러면 flash 에 담아서 다시 달력으로
        if (error != null) {
            session.setAttribute("flashError", error);
            response.sendRedirect(request.getContextPath() + "/trade/calendar.do?year=" + year + "&month=" + month);
            return;
        }
        
        //  에러에 값이 없으면 정상 저장 처리 후 dto로 저장
        TradeRecordDTO dto = new TradeRecordDTO();
        dto.setUserid(loginUser.getUserid());
        dto.setStockCode(isBlankToNull(stockCode));
        dto.setStockName(isBlankToNull(stockName));
        dto.setTradeType(tradeType);
        dto.setQuantity(quantity);
        dto.setPrice(price);
        dto.setMemo(isBlankToNull(memo));
        // 날짜: java.sql.Date로 맞춰서 넣기
        dto.setTradeDate(java.sql.Date.valueOf(tradeDateStr)); // yyyy-MM-dd OK

        tradeRecordDAO.insert(dto);

        response.sendRedirect(
                request.getContextPath()
                        + "/trade/calendar.do?year=" + year + "&month=" + month);
    }

    // 공통 유틸 ===============================

    private int parseIntOr(String s, int def) {
        try { return Integer.parseInt(s); }
        catch (Exception e) { return def; }
    }

    private BigDecimal nvl(BigDecimal value) {
        return (value == null) ? BigDecimal.ZERO : value;
    }

    private String isBlankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
