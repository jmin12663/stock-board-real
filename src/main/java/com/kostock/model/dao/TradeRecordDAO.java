package com.kostock.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.kostock.model.dto.TradeRecordDTO;
import com.kostock.util.DBUtil;

public class TradeRecordDAO {

    // 거래 기록 입력
    public int insertTradeRecord(TradeRecordDTO dto) {
        int result = 0;

        String sql = "INSERT INTO TRADE_RECORD "
                   + "(userid, stock_code, trade_date, trade_type, quantity, price, memo) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dto.getUserid());
            pstmt.setString(2, dto.getStockCode());
            pstmt.setDate(3, new java.sql.Date(dto.getTradeDate().getTime()));
            pstmt.setString(4, dto.getTradeType());
            pstmt.setInt(5, dto.getQuantity());
            pstmt.setDouble(6, dto.getPrice());
            pstmt.setString(7, dto.getMemo());

            result = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // 특정 유저의 거래 기록 목록
    public List<TradeRecordDTO> selectTradeRecordsByUser(String userid) {
        List<TradeRecordDTO> list = new ArrayList<>();

        String sql = "SELECT record_id, userid, stock_code, trade_date, trade_type, "
                   + "       quantity, price, memo, created_at "
                   + "FROM TRADE_RECORD "
                   + "WHERE userid = ? "
                   + "ORDER BY trade_date DESC, record_id DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userid);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TradeRecordDTO dto = new TradeRecordDTO();
                    dto.setRecordId(rs.getInt("record_id"));
                    dto.setUserid(rs.getString("userid"));
                    dto.setStockCode(rs.getString("stock_code"));
                    dto.setTradeDate(rs.getDate("trade_date"));
                    dto.setTradeType(rs.getString("trade_type"));
                    dto.setQuantity(rs.getInt("quantity"));
                    dto.setPrice(rs.getDouble("price"));
                    dto.setMemo(rs.getString("memo"));
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
