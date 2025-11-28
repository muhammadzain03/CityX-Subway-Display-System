/**
 * News Data Controller
 * 
 * Manages fetching and parsing of live news data from external news APIs.
 * Handles API communication, response parsing, error handling, and provides
 * formatted news content for display in the news panel.
 * 
 * Key responsibilities:
 * - Fetching news from external APIs
 * - Parsing JSON responses and extracting headlines
 * - Error handling and fallback content
 * - News content formatting for display
 * - API rate limiting and caching
 * 
 * @author Subway Screen Development Team
 * @version 2.0
 */
package ca.ucalgary.edu.ensf380.controller;

import ca.ucalgary.edu.ensf380.view.NewsPanel;
import ca.ucalgary.edu.ensf380.util.AppLogger;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;


public class NewsController extends DataFetcherController {

    private final NewsPanel newsPanel;
    // NOTE: In production, API tokens should be stored in environment variables
    private static final String API_TOKEN = "k1O0sVX6bOs5UMhjgWAuu6dsLA1GoO8I9Wzr80MB";
    private static final String NEWS_REGEX = "\"title\":\"(.*?)\"";
    private static final Pattern NEWS_PATTERN = Pattern.compile(NEWS_REGEX);
    
    // Fallback news content for when API fails
    private static final String[] FALLBACK_NEWS = {
        "Welcome to CityX Subway - Your reliable transit solution for over 50 years",
        "All lines operating on schedule with excellent service reliability",
        "Download our mobile app for real-time updates, trip planning, and digital passes",
        "New accessibility features including enhanced audio announcements now available",
        "Weekend service extended hours - trains running until 2:00 AM Friday and Saturday",
        "Student discounts available - show your student ID for reduced fares",
        "Free WiFi now available at all major stations across the network",
        "Environmental initiative: CityX Subway prevents 2.5 million car trips annually",
        "Safety first - emergency help buttons located throughout all stations and trains"
    };

    public NewsController(NewsPanel newsPanel) {
        this.newsPanel = newsPanel;
        
        // Immediately show professional content while real news loads in background
        AppLogger.info("NEWS: Displaying initial content immediately");
        showInitialContent();
    }

