package com.kostock.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import com.kostock.model.dao.UserDAO;
import com.kostock.model.dto.UserDTO;

@WebServlet("/member/join.do")
public class JoinController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 컨텍스트 패스 사용 (하드코딩 X)
        response.sendRedirect(request.getContextPath() + "/member/join.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String userid   = request.getParameter("userid");
        String pw       = request.getParameter("password");
        String pw2      = request.getParameter("password2");  // join.jsp 에서 추가한 필드
        String name     = request.getParameter("name");
        String role     = "USER";
        //  기본 입력값 검증 
        if (userid == null || userid.isBlank()
                || pw == null || pw.isBlank()
                || name == null || name.isBlank()) {

            request.setAttribute("error", "이름, 아이디, 비밀번호는 필수 입력 항목입니다.");
            request.getRequestDispatcher("/member/join.jsp")
                   .forward(request, response);
            return;
        }
        //  비밀번호 확인 체크
        if (pw2 == null || !pw.equals(pw2)) {
            request.setAttribute("error", "비밀번호가 서로 일치하지 않습니다.");
            request.getRequestDispatcher("/member/join.jsp")
                   .forward(request, response);
            return;
        }

        UserDTO dto = new UserDTO(userid, pw, name, role);
        UserDAO dao = new UserDAO();

        int result = dao.insertUser(dto);
        if (result > 0) {
            // 회원가입 성공 → 로그인 페이지로 이동
            response.sendRedirect(request.getContextPath() + "/member/login.jsp");
        } else {
            // 회원가입 실패 → 에러 메시지 보여주며 다시 회원가입 폼
            request.setAttribute("error", "회원가입에 실패했습니다. 잠시 후 다시 시도해주세요.");
            request.getRequestDispatcher("/member/join.jsp")
                   .forward(request, response);
        }
    }
}
