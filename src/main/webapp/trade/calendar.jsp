<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">


<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="com.kostock.model.dto.TradeRecordDTO" %>
<%@ page import="com.kostock.model.dto.UserDTO" %>

<%
	UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
	
    // ───── Controller 에서 넘어온 값들 ─────
    int year         = (Integer) request.getAttribute("year");
    int month        = (Integer) request.getAttribute("month");
    int daysInMonth  = (Integer) request.getAttribute("daysInMonth");
    int firstDow     = (Integer) request.getAttribute("firstDow"); // 일=1

    BigDecimal monthBuy  = (BigDecimal) request.getAttribute("monthBuy");
    BigDecimal monthSell = (BigDecimal) request.getAttribute("monthSell");

    if (monthBuy  == null) monthBuy  = BigDecimal.ZERO;
    if (monthSell == null) monthSell = BigDecimal.ZERO;

    Boolean hasSellObj = (Boolean) request.getAttribute("hasSell");
    boolean hasSell = (hasSellObj != null && hasSellObj.booleanValue());

    @SuppressWarnings("unchecked")
    Map<Integer, List<TradeRecordDTO>> dayTradesMap =
        (Map<Integer, List<TradeRecordDTO>>) request.getAttribute("dayTradesMap");

    String error = (String) request.getAttribute("error");

    // 이전/다음 달 계산
    int prevY = year, prevM = month - 1;
    int nextY = year, nextM = month + 1;
    if (prevM == 0) { prevM = 12; prevY--; }
    if (nextM == 13) { nextM = 1; nextY++; }

    String yyyyMM = String.format("%04d-%02d", year, month);
    
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>매매기록 캘린더</title>
</head>
<body>
<div class="wrapper">

<div class="header">
    <div class="logo">
        <a href="<%= request.getContextPath() %>/index.jsp">KoStock</a>
    </div>
    
    <div class="nav-right">
	<% if (loginUser == null) { %>
   	 <!-- 로그인 안된 상태 -->
   	 <a href="<%= request.getContextPath() %>/member/login.jsp">로그인</a>
    	<a href="<%= request.getContextPath() %>/member/join.jsp">회원가입</a>
	<% } else { %>
   	 <!-- 로그인 된 상태 -->
    	<span><%= loginUser.getName() %>님 (역할: <%= loginUser.getRole() %>)</span>
    	<a href="<%= request.getContextPath() %>/member/logout.do">로그아웃</a>
	<% } %>

	<a href="<%= request.getContextPath() %>/board/list.do?categoryId=1">홈페이지</a>
	</div>
</div>

<jsp:include page="/common/nav.jsp" />

<h2 class="board-title">매매 기록</h2>

