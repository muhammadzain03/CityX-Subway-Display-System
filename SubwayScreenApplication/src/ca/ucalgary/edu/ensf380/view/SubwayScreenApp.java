package ca.ucalgary.edu.ensf380.view;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import ca.ucalgary.edu.ensf380.MyApp3;
import ca.ucalgary.edu.ensf380.controller.ReadSimulatorOutput;
import ca.ucalgary.edu.ensf380.controller.StationController;
import ca.ucalgary.edu.ensf380.model.Train;

public class SubwayScreenApp {
    
    public static void main(String[] args) {
        // Validate that exactly three arguments are provided for the train number, city, and country code
        if (args.length != 3) {
            System.out.println("Usage: java SubwayScreenApp <train_number> <city> <country_code>");
            return;
        }

        // Parse command-line arguments for train number, city, and country code
        String trainNumber = args[0];
        String city = args[1];
        String countryCode = args[2];
        
        // Convert train number from String to integer for indexing purposes
        int trainNum = Integer.parseInt(trainNumber) - 1;

        // Initialize the main application components
        MyApp3 app3 = new MyApp3();
        ReadSimulatorOutput output = new ReadSimulatorOutput();
        StationController stationController = new StationController();
        SubwayScreenGUI gui = new SubwayScreenGUI(trainNumber, city, countryCode, stationController.getStations());

        // Main loop: runs continuously while app3 is in a running state
        while (app3.running) {
            // Update and retrieve the current train positions from the simulator output
            output.readOutput();
            ArrayList<Train> trains = output.getTrains();
            System.out.println(trains.get(trainNum).getPosition());  // Print the current train position

            // Update the GUI with the train's current position and get the next station code
            stationController.updateTrainPos(trainNum, trains, gui);
            String nextStationCode = stationController.nextStationNum;

            // Path to the audio file for the announcement at the next station
            String audioFilePath = "./audio/" + nextStationCode.trim() + ".mp3";
            playAudioAnnouncement(audioFilePath);

            // Pause the loop for 13 seconds to allow for the audio announcement and screen display update
            try {
                Thread.sleep(13000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Plays an audio announcement for the next station.
     *
     * @param audioFilePath the file path to the audio file
     */
    private static void playAudioAnnouncement(String audioFilePath) {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(audioFilePath))) {
            AdvancedPlayer player = new AdvancedPlayer(bis);
            player.play();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + audioFilePath);
        } catch (IOException | JavaLayerException e) {
            e.printStackTrace();
        }
    }
}
