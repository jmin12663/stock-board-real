package com.kostock.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.kostock.model.dto.PostDTO;
import com.kostock.util.DBUtil;

public class PostDAO {

    // 글쓰기
    public int insertPost(PostDTO dto) {
        int result = 0;

        String sql = "INSERT INTO POST "
                   + "(userid, category_id, stock_code, title, content) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dto.getUserid());
            pstmt.setInt(2, dto.getCategoryId());
            pstmt.setString(3, dto.getStockCode());
            pstmt.setString(4, dto.getTitle());
            pstmt.setString(5, dto.getContent());

            result = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // 카테고리별 전체 목록 (ex. 자유게시판, 정보게시판 등)
    public List<PostDTO> selectPostListByCategory(int categoryId) {

        List<PostDTO> list = new ArrayList<>();

        String sql =
            "SELECT post_id, userid, category_id, stock_code, " +
            "       title, created_at, view_count, " +
            "       ROW_NUMBER() OVER (ORDER BY post_id DESC) AS list_no " +  // ★ 추가
            "FROM POST " +
            "WHERE category_id = ? " +
            "ORDER BY post_id ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, categoryId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PostDTO dto = new PostDTO();
                    dto.setPostId(rs.getInt("post_id"));
                    dto.setUserid(rs.getString("userid"));
                    dto.setCategoryId(rs.getInt("category_id"));
                    dto.setStockCode(rs.getString("stock_code"));
                    dto.setTitle(rs.getString("title"));
                    dto.setCreatedAt(rs.getDate("created_at"));
                    dto.setViewCount(rs.getInt("view_count"));

                    // ★ 여기서 화면용 번호 세팅
                    dto.setListNo(rs.getInt("list_no"));

                    list.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 게시글 상세 조회
    public PostDTO selectPostById(int postId) {
        PostDTO dto = null;

        String sql = "SELECT post_id, userid, category_id, stock_code, "
                   + "       title, content, created_at, updated_at, view_count "
                   + "FROM POST "
                   + "WHERE post_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    dto = new PostDTO();
                    dto.setPostId(rs.getInt("post_id"));
                    dto.setUserid(rs.getString("userid"));
                    dto.setCategoryId(rs.getInt("category_id"));
                    dto.setStockCode(rs.getString("stock_code"));
                    dto.setTitle(rs.getString("title"));
                    dto.setContent(rs.getString("content"));
                    dto.setCreatedAt(rs.getDate("created_at"));
                    dto.setUpdatedAt(rs.getDate("updated_at"));
                    dto.setViewCount(rs.getInt("view_count"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dto;
    }

    // 조회수 +1
    public void increaseViewCount(int postId) {
        String sql = "UPDATE POST SET view_count = view_count + 1 "
                   + "WHERE post_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
