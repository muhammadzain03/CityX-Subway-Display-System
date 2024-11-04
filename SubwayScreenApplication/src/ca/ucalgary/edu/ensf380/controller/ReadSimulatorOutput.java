package ca.ucalgary.edu.ensf380.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import ca.ucalgary.edu.ensf380.model.Train;

public class ReadSimulatorOutput {
    private static final ArrayList<Train> trains = new ArrayList<>();

    public ReadSimulatorOutput() {
        // Default constructor
    }
    
    /**
     * Returns the list of trains.
     *
     * @return ArrayList of Train objects
     */
    public ArrayList<Train> getTrains() {
        return trains;
    }
    
    /**
     * Reads the latest CSV file output from the simulator and populates the train list.
     */
    public void readOutput() {
        String folderPath = "./out";
        trains.clear();
        File folder = new File(folderPath);

        // Check if folder exists
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("The specified path is not a directory or does not exist: " + folderPath);
            return;
        }

        File[] listOfFiles = folder.listFiles();
        File latest = getLatestFile(listOfFiles);

        // Check if there are any files
        if (latest == null) {
            System.err.println("No files found in the specified directory.");
            return;
        }

        System.out.println("Reading file: " + latest.getName());

        try (BufferedReader br = new BufferedReader(new FileReader(latest))) {
            String line;

            // Skip the header line if present
            if ((line = br.readLine()) != null) {
                // Process header if necessary
            }

            // Read train data lines
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }

                String[] values = line.split(",");

                // Validate the number of values before accessing indices
                if (values.length >= 4) {
                    setTrains(values[1], values[2], values[3]);
                } else {
                    System.err.println("Invalid line format: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the CSV file: " + e.getMessage());
        }
    }
    
    /**
     * Retrieves the latest file based on the last modified date.
     *
     * @param files an array of File objects
     * @return the latest modified File object or null if no files are present
     */
    private File getLatestFile(File[] files) {
        if (files == null || files.length == 0) {
            return null;
        }

        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return files[0];
    }
    
    /**
     * Adds a Train object to the list with the specified details.
     *
     * @param number the train number
     * @param station the station name
     * @param dir the train direction
     */
    private void setTrains(String number, String station, String dir) {
        Train train = new Train(number, station, dir);
        trains.add(train);
    }
}
