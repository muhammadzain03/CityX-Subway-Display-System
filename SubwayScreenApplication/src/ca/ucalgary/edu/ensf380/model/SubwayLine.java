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
