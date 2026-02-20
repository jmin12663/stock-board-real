package com.kostock.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {

    // ===== MySQL 연결 정보로 변경 =====
    private static final String URL = "jdbc:mysql://localhost:3306/kostock?serverTimezone=Asia/Seoul&characterEncoding=UTF-8";
    private static final String USER = "kostock";    // MySQL 사용자명 (변경 가능)
    private static final String PASSWORD = "1234";   // MySQL 비밀번호 (변경 가능)

    public static Connection getConnection() {
        Connection conn = null;

        try {
            // MySQL 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("DB 연결 성공");
        } catch (Exception e) {
            System.out.println("DB 연결 실패");
            e.printStackTrace();
        }

        return conn;
    }
}