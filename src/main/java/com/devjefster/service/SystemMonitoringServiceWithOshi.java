package com.devjefster.service;


import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SystemMonitoringServiceWithOshi {
    private static final Logger logger = Logger.getLogger(SystemMonitoringServiceWithOshi.class.getName());
    private final HardwareAbstractionLayer hardware;
    private final CentralProcessor processor;
    private final GlobalMemory memory;
    private final long[] previousTicks;

    public SystemMonitoringServiceWithOshi() {
        SystemInfo systemInfo = new SystemInfo();
        this.hardware = systemInfo.getHardware();
        this.processor = hardware.getProcessor();
        this.memory = hardware.getMemory();
        previousTicks = processor.getSystemCpuLoadTicks();

    }

    public void checkSystemUsage(String serviceName) {
        logInfo("🔹 Starting system monitoring...", serviceName);
        try {
            checkCpuUsage(serviceName);
            checkMemoryUsage(serviceName);
            checkDiskUsage(serviceName);
            checkNetworkUsage(serviceName);
        } catch (Exception e) {
            logError(serviceName, e);
        }
        logInfo("✅ Monitoring completed", serviceName);
    }

    private void checkCpuUsage(String serviceName) {
        double cpuLoad = processor.getSystemCpuLoadBetweenTicks(previousTicks) * 100;
        logInfo(String.format("CPU Usage: %.2f%%", cpuLoad), serviceName);
    }

    private void checkMemoryUsage(String serviceName) {
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        long usedMemory = totalMemory - availableMemory;
        double usagePercentage = (double) usedMemory / totalMemory * 100;
        logInfo(String.format("Memory Usage: %.2f%% (Used: %dMB / Total: %dMB)",
                usagePercentage, usedMemory / 1048576, totalMemory / 1048576), serviceName);
    }

    private void checkDiskUsage(String serviceName) {
        File root = new File("/");
        long totalSpace = root.getTotalSpace();
        long freeSpace = root.getFreeSpace();
        logInfo(String.format("Disk Usage: %dGB free of %dGB",
                freeSpace / 1073741824, totalSpace / 1073741824), serviceName);
    }

    private void checkNetworkUsage(String serviceName) {
        List<NetworkIF> networkInterfaces = hardware.getNetworkIFs();
        for (NetworkIF net : networkInterfaces) {
            logInfo(String.format("Network Interface: %s - Received: %dMB, Sent: %dMB",
                    net.getName(), net.getBytesRecv() / 1048576, net.getBytesSent() / 1048576), serviceName);
        }
    }

    private void logInfo(String message, String serviceName) {
        logger.log(Level.INFO, "[{0}] {1}", new Object[]{serviceName, message});
    }

    private void logError(String serviceName, Exception e) {
        logger.log(Level.SEVERE, "[{0}] {1} - {2}", new Object[]{serviceName, "Error while monitoring system metrics", e.getMessage()});
    }
}