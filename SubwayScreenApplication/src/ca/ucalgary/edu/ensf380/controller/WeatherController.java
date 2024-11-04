package ca.ucalgary.edu.ensf380.controller;

import ca.ucalgary.edu.ensf380.view.WeatherPanel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeatherController extends DataFetcherController {
    private final WeatherPanel weatherPanel;
    private static final String WEATHER_REGEX = "^(.*?)\\s+(.*?)\\s+([+-]?\\d+°C)\\s+([←↔→↑↓↖↗↙↘]+\\d+km/h)\\s+(\\d+\\.\\d+mm)$";
    private static final Pattern WEATHER_PATTERN = Pattern.compile(WEATHER_REGEX);

    public WeatherController(WeatherPanel weatherPanel) {
        this.weatherPanel = weatherPanel;
    }

    /**
     * Retrieves weather information for a specified city and updates the WeatherPanel.
     *
     * @param city the name of the city to retrieve weather data for
     */
    public void retrieveWeather(String city) {
        new Thread(() -> {
            try {
                String weatherData = fetchData("https://wttr.in/" + city + "?format=%l+%C+%t+%w+%p");
                String[] parsedData = parseWeatherData(weatherData);

                if (parsedData != null) {
                    updateWeather(parsedData[0], parsedData[1], parsedData[2], parsedData[3], parsedData[4]);
                } else {
                    updateWeather("Unknown", "Unknown", "Unknown", "Unknown", "Unknown");
                }
            } catch (Exception e) {
                e.printStackTrace();
                updateWeather("Error", "Error", "Error", "Error", "Error");
            }
        }).start();
    }

    /**
     * Parses weather data from the API response.
     *
     * @param weatherData the raw weather data string
     * @return an array of parsed weather details or default values if parsing fails
     */
    private String[] parseWeatherData(String weatherData) {
        String trimmedData = weatherData.trim();
        Matcher matcher = WEATHER_PATTERN.matcher(trimmedData);

        if (matcher.find()) {
            String location = matcher.group(1).trim();
            String condition = matcher.group(2).trim();
            String temperature = matcher.group(3).trim();
            String wind = matcher.group(4).trim();
            String precipitation = matcher.group(5).trim();

            return new String[]{location, condition, temperature, wind, precipitation};
        }
        return new String[]{"Unknown", "Unknown", "Unknown", "Unknown", "Unknown"};
    }

    /**
     * Updates the weather display on the WeatherPanel.
     *
     * @param location the location name
     * @param condition the weather condition
     * @param temperature the temperature in °C
     * @param wind the wind speed and direction
     * @param precipitation the precipitation amount
     */
    private void updateWeather(String location, String condition, String temperature, String wind, String precipitation) {
        String weatherInfo = String.format(
            "<html>Location: %s<br>Condition: %s<br>Temperature: %s<br>Wind: %s<br>Precipitation: %s</html>",
            location, condition, temperature, wind, precipitation
        );
        weatherPanel.updateWeatherLabel(weatherInfo);
    }
}
