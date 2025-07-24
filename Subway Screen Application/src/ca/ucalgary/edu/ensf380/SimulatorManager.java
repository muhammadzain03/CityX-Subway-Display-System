/**
 * Simulator Process Manager for Subway Screen Application
 * 
 * Manages the lifecycle of the external SubwaySimulator.jar process that generates
 * real-time train position data. Runs in headless mode without displaying a GUI,
 * providing only the essential process management functionality.
 * 
 * This component is essential for the application as it:
 * - Starts and manages the SubwaySimulator.jar subprocess
 * - Provides the 'running' state that controls the main application loop
 * - Monitors process health and status
 * 
 * @author Subway Screen Development Team
 * @version 2.0
 */
package ca.ucalgary.edu.ensf380;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ca.ucalgary.edu.ensf380.util.AppConstants;
import ca.ucalgary.edu.ensf380.util.AppLogger;

public class SimulatorManager {
    private Process simulatorProcess;
    private ExecutorService executor;
    public volatile boolean running = true;

    public SimulatorManager() {
        AppLogger.startup("SimulatorManager", "Initializing headless simulator manager");
        setupExecutor();
        startSimulatorProcess();
    }
    
    /**
     * Setup thread executor for process management
     */
    private void setupExecutor() {
        executor = Executors.newFixedThreadPool(2, r -> {
            Thread t = new Thread(r, "SimulatorProcess-" + System.currentTimeMillis());
            t.setDaemon(true);
            return t;
        });
        AppLogger.debug("Simulator process executor initialized");
    }

    /**
     * Start the subway simulator process with improved error handling
     */
    private void startSimulatorProcess() {
        if (simulatorProcess != null && simulatorProcess.isAlive()) {
            AppLogger.warning("Simulator process is already running");
            return;
        }
        
        try {
            AppLogger.info("Starting subway simulator process");
            
            // Build process command using constants
            ProcessBuilder builder = new ProcessBuilder(
                "java", "-jar", 
                AppConstants.SIMULATOR_JAR, 
                "--in", AppConstants.SUBWAY_DATA_FILE, 
                "--out", AppConstants.OUTPUT_PATH
            );
            
            builder.redirectErrorStream(true);
            simulatorProcess = builder.start();
            
            // Monitor process output (but don't display it)
            executor.execute(this::monitorProcessOutput);
            
            // Monitor process lifecycle
            executor.execute(this::monitorProcessLifecycle);
            
            AppLogger.info("Subway simulator process started successfully");
            
        } catch (IOException e) {
            AppLogger.error("Failed to start simulator process", e);
            running = false;
        }
    }
    
    /**
     * Monitor process output silently (no GUI display)
     */
    private void monitorProcessOutput() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(simulatorProcess.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null && running) {
                // Log only important messages, not every line
                if (line.contains("ERROR") || line.contains("Exception")) {
                    AppLogger.warning("Simulator error: " + line);
                } else {
                    AppLogger.debug("Simulator output: " + line);
                }
            }
        } catch (IOException e) {
            if (running) {
                AppLogger.error("Error reading simulator output", e);
            }
        }
    }
    
    /**
     * Monitor process lifecycle and handle termination
     */
    private void monitorProcessLifecycle() {
        try {
            int exitCode = simulatorProcess.waitFor();
            
            if (exitCode == 0) {
                AppLogger.info("Simulator process completed with exit code: " + exitCode);
            } else {
                AppLogger.warning("Simulator process exited with code: " + exitCode);
            }
            
            simulatorProcess = null;
            running = false;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            AppLogger.warning("Simulator process monitoring interrupted");
        }
    }

    /**
     * Stop the simulator process gracefully
     */
    public void stopSimulatorProcess() {
        if (simulatorProcess == null || !simulatorProcess.isAlive()) {
            AppLogger.debug("No simulator process to stop");
            return;
        }
        
        AppLogger.info("Stopping simulator process");
        
        try {
            // Graceful shutdown
            simulatorProcess.destroy();
            
            // Wait for graceful shutdown, then force if necessary
            if (!simulatorProcess.waitFor(5, java.util.concurrent.TimeUnit.SECONDS)) {
                AppLogger.warning("Forcing simulator process termination");
                simulatorProcess.destroyForcibly();
            }
            
            simulatorProcess = null;
            running = false;
            
            AppLogger.info("Simulator process stopped successfully");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            AppLogger.error("Error stopping simulator process", e);
        }
    }
    
    /**
     * Handle application shutdown gracefully
     */
    public void shutdown() {
        AppLogger.info("Simulator manager shutdown requested");
        
        running = false;
        stopSimulatorProcess();
        
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(3, java.util.concurrent.TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        AppLogger.info("Simulator manager shutdown completed");
    }
    
    /**
     * Get current running state
     */
    public boolean isRunning() {
        return running && (simulatorProcess == null || simulatorProcess.isAlive());
    }
    
    /**
     * Get simulator process status
     */
    public boolean isSimulatorActive() {
        return simulatorProcess != null && simulatorProcess.isAlive();
    }

    /**
     * Factory method to create and start simulator manager
     */
    public static SimulatorManager create() {
        AppLogger.debug("Creating headless simulator manager instance");
        return new SimulatorManager();
    }
} 