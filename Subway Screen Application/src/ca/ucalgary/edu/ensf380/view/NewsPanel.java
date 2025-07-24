/**
 * News panel that displays live news headlines with custom painting for modern design.
 * Features scrolling text, gradient backgrounds, and professional styling.
 */
package ca.ucalgary.edu.ensf380.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class NewsPanel {
    // Modern color scheme matching other panels
    private static final Color BACKGROUND_COLOR = new Color(30, 41, 59);
    private static final Color CARD_COLOR = new Color(51, 65, 85);
    private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private static final Color ACCENT_COLOR = new Color(239, 68, 68);
    
    private final JPanel panel;
    private JLabel newsLabel;
    private String currentNews = "Welcome to CityX Subway - Your reliable transit solution • All lines operating smoothly • Download our mobile app for real-time updates";
    private Timer scrollTimer;
    private int scrollPosition = 0;

    public NewsPanel() {
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Fill background
                g2.setColor(BACKGROUND_COLOR);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw modern news card
                drawNewsCard(g2);
                
                g2.dispose();
            }
            
            private void drawNewsCard(Graphics2D g2) {
                int margin = 8;
                int cardHeight = getHeight() - 2 * margin;
                int cardWidth = getWidth() - 2 * margin;
                
                // Draw shadow
                g2.setColor(new Color(0, 0, 0, 120));
                g2.fill(new RoundRectangle2D.Float(margin + 2, margin + 2, cardWidth, cardHeight, 8, 8));
                
                // Draw card background with gradient
                GradientPaint gradient = new GradientPaint(
                    margin, margin, CARD_COLOR,
                    margin, margin + cardHeight, new Color(71, 85, 105)
                );
                g2.setPaint(gradient);
                g2.fill(new RoundRectangle2D.Float(margin, margin, cardWidth, cardHeight, 8, 8));
                
                // Draw card border
                g2.setStroke(new BasicStroke(1f));
                g2.setColor(new Color(71, 85, 105));
                g2.draw(new RoundRectangle2D.Float(margin, margin, cardWidth, cardHeight, 8, 8));
                
                // Draw "LIVE NEWS" header with red dot
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.setColor(TEXT_SECONDARY);
                FontMetrics headerFm = g2.getFontMetrics();
                
                // Red live indicator dot
                g2.setColor(ACCENT_COLOR);
                g2.fillOval(margin + 10, margin + 8, 6, 6);
                
                // "LIVE NEWS" text
                g2.setColor(TEXT_SECONDARY);
                g2.drawString("LIVE NEWS", margin + 22, margin + 15);
                
                // Draw news content with RIGHT TO LEFT scrolling (as user prefers)
                if (currentNews != null && !currentNews.trim().isEmpty()) {
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    g2.setColor(TEXT_PRIMARY);
                    FontMetrics newsFm = g2.getFontMetrics();
                    
                    // Clean the news text
                    String cleanNews = currentNews.replaceAll("<[^>]*>", "").trim();
                    if (cleanNews.length() > 500) {
                        cleanNews = cleanNews.substring(0, 497) + "...";
                    }
                    
                    // Calculate text width and available space
                    int textWidth = newsFm.stringWidth(cleanNews);
                    int availableWidth = cardWidth - 20;
                    
                    if (textWidth > availableWidth) {
                        // RIGHT TO LEFT scrolling - start from right side, move left
                        int scrollX = cardWidth - scrollPosition;
                        
                        // Reset when text completely exits left side
                        if (scrollX + textWidth < margin) {
                            scrollPosition = 0; // Reset scroll to start from right again
                        }
                        
                        // Create clipping area to prevent text from showing outside card
                        Shape oldClip = g2.getClip();
                        g2.setClip(margin + 5, margin + 20, cardWidth - 10, 20);
                        
                        g2.drawString(cleanNews, scrollX, margin + 35);
                        g2.setClip(oldClip); // Restore original clip
                    } else {
                        // Center the text if it fits completely
                        int textX = margin + (cardWidth - textWidth) / 2;
                        g2.drawString(cleanNews, textX, margin + 35);
                    }
                }
            }
        };
        
        panel.setPreferredSize(new Dimension(800, 50));
        panel.setBackground(BACKGROUND_COLOR);
        
        // Create invisible label for compatibility
        newsLabel = new JLabel("Welcome to CityX Subway", SwingConstants.CENTER);
        newsLabel.setVisible(false);
        panel.add(newsLabel);
        
        // Start RIGHT TO LEFT scrolling timer immediately (faster movement)
        scrollTimer = new Timer(50, e -> { // Faster timer (50ms instead of 100ms)
            scrollPosition += 3; // Move 3 pixels per frame for smoother, faster scrolling
            panel.repaint();
        });
        scrollTimer.start(); // Animation starts immediately with initial content
    }

    public JPanel getPanel() {
        return panel;
    }

    public void updateNewsLabel(String news) {
        if (news != null && !news.trim().isEmpty()) {
            currentNews = news;
            newsLabel.setText(news);
            scrollPosition = 0; // Reset scroll when news updates
            panel.repaint();
        }
    }
}
