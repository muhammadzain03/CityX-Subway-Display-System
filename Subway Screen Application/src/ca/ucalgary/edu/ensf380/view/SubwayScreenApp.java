/**
 * Main Application Entry Point for Subway Screen Application
 * 
 * This is the primary entry point that orchestrates the entire subway screen system.
 * It initializes the UI theme, validates command-line arguments, starts the simulator
 * manager, and runs the main application loop that continuously updates train positions
 * and plays audio announcements.
 * 
 * Key responsibilities:
 * - Application startup and initialization
 * - Command-line argument validation
 * - Modern UI theme setup with FlatLaf
 * - Main event loop coordination
 * - Audio announcement management
 * - Graceful application shutdown
 * 
 * @author Subway Screen Development Team  
 * @version 2.0
 */
package ca.ucalgary.edu.ensf380.view;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import ca.ucalgary.edu.ensf380.SimulatorManager;
import ca.ucalgary.edu.ensf380.controller.ReadSimulatorOutput;
import ca.ucalgary.edu.ensf380.controller.StationController;
import ca.ucalgary.edu.ensf380.model.Train;
import ca.ucalgary.edu.ensf380.util.AppConstants;
import ca.ucalgary.edu.ensf380.util.AppLogger;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.UIManager;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JOptionPane;

public class SubwayScreenApp {
    
    public static void main(String[] args) {
        // Initialize logging system first
        AppLogger.initialize();
        AppLogger.startup("Application", "Starting Subway Screen Application v" + AppConstants.APP_VERSION);
        
        // ===== Modern UI Setup =====
        setupModernUI();
        
        // ===== Parse Arguments with Smart Defaults =====
        String[] parsedArgs = parseArgumentsWithDefaults(args);
        String trainNumber = parsedArgs[0];
        String city = parsedArgs[1];
        String countryCode = parsedArgs[2];
        
        AppLogger.startup("Arguments", String.format("Train: %s, City: %s, Country: %s", trainNumber, city, countryCode));
        
        // Validate train number
        if (!validateTrainNumber(trainNumber)) {
            return;
        }
        
        // Convert train number from String to integer for indexing purposes
        int trainNum = Integer.parseInt(trainNumber) - 1;

        // Initialize the main application components
        try {
            AppLogger.startup("Components", "Initializing application components");
            
            SimulatorManager simulatorManager = SimulatorManager.create();
            ReadSimulatorOutput output = new ReadSimulatorOutput();
            StationController stationController = new StationController();
            SubwayScreenGUI gui = new SubwayScreenGUI(trainNumber, city, countryCode, stationController.getStations());

            AppLogger.startup("Components", "All components initialized successfully");
            AppLogger.info(AppConstants.SUCCESS_APP_STARTED);

            // Main application loop
            runMainLoop(simulatorManager, output, stationController, gui, trainNum);
            
            // Shutdown simulator when main loop ends
            simulatorManager.shutdown();
            
        } catch (Exception e) {
            AppLogger.error("Failed to initialize application components", e);
            showErrorDialog("Failed to start application: " + e.getMessage());
        } finally {
            // Graceful shutdown
            AppLogger.info("Application shutting down");
            AppLogger.shutdown();
            System.exit(0); // Ensure complete application termination
        }
    }
    
    /**
     * Setup modern UI with FlatLaf dark theme
     */
    private static void setupModernUI() {
        try {
            AppLogger.startup("UI", "Setting up modern UI theme");
            
            FlatDarkLaf.setup();
            UIManager.setLookAndFeel(new FlatDarkLaf());
            
            // Global font (Segoe UI, fallback to default if unavailable)
            UIManager.put("defaultFont", AppConstants.MAIN_FONT);
            
            // FlatLaf rounded corners
            UIManager.put("Component.arc", AppConstants.LARGE_RADIUS);
            UIManager.put("Button.arc", AppConstants.LARGE_RADIUS);
            UIManager.put("TextComponent.arc", AppConstants.MEDIUM_RADIUS);
            
            // Dark theme panel background
            UIManager.put("Panel.background", AppConstants.BACKGROUND_COLOR);
            UIManager.put("Label.foreground", AppConstants.TEXT_PRIMARY);
            
            AppLogger.startup("UI", "Modern UI theme applied successfully");
            
        } catch (Exception ex) {
            AppLogger.error("Failed to initialize FlatLaf theme", ex);
            showErrorDialog("Failed to initialize modern UI theme");
        }
    }
    
    /**
     * Parse command-line arguments with intelligent defaults
     * Allows the application to run with 0, 1, 2, or 3 arguments
     * 
     * @param args command-line arguments
     * @return array of [trainNumber, city, countryCode] with defaults filled in
     */
    private static String[] parseArgumentsWithDefaults(String[] args) {
        // Default values for a great demo experience
        String defaultTrain = "1";
        String defaultCity = "Calgary"; 
        String defaultCountry = "CA";
        
        String trainNumber = defaultTrain;
        String city = defaultCity;
        String countryCode = defaultCountry;
        
        // Override defaults with provided arguments
        if (args.length >= 1 && !args[0].trim().isEmpty()) {
            trainNumber = args[0].trim();
        }
        if (args.length >= 2 && !args[1].trim().isEmpty()) {
            city = args[1].trim();
        }
        if (args.length >= 3 && !args[2].trim().isEmpty()) {
            countryCode = args[2].trim();
        }
        
        // Log what we're using
        if (args.length == 0) {
            AppLogger.info("STARTUP: Using default configuration - Train: " + trainNumber + ", City: " + city + ", Country: " + countryCode);
        } else if (args.length < 3) {
            AppLogger.info("STARTUP: Using partial arguments with defaults - Train: " + trainNumber + ", City: " + city + ", Country: " + countryCode);
        } else {
            AppLogger.info("STARTUP: Using provided arguments - Train: " + trainNumber + ", City: " + city + ", Country: " + countryCode);
        }
        
        return new String[]{trainNumber, city, countryCode};
    }
    
