package ca.ucalgary.edu.ensf380.controller;

import ca.ucalgary.edu.ensf380.view.AdvertisementPanel;
import ca.ucalgary.edu.ensf380.util.DatabaseUtil;
import ca.ucalgary.edu.ensf380.util.AppConstants;
import ca.ucalgary.edu.ensf380.util.AppLogger;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Controller for managing advertisement display and rotation.
 * Handles database operations, ad rotation timing, and panel updates
 * with proper service layer architecture.
 * 
 * @author Subway Screen Development Team
 * @version 2.0
 */
public class AdvertisementController {
    private List<Map<String, Object>> advertisements;
    private int currentAdIndex = 0;
    private Timer timer;
    private final AdvertisementPanel advertisementPanel;
    private ScheduledExecutorService scheduledExecutor;
    private boolean isRunning = false;
    
    public AdvertisementController(AdvertisementPanel advertisementPanel) {
        this.advertisementPanel = advertisementPanel;
        
        AppLogger.startup("AdvertisementController", "Initializing advertisement system");
        
        try {
            this.advertisements = loadAds();
            if (advertisements.isEmpty()) {
                AppLogger.warning("No advertisements loaded, using empty list");
            } else {
                AppLogger.info("Loaded " + advertisements.size() + " advertisements");
            }
        } catch (Exception e) {
            AppLogger.error("Error loading advertisements, using empty list", e);
            this.advertisements = new ArrayList<>();
        }
        
        initializeScheduledExecutor();
        startAdRotation();
    }
    
