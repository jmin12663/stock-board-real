package com.kostock.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kostock.model.dto.IndexSummaryDTO;
import com.kostock.model.dto.StockPriceDTO;
import com.kostock.util.DBUtil;

public class StockDAO {

    // ===== LAG, ROW_NUMBER는 MySQL 8.0+에서 동일하게 사용 가능 =====   
    // 상단 지수 요약 조회 코드
    public List<IndexSummaryDTO> getIndexSummaries() throws SQLException {
        List<IndexSummaryDTO> list = new ArrayList<>();
        String sql =
            "SELECT stock_code, stock_name, close_price, prev_close, " +
            "       close_price - prev_close AS diff, " +
            "       ROUND((close_price - prev_close)/prev_close*100, 2) AS diff_rate " +
            "FROM ( " +
            "  SELECT s.stock_code, s.stock_name, " +
            "         p.price_date, p.close_price, " +
            "         LAG(p.close_price) OVER (PARTITION BY s.stock_code ORDER BY p.price_date) AS prev_close, " +
            "         ROW_NUMBER() OVER (PARTITION BY s.stock_code ORDER BY p.price_date DESC) AS rn " +
            "  FROM STOCK s " +
            "  JOIN STOCK_PRICE_D p ON s.stock_code = p.stock_code " +
            "  WHERE s.stock_code IN ('KOSPI', 'KOSDAQ') " +
            ") AS subquery WHERE rn = 1";
            // MySQL은 서브쿼리에 alias 필수

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                IndexSummaryDTO dto = new IndexSummaryDTO();
                dto.setStockCode(rs.getString("stock_code"));
                dto.setStockName(rs.getString("stock_name"));
                dto.setClosePrice(rs.getDouble("close_price"));
                dto.setPrevClosePrice(rs.getDouble("prev_close"));
                dto.setDiff(rs.getDouble("diff"));
                dto.setDiffRate(rs.getDouble("diff_rate"));
                list.add(dto);
            }
        }
        return list;
    }
    
    // ===== TRUNC(SYSDATE) → CURDATE() 변경 =====
    // 특정 지수 일봉 리스트 차트 만들기 용
    public List<StockPriceDTO> getDailyPrices(String code, int days) throws SQLException {
        List<StockPriceDTO> list = new ArrayList<>();

        String sql =
            "SELECT price_date, close_price " +
            "FROM STOCK_PRICE_D " +
            "WHERE stock_code = ? " +
            "  AND price_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
            "ORDER BY price_date";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);
            pstmt.setInt(2, days);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    StockPriceDTO dto = new StockPriceDTO();
                    dto.setPriceDate(rs.getDate("price_date"));
                    dto.setClosePrice(rs.getDouble("close_price"));
                    list.add(dto);
                }
            }
        }
        return list;
    }
    
    // 지수 일봉 저장 메서드 (MySQL의 UPSERT 문법 사용)
    public void upsertDailyClosePrices(String stockCode, List<StockPriceDTO> list) throws SQLException {
        // MySQL: INSERT ... ON DUPLICATE KEY UPDATE 사용
        String sql = """
            INSERT INTO STOCK_PRICE_D (STOCK_CODE, PRICE_DATE, CLOSE_PRICE)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE CLOSE_PRICE = VALUES(CLOSE_PRICE)
            """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (StockPriceDTO dto : list) {
                java.sql.Date date = dto.getPriceDate();
                
                pstmt.setString(1, stockCode);
                pstmt.setDate(2, date);
                pstmt.setDouble(3, dto.getClosePrice());
                
                pstmt.executeUpdate();
            }
        }
    }
        
    // 주가 데이터에서 날짜가 가장 최근인 날짜를 얻음
    public java.sql.Date getLatestPriceDate(String stockCode) throws SQLException {
        String sql = "SELECT MAX(PRICE_DATE) AS LAST_DATE " +
                     "FROM STOCK_PRICE_D " +
                     "WHERE STOCK_CODE = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, stockCode);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDate("LAST_DATE");
                }
            }
        }
        return null;
    }
}