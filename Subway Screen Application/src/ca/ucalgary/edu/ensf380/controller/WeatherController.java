package ca.ucalgary.edu.ensf380.controller;

import ca.ucalgary.edu.ensf380.view.WeatherPanel;
import ca.ucalgary.edu.ensf380.util.AppConstants;
import ca.ucalgary.edu.ensf380.util.AppLogger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller for managing weather data retrieval and display.
 * Handles API calls, data parsing, and panel updates with proper
 * error handling and retry logic.
 * 
 * @author Subway Screen Development Team
 * @version 2.0
 */
public class WeatherController extends DataFetcherController {
    private final WeatherPanel weatherPanel;
    private static final Pattern WEATHER_PATTERN = Pattern.compile(AppConstants.WEATHER_REGEX);
    private String lastKnownWeather = null;
    private long lastSuccessfulUpdate = 0;

    public WeatherController(WeatherPanel weatherPanel) {
        this.weatherPanel = weatherPanel;
        AppLogger.startup("WeatherController", "Weather controller initialized");
    }

    /**
     * Retrieves weather information for a specified city and updates the WeatherPanel
     * with improved error handling and retry logic.
     *
     * @param city the name of the city to retrieve weather data for
     */
    public void retrieveWeather(String city) {
        if (city == null || city.trim().isEmpty()) {
            AppLogger.error("City name cannot be null or empty for weather retrieval");
            updateWeather("N/A", "N/A", "N/A", "N/A", "N/A");
            return;
        }
        
        AppLogger.data("Weather", "Retrieving weather data for city: " + city);
        
        // Use background thread for network operations
        new Thread(() -> {
            retrieveWeatherWithRetry(city.trim(), AppConstants.API_RETRY_ATTEMPTS);
        }, "WeatherRetrieval-" + city).start();
    }
    
