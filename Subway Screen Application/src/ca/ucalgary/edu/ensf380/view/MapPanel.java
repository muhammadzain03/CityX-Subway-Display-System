/**
 * Interactive Subway Map Display Panel
 * 
 * Advanced map visualization component that renders the subway system with
 * real-time train positions, interactive station elements, and smooth animations.
 * Features modern graphics with hover effects, click interactions, and dynamic
 * train movement with visual feedback.
 * 
 * Key features:
 * - Custom graphics rendering with Graphics2D
 * - Real-time train position animation with easing
 * - Interactive station tooltips and details
 * - Hover effects and click handling
 * - Professional visual styling with shadows and gradients
 * - Responsive design and scaling
 * 
 * @author Subway Screen Development Team
 * @version 2.0
 */
package ca.ucalgary.edu.ensf380.view;

import javax.swing.*;
import ca.ucalgary.edu.ensf380.controller.ReadSimulatorOutput;
import ca.ucalgary.edu.ensf380.model.Station;
import ca.ucalgary.edu.ensf380.model.Train;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.Timer;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.List;

public class MapPanel extends JPanel {
    private ArrayList<Station> stations;
    private ArrayList<Train> trainInfo;
    private final String trainNum;
    private Point mousePoint = null;
    private Station hoveredStation = null;
    private Station clickedStation = null;
    private java.util.List<AnimatedTrain> animatedTrains = new ArrayList<>();
    private Timer animationTimer;
    private Timer hoverAnimationTimer;
    private float hoverPulse = 0.0f;
    private boolean showStationDetails = false;

    // Scaling and centering variables
    private double minX, maxX, minY, maxY, scale;
    private int offsetX, offsetY;
    
    // Modern color scheme
    private static final Color BACKGROUND_COLOR = new Color(15, 23, 42); // Dark slate
    private static final Color GRID_COLOR = new Color(30, 41, 59, 80); // Subtle grid
    private static final Color TEXT_COLOR = new Color(241, 245, 249); // Light text
    
    // Line colors - modern and vibrant
    private static final Color RED_LINE = new Color(239, 68, 68); // Modern red
    private static final Color GREEN_LINE = new Color(34, 197, 94); // Modern green  
    private static final Color BLUE_LINE = new Color(59, 130, 246); // Modern blue
    
    // Station colors
    private static final Color REGULAR_STATION = new Color(241, 245, 249); // White
    private static final Color TRANSFER_STATION = new Color(255, 193, 7); // Amber
    private static final Color TERMINAL_STATION = new Color(239, 68, 68); // Red
    private static final Color TRAIN_COLOR = new Color(16, 185, 129); // Emerald
    
    // Typography
    private static final Font STATION_FONT = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font TOOLTIP_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    public MapPanel(ArrayList<Station> stations, String trainNumber) {
        this.stations = stations;
        this.trainNum = trainNumber;
        setBackground(BACKGROUND_COLOR);
        setTrains();
        
        // Animation timer for smooth train movement
        animationTimer = new Timer(16, e -> animateTrains()); // ~60 FPS
        animationTimer.start();
        
        // Hover animation timer
        hoverAnimationTimer = new Timer(50, e -> {
            hoverPulse += 0.2f;
            if (hoverPulse > Math.PI * 2) hoverPulse = 0.0f;
            if (hoveredStation != null) repaint();
        });
        hoverAnimationTimer.start();
        
        // Enhanced mouse interaction
        addMouseMotionListener(new MouseMotionListener() {
            public void mouseMoved(MouseEvent e) {
                mousePoint = e.getPoint();
                Station newHovered = getStationAtPoint(mousePoint);
                
                if (newHovered != hoveredStation) {
                    hoveredStation = newHovered;
                    setCursor(hoveredStation != null ? 
                        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : 
                        Cursor.getDefaultCursor());
                    repaint();
                }
            }
            public void mouseDragged(MouseEvent e) {}
        });
        
        // Add click listener for station interactions
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Station clicked = getStationAtPoint(e.getPoint());
                if (clicked != null) {
                    handleStationClick(clicked);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredStation = null;
                setCursor(Cursor.getDefaultCursor());
                repaint();
            }
        });
    }

    private void animateTrains() {
        // Move each animated train toward its target
        for (AnimatedTrain t : animatedTrains) {
            t.updatePosition();
        }
        repaint();
    }

    private void computeMapTransform() {
        if (stations == null || stations.isEmpty()) return;
        minX = Double.MAX_VALUE; maxX = -Double.MAX_VALUE;
        minY = Double.MAX_VALUE; maxY = -Double.MAX_VALUE;
        for (Station s : stations) {
            minX = Math.min(minX, s.getX());
            maxX = Math.max(maxX, s.getX());
            minY = Math.min(minY, s.getY());
            maxY = Math.max(maxY, s.getY());
        }
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        double scaleX = panelWidth / (maxX - minX + 1e-6);
        double scaleY = panelHeight / (maxY - minY + 1e-6);
        scale = Math.min(scaleX, scaleY) * 0.85; // 0.85 to add margin
        offsetX = (int) ((panelWidth - (maxX - minX) * scale) / 2);
        offsetY = (int) ((panelHeight - (maxY - minY) * scale) / 2);
    }

    private int sx(double csvX) { return (int) ((csvX - minX) * scale) + offsetX; }
    private int sy(double csvY) { return (int) ((csvY - minY) * scale) + offsetY; }

    // Build line-to-stations map in correct order
    private HashMap<String, java.util.List<Station>> buildLinesByOrder() {
        HashMap<String, java.util.List<Station>> lines = new HashMap<>();
        // Build ordered lists for each line (R, G, B)
        for (Station s : stations) {
            String line = s.getCode().substring(0, 1);
            lines.computeIfAbsent(line, k -> new ArrayList<>()).add(s);
        }
        // Sort each line's stations by their number with consistent comparison
        for (java.util.List<Station> lineStations : lines.values()) {
            lineStations.sort((a, b) -> {
                String numA = a.getNumber() != null ? a.getNumber() : "";
                String numB = b.getNumber() != null ? b.getNumber() : "";
                
                // Check if both are numeric
                boolean aIsNumeric = isNumeric(numA);
                boolean bIsNumeric = isNumeric(numB);
                
                if (aIsNumeric && bIsNumeric) {
                    // Both are numeric - compare as integers
                    return Integer.compare(Integer.parseInt(numA), Integer.parseInt(numB));
                } else if (aIsNumeric && !bIsNumeric) {
                    // Only A is numeric - numeric comes before non-numeric
                    return -1;
                } else if (!aIsNumeric && bIsNumeric) {
                    // Only B is numeric - numeric comes before non-numeric
                    return 1;
                } else {
                    // Both are non-numeric - compare as strings
                    return numA.compareTo(numB);
                }
            });
        }
        return lines;
    }
    
    /**
     * Helper method to check if a string is numeric
     */
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        computeMapTransform();
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Enable high-quality rendering
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        // Draw background
        g2.setColor(BACKGROUND_COLOR);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw subtle grid
        drawGrid(g2);

        // Draw subway lines with modern styling
        drawSubwayLines(g2);
        
        // Draw stations with proper hierarchy
        drawStations(g2);

        // Draw animated trains
        drawTrains(g2);

        // Draw tooltip if hovering over a station
        if (hoveredStation != null) {
            drawTooltip(g2, hoveredStation);
        }
        
        g2.dispose();
    }
    
    private void drawGrid(Graphics2D g2) {
        g2.setColor(GRID_COLOR);
        g2.setStroke(new BasicStroke(0.5f));
        
        int gridSize = 50;
        for (int x = 0; x < getWidth(); x += gridSize) {
            g2.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y < getHeight(); y += gridSize) {
            g2.drawLine(0, y, getWidth(), y);
        }
    }
    
    private void drawSubwayLines(Graphics2D g2) {
        HashMap<String, java.util.List<Station>> lines = buildLinesByOrder();
        
        for (String line : lines.keySet()) {
            java.util.List<Station> lineStations = lines.get(line);
            if (lineStations.size() > 1) {
                // Create path for the line
                int[] xs = new int[lineStations.size()];
                int[] ys = new int[lineStations.size()];
                for (int i = 0; i < lineStations.size(); i++) {
                    xs[i] = sx(lineStations.get(i).getX());
                    ys[i] = sy(lineStations.get(i).getY());
                }
                
                // Draw line shadow for depth
                g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(0, 0, 0, 50));
                for (int i = 0; i < xs.length - 1; i++) {
                    g2.drawLine(xs[i] + 2, ys[i] + 2, xs[i + 1] + 2, ys[i + 1] + 2);
                }
                
                // Draw main line
                g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(getModernLineColor(line));
                for (int i = 0; i < xs.length - 1; i++) {
                    g2.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);
                }
            }
        }
    }
    
    private void drawStations(Graphics2D g2) {
        HashMap<String, java.util.List<Station>> lines = buildLinesByOrder();
        
        // Build station type maps
        HashMap<String, Integer> stationLineCount = new HashMap<>();
        for (String line : lines.keySet()) {
            for (Station s : lines.get(line)) {
                stationLineCount.put(s.getCode(), stationLineCount.getOrDefault(s.getCode(), 0) + 1);
            }
        }
        
        // Find terminals (first/last station on each line)
        HashMap<String, Boolean> isTerminal = new HashMap<>();
        for (String line : lines.keySet()) {
            java.util.List<Station> lineStations = lines.get(line);
            if (!lineStations.isEmpty()) {
                isTerminal.put(lineStations.get(0).getCode(), true);
                isTerminal.put(lineStations.get(lineStations.size() - 1).getCode(), true);
            }
        }

        // Draw stations with shadows and modern styling
        for (Station station : stations) {
            int x = sx(station.getX());
            int y = sy(station.getY());
            String code = station.getCode();
            boolean isTransfer = stationLineCount.getOrDefault(code, 0) > 1;
            boolean terminal = isTerminal.getOrDefault(code, false);
            
            drawModernStation(g2, x, y, station, isTransfer, terminal);
        }
    }
    
    private void drawModernStation(Graphics2D g2, int x, int y, Station station, boolean isTransfer, boolean isTerminal) {
        int size = isTransfer || isTerminal ? 16 : 12;
        int shadowOffset = 2;
        
        // Enhanced hover effect
        boolean isHovered = station.equals(hoveredStation);
        boolean isClicked = station.equals(clickedStation) && showStationDetails;
        
        if (isHovered || isClicked) {
            // Animated hover glow
            float glowAlpha = isClicked ? 0.8f : 0.3f + 0.2f * (float)Math.sin(hoverPulse);
            int glowSize = isClicked ? size + 12 : size + 8;
            
            g2.setColor(new Color(16, 185, 129, (int)(glowAlpha * 100)));
            g2.fillOval(x - glowSize/2, y - glowSize/2, glowSize, glowSize);
            
            // Outer glow ring for clicked stations
            if (isClicked) {
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(new Color(16, 185, 129, (int)(glowAlpha * 150)));
                g2.drawOval(x - (size + 16)/2, y - (size + 16)/2, size + 16, size + 16);
            }
        }
        
        // Enhanced shadow for hovered/clicked stations
        int dynamicShadowOffset = isHovered || isClicked ? shadowOffset + 2 : shadowOffset;
        g2.setColor(new Color(0, 0, 0, isHovered || isClicked ? 150 : 100));
        g2.fill(new Ellipse2D.Float(x - size/2 + dynamicShadowOffset, y - size/2 + dynamicShadowOffset, size, size));
        
        // Determine station color and style
        Color stationColor;
        if (isTerminal) {
            stationColor = TERMINAL_STATION;
        } else if (isTransfer) {
            stationColor = TRANSFER_STATION;
        } else {
            stationColor = REGULAR_STATION;
        }
        
        // Enhance station color when hovered
        if (isHovered) {
            stationColor = brightenColor(stationColor, 0.2f);
        }
        
        // Draw station circle
        g2.setColor(stationColor);
        g2.fill(new Ellipse2D.Float(x - size/2, y - size/2, size, size));
        
        // Enhanced border for interactions
        g2.setStroke(new BasicStroke(isHovered || isClicked ? 3f : 2f));
        g2.setColor(isHovered || isClicked ? new Color(16, 185, 129) : new Color(30, 41, 59));
        g2.draw(new Ellipse2D.Float(x - size/2, y - size/2, size, size));
        
        // Add special styling for transfer stations
        if (isTransfer && !isTerminal) {
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fill(new Ellipse2D.Float(x - 3, y - 3, 6, 6));
        }
    }
    
    private void drawTrains(Graphics2D g2) {
        for (int i = 0; i < animatedTrains.size(); i++) {
            AnimatedTrain t = animatedTrains.get(i);
            drawEnhancedTrain(g2, t, i);
        }
    }
    
    private void drawEnhancedTrain(Graphics2D g2, AnimatedTrain train, int trainIndex) {
        int trainSize = 16;
        int x = sx(train.x) - trainSize/2;
        int y = sy(train.y) - trainSize/2;
        
        // Draw train glow effect when pulsing
        if (train.isPulsing()) {
            g2.setColor(new Color(16, 185, 129, 50));
            g2.fillOval(x - 4, y - 4, trainSize + 8, trainSize + 8);
        }
        
        // Draw train shadow with dynamic alpha
        g2.setColor(new Color(0, 0, 0, (int)(150 * train.getPulseAlpha())));
        g2.fillOval(x + 2, y + 2, trainSize, trainSize);
        
        // Draw main train body
        Color trainColor = new Color(16, 185, 129, (int)(255 * train.getPulseAlpha()));
        g2.setColor(trainColor);
        g2.fillOval(x, y, trainSize, trainSize);
        
        // Draw train highlight
        g2.setColor(new Color(255, 255, 255, (int)(200 * train.getPulseAlpha())));
        g2.fillOval(x + 3, y + 3, 5, 5);
        
        // Draw train border
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(new Color(6, 78, 59, (int)(255 * train.getPulseAlpha())));
        g2.draw(new Ellipse2D.Float(x, y, trainSize, trainSize));
        
        // Draw direction indicator
        drawDirectionIndicator(g2, train, x + trainSize/2, y + trainSize/2, trainIndex);
        
        // Draw train number
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.setColor(new Color(241, 245, 249, (int)(255 * train.getPulseAlpha())));
        String trainNum = String.valueOf(trainIndex + 1);
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (trainSize - fm.stringWidth(trainNum)) / 2;
        int textY = y + trainSize/2 + fm.getAscent()/2 - 1;
        g2.drawString(trainNum, textX, textY);
    }
    
    private void drawDirectionIndicator(Graphics2D g2, AnimatedTrain train, int centerX, int centerY, int trainIndex) {
        // Calculate direction based on movement
        double dx = train.targetX - train.x;
        double dy = train.targetY - train.y;
        
        if (Math.abs(dx) < 0.1 && Math.abs(dy) < 0.1) return; // Not moving
        
        // Draw direction arrow
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(new Color(241, 245, 249, (int)(180 * train.getPulseAlpha())));
        
        double angle = Math.atan2(dy, dx);
        int arrowLength = 12;
        int arrowX = centerX + (int)(Math.cos(angle) * arrowLength);
        int arrowY = centerY + (int)(Math.sin(angle) * arrowLength);
        
        // Draw arrow line
        g2.drawLine(centerX, centerY, arrowX, arrowY);
        
        // Draw arrow head
        double arrowHeadAngle = Math.PI / 6; // 30 degrees
        int arrowHeadLength = 4;
        
        int headX1 = arrowX - (int)(Math.cos(angle - arrowHeadAngle) * arrowHeadLength);
        int headY1 = arrowY - (int)(Math.sin(angle - arrowHeadAngle) * arrowHeadLength);
        int headX2 = arrowX - (int)(Math.cos(angle + arrowHeadAngle) * arrowHeadLength);
        int headY2 = arrowY - (int)(Math.sin(angle + arrowHeadAngle) * arrowHeadLength);
        
        g2.drawLine(arrowX, arrowY, headX1, headY1);
        g2.drawLine(arrowX, arrowY, headX2, headY2);
    }

    // Get modern line colors
    private Color getModernLineColor(String code) {
        String prefix = code.substring(0, 1);
        switch (prefix) {
            case "R": return RED_LINE;
            case "G": return GREEN_LINE;
            case "B": return BLUE_LINE;
            default: return new Color(156, 163, 175); // Gray
        }
    }

    // Find station at mouse point (within 15px for better usability)
    private Station getStationAtPoint(Point p) {
        for (Station s : stations) {
            int x = sx(s.getX());
            int y = sy(s.getY());
            if (p.distance(x, y) < 15) return s;
        }
        return null;
    }

    // Draw modern tooltip
    private void drawTooltip(Graphics2D g2, Station station) {
        int x = sx(station.getX()) + 25;
        int y = sy(station.getY()) - 15;
        
        String stationName = station.getName().trim();
        String stationCode = station.getCode();
        String lineInfo = "Line: " + stationCode.charAt(0);
        
        // Additional details for clicked stations
        java.util.List<String> details = new ArrayList<>();
        details.add(stationName);
        details.add("Code: " + stationCode);
        details.add(lineInfo);
        
        if (showStationDetails && station.equals(clickedStation)) {
            details.add("Position: (" + (int)station.getX() + ", " + (int)station.getY() + ")");
            details.add("Click again to hide details");
        } else if (station.equals(hoveredStation)) {
            details.add("Click for more details");
        }
        
        // Measure text
        g2.setFont(TOOLTIP_FONT);
        FontMetrics fm = g2.getFontMetrics();
        int maxWidth = 0;
        for (String detail : details) {
            maxWidth = Math.max(maxWidth, fm.stringWidth(detail));
        }
        
        int textHeight = fm.getHeight() * details.size() + 10;
        int padding = 15;
        
        // Adjust tooltip position if it goes off screen
        if (x + maxWidth + padding * 2 > getWidth()) {
            x = sx(station.getX()) - maxWidth - padding * 2 - 25;
        }
        if (y - textHeight - padding < 0) {
            y = sy(station.getY()) + 35;
        }
        
        // Enhanced tooltip background with animation
        boolean isDetailed = showStationDetails && station.equals(clickedStation);
        Color bgColor1 = isDetailed ? new Color(16, 185, 129, 240) : new Color(30, 41, 59, 240);
        Color bgColor2 = isDetailed ? new Color(6, 78, 59, 240) : new Color(15, 23, 42, 240);
        
        GradientPaint gradient = new GradientPaint(
            x, y - textHeight - padding,
            bgColor1,
            x, y + padding,
            bgColor2
        );
        g2.setPaint(gradient);
        g2.fill(new RoundRectangle2D.Float(
            x, y - textHeight - padding,
            maxWidth + padding * 2, textHeight + padding * 2,
            15, 15
        ));
        
        // Enhanced tooltip border
        g2.setStroke(new BasicStroke(isDetailed ? 2f : 1f));
        g2.setColor(isDetailed ? new Color(16, 185, 129, 200) : new Color(71, 85, 105, 200));
        g2.draw(new RoundRectangle2D.Float(
            x, y - textHeight - padding,
            maxWidth + padding * 2, textHeight + padding * 2,
            15, 15
        ));
        
        // Draw text with enhanced styling
        for (int i = 0; i < details.size(); i++) {
            String detail = details.get(i);
            if (i == 0) {
                // Station name - larger and bold
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                                 g2.setColor(TEXT_COLOR);
            } else {
                // Other details - smaller
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.setColor(new Color(156, 163, 175));
            }
            
            g2.drawString(detail, x + padding, y - textHeight + fm.getHeight() * (i + 1) - 5);
        }
    }

    // AnimatedTrain inner class with enhanced features
    private static class AnimatedTrain {
        double x, y;
        double targetX, targetY;
        double speed = 3.0; // pixels per frame
        String direction = "forward";
        float pulseAlpha = 1.0f;
        boolean pulsing = false;
        int pulseCounter = 0;
        
        public AnimatedTrain(double x, double y) {
            this.x = x; 
            this.y = y; 
            this.targetX = x; 
            this.targetY = y;
        }
        
        public void setTarget(double tx, double ty) {
            // Only start pulsing if the position actually changed
            if (Math.abs(this.targetX - tx) > 1 || Math.abs(this.targetY - ty) > 1) {
                this.pulsing = true;
                this.pulseCounter = 60; // Pulse for 60 frames (1 second at 60fps)
            }
            this.targetX = tx; 
            this.targetY = ty;
        }
        
        public void setDirection(String dir) {
            this.direction = dir;
        }
        
        public void updatePosition() {
            double dx = targetX - x;
            double dy = targetY - y;
            double dist = Math.hypot(dx, dy);
            
            if (dist < speed) { 
                x = targetX; 
                y = targetY; 
            } else {
                // Smooth easing animation
                double easingFactor = 0.15;
                x += dx * easingFactor;
                y += dy * easingFactor;
            }
            
            // Update pulse animation
            if (pulsing) {
                pulseCounter--;
                pulseAlpha = 0.7f + 0.3f * (float)Math.sin(pulseCounter * 0.3);
                if (pulseCounter <= 0) {
                    pulsing = false;
                    pulseAlpha = 1.0f;
                }
            }
        }
        
        public float getPulseAlpha() {
            return pulseAlpha;
        }
        
        public boolean isPulsing() {
            return pulsing;
        }
    }

    // Enhanced setTrains method with direction tracking
    public void setTrains() {
        ReadSimulatorOutput output = new ReadSimulatorOutput();
        output.readOutput();
        trainInfo = output.getTrains();
        
        // Animate trains: update targets and directions
        if (animatedTrains.size() != trainInfo.size()) {
            animatedTrains.clear();
            for (Train t : trainInfo) {
                Station s = findStationByCode(t.getPosition());
                if (s != null) {
                    AnimatedTrain animTrain = new AnimatedTrain(s.getX(), s.getY());
                    animTrain.setDirection(t.getDirection());
                    animatedTrains.add(animTrain);
                }
            }
        } else {
            for (int i = 0; i < trainInfo.size() && i < animatedTrains.size(); i++) {
                Train t = trainInfo.get(i);
                Station s = findStationByCode(t.getPosition());
                if (s != null) {
                    animatedTrains.get(i).setTarget(s.getX(), s.getY());
                    animatedTrains.get(i).setDirection(t.getDirection());
                }
            }
        }
        repaint();
    }

    private Station findStationByCode(String code) {
        for (Station s : stations) if (s.getCode().equals(code)) return s;
        return null;
    }

    private void handleStationClick(Station station) {
        clickedStation = station;
        showStationDetails = !showStationDetails;
        
        // Visual feedback for click
        Timer clickFeedback = new Timer(100, null);
        final int[] pulseCount = {0};
        clickFeedback.addActionListener(e -> {
            pulseCount[0]++;
            repaint();
            if (pulseCount[0] >= 3) {
                clickFeedback.stop();
            }
        });
        clickFeedback.start();
        
        repaint();
    }

    /**
     * Sets the list of stations and repaints the panel.
     *
     * @param stations the list of stations to display
     */
    public void setStations(ArrayList<Station> stations) {
        this.stations = stations;
        repaint();
    }

    /**
     * Returns the MapPanel component.
     *
     * @return this MapPanel as a JPanel
     */
    public JPanel getPanel() {
        return this;
    }

    private Color brightenColor(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() * (1 + factor)));
        int g = Math.min(255, (int)(color.getGreen() * (1 + factor)));
        int b = Math.min(255, (int)(color.getBlue() * (1 + factor)));
        return new Color(r, g, b, color.getAlpha());
    }
}
