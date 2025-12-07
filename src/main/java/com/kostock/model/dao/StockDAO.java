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

       
    // 상단 지수 요약 조회 코드
    public List<IndexSummaryDTO> getIndexSummaries() throws SQLException {
        List<IndexSummaryDTO> list = new ArrayList<>();
        String sql =
            "SELECT stock_code, stock_name, close_price, prev_close, " +
            "       close_price - prev_close AS diff, " + // 직전일과 종가 차이
            		// 변동률 계산
            "       ROUND((close_price - prev_close)/prev_close*100, 2) AS diff_rate " + 
            "FROM ( " +
            "  SELECT s.stock_code, s.stock_name, " +
            "         p.price_date, p.close_price, " +
            		  // ========각 종목을 날짜 순서로 종가를 가져온다
            "         LAG(p.close_price) OVER (PARTITION BY s.stock_code ORDER BY p.price_date) AS prev_close, " +
            		  // ========각 종목을 날짜 내림차순으로 가져온뒤 번호를 최신날짜부터 1부터 매긴다
            "         ROW_NUMBER() OVER (PARTITION BY s.stock_code ORDER BY p.price_date DESC) AS rn " +
            "  FROM STOCK s " + // =======stock + stock-price-d 합치기
            "  JOIN STOCK_PRICE_D p ON s.stock_code = p.stock_code " +
            "  WHERE s.stock_code IN ('KOSPI', 'KOSDAQ') " +
            ") WHERE rn = 1"; // 가장 최신 날짜 데이터 활용

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
    
    
    // 특정 지수 일봉 리스트 차트 만들기 용
    public List<StockPriceDTO> getDailyPrices(String code, int days) throws SQLException {
        List<StockPriceDTO> list = new ArrayList<>();

        String sql =
            "SELECT price_date, close_price " +
            "FROM STOCK_PRICE_D " +
            "WHERE stock_code = ? " +
            // 오늘 자정 기준으로 주식의 일별 주가를 가져옴
            "  AND price_date >= TRUNC(SYSDATE) - ? " +
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
    
    // 지수 일봉 저장 메서드
    public void upsertDailyClosePrices(String stockCode, List<StockPriceDTO> list) throws SQLException {
        String updateSql = """
            UPDATE STOCK_PRICE_D
               SET CLOSE_PRICE = ?
             WHERE STOCK_CODE = ? AND PRICE_DATE = ?
            """;
        String insertSql = """
            INSERT INTO STOCK_PRICE_D (STOCK_CODE, PRICE_DATE, CLOSE_PRICE)
            VALUES (?, ?, ?)
            """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement updatePstmt = conn.prepareStatement(updateSql);
             PreparedStatement insertPstmt = conn.prepareStatement(insertSql)) {

            for (StockPriceDTO dto : list) {
                java.sql.Date date = dto.getPriceDate();
                // UPDATE 먼저 
                updatePstmt.setDouble(1, dto.getClosePrice());
                updatePstmt.setString(2, stockCode);
                updatePstmt.setDate(3, date);

                int updated = updatePstmt.executeUpdate();

                // 업데이트된 행이 없으면 INSERT
                if (updated == 0) {
                    insertPstmt.setString(1, stockCode);
                    insertPstmt.setDate(2, date);
                    insertPstmt.setDouble(3, dto.getClosePrice());
                    insertPstmt.executeUpdate();
                }
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
