package test.ca.ucalgary.edu.ensf380.model;

import ca.ucalgary.edu.ensf380.model.Station;

/**
 * Comprehensive Unit Tests for Station Model Class
 * 
 * Tests all public methods, constructors, edge cases, and data integrity
 * of the Station class to ensure reliable subway station data management.
 * 
 * @author Subway Screen Test Team
 * @version 1.0
 */
public class StationTest {
    
    private Station station;
    private static final String TEST_NAME = "Downtown Central";
    private static final String TEST_CODE = "DC01";
    private static final String TEST_NUMBER = "15";
    private static final double TEST_X = 750.5;
    private static final double TEST_Y = 450.25;
    
    public void setUp() {
        station = new Station(TEST_NAME, TEST_CODE, TEST_NUMBER, TEST_X, TEST_Y);
    }
    
    // Constructor Tests
    public void testFullConstructor() {
        Station testStation = new Station(TEST_NAME, TEST_CODE, TEST_NUMBER, TEST_X, TEST_Y);
        
        assert TEST_NAME.equals(testStation.getName());
        assert TEST_CODE.equals(testStation.getCode());
        assert TEST_NUMBER.equals(testStation.getNumber());
        assert TEST_X == testStation.getX();
        assert TEST_Y == testStation.getY();
    }
    
    public void testPartialConstructor() {
        Station testStation = new Station(TEST_NAME, TEST_CODE, TEST_NUMBER);
        
        assert TEST_NAME.equals(testStation.getName());
        assert TEST_CODE.equals(testStation.getCode());
        assert TEST_NUMBER.equals(testStation.getNumber());
        assert 0.0 == testStation.getX(); // Default double value
        assert 0.0 == testStation.getY(); // Default double value
    }
    
    public void testConstructorWithNullName() {
        Station testStation = new Station(null, TEST_CODE, TEST_NUMBER, TEST_X, TEST_Y);
        assert testStation.getName() == null;
        assert TEST_CODE.equals(testStation.getCode());
    }
    
    public void testConstructorWithEmptyStrings() {
        Station testStation = new Station("", "", "", TEST_X, TEST_Y);
        assert "".equals(testStation.getName());
        assert "".equals(testStation.getCode());
        assert "".equals(testStation.getNumber());
    }
    
    // Getter Tests
    public void testGetName() {
        setUp();
        assert TEST_NAME.equals(station.getName());
    }
    
    public void testGetCode() {
        setUp();
        assert TEST_CODE.equals(station.getCode());
    }
    
    public void testGetNumber() {
        setUp();
        assert TEST_NUMBER.equals(station.getNumber());
    }
    
    public void testGetX() {
        setUp();
        assert TEST_X == station.getX();
    }
    
    public void testGetY() {
        setUp();
        assert TEST_Y == station.getY();
    }
    
    // Setter Tests
    public void testSetName() {
        setUp();
        String newName = "North Terminal";
        station.setName(newName);
        assert newName.equals(station.getName());
    }
    
    public void testSetNameWithNull() {
        setUp();
        station.setName(null);
        assert station.getName() == null;
    }
    
    public void testSetNameWithEmpty() {
        setUp();
        station.setName("");
        assert "".equals(station.getName());
    }
    
    public void testSetCode() {
        setUp();
        String newCode = "NT01";
        station.setCode(newCode);
        assert newCode.equals(station.getCode());
    }
    
    public void testSetCodeWithNull() {
        setUp();
        station.setCode(null);
        assert station.getCode() == null;
    }
    
    public void testSetNumber() {
        setUp();
        String newNumber = "42";
        station.setNumber(newNumber);
        assert newNumber.equals(station.getNumber());
    }
    
    public void testSetNumberWithNull() {
        setUp();
        station.setNumber(null);
        assert station.getNumber() == null;
    }
    
    public void testSetX() {
        setUp();
        double newX = 999.99;
        station.setX(newX);
        assert newX == station.getX();
    }
    
    public void testSetXWithNegative() {
        setUp();
        double negativeX = -500.0;
        station.setX(negativeX);
        assert negativeX == station.getX();
    }
    
    public void testSetXWithZero() {
        setUp();
        station.setX(0.0);
        assert 0.0 == station.getX();
    }
    
    public void testSetY() {
        setUp();
        double newY = 888.88;
        station.setY(newY);
        assert newY == station.getY();
    }
    
    public void testSetYWithNegative() {
        setUp();
        double negativeY = -300.0;
        station.setY(negativeY);
        assert negativeY == station.getY();
    }
    
    public void testSetYWithZero() {
        setUp();
        station.setY(0.0);
        assert 0.0 == station.getY();
    }
    
    // Edge Case Tests
    public void testCoordinatePrecision() {
        setUp();
        double smallX = 0.0001;
        double smallY = 0.0001;
        station.setX(smallX);
        station.setY(smallY);
        
        assert Math.abs(smallX - station.getX()) < 0.00001;
        assert Math.abs(smallY - station.getY()) < 0.00001;
    }
    
