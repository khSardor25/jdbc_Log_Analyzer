import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Stream;

public class fileParser {
    public static void parseFileFast(String filePath) {
        System.out.println("Parsing file in multiple threads" + filePath);
        long start = System.currentTimeMillis();

        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "155795";

        String sql = "INSERT INTO logs (ip, timestamp, method, endpoint, status, bytes_sent, user_agent) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

          
            Files.lines(Paths.get(filePath))
                    .parallel()
                    .map(log_analyzer::checker)
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

    public static void parseFromUrl(String urlString) {
        System.out.println("Downloading log with: " + urlString);
        long start = System.currentTimeMillis();

        try {
           
            URL url = new URI(urlString).toURL();
            Path tempFile = Files.createTempFile("remote-log-", ".log");

            try (InputStream in = url.openStream()) {
                Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            System.out.println("File downloaded:" + tempFile.toString());

            
            parseFileFast(tempFile.toString());

       
            Files.deleteIfExists(tempFile);

        } catch (Exception e) {
            System.out.println("Error while downloading");
            e.printStackTrace();
        }

        long total = (System.currentTimeMillis() - start) / 1000;
        System.out.println("Log analysed in " + total + " secs");
    }

}
