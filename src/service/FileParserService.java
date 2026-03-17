package service;

import config.DatabaseConfig;
import entity.LogEntry;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.stream.Stream;

public class FileParserService {
    public static void parseFileFast(String filePath) {
        System.out.println("Parsing file: " + filePath);
        long start = System.currentTimeMillis();

        String sql = "INSERT INTO logs (ip, timestamp, method, endpoint, status, bytes_sent, user_agent) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             Stream<String> lines = Files.lines(Paths.get(filePath))) {

            conn.setAutoCommit(false);

            int count = 0;
            int batchSize = 1000;

            // Using sequential stream to ensure thread-safety with PreparedStatement and Connection
            Iterable<String> iterable = lines::iterator;
            for (String line : iterable) {
                LogEntry entry = LogAnalyzerService.checker(line, true);
                if (entry != null) {
                    try {
                        ps.setString(1, entry.getIp());
                        ps.setString(2, entry.getTimestamp());
                        ps.setString(3, entry.getMethod());
                        ps.setString(4, entry.getEndpoint());
                        ps.setInt(5, Integer.parseInt(entry.getStatus()));
                        ps.setInt(6, entry.getBytesSent().equals("-") ? 0 : Integer.parseInt(entry.getBytesSent()));
                        ps.setString(7, entry.getUserAgent());

                        ps.addBatch();
                        count++;

                        if (count % batchSize == 0) {
                            ps.executeBatch();
                        }
                    } catch (Exception e) {
                        // Skip problematic lines
                    }
                }
            }

            ps.executeBatch();
            conn.commit();

            long totalMillis = System.currentTimeMillis() - start;

            System.out.println("==========================================");
            System.out.println("Inserted " + count + " entries in: " + (totalMillis / 1000.0) + " secs");
            System.out.println("==========================================\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
