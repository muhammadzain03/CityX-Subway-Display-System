package ca.ucalgary.edu.ensf380.controller;

import java.io.IOException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.util.Scanner;

public abstract class DataFetcherController {

    /**
     * Fetches data from a given URL as a string.
     *
     * @param stringUrl the URL to fetch data from
     * @return the response data as a String
     * @throws IOException if an I/O error occurs or if the response code is not HTTP_OK
     */
    protected String fetchData(String stringUrl) throws IOException {
        StringBuilder response = new StringBuilder();
        URL url = new URL(stringUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
            try (Scanner scanner = new Scanner(connection.getInputStream())) {
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine()).append("\n");
                }
            }
        } else {
            throw new IOException("Error in fetching data. Response code: " + connection.getResponseCode());
        }

        return response.toString();
    }
}