    public void testLargeCoordinates() {
        setUp();
        double largeX = 999999.999;
        double largeY = 999999.999;
        station.setX(largeX);
        station.setY(largeY);
        
        assert largeX == station.getX();
        assert largeY == station.getY();
    }
    
    public void testSpecialCharacters() {
        setUp();
        String specialName = "Gare du Nord - François Mitterrand";
        String specialCode = "GN-FM01";
        String specialNumber = "B-15A";
        
        station.setName(specialName);
        station.setCode(specialCode);
        station.setNumber(specialNumber);
        
        assert specialName.equals(station.getName());
        assert specialCode.equals(station.getCode());
        assert specialNumber.equals(station.getNumber());
    }
    
    public void testUnicodeCharacters() {
        setUp();
        String unicodeName = "新宿駅"; // Shinjuku Station in Japanese
        String unicodeCode = "新01";
        
        station.setName(unicodeName);
        station.setCode(unicodeCode);
        
        assert unicodeName.equals(station.getName());
        assert unicodeCode.equals(station.getCode());
    }
    
    public void testDataIntegrity() {
        setUp();
        String originalName = station.getName();
        String retrievedName = station.getName();
        
        // This shouldn't affect the station's internal state
        retrievedName = "Modified Name";
        
        assert originalName.equals(station.getName());
    }
    
    public void testMultiplePropertyChanges() {
        setUp();
        // Change all properties
        station.setName("New Name");
        station.setCode("NN01");
        station.setNumber("99");
        station.setX(100.0);
        station.setY(200.0);
        
        // Verify all changes persisted
        assert "New Name".equals(station.getName());
        assert "NN01".equals(station.getCode());
        assert "99".equals(station.getNumber());
        assert 100.0 == station.getX();
        assert 200.0 == station.getY();
    }
    
    public void testLongStrings() {
        setUp();
        String longName = "A".repeat(1000);
        String longCode = "B".repeat(500);
        String longNumber = "1".repeat(100);
        
        station.setName(longName);
        station.setCode(longCode);
        station.setNumber(longNumber);
        
        assert longName.equals(station.getName());
        assert longCode.equals(station.getCode());
        assert longNumber.equals(station.getNumber());
    }
    
    // Test Runner - Simple manual test runner
    public static void main(String[] args) {
        StationTest test = new StationTest();
        
        System.out.println("Running Station Model Tests...");
        
        try {
            test.testFullConstructor();
            System.out.println("✓ Full constructor test passed");
            
            test.testPartialConstructor();
            System.out.println("✓ Partial constructor test passed");
            
            test.testConstructorWithNullName();
            System.out.println("✓ Constructor with null name test passed");
            
            test.testConstructorWithEmptyStrings();
            System.out.println("✓ Constructor with empty strings test passed");
            
            test.testGetName();
            System.out.println("✓ Get name test passed");
            
            test.testGetCode();
            System.out.println("✓ Get code test passed");
            
            test.testGetNumber();
            System.out.println("✓ Get number test passed");
            
            test.testGetX();
            System.out.println("✓ Get X test passed");
            
            test.testGetY();
            System.out.println("✓ Get Y test passed");
            
            test.testSetName();
            System.out.println("✓ Set name test passed");
            
            test.testSetNameWithNull();
            System.out.println("✓ Set name with null test passed");
            
            test.testSetNameWithEmpty();
            System.out.println("✓ Set name with empty test passed");
            
            test.testSetCode();
            System.out.println("✓ Set code test passed");
            
            test.testSetCodeWithNull();
            System.out.println("✓ Set code with null test passed");
            
            test.testSetNumber();
            System.out.println("✓ Set number test passed");
            
            test.testSetNumberWithNull();
            System.out.println("✓ Set number with null test passed");
            
            test.testSetX();
            System.out.println("✓ Set X test passed");
            
            test.testSetXWithNegative();
            System.out.println("✓ Set X with negative test passed");
            
            test.testSetXWithZero();
            System.out.println("✓ Set X with zero test passed");
            
            test.testSetY();
            System.out.println("✓ Set Y test passed");
            
            test.testSetYWithNegative();
            System.out.println("✓ Set Y with negative test passed");
            
            test.testSetYWithZero();
            System.out.println("✓ Set Y with zero test passed");
            
            test.testCoordinatePrecision();
            System.out.println("✓ Coordinate precision test passed");
            
            test.testLargeCoordinates();
            System.out.println("✓ Large coordinates test passed");
            
            test.testSpecialCharacters();
            System.out.println("✓ Special characters test passed");
            
            test.testUnicodeCharacters();
            System.out.println("✓ Unicode characters test passed");
            
            test.testDataIntegrity();
            System.out.println("✓ Data integrity test passed");
            
            test.testMultiplePropertyChanges();
            System.out.println("✓ Multiple property changes test passed");
            
            test.testLongStrings();
            System.out.println("✓ Long strings test passed");
            
            System.out.println("\n🎉 All Station Model Tests PASSED! (29/29)");
            
        } catch (AssertionError e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Unexpected error during testing: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 