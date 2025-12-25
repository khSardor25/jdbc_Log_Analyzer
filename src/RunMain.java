import config.DatabaseConfig;
import entity.LogEntry;
import service.AnalyticsService;
import service.DatabaseService;
import service.FileParserService;
import service.LogAnalyzerService;

import java.sql.SQLException;
import java.util.Scanner;

public class RunMain {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to log analyser !!!");

        while (true) {
            System.out.println("\nChoose type of operation");
            System.out.println("Press 1 to check the connection with Database");
            System.out.println("Press 2 to see Analytics");
            System.out.println("Press 3 to parse the String");
            System.out.println("Press 4 to parse Logs from file (multithreading)");
            System.out.println("Press 5 to quit");
            System.out.print("Respond: ");

            int respond = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (respond) {

                case 1 -> {
                    String url = DatabaseConfig.getUrl();
                    String user = DatabaseConfig.getUser();
                    String pwd = DatabaseConfig.getPwd();
                    DatabaseService.connectionCheck(url, user, pwd);
                }

                case 2 -> {
                    AnalyticsService.analytics_menu();
                }

                case 3 -> {
                    System.out.print("Paste your log entry: ");
                    String logInput = scanner.nextLine().trim();

                    if (logInput.isEmpty()) {
                        System.out.println("==========================================");
                        System.out.println("Error: Empty input received");
                        System.out.println("==========================================");
                        break;
                    }

                    LogEntry myLog = LogAnalyzerService.checker(logInput);

                    System.out.print("Do you want to push this Log into Database Y/N: ");
                    String answer = scanner.nextLine();

                    if (answer.equalsIgnoreCase("Y")) {
                        try {
                            DatabaseService.db_push(
                                    myLog.ip,
                                    myLog.timestamp,
                                    myLog.method,
                                    myLog.endpoint,
                                    myLog.status,
                                    myLog.bytesSent,
                                    myLog.userAgent
                            );
                        } catch (SQLException e) {
                            System.out.println("Database error occurred");
                        }
                    }
                }

                case 4 -> {
                    System.out.print("Enter path: ");
                    String filePath = scanner.nextLine();
                    FileParserService.parseFileFast(filePath);
                }

                case 5 -> {
                    System.out.println("GoodBye");
                    return;
                }

                default -> {
                    System.out.println("Invalid option. Please choose 1–5.");
                }
            }
        }
    }
}
