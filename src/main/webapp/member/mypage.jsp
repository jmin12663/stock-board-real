<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">

<%@ page import="com.kostock.model.dto.UserDTO" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<%
    UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    
    // 데이터 가져오기
    int postCount = (Integer) request.getAttribute("postCount");
    int totalViews = (Integer) request.getAttribute("totalViews");
    int commentCount = (Integer) request.getAttribute("commentCount");
    int tradeCount = (Integer) request.getAttribute("tradeCount");
    BigDecimal totalBuy = (BigDecimal) request.getAttribute("totalBuy");
    BigDecimal totalSell = (BigDecimal) request.getAttribute("totalSell");
    
    // 숫자 포맷팅
    NumberFormat nf = NumberFormat.getInstance(Locale.KOREA);
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>마이페이지 - KoStock</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">
</head>
<body>
    <div class="wrapper">
	    <div class="header">
		    <div class="logo">
		        <a href="<%= request.getContextPath() %>/index.jsp">KoStock</a>
		    </div>
		    
		    <div class="nav-right">
			   	 <!-- 로그인 된 상태 -->
			    	<span><%= loginUser.getName() %>님 (<%= loginUser.getRole() %>)</span>
			    	<a href="<%= request.getContextPath() %>/member/logout.do">로그아웃</a>
					<a href="<%= request.getContextPath() %>/member/mypage.do">마이페이지</a>			    	
				<!-- <a href="<%= request.getContextPath() %>/board/list.do?categoryId=1">홈페이지</a> -->
			</div>
		</div>
		
    <div class="mypage-container">
        <div class="mypage-header">
            <h1>
                <%= loginUser.getName() %>님의 정보 
            </h1>
            <p>이름: <%= loginUser.getName() %></p>
            <p>가입일: <%= loginUser.getJoinDate() %></p>
            <p>아이디: <%= loginUser.getUserid() %></p>
            <p>비밀번호: <%= loginUser.getPassword() %> </p>
            <div class="edit-actions">
			    <a href="<%= request.getContextPath() %>/member/editmypage.do" 
			       class="btn-primary">
			        수정하기
			    </a>
			</div>
        </div>
	</div>
    <!--  
    <div class="mypage-status">
        <div class="stat-card">
            <div class="stat-info">
                <div class="stat-label">작성한 게시글: <%= postCount %> <span>개</span> </div>                
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-info">
                <div class="stat-label">총 조회수: <%= nf.format(totalViews) %> <span></span></div>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-info">
                <div class="stat-label">작성한 댓글: <%= commentCount %> <span>개</span></div>
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-info">
                <div class="stat-label">거래 기록: <%= tradeCount %> <span>건</span></div>
            </div>
        </div>
    </div>
    -->
    
    <!-- ===================== 상세 섹션 ===================== -->
    <div class="sections">
        <div class="section">
            <h3>활동 통계</h3>

            <div class="info-row">
                <span class="info-label">작성한 게시글 : </span>
                <span class="info-value"><%= postCount %>개</span>
            </div>

            <div class="info-row">
                <span class="info-label">받은 총 조회수 : </span>
                <span class="info-value"><%= nf.format(totalViews) %>회</span>
            </div>

            <div class="info-row">
                <span class="info-label">작성한 댓글 : </span>
                <span class="info-value"><%= commentCount %>개</span>
            </div>

            <div class="info-row">
                <span class="info-label">평균 조회수 : </span>
                <span class="info-value">
                    <%= postCount > 0 ? nf.format(totalViews / postCount) : "0" %>회
                </span>
            </div>
        </div>
        
        <div class="section">
            <h3>거래 통계</h3>

            <div class="info-row">
                <span class="info-label">총 거래 기록 : </span>
                <span class="info-value"><%= tradeCount %>건</span>
            </div>

            <div class="info-row">
                <span class="info-label">총 매수 금액 : </span>
                <span class="info-value text-danger">
                    <%= nf.format(totalBuy) %>원
                </span>
            </div>

            <div class="info-row">
                <span class="info-label">총 매도 금액 : </span>
                <span class="info-value text-primary">
                    <%= nf.format(totalSell) %>원
                </span>
            </div>

            <div class="info-row">
                <span class="info-label">손익 (개략) : </span>
                <span class="info-value 
                    <%= totalSell.compareTo(totalBuy) >= 0 ? "text-primary" : "text-danger" %>">
                    <%= nf.format(totalSell.subtract(totalBuy)) %>원
                </span>
            </div>
        </div>

        <div class="danger-zone">
            <h3> !주의 </h3>
            <p class="warning-text">
                회원 탈퇴 시 모든 게시글, 댓글, 거래 기록이 삭제되며 복구할 수 없습니다.
            </p>
            <form action="${pageContext.request.contextPath}/member/deleteUser.do" method="post" 
            	onsubmit="return confirm('정말 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다!');">
			    <button type="submit" class="btn-danger">
			        회원 탈퇴
			    </button>
			</form>
        </div>
        
      </div>
</div>
</body>
</html>
