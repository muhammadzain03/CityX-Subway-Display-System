package test.ca.ucalgary.edu.ensf380;

import ca.ucalgary.edu.ensf380.controller.WeatherController;
import ca.ucalgary.edu.ensf380.view.WeatherPanel;

import javax.swing.*;
import java.awt.*;

public class WeatherPanelTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Weather Panel Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 500);

        WeatherPanel weatherPanel = new WeatherPanel();
        WeatherController weatherController = new WeatherController(weatherPanel);

        frame.getContentPane().add(weatherPanel.getPanel(), BorderLayout.CENTER);
        frame.setVisible(true);

        // Fetch weather data for a given city 
        weatherController.retrieveWeather("Calgary");
    }
}
