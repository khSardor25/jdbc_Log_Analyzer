package config;

public class DatabaseConfig {
    private static String url = "jdbc:postgresql://localhost:5432/postgres";
    private static String user = "postgres";
    private static String pwd = "155795";


    public static String getPwd() {
            return pwd;
    }


    public static String getUser(){
        return user;
    }


    public static String getUrl(){
        return url;
    }


}
