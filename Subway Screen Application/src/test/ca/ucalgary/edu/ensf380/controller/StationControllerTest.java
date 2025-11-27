package test.ca.ucalgary.edu.ensf380.controller;

import ca.ucalgary.edu.ensf380.controller.StationController;
import ca.ucalgary.edu.ensf380.model.Station;
import java.util.ArrayList;

/**
 * Comprehensive Unit Tests for StationController Class
 * 
 * Tests station loading, CSV parsing, and data management functionality
 * of the StationController to ensure reliable subway station operations.
 * 
 * @author Subway Screen Test Team
 * @version 1.0
 */
public class StationControllerTest {
    
    private StationController stationController;
    
    public void setUp() {
        stationController = new StationController();
    }
    
    // Basic Functionality Tests
    public void testConstructorInitialization() {
        StationController controller = new StationController();
        ArrayList<Station> stations = controller.getStations();
        assert stations != null : "Stations list should not be null";
        System.out.println("✓ Constructor initialization creates non-null stations list");
    }
    
    public void testGetStationsReturnsValidList() {
        setUp();
        ArrayList<Station> stations = stationController.getStations();
        
        assert stations != null : "Stations list should not be null";
        assert stations.size() > 0 : "Should have loaded some stations";
        System.out.println("✓ getStations() returns valid list with " + stations.size() + " stations");
    }
    
    public void testStationDataIntegrity() {
        setUp();
        ArrayList<Station> stations = stationController.getStations();
        
        boolean hasValidStations = false;
        for (Station station : stations) {
            if (station != null && 
                station.getName() != null && 
                station.getCode() != null && 
                station.getNumber() != null) {
                hasValidStations = true;
                break;
            }
        }
        
        assert hasValidStations : "Should have at least one valid station with all fields";
        System.out.println("✓ Stations have valid data integrity");
    }
    
    public void testStationCoordinates() {
        setUp();
        ArrayList<Station> stations = stationController.getStations();
        
        boolean hasCoordinates = false;
        for (Station station : stations) {
            if (station != null && (station.getX() != 0.0 || station.getY() != 0.0)) {
                hasCoordinates = true;
                // Check that coordinates are reasonable (positive values for display)
                assert station.getX() >= 0 : "X coordinate should be non-negative: " + station.getX();
                assert station.getY() >= 0 : "Y coordinate should be non-negative: " + station.getY();
            }
        }
        
        assert hasCoordinates : "Should have at least one station with coordinates";
        System.out.println("✓ Station coordinates are valid and reasonable");
    }
    
    public void testStationCodes() {
        setUp();
        ArrayList<Station> stations = stationController.getStations();
        
        for (Station station : stations) {
            if (station != null && station.getCode() != null) {
                String code = station.getCode();
                assert code.length() > 0 : "Station code should not be empty";
                // Check if code follows typical pattern (letter + numbers)
                assert code.matches("^[A-Z]\\d+$") : "Station code should follow pattern like R01, B15, G03: " + code;
            }
        }
        
        System.out.println("✓ Station codes follow expected format");
    }
    
    public void testStationNumbers() {
        setUp();
        ArrayList<Station> stations = stationController.getStations();
        
        for (Station station : stations) {
            if (station != null && station.getNumber() != null) {
                String number = station.getNumber();
                assert number.length() > 0 : "Station number should not be empty";
                
                try {
                    int num = Integer.parseInt(number);
                    assert num > 0 : "Station number should be positive: " + num;
                } catch (NumberFormatException e) {
                    // Some station numbers might be alphanumeric, which is okay
                    assert number.length() <= 10 : "Station number too long: " + number;
                }
            }
        }
        
        System.out.println("✓ Station numbers are valid");
    }
    
    public void testUniqueStationCodes() {
        setUp();
        ArrayList<Station> stations = stationController.getStations();
        ArrayList<String> codes = new ArrayList<>();
        
        for (Station station : stations) {
            if (station != null && station.getCode() != null) {
                String code = station.getCode();
                assert !codes.contains(code) : "Duplicate station code found: " + code;
                codes.add(code);
            }
        }
        
        System.out.println("✓ All station codes are unique (" + codes.size() + " unique codes)");
    }
    
