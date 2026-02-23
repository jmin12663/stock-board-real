package com.kostock.controller;

import java.io.IOException;

import com.kostock.model.dao.UserDAO;
import com.kostock.model.dto.UserDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/member/editmypage.do")
public class MypageEditController extends HttpServlet {
    
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
        
        // 정보 수정 페이지로 이동
        request.getRequestDispatcher("/member/editmypage.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession();
        UserDTO loginUser = (UserDTO) session.getAttribute("loginUser");
        
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // 파라미터 받기
        String action = request.getParameter("action");
        
        if ("updateName".equals(action)) {
            // 이름 변경 처리
            updateName(request, response, session, loginUser);
        } else if ("updatePassword".equals(action)) {
            // 비밀번호 변경 처리
            updatePassword(request, response, session, loginUser);
        }
    }
    
    // 이름 변경 처리
    private void updateName(HttpServletRequest request, HttpServletResponse response,
                           HttpSession session, UserDTO loginUser) 
            throws ServletException, IOException {
        
        String newName = request.getParameter("name");
        
        // 유효성 검사
        if (newName == null || newName.trim().isEmpty()) {
            request.setAttribute("nameError", "이름을 입력해주세요.");
            request.getRequestDispatcher("/member/editmypage.jsp").forward(request, response);
            return;
        }
        
        if (newName.length() > 50) {
            request.setAttribute("nameError", "이름은 50자 이내로 입력해주세요.");
            request.getRequestDispatcher("/member/editmypage.jsp").forward(request, response);
            return;
        }
        
        // DB 업데이트
        UserDAO dao = new UserDAO();
        int result = dao.updateUserName(loginUser.getUserid(), newName);
        
        if (result > 0) {
            // 성공: 세션 정보도 업데이트
            loginUser.setName(newName);
            session.setAttribute("loginUser", loginUser);
            
            request.setAttribute("nameSuccess", "이름이 성공적으로 변경되었습니다.");
        } else {
            request.setAttribute("nameError", "이름 변경에 실패했습니다.");
        }
        
        request.getRequestDispatcher("/member/editmypage.jsp").forward(request, response);
    }
    
    // 비밀번호 변경 처리
    private void updatePassword(HttpServletRequest request, HttpServletResponse response,
                               HttpSession session, UserDTO loginUser) 
            throws ServletException, IOException {
        
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // 유효성 검사
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            request.setAttribute("passwordError", "현재 비밀번호를 입력해주세요.");
            request.getRequestDispatcher("/member/editmypage.jsp").forward(request, response);
            return;
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            request.setAttribute("passwordError", "새 비밀번호를 입력해주세요.");
            request.getRequestDispatcher("/member/editmypage.jsp").forward(request, response);
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("passwordError", "새 비밀번호가 일치하지 않습니다.");
            request.getRequestDispatcher("/member/editmypage.jsp").forward(request, response);
            return;
        }
        
        if (currentPassword.equals(newPassword)) {
            request.setAttribute("passwordError", "새 비밀번호는 현재 비밀번호와 달라야 합니다.");
            request.getRequestDispatcher("/member/editmypage.jsp").forward(request, response);
            return;
        }
        
        // DB 업데이트
        UserDAO dao = new UserDAO();
        int result = dao.updatePassword(loginUser.getUserid(), currentPassword, newPassword);
        
        if (result > 0) {
            // 성공: 세션 정보도 업데이트
            loginUser.setPassword(newPassword);
            session.setAttribute("loginUser", loginUser);
            
            request.setAttribute("passwordSuccess", "비밀번호가 성공적으로 변경되었습니다.");
        } else {
            request.setAttribute("passwordError", "현재 비밀번호가 일치하지 않습니다.");
        }
        
        request.getRequestDispatcher("/member/editmypage.jsp").forward(request, response);
    }
}