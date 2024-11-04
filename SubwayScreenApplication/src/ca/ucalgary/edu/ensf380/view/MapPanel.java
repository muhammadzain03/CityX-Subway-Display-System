package ca.ucalgary.edu.ensf380.view;

import javax.swing.*;
import ca.ucalgary.edu.ensf380.controller.ReadSimulatorOutput;
import ca.ucalgary.edu.ensf380.model.Station;
import ca.ucalgary.edu.ensf380.model.Train;

import java.awt.*;
import java.util.ArrayList;

public class MapPanel extends JPanel {
    private ArrayList<Station> stations;
    private ArrayList<Train> trainInfo;
    private final String trainNum;

    public MapPanel(ArrayList<Station> stations, String trainNumber) {
        setTrains();
        this.stations = stations;
        this.trainNum = trainNumber;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw stations
        for (Station station : stations) {
            int x = (int) station.getX() / 2;
            int y = (int) station.getY() / 2;
            String stationNumber = station.getCode();

            // Set color based on station code prefix
            if (stationNumber.startsWith("R")) {
                g.setColor(Color.RED);
            } else if (stationNumber.startsWith("G")) {
                g.setColor(Color.GREEN);
            } else if (stationNumber.startsWith("B")) {
                g.setColor(Color.BLUE);
            }

            g.fillOval(x, y, 10, 10);
        }

        // Draw trains
        for (Train trainData : trainInfo) {
            for (Station station : stations) {
                if (station.getCode().equals(trainData.getPosition())) {
                    int x = (int) station.getX() / 2;
                    int y = (int) station.getY() / 2;

                    if (trainNum.equals(trainData.getId())) {
                        g.setColor(Color.CYAN);
                        g.fillOval(x, y, 14, 14);
                    } else {
                        g.setColor(Color.BLACK);
                        g.fillOval(x, y, 14, 14);
                    }
                }
            }
        }
    }

    /**
     * Sets the list of stations and repaints the panel.
     *
     * @param stations the list of stations to display
     */
    public void setStations(ArrayList<Station> stations) {
        this.stations = stations;
        repaint();
    }

    /**
     * Initializes the train information by reading from the simulator output.
     */
    public void setTrains() {
        ReadSimulatorOutput output = new ReadSimulatorOutput();
        output.readOutput();
        trainInfo = output.getTrains();
        repaint();
    }

    /**
     * Returns the MapPanel component.
     *
     * @return this MapPanel as a JPanel
     */
    public JPanel getPanel() {
        return this;
    }
}
