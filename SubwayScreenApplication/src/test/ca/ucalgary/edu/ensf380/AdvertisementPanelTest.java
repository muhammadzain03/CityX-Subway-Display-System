package test.ca.ucalgary.edu.ensf380;

import ca.ucalgary.edu.ensf380.view.AdvertisementPanel;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.Timer;

public class AdvertisementPanelTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Advertisement Panel Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        AdvertisementPanel adPanel = new AdvertisementPanel();

        java.util.List<Map<String, Object>> ads = new ArrayList<>();

        // Advertisement 1
        Map<String, Object> ad1 = new HashMap<>();
        ad1.put("media_type", "GIF");
        ad1.put("media_path", "ad1.gif");
        ads.add(ad1);

        // Advertisement 2
        Map<String, Object> ad2 = new HashMap<>();
        ad2.put("media_type", "GIF");
        ad2.put("media_path", "ad2.gif");
        ads.add(ad2);

        // Advertisement 3
        Map<String, Object> ad3 = new HashMap<>();
        ad3.put("media_type", "GIF");
        ad3.put("media_path", "ad3.gif");
        ads.add(ad3);

        // Advertisement 4
        Map<String, Object> ad4 = new HashMap<>();
        ad4.put("media_type", "GIF");
        ad4.put("media_path", "ad4.gif");
        ads.add(ad4);

        // Advertisement 5
        Map<String, Object> ad5 = new HashMap<>();
        ad5.put("media_type", "GIF");
        ad5.put("media_path", "ad5.gif");
        ads.add(ad5);

        Timer timer = new Timer();
        TimerTask switchAds = new TimerTask() {
            int currentAdIndex = 0;

            @Override
            public void run() {
                adPanel.displayAdvertisement(ads.get(currentAdIndex));
                currentAdIndex = (currentAdIndex + 1) % ads.size();
            }
        };
        
        timer.schedule(switchAds, 0, 10000); // Switch ads every 10 seconds

        frame.getContentPane().add(adPanel.getPanel(), BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