    /**
     * Retrieves news headlines for a given country code and updates the NewsPanel.
     * Uses retry logic and combines real news with transit information.
     * Background operation - initial content already displayed.
     * 
     * @param countryCode the country code to retrieve news for
     */
    public void retrieveNews(String countryCode) {
        // Give the UI a moment to render the initial content
        new Thread(() -> {
            try {
                Thread.sleep(100); // Brief delay to let UI show initial content first
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            String realNews = null;
            
            // Try to fetch real news with retries
            for (int attempt = 1; attempt <= 2; attempt++) {
                try {
                    AppLogger.info("NEWS: Attempting to fetch real news (attempt " + attempt + "/2)");
                    realNews = fetchNewsWithTimeout(countryCode);
                    if (realNews != null && !realNews.trim().isEmpty()) {
                        AppLogger.info("NEWS: Successfully fetched real news headlines");
                        break;
                    }
                } catch (Exception e) {
                    AppLogger.warning("NEWS: Attempt " + attempt + " failed - " + e.getMessage());
                    if (attempt < 2) {
                        try {
                            Thread.sleep(2000); // Wait 2 seconds before retry
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }
            
            // Only update if we got real news (otherwise keep showing initial content)
            if (realNews != null && !realNews.trim().isEmpty()) {
                String finalNews = buildCombinedNewsContent(realNews);
                updateNews(finalNews);
                AppLogger.info("NEWS: Successfully updated with live headlines");
            } else {
                AppLogger.info("NEWS: Keeping initial content (API unavailable)");
            }
            
        }).start();
    }
    
    /**
     * Fetches news with extended timeout specifically for news API
     */
    private String fetchNewsWithTimeout(String countryCode) throws Exception {
        String locale = countryCode.toLowerCase();
        String url = String.format(
            "https://api.thenewsapi.com/v1/news/top?api_token=%s&locale=%s&language=en&limit=3",
            API_TOKEN, locale
        );
        
        AppLogger.debug("NEWS API: Fetching from " + url);
        long startTime = System.currentTimeMillis();
        
        // Create HTTP client with extended timeout for news API
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(7000)) // 7 second connect
            .build();
            
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(20000)) // 20 second read timeout
                .header("User-Agent", "SubwayScreen/2.0")
                .header("Accept", "application/json")
                .GET()
                .build();
                
        HttpResponse<String> response = client.send(request, 
            HttpResponse.BodyHandlers.ofString());
        
        long duration = System.currentTimeMillis() - startTime;
        AppLogger.info("NEWS API: Response received in " + duration + "ms with status " + response.statusCode());
        
        if (response.statusCode() != 200) {
            throw new Exception("HTTP " + response.statusCode() + " response");
        }
        
        // Parse the JSON response
        JSONObject obj = new JSONObject(response.body());
        JSONArray articles = obj.optJSONArray("data");
        
        if (articles == null || articles.length() == 0) {
            return null;
        }
        
        StringBuilder newsBuilder = new StringBuilder();
        for (int i = 0; i < Math.min(3, articles.length()); i++) {
            JSONObject article = articles.getJSONObject(i);
            String headline = article.optString("title", "").trim();
            if (!headline.isEmpty()) {
                if (newsBuilder.length() > 0) {
                    newsBuilder.append(" • ");
                }
                newsBuilder.append(headline);
            }
        }
        
        String result = newsBuilder.toString();
        AppLogger.info("NEWS API: Parsed " + articles.length() + " articles, extracted " + 
                      (result.split(" • ").length) + " headlines");
        return result;
    }
    
    /**
     * Builds combined news content mixing real news with transit information
     */
    private String buildCombinedNewsContent(String realNews) {
        StringBuilder combined = new StringBuilder();
        
        // Start with real news if available
        if (realNews != null && !realNews.trim().isEmpty()) {
            combined.append(realNews);
            AppLogger.info("NEWS: Updated with real headlines + transit info");
        } else {
            AppLogger.info("NEWS: API unavailable - showing enhanced transit info");
        }
        
        // Always add some transit-related content for context
        String[] selectedFallback;
        if (realNews != null && !realNews.trim().isEmpty()) {
            // If we have real news, add fewer but high-value transit items
            selectedFallback = new String[]{
                FALLBACK_NEWS[1], // Service status
                FALLBACK_NEWS[4]  // Extended hours
            };
        } else {
            // If no real news, show comprehensive transit content
            selectedFallback = new String[]{
                FALLBACK_NEWS[0], // Welcome message
                FALLBACK_NEWS[1], // Service status  
                FALLBACK_NEWS[2], // Mobile app
                FALLBACK_NEWS[3], // Accessibility
                FALLBACK_NEWS[4]  // Weekend service
            };
        }
        
        for (String fallbackItem : selectedFallback) {
            if (combined.length() > 0) {
                combined.append(" • ");
            }
            combined.append(fallbackItem);
        }
        
        return combined.toString();
    }

    /**
     * Updates the news panel with the provided news content.
     *
     * @param news the news content to display
     */
    private void updateNews(String news) {
        // Ensure UI updates happen on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            newsPanel.updateNewsLabel(news);
        });
    }
    
    /**
     * Updates news panel with fallback content when API fails.
     * Provides professional transit-related content instead of error messages.
     */
    private void updateNewsWithFallback() {
        StringBuilder fallbackNews = new StringBuilder();
        for (int i = 0; i < FALLBACK_NEWS.length; i++) {
            if (i > 0) {
                fallbackNews.append(" • ");
            }
            fallbackNews.append(FALLBACK_NEWS[i]);
        }
        
        AppLogger.info("Using fallback news content due to API unavailability");
        updateNews(fallbackNews.toString());
    }

    /**
     * Show immediate professional content while real news loads in background
     */
    private void showInitialContent() {
        // Show a curated mix of the best fallback content immediately
        String[] initialContent = {
            FALLBACK_NEWS[0], // Welcome message
            FALLBACK_NEWS[1], // Service status
            FALLBACK_NEWS[2], // Mobile app
            FALLBACK_NEWS[6]  // Free WiFi
        };
        
        StringBuilder immediate = new StringBuilder();
        for (int i = 0; i < initialContent.length; i++) {
            if (i > 0) {
                immediate.append(" • ");
            }
            immediate.append(initialContent[i]);
        }
        
        updateNews(immediate.toString());
        AppLogger.info("NEWS: Initial professional content displayed");
    }
}
