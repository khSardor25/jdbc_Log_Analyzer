package service;

import entity.LogEntry;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class FileParserService {
    private static final int BATCH_SIZE = 1000;
    private static final RawLine POISON_LINE = new RawLine(null, true);
    private static final ParseResult POISON_RESULT = new ParseResult(null, true, false);

    public static void parseFileFast(String filePath) throws SQLException {
        System.out.println("Parsing file: " + filePath);
        long start = System.currentTimeMillis();
        Path path = Paths.get(filePath);
        int parserThreads = Math.max(2, Runtime.getRuntime().availableProcessors());

        BlockingQueue<RawLine> lineQueue = new LinkedBlockingQueue<>();
        BlockingQueue<ParseResult> resultQueue = new LinkedBlockingQueue<>();
        AtomicReference<Throwable> failure = new AtomicReference<>();
        ImportStats stats = new ImportStats();

        Thread readerThread = new Thread(() -> readFile(path, lineQueue, parserThreads, failure), "log-reader");
        List<Thread> parserWorkers = createParserWorkers(parserThreads, lineQueue, resultQueue, failure);
        Thread writerThread = new Thread(() -> writeToDatabase(resultQueue, parserThreads, stats, failure), "db-writer");

        readerThread.start();
        for (Thread worker : parserWorkers) {
            worker.start();
        }
        writerThread.start();

        try {
            readerThread.join();
            for (Thread worker : parserWorkers) {
                worker.join();
            }
            writerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Import interrupted", e);
        }

        Throwable thrown = failure.get();
        if (thrown != null) {
            if (thrown instanceof SQLException sqlException) {
                throw sqlException;
            }
            throw new SQLException("Failed to import log file", thrown);
        }

        long totalMillis = System.currentTimeMillis() - start;

        System.out.println("==========================================");
        System.out.println("Used " + parserThreads + " parser threads");
        System.out.println("Inserted " + stats.inserted + " entries in: " + (totalMillis / 1000.0) + " secs");
        System.out.println("Skipped " + stats.skipped + " invalid entries");
        System.out.println("==========================================\n");
    }

    private static List<Thread> createParserWorkers(
            int parserThreads,
            BlockingQueue<RawLine> lineQueue,
            BlockingQueue<ParseResult> resultQueue,
            AtomicReference<Throwable> failure
    ) {
        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < parserThreads; i++) {
            Thread worker = new Thread(() -> parseLines(lineQueue, resultQueue, failure), "parser-" + i);
            workers.add(worker);
        }
        return workers;
    }

    private static void readFile(
            Path path,
            BlockingQueue<RawLine> lineQueue,
            int parserThreads,
            AtomicReference<Throwable> failure
    ) {
        try (Stream<String> lines = Files.lines(path)) {
            for (String line : (Iterable<String>) lines::iterator) {
                if (failure.get() != null) {
                    break;
                }
                lineQueue.put(new RawLine(line, false));
            }
        } catch (Exception e) {
            failure.compareAndSet(null, e);
        } finally {
            for (int i = 0; i < parserThreads; i++) {
                putQuietly(lineQueue, POISON_LINE, failure);
            }
        }
    }

    private static void parseLines(
            BlockingQueue<RawLine> lineQueue,
            BlockingQueue<ParseResult> resultQueue,
            AtomicReference<Throwable> failure
    ) {
        try {
            while (failure.get() == null) {
                RawLine rawLine = lineQueue.take();
                if (rawLine.poison) {
                    break;
                }

                LogEntry entry = LogAnalyzerService.checker(rawLine.value, true);
                if (entry == null) {
                    resultQueue.put(new ParseResult(null, false, true));
                } else {
                    resultQueue.put(new ParseResult(entry, false, false));
                }
            }
        } catch (Exception e) {
            failure.compareAndSet(null, e);
        } finally {
            putQuietly(resultQueue, POISON_RESULT, failure);
        }
    }

    private static void writeToDatabase(
            BlockingQueue<ParseResult> resultQueue,
            int parserThreads,
            ImportStats stats,
            AtomicReference<Throwable> failure
    ) {
        try (Connection conn = DatabaseService.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(DatabaseService.INSERT_SQL)) {
                int finishedWorkers = 0;

                while (finishedWorkers < parserThreads) {
                    ParseResult result = resultQueue.take();

                    if (result.poison) {
                        finishedWorkers++;
                        continue;
                    }

                    if (result.invalid) {
                        stats.skipped++;
                        continue;
                    }

                    if (DatabaseService.bindLog(ps, result.entry)) {
                        ps.addBatch();
                        stats.inserted++;
                        if (stats.inserted % BATCH_SIZE == 0) {
                            ps.executeBatch();
                        }
                    } else {
                        stats.skipped++;
                    }
                }

                ps.executeBatch();
                conn.commit();
            } catch (Exception e) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                    // best-effort rollback
                }
                throw e;
            }
        } catch (Exception e) {
            failure.compareAndSet(null, e);
        }
    }

    private static void putQuietly(
            BlockingQueue<?> queue,
            Object value,
            AtomicReference<Throwable> failure
    ) {
        try {
            @SuppressWarnings("unchecked")
            BlockingQueue<Object> typedQueue = (BlockingQueue<Object>) queue;
            typedQueue.put(value);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            failure.compareAndSet(null, e);
        }
    }

    private static class RawLine {
        private final String value;
        private final boolean poison;

        private RawLine(String value, boolean poison) {
            this.value = value;
            this.poison = poison;
        }
    }

    private static class ParseResult {
        private final LogEntry entry;
        private final boolean poison;
        private final boolean invalid;

        private ParseResult(LogEntry entry, boolean poison, boolean invalid) {
            this.entry = entry;
            this.poison = poison;
            this.invalid = invalid;
        }
    }

    private static class ImportStats {
        private int inserted;
        private int skipped;
    }
}
