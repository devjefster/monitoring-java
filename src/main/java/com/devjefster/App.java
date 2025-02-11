package com.devjefster;

import com.devjefster.service.SystemMonitoringService;
import com.devjefster.service.SystemMonitoringServiceWithOshi;

import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        SystemMonitoringService systemMonitoringService = new SystemMonitoringService();
        systemMonitoringService.logStartupTime("MonitoringApp");
        SystemMonitoringServiceWithOshi monitoringService = new SystemMonitoringServiceWithOshi();


        logger.log(Level.INFO, "🔹 Starting System Monitoring...");
        // Log startup time
        monitoringService.checkSystemUsage("MonitoringApp with Oshi");
        systemMonitoringService.checkSystemUsage("MonitoringApp");

        // Periodically check system usage
        while (true) {
            monitoringService.checkSystemUsage("MonitoringApp with Oshi");
            systemMonitoringService.checkSystemUsage("MonitoringApp");
            try {
                Thread.sleep(5000); // Check every 5 seconds (adjust as needed)
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, String.format("Monitoring interrupted: %s", e.getMessage()));
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
