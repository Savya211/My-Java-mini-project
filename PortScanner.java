import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A multi-threaded port scanner written in pure Java 8.
 * This application scans a range of ports on a target IP address to identify open ports
 * and potential services running on them.
 *
 * This file contains all necessary classes as static nested classes for easy compilation and execution.
 */
public class PortScanner {

    // A thread-safe queue to store the results from multiple threads.
    private static final ConcurrentLinkedQueue<Port> openPorts = new ConcurrentLinkedQueue<>();
    private static final Map<Integer, String> COMMON_PORTS = new HashMap<>();

    static {
        // A map of common ports to their likely services for quick identification.
        COMMON_PORTS.put(20, "FTP (File Transfer Protocol)");
        COMMON_PORTS.put(21, "FTP (File Transfer Protocol)");
        COMMON_PORTS.put(22, "SSH (Secure Shell)");
        COMMON_PORTS.put(23, "Telnet");
        COMMON_PORTS.put(25, "SMTP (Simple Mail Transfer Protocol)");
        COMMON_PORTS.put(53, "DNS (Domain Name System)");
        COMMON_PORTS.put(80, "HTTP (Hypertext Transfer Protocol)");
        COMMON_PORTS.put(110, "POP3 (Post Office Protocol v3)");
        COMMON_PORTS.put(143, "IMAP (Internet Message Access Protocol)");
        COMMON_PORTS.put(443, "HTTPS (HTTP Secure)");
        COMMON_PORTS.put(465, "SMTPS (SMTP Secure)");
        COMMON_PORTS.put(993, "IMAPS (IMAP Secure)");
        COMMON_PORTS.put(995, "POP3S (POP3 Secure)");
        COMMON_PORTS.put(3306, "MySQL Database");
        COMMON_PORTS.put(5432, "PostgreSQL Database");
        COMMON_PORTS.put(8080, "HTTP Proxy / Alternative HTTP");
    }

