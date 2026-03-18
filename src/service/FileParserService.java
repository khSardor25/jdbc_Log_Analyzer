package service;

import entity.LogEntry;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Stream;

public class FileParserService {
    public static void parseFileFast(String filePath) throws SQLException {
        System.out.println("Parsing file: " + filePath);
        long start = System.currentTimeMillis();

        Connection conn = DatabaseService.getConnection();
        try (conn;
             PreparedStatement ps = conn.prepareStatement(DatabaseService.INSERT_SQL);
             Stream<String> lines = Files.lines(Paths.get(filePath))) {

            conn.setAutoCommit(false);

            int count = 0;
            int skipped = 0;
            int batchSize = 1000;

            // Using sequential stream to ensure thread-safety with PreparedStatement and Connection
            Iterable<String> iterable = lines::iterator;
            for (String line : iterable) {
                LogEntry entry = LogAnalyzerService.checker(line, true);
                if (entry != null) {
                    if (DatabaseService.bindLog(ps, entry)) {
                        ps.addBatch();
                        count++;
                        if (count % batchSize == 0) {
                            ps.executeBatch();
                        }
                    } else {
                        skipped++;
                    }
                } else {
                    skipped++;
                }
            }

            ps.executeBatch();
            conn.commit();

            long totalMillis = System.currentTimeMillis() - start;

            System.out.println("==========================================");
            System.out.println("Inserted " + count + " entries in: " + (totalMillis / 1000.0) + " secs");
            System.out.println("Skipped " + skipped + " invalid entries");
            System.out.println("==========================================\n");

        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (Exception ignored) {
                // best-effort rollback
            }
            e.printStackTrace();
        }
    }
}