<div class="calendar-container">

    <!-- ───── 상단: 월 이동 + 월 합계 ───── -->
    <div class="calendar-header">
        <form method="get"
              action="<%=request.getContextPath()%>/trade/calendar.do"
              class="calendar-nav">
            <input type="hidden" name="year" value="<%=year%>">
            <input type="hidden" name="month" value="<%=month%>">

            <a class="btn btn-secondary btn-sm"
               href="<%=request.getContextPath()%>/trade/calendar.do?year=<%=prevY%>&month=<%=prevM%>">
                ◀
            </a>

            <span class="calendar-title">
                <%=year%>년 <%=month%>월
            </span>

            <a class="btn btn-secondary btn-sm"
               href="<%=request.getContextPath()%>/trade/calendar.do?year=<%=nextY%>&month=<%=nextM%>">
                ▶
            </a>
        </form>

        <div class="calendar-month-summary">
            월 합계:
            <span class="blue">매수 <%=monthBuy%></span> /
            <span class="red">매도 <%=monthSell%></span>
        </div>
    </div>

    <% if (error != null) { %>
      <p class="calendar-error"><%= error %></p>
    <% } %>

    <!-- ───── 입력 폼 카드 ───── -->
    <div class="calendar-form-card">
      <form method="post" action="<%=request.getContextPath()%>/trade/calendar.do" class="calendar-form">
        <input type="hidden" name="year" value="<%=year%>">
        <input type="hidden" name="month" value="<%=month%>">

        <div class="calendar-form-row">
            <label>날짜</label>
            <input type="date" id="tradeDate" name="tradeDate" value="<%=yyyyMM%>-01">
        </div>

        <div class="calendar-form-row">
            <label>종목코드</label>
            <input type="text" name="stockCode" class="input-small">
            <label>종목명</label>
            <input type="text" name="stockName" class="input-medium">
        </div>

        <div class="calendar-form-row">
            <label>유형</label>
            <select name="tradeType" class="input-small">
              <option value="BUY">BUY</option>
              <option value="SELL">SELL</option>
            </select>

            <label>수량</label>
            <input type="text" name="quantity" class="input-small">

            <label>단가</label>
            <input type="text" name="price" class="input-small">
        </div>

        <div class="calendar-form-row">
            <label>메모</label>
            <input type="text" name="memo" class="input-long">
        </div>

        <div class="calendar-form-actions">
            <button type="submit" class="btn btn-primary btn-sm">저장</button>
        </div>
      </form>

      <div class="small" style="margin-top:4px;">
        * 달력에서 날짜를 클릭하면 위 “날짜” 입력칸이 자동으로 바뀜
      </div>
    </div>

    <!-- ───── 달력 표 ───── -->
    <table class="calendar-table">
      <thead>
        <tr>
          <th class="sun">일</th>
          <th>월</th>
          <th>화</th>
          <th>수</th>
          <th>목</th>
          <th>금</th>
          <th class="sat">토</th>
        </tr>
      </thead>
      <tbody>
<%
    int day = 1;
    int cell = 1;

    for (int row = 0; row < 6; row++) {
%>
        <tr>
<%
      for (int col = 0; col < 7; col++, cell++) {
          if (cell < firstDow || day > daysInMonth) {
%>
          <td class="calendar-cell empty"></td>
<%
          } else {
              String dateStr = String.format("%04d-%02d-%02d", year, month, day);
              List<TradeRecordDTO> dayTrades =
                  (dayTradesMap != null) ? dayTradesMap.get(day) : null;

              boolean hasTrade = (dayTrades != null && !dayTrades.isEmpty());
%>
          <td class="calendar-cell day <%= hasTrade ? "has-trade" : "" %>"
              onclick="pickDate('<%=dateStr%>')">
            <div class="day-number"><%=day%></div>

<%
            if (hasTrade) {
                for (TradeRecordDTO tr : dayTrades) {
                    String ttype = tr.getTradeType();
                    String label = "BUY".equals(ttype) ? "[매수]" : "[매도]";
                    String colorClass = "BUY".equals(ttype) ? "blue" : "red";
%>
            <div class="tradeItem <%=colorClass%>">
              <%= label %>
              <%= (tr.getStockName() == null ? "" : tr.getStockName()) %>
              (<%= tr.getQuantity() %>주 × <%= tr.getPrice() %>)

              <form method="post"
                    action="<%=request.getContextPath()%>/trade/delete.do"
                    style="display:inline;">
                <input type="hidden" name="recordId" value="<%= tr.getRecordId() %>">
                <input type="hidden" name="year" value="<%= year %>">
                <input type="hidden" name="month" value="<%= month %>">
                <button type="submit" class="btnDel"
                        onclick="return confirm('삭제할까요?');">삭제</button>
              </form>
            </div>
<%
                }
            } else {
%>
            <div class="small">거래 없음</div>
<%
            }
%>
          </td>
<%
              day++;
          }
      } // for col
%>
        </tr>
<%
      if (day > daysInMonth) break;
    } // for row
%>
      </tbody>
    </table>

    <div class="calendar-legend">
        <span class="legend-box legend-has-trade"></span> 거래 있음
        <span class="legend-box legend-empty"></span> 거래 없음
    </div>

</div> <!-- /.calendar-container -->

<script>
function pickDate(d) {
  document.getElementById("tradeDate").value = d;
}
</script>

</div>
</body>
</html>