    /**
     * Main class / CLIController: Handles user input, orchestrates the scan, and displays results.
     */
    public static void main(String[] args) {
        // 1. Welcome Screen
        drawBanner();

        // 2. User Inputs
        Scanner inputScanner = new Scanner(System.in);
        System.out.print("Enter the target IP address to scan: ");
        String targetIp = inputScanner.nextLine();

        System.out.print("Scan [A]ll common ports up to 65535, or a [S]pecific list? (A/S): ");
        String scanType = inputScanner.nextLine();

        List<Integer> portsToScan = new ArrayList<>();

        if (scanType.equalsIgnoreCase("S")) {
            System.out.print("Enter comma-separated ports to scan (e.g., 22,80,443): ");
            String portsLine = inputScanner.nextLine();
            // Basic validation and parsing
            try {
                String[] portStrings = portsLine.split(",");
                for (String portStr : portStrings) {
                    int port = Integer.parseInt(portStr.trim());
                    if (port > 0 && port <= 65535) {
                        portsToScan.add(port);
                    } else {
                        System.out.println("Warning: Port " + port + " is invalid, skipping.");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input format. Please use numbers separated by commas.");
                inputScanner.close();
                return; // Exit if input is bad
            }
        } else {
            // Default to scanning all ports up to 1024 if user enters 'A' or anything else
            for (int i = 1; i <= 1024; i++) {
                portsToScan.add(i);
            }
             System.out.println("Scanning all ports up to 1024...");
        }


        System.out.println("\nScanning " + targetIp + " for " + portsToScan.size() + " specified ports...");
        long startTime = System.currentTimeMillis();

        // 3. Concurrency Management
        ScannerEngine.startScan(targetIp, portsToScan);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 4. Summary & Export
        System.out.println("\nScan finished in " + duration + " ms.");
        if (openPorts.isEmpty()) {
            System.out.println("No open ports found from the specified list.");
        } else {
            System.out.println("Total open ports found: " + openPorts.size());
            System.out.println("------------------------------------");
            // Sort ports for clean, ordered output
            List<Port> sortedPorts = new ArrayList<>(openPorts);
            Collections.sort(sortedPorts);
            for (Port port : sortedPorts) {
                System.out.println(port);
            }
            System.out.println("------------------------------------");

            System.out.print("\nWould you like to save this report to a file? (Y/N): ");
            String saveChoice = inputScanner.next();
            if (saveChoice.equalsIgnoreCase("Y")) {
                ReportGenerator.saveReport(targetIp, portsToScan.size(), duration, sortedPorts);
            }
        }

        System.out.println("\nThank you for using the Port Scanner!");
        inputScanner.close();
    }

    /**
     * Helper method to resolve a port number to a common service name.
     * @param portNumber The port number.
     * @return The service name, or "Unknown Service".
     */
    private static String resolveService(int portNumber) {
        return COMMON_PORTS.getOrDefault(portNumber, "Unknown Service");
    }

    /**
     * Displays a simple ASCII art banner for the tool.
     */
    private static void drawBanner() {
        System.out.println("******************************************************");
        System.out.println("*                                                    *");
        System.out.println("*      Java Multi-Threaded Port Scanner & Mapper     *");
        System.out.println("*                                                    *");
        System.out.println("******************************************************");
        System.out.println();
    }

    /**
     * ScannerEngine: Manages the concurrency using a fixed-size thread pool.
     */
    static class ScannerEngine {
        private static final int THREAD_POOL_SIZE = 100; // Number of concurrent threads

        public static void startScan(String targetIp, List<Integer> portsToScan) {
            // Creates a thread pool that reuses a fixed number of threads.
            // This is efficient because it avoids the overhead of creating a new thread for each task.
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

            // Submit a task for each port in the provided list.
            for (int port : portsToScan) {
                // The execute() method submits a new Runnable task to the thread pool.
                // If all threads are busy, the task is placed in a queue to wait.
                executor.execute(new PortScanTask(targetIp, port));
            }

            // It's crucial to shut down the executor service.
            // shutdown() initiates a graceful shutdown: no new tasks are accepted,
            // but previously submitted tasks are allowed to complete.
            executor.shutdown();

            try {
                // This line blocks until all tasks have completed execution after a shutdown request,
                // or the timeout occurs. This ensures the main thread waits for the scanning to finish.
                // We'll wait up to 20 minutes, which should be more than enough for most scans.
                if (!executor.awaitTermination(20, TimeUnit.MINUTES)) {
                    System.err.println("Warning: Scan timed out. Not all ports may have been scanned.");
                    // Forcibly shut down if tasks are stuck
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                // This exception is thrown if the current thread is interrupted while waiting.
                System.err.println("The scan was interrupted.");
                // (Re-)Cancel if current thread also interrupted
                executor.shutdownNow();
                // Preserve interrupt status
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * PortScanTask: Implements Runnable and contains the logic to scan a single port.
     */
    static class PortScanTask implements Runnable {
        private final String ip;
        private final int port;
        private static final int TIMEOUT_MS = 200; // Connection timeout in milliseconds

        public PortScanTask(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {
            // The core logic of the port scanner.
            // We use a try-with-resources statement to ensure the socket is closed automatically.
            try (Socket socket = new Socket()) {
                // Attempt to connect to the target IP and port with a specified timeout.
                // A timeout is critical to prevent threads from hanging indefinitely on closed or filtered ports.
                socket.connect(new InetSocketAddress(ip, port), TIMEOUT_MS);
                
                // If the connection is successful, the port is open.
                // We create a new Port object and add it to our thread-safe collection.
                Port openPort = new Port(port, true, resolveService(port));
                openPorts.add(openPort);

            } catch (IOException e) {
                // An IOException (like ConnectException or SocketTimeoutException) indicates
                // that the port is likely closed, filtered, or the host is unreachable.
                // In this case, we do nothing and the thread completes its task for this port.
            }
        }
    }

    /**
     * Port: A simple model/POJO class to hold information about a scanned port.
     * Implements Comparable to allow sorting by port number.
     */
    static class Port implements Comparable<Port> {
        private final int portNumber;
        private final boolean isOpen;
        private final String serviceName;

        public Port(int portNumber, boolean isOpen, String serviceName) {
            this.portNumber = portNumber;
            this.isOpen = isOpen;
            this.serviceName = serviceName;
        }

        public int getPortNumber() {
            return portNumber;
        }

        @Override
        public String toString() {
            return String.format("Port %-5d is OPEN - Service: %s", portNumber, serviceName);
        }

        @Override
        public int compareTo(Port other) {
            return Integer.compare(this.portNumber, other.portNumber);
        }
    }

    /**
     * ReportGenerator: Handles writing the scan results to a text file.
     */
    static class ReportGenerator {
        private static final String FILENAME = "scan_report.txt";

        public static void saveReport(String targetIp, int portsScannedCount, long duration, List<Port> finalResults) {
            // Uses a try-with-resources statement to ensure the FileWriter is closed automatically.
            try (FileWriter writer = new FileWriter(FILENAME)) {
                System.out.println("Saving report to " + FILENAME + "...");

                writer.write("========================================\n");
                writer.write("      Port Scan Report\n");
                writer.write("========================================\n");
                writer.write("Target IP: " + targetIp + "\n");
                writer.write("Ports Scanned: " + portsScannedCount + " (custom list)\n");
                writer.write("Scan Duration: " + duration + " ms\n");
                writer.write("Open Ports Found: " + finalResults.size() + "\n");
                writer.write("----------------------------------------\n\n");

                for (Port port : finalResults) {
                    writer.write(port.toString() + "\n");
                }

                writer.write("\n========================================\n");
                System.out.println("Report saved successfully.");

            } catch (IOException e) {
                System.err.println("Error: Could not write report to file. " + e.getMessage());
            }
        }
    }
}
