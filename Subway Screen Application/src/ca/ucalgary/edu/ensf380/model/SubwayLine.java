/**
 * Subway Line Data Model
 * 
 * Represents a complete subway line with its collection of stations and line
 * properties. Manages the logical grouping of stations that belong to the same
 * transit line (Red, Green, Blue) and provides line-specific functionality.
 * 
 * This model encapsulates:
 * - Line identification and naming
 * - Collection of stations on the line
 * - Line-specific properties and behavior
 * 
 * @author Subway Screen Development Team
 * @version 2.0
 */
package ca.ucalgary.edu.ensf380.model;

import java.util.ArrayList;
import java.util.List;

public class SubwayLine {
    private List<Station> stations;
    private String name;

    public SubwayLine(String name) {
        this.name = name;
        this.stations = new ArrayList<>();
    }

    public SubwayLine(List<Station> stations) {
        this.stations = stations;
    }

    public void addStation(Station station) {
        stations.add(station);
    }

    public List<Station> getStations() {
        return stations;
    }

    public Station getStationByName(String name) {
        for (Station station : stations) {
            if (station.getName().equals(name)) {
                return station;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
