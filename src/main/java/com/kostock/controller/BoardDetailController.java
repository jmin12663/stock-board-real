package com.kostock.controller;

import java.io.IOException;

import com.kostock.model.dao.PostDAO;
import com.kostock.model.dto.PostDTO;
import com.kostock.model.dao.CommentDAO;
import com.kostock.model.dto.CommentDTO;

import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/board/detail.do")
public class BoardDetailController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 파라미터 꺼내기
        String postIdParam = request.getParameter("postId");

        if (postIdParam == null) {
            // postId 없으면 그냥 목록으로 보냄
            response.sendRedirect(request.getContextPath() + "/board/list.do?categoryId=1");
            return;
        }

        int postId = Integer.parseInt(postIdParam);

        // DAO 호출해서 글 가져오기 + 조회수 증가
        PostDAO dao = new PostDAO();

        // 조회수 +1
        dao.increaseViewCount(postId);

        // 게시글 내용
        PostDTO post = dao.selectPostById(postId);

        if (post == null) {
            // 존재하지 않는 글이면 목록으로
            response.sendRedirect(request.getContextPath() + "/board/list.do?categoryId=1");
            return;
        }
        
        CommentDAO cdao = new CommentDAO();
        List<CommentDTO> comments = cdao.selectCommentsByPostId(postId);

     // JSP에서 쓸 데이터 저장
        request.setAttribute("post", post);
        request.setAttribute("comments", comments);
        request.setAttribute("postId", postId);

     // 화면으로 포워드
        request.getRequestDispatcher("/board/detail.jsp")
               .forward(request, response);
    }

}
