package ca.ucalgary.edu.ensf380.controller;

import ca.ucalgary.edu.ensf380.view.AdvertisementPanel;
import ca.ucalgary.edu.ensf380.util.DatabaseUtil;

import java.sql.*;
import java.util.*;

public class AdvertisementController {
    private List<Map<String, Object>> advertisements;
    private int currentAdIndex = 0;
    private Timer timer;
    private final AdvertisementPanel advertisementPanel;
    
    public AdvertisementController(AdvertisementPanel advertisementPanel) {
        this.advertisementPanel = advertisementPanel;
        try {
            this.advertisements = loadAds();
        } catch (SQLException e) {
            System.out.println("Error loading advertisements from the database: " + e.getMessage());
            this.advertisements = new ArrayList<>(); // Initialize to an empty list if loading fails
        }
        startAdRotation();
    }

    /**
     * Loads advertisements from the database.
     *
     * @return List of advertisements with each ad represented as a Map
     * @throws SQLException if there is a database access error
     */
    private List<Map<String, Object>> loadAds() throws SQLException {
        List<Map<String, Object>> ads = new ArrayList<>();
        String query = "SELECT * FROM advertisements";
        DatabaseUtil dbUtil = new DatabaseUtil();

        try {
            dbUtil.createConnection();
            ResultSet resultSet = dbUtil.selectQuery(query);

            while (resultSet.next()) {
                Map<String, Object> ad = new HashMap<>();
                ad.put("id", resultSet.getInt("id"));
                ad.put("media_type", resultSet.getString("media_type"));
                ad.put("media_path", resultSet.getString("media_path"));
                ad.put("display_duration", resultSet.getInt("display_duration"));
                ads.add(ad);
            }
        } finally {
            dbUtil.close();
        }
        return ads;
    }

    /**
     * Starts rotating advertisements by scheduling a timer task.
     */
    public void startAdRotation() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (advertisements != null && !advertisements.isEmpty()) {
                    Map<String, Object> ad = advertisements.get(currentAdIndex);
                    advertisementPanel.displayAdvertisement(ad);
                    currentAdIndex = (currentAdIndex + 1) % advertisements.size();
                }
            }
        }, 0);
    }

    /**
     * Pauses the advertisement rotation for 10 seconds by clearing the ad display.
     */
    public void pauseAd() {
        if (timer != null) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    advertisementPanel.displayAdvertisement(null); // Clear the ad during pause
                }
            }, 10000); // Display ad for 10 seconds
        }
    }

    /**
     * Resumes the advertisement rotation after a 5-second pause.
     */
    public void resumeAd() {
        if (timer != null) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    startAdRotation(); // Schedule the next ad
                }
            }, 5000); // Pause for 5 seconds
        }
    }

    /**
     * Stops the advertisement rotation and cancels the timer.
     */
    public void stopAdRotation() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
