package com.kostock.model.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kostock.model.dto.TradeRecordDTO;
import com.kostock.util.DBUtil;

public class TradeRecordDAO {
    //  INSERT / DELETE
    public int insert(TradeRecordDTO dto) {
        int result = 0;
        // 컨트롤러에서 온 dto의 값을 동적으로 DB에 저장함
        String sql =
            "INSERT INTO trade_record " +
            "(userid, stock_code, stock_name, trade_date, trade_type, quantity, price, memo) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dto.getUserid());
            pstmt.setString(2, dto.getStockCode()); // null 가능
            pstmt.setString(3, dto.getStockName());
            pstmt.setDate(4, new java.sql.Date(dto.getTradeDate().getTime()));
            pstmt.setString(5, dto.getTradeType());
            pstmt.setInt(6, dto.getQuantity());
            pstmt.setBigDecimal(7, dto.getPrice());
            pstmt.setString(8, dto.getMemo());

            result = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public int delete(int recordId, String userid) {
        int result = 0;

        String sql = "DELETE FROM trade_record WHERE record_id=? AND userid=?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, recordId);
            pstmt.setString(2, userid);
            result = pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 월 합계 (BUY or SELL) : 금액 = SUM(quantity * price)
    public BigDecimal sumAmountByMonth(String userid, int year, int month, String tradeType) {
        BigDecimal sum = BigDecimal.ZERO;

        MonthRange range = getMonthRange(year, month);
        String sql = // 해당 월의 모든 거래의 값의 합산
            "SELECT SUM(quantity * price) AS amt " +
            "FROM trade_record " +
            "WHERE userid=? AND trade_type=? " +
            "  AND trade_date >= ? AND trade_date < ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userid);
            pstmt.setString(2, tradeType);
            pstmt.setDate(3, Date.valueOf(range.start));
            pstmt.setDate(4, Date.valueOf(range.endExclusive));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) sum = nvl(rs.getBigDecimal("amt"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sum;
    }


    //  월 거래 전체 리스트 (달력용)
    public List<TradeRecordDTO> selectByMonth(String userid, int year, int month) {
        List<TradeRecordDTO> list = new ArrayList<>();
        //날짜 범위 계산
        MonthRange range = getMonthRange(year, month);
        // trade_record의 사용자별 한 달 기준 거래 날짜 순서를 정렬함
        String sql = //trade_record의 모든 필드
            "SELECT record_id, userid, stock_code, stock_name, trade_date, trade_type, " +
            "       quantity, price, memo, created_at " +
            "FROM trade_record " +	// 특정 사용자와 해당 월 날짜만 필터
            "WHERE userid=? AND trade_date >= ? AND trade_date < ? " +
            "ORDER BY trade_date ASC, record_id DESC"; // 날짜를 오름차순, 최근거래 내림차순

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userid);
            pstmt.setDate(2, Date.valueOf(range.start));
            pstmt.setDate(3, Date.valueOf(range.endExclusive));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToTradeRecord(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //  private 유틸 메서드
    // ResultSet 한 행 → TradeRecordDTO
    private TradeRecordDTO mapRowToTradeRecord(ResultSet rs) throws java.sql.SQLException {
        TradeRecordDTO dto = new TradeRecordDTO();
        dto.setRecordId(rs.getInt("record_id"));
        dto.setUserid(rs.getString("userid"));
        dto.setStockCode(rs.getString("stock_code"));
        dto.setStockName(rs.getString("stock_name"));
        dto.setTradeDate(rs.getDate("trade_date"));
        dto.setTradeType(rs.getString("trade_type"));
        dto.setQuantity(rs.getInt("quantity"));
        dto.setPrice(rs.getBigDecimal("price"));
        dto.setMemo(rs.getString("memo"));
        dto.setCreatedAt(rs.getDate("created_at"));
        return dto;
    }

    // 월 시작/끝(다음달 1일) 범위
    private MonthRange getMonthRange(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate endExclusive = start.plusMonths(1);
        return new MonthRange(start, endExclusive);
    }

    private static class MonthRange {
        final LocalDate start;
        final LocalDate endExclusive;
        MonthRange(LocalDate start, LocalDate endExclusive) {
            this.start = start;
            this.endExclusive = endExclusive;
        }
    }

    // BigDecimal null 방어
    private BigDecimal nvl(BigDecimal value) {
        return (value == null) ? BigDecimal.ZERO : value;
    }
    
    // 사용자의 기래 기록 수 조
    public int getUserTradeCount(String userid) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM trade_record WHERE userid = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userid);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) count = rs.getInt(1);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return count;
    }

    // 사용자의 총 매수 금
    public BigDecimal getUserTotalBuyAmount(String userid) {
        BigDecimal total = BigDecimal.ZERO;
        String sql = "SELECT SUM(quantity * price) FROM trade_record WHERE userid = ? AND trade_type = 'BUY'";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userid);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal result = rs.getBigDecimal(1);
                    if (result != null) total = result;
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return total;
    }

    // 사용자의 총 매도 금액 
    public BigDecimal getUserTotalSellAmount(String userid) {
        BigDecimal total = BigDecimal.ZERO;
        String sql = "SELECT SUM(quantity * price) FROM trade_record WHERE userid = ? AND trade_type = 'SELL'";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userid);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal result = rs.getBigDecimal(1);
                    if (result != null) total = result;
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return total;
    }
}
