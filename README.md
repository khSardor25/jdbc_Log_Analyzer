Log Analyzer (Maven)

Log Analyzer is a console-based Java application designed for system administrators and developers to analyze server logs.
It helps detect patterns such as DDoS attempts, top requested endpoints, and general traffic analytics using structured log processing and database storage.

Project Structure
src/
├── config/
│   └── DatabaseConfig.java
│
├── entity/
│   └── LogEntry.java
│
├── service/
│   ├── AnalyticsService.java
│   ├── DatabaseService.java
│   ├── FileParserService.java
│   └── LogAnalyzerService.java
│
└── RunMain.java

Package Overview
config

DatabaseConfig:
Handles database connection configuration and reads credentials from environment variables.

entity:
LogEntry
Represents a single log record (IP, endpoint, timestamp, status, etc.).

service:
FileParserService
Parses raw log files and converts them into LogEntry objects.

DatabaseService
Manages database connectivity and persistence logic.

AnalyticsService
Performs analytical computations (top endpoints, suspicious IPs, traffic statistics).

LogAnalyzerService
Orchestrates parsing, storing, and analyzing logs.


RunMain
Application entry point and CLI controller.

Features:
Log file parsing,
PostgreSQL integration,
Detection of suspicious traffic patterns,
Top endpoints and request statistics,
Environment-based configuration,
CLI-based execution

Build & Run:
Prerequisites

Java 17+ (or your configured version)

Maven

PostgreSQL

If Maven is not installed:
sudo apt install maven

Build:
mvn clean package

Run:
java -jar target/log-AnalyticsService-1.0-SNAPSHOT.jar


Database Configuration
The application reads database credentials from environment variables.

export DB_URL=jdbc:postgresql://localhost:5432/postgres
export DB_USER=postgres
export DB_PASSWORD=password

No hardcoded credentials are used.


Notes:
This is a console (CLI) application,
Designed with separation of concerns (config / entity / service),
Easily extensible for new analytics modules,
Suitable for educational, experimental, and sysadmin use
