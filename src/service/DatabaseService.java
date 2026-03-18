package service;

import config.DatabaseConfig;
import entity.LogEntry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class DatabaseService {
    static final String INSERT_SQL = "INSERT INTO logs (ip, timestamp, method, endpoint, status, bytes_sent, user_agent) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final DateTimeFormatter LOG_TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

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

    public static void db_push(LogEntry entry) throws SQLException {
        Connection conn = getConnection();
        try (conn; PreparedStatement prStm = conn.prepareStatement(INSERT_SQL)) {
            conn.setAutoCommit(false);
            if (!bindLog(prStm, entry)) {
                System.out.println("==========================================");
                System.out.println("Invalid log entry, nothing inserted");
                System.out.println("==========================================\n");
                return;
            }
            prStm.executeUpdate();
            conn.commit();
            System.out.println("==========================================");
            System.out.println("Inserted succesfully");
            System.out.println("==========================================\n");
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
                // best-effort rollback
            }
            throw e;
        }
    }

    public static void db_push(String ip, String date, String method, String endpoint, String status, String bytesSent, String userAgent) throws SQLException {
        db_push(new LogEntry(ip, date, method, endpoint, status, bytesSent, userAgent));
    }

    static boolean bindLog(PreparedStatement prStm, LogEntry entry) throws SQLException {
        Timestamp ts = parseTimestamp(entry.getTimestamp());
        Integer status = parseInt(entry.getStatus());
        Long bytes = parseBytes(entry.getBytesSent());

        if (ts == null || status == null || bytes == null) {
            return false;
        }

        prStm.setString(1, entry.getIp());
        prStm.setTimestamp(2, ts);
        prStm.setString(3, entry.getMethod());
        prStm.setString(4, entry.getEndpoint());
        prStm.setInt(5, status);
        prStm.setLong(6, bytes);
        prStm.setString(7, entry.getUserAgent());
        return true;
    }

    private static Timestamp parseTimestamp(String raw) {
        try {
            OffsetDateTime odt = OffsetDateTime.parse(raw, LOG_TIMESTAMP_FORMAT);
            return Timestamp.from(odt.toInstant());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Long parseBytes(String value) {
        if ("-".equals(value)) {
            return 0L;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
