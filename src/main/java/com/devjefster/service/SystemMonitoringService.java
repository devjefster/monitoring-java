package com.devjefster.service;

import java.io.File;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SystemMonitoringService {

    private static final long STARTUP_TIME = ManagementFactory.getRuntimeMXBean().getStartTime();
    private static final Logger logger = Logger.getLogger(SystemMonitoringService.class.getName());

    private final OperatingSystemMXBean osBean;
    private final Runtime runtime;
    private final ThreadMXBean threadMXBean;
    private final GarbageCollectorMXBean gcBean;

    public SystemMonitoringService() {
        this.osBean = ManagementFactory.getOperatingSystemMXBean();
        this.runtime = Runtime.getRuntime();
        this.threadMXBean = ManagementFactory.getThreadMXBean();
        this.gcBean = ManagementFactory.getGarbageCollectorMXBeans().get(0);
    }

    public void checkSystemUsage(String serviceName) {
        logStart(serviceName);

        try {
            checkHeapMemoryUsage(serviceName);
            checkCpuUsage(serviceName);
            checkDiskUsage(serviceName);
            checkNetworkUsage(serviceName);
            checkThreadCount(serviceName);
            checkGCStats(serviceName);
            checkLoadAverage(serviceName);
        } catch (Exception exception) {
            logError("Error during system usage check", serviceName, exception);
        }

        logEnd(serviceName);
    }

    private void checkHeapMemoryUsage(String serviceName) {
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        long max = runtime.maxMemory();
        long used = total - free;
        double usagePercentage = (double) used / max * 100;

        logInfo(String.format("Heap Memory - Total: %dMB, Max: %dMB, Free: %dMB, Used: %dMB, Usage: %.2f%%",
                total / 1048576, max / 1048576, free / 1048576, used / 1048576, usagePercentage), serviceName);

        if (usagePercentage > 75) {
            logWarn("Heap memory usage exceeded 75%! Consider optimizing.", serviceName);
        }
    }

    private void checkCpuUsage(String serviceName) {
        double cpuLoad = osBean.getSystemLoadAverage();
        int availableProcessors = runtime.availableProcessors();

        logInfo(String.format("CPU Usage - Load: %.2f, Available Processors: %d", cpuLoad, availableProcessors), serviceName);
    }

    private void checkDiskUsage(String serviceName) {
        File diskRoot = new File("/");
        long totalSpace = diskRoot.getTotalSpace();
        long usableSpace = diskRoot.getUsableSpace();
        double diskUsagePercentage = ((double) (totalSpace - usableSpace) / totalSpace) * 100;

        logInfo(String.format("Disk Usage - Total: %dGB, Free: %dGB, Usage: %.2f%%",
                totalSpace / 1073741824, usableSpace / 1073741824, diskUsagePercentage), serviceName);

        if (diskUsagePercentage > 85) {
            logWarn("High Disk Usage (>85%)! Consider freeing up space.", serviceName);
        }
    }

    private void checkNetworkUsage(String serviceName) {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface net : Collections.list(networkInterfaces)) {
                long bytesRecv = net.getMTU();
                logInfo(String.format("Network Interface: %s - MTU: %d", net.getName(), bytesRecv), serviceName);
            }
        } catch (SocketException e) {
            logError("Error retrieving network interfaces", serviceName, e);
        }
    }

    private void checkThreadCount(String serviceName) {
        int threadCount = threadMXBean.getThreadCount();
        logInfo(String.format("Thread Count - Active Threads: %d", threadCount), serviceName);

        if (threadCount > 1000) {
            logWarn("High thread count detected (>1000)", serviceName);
        }
    }

    private void checkGCStats(String serviceName) {
        long gcCount = gcBean.getCollectionCount();
        long gcTime = gcBean.getCollectionTime();

        logInfo(String.format("GC Stats - Collection Count: %d, Time: %dms", gcCount, gcTime), serviceName);
    }

    private void checkLoadAverage(String serviceName) {
        double load1 = osBean.getSystemLoadAverage();
        logInfo(String.format("Load Average - 1 Minute: %.2f", load1), serviceName);
    }

    public void logStartupTime(String serviceName) {
        Instant startupInstant = Instant.ofEpochMilli(STARTUP_TIME);
        LocalDateTime startupTime = LocalDateTime.ofInstant(startupInstant, ZoneId.systemDefault());
        logInfo(String.format("Application Startup Time: %s", startupTime), serviceName);
    }

    private void logInfo(String message, String serviceName) {
        logger.log(Level.INFO, String.format("APP_NAME: %s: %s", serviceName, message));
    }

    private void logWarn(String message, String serviceName) {
        logger.log(Level.WARNING, String.format("APP_NAME: %s: %s", serviceName, message));
    }

    private void logError(String message, String serviceName, Exception exception) {
        logger.log(Level.SEVERE, String.format("APP_NAME: %s: %s: %s", serviceName, message, exception.getMessage()), exception);
    }

    private void logStart(String serviceName) {
        logger.log(Level.INFO, String.format("🔹 APP_NAME: %s: System usage check started", serviceName));
    }

    private void logEnd(String serviceName) {
        logger.log(Level.INFO, String.format("✅ APP_NAME: %s: System usage check completed", serviceName));
    }
}
