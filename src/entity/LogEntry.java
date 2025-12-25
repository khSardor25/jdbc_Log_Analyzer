package entity;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

public class LogEntry {
    public String ip;
    public String timestamp;
    public String method;
    public String endpoint;
    public String status;
    public String bytesSent;
    public String userAgent;

    public LogEntry(String ip, String timestamp, String method, String endpoint, String status, String bytesSent, String userAgent) {
        this.ip = ip;
        this.timestamp = timestamp;
        this.method = method;
        this.endpoint = endpoint;
        this.status = status;
        this.bytesSent = bytesSent;
        this.userAgent = userAgent;
    }
}

