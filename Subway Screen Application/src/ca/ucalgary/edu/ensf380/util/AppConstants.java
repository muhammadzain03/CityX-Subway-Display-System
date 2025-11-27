package ca.ucalgary.edu.ensf380.util;

import java.awt.Color;
import java.awt.Font;

/**
 * Application constants and configuration settings for the Subway Screen Application.
 * Centralizes all magic numbers, paths, colors, fonts, and timing configurations
 * for better maintainability and consistency across the application.
 * 
 * @author Subway Screen Development Team
 * @version 2.0
 */
public final class AppConstants {
    
    // Prevent instantiation
    private AppConstants() {}
    
    // ========== APPLICATION METADATA ==========
    public static final String APP_NAME = "CityX Subway Display";
    public static final String APP_VERSION = "2.0";
    public static final String APP_AUTHOR = "Subway Screen Development Team";
    
    // ========== FILE PATHS ==========
    public static final String ADVERTISEMENTS_PATH = "advertisements/";
    public static final String AUDIO_PATH = "audio/";
    public static final String DATA_PATH = "data/";
    public static final String OUTPUT_PATH = "out/";
    public static final String EXECUTABLE_PATH = "exe/";
    
    // Specific file paths
    public static final String SUBWAY_DATA_FILE = DATA_PATH + "subway.csv";
    public static final String SIMULATOR_JAR = EXECUTABLE_PATH + "SubwaySimulator.jar";
    
    // ========== UI DIMENSIONS ==========
    public static final int MAIN_WINDOW_WIDTH = 1200;
    public static final int MAIN_WINDOW_HEIGHT = 700;
    public static final int WEATHER_PANEL_WIDTH = 220;
    public static final int WEATHER_PANEL_HEIGHT = 450;
    public static final int NEWS_PANEL_HEIGHT = 60;
    public static final int STATION_INFO_PANEL_HEIGHT = 100;
    public static final int BOTTOM_PANEL_HEIGHT = 170;
    
    // ========== COLOR SCHEME ==========
    // Primary colors
    public static final Color BACKGROUND_COLOR = new Color(15, 23, 42);      // Dark slate
    public static final Color CARD_COLOR = new Color(30, 41, 59);            // Slate 700
    public static final Color PANEL_SPACING_COLOR = new Color(30, 41, 59);   // Subtle separator
    
    // Text colors
    public static final Color TEXT_PRIMARY = new Color(241, 245, 249);       // Slate 100
    public static final Color TEXT_SECONDARY = new Color(148, 163, 184);     // Slate 400
    public static final Color TEXT_COLOR = new Color(241, 245, 249);         // Light text
    
    // Accent colors
    public static final Color ACCENT_COLOR = new Color(16, 185, 129);        // Emerald 500
    public static final Color LOADING_COLOR = new Color(59, 130, 246);       // Blue 500
    public static final Color ERROR_COLOR = new Color(239, 68, 68);          // Red 500
    public static final Color WARNING_COLOR = new Color(245, 158, 11);       // Amber 500
    public static final Color SUCCESS_COLOR = new Color(34, 197, 94);        // Green 500
    
    // Subway line colors
    public static final Color RED_LINE = new Color(239, 68, 68);             // Modern red
    public static final Color GREEN_LINE = new Color(34, 197, 94);           // Modern green  
    public static final Color BLUE_LINE = new Color(59, 130, 246);           // Modern blue
    
    // Station colors
    public static final Color REGULAR_STATION = new Color(241, 245, 249);    // White
    public static final Color TRANSFER_STATION = new Color(255, 193, 7);     // Amber
    public static final Color TERMINAL_STATION = new Color(239, 68, 68);     // Red
    public static final Color TRAIN_COLOR = new Color(16, 185, 129);         // Emerald
    public static final Color CURRENT_STATION_COLOR = new Color(16, 185, 129); // Emerald 500
    public static final Color NEXT_STATION_COLOR = new Color(59, 130, 246);  // Blue 500
    public static final Color PREV_STATION_COLOR = new Color(156, 163, 175); // Gray 400
    
