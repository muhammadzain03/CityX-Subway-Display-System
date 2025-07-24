/**
 * Abstract Base Controller for Data Fetching Operations
 * 
 * Provides common functionality for controllers that need to fetch data from external
 * APIs or web services. Handles HTTP requests with proper timeout configuration and
 * error handling. Extended by WeatherController and NewsController.
 * 
 * Key responsibilities:
 * - HTTP client configuration and management
 * - Network request execution with timeout handling
 * - Common error handling for network operations
 * - Response validation and processing
 * 
 * @author Subway Screen Development Team
 * @version 2.0
 */
package ca.ucalgary.edu.ensf380.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import ca.ucalgary.edu.ensf380.util.AppConstants;
import ca.ucalgary.edu.ensf380.util.AppLogger;

public abstract class DataFetcherController {

    /**
     * Fetches data from a given URL as a string with improved error handling and logging.
     *
     * @param stringUrl the URL to fetch data from
     * @return the response data as a String
     * @throws IOException if an I/O error occurs or if the response code is not HTTP_OK
     */
    protected String fetchData(String stringUrl) throws IOException {
        if (stringUrl == null || stringUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        
        AppLogger.debug("Fetching data from URL: " + stringUrl);
        long startTime = System.currentTimeMillis();
        
        try {
            HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(AppConstants.HTTP_CONNECT_TIMEOUT))
                .build();
                
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(stringUrl))
                    .timeout(Duration.ofMillis(AppConstants.HTTP_READ_TIMEOUT))
                    .header("User-Agent", AppConstants.APP_NAME + "/" + AppConstants.APP_VERSION)
                    .GET()
                    .build();
                    
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            long duration = System.currentTimeMillis() - startTime;
            AppLogger.network("HTTP GET", stringUrl, response.statusCode());
            AppLogger.performance("HTTP request", duration);
            
            if (response.statusCode() == 200) {
                AppLogger.debug("Successfully fetched " + response.body().length() + " characters");
                return response.body();
            } else {
                String errorMsg = "HTTP error response code: " + response.statusCode();
                AppLogger.networkError("HTTP GET", stringUrl, new IOException(errorMsg));
                throw new IOException(errorMsg);
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            AppLogger.networkError("HTTP GET", stringUrl, e);
            throw new IOException("Request was interrupted", e);
        } catch (Exception e) {
            AppLogger.networkError("HTTP GET", stringUrl, e);
            throw new IOException("Network request failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validates URL format
     */
    protected boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        try {
            URI.create(url);
            return url.startsWith("http://") || url.startsWith("https://");
        } catch (Exception e) {
            AppLogger.warning("Invalid URL format: " + url);
            return false;
        }
    }
}
