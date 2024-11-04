package ca.ucalgary.edu.ensf380.view;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class AdvertisementPanel {
    private static final String BASE_PATH = "advertisements/";
    private final JPanel panel;
    private final JLabel adLabel;

    public AdvertisementPanel() {
        this.panel = new JPanel(new BorderLayout());
        this.adLabel = new JLabel("Loading ads...", SwingConstants.CENTER);
        panel.add(adLabel, BorderLayout.CENTER);
    }

    public JPanel getPanel() {
        return panel;
    }

    /**
     * Displays the advertisement on the panel.
     *
     * @param ad a Map containing advertisement details, or null to clear the display
     */
    public void displayAdvertisement(Map<String, Object> ad) {
        if (ad == null) {
            adLabel.setIcon(null);
            adLabel.setText("Map Display will come here");
            return;
        }

        String mediaType = (String) ad.get("media_type");
        String mediaPath = BASE_PATH + ad.get("media_path");

        switch (mediaType) {
            case "JPEG":
            case "BMP":
            case "GIF":
                ImageIcon imageIcon = new ImageIcon(mediaPath);
                adLabel.setIcon(imageIcon);
                adLabel.setText("");
                break;
            default:
                adLabel.setIcon(null);
                adLabel.setText("Unsupported media type: " + mediaType);
                break;
        }
    }
}
