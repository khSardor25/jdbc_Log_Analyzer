📊 Log Analyzer (Java + Maven)

Log Analyzer is a console-based Java application that helps developers and system administrators analyze server logs.
It parses log files, stores structured data in PostgreSQL, and performs analytics to detect traffic patterns, suspicious activity, and popular endpoints.

🚀 Features

📄 Log Parsing – Converts raw log files into structured objects

🐘 PostgreSQL Integration – Stores logs using JDBC

📊 Traffic Analytics – Finds top requested endpoints

🚨 Suspicious Activity Detection – Identifies abnormal IP request patterns

⚙️ Environment Configuration – Uses environment variables for DB credentials

🧩 Modular Architecture – Clean separation of responsibilities

💻 CLI Application – Lightweight console-based execution

🏗 Project Structure
src/
├── config/
│   └── DatabaseConfig.java
├── entity/
│   └── LogEntry.java
├── service/
│   ├── AnalyticsService.java
│   ├── DatabaseService.java
│   ├── FileParserService.java
│   └── LogAnalyzerService.java
└── RunMain.java
📦 Components

DatabaseConfig – Manages PostgreSQL connection via environment variables
LogEntry – Represents a parsed log record (IP, endpoint, timestamp, status)
FileParserService – Parses raw log files into structured objects
DatabaseService – Handles database storage and queries
AnalyticsService – Performs traffic analytics and anomaly detection
LogAnalyzerService – Coordinates parsing, storage, and analytics
RunMain – Application entry point

🛠 Build & Run
Requirements

Java 17+

Maven

PostgreSQL

Build
mvn clean package
Run
java -jar target/log-AnalyticsService-1.0-SNAPSHOT.jar
🗄 Database Configuration

The application reads credentials from environment variables:

export DB_URL=jdbc:postgresql://localhost:5432/postgres
export DB_USER=postgres
export DB_PASSWORD=password

No credentials are hardcoded in the source code.

📌 Notes

Console-based Java application

Designed with separation of concerns

Easily extensible with new analytics modules

Suitable for learning backend development and log analytics
