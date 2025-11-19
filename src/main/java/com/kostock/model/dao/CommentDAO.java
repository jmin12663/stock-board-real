package com.kostock.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.kostock.model.dto.CommentDTO;
import com.kostock.util.DBUtil;

public class CommentDAO {

    // 댓글 작성
    public int insertComment(CommentDTO dto) {
        int result = 0;

        String sql = "INSERT INTO COMMENTS (post_id, userid, content) "
                   + "VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dto.getPostId());
            pstmt.setString(2, dto.getUserid());
            pstmt.setString(3, dto.getContent());

            result = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // 특정 게시글의 댓글 목록
    public List<CommentDTO> selectCommentsByPostId(int postId) {
        List<CommentDTO> list = new ArrayList<>();

        String sql = "SELECT comment_id, post_id, userid, content, created_at "
                   + "FROM COMMENTS WHERE post_id = ? "
                   + "ORDER BY comment_id ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CommentDTO dto = new CommentDTO();
                    dto.setCommentId(rs.getInt("comment_id"));
                    dto.setPostId(rs.getInt("post_id"));
                    dto.setUserid(rs.getString("userid"));
                    dto.setContent(rs.getString("content"));
                    dto.setCreatedAt(rs.getDate("created_at"));
                    list.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
