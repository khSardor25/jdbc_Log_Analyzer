package entity;

public class LogEntry {
    private String ip;
    private String timestamp;
    private String method;
    private String endpoint;
    private String status;
    private String bytesSent;
    private String userAgent;

    public LogEntry(String ip, String timestamp, String method, String endpoint, String status, String bytesSent, String userAgent) {
        this.ip = ip;
        this.timestamp = timestamp;
        this.method = method;
        this.endpoint = endpoint;
        this.status = status;
        this.bytesSent = bytesSent;
        this.userAgent = userAgent;
    }

    public String getIp() { return ip; }
    public String getTimestamp() { return timestamp; }
    public String getMethod() { return method; }
    public String getEndpoint() { return endpoint; }
    public String getStatus() { return status; }
    public String getBytesSent() { return bytesSent; }
    public String getUserAgent() { return userAgent; }
}
