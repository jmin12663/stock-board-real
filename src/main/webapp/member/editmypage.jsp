<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.kostock.model.dto.UserDTO" %>
<%
    UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>개인정보 수정 - KoStock</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css">
    
    <script>
        function validateNameForm() {
            const name = document.getElementById('name').value.trim();
            
            if (!name) {
                alert('이름을 입력해주세요.');
                return false;
            }
            
            if (name.length > 50) {
                alert('이름은 50자 이내로 입력해주세요.');
                return false;
            }
            
            return true;
        }
        
        function validatePasswordForm() {
            const currentPassword = document.getElementById('currentPassword').value;
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            if (!currentPassword) {
                alert('현재 비밀번호를 입력해주세요.');
                return false;
            }
            
            if (!newPassword) {
                alert('새 비밀번호를 입력해주세요.');
                return false;
            }
            
            if (newPassword.length < 4) {
                alert('비밀번호는 4자 이상이어야 합니다.');
                return false;
            }
            
            if (newPassword !== confirmPassword) {
                alert('새 비밀번호가 일치하지 않습니다.');
                return false;
            }
            
            if (currentPassword === newPassword) {
                alert('새 비밀번호는 현재 비밀번호와 달라야 합니다.');
                return false;
            }
            
            return confirm('비밀번호를 변경하시겠습니까?');
        }
    </script>
</head>
<body>
        <div class="wrapper">

    <div class="edit-container">

        <div class="edit-header">
            <h1>개인정보 수정</h1>
            <p>회원님의 정보를 안전하게 관리하세요</p>
        </div>

        <!-- 기본 정보 -->
        <div class="edit-section">
            <h2>기본 정보</h2>

            <% if (request.getAttribute("nameSuccess") != null) { %>
                <div class="edit-success">
                    <%= request.getAttribute("nameSuccess") %>
                </div>
            <% } %>

            <% if (request.getAttribute("nameError") != null) { %>
                <div class="edit-error">
                    <%= request.getAttribute("nameError") %>
                </div>
            <% } %>

            <form action="..." method="post">
                <div class="edit-form-row">
                    <label class="edit-label">이름</label>
                    <input class="edit-input" type="text" name="name"
                           value="<%= loginUser.getName() %>" required>
                </div>

                <div class="edit-actions">
                    <button type="submit" class="btn btn-primary">
                        이름 변경
                    </button>
                </div>
            </form>

            <div class="edit-divider"></div>

            <div class="edit-form-row">
                <label class="edit-label">회원 등급</label>
                <input class="edit-input" type="text"
                       value="<%= loginUser.getRole() %>" disabled>
            </div>

            <div class="edit-form-row">
                <label class="edit-label">아이디</label>
                <input class="edit-input" type="text"
                       value="<%= loginUser.getUserid() %>" disabled>
            </div>

        </div>

        <!-- 비밀번호 변경 -->
        <div class="edit-section">
            <h2>비밀번호 변경</h2>

            <form action="..." method="post">
                <div class="edit-form-row">
                    <label class="edit-label">현재 비밀번호</label>
                    <input class="edit-input" type="password" name="currentPassword" required>
                </div>

                <div class="edit-form-row">
                    <label class="edit-label">새 비밀번호</label>
                    <input class="edit-input" type="password" name="newPassword" required>
                </div>

                <div class="edit-form-row">
                    <label class="edit-label">새 비밀번호 확인</label>
                    <input class="edit-input" type="password" name="confirmPassword" required>
                </div>

                <div class="edit-actions">
                    <button type="submit" class="btn btn-primary">
                        비밀번호 변경
                    </button>
                    <a href="<%= request.getContextPath() %>/member/mypage.do"
                       class="btn btn-secondary">취소</a>
                </div>
            </form>

        </div>

    </div>
</div>
</body>
</html>
