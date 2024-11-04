package ca.ucalgary.edu.ensf380.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import ca.ucalgary.edu.ensf380.model.Station;
import ca.ucalgary.edu.ensf380.model.Train;
import ca.ucalgary.edu.ensf380.view.SubwayScreenGUI;

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
     * Populates the station list from the Map.csv file.
     */
    private void populateStation() {
        String filePath = "./Map/Map.csv";
        File file = new File(filePath);

        System.out.println("Reading file: " + file.getName());

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            // Skip the header line if necessary
            if ((line = br.readLine()) != null) {
                // Process header if needed
            }

            // Read station data lines
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 7) {
                    setStations(values[4], values[3], values[0], Double.parseDouble(values[5]), Double.parseDouble(values[6]));
                } else {
                    System.err.println("Invalid line format: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the station data file: " + e.getMessage());
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
        Station station = new Station(name, code, num, x, y);
        stations.add(station);
    }

    /**
     * Updates the train position and provides the surrounding station information.
     *
     * @param trainNum the train number to update
     * @param trains the list of Train objects
     * @param gui the SubwayScreenGUI instance to update with station information
     */
    public void updateTrainPos(int trainNum, ArrayList<Train> trains, SubwayScreenGUI gui) {
        ArrayList<Station> stationList = getStations();
        String previousStation = null;
        String nextStation = null;
        String nextStation1 = null;
        String nextStation2 = null;

        for (Station station : stationList) {
            if (station.getCode().equals(trains.get(trainNum).getPosition())) {
                System.out.println("Train T" + trains.get(trainNum).getId() + " is at " + station.getName() + " moving " + trains.get(trainNum).getDirection());
                currentStation = station.getName();

                if (trains.get(trainNum).getDirection().equals("forward")) {
                    previousStation = getStationNameByOffset(stationList, station, -1);
                    nextStation = getStationNameByOffset(stationList, station, 1);
                    nextStationNum = getStationCodeByOffset(stationList, station, 1);
                    nextStation1 = getStationNameByOffset(stationList, station, 2);
                    nextStation2 = getStationNameByOffset(stationList, station, 3);

                } else if (trains.get(trainNum).getDirection().equals("backward")) {
                    previousStation = getStationNameByOffset(stationList, station, 1);
                    nextStation = getStationNameByOffset(stationList, station, -1);
                    nextStationNum = getStationCodeByOffset(stationList, station, -1);
                    nextStation1 = getStationNameByOffset(stationList, station, -2);
                    nextStation2 = getStationNameByOffset(stationList, station, -3);
                }
                gui.getStationInfoPanel().updateTrainPosition(previousStation, currentStation, nextStation, nextStation1, nextStation2);
                break;
            }
        }
    }

    /**
     * Gets the name of the station at a specific offset from the current station.
     *
     * @param stationList the list of Station objects
     * @param currentStation the current Station
     * @param offset the number of stations away from the current station
     * @return the name of the station at the specified offset, or null if not found
     */
    private String getStationNameByOffset(ArrayList<Station> stationList, Station currentStation, int offset) {
        int targetNumber = Integer.parseInt(currentStation.getNumber()) + offset;
        return stationList.stream()
                .filter(station -> station.getNumber().equals(String.valueOf(targetNumber)))
                .map(Station::getName)
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets the code of the station at a specific offset from the current station.
     *
     * @param stationList the list of Station objects
     * @param currentStation the current Station
     * @param offset the number of stations away from the current station
     * @return the code of the station at the specified offset, or null if not found
     */
    private String getStationCodeByOffset(ArrayList<Station> stationList, Station currentStation, int offset) {
        int targetNumber = Integer.parseInt(currentStation.getNumber()) + offset;
        return stationList.stream()
                .filter(station -> station.getNumber().equals(String.valueOf(targetNumber)))
                .map(Station::getCode)
                .findFirst()
                .orElse(null);
    }
}
