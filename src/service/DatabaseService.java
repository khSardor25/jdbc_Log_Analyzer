package service;

import config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseService {
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

    public static void db_push(String id, String date, String method, String endp, String status, String bytes_sen, String u_agent) throws SQLException {
        String url = DatabaseConfig.getUrl();
        String user = DatabaseConfig.getUser();
        String pwd = DatabaseConfig.getPwd();
        String sql = "INSERT INTO logs (ip, timestamp, method, endpoint, status, bytes_sent, user_agent)VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection conn = DriverManager.getConnection(url, user, pwd);
                PreparedStatement prStm = conn.prepareStatement(sql);
        ) {
            conn.setAutoCommit(false);
            prStm.setString(1, id);
            prStm.setString(2, date);
            prStm.setString(3, method);
            prStm.setString(4, endp);
            prStm.setInt(5, Integer.parseInt(status));
            prStm.setString(6, String.valueOf(Integer.parseInt(bytes_sen)));
            prStm.setString(7, u_agent);
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


