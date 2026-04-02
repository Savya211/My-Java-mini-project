
# Project Report: Java Multi-threaded Port Scanner & Network Mapper

**Author:** [Your Name]
**Course:** [Your Course Name]
**Date:** April 2, 2026

---

## 1. Abstract

This report details the design, architecture, and functionality of a "Multi-threaded Port Scanner and Network Mapper," a command-line utility developed entirely in Core Java (Version 8). The application is designed to scan a target IP address or a range of ports to identify open network ports, discover potential services, and map active devices. The primary goal of this project is to demonstrate a strong understanding of fundamental computer science principles, including multi-threading, socket programming, data structures, and Object-Oriented Programming (OOP), within a practical and useful application. The tool is built for performance and efficiency, leveraging a thread pool to conduct scans concurrently.

---

## 2. Features

The application includes the following key features:

*   **Multi-threaded Scanning:** Utilizes a fixed-size thread pool (`ExecutorService`) to scan multiple ports simultaneously, dramatically reducing the time required to scan a large number of ports compared to a sequential approach.
*   **Flexible Scan Targets:** Users can choose between two scanning modes:
    1.  **Default Scan:** Scans the most common 1024 ports on a target IP.
    2.  **Specific Port Scan:** Allows the user to provide a custom, comma-separated list of ports for a more targeted and faster scan.
*   **Service Resolution:** Maps common port numbers to their likely services (e.g., Port 80 to HTTP, Port 22 to SSH), making the output more informative and user-friendly.
*   **Command-Line Interface (CLI):** The entire application is operated through a simple, interactive CLI, ensuring high compatibility and low resource usage.
*   **Error Handling:** Includes basic input validation and error handling to gracefully manage incorrect user inputs (e.g., non-numeric port numbers).
*   **Performance Metrics:** Calculates and displays the total scan time in milliseconds, providing immediate feedback on the efficiency of the scan.
*   **Report Generation:** Offers the user the option to save the final scan results to a text file (`scan_report.txt`) for documentation and later analysis.

---

## 3. Core Computer Science Concepts Implemented

This project is a practical application of several key computer science concepts:

*   **Concurrency and Multi-threading:** The core of the application's performance lies in its use of the `java.util.concurrent` package. An `ExecutorService` manages a pool of worker threads, each executing a `PortScanTask`. This parallel execution is essential for making the port scanner efficient.
*   **TCP/IP Socket Programming:** The project directly uses Java's `java.net.Socket` class to establish TCP connections to target ports. The success or failure of a socket connection (specifically, `socket.connect()`) is the mechanism used to determine if a port is open or closed. A connection timeout is used to prevent threads from hanging on non-responsive ports.
*   **Object-Oriented Programming (OOP):** The application is structured using clear OOP principles:
    *   **Encapsulation:** Functionality is divided into distinct classes (`PortScanner`, `ScannerEngine`, `PortScanTask`, `Port`, `ReportGenerator`), each with a specific responsibility.
    *   **Data Abstraction:** The `Port` class acts as a Plain Old Java Object (POJO) or data model, abstracting the details of a port into a simple object containing its number, status, and service name.
*   **Data Structures:**
    *   **`ConcurrentLinkedQueue`:** This thread-safe queue is used to collect the results (open ports) from all the concurrent threads without causing race conditions or requiring manual synchronization blocks.
    *   **`HashMap`:** A `HashMap` is used for efficient, O(1) average time complexity lookup of common port numbers to their service names.
    *   **`ArrayList`:** An `ArrayList` is used to gather the final results for sorting before they are displayed to the user.
*   **File I/O:** The `ReportGenerator` class uses Java's `java.io.FileWriter` to perform file operations, demonstrating the ability to persist program output to the file system.

---

## 4. Software Architecture

The program is organized into five main classes, designed as static nested classes for portability and ease of compilation.

| Class / Component | Role |
| :--- | :--- |
| **`PortScanner`** | This is the main class that contains the `main` method. It acts as the **CLI Controller**, handling all user input, orchestrating the overall workflow, and displaying the final results to the console. |
| **`ScannerEngine`** | This class is responsible for managing the concurrency. It creates and manages the `ExecutorService` (the thread pool) and submits a `PortScanTask` for each port that needs to be scanned. |
| **`PortScanTask`** | An implementation of the `Runnable` interface. An instance of this class represents a single unit of work: scanning one specific port on the target IP. Its `run()` method contains the core socket connection logic. |
| **`Port`** | A simple data model (POJO) that encapsulates all the information about a single port: its number, whether it's open, and its resolved service name. It implements `Comparable` to allow for sorting. |
| **`ReportGenerator`**| This utility class handles the logic for writing the scan results into the `scan_report.txt` file. |

---

## 5. How to Compile and Run

1.  **Prerequisites:** Java Development Kit (JDK) 8 or higher must be installed and its `bin` directory must be added to the system's PATH environment variable.
2.  **Navigate to Directory:** Open a terminal or command prompt and navigate to the directory containing `PortScanner.java`.
3.  **Compile:** `javac PortScanner.java`
4.  **Run:** `java PortScanner`

---

## 6. Sample Usage and Output

### Scenario 1: Scanning a specific list of ports

The user wants to quickly check for web and SSH servers on a target.

**User Input:**
```
Enter the target IP address to scan: 192.168.1.1
Scan [A]ll common ports up to 65535, or a [S]pecific list? (A/S): S
Enter comma-separated ports to scan (e.g., 22,80,443): 22, 80, 443, 8080
```

**Expected Console Output:**
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

### `scan_report.txt` Content:
```
========================================
      Port Scan Report
========================================
Target IP: 192.168.1.1
Ports Scanned: 4 (custom list)
Scan Duration: 215 ms
Open Ports Found: 2
----------------------------------------

Port 80    is OPEN - Service: HTTP (Hypertext Transfer Protocol)
Port 443   is OPEN - Service: HTTPS (HTTP Secure)

========================================
```

---

## 7. Conclusion

The Java Multi-threaded Port Scanner is a successful demonstration of core Java technologies applied to a real-world networking problem. It effectively balances simplicity in design with high performance through concurrency. The project successfully meets all initial requirements and serves as an excellent educational tool for understanding multi-threading, socket programming, and object-oriented design in Java.
