package ca.ucalgary.edu.ensf380.view;

import javax.swing.*;
import java.awt.*;

public class NewsPanel {
    private final JPanel panel;
    private final JLabel newsLabel;
    private final Timer timer;

    public NewsPanel() {
        this.panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(800, 50));

        this.newsLabel = new JLabel("Loading news...", SwingConstants.CENTER);
        newsLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        
        this.timer = new Timer(100, e -> scrollNews());
        timer.start();

        panel.add(newsLabel, BorderLayout.CENTER);
    }

    public JPanel getPanel() {
        return panel;
    }

    /**
     * Scrolls the news text by moving the first character to the end.
     */
    private void scrollNews() {
        String text = newsLabel.getText();
        newsLabel.setText(text.substring(1) + text.charAt(0));
    }

    /**
     * Updates the news label with new text.
     *
     * @param news the news content to display
     */
    public void updateNewsLabel(String news) {
        newsLabel.setText(news);
    }
}
