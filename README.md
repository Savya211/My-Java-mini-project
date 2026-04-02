# 🔍 Java Multi-Threaded Port Scanner & Network Mapper

A high-performance, multi-threaded port scanner built entirely in **Core Java 8**. This command-line tool scans a target IP address across a range of ports to identify open network ports, discover potential services, and generate detailed scan reports.

> Built as a mini project for college submission, demonstrating practical mastery of multi-threading, socket programming, data structures, and OOP in Java.

---

## ✨ Features

| Feature | Description |
|---|---|
| ⚡ **Multi-threaded Scanning** | Uses a fixed-size thread pool (`ExecutorService`) with 100 concurrent threads for blazing-fast parallel port scanning |
| 🎯 **Flexible Scan Modes** | Scan all common ports (1–1024) or provide a custom comma-separated list for targeted scans |
| 🏷️ **Service Resolution** | Automatically maps open ports to well-known services (HTTP, SSH, HTTPS, MySQL, etc.) |
| 📊 **Performance Metrics** | Displays total scan duration in milliseconds |
| 📄 **Report Generation** | Option to export results to `scan_report.txt` for documentation |
| 🛡️ **Error Handling** | Graceful input validation and timeout management for non-responsive ports |

---

## 🏗️ Architecture

The application is structured using **five classes**, organized as static nested classes for easy single-file compilation:

```
PortScanner (Main / CLI Controller)
├── ScannerEngine        → Manages the thread pool & concurrency
├── PortScanTask         → Runnable task for scanning a single port
├── Port                 → Data model (POJO) for port info
└── ReportGenerator      → Writes scan results to a file
```

### Key CS Concepts Used
- **Concurrency** — `ExecutorService` with a fixed thread pool of 100 workers
- **TCP Socket Programming** — `java.net.Socket` + connection timeout for port probing
- **Thread-Safe Collections** — `ConcurrentLinkedQueue` for lock-free result aggregation
- **OOP & Encapsulation** — Clean separation of concerns across five classes
- **File I/O** — `FileWriter` for persisting scan reports

---

## 🚀 Getting Started

### Prerequisites
- **Java Development Kit (JDK) 8** or higher
- JDK `bin` directory added to your system `PATH`

### Compile & Run

```bash
# Compile
javac PortScanner.java

# Run
java PortScanner
```

---

## 📖 Usage

### Option 1 — Scan All Common Ports (1–1024)

```
Enter the target IP address to scan: 192.168.1.1
Scan [A]ll common ports up to 65535, or a [S]pecific list? (A/S): A
```

### Option 2 — Scan Specific Ports

```
Enter the target IP address to scan: 192.168.1.1
Scan [A]ll common ports up to 65535, or a [S]pecific list? (A/S): S
Enter comma-separated ports to scan (e.g., 22,80,443): 22, 80, 443, 8080
```

### Sample Output

```
Scanning 192.168.1.1 for 4 specified ports...

Scan finished in 215 ms.
Total open ports found: 2
------------------------------------
Port 80    is OPEN - Service: HTTP (Hypertext Transfer Protocol)
Port 443   is OPEN - Service: HTTPS (HTTP Secure)
------------------------------------

Would you like to save this report to a file? (Y/N): y
Saving report to scan_report.txt...
Report saved successfully.
```

---

## 📂 Project Structure

```
.
├── PortScanner.java            # Main source code (all classes)
├── CaseStudy_PortScanner.pdf   # Case study document
├── ProjectReport.md            # Detailed project report
├── scan_report.txt             # Sample generated scan report
└── README.md                   # You are here
```

---

## ⚙️ Recognized Services

The scanner identifies the following common ports out of the box:

| Port | Service |
|------|---------|
| 20/21 | FTP |
| 22 | SSH |
| 23 | Telnet |
| 25 | SMTP |
| 53 | DNS |
| 80 | HTTP |
| 110 | POP3 |
| 143 | IMAP |
| 443 | HTTPS |
| 465 | SMTPS |
| 993 | IMAPS |
| 995 | POP3S |
| 3306 | MySQL |
| 5432 | PostgreSQL |
| 8080 | HTTP Proxy |

---

## 📝 License

This project is intended for **educational purposes** as part of a college mini project submission.

---

## 👤 Author

**Savya211**
