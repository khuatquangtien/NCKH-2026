package vn.edu.epu.quanlyhoso.common.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Cấu hình trỏ tới Docker MySQL ở cổng 3307
    private static final String URL = "jdbc:mysql://localhost:3307/quan_ly_ho_so_backend?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "123456"; // <-- Đổi mật khẩu tại đây

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}