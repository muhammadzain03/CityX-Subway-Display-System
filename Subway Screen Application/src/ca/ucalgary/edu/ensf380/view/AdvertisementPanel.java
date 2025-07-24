/**
 * Enhanced Advertisement Display Panel
 * 
 * Sophisticated multimedia panel for displaying rotating advertisements with
 * advanced transition effects, loading animations, and professional styling.
 * Features multiple transition types, progress indicators, and smooth scaling
 * effects for an engaging advertisement experience.
 * 
 * Key features:
 * - Multiple transition effects (fade, scale in/out)
 * - Loading animations with progress indicators
 * - Professional framed layout with shadows
 * - High-quality image rendering with bicubic interpolation
 * - Dynamic card styling with animated accents
 * - Responsive design and scaling
 * 
 * @author Subway Screen Development Team
 * @version 2.0
 */
package ca.ucalgary.edu.ensf380.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.AffineTransform;
import java.util.Map;
import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;

public class AdvertisementPanel {
    private static final String BASE_PATH = "advertisements/";
    private final JPanel panel;
    private final JLabel adLabel;
    private float alpha = 1.0f;
    private Timer fadeTimer;
    private Timer loadingTimer;
    private ImageIcon currentIcon = null;
    private ImageIcon nextIcon = null;
    private boolean fadingIn = true;
    private boolean isLoading = false;
    private float loadingProgress = 0.0f;
    private float scaleAnimation = 1.0f;
    private int transitionType = 0; // 0: fade, 1: slide, 2: scale
    
    // Modern color scheme matching the design system
    private static final Color BACKGROUND_COLOR = new Color(15, 23, 42); // Dark slate
    private static final Color CARD_COLOR = new Color(30, 41, 59); // Slate 700
    private static final Color TEXT_PRIMARY = new Color(241, 245, 249); // Slate 100
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184); // Slate 400
    private static final Color ACCENT_COLOR = new Color(16, 185, 129); // Emerald 500
    private static final Color LOADING_COLOR = new Color(59, 130, 246); // Blue 500
    
    // Typography
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font STATUS_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public AdvertisementPanel() {
        this.panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                
                // Enable high-quality rendering
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                // Draw background
                g2.setColor(BACKGROUND_COLOR);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw advertisement frame
                drawAdFrame(g2);
                
                // Draw loading indicator if loading
                if (isLoading) {
                    drawLoadingIndicator(g2);
                } else if (currentIcon != null) {
                    // Draw advertisement content with enhanced transitions
                    drawEnhancedAdvertisement(g2);
                }
                
                g2.dispose();
            }
            
            private void drawAdFrame(Graphics2D g2) {
                int margin = 10;
                int cardWidth = getWidth() - 2 * margin;
                int cardHeight = getHeight() - 2 * margin;
                
                // Draw shadow with animation
                int shadowOffset = isLoading ? 6 : 4;
                g2.setColor(new Color(0, 0, 0, isLoading ? 160 : 120));
                g2.fill(new RoundRectangle2D.Float(margin + shadowOffset, margin + shadowOffset, cardWidth, cardHeight, 15, 15));
                
                // Draw card background with enhanced gradient
                Color cardColor1 = isLoading ? new Color(51, 65, 85) : CARD_COLOR;
                Color cardColor2 = isLoading ? new Color(71, 85, 105) : new Color(51, 65, 85);
                
                GradientPaint gradient = new GradientPaint(
                    margin, margin, cardColor1,
                    margin, margin + cardHeight, cardColor2
                );
                g2.setPaint(gradient);
                g2.fill(new RoundRectangle2D.Float(margin, margin, cardWidth, cardHeight, 15, 15));
                
                // Draw animated border
                g2.setStroke(new BasicStroke(2f));
                Color borderColor = isLoading ? LOADING_COLOR : new Color(71, 85, 105);
                g2.setColor(borderColor);
                g2.draw(new RoundRectangle2D.Float(margin, margin, cardWidth, cardHeight, 15, 15));
                
                // Draw header area
                g2.setColor(new Color(51, 65, 85));
                g2.fill(new RoundRectangle2D.Float(margin, margin, cardWidth, 40, 15, 15));
                g2.fillRect(margin, margin + 25, cardWidth, 15); // Fill bottom corners
                
                // Draw "ADVERTISEMENT" header
                g2.setFont(HEADER_FONT);
                g2.setColor(TEXT_SECONDARY);
                g2.drawString("ADVERTISEMENT", margin + 15, margin + 25);
                
                // Draw status indicator
                Color indicatorColor = isLoading ? LOADING_COLOR : ACCENT_COLOR;
                if (isLoading) {
                    // Animated loading indicator
                    float pulse = 0.5f + 0.5f * (float)Math.sin(System.currentTimeMillis() * 0.01);
                    g2.setColor(new Color(indicatorColor.getRed(), indicatorColor.getGreen(), indicatorColor.getBlue(), (int)(pulse * 255)));
                } else {
                    g2.setColor(indicatorColor);
                }
                g2.fillOval(getWidth() - margin - 25, margin + 12, 8, 8);
                
                // Draw accent line with animation
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(indicatorColor);
                
                if (isLoading) {
                    // Animated progress line
                    int lineWidth = (int)((getWidth() - 2 * margin - 30) * loadingProgress);
                    g2.drawLine(margin + 15, margin + 35, margin + 15 + lineWidth, margin + 35);
                } else {
                    g2.drawLine(margin + 15, margin + 35, getWidth() - margin - 15, margin + 35);
                }
            }
            
            private void drawLoadingIndicator(Graphics2D g2) {
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                // Draw loading spinner
                g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                int spinnerRadius = 30;
                double angle = System.currentTimeMillis() * 0.01;
                
                for (int i = 0; i < 8; i++) {
                    double a = angle + i * Math.PI / 4;
                    float alpha = (float)(0.3 + 0.7 * (7 - i) / 7.0);
                    
                    g2.setColor(new Color(LOADING_COLOR.getRed(), LOADING_COLOR.getGreen(), LOADING_COLOR.getBlue(), (int)(alpha * 255)));
                    
                    int x1 = centerX + (int)(Math.cos(a) * (spinnerRadius - 10));
                    int y1 = centerY + (int)(Math.sin(a) * (spinnerRadius - 10));
                    int x2 = centerX + (int)(Math.cos(a) * spinnerRadius);
                    int y2 = centerY + (int)(Math.sin(a) * spinnerRadius);
                    
                    g2.drawLine(x1, y1, x2, y2);
                }
                
                // Draw loading text
                g2.setFont(STATUS_FONT);
                g2.setColor(TEXT_SECONDARY);
                String loadingText = "Loading advertisement...";
                FontMetrics fm = g2.getFontMetrics();
                int textX = centerX - fm.stringWidth(loadingText) / 2;
                int textY = centerY + 60;
                g2.drawString(loadingText, textX, textY);
            }
            
            private void drawEnhancedAdvertisement(Graphics2D g2) {
                int margin = 10;
                int headerHeight = 40;
                int contentMargin = 15; // Reduced back to smaller margin for larger ads
                
                // Create clipping area for advertisement content (larger area)
                int clipX = margin + contentMargin;
                int clipY = margin + headerHeight + contentMargin;
                int clipWidth = getWidth() - 2 * (margin + contentMargin);
                int clipHeight = getHeight() - 2 * margin - headerHeight - 2 * contentMargin;
                
                Shape oldClip = g2.getClip();
                g2.setClip(clipX, clipY, clipWidth, clipHeight);
                
                // Apply enhanced transition effects
                AffineTransform oldTransform = g2.getTransform();
                
                // Scale animation effect
                if (scaleAnimation != 1.0f) {
                    double centerX = clipX + clipWidth / 2.0;
                    double centerY = clipY + clipHeight / 2.0;
                    g2.translate(centerX, centerY);
                    g2.scale(scaleAnimation, scaleAnimation);
                    g2.translate(-centerX, -centerY);
                }
                
                // Apply fade effect
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                
                // Calculate image position and scaling to fill the entire area
                int imgWidth = currentIcon.getIconWidth();
                int imgHeight = currentIcon.getIconHeight();
                
                // Full scaling to cover entire advertisement area
                double scaleX = (double) clipWidth / imgWidth;
                double scaleY = (double) clipHeight / imgHeight;
                double scale = Math.min(scaleX, scaleY);
                
                int scaledWidth = (int) (imgWidth * scale);
                int scaledHeight = (int) (imgHeight * scale);
                
                int x = clipX + (clipWidth - scaledWidth) / 2;
                int y = clipY + (clipHeight - scaledHeight) / 2;
                
                // Draw image with enhanced quality
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.drawImage(currentIcon.getImage(), x, y, scaledWidth, scaledHeight, panel);
                
                // Restore transform and composite
                g2.setTransform(oldTransform);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2.setClip(oldClip);
            }
        };
        
        this.adLabel = new JLabel("Initializing advertisements...", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Enhanced status text styling
                g2.setFont(STATUS_FONT);
                g2.setColor(TEXT_SECONDARY);
                FontMetrics fm = g2.getFontMetrics();
                String text = getText();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = getHeight() / 2 + fm.getAscent() / 2;
                
                // Add text shadow for better readability
                g2.setColor(new Color(0, 0, 0, 100));
                g2.drawString(text, x + 1, y + 1);
                g2.setColor(TEXT_SECONDARY);
                g2.drawString(text, x, y);
                
                g2.dispose();
            }
        };
        
        adLabel.setOpaque(false);
        panel.add(adLabel, BorderLayout.CENTER);
        panel.setBackground(BACKGROUND_COLOR);
    }

    public JPanel getPanel() {
        return panel;
    }

    /**
     * Displays the advertisement on the panel with enhanced transitions.
     *
     * @param ad a Map containing advertisement details, or null to clear the display
     */
    public void displayAdvertisement(Map<String, Object> ad) {
        if (ad == null) {
            currentIcon = null;
            adLabel.setText("No advertisements available");
            adLabel.setVisible(true);
            panel.repaint();
            return;
        }

        String mediaType = (String) ad.get("media_type");
        String mediaPath = BASE_PATH + ad.get("media_path");

        if (mediaType.equals("GIF") || mediaType.equals("JPEG") || mediaType.equals("BMP")) {
            // Show loading state
            startLoadingAnimation();
            
            // Load image in background to simulate realistic loading
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(500); // Simulate loading time
                    nextIcon = new ImageIcon(mediaPath);
                    SwingUtilities.invokeLater(() -> {
                        stopLoadingAnimation();
                        adLabel.setVisible(false);
                        startEnhancedTransition();
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        } else {
            currentIcon = null;
            adLabel.setText("Unsupported media: " + mediaType);
            adLabel.setVisible(true);
            panel.repaint();
        }
    }
    
    private void startLoadingAnimation() {
        isLoading = true;
        loadingProgress = 0.0f;
        
        if (loadingTimer != null) loadingTimer.stop();
        loadingTimer = new Timer(20, e -> {
            loadingProgress += 0.02f;
            if (loadingProgress >= 1.0f) {
                loadingProgress = 1.0f;
            }
            panel.repaint();
        });
        loadingTimer.start();
    }
    
    private void stopLoadingAnimation() {
        isLoading = false;
        if (loadingTimer != null) {
            loadingTimer.stop();
        }
    }

    private void startEnhancedTransition() {
        if (fadeTimer != null && fadeTimer.isRunning()) fadeTimer.stop();
        
        // Randomly select transition type for variety
        transitionType = (int)(Math.random() * 3);
        
        fadingIn = false;
        alpha = 1.0f;
        scaleAnimation = 1.0f;
        
        fadeTimer = new Timer(16, e -> { // 60 FPS for smooth animation
            if (!fadingIn) {
                // Fade out current image with effects
                alpha -= 0.06f;
                
                switch (transitionType) {
                    case 1: // Scale out
                        scaleAnimation += 0.02f;
                        break;
                    case 2: // Scale in
                        scaleAnimation -= 0.03f;
                        break;
                }
                
                if (alpha <= 0.0f) {
                    alpha = 0.0f;
                    currentIcon = nextIcon;
                    fadingIn = true;
                    scaleAnimation = transitionType == 2 ? 1.2f : 0.8f; // Start scale for fade in
                }
            } else {
                // Fade in new image with effects
                alpha += 0.06f;
                
                switch (transitionType) {
                    case 1: // Scale normalize
                        scaleAnimation = Math.max(1.0f, scaleAnimation - 0.02f);
                        break;
                    case 2: // Scale normalize
                        scaleAnimation = Math.min(1.0f, scaleAnimation + 0.03f);
                        break;
                    default: // Just fade
                        scaleAnimation = 1.0f;
                        break;
                }
                
                if (alpha >= 1.0f && Math.abs(scaleAnimation - 1.0f) < 0.01f) {
                    alpha = 1.0f;
                    scaleAnimation = 1.0f;
                    fadeTimer.stop();
                }
            }
            adLabel.setVisible(false);
            panel.repaint();
        });
        fadeTimer.start();
    }
}
