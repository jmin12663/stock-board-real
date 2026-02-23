package com.kostock.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.kostock.model.dto.UserDTO;
import com.kostock.util.DBUtil;

public class UserDAO {
    // 회원가입
    public int insertUser(UserDTO dto) {
        int result = 0;

        // ===== SYSDATE → NOW() 변경 =====
        String sql = "INSERT INTO Users (userid, password, name, role, join_date) "
        		+ "VALUES (?, ?, ?, ?, NOW())";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dto.getUserid());
            pstmt.setString(2, dto.getPassword());
            pstmt.setString(3, dto.getName());
            pstmt.setString(4, dto.getRole());

            result = pstmt.executeUpdate(); // 데이터 저장

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // 로그인 체크
    public UserDTO login(String userid, String password) {

        UserDTO dto = null;
        String sql = "SELECT userid, password, name, role, join_date "
                   + "FROM USERS WHERE userid=? AND password=?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userid);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                dto = new UserDTO();
                dto.setUserid(rs.getString("userid"));
                dto.setPassword(rs.getString("password"));
                dto.setName(rs.getString("name"));
                dto.setRole(rs.getString("role"));
                dto.setJoinDate(rs.getDate("join_date"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dto;
    }
    // 회원 탈퇴 
    public int deleteUser(String userid) {
        int result = 0;
        String sql = "DELETE FROM users WHERE userid = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userid);
            result = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // 회원 정보 수정 이름
    public int updateUserName(String userid, String newName) {
        int result = 0;
        
        String sql = "UPDATE USERS SET name = ? WHERE userid = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newName);
            pstmt.setString(2, userid);
            
            result = pstmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }

    // 회원 정보 수정 비밀번호
    public int updatePassword(String userid, String currentPassword, String newPassword) {
        int result = 0;
        
        // 1. 현재 비밀번호 확인
        String checkSql = "SELECT COUNT(*) FROM USERS WHERE userid = ? AND password = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
            
            checkPstmt.setString(1, userid);
            checkPstmt.setString(2, currentPassword);
            
            ResultSet rs = checkPstmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) == 1) {
                // 2. 현재 비밀번호가 맞으면 새 비밀번호로 변경
                String updateSql = "UPDATE USERS SET password = ? WHERE userid = ?";
                
                try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                    updatePstmt.setString(1, newPassword);
                    updatePstmt.setString(2, userid);
                    
                    result = updatePstmt.executeUpdate();
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }

    // 사용자 정보 조회 
    public UserDTO getUserInfo(String userid) {
        UserDTO dto = null;
        
        String sql = "SELECT userid, name, role, join_date FROM USERS WHERE userid = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userid);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                dto = new UserDTO();
                dto.setUserid(rs.getString("userid"));
                dto.setName(rs.getString("name"));
                dto.setRole(rs.getString("role"));
                dto.setJoinDate(rs.getDate("join_date"));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return dto;
    }
}