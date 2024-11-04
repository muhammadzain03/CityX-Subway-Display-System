package ca.ucalgary.edu.ensf380.view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherPanel {
    private final JPanel panel;
    private final JLabel weatherLabel;
    private final JLabel timeLabel;
    private final Timer timer;

    public WeatherPanel() {
        this.panel = new JPanel(new GridLayout(2, 1));
        panel.setPreferredSize(new Dimension(200, 400));

        this.timeLabel = new JLabel("Loading time...", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        this.weatherLabel = new JLabel("Loading weather data...");

        this.timer = new Timer(1000, e -> updateTime());
        timer.start();

        panel.add(timeLabel);
        panel.add(weatherLabel);
    }

    public JPanel getPanel() {
        return panel;
    }

    /**
     * Updates the time displayed on the time label.
     */
    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        timeLabel.setText(now.format(formatter));
    }

    /**
     * Updates the weather information displayed on the weather label.
     *
     * @param weatherInfo the weather information to display
     */
    public void updateWeatherLabel(String weatherInfo) {
        weatherLabel.setText(weatherInfo);
    }
}
