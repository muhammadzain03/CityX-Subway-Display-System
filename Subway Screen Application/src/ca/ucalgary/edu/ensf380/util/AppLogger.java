package ca.ucalgary.edu.ensf380.util;

import java.util.logging.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Centralized logging utility for the Subway Screen Application.
 * Provides structured logging with different levels, file output, and console output.
 * Replaces System.out.println calls with proper logging practices.
 * 
 * Usage:
 *   AppLogger.info("Application started");
 *   AppLogger.error("Database connection failed", exception);
 *   AppLogger.debug("Train position updated");
 * 
 * @author Subway Screen Development Team
 * @version 2.0
 */
public final class AppLogger {
    
    private static final Logger logger = Logger.getLogger("SubwayScreenApp");
    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE = LOG_DIR + "/subway_screen.log";
    private static boolean initialized = false;
    
    // Prevent instantiation
    private AppLogger() {}
    
    /**
     * Initialize the logging system with console and file handlers
     */
    public static synchronized void initialize() {
        if (initialized) return;
        
        try {
            // Create logs directory if it doesn't exist
            Files.createDirectories(Paths.get(LOG_DIR));
            
            // Remove default handlers
            Logger rootLogger = Logger.getLogger("");
            Handler[] handlers = rootLogger.getHandlers();
            for (Handler handler : handlers) {
                rootLogger.removeHandler(handler);
            }
            
            // Set up custom console handler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(AppConstants.DEBUG_MODE ? Level.ALL : Level.INFO);
            consoleHandler.setFormatter(new CustomFormatter(false));
            logger.addHandler(consoleHandler);
            
            // Set up file handler
            FileHandler fileHandler = new FileHandler(LOG_FILE, true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new CustomFormatter(true));
            logger.addHandler(fileHandler);
            
            // Set logger level
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(false);
            
            initialized = true;
            info("Logging system initialized successfully");
            
        } catch (IOException e) {
            System.err.println("Failed to initialize logging system: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Log an INFO level message
     */
    public static void info(String message) {
        ensureInitialized();
        logger.info(message);
    }
    
    /**
     * Log an INFO level message with exception
     */
    public static void info(String message, Throwable throwable) {
        ensureInitialized();
        logger.log(Level.INFO, message, throwable);
    }
    
    /**
     * Log a WARNING level message
     */
    public static void warning(String message) {
        ensureInitialized();
        logger.warning(message);
    }
    
    /**
     * Log a WARNING level message with exception
     */
    public static void warning(String message, Throwable throwable) {
        ensureInitialized();
        logger.log(Level.WARNING, message, throwable);
    }
    
    /**
     * Log an ERROR level message
     */
    public static void error(String message) {
        ensureInitialized();
        logger.severe(message);
    }
    
    /**
     * Log an ERROR level message with exception
     */
    public static void error(String message, Throwable throwable) {
        ensureInitialized();
        logger.log(Level.SEVERE, message, throwable);
    }
    
    /**
     * Log a DEBUG level message (only shown if DEBUG_MODE is enabled)
     */
    public static void debug(String message) {
        ensureInitialized();
        if (AppConstants.DEBUG_MODE) {
            logger.fine(message);
        }
    }
    
    /**
     * Log a DEBUG level message with exception
     */
    public static void debug(String message, Throwable throwable) {
        ensureInitialized();
        if (AppConstants.DEBUG_MODE) {
            logger.log(Level.FINE, message, throwable);
        }
    }
    
    /**
     * Log performance metrics (only if PERFORMANCE_MONITORING is enabled)
     */
    public static void performance(String operation, long durationMs) {
        ensureInitialized();
        if (AppConstants.PERFORMANCE_MONITORING) {
            logger.info(String.format("PERFORMANCE: %s completed in %d ms", operation, durationMs));
        }
    }
    
    /**
     * Log application startup information
     */
    public static void startup(String component, String status) {
        ensureInitialized();
        logger.info(String.format("STARTUP: %s - %s", component, status));
    }
    
    /**
     * Log data operations
     */
    public static void data(String operation, String details) {
        ensureInitialized();
        logger.info(String.format("DATA: %s - %s", operation, details));
    }
    
    /**
     * Log UI events
     */
    public static void ui(String event, String details) {
        ensureInitialized();
        if (AppConstants.DEBUG_MODE) {
            logger.fine(String.format("UI: %s - %s", event, details));
        }
    }
    
    /**
     * Log network operations
     */
    public static void network(String operation, String url, int responseCode) {
        ensureInitialized();
        logger.info(String.format("NETWORK: %s - %s (Status: %d)", operation, url, responseCode));
    }
    
    /**
     * Log network errors
     */
    public static void networkError(String operation, String url, Throwable throwable) {
        ensureInitialized();
        logger.log(Level.WARNING, String.format("NETWORK ERROR: %s - %s", operation, url), throwable);
    }
    
    /**
     * Ensure logging system is initialized before use
     */
    private static void ensureInitialized() {
        if (!initialized) {
            initialize();
        }
    }
    
    /**
     * Custom formatter for log messages
     */
    private static class CustomFormatter extends Formatter {
        private final boolean includeStackTrace;
        private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        
        public CustomFormatter(boolean includeStackTrace) {
            this.includeStackTrace = includeStackTrace;
        }
        
        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();
            
            // Timestamp
            sb.append(LocalDateTime.now().format(timeFormatter));
            sb.append(" ");
            
            // Log level
            sb.append(String.format("%-7s", record.getLevel().toString()));
            sb.append(" ");
            
            // Logger name (shortened)
            String loggerName = record.getLoggerName();
            if (loggerName.length() > 20) {
                loggerName = "..." + loggerName.substring(loggerName.length() - 17);
            }
            sb.append(String.format("%-20s", loggerName));
            sb.append(" - ");
            
            // Message
            sb.append(record.getMessage());
            sb.append(System.lineSeparator());
            
            // Exception details (if present and requested)
            if (record.getThrown() != null && includeStackTrace) {
                sb.append("Exception: ");
                sb.append(record.getThrown().getClass().getSimpleName());
                sb.append(": ");
                sb.append(record.getThrown().getMessage());
                sb.append(System.lineSeparator());
                
                // Add stack trace for file logging
                for (StackTraceElement element : record.getThrown().getStackTrace()) {
                    sb.append("    at ");
                    sb.append(element.toString());
                    sb.append(System.lineSeparator());
                }
            } else if (record.getThrown() != null) {
                // For console, just show exception type and message
                sb.append(" [");
                sb.append(record.getThrown().getClass().getSimpleName());
                sb.append(": ");
                sb.append(record.getThrown().getMessage());
                sb.append("]");
                sb.append(System.lineSeparator());
            }
            
            return sb.toString();
        }
    }
    
    /**
     * Shutdown the logging system gracefully
     */
    public static void shutdown() {
        if (initialized) {
            info("Shutting down logging system");
            Handler[] handlers = logger.getHandlers();
            for (Handler handler : handlers) {
                handler.close();
            }
        }
    }
} 