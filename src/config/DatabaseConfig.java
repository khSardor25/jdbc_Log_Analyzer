package config;

public class DatabaseConfig {
    private static String url = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/postgres");
    private static String user = System.getenv().getOrDefault("DB_USER", "postgres");
    private static String pwd = System.getenv().getOrDefault("DB_PASSWORD", "155795");


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