    /**
     * Retrieve weather with retry logic
     */
    private void retrieveWeatherWithRetry(String city, int remainingAttempts) {
        try {
            long startTime = System.currentTimeMillis();
            String url = buildWeatherUrl(city);
            
            AppLogger.debug("Fetching weather from: " + url);
            String weatherData = fetchData(url);
            
            if (weatherData == null || weatherData.trim().isEmpty()) {
                throw new RuntimeException("Empty weather data received");
            }
            
            String[] parsedData = parseWeatherData(weatherData);
            long duration = System.currentTimeMillis() - startTime;
            
            if (parsedData != null) {
                updateWeather(parsedData[0], parsedData[1], parsedData[2], parsedData[3], parsedData[4]);
                lastKnownWeather = weatherData;
                lastSuccessfulUpdate = System.currentTimeMillis();
                AppLogger.performance("Weather API call", duration);
                AppLogger.info(AppConstants.SUCCESS_WEATHER_UPDATED + " for " + city);
            } else {
                throw new RuntimeException("Weather data parsing failed");
            }
            
        } catch (Exception e) {
            AppLogger.networkError("Weather retrieval", city, e);
            
            if (remainingAttempts > 1) {
                AppLogger.info("Retrying weather retrieval for " + city + " (" + (remainingAttempts - 1) + " attempts remaining)");
                
                // Wait before retry
                try {
                    Thread.sleep(AppConstants.API_RETRY_DELAY);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
                
                retrieveWeatherWithRetry(city, remainingAttempts - 1);
            } else {
                AppLogger.error(AppConstants.ERROR_WEATHER_API + " for " + city + " after all retry attempts");
                
                // Use last known weather if available
                if (lastKnownWeather != null && isRecentUpdate()) {
                    AppLogger.info("Using cached weather data for " + city);
                    String[] cachedData = parseWeatherData(lastKnownWeather);
                    if (cachedData != null) {
                        updateWeather(cachedData[0] + " (Cached)", cachedData[1], cachedData[2], cachedData[3], cachedData[4]);
                        return;
                    }
                }
                
                // Fallback to N/A values
                updateWeatherWithError(city, e.getMessage());
            }
        }
    }
    
    /**
     * Build weather API URL with proper encoding
     */
    private String buildWeatherUrl(String city) {
        // Simple URL encoding for city name
        String encodedCity = city.replace(" ", "%20").replace(",", "%2C");
        return AppConstants.WEATHER_API_BASE + encodedCity + AppConstants.WEATHER_API_FORMAT;
    }
    
    /**
     * Check if last update was recent enough to use cached data
     */
    private boolean isRecentUpdate() {
        long timeSinceLastUpdate = System.currentTimeMillis() - lastSuccessfulUpdate;
        return timeSinceLastUpdate < 300000; // 5 minutes
    }

    /**
     * Parses weather data from the API response with improved validation.
     *
     * @param weatherData the raw weather data string
     * @return an array of parsed weather details or null if parsing fails
     */
    private String[] parseWeatherData(String weatherData) {
        if (weatherData == null || weatherData.trim().isEmpty()) {
            AppLogger.warning("Empty weather data provided for parsing");
            return null;
        }
        
        String trimmedData = weatherData.trim();
        AppLogger.debug("Parsing weather data: " + trimmedData);
        
        try {
            Matcher matcher = WEATHER_PATTERN.matcher(trimmedData);
            if (matcher.find()) {
                String location = validateAndClean(matcher.group(1));
                String condition = validateAndClean(matcher.group(2));
                String temperature = validateAndClean(matcher.group(3));
                String wind = validateAndClean(matcher.group(4));
                String precipitation = validateAndClean(matcher.group(5));
                
                // Validate that essential data is present
                if (location.equals("N/A") && condition.equals("N/A") && temperature.equals("N/A")) {
                    AppLogger.warning("Weather data contains no useful information");
                    return null;
                }
                
                AppLogger.debug("Successfully parsed weather data for: " + location);
                return new String[]{location, condition, temperature, wind, precipitation};
            } else {
                AppLogger.warning("Weather data does not match expected pattern: " + trimmedData);
                
                // Try fallback parsing for partial data
                return tryFallbackParsing(trimmedData);
            }
        } catch (Exception e) {
            AppLogger.error("Weather parsing error", e);
            return null;
        }
    }
    
    /**
     * Validate and clean individual weather data fields
     */
    private String validateAndClean(String value) {
        if (value == null) return "N/A";
        
        String cleaned = value.trim();
        if (cleaned.isEmpty()) return "N/A";
        
        // Remove any problematic characters
        cleaned = cleaned.replaceAll("[\\r\\n\\t]", " ");
        cleaned = cleaned.replaceAll("\\s+", " ");
        
        return cleaned;
    }
    
    /**
     * Try fallback parsing for partial weather data
     */
    private String[] tryFallbackParsing(String weatherData) {
        AppLogger.debug("Attempting fallback weather parsing");
        
        // Simple fallback: just display raw data if it looks reasonable
        if (weatherData.length() < 200 && !weatherData.contains("error") && !weatherData.contains("404")) {
            return new String[]{"Unknown Location", weatherData, "N/A", "N/A", "N/A"};
        }
        
        return null;
    }

    /**
     * Updates the weather display on the WeatherPanel with enhanced formatting.
     *
     * @param location the location name
     * @param condition the weather condition
     * @param temperature the temperature in °C
     * @param wind the wind speed and direction
     * @param precipitation the precipitation amount
     */
    private void updateWeather(String location, String condition, String temperature, String wind, String precipitation) {
        try {
            String weatherInfo = formatWeatherInfo(location, condition, temperature, wind, precipitation);
            weatherPanel.updateWeatherLabel(weatherInfo);
            AppLogger.debug("Weather display updated successfully");
        } catch (Exception e) {
            AppLogger.error("Error updating weather display", e);
        }
    }
    
    /**
     * Update weather display with error information
     */
    private void updateWeatherWithError(String city, String errorMessage) {
        String errorInfo = String.format(
            "<html>Location: %s<br>Status: %s<br>Last Update: Never<br>Error: %s</html>",
            city, "Service Unavailable", errorMessage != null ? errorMessage : "Unknown error"
        );
        weatherPanel.updateWeatherLabel(errorInfo);
    }
    
    /**
     * Format weather information with better structure
     */
    private String formatWeatherInfo(String location, String condition, String temperature, String wind, String precipitation) {
        return String.format(
            "<html>" +
            "<div style='font-family: %s; color: #F1F5F9;'>" +
            "<b>%s</b><br>" +
            "<span style='color: #94A3B8;'>Condition:</span> %s<br>" +
            "<span style='color: #94A3B8;'>Temperature:</span> %s<br>" +
            "<span style='color: #94A3B8;'>Wind:</span> %s<br>" +
            "<span style='color: #94A3B8;'>Precipitation:</span> %s" +
            "</div>" +
            "</html>",
            AppConstants.FONT_FAMILY,
            location,
            condition,
            temperature,
            wind,
            precipitation
        );
    }
    
    /**
     * Get weather service status
     */
    public boolean isWeatherServiceHealthy() {
        return lastSuccessfulUpdate > 0 && isRecentUpdate();
    }
    
    /**
     * Get last successful update timestamp
     */
    public long getLastUpdateTime() {
        return lastSuccessfulUpdate;
    }
}