    /**
     * Validate arguments are in acceptable ranges/formats
     * 
     * @param trainNumber the train number to validate
     * @return true if valid, false otherwise
     */
    private static boolean validateTrainNumber(String trainNumber) {
        try {
            int trainNum = Integer.parseInt(trainNumber);
            if (trainNum < AppConstants.MIN_TRAIN_ID || trainNum > AppConstants.MAX_TRAIN_ID) {
                AppLogger.error("Invalid train number: " + trainNumber + ". Must be between " + AppConstants.MIN_TRAIN_ID + " and " + AppConstants.MAX_TRAIN_ID);
                showErrorDialog("Invalid train number. Must be between " + AppConstants.MIN_TRAIN_ID + " and " + AppConstants.MAX_TRAIN_ID);
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            AppLogger.error("Invalid train number format: " + trainNumber, e);
            showErrorDialog("Train number must be a valid integer");
            return false;
        }
    }
    
    /**
     * Main application loop with improved error handling and logging
     */
    private static void runMainLoop(SimulatorManager simulatorManager, ReadSimulatorOutput output, 
                                  StationController stationController, SubwayScreenGUI gui, int trainNum) {
        AppLogger.info("Starting main application loop");
        
        int loopCount = 0;
        long lastUpdateTime = System.currentTimeMillis();
        
        // Main loop: runs continuously while simulator manager is running
        while (simulatorManager.isRunning()) {
            try {
                long startTime = System.currentTimeMillis();
                
                // Update and retrieve the current train positions from the simulator output
                output.readOutput();
                ArrayList<Train> trains = output.getTrains();
                
                if (trains.isEmpty()) {
                    AppLogger.warning("No train data available from simulator");
                    Thread.sleep(AppConstants.TRAIN_UPDATE_INTERVAL);
                    continue;
                }
                
                if (trainNum >= trains.size()) {
                    AppLogger.warning("Train index " + trainNum + " exceeds available trains (" + trains.size() + ")");
                    Thread.sleep(AppConstants.TRAIN_UPDATE_INTERVAL);
                    continue;
                }
                
                Train currentTrain = trains.get(trainNum);
                AppLogger.debug("Current train position: " + currentTrain.getPosition() + " moving " + currentTrain.getDirection());

                // Update the GUI with the train's current position and get the next station code
                stationController.updateTrainPos(trainNum, trains, gui);
                String nextStationCode = stationController.nextStationNum;

                // Play audio announcement for the next station
                if (nextStationCode != null && !nextStationCode.trim().isEmpty()) {
                    playAudioAnnouncement(nextStationCode.trim());
                }
                
                // Log performance metrics if enabled
                long duration = System.currentTimeMillis() - startTime;
                AppLogger.performance("Main loop iteration", duration);
                
                loopCount++;
                if (loopCount % 10 == 0) { // Log every 10 iterations
                    long timeSinceLastLog = System.currentTimeMillis() - lastUpdateTime;
                    AppLogger.data("Main loop", String.format("Completed %d iterations in %d ms", 10, timeSinceLastLog));
                    lastUpdateTime = System.currentTimeMillis();
                }

                // Pause the loop for the configured interval
                Thread.sleep(AppConstants.TRAIN_UPDATE_INTERVAL);
                
            } catch (InterruptedException e) {
                AppLogger.warning("Main loop interrupted", e);
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                AppLogger.error("Error in main loop iteration", e);
                // Continue running despite errors
                try {
                    Thread.sleep(AppConstants.TRAIN_UPDATE_INTERVAL);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        AppLogger.info("Main application loop terminated");
    }

    /**
     * Plays an audio announcement for the next station with improved error handling.
     *
     * @param stationCode the station code for the audio file
     */
    private static void playAudioAnnouncement(String stationCode) {
        String audioFilePath = AppConstants.AUDIO_PATH + stationCode + ".mp3";
        AppLogger.debug("Playing audio announcement: " + audioFilePath);
        
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(audioFilePath))) {
            AdvancedPlayer player = new AdvancedPlayer(bis);
            player.play();
            AppLogger.debug("Audio announcement completed for station: " + stationCode);
            
        } catch (FileNotFoundException e) {
            AppLogger.warning("Audio file not found: " + audioFilePath);
            // Don't show dialog for missing audio files, just log the warning
        } catch (IOException e) {
            AppLogger.error("IO error while playing audio: " + audioFilePath, e);
        } catch (JavaLayerException e) {
            AppLogger.error("Audio playback error for: " + audioFilePath, e);
        } catch (Exception e) {
            AppLogger.error("Unexpected error during audio playback: " + audioFilePath, e);
        }
    }
    
    /**
     * Show error dialog to user with consistent styling
     */
    private static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, 
            AppConstants.APP_NAME + " - Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
