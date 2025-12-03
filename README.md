## Log Analyzer (Maven)

This project now follows a conventional Maven layout so future GUI or service layers can reuse the same domain logic without guessing how things fit together.

### Modules & Packages

- `com.prototype.app` – CLI entry point (`Application`) that wires services together.
- `com.prototype.config` – centralized database configuration (`DatabaseConfig`) with env overrides (`DB_URL`, `DB_USER`, `DB_PASSWORD`).
- `com.prototype.db` – persistence layer (`DatabaseService`) that owns JDBC code and batch inserts.
- `com.prototype.analytics` – read-only analytics queries exposed through `AnalyticsService`.
- `com.prototype.parser` – parsing helpers (`LogParser`, `LogEntry`) for access logs.
- `com.prototype.ingest` – file/URL ingestion + batching (`FileIngestionService`).

### Build & Run

```bash
mvn clean package
java -jar target/log-analytics-1.0-SNAPSHOT.jar
```

> Maven is not installed on this machine yet (`sudo apt install maven`) so run those commands only after installing it locally.

At runtime you can override DB credentials:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/postgres
export DB_USER=postgres
export DB_PASSWORD=password
```


