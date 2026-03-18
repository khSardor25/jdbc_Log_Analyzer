package service;

import entity.LogEntry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogAnalyzerService {
    private static final Pattern LOG_PATTERN = Pattern.compile(
            "^(\\S+) \\S+ \\S+ \\[([^]]+)\\] \"(\\w+) (\\S+) HTTP[^\"]*\" (\\d{3}) (\\d+|-) .* \"([^\"]*)\"$"
    );

    public static LogEntry checker(String before_parse) {
        return checker(before_parse, false);
    }

    public static LogEntry checker(String before_parse, boolean silent) {
        Matcher matcher = LOG_PATTERN.matcher(before_parse);
        if (matcher.matches()) {
            if (!silent) {
                System.out.println("==========================================");
                System.out.println("IP:         " + matcher.group(1));
                System.out.println("Date:       " + matcher.group(2));
                System.out.println("Method:     " + matcher.group(3));
                System.out.println("Endpoint:   " + matcher.group(4));
                System.out.println("Status:     " + matcher.group(5));
                System.out.println("Bytes:      " + matcher.group(6));
                System.out.println("User-Agent: " + matcher.group(7));
                System.out.println("==========================================\n");
            }
            return new LogEntry(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5), matcher.group(6), matcher.group(7));
        } else {
            if (!silent) {
                System.out.println("==========================================");
                System.out.println("Error: something went wrong");
                System.out.println("==========================================\n");
                System.out.println("-->" + before_parse);
            }
            return null;
        }
    }
}
