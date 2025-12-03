<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">


<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<div class="wrapper">

<!-- 지수 요약 바  -->


<c:if test="${not empty indexList}">
    <div class="index-bar">
        <c:forEach var="idx" items="${indexList}">
            <div class="index-item">
                
                <!-- 위쪽: 텍스트(지수 이름 + 수치) -->
                <div class="idx-header">
                    <span class="idx-name">${idx.stockName}</span>

                    <span class="idx-price">
                        <fmt:formatNumber value="${idx.closePrice}" pattern="#,##0.00"/>
                    </span>

                    <c:set var="isUp" value="${idx.diff > 0}" />
                    <span class="idx-change ${isUp ? 'up' : (idx.diff < 0 ? 'down' : 'flat')}">
                        <fmt:formatNumber value="${idx.diff}" pattern="#,##0.00"/>
                        (
                        <fmt:formatNumber value="${idx.diffRate}" pattern="#,##0.00"/>%
                        )
                    </span>
                </div>

                <!-- 아래쪽: 차트 -->
                <div class="idx-chart">
                    <canvas id="spark_${idx.stockCode}"></canvas>
                </div>
            </div>
        </c:forEach>
    </div>
</c:if>



<!-- ===== 지수 요약 바 끝 ===== -->
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<script>
document.addEventListener('DOMContentLoaded', function() {

    // indexList 돌면서 각 지수별로 차트 생성 
    <c:forEach var="idx" items="${indexList}">
    <c:set var="prices" value="${indexPriceMap[idx.stockCode]}" />

    // 차트 색 결정 (상승: 빨강, 하락: 파랑, 보합: 회색) 
    // 차트색
    <c:set var="chartBorderColor"
           value="${idx.diff > 0 ? 'rgba(255, 80, 80, 1)'
                    : (idx.diff < 0 ? 'rgba(80, 120, 255, 1)'
                                    : 'rgba(130, 130, 130, 1)')}" />
    // 차트 면적 색
    <c:set var="chartBgColor"
           value="${idx.diff > 0 ? 'rgba(255, 80, 80, 0.18)'
                    : (idx.diff < 0 ? 'rgba(80, 120, 255, 0.18)'
                                    : 'rgba(130, 130, 130, 0.15)')}" />

    (function() {
        var code = "${idx.stockCode}";
        var canvas = document.getElementById("spark_" + code);
        if (!canvas) return;
        var ctx = canvas.getContext("2d");

        var labels = [
            <c:forEach var="p" items="${prices}" varStatus="st">
                "<fmt:formatDate value='${p.priceDate}' pattern='MM-dd'/>"<c:if test="${!st.last}">,</c:if>
            </c:forEach>
        ];

        var data = [
            <c:forEach var="p" items="${prices}" varStatus="st">
                ${p.closePrice}<c:if test="${!st.last}">,</c:if>
            </c:forEach>
        ];
			
            // 차트 디테일
            new Chart(ctx, {
                type: 'line',
                
                data: {
                    labels: labels,   // 'MM-dd' 날짜 배열
                    datasets: [{
                        data: data,   // 종가 배열
                        fill: true,   // 면 채우기
                        tension: 0.3,
                        borderWidth: 1.5,
                        pointRadius: 0,
                        borderColor: "${chartBorderColor}",      // 원하는 색
                        backgroundColor: "${chartBgColor}"
                    }]
                },
                
                options: {
                    responsive: true,
                    maintainAspectRatio: false,   // ★ CSS 높이대로
                    plugins: {
                        legend: { display: false },
                        tooltip: { enabled: true } // 마우스 올리면 값 보기
                    },
                    
                    scales: {
                        x: {
                            display: true,
                            ticks: {
                            	// 날짜 표시 갯수
                                maxTicksLimit: 7,
                                autoSkip: true,
                                font: { size: 9 }
                            },
                            grid: { display: false }
                        },
                        y: {
                            position: 'right',      // ★ 가격 축을 오른쪽으로 이동
                            display: true,
                            ticks: {
                            	// 가격 표시 갯수
                                maxTicksLimit: 5,
                                font: { size: 9 },
                                callback: function(v) { return v.toLocaleString(); }
                            },
                            grid: {
                                display: true,
                                drawBorder: true
                            }
                        }

                    }
                }
            });

        })();
    </c:forEach>

});
</script>

</div>
</body>
</html>