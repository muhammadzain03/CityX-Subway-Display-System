/**
 * Train Station Information Display Panel
 * 
 * Displays current train position information with previous, current, and next
 * stations in an organized card-based layout. Features color-coded station
 * indicators and professional typography for clear train status communication.
 * 
 * Key features:
 * - Previous, current, and next station display
 * - Color-coded station status indicators
 * - Card-based layout for each station
 * - Text truncation for long station names
 * - Professional styling and typography
 * - Real-time position updates
 * 
 * @author Subway Screen Development Team
 * @version 2.0
 */
package ca.ucalgary.edu.ensf380.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class StationInfoPanel {
    private final JPanel panel;
    private final JLabel[] stationLabel = new JLabel[5];
    private final String[] stationTypes = {"Previous", "Current", "Next", "Next", "Next"};
    
    // Station name storage
    private String previousStationName = "";
    private String currentStationName = "";
    private String nextStationName = "";
    private String nextStation1Name = "";
    private String nextStation2Name = "";
    
    // Modern color scheme matching the design system
    private static final Color BACKGROUND_COLOR = new Color(15, 23, 42); // Dark slate
    private static final Color CARD_COLOR = new Color(30, 41, 59); // Slate 700
    private static final Color TEXT_PRIMARY = new Color(241, 245, 249); // Slate 100
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184); // Slate 400
    private static final Color CURRENT_STATION_COLOR = new Color(16, 185, 129); // Emerald 500
    private static final Color NEXT_STATION_COLOR = new Color(59, 130, 246); // Blue 500
    private static final Color PREV_STATION_COLOR = new Color(156, 163, 175); // Gray 400
    
    // Typography
    private static final Font STATION_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font TYPE_FONT = new Font("Segoe UI", Font.PLAIN, 10);

    public StationInfoPanel() {
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
                
                // Calculate card dimensions with proper spacing to prevent overlap
                int totalWidth = getWidth();
                int totalHeight = getHeight();
                int margin = 5; // Reduced margin
                int cardSpacing = 4; // Reduced spacing between cards
                
                // Calculate available space for cards
                int availableWidth = totalWidth - 2 * margin;
                int cardWidth = (availableWidth - 4 * cardSpacing) / 5; // 5 cards total
                int cardHeight = totalHeight - 2 * margin;
                
                // Ensure minimum card width to prevent overlap
                if (cardWidth < 80) {
                    cardWidth = 80;
                    cardSpacing = Math.max(2, (availableWidth - 5 * cardWidth) / 4);
                }
                
                // Draw station cards with better positioning
                int currentX = margin;
                
                drawStationCard(g2, currentX, margin, cardWidth, cardHeight, 
                               "Previous", previousStationName, PREV_STATION_COLOR, false);
                currentX += cardWidth + cardSpacing;
                
                drawStationCard(g2, currentX, margin, cardWidth, cardHeight, 
                               "Current", currentStationName, CURRENT_STATION_COLOR, true);
                currentX += cardWidth + cardSpacing;
                
                drawStationCard(g2, currentX, margin, cardWidth, cardHeight, 
                               "Next", nextStationName, NEXT_STATION_COLOR, false);
                currentX += cardWidth + cardSpacing;
                
                drawStationCard(g2, currentX, margin, cardWidth, cardHeight, 
                               "Next", nextStation1Name, NEXT_STATION_COLOR, false);
                currentX += cardWidth + cardSpacing;
                
                drawStationCard(g2, currentX, margin, cardWidth, cardHeight, 
                               "Next", nextStation2Name, NEXT_STATION_COLOR, false);
                
                g2.dispose();
            }
            
            private void drawStationCard(Graphics2D g2, int x, int y, int width, int height, 
                                       String type, String stationName, Color accentColor, boolean isCurrent) {
                
                // Draw card shadow
                g2.setColor(new Color(0, 0, 0, isCurrent ? 120 : 80));
                g2.fill(new RoundRectangle2D.Float(x + 2, y + 2, width, height, 8, 8));
                
                // Draw card background with gradient
                Color cardColor1 = isCurrent ? new Color(51, 65, 85) : CARD_COLOR;
                Color cardColor2 = isCurrent ? new Color(71, 85, 105) : new Color(51, 65, 85);
                
                GradientPaint gradient = new GradientPaint(
                    x, y, cardColor1,
                    x, y + height, cardColor2
                );
                g2.setPaint(gradient);
                g2.fill(new RoundRectangle2D.Float(x, y, width, height, 8, 8));
                
                // Draw accent border
                g2.setStroke(new BasicStroke(isCurrent ? 2f : 1f));
                g2.setColor(isCurrent ? accentColor : new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 150));
                g2.draw(new RoundRectangle2D.Float(x, y, width, height, 8, 8));
                
                // Draw type label at the top
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                g2.setColor(TEXT_SECONDARY);
                FontMetrics typeFm = g2.getFontMetrics();
                String typeText = type.toUpperCase();
                int typeX = x + (width - typeFm.stringWidth(typeText)) / 2;
                g2.drawString(typeText, typeX, y + 15);
                
                // AGGRESSIVE station name cleaning - ONLY show ONE station
                if (stationName != null && !stationName.isEmpty()) {
                    // Clean the station name aggressively
                    String cleanName = stationName.trim();
                    
                    // Remove any commas and take only the first part
                    if (cleanName.contains(",")) {
                        cleanName = cleanName.split(",")[0].trim();
                    }
                    
                    // Remove any extra spaces and take only first word if too long
                    cleanName = cleanName.replaceAll("\\s+", " ");
                    String[] words = cleanName.split(" ");
                    if (words.length > 2) {
                        // If more than 2 words, just take the first 2
                        cleanName = words[0] + " " + words[1];
                    } else if (words.length == 1 && words[0].length() > 12) {
                        // If single word is too long, truncate it
                        cleanName = words[0].substring(0, 10) + "...";
                    }
                    
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    g2.setColor(TEXT_PRIMARY);
                    FontMetrics stationFm = g2.getFontMetrics();
                    
                    // Final truncation if still too long for the card
                    int maxWidth = width - 12; // Leave padding
                    if (stationFm.stringWidth(cleanName) > maxWidth) {
                        cleanName = truncateString(cleanName, stationFm, maxWidth);
                    }
                    
                    // Center the station name in the middle of the card
                    int nameX = x + (width - stationFm.stringWidth(cleanName)) / 2;
                    int nameY = y + height / 2 + stationFm.getAscent() / 2;
                    g2.drawString(cleanName, nameX, nameY);
                }
                
                // Draw accent dot at bottom
                int dotSize = 3;
                int dotX = x + width / 2 - dotSize / 2;
                int dotY = y + height - 10;
                g2.setColor(accentColor);
                g2.fillOval(dotX, dotY, dotSize, dotSize);
            }
            
            private String truncateString(String text, FontMetrics fm, int maxWidth) {
                if (fm.stringWidth(text) <= maxWidth) {
                    return text;
                }
                
                String ellipsis = "...";
                int ellipsisWidth = fm.stringWidth(ellipsis);
                
                for (int i = text.length() - 1; i > 0; i--) {
                    String truncated = text.substring(0, i) + ellipsis;
                    if (fm.stringWidth(truncated) <= maxWidth) {
                        return truncated;
                    }
                }
                
                return ellipsis;
            }
        };
        
        panel.setLayout(null); // Using absolute positioning for precise control
        panel.setPreferredSize(new Dimension(800, 100));
        panel.setBackground(BACKGROUND_COLOR);
        
        // Create station labels with modern styling
        int margin = 8;
        int cardSpacing = 4;
        int totalWidth = 800 - 2 * margin;
        int cardWidth = (totalWidth - 4 * cardSpacing) / 5;
        
        for (int i = 0; i < stationLabel.length; i++) {
            final int index = i;
            stationLabel[i] = new JLabel("", SwingConstants.CENTER) { // Empty text - we use custom painting only
                @Override
                protected void paintComponent(Graphics g) {
                    // Custom painting handled by parent panel - this label is invisible
                    // We don't draw anything here to avoid conflicts
                }
            };
            
            int x = margin + index * (cardWidth + cardSpacing);
            stationLabel[i].setBounds(x, 20, cardWidth, 60);
            stationLabel[i].setOpaque(false);
            stationLabel[i].setVisible(false); // Make invisible - we use custom painting only
            panel.add(stationLabel[i]);
        }
    }

    public JPanel getPanel() {
        return panel;
    }

    /**
     * Updates the station labels to display the previous, current, and next stations.
     *
     * @param prev the previous station name
     * @param curr the current station name
     * @param next the next station name
     * @param next1 the station after the next
     * @param next2 the station two steps after the next
     */
    public void updateTrainPosition(String prev, String curr, String next, String next1, String next2) {
        // Store station names for painting with aggressive cleaning
        previousStationName = cleanStationNameAggressive(prev);
        currentStationName = cleanStationNameAggressive(curr);
        nextStationName = cleanStationNameAggressive(next);
        nextStation1Name = cleanStationNameAggressive(next1);
        nextStation2Name = cleanStationNameAggressive(next2);
        
        // DON'T update JLabel text - we use custom painting only
        // The JLabels are invisible and only used for compatibility
        
        panel.repaint(); // Trigger repaint to show updated station names
    }
    
    private String cleanStationName(String stationName) {
        if (stationName == null || stationName.trim().isEmpty()) {
            return "Unknown";
        }
        return stationName.trim().replace(" Station", "");
    }

    private String cleanStationNameAggressive(String stationName) {
        if (stationName == null || stationName.trim().isEmpty()) {
            return "Unknown";
        }
        
        String cleaned = stationName.trim();
        
        // Remove any commas and take only the first part
        if (cleaned.contains(",")) {
            cleaned = cleaned.split(",")[0].trim();
        }
        
        // Remove "Station" suffix if present
        cleaned = cleaned.replace(" Station", "").trim();
        
        // If multiple words, limit to 2 words max
        String[] words = cleaned.split("\\s+");
        if (words.length > 2) {
            cleaned = words[0] + " " + words[1];
        } else if (words.length == 1 && words[0].length() > 15) {
            cleaned = words[0].substring(0, 12) + "...";
        }
        
        return cleaned;
    }
}
