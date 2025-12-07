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

	        String sql = "INSERT INTO Users (userid, password, name, role, join_date) "
	        		+ "VALUES (?, ?, ?, ?, SYSDATE)";

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
	}

