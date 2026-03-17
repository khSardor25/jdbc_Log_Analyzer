# 📊 Log Analyzer (Java)

Log Analyzer is a professional console-based Java application designed for high-performance server log analysis. It parses standard raw log files, stores them in a structured PostgreSQL database, and provides insightful analytics including traffic patterns, endpoint popularity, and error rate detection.

## 🚀 Features

-   **📄 Efficient Log Parsing** – Converts complex raw log entries into structured Java objects using robust Regex patterns.
-   **🐘 PostgreSQL Integration** – High-performance storage using JDBC with batch processing for bulk imports.
-   **📊 Advanced Analytics**:
    -   **Top 10 Active IPs**: Identify the most frequent visitors.
    -   **Top 10 Popular Endpoints**: Discover the most requested resources.
    -   **Error Rate Analysis**: Monitor Internal Server Errors (5XX) and their percentage of total traffic.
    -   **Hourly Traffic Trends**: Analyze request volume over time using PostgreSQL's time-series functions.
-   **⚙️ Flexible Configuration** – Seamlessly switch environments using environment variables for database credentials.
-   **🛡️ Performance & Reliability** – Thread-safe database interactions and optimized batch inserts for large log files.
-   **💻 User-Friendly CLI** – Interactive command-line interface for ease of use.

## 📋 Prerequisites

Before you start, ensure you have the following installed:

-   **Java Development Kit (JDK) 17 or higher**
-   **PostgreSQL Database Server**
-   **PostgreSQL JDBC Driver** (included in the `lib/` directory)

---

## 🛠 Setup & Configuration

### 1. Database Setup

You need a running PostgreSQL instance. Create a table named `logs` using the following SQL schema:

```sql
CREATE TABLE logs (
    id SERIAL PRIMARY KEY,
    ip VARCHAR(45),
    timestamp TIMESTAMP,
    method VARCHAR(10),
    endpoint TEXT,
    status INT,
    bytes_sent BIGINT,
    user_agent TEXT
);
```

### 2. Environment Variables

The application is designed for security and flexibility. Configure your database connection by setting the following environment variables:

| Variable | Description | Default Value |
| :--- | :--- | :--- |
| `DB_URL` | The JDBC connection string | `jdbc:postgresql://localhost:5432/postgres` |
| `DB_USER` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | `155795` |

**Example (Linux/macOS):**
```bash
export DB_URL=jdbc:postgresql://your_host:5432/your_db
export DB_USER=your_username
export DB_PASSWORD=your_password
```

**Example (Windows PowerShell):**
```powershell
$env:DB_URL="jdbc:postgresql://your_host:5432/your_db"
$env:DB_USER="your_username"
$env:DB_PASSWORD="your_password"
```

---

## 🏗 Build & Run

### Manual Compilation

Since this project uses manual dependency management, compile it from the root directory:

```bash
# Create an output directory
mkdir -p out

# Compile the source code
javac -cp "lib/*:src" src/RunMain.java src/config/*.java src/entity/*.java src/service/*.java -d out
```

### Running the Application

Once compiled, you can launch the application:

```bash
java -cp "lib/*:out" RunMain
```

---

## 📂 Project Structure

-   `src/RunMain.java` – Entry point of the application.
-   `src/config/DatabaseConfig.java` – Centralized configuration management.
-   `src/entity/LogEntry.java` – Data model for log records.
-   `src/service/`
    -   `DatabaseService.java` – Handles JDBC connections and push operations.
    -   `FileParserService.java` – High-speed file parsing and batch database insertion.
    -   `LogAnalyzerService.java` – Regex-based parsing logic.
    -   `AnalyticsService.java` – SQL-driven data analysis and reporting.
-   `lib/` – Contains required external libraries (PostgreSQL Driver).

---

## 📌 Usage Guide

1.  **Check Connection**: Test your database connectivity before starting.
2.  **Parse String**: Manually paste a single log line to verify parsing logic.
3.  **Parse File**: Provide a path to a log file for bulk import. The application uses batch processing for maximum speed.
4.  **See Analytics**: View generated reports on your log data.

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request or open an issue for any bugs or feature requests.

---

*This project was refactored for improved performance, removal of code duplication, and better maintainability.*
