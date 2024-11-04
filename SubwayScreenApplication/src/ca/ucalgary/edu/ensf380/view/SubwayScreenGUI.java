package ca.ucalgary.edu.ensf380.view;

import ca.ucalgary.edu.ensf380.model.Station;
import ca.ucalgary.edu.ensf380.controller.AdvertisementController;
import ca.ucalgary.edu.ensf380.controller.WeatherController;
import ca.ucalgary.edu.ensf380.controller.NewsController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SubwayScreenGUI {

    private final AdvertisementPanel advertisementPanel;
    private final WeatherPanel weatherPanel;
    private final NewsPanel newsPanel;
    private final StationInfoPanel stationInfoPanel;
    private final MapPanel mapPanel;

    private final AdvertisementController advertisementController;
    private final WeatherController weatherController;
    private final NewsController newsController;

    private final JPanel adMapPanel;
    private final CardLayout cardLayout;
    private Timer displayTimer;

    private boolean showingAd = true; // Track which card is currently visible

    public SubwayScreenGUI(String trainNumber, String city, String countryCode, ArrayList<Station> stations) {
        this.advertisementPanel = new AdvertisementPanel();
        this.weatherPanel = new WeatherPanel();
        this.newsPanel = new NewsPanel();
        this.stationInfoPanel = new StationInfoPanel();
        this.mapPanel = new MapPanel(stations, trainNumber);

        this.cardLayout = new CardLayout();
        this.adMapPanel = new JPanel(cardLayout);

        this.advertisementController = new AdvertisementController(advertisementPanel);
        this.weatherController = new WeatherController(weatherPanel);
        weatherController.retrieveWeather(city);

        this.newsController = new NewsController(newsPanel);
        newsController.retrieveNews(countryCode);

        setupGUI(trainNumber);
        startDisplayTimer();
    }

    private void setupGUI(String trainNumber) {
        EventQueue.invokeLater(() -> {
            JFrame mainFrame = new JFrame("Subway Screen - " + trainNumber);
            mainFrame.setSize(1000, 600);
            mainFrame.setLayout(new BorderLayout());
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            adMapPanel.add(advertisementPanel.getPanel(), "AdvertisementPanel");
            adMapPanel.add(mapPanel.getPanel(), "MapPanel");

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(adMapPanel, BorderLayout.CENTER);
            mainPanel.add(weatherPanel.getPanel(), BorderLayout.EAST);

            mainFrame.getContentPane().add(BorderLayout.CENTER, mainPanel);

            JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
            bottomPanel.add(newsPanel.getPanel());
            bottomPanel.add(stationInfoPanel.getPanel());

            mainFrame.getContentPane().add(BorderLayout.SOUTH, bottomPanel);

            mainFrame.setVisible(true);
        });
    }

    private void startDisplayTimer() {
        displayTimer = new Timer(10000, e -> {
            if (showingAd) {
                cardLayout.show(adMapPanel, "MapPanel");
                advertisementController.resumeAd();
            } else {
                cardLayout.show(adMapPanel, "AdvertisementPanel");
                advertisementController.pauseAd();
            }
            showingAd = !showingAd; // Toggle the card state
        });
        displayTimer.start();
    }

    public StationInfoPanel getStationInfoPanel() {
        return stationInfoPanel;
    }
}
