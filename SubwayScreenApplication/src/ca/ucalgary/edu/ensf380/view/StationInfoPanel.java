package ca.ucalgary.edu.ensf380.view;

import javax.swing.*;
import java.awt.*;

public class StationInfoPanel {
    private final JPanel panel;
    private final JLabel[] stationLabel = new JLabel[5];

    public StationInfoPanel() {
        this.panel = new JPanel(new GridLayout(1, 5));
        panel.setPreferredSize(new Dimension(800, 100));
        
        for (int i = 0; i < stationLabel.length; i++) {
            stationLabel[i] = new JLabel("Next stations: Fetching...", SwingConstants.CENTER);
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
        stationLabel[0].setText("Previous: " + prev);
        stationLabel[1].setText("Current: " + curr);
        stationLabel[2].setText("Next: " + next);
        stationLabel[3].setText("Next: " + next1);
        stationLabel[4].setText("Next: " + next2);
    }
}
