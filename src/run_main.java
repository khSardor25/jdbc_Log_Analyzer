

import java.sql.SQLException;
import java.util.Scanner;

public class run_main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to log analayser !!!");

        while(true) {
            System.out.println("Choose type of operation");
            System.out.println("Press 1 to check the connection with Database");
            System.out.println("Press 2 to see Analytics");
            System.out.println("Press 3 to parse the String");
            System.out.println("Press 4 to parse Logs from file (multithreading)");
            System.out.println("Press 5 to parse log from URL (http/https)");
            System.out.println("Press 6 to quit");
            System.out.print("Respond: ");
            int respond = scanner.nextInt();
            if (respond == 6) {
                System.out.println("GoodBye");
                return;
            }

            if (respond == 1) {
                String url = "jdbc:postgresql://localhost:5432/postgres";
                String user = "postgres";
                String pwd = "155795";
                dataBaseFunc.connectionCheck(url, user, pwd);
            }

            if (respond == 3) {
                System.out.print("Paste your log entry: ");
                Scanner scanner2 = new Scanner(System.in);
                String respond_string = scanner2.nextLine().trim();
                if (!respond_string.isEmpty()) {
                    LogEntry myLog = log_analyzer.checker(respond_string);
                    System.out.print("Do you want to push this Log into Database Y/N: ");
                    Scanner scanner33 = new Scanner(System.in);
                    String respond33 = scanner33.nextLine();
                    if (respond33.equalsIgnoreCase("Y")){
                        try{
                            dataBaseFunc.db_push(
                                    myLog.ip,
                            myLog.timestamp,
                            myLog.method,
                            myLog.endpoint,
                            myLog.status,
                            myLog.bytesSent,
                            myLog.userAgent

                        );
                        }
                        catch(SQLException e){
                            System.out.println("Error");
                        }
                    }



                } else {
                    System.out.println("==========================================");
                    System.out.println("Error: Empty input received");
                    System.out.println("==========================================\n");
                }
            }

            if (respond == 4) {
                Scanner scanner4 = new Scanner(System.in);
                System.out.println("Enter path");
                String file_path = scanner4.nextLine();
                fileParser.parseFileFast(file_path);
            }

            if (respond == 2) {
                analytics.analytics_menu();
            }


            if (respond == 5) {
                System.out.print("Paste url : ");
                Scanner sc = new Scanner(System.in);
                String url = sc.nextLine().trim();

                if (!url.isEmpty()) {
                    fileParser.parseFromUrl(url);
                } else {
                    System.out.println("Empty log");
                }
            }
        }
    }
}

