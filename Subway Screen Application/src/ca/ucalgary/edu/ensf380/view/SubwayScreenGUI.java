/**
 * Main GUI Orchestrator for Subway Screen Application
 * 
 * Central coordinator that manages and arranges all UI panels into a cohesive
 * subway screen interface. Handles layout management, component initialization,
 * and the coordination between different display panels (map, weather, news, etc.).
 * 
 * Key responsibilities:
 * - Overall window layout and sizing
 * - Panel instantiation and arrangement
 * - Component coordination and communication
 * - Display mode switching (map/advertisement)
 * - Timer management for screen updates
 * 
 * @author Subway Screen Development Team
 * @version 2.0
 */
package ca.ucalgary.edu.ensf380.view;

import ca.ucalgary.edu.ensf380.model.Station;
import ca.ucalgary.edu.ensf380.util.AppLogger;
import ca.ucalgary.edu.ensf380.controller.AdvertisementController;
import ca.ucalgary.edu.ensf380.controller.WeatherController;
import ca.ucalgary.edu.ensf380.controller.NewsController;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class SubwayScreenGUI {

    private final AdvertisementPanel advertisementPanel;
    private final WeatherPanel weatherPanel;
    private final NewsPanel newsPanel;
    private final StationInfoPanel stationInfoPanel;
    private final MapPanel mapPanel;

    private final AdvertisementController advertisementController;
    private final WeatherController weatherController;
    private final NewsController newsController;

    private final JPanel adMapPanel;
    private final CardLayout cardLayout;
    private Timer displayTimer;

    private boolean showingAd = true; // Track which card is currently visible
    
    // Modern color scheme matching all panels
    private static final Color BACKGROUND_COLOR = new Color(15, 23, 42); // Dark slate
    private static final Color PANEL_SPACING_COLOR = new Color(30, 41, 59); // Subtle separator

    public SubwayScreenGUI(String trainNumber, String city, String countryCode, ArrayList<Station> stations) {
        this.advertisementPanel = new AdvertisementPanel();
        this.weatherPanel = new WeatherPanel();
        this.newsPanel = new NewsPanel();
        this.stationInfoPanel = new StationInfoPanel();
        this.mapPanel = new MapPanel(stations, trainNumber);

        this.cardLayout = new CardLayout();
        this.adMapPanel = new JPanel(cardLayout);
        this.adMapPanel.setBackground(BACKGROUND_COLOR);

        this.advertisementController = new AdvertisementController(advertisementPanel);
        this.weatherController = new WeatherController(weatherPanel);
        weatherController.retrieveWeather(city);

        this.newsController = new NewsController(newsPanel);
        newsController.retrieveNews(countryCode);

        setupGUI(trainNumber);
        startDisplayTimer();
    }

    private void setupGUI(String trainNumber) {
        EventQueue.invokeLater(() -> {
            JFrame mainFrame = new JFrame("CityX Subway Display - Train " + trainNumber);
            mainFrame.setSize(1200, 700);
            mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // Add window listener for proper shutdown
        mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                AppLogger.info("Application window closing - initiating shutdown");
                System.exit(0);
            }
        });
            mainFrame.setBackground(BACKGROUND_COLOR);
            
            // Set modern window properties
            mainFrame.setLocationRelativeTo(null); // Center the window
            
            // Create main content panel with modern styling
            JPanel contentPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    
                    // Enable high-quality rendering
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Draw background
                    g2.setColor(BACKGROUND_COLOR);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    
                    // Draw subtle separator lines
                    g2.setColor(PANEL_SPACING_COLOR);
                    g2.setStroke(new BasicStroke(1f));
                    
                    // Vertical separator between main content and weather panel
                    int weatherPanelX = getWidth() - 230;
                    g2.drawLine(weatherPanelX - 5, 10, weatherPanelX - 5, getHeight() - 120);
                    
                    // Horizontal separators
                    g2.drawLine(10, getHeight() - 120, getWidth() - 10, getHeight() - 120); // Above news
                    g2.drawLine(10, getHeight() - 50, getWidth() - 10, getHeight() - 50); // Above station info
                    
                    g2.dispose();
                }
            };
            contentPanel.setLayout(new BorderLayout(10, 10));
            contentPanel.setBackground(BACKGROUND_COLOR);
            contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Setup card layout for ads/map switching
            adMapPanel.add(advertisementPanel.getPanel(), "AdvertisementPanel");
            adMapPanel.add(mapPanel.getPanel(), "MapPanel");

            // Create main content area (left and center)
            JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));
            mainContentPanel.setBackground(BACKGROUND_COLOR);
            mainContentPanel.add(adMapPanel, BorderLayout.CENTER);

            // Create right panel for weather
            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.setBackground(BACKGROUND_COLOR);
            rightPanel.setPreferredSize(new Dimension(230, 0));
            rightPanel.add(weatherPanel.getPanel(), BorderLayout.CENTER);

            // Create bottom panel for news and station info
            JPanel bottomPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Draw background
                    g2.setColor(BACKGROUND_COLOR);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    
                    g2.dispose();
                }
            };
            bottomPanel.setLayout(new BorderLayout(0, 5));
            bottomPanel.setBackground(BACKGROUND_COLOR);
            bottomPanel.setPreferredSize(new Dimension(0, 170));
            
            // Add components to bottom panel
            bottomPanel.add(newsPanel.getPanel(), BorderLayout.NORTH);
            bottomPanel.add(stationInfoPanel.getPanel(), BorderLayout.SOUTH);

            // Assemble the main layout
            contentPanel.add(mainContentPanel, BorderLayout.CENTER);
            contentPanel.add(rightPanel, BorderLayout.EAST);
            contentPanel.add(bottomPanel, BorderLayout.SOUTH);

            mainFrame.setContentPane(contentPanel);
            
            // Add window title bar styling
            try {
                mainFrame.setIconImage(createAppIcon());
            } catch (Exception e) {
                // Icon creation failed, continue without icon
            }
            
            mainFrame.setVisible(true);
        });
    }
    
    /**
     * Creates a simple app icon for the window
     */
    private Image createAppIcon() {
        int size = 16; // Reduced icon size
        Image icon = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) icon.getGraphics();
        
        // Enable antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw simple subway icon (scaled down)
        g2.setColor(new Color(16, 185, 129)); // Emerald
        g2.fillRoundRect(2, 4, size - 4, size - 8, 4, 4);
        
        g2.setColor(new Color(241, 245, 249)); // White
        g2.fillOval(4, 6, 3, 3);
        g2.fillOval(size - 7, 6, 3, 3);
        
        g2.dispose();
        return icon;
    }

    private void startDisplayTimer() {
        displayTimer = new Timer(10000, e -> {
            if (showingAd) {
                cardLayout.show(adMapPanel, "MapPanel");
                advertisementController.pauseAd();
            } else {
                cardLayout.show(adMapPanel, "AdvertisementPanel");
                advertisementController.resumeAd();
            }
            showingAd = !showingAd; // Toggle the card state
        });
        displayTimer.start();
    }

    public StationInfoPanel getStationInfoPanel() {
        return stationInfoPanel;
    }
    
    /**
     * Get the map panel for external updates
     * 
     * @return the MapPanel instance
     */
    public MapPanel getMapPanel() {
        return mapPanel;
    }
}