    // Grid and borders
    public static final Color GRID_COLOR = new Color(30, 41, 59, 80);        // Subtle grid
    public static final Color BORDER_COLOR = new Color(71, 85, 105);         // Border color
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 100);        // Shadow color
    
    // ========== TYPOGRAPHY ==========
    public static final String FONT_FAMILY = "Segoe UI";
    public static final Font MAIN_FONT = new Font(FONT_FAMILY, Font.PLAIN, 14);  // Reduced from 18
    public static final Font HEADER_FONT = new Font(FONT_FAMILY, Font.BOLD, 12);
    public static final Font STATION_FONT = new Font(FONT_FAMILY, Font.BOLD, 12); // Reduced from 14
    public static final Font NEWS_FONT = new Font(FONT_FAMILY, Font.PLAIN, 14);    // Reduced from 16
    public static final Font TIME_FONT = new Font(FONT_FAMILY, Font.BOLD, 28);     // Reduced from 32
    public static final Font WEATHER_FONT = new Font(FONT_FAMILY, Font.PLAIN, 12); // Reduced from 14
    public static final Font STATUS_FONT = new Font(FONT_FAMILY, Font.PLAIN, 12);  // Reduced from 14
    public static final Font TOOLTIP_FONT = new Font(FONT_FAMILY, Font.PLAIN, 11); // Reduced from 12
    public static final Font TYPE_FONT = new Font(FONT_FAMILY, Font.PLAIN, 9);     // Reduced from 10
    public static final Font LABEL_FONT = new Font(FONT_FAMILY, Font.BOLD, 11);    // Reduced from 12
    
    // ========== TIMING CONSTANTS ==========
    // Animation timings (in milliseconds)
    public static final int ANIMATION_FRAME_RATE = 16;          // ~60 FPS
    public static final int HOVER_ANIMATION_RATE = 50;          // Hover pulse rate
    public static final int LOADING_ANIMATION_RATE = 20;        // Loading animation rate
    public static final int CLICK_FEEDBACK_RATE = 100;          // Click feedback rate
    
    // Application update intervals
    public static final int TRAIN_UPDATE_INTERVAL = 13000;      // 13 seconds
    public static final int AD_DISPLAY_INTERVAL = 10000;        // 10 seconds
    public static final int TIME_UPDATE_INTERVAL = 1000;        // 1 second
    public static final int NEWS_SCROLL_RATE = 30;              // News scrolling rate
    
    // Transition timings
    public static final int AD_FADE_DURATION = 16;              // Advertisement fade timing
    public static final int LOADING_SIMULATION_TIME = 500;      // Simulated loading time
    public static final int PULSE_DURATION = 60;                // Pulse animation frames
    
    // ========== ANIMATION CONSTANTS ==========
    public static final float FADE_STEP = 0.06f;                // Fade animation step
    public static final float SCALE_STEP = 0.02f;               // Scale animation step
    public static final float EASING_FACTOR = 0.15f;            // Movement easing factor
    public static final float LOADING_PROGRESS_STEP = 0.02f;     // Loading progress step
    public static final float PULSE_SPEED = 0.2f;               // Hover pulse speed
    public static final float TRAIN_SPEED = 3.0f;               // Train movement speed
    
    // ========== UI COMPONENT SIZES ==========
    // Map panel settings
    public static final double MAP_SCALE_MARGIN = 0.85;         // Map scaling margin
    public static final int GRID_SIZE = 50;                     // Background grid size
    public static final int STATION_SIZE_SMALL = 10;            // Regular station size (reduced from 12)
    public static final int STATION_SIZE_LARGE = 14;            // Transfer/terminal station size (reduced from 16)
    public static final int TRAIN_SIZE = 16;                    // Train indicator size
    public static final int TRAIN_SIZE_SELECTED = 20;           // Selected train indicator size (larger)
    public static final int TOOLTIP_PADDING = 15;               // Tooltip padding
    public static final int SHADOW_OFFSET = 2;                  // Standard shadow offset
    
    // Border and stroke widths
    public static final float THIN_STROKE = 1f;
    public static final float MEDIUM_STROKE = 2f;
    public static final float THICK_STROKE = 3f;
    public static final float LINE_STROKE = 6f;                 // Subway line stroke
    public static final float SHADOW_STROKE = 8f;               // Line shadow stroke
    
    // Corner radius values
    public static final int SMALL_RADIUS = 10;
    public static final int MEDIUM_RADIUS = 12;
    public static final int LARGE_RADIUS = 15;
    public static final int CARD_RADIUS = 15;
    
    // ========== NETWORK TIMEOUTS ==========
    public static final int HTTP_CONNECT_TIMEOUT = 15000;       // 15 seconds (increased for slow networks)
    public static final int HTTP_READ_TIMEOUT = 20000;          // 20 seconds
    public static final int NEWS_API_TIMEOUT = 15000;           // 15 seconds for news API (longer)
    public static final int API_RETRY_ATTEMPTS = 3;             // Number of retry attempts (increased)
    public static final int API_RETRY_DELAY = 3000;             // Delay between retries (3 seconds)
    
    // ========== DATABASE SETTINGS ==========
    public static final String DB_URL = "jdbc:mysql://localhost:3306/subway_screen";
    public static final String DB_USERNAME = "root";
    // NOTE: In production, credentials should be stored in environment variables or secure vault
    public static final String DB_PASSWORD = "Qwerty$455";
    public static final int DB_CONNECTION_TIMEOUT = 5000;       // 5 seconds
    
    // ========== API ENDPOINTS ==========
    public static final String WEATHER_API_BASE = "https://wttr.in/";
    public static final String WEATHER_API_FORMAT = "?format=%25l+%25C+%25t+%25w+%25p";
    public static final String NEWS_API_BASE = "https://api.thenewsapi.com/v1/news/top";
    public static final String NEWS_API_TOKEN = "k1O0sVX6bOs5UMhjgWAuu6dsLA1GoO8I9Wzr80MB";
    public static final String NEWS_API_LANGUAGE = "en";
    public static final int NEWS_API_LIMIT = 5;
    
    // ========== VALIDATION CONSTANTS ==========
    public static final int MAX_STATION_NAME_LENGTH = 50;
    public static final int MAX_NEWS_TITLE_LENGTH = 200;
    public static final int MIN_TRAIN_ID = 1;
    public static final int MAX_TRAIN_ID = 99;
    public static final String[] SUPPORTED_MEDIA_TYPES = {"GIF", "JPEG", "BMP"};
    public static final String[] SUPPORTED_AUDIO_FORMATS = {"MP3"};
    
    // ========== ERROR MESSAGES ==========
    public static final String ERROR_SIMULATOR_NOT_FOUND = "Subway simulator not found";
    public static final String ERROR_DATABASE_CONNECTION = "Failed to connect to database";
    public static final String ERROR_WEATHER_API = "Weather service unavailable";
    public static final String ERROR_NEWS_API = "News service unavailable";
    public static final String ERROR_AUDIO_PLAYBACK = "Audio playback failed";
    public static final String ERROR_FILE_NOT_FOUND = "Required file not found";
    public static final String ERROR_INVALID_DATA = "Invalid data format";
    
    // ========== SUCCESS MESSAGES ==========
    public static final String SUCCESS_APP_STARTED = "Subway Screen Application started successfully";
    public static final String SUCCESS_DATA_LOADED = "Station data loaded successfully";
    public static final String SUCCESS_WEATHER_UPDATED = "Weather information updated";
    public static final String SUCCESS_NEWS_UPDATED = "News feed updated";
    
    // ========== REGEX PATTERNS ==========
    public static final String WEATHER_REGEX = "^(.*?)\\s+(.*?)\\s+([+-]?\\d+°C)\\s+([←↔→↑↓↖↗↙↘]+\\d+km/h)\\s+(\\d+\\.\\d+mm)$";
    public static final String NEWS_TITLE_REGEX = "\"title\":\"(.*?)\"";
    public static final String STATION_CODE_REGEX = "^[RGB]\\d{2}$";
    
    // ========== DEVELOPMENT FLAGS ==========
    public static final boolean DEBUG_MODE = false;             // Enable debug logging
    public static final boolean PERFORMANCE_MONITORING = false; // Enable performance metrics
} 