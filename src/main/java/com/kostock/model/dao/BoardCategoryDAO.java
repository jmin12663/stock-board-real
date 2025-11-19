package com.kostock.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.kostock.model.dto.BoardCategoryDTO;
import com.kostock.util.DBUtil;

public class BoardCategoryDAO {

    public List<BoardCategoryDTO> selectAllCategories() {

        List<BoardCategoryDTO> list = new ArrayList<>();

        String sql = "SELECT category_id, category_name "
                   + "FROM BOARD_CATEGORY ORDER BY category_id";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                BoardCategoryDTO dto = new BoardCategoryDTO();
                dto.setCategoryId(rs.getInt("category_id"));
                dto.setCategoryName(rs.getString("category_name"));
                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