    public void testRedLineStations() {
        setUp();
        ArrayList<Station> stations = stationController.getStations();
        
        int redStations = 0;
        for (Station station : stations) {
            if (station != null && station.getCode() != null && station.getCode().startsWith("R")) {
                redStations++;
            }
        }
        
        assert redStations > 0 : "Should have Red line stations";
        System.out.println("✓ Found " + redStations + " Red line stations");
    }
    
    public void testBlueLineStations() {
        setUp();
        ArrayList<Station> stations = stationController.getStations();
        
        int blueStations = 0;
        for (Station station : stations) {
            if (station != null && station.getCode() != null && station.getCode().startsWith("B")) {
                blueStations++;
            }
        }
        
        assert blueStations > 0 : "Should have Blue line stations";
        System.out.println("✓ Found " + blueStations + " Blue line stations");
    }
    
    public void testGreenLineStations() {
        setUp();
        ArrayList<Station> stations = stationController.getStations();
        
        int greenStations = 0;
        for (Station station : stations) {
            if (station != null && station.getCode() != null && station.getCode().startsWith("G")) {
                greenStations++;
            }
        }
        
        assert greenStations > 0 : "Should have Green line stations";
        System.out.println("✓ Found " + greenStations + " Green line stations");
    }
    
    public void testStationNameVariety() {
        setUp();
        ArrayList<Station> stations = stationController.getStations();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> uniqueNames = new ArrayList<>();
        
        for (Station station : stations) {
            if (station != null && station.getName() != null) {
                names.add(station.getName());
                if (!uniqueNames.contains(station.getName())) {
                    uniqueNames.add(station.getName());
                }
            }
        }
        
        assert names.size() > 10 : "Should have a good variety of station names";
        System.out.println("✓ Found " + names.size() + " total stations, " + uniqueNames.size() + " unique station names");
        
        // Note: Duplicate names are OK in real subway systems (e.g., "Main St" on different lines)
        // as long as station codes are unique
        if (names.size() != uniqueNames.size()) {
            System.out.println("  ℹ️  Note: " + (names.size() - uniqueNames.size()) + " stations share names with others (this is realistic for subway systems)");
        }
    }
    
    public void testMemoryEfficiency() {
        StationController controller1 = new StationController();
        StationController controller2 = new StationController();
        
        // Both controllers should return the same static list
        ArrayList<Station> stations1 = controller1.getStations();
        ArrayList<Station> stations2 = controller2.getStations();
        
        assert stations1 == stations2 : "Controllers should share the same static stations list for memory efficiency";
        System.out.println("✓ Multiple controllers share the same station data (memory efficient)");
    }
    
    public void testStationStringFields() {
        setUp();
        ArrayList<Station> stations = stationController.getStations();
        
        for (Station station : stations) {
            if (station != null) {
                // Test that string fields don't contain problematic characters
                if (station.getName() != null) {
                    assert !station.getName().contains("\n") : "Station name contains newline";
                    assert !station.getName().contains("\t") : "Station name contains tab";
                }
                
                if (station.getCode() != null) {
                    assert !station.getCode().contains(" ") : "Station code contains space";
                    assert station.getCode().equals(station.getCode().trim()) : "Station code has leading/trailing spaces";
                }
            }
        }
        
        System.out.println("✓ Station string fields are properly formatted");
    }
    
    // Test Runner
    public static void main(String[] args) {
        StationControllerTest test = new StationControllerTest();
        
        System.out.println("Running StationController Tests...");
        System.out.println("=====================================");
        
        try {
            test.testConstructorInitialization();
            test.testGetStationsReturnsValidList();
            test.testStationDataIntegrity();
            test.testStationCoordinates();
            test.testStationCodes();
            test.testStationNumbers();
            test.testUniqueStationCodes();
            test.testRedLineStations();
            test.testBlueLineStations();
            test.testGreenLineStations();
            test.testStationNameVariety();
            test.testMemoryEfficiency();
            test.testStationStringFields();
            
            System.out.println("\n🎉 All StationController Tests PASSED! (13/13)");
            System.out.println("✅ Station data loading and management is working correctly");
            
        } catch (AssertionError e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Unexpected error during testing: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 