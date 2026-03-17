package service;

import config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseService {

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            DatabaseConfig.getUrl(),
            DatabaseConfig.getUser(),
            DatabaseConfig.getPwd()
        );
    }

    public static void connectionCheck(String url, String user, String pwd) {
        try (Connection conn = DriverManager.getConnection(url, user, pwd)) {
            System.out.println("==========================================");
            System.out.println("Connection established succesfully !!!");
            System.out.println("==========================================\n");
        } catch (SQLException var8) {
            System.out.println("==========================================");
            System.out.println("Ooops something went wrong, Connection to Database failed !!!");
            System.out.println("==========================================\n");
            var8.printStackTrace();
        }

    }

    public static void db_push(String ip, String date, String method, String endpoint, String status, String bytesSent, String userAgent) throws SQLException {
        String sql = "INSERT INTO logs (ip, timestamp, method, endpoint, status, bytes_sent, user_agent) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection conn = getConnection();
                PreparedStatement prStm = conn.prepareStatement(sql);
        ) {
            conn.setAutoCommit(false);
            prStm.setString(1, ip);
            prStm.setString(2, date);
            prStm.setString(3, method);
            prStm.setString(4, endpoint);
            prStm.setInt(5, Integer.parseInt(status));

            // Handle "-" in bytesSent
            int bytes = bytesSent.equals("-") ? 0 : Integer.parseInt(bytesSent);
            prStm.setInt(6, bytes);

            prStm.setString(7, userAgent);
            prStm.executeUpdate();
            conn.commit();
            System.out.println("==========================================");
            System.out.println("Inserted succesfully");
            System.out.println("==========================================\n");
        } catch (SQLException e) {
            System.out.println("==========================================");
            System.out.println("Something went wrong");
            System.out.println("==========================================\n");
            e.printStackTrace();
        }

    }
}
