package service;

import config.DatabaseConfig;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class FileParserService {
    public static void parseFileFast(String filePath) {
        System.out.println("Parsing file in multiple threads" + filePath);
        long start = System.currentTimeMillis();

        String url = DatabaseConfig.getUrl();
        String user = DatabaseConfig.getUser();
        String password = DatabaseConfig.getPwd();

        String sql = "INSERT INTO logs (ip, timestamp, method, endpoint, status, bytes_sent, user_agent) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

          
            Files.lines(Paths.get(filePath))
                    .parallel()
                    .map(LogAnalyzerService::checker)
                    .forEach(entry -> {
                        try {
                            ps.setString(1, entry.ip);
                            ps.setString(2, entry.timestamp);
                            ps.setString(3, entry.method);
                            ps.setString(4, entry.endpoint);
                            ps.setInt(5, Integer.parseInt(entry.status));
                            ps.setInt(6, entry.bytesSent.equals("-") ? 0 : Integer.parseInt(entry.bytesSent));
                            ps.setString(7, entry.userAgent);

                            ps.addBatch();

                        } catch (Exception e) {
                            
                        }
                    });

            ps.executeBatch();
            conn.commit();

            long totalMillis = System.currentTimeMillis() - start;

            System.out.println("==========================================");

            System.out.println("Ineserted in: "
                    + (totalMillis / 1000.0) + " secs");
            System.out.println("==========================================\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
