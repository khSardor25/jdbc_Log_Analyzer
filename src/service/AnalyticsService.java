package service;

import config.DatabaseConfig;

import java.sql.*;
import java.util.Scanner;

public class AnalyticsService {
    public static void analytics_menu(){
        Scanner scanner66 = new Scanner(System.in);

        while(true) {
            System.out.println("Choose type of operation");
            System.out.println("Press 1 to see top 10 MOST active IPS");
            System.out.println("Press 2 to see top 10 MOST popular endpoints");
            System.out.println("Press 3 to see the amount of internal server errors");
            System.out.println("Press 4 to see hourly traffic");
            System.out.println("Press 5 to quit");
            System.out.print("Respond: ");
            int respond = scanner66.nextInt();
            switch(respond){
                case 5:
                    System.out.println("Goodbye");
                    return;

                case 1:
                    topIp();
                    break;
                case 2:
                    popular_endpoints();
                    break;
                case 3:
                    errors5xx();
                    break;
                case 4:
                    trafficByHour();
                    break;

            }


        }

    }








    public static void topIp() {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "155795";
        String sql = "SELECT ip, COUNT(*) as cnt FROM logs GROUP BY ip ORDER BY cnt DESC LIMIT 10;";

        try (
                Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
        ) {
            System.out.println("==========================================");
            System.out.println("Top 10 most active IPs:");
            System.out.println("----------------------------------------");
            System.out.println("IP               | Amount of requests");
            System.out.println("----------------------------------------");

            while(rs.next()) {
                String ip = rs.getString("ip");
                int count = rs.getInt("cnt");
                System.out.printf("%-16s | %d%n", ip, count);
            }

            System.out.println("==========================================\n");
        } catch (SQLException e) {
            System.out.println("Error");
            e.printStackTrace();
        }

    }
    public static void popular_endpoints(){
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String pwd = "155795";

        String sql = "SELECT endpoint, COUNT(*) as hits FROM logs GROUP BY endpoint ORDER BY hits DESC LIMIT 10;";
        try(Connection conn = DriverManager.getConnection(url,user,pwd);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
        ){
            System.out.println("==========================================");
            System.out.println("Top 10 most actively visited endpoints:");
            System.out.println("----------------------------------------");
            System.out.println("EndPoint               | Amount of requests");
            System.out.println("----------------------------------------");

            while(rs.next()) {
                String endpoint = rs.getString("endpoint");
                int hits = rs.getInt("hits");
                System.out.printf("%-28s │ %,d%n", endpoint, hits);
            }
            System.out.println("==========================================\n");


        }
        catch(SQLException e){
            System.out.println("Error, smthing went wrong");
        }



    }

    public static void errors5xx() {
        String url = DatabaseConfig.getUrl();
        String user = DatabaseConfig.getUser();
        String password = DatabaseConfig.getPwd();

        String sql = "SELECT COUNT(*) AS errors_5xx FROM logs WHERE status >= 500;";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("==========================================");
            System.out.println("       Internal server errors 5XX   ");
            System.out.println("==========================================\n");

            if (rs.next()) {
                long errors = rs.getLong("errors_5xx");
                long total = getTotalRequests();  

                System.out.printf("Errors (500–599): %,d%n", errors);

                if (total > 0) {
                    double percent = 100.0 * errors / total;
                    System.out.printf("Rate errors / totalt: %.4f%%%n", percent);
                }
            }

            System.out.println("==========================================\n");

        } catch (SQLException e) {
            System.out.println("Error while analysing 5xx ");
            e.printStackTrace();
        }
    }


    private static long getTotalRequests() {
        String sql = "SELECT COUNT(*) FROM logs";
        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/logsdb", "postgres", "155795");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    public static void trafficByHour() {
        String url = DatabaseConfig.getUrl();
        String user = DatabaseConfig.getUser();
        String password = DatabaseConfig.getPwd();

        String sql = """
        SELECT 
            date_trunc('hour', timestamp::timestamp) AS hour,
            COUNT(*) AS requests
        FROM logs 
        GROUP BY hour 
        ORDER BY hour;
        """;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("==========================================");
            System.out.println("       Hourly Traffic      ");
            System.out.println("------------------------------------------");
            System.out.println("Hours       │ Amount of requests");
            System.out.println("==========================================");

            while (rs.next()) {
                Timestamp hour = rs.getTimestamp("hour");
                long requests = rs.getLong("requests");

              
                String formattedHour = hour.toLocalDateTime()
                        .truncatedTo(java.time.temporal.ChronoUnit.HOURS)
                        .toString()
                        .replace("T", " ");

                System.out.printf("%-19s │ %,12d%n", formattedHour, requests);
            }

            System.out.println("==========================================\n");

        } catch (SQLException e) {
            System.out.println("Error while analysing the traffic");
            e.printStackTrace();
        }
    }




}

