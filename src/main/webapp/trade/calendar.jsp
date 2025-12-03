<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/resources/css/main.css">

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

<% if (loginUser == null) { %>
    <!-- 로그인 안된 상태 -->
    <a href="<%= request.getContextPath() %>/member/login.jsp">로그인</a> |
    <a href="<%= request.getContextPath() %>/member/join.jsp">회원가입</a>
<% } else { %>
    <!-- 로그인 된 상태 -->
    <span><%= loginUser.getName() %>님 (역할: <%= loginUser.getRole() %>)</span>
    <a href="<%= request.getContextPath() %>/member/logout.do">로그아웃</a>
<% } %>

<a href="<%= request.getContextPath() %>/index.jsp">홈페이지</a>
<hr>

<jsp:include page="/common/nav.jsp" />

<title>매매기록 캘린더</title>
<style>
  table { border-collapse: collapse; width: 900px; }
  th, td { border:1px solid #ddd; padding:8px; vertical-align: top; height:110px; }
  th { background:#f5f5f5; }
  .day { cursor:pointer; }
  .small { font-size:12px; color:#555; }
  .red { color:#d00; }
  .blue { color:#06c; }
  .box { width:900px; border:1px solid #ddd; padding:10px; margin-bottom:10px; }
  .tradeItem { font-size:12px; margin-top:4px; }
  .btnDel { font-size:11px; padding:2px 6px; margin-left:6px; }
</style>
</head>
<body>



<h2>매매기록지 (달력)</h2>

<!-- ───── 상단: 월 합계/실현손익 ───── -->
<div class="box">
  <a href="<%=request.getContextPath()%>/trade/calendar.do?year=<%=prevY%>&month=<%=prevM%>">◀ 이전달</a>
  <b style="margin:0 10px;"><%=year%>년 <%=month%>월</b>
  <a href="<%=request.getContextPath()%>/trade/calendar.do?year=<%=nextY%>&month=<%=nextM%>">다음달 ▶</a>

  <div style="margin-top:8px;">
    월 합계:
    <span class="blue">매수 <%=monthBuy%></span> /
    <span class="red">매도 <%=monthSell%></span>
    <br/>
  </div>
</div>

<% if (error != null) { %>
  <p style="color:red;"><%= error %></p>
<% } %>

<!-- ───── 입력 폼 ───── -->
<div class="box">
  <form method="post" action="<%=request.getContextPath()%>/trade/calendar.do">
    <input type="hidden" name="year" value="<%=year%>">
    <input type="hidden" name="month" value="<%=month%>">

    날짜:
    <input type="date" id="tradeDate" name="tradeDate" value="<%=yyyyMM%>-01">

    종목코드:
    <input type="text" name="stockCode" style="width:90px;">
    종목명:
    <input type="text" name="stockName" style="width:120px;">

    유형:
    <select name="tradeType">
      <option value="BUY">BUY</option>
      <option value="SELL">SELL</option>
    </select>

    수량:
    <input type="text" name="quantity" style="width:60px;">
    단가:
    <input type="text" name="price" style="width:90px;">

    메모:
    <input type="text" name="memo" style="width:200px;">

    <button type="submit">저장</button>
  </form>

  <div class="small" style="margin-top:6px;">
    * 달력에서 날짜를 클릭하면 위 “날짜” 입력칸이 자동으로 바뀜
  </div>
</div>

<!-- ───── 달력 표 ───── -->
<table>
  <tr>
    <th>일</th><th>월</th><th>화</th><th>수</th><th>목</th><th>금</th><th>토</th>
  </tr>

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
      <td></td>
  <%
          } else {
              String dateStr = String.format("%04d-%02d-%02d", year, month, day);
              List<TradeRecordDTO> dayTrades =
                  (dayTradesMap != null) ? dayTradesMap.get(day) : null;
  %>
      <td class="day" onclick="pickDate('<%=dateStr%>')">
        <b><%=day%></b><br/>

        <%-- ✅ 개별 거래 내역만 한글 [매수]/[매도]로 통합 표시 --%>
        <%
            if (dayTrades != null) {
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
      }
  %>
  </tr>
<%
      if (day > daysInMonth) break;
    }
%>
</table>

<script>
function pickDate(d) {
  document.getElementById("tradeDate").value = d;
}
</script>

</body>
</html>
