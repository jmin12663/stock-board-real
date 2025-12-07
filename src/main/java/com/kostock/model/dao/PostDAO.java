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
// ----------------------------------------------------검색어 없을때 리스트
    // 게시글 페이징
    public List<PostDTO> selectPostListByCategoryPaging(int categoryId, int page, int pageSize) {

        List<PostDTO> list = new ArrayList<>();

        int start = (page - 1) * pageSize + 1; // 1-based
        int end   = page * pageSize;

        String sql =
            "SELECT * FROM ( " +
            "   SELECT " +
            		   // 정렬해서 list.no 부여해줌  // 최신글이 list_no = 1
            "          ROW_NUMBER() OVER (ORDER BY created_at DESC, post_id DESC) AS list_no, " + 
            "          post_id, userid, category_id, stock_code, " + // 내림차순으로 정렬
            "          title, created_at, view_count " +
            "   FROM POST " +
            "   WHERE category_id = ? " + //게시판 종류
            ") " +
            "WHERE list_no BETWEEN ? AND ? " +
            "ORDER BY list_no ASC";  // 1,2,3,... 순서로 -> 화면에서 위가 최신글

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, categoryId);
            pstmt.setInt(2, start);
            pstmt.setInt(3, end);

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
                    dto.setListNo(rs.getInt("list_no")); // 화면용 번호
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    //카테고리 별 글 개수
    public int getPostCountByCategory(int categoryId) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM POST WHERE category_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, categoryId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) count = rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
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
    
 // 작성자 본인 글인지 체크 (보안용)
    public boolean isOwner(int postId, String userid) {
        boolean ok = false;
        //게시물과 유저 아이디가 같은지 검증 결과로 1이 나옴
        String sql = "SELECT COUNT(*) FROM POST WHERE post_id=? AND userid=?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            pstmt.setString(2, userid);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) ok = (rs.getInt(1) == 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok;
    }

    // 수정
    public int updatePost(PostDTO dto) {
        int result = 0;

        String sql = "UPDATE POST "
                   + "SET title=?, content=?, stock_code=?, updated_at=SYSDATE "
                   + "WHERE post_id=?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dto.getTitle());
            pstmt.setString(2, dto.getContent());
            pstmt.setString(3, dto.getStockCode()); // null 가능
            pstmt.setInt(4, dto.getPostId());

            result = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 게시글 삭제
    public int deletePost(int postId) {
        int result = 0;

        String sql = "DELETE FROM POST WHERE post_id=?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            result = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    // -------------------------------------------------검색어 있을때 목록
    // 카테고리 별 검색 후 게시글 개수
    public int getPostCountByCategorySearch(int categoryId, String field, String searchWord) {
        int count = 0; //게시글 갯수
        
        // field 값이 title or content 경우
        String where;
        if ("content".equals(field)) {
            where = "DBMS_LOB.INSTR(content, ?) > 0";
        } else { // title
            where = "LOWER(title) LIKE '%'||LOWER(?)||'%'";
        }
        
        //카테고리 값에 맞는 게시글 검색
        String sql = "SELECT COUNT(*) FROM POST WHERE category_id = ? AND " + where;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, categoryId);
            pstmt.setString(2, searchWord);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }
    
    // 카테고리 별 검색 게시물 목록 조회
    public List<PostDTO> selectPostListByCategorySearchPaging(
            int categoryId, String field, String searchWord,
            int page, int pageSize) {

        List<PostDTO> list = new ArrayList<>();
        int start = (page - 1) * pageSize + 1;
        int end   = page * pageSize;

        String where; // where에 field 값에 맞춰 조건 동적 생성
        if ("content".equals(field)) {
            where = "DBMS_LOB.INSTR(content, ?) > 0";
        } else { // title (또는 기타)
            where = "LOWER(title) LIKE '%'||LOWER(?)||'%'";
        }

        String sql =
            "SELECT * FROM ( " +
            "   SELECT " +
            			// 정렬해서 list.no 부여해줌  // 최신글이 list_no = 1
            "          ROW_NUMBER() OVER (ORDER BY created_at DESC, post_id DESC) AS list_no, " +
            "          post_id, userid, category_id, stock_code, title, created_at, view_count " +
            "   FROM POST " + // 내림차순으로 정렬
            "   WHERE category_id = ? AND " + where + //검색조건과 카테고리에 맞춤
            ") " +
            "WHERE list_no BETWEEN ? AND ? " +
            "ORDER BY list_no ASC";
        	//다시 오름차순으로
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, categoryId);
            pstmt.setString(2, searchWord);
            pstmt.setInt(3, start);
            pstmt.setInt(4, end);

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
                    dto.setListNo(rs.getInt("list_no"));
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    
}
