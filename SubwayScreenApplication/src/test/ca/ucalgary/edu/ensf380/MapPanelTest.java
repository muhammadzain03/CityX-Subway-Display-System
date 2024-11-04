package test.ca.ucalgary.edu.ensf380;

import ca.ucalgary.edu.ensf380.view.MapPanel;
import ca.ucalgary.edu.ensf380.controller.StationController;
import ca.ucalgary.edu.ensf380.model.Station;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MapPanelTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Map Panel Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        StationController stationController = new StationController();
        ArrayList<Station> stations = stationController.getStations();

        MapPanel mapPanel = new MapPanel(stations, null);
        frame.getContentPane().add(mapPanel.getPanel(), BorderLayout.CENTER);

        frame.setVisible(true);
    }
}
