package ca.ucalgary.edu.ensf380.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import ca.ucalgary.edu.ensf380.model.Station;
import ca.ucalgary.edu.ensf380.model.Train;
import ca.ucalgary.edu.ensf380.view.SubwayScreenGUI;
import ca.ucalgary.edu.ensf380.util.AppConstants;
import ca.ucalgary.edu.ensf380.util.AppLogger;

public class StationController {
    private static final ArrayList<Station> stations = new ArrayList<>();

    public String nextStationNum;
    public String currentStation;

    public StationController() {
        populateStation();
    }

    /**
     * Returns the list of stations.
     *
     * @return ArrayList of Station objects
     */
    public ArrayList<Station> getStations() {
        return stations;
    }

    /**
     * Populates the station list from the subway.csv file with improved error handling.
     */
    private void populateStation() {
        AppLogger.data("Station Loading", "Starting to load station data from " + AppConstants.SUBWAY_DATA_FILE);
        long startTime = System.currentTimeMillis();
        
        File file = new File(AppConstants.SUBWAY_DATA_FILE);
        
        // Validate file exists
        if (!file.exists()) {
            AppLogger.error("Station data file not found: " + AppConstants.SUBWAY_DATA_FILE);
            return;
        }
        
        if (!file.canRead()) {
            AppLogger.error("Cannot read station data file: " + AppConstants.SUBWAY_DATA_FILE);
            return;
        }

        int loadedStations = 0;
        int skippedLines = 0;
        
        // Use try-with-resources for proper resource management
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            // Skip the header line if present
            if ((line = br.readLine()) != null) {
                AppLogger.debug("Skipping header line: " + line);
            }

            // Read station data lines
            int lineNumber = 1; // Start at 1 since we skipped header
            while ((line = br.readLine()) != null) {
                lineNumber++;
                
                if (line.trim().isEmpty()) {
                    AppLogger.debug("Skipping empty line " + lineNumber);
                    skippedLines++;
                    continue;
                }
                
                try {
                    if (parseAndAddStation(line, lineNumber)) {
                        loadedStations++;
                    } else {
                        skippedLines++;
                    }
                } catch (Exception e) {
                    AppLogger.error("Error parsing line " + lineNumber + ": " + line, e);
                    skippedLines++;
                }
            }
            
            long duration = System.currentTimeMillis() - startTime;
            AppLogger.data("Station Loading", String.format("Loaded %d stations, skipped %d lines in %d ms", 
                loadedStations, skippedLines, duration));
            AppLogger.info(AppConstants.SUCCESS_DATA_LOADED + " (" + loadedStations + " stations)");
            
        } catch (IOException e) {
            AppLogger.error("Error reading station data file: " + AppConstants.SUBWAY_DATA_FILE, e);
        }
    }
    
    /**
     * Parse a single line and add station if valid
     */
    private boolean parseAndAddStation(String line, int lineNumber) {
        String[] values = line.split(",");
        
        if (values.length < 7) {
            AppLogger.warning("Invalid line format at line " + lineNumber + " (expected 7+ columns, got " + values.length + "): " + line);
            return false;
        }
        
        try {
            // Extract and validate data
            // Column 0: Row number (not used)
            // Column 1: Line (e.g., "R", "G", "B")
            // Column 2: StationNumber (e.g., "8" for station 8 on the line) 
            // Column 3: StationCode (e.g., "R08")
            // Column 4: StationName
            String stationNumber = values[2].trim();  // Use Column 2 (StationNumber), not Column 0 (Row)
            String stationCode = values[3].trim();
            String stationName = values[4].trim();
            
            // Validate station code format
            if (!stationCode.matches(AppConstants.STATION_CODE_REGEX)) {
                AppLogger.warning("Invalid station code format at line " + lineNumber + ": " + stationCode);
                return false;
            }
            
            // Validate station name length
            if (stationName.length() > AppConstants.MAX_STATION_NAME_LENGTH) {
                AppLogger.warning("Station name too long at line " + lineNumber + ": " + stationName);
                stationName = stationName.substring(0, AppConstants.MAX_STATION_NAME_LENGTH);
            }
            
            // Parse coordinates
            double x = Double.parseDouble(values[5].trim());
            double y = Double.parseDouble(values[6].trim());
            
            // Validate coordinates are reasonable
            if (x < 0 || y < 0 || x > 10000 || y > 10000) {
                AppLogger.warning("Suspicious coordinates at line " + lineNumber + ": (" + x + ", " + y + ")");
            }
            
            // Create and add station
            setStations(stationName, stationCode, stationNumber, x, y);
            AppLogger.debug("Added station: " + stationCode + " - " + stationName + " at (" + x + ", " + y + ")");
            return true;
            
        } catch (NumberFormatException e) {
            AppLogger.error("Invalid number format at line " + lineNumber + ": " + line, e);
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            AppLogger.error("Array index out of bounds at line " + lineNumber + ": " + line, e);
            return false;
        }
    }

    /**
     * Adds a Station object to the station list with specified attributes.
     *
     * @param name the station name
     * @param code the station code
     * @param num the station number
     * @param x the x-coordinate of the station
     * @param y the y-coordinate of the station
     */
    private void setStations(String name, String code, String num, double x, double y) {
        // Check for duplicate stations
        for (Station existingStation : stations) {
            if (existingStation.getCode().equals(code)) {
                // Silently skip duplicate station codes
                return;
            }
        }
        
        Station station = new Station(name, code, num, x, y);
        stations.add(station);
    }

    /**
     * Updates the train position and provides the surrounding station information with improved validation.
     *
     * @param trainNum the train number to update
     * @param trains the list of Train objects
     * @param gui the SubwayScreenGUI instance to update with station information
     */
    public void updateTrainPos(int trainNum, ArrayList<Train> trains, SubwayScreenGUI gui) {
        if (trains == null || trains.isEmpty()) {
            AppLogger.warning("No trains data available for position update");
            return;
        }
        
        if (trainNum < 0 || trainNum >= trains.size()) {
            AppLogger.error("Invalid train number: " + trainNum + " (available: 0-" + (trains.size() - 1) + ")");
            return;
        }
        
        if (stations.isEmpty()) {
            AppLogger.error("No station data available for train position update");
            return;
        }
        
        Train currentTrain = trains.get(trainNum);
        String trainPosition = currentTrain.getPosition();
        String trainDirection = currentTrain.getDirection();
        
        AppLogger.debug("Updating train " + trainNum + " position: " + trainPosition + " direction: " + trainDirection);
        
        // Find the station matching the train's current position
        Station currentStationObj = findStationByCode(trainPosition);
        if (currentStationObj == null) {
            AppLogger.warning("Station not found for code: " + trainPosition);
            return;
        }
        
        // Calculate surrounding stations
        String previousStation = null;
        String nextStation = null;
        String nextStation1 = null;
        String nextStation2 = null;
        
        currentStation = currentStationObj.getName();
        
        try {
            if ("forward".equals(trainDirection)) {
                previousStation = getStationNameByOffset(currentStationObj, -1);
                nextStation = getStationNameByOffset(currentStationObj, 1);
                nextStationNum = getStationCodeByOffset(currentStationObj, 1);
                nextStation1 = getStationNameByOffset(currentStationObj, 2);
                nextStation2 = getStationNameByOffset(currentStationObj, 3);
            } else if ("backward".equals(trainDirection)) {
                previousStation = getStationNameByOffset(currentStationObj, 1);
                nextStation = getStationNameByOffset(currentStationObj, -1);
                nextStationNum = getStationCodeByOffset(currentStationObj, -1);
                nextStation1 = getStationNameByOffset(currentStationObj, -2);
                nextStation2 = getStationNameByOffset(currentStationObj, -3);
            } else {
                AppLogger.warning("Unknown train direction: " + trainDirection);
                return;
            }
            
            // Update GUI with calculated station information
            if (gui != null && gui.getStationInfoPanel() != null) {
                gui.getStationInfoPanel().updateTrainPosition(previousStation, currentStation, nextStation, nextStation1, nextStation2);
                AppLogger.debug("Updated GUI with station information");
            } else {
                AppLogger.warning("GUI or StationInfoPanel is null, cannot update display");
            }
            
            // Update map with latest train positions (reuse already-loaded train data)
            if (gui != null && gui.getMapPanel() != null) {
                gui.getMapPanel().updateTrainPositions(trains);
                AppLogger.debug("Updated map with train positions");
            }
            
        } catch (Exception e) {
            AppLogger.error("Error updating train position display", e);
        }
    }
    
    /**
     * Find station by code with validation
     */
    private Station findStationByCode(String stationCode) {
        if (stationCode == null || stationCode.trim().isEmpty()) {
            return null;
        }
        
        return stations.stream()
                .filter(station -> stationCode.equals(station.getCode()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets the name of the station at a specific offset from the current station with better error handling.
     *
     * @param currentStation the current Station
     * @param offset the number of stations away from the current station
     * @return the name of the station at the specified offset, or "End of Line" if not found
     */
    private String getStationNameByOffset(Station currentStation, int offset) {
        try {
            int currentNumber = Integer.parseInt(currentStation.getNumber());
            int targetNumber = currentNumber + offset;
            
            // Find station with target number on the same line
            String currentLine = currentStation.getCode().substring(0, 1);
            
            return stations.stream()
                    .filter(station -> station.getCode().startsWith(currentLine))
                    .filter(station -> {
                        try {
                            return Integer.parseInt(station.getNumber()) == targetNumber;
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    })
                    .map(Station::getName)
                    .findFirst()
                    .orElse("End of Line");
                    
        } catch (NumberFormatException e) {
            AppLogger.error("Invalid station number format: " + currentStation.getNumber(), e);
            return "Unknown";
        } catch (Exception e) {
            AppLogger.error("Error getting station name by offset", e);
            return "Error";
        }
    }

    /**
     * Gets the code of the station at a specific offset from the current station with better error handling.
     *
     * @param currentStation the current Station
     * @param offset the number of stations away from the current station
     * @return the code of the station at the specified offset, or null if not found
     */
    private String getStationCodeByOffset(Station currentStation, int offset) {
        try {
            int currentNumber = Integer.parseInt(currentStation.getNumber());
            int targetNumber = currentNumber + offset;
            
            // Find station with target number on the same line
            String currentLine = currentStation.getCode().substring(0, 1);
            
            return stations.stream()
                    .filter(station -> station.getCode().startsWith(currentLine))
                    .filter(station -> {
                        try {
                            return Integer.parseInt(station.getNumber()) == targetNumber;
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    })
                    .map(Station::getCode)
                    .findFirst()
                    .orElse(null);
                    
        } catch (NumberFormatException e) {
            AppLogger.error("Invalid station number format: " + currentStation.getNumber(), e);
            return null;
        } catch (Exception e) {
            AppLogger.error("Error getting station code by offset", e);
            return null;
        }
    }
}
