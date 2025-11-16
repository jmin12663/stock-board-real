package com.kostock.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {

    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE"; 
    private static final String USER = "KOSTOCK";    // SQL Developer에서 만든 계정
    private static final String PASSWORD = "1234";

    public static Connection getConnection() {
        Connection conn = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");  // 오라클 드라이버 로드
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("DB 연결 성공");
        } catch (Exception e) {
            System.out.println("DB 연결 실패");
            e.printStackTrace();
        }

        return conn;
    }
}