    /**
     * Initialize the scheduled executor service for better thread management
     */
    private void initializeScheduledExecutor() {
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "AdvertisementRotation");
            t.setDaemon(true);
            return t;
        });
        AppLogger.debug("Scheduled executor initialized for advertisement rotation");
    }

    /**
     * Loads advertisements from the database with improved error handling and validation.
     *
     * @return List of advertisements with each ad represented as a Map
     * @throws SQLException if there is a database access error
     */
    private List<Map<String, Object>> loadAds() throws SQLException {
        List<Map<String, Object>> ads = new ArrayList<>();
        DatabaseUtil dbUtil = new DatabaseUtil();
        
        AppLogger.data("Advertisement Loading", "Starting to load advertisements from database");
        long startTime = System.currentTimeMillis();

        try {
            // Attempt to connect to database
            if (!dbUtil.createConnection()) {
                AppLogger.error("Failed to establish database connection for advertisement loading");
                return ads; // Return empty list
            }
            
            // Test connection
            if (!dbUtil.testConnection()) {
                AppLogger.error("Database connection test failed");
                return ads;
            }
            
            String query = "SELECT * FROM advertisements ORDER BY id";
            ResultSet resultSet = dbUtil.selectQuery(query);
            
            if (resultSet == null) {
                AppLogger.error("Failed to execute advertisement query");
                return ads;
            }

            int loadedCount = 0;
            while (resultSet.next()) {
                try {
                    Map<String, Object> ad = createAdMapFromResultSet(resultSet);
                    if (validateAdvertisement(ad)) {
                        ads.add(ad);
                        loadedCount++;
                        AppLogger.debug("Loaded advertisement: " + ad.get("media_path"));
                    } else {
                        AppLogger.warning("Invalid advertisement data, skipping: " + ad.get("id"));
                    }
                } catch (SQLException e) {
                    AppLogger.error("Error processing advertisement row", e);
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            AppLogger.data("Advertisement Loading", String.format("Loaded %d advertisements in %d ms", loadedCount, duration));
            
        } catch (SQLException e) {
            AppLogger.error("Database error while loading advertisements", e);
            throw e;
        } finally {
            dbUtil.close();
        }
        
        return ads;
    }
    
    /**
     * Create advertisement map from ResultSet with proper data extraction
     */
    private Map<String, Object> createAdMapFromResultSet(ResultSet resultSet) throws SQLException {
        Map<String, Object> ad = new HashMap<>();
        ad.put("id", resultSet.getInt("id"));
        ad.put("media_type", resultSet.getString("media_type"));
        ad.put("media_path", resultSet.getString("media_path"));
        ad.put("display_duration", resultSet.getInt("display_duration"));
        return ad;
    }
    
    /**
     * Validate advertisement data before adding to list
     */
    private boolean validateAdvertisement(Map<String, Object> ad) {
        // Check required fields
        if (ad.get("id") == null || ad.get("media_type") == null || ad.get("media_path") == null) {
            AppLogger.warning("Advertisement missing required fields: " + ad);
            return false;
        }
        
        // Validate media type
        String mediaType = (String) ad.get("media_type");
        boolean validType = false;
        for (String supportedType : AppConstants.SUPPORTED_MEDIA_TYPES) {
            if (supportedType.equalsIgnoreCase(mediaType)) {
                validType = true;
                break;
            }
        }
        
        if (!validType) {
            AppLogger.warning("Unsupported media type in advertisement: " + mediaType);
            return false;
        }
        
        // Validate display duration
        Integer duration = (Integer) ad.get("display_duration");
        if (duration == null || duration <= 0) {
            AppLogger.warning("Invalid display duration in advertisement: " + duration);
            ad.put("display_duration", AppConstants.AD_DISPLAY_INTERVAL / 1000); // Default duration
        }
        
        // Validate media path
        String mediaPath = (String) ad.get("media_path");
        if (mediaPath == null || mediaPath.trim().isEmpty()) {
            AppLogger.warning("Empty media path in advertisement");
            return false;
        }
        
        return true;
    }

    /**
     * Starts rotating advertisements using ScheduledExecutorService for better thread management.
     */
    public void startAdRotation() {
        if (isRunning) {
            AppLogger.debug("Advertisement rotation already running");
            return;
        }
        
        if (advertisements == null || advertisements.isEmpty()) {
            AppLogger.warning("No advertisements available for rotation");
            return;
        }
        
        AppLogger.info("Starting advertisement rotation with " + advertisements.size() + " advertisements");
        
        // Stop any existing timer
        if (timer != null) {
            timer.cancel();
        }
        
        // Use ScheduledExecutorService for better thread management
        isRunning = true;
        timer = new Timer();
        
        // Start immediately and repeat every 10 seconds
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (advertisements != null && !advertisements.isEmpty()) {
                        Map<String, Object> ad = advertisements.get(currentAdIndex);
                        advertisementPanel.displayAdvertisement(ad);
                        
                        AppLogger.debug("Displaying advertisement " + (currentAdIndex + 1) + "/" + advertisements.size() + 
                                       ": " + ad.get("media_path"));
                        
                        currentAdIndex = (currentAdIndex + 1) % advertisements.size();
                        
                        // Log rotation completion
                        if (currentAdIndex == 0) {
                            AppLogger.data("Advertisement", "Completed full rotation cycle");
                        }
                    }
                } catch (Exception e) {
                    AppLogger.error("Error during advertisement rotation", e);
                }
            }
        }, 0, AppConstants.AD_DISPLAY_INTERVAL); // Start immediately, repeat every 10 seconds
    }

    /**
     * Pauses the advertisement rotation with logging.
     */
    public void pauseAd() {
        AppLogger.debug("Pausing advertisement rotation");
        
        if (timer != null) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        advertisementPanel.displayAdvertisement(null); // Clear the ad during pause
                        AppLogger.debug("Advertisement display cleared during pause");
                    } catch (Exception e) {
                        AppLogger.error("Error clearing advertisement during pause", e);
                    }
                }
            }, AppConstants.AD_DISPLAY_INTERVAL);
        }
    }

    /**
     * Resumes the advertisement rotation with logging.
     */
    public void resumeAd() {
        AppLogger.debug("Resuming advertisement rotation");
        
        if (timer != null) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        startAdRotation(); // Schedule the next ad
                        AppLogger.debug("Advertisement rotation resumed");
                    } catch (Exception e) {
                        AppLogger.error("Error resuming advertisement rotation", e);
                    }
                }
            }, AppConstants.AD_DISPLAY_INTERVAL / 2);
        }
    }

    /**
     * Stops the advertisement rotation and cleans up resources.
     */
    public void stopAdRotation() {
        AppLogger.info("Stopping advertisement rotation");
        isRunning = false;
        
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        
        if (scheduledExecutor != null && !scheduledExecutor.isShutdown()) {
            scheduledExecutor.shutdown();
            try {
                if (!scheduledExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduledExecutor.shutdownNow();
                    AppLogger.warning("Forced shutdown of advertisement scheduled executor");
                }
            } catch (InterruptedException e) {
                scheduledExecutor.shutdownNow();
                Thread.currentThread().interrupt();
                AppLogger.warning("Advertisement executor shutdown interrupted");
            }
        }
        
        AppLogger.data("Advertisement", "Rotation stopped and resources cleaned up");
    }
    
    /**
     * Reload advertisements from database
     */
    public void reloadAdvertisements() {
        AppLogger.info("Reloading advertisements from database");
        
        try {
            List<Map<String, Object>> newAds = loadAds();
            this.advertisements = newAds;
            this.currentAdIndex = 0;
            
            AppLogger.info("Successfully reloaded " + advertisements.size() + " advertisements");
            
            // Restart rotation if it was running
            if (isRunning) {
                stopAdRotation();
                startAdRotation();
            }
            
        } catch (SQLException e) {
            AppLogger.error("Failed to reload advertisements", e);
        }
    }
    
    /**
     * Get current advertisement statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_advertisements", advertisements.size());
        stats.put("current_index", currentAdIndex);
        stats.put("is_running", isRunning);
        stats.put("next_ad", currentAdIndex < advertisements.size() ? 
                 advertisements.get(currentAdIndex).get("media_path") : "None");
        
        AppLogger.debug("Advertisement statistics requested: " + stats);
        return stats;
    }
    
    /**
     * Cleanup resources when controller is destroyed
     */
    public void cleanup() {
        AppLogger.info("Cleaning up AdvertisementController resources");
        stopAdRotation();
    }
}
