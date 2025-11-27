/**
 * Weather and Time Display Panel
 * 
 * Custom-painted panel that displays current time and weather information in
 * an elegant card-based layout. Features modern styling with gradients, shadows,
 * and professional typography for an attractive weather display.
 * 
 * Key features:
 * - Real-time clock display with large, readable fonts
 * - Weather information with formatted layout
 * - Card-based design with rounded corners and shadows
 * - Professional color scheme and typography
 * - Responsive layout and proper text formatting
 * 
 * @author Subway Screen Development Team
 * @version 2.0
 */
package ca.ucalgary.edu.ensf380.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherPanel {
    private final JPanel panel;
    private final JLabel timeLabel;
    private final JLabel weatherLabel;
    private final Timer timer;
    
    // Modern color scheme matching MapPanel
    private static final Color BACKGROUND_COLOR = new Color(15, 23, 42); // Dark slate
    private static final Color CARD_COLOR = new Color(30, 41, 59); // Slate 700
    private static final Color TEXT_PRIMARY = new Color(241, 245, 249); // Slate 100
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184); // Slate 400
    private static final Color ACCENT_COLOR = new Color(59, 130, 246); // Blue 500
    
    // Typography
    private static final Font TIME_FONT = new Font("Segoe UI", Font.BOLD, 32);
    private static final Font WEATHER_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 12);

    public WeatherPanel() {
        this.panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                
                // Enable high-quality rendering
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Draw background
                g2.setColor(BACKGROUND_COLOR);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw time card
                drawTimeCard(g2);
                
                // Draw weather card
                drawWeatherCard(g2);
                
                g2.dispose();
            }
            
            private void drawTimeCard(Graphics2D g2) {
                int cardHeight = 120;
                int margin = 15;
                int cardWidth = getWidth() - 2 * margin;
                
                // Draw shadow
                g2.setColor(new Color(0, 0, 0, 100));
                g2.fill(new RoundRectangle2D.Float(margin + 3, margin + 3, cardWidth, cardHeight, 15, 15));
                
                // Draw card background with gradient
                GradientPaint gradient = new GradientPaint(
                    margin, margin, CARD_COLOR,
                    margin, margin + cardHeight, new Color(51, 65, 85)
                );
                g2.setPaint(gradient);
                g2.fill(new RoundRectangle2D.Float(margin, margin, cardWidth, cardHeight, 15, 15));
                
                // Draw card border
                g2.setStroke(new BasicStroke(1f));
                g2.setColor(new Color(71, 85, 105));
                g2.draw(new RoundRectangle2D.Float(margin, margin, cardWidth, cardHeight, 15, 15));
                
                // Draw "CURRENT TIME" label at top with proper spacing - FIXED!
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(TEXT_SECONDARY);
                FontMetrics headerFm = g2.getFontMetrics();
                String timeLabelText = "CURRENT TIME"; // PROPERLY SPACED!
                int timeLabelX = (getWidth() - headerFm.stringWidth(timeLabelText)) / 2;
                g2.drawString(timeLabelText, timeLabelX, margin + 20);
                
                // Draw accent line BELOW the label with proper spacing
                int accentY = margin + 32;
                g2.setColor(ACCENT_COLOR);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(margin + 15, accentY, margin + cardWidth - 15, accentY);
                
                // Draw time text with proper spacing below accent line
                g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
                g2.setColor(TEXT_PRIMARY);
                FontMetrics timeFm = g2.getFontMetrics();
                String timeText = currentTime;
                int timeX = (getWidth() - timeFm.stringWidth(timeText)) / 2;
                int timeY = margin + 78;
                g2.drawString(timeText, timeX, timeY);
            }
            
            private void drawWeatherCard(Graphics2D g2) {
                int cardTop = 140;
                int cardHeight = 200;
                int margin = 15;
                int cardWidth = getWidth() - 2 * margin;
                
                // Draw shadow
                g2.setColor(new Color(0, 0, 0, 100));
                g2.fill(new RoundRectangle2D.Float(margin + 3, cardTop + 3, cardWidth, cardHeight, 15, 15));
                
                // Draw card background with gradient
                GradientPaint gradient = new GradientPaint(
                    margin, cardTop, CARD_COLOR,
                    margin, cardTop + cardHeight, new Color(51, 65, 85)
                );
                g2.setPaint(gradient);
                g2.fill(new RoundRectangle2D.Float(margin, cardTop, cardWidth, cardHeight, 15, 15));
                
                // Draw card border
                g2.setStroke(new BasicStroke(1f));
                g2.setColor(new Color(71, 85, 105));
                g2.draw(new RoundRectangle2D.Float(margin, cardTop, cardWidth, cardHeight, 15, 15));
                
                // Draw "WEATHER" header
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(TEXT_SECONDARY);
                FontMetrics headerFm = g2.getFontMetrics();
                String weatherHeader = "WEATHER";
                int headerX = (getWidth() - headerFm.stringWidth(weatherHeader)) / 2;
                g2.drawString(weatherHeader, headerX, cardTop + 20);
                
                // Draw accent line
                int accentY = cardTop + 32;
                g2.setColor(new Color(16, 185, 129));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(margin + 15, accentY, margin + cardWidth - 15, accentY);
                
                // Draw weather icon
                g2.setColor(new Color(16, 185, 129));
                g2.fillOval(margin + cardWidth/2 - 10, cardTop + 45, 20, 20);
                
                // Draw weather text in a cleaner format
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(TEXT_PRIMARY);
                
                if (weatherText != null && !weatherText.trim().isEmpty()) {
                    // Parse and format weather info better
                    String[] lines = weatherText.split("<br>");
                    int lineY = cardTop + 85;
                    int lineHeight = 14;
                    
                    for (String line : lines) {
                        line = line.replaceAll("<[^>]*>", "").trim(); // Remove HTML tags
                        if (!line.isEmpty() && lineY < cardTop + cardHeight - 10) {
                            int textX = margin + 10;
                            // Wrap long lines
                            if (g2.getFontMetrics().stringWidth(line) > cardWidth - 20) {
                                String[] words = line.split(" ");
                                StringBuilder currentLine = new StringBuilder();
                                for (String word : words) {
                                    if (g2.getFontMetrics().stringWidth(currentLine + " " + word) > cardWidth - 20) {
                                        if (currentLine.length() > 0) {
                                            g2.drawString(currentLine.toString(), textX, lineY);
                                            lineY += lineHeight;
                                            currentLine = new StringBuilder(word);
                                        }
                                    } else {
                                        if (currentLine.length() > 0) currentLine.append(" ");
                                        currentLine.append(word);
                                    }
                                }
                                if (currentLine.length() > 0 && lineY < cardTop + cardHeight - 10) {
                                    g2.drawString(currentLine.toString(), textX, lineY);
                                    lineY += lineHeight;
                                }
                            } else {
                                g2.drawString(line, textX, lineY);
                                lineY += lineHeight;
                            }
                        }
                    }
                }
            }
        };
        
        panel.setLayout(null); // Using absolute positioning for precise control
        panel.setPreferredSize(new Dimension(220, 450));
        panel.setBackground(BACKGROUND_COLOR);

        // Create INVISIBLE time and weather labels ONLY for compatibility - they won't be shown
        this.timeLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timeLabel.setVisible(false); // Make invisible - we use custom painting
        
        this.weatherLabel = new JLabel("<html>Location: Calgary<br>Status: Loading...<br>Connecting to weather service</html>", SwingConstants.LEFT);
        weatherLabel.setVisible(false); // Make invisible - we use custom painting

        // Initialize time immediately, then start timer for updates
        updateTime(); // Set initial time
        this.timer = new Timer(1000, e -> updateTime());
        timer.start();
    }

    public JPanel getPanel() {
        return panel;
    }

    /**
     * Updates the time displayed on the time label.
     */
    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        currentTime = now.format(formatter);
        timeLabel.setText(currentTime);
        panel.repaint(); // FIXED! Trigger custom painting to update the display
    }

    /**
     * Updates the weather information displayed on the weather label.
     *
     * @param weatherInfo the weather information to display
     */
    public void updateWeatherLabel(String weatherInfo) {
        // Store weather text for custom painting
        weatherText = weatherInfo != null ? weatherInfo : "Loading weather data...";
        
        // Update invisible label for compatibility (not displayed)
        weatherLabel.setText(weatherText);
        
        // Trigger repaint to show updated weather
        panel.repaint();
    }
    
    private String currentTime = "00:00:00";
    private String weatherText = "<html>Location: Calgary<br>Status: Loading...<br>Connecting to weather service</html>";
    

}
