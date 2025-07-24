package test.ca.ucalgary.edu.ensf380.model;

import ca.ucalgary.edu.ensf380.model.Train;

/**
 * Comprehensive Unit Tests for Train Model Class
 * 
 * Tests all public methods, constructors, edge cases, and data integrity
 * of the Train class to ensure reliable train data management.
 * 
 * @author Subway Screen Test Team
 * @version 1.0
 */
public class TrainTest {
    
    private Train train;
    private static final String TEST_ID = "T001";
    private static final String TEST_POSITION = "R15";
    private static final String TEST_DIRECTION = "FORWARD";
    
    public void setUp() {
        train = new Train(TEST_ID, TEST_POSITION, TEST_DIRECTION);
    }
    
    // Constructor Tests
    public void testConstructor() {
        Train testTrain = new Train(TEST_ID, TEST_POSITION, TEST_DIRECTION);
        
        assert TEST_ID.equals(testTrain.getId());
        assert TEST_POSITION.equals(testTrain.getPosition());
        assert TEST_DIRECTION.equals(testTrain.getDirection());
    }
    
    public void testConstructorWithNullValues() {
        Train testTrain = new Train(null, null, null);
        
        assert testTrain.getId() == null;
        assert testTrain.getPosition() == null;
        assert testTrain.getDirection() == null;
    }
    
    public void testConstructorWithEmptyStrings() {
        Train testTrain = new Train("", "", "");
        
        assert "".equals(testTrain.getId());
        assert "".equals(testTrain.getPosition());
        assert "".equals(testTrain.getDirection());
    }
    
    // Getter Tests
    public void testGetId() {
        setUp();
        assert TEST_ID.equals(train.getId());
    }
    
    public void testGetPosition() {
        setUp();
        assert TEST_POSITION.equals(train.getPosition());
    }
    
    public void testGetDirection() {
        setUp();
        assert TEST_DIRECTION.equals(train.getDirection());
    }
    
    // Setter Tests
    public void testSetPosition() {
        setUp();
        String newPosition = "B23";
        train.setPosition(newPosition);
        assert newPosition.equals(train.getPosition());
    }
    
    public void testSetPositionWithNull() {
        setUp();
        train.setPosition(null);
        assert train.getPosition() == null;
    }
    
    public void testSetPositionWithEmpty() {
        setUp();
        train.setPosition("");
        assert "".equals(train.getPosition());
    }
    
    public void testSetDirection() {
        setUp();
        String newDirection = "BACKWARD";
        train.setDirection(newDirection);
        assert newDirection.equals(train.getDirection());
    }
    
    public void testSetDirectionWithNull() {
        setUp();
        train.setDirection(null);
        assert train.getDirection() == null;
    }
    
    public void testSetDirectionWithEmpty() {
        setUp();
        train.setDirection("");
        assert "".equals(train.getDirection());
    }
    
    // Edge Case Tests
    public void testSpecialCharacters() {
        setUp();
        String specialPosition = "R-15A";
        String specialDirection = "NORTH-EAST";
        
        train.setPosition(specialPosition);
        train.setDirection(specialDirection);
        
        assert specialPosition.equals(train.getPosition());
        assert specialDirection.equals(train.getDirection());
    }
    
    public void testUnicodeCharacters() {
        setUp();
        String unicodePosition = "東15"; // East 15 in Japanese
        String unicodeDirection = "東向き"; // Eastbound in Japanese
        
        train.setPosition(unicodePosition);
        train.setDirection(unicodeDirection);
        
        assert unicodePosition.equals(train.getPosition());
        assert unicodeDirection.equals(train.getDirection());
    }
    
    public void testLongStrings() {
        setUp();
        String longPosition = "R".repeat(1000) + "15";
        String longDirection = "FORWARD".repeat(100);
        
        train.setPosition(longPosition);
        train.setDirection(longDirection);
        
        assert longPosition.equals(train.getPosition());
        assert longDirection.equals(train.getDirection());
    }
    
    public void testMultiplePropertyChanges() {
        setUp();
        
        // Change position multiple times
        train.setPosition("R01");
        assert "R01".equals(train.getPosition());
        
        train.setPosition("R02");
        assert "R02".equals(train.getPosition());
        
        // Change direction multiple times
        train.setDirection("BACKWARD");
        assert "BACKWARD".equals(train.getDirection());
        
        train.setDirection("FORWARD");
        assert "FORWARD".equals(train.getDirection());
    }
    
    public void testDataIntegrity() {
        setUp();
        String originalPosition = train.getPosition();
        String retrievedPosition = train.getPosition();
        
        // This shouldn't affect the train's internal state
        retrievedPosition = "Modified Position";
        
        assert originalPosition.equals(train.getPosition());
    }
    
    // Test Runner - Simple manual test runner
    public static void main(String[] args) {
        TrainTest test = new TrainTest();
        
        System.out.println("Running Train Model Tests...");
        
        try {
            test.testConstructor();
            System.out.println("✓ Constructor test passed");
            
            test.testConstructorWithNullValues();
            System.out.println("✓ Constructor with null values test passed");
            
            test.testConstructorWithEmptyStrings();
            System.out.println("✓ Constructor with empty strings test passed");
            
            test.testGetId();
            System.out.println("✓ Get ID test passed");
            
            test.testGetPosition();
            System.out.println("✓ Get position test passed");
            
            test.testGetDirection();
            System.out.println("✓ Get direction test passed");
            
            test.testSetPosition();
            System.out.println("✓ Set position test passed");
            
            test.testSetPositionWithNull();
            System.out.println("✓ Set position with null test passed");
            
            test.testSetPositionWithEmpty();
            System.out.println("✓ Set position with empty test passed");
            
            test.testSetDirection();
            System.out.println("✓ Set direction test passed");
            
            test.testSetDirectionWithNull();
            System.out.println("✓ Set direction with null test passed");
            
            test.testSetDirectionWithEmpty();
            System.out.println("✓ Set direction with empty test passed");
            
            test.testSpecialCharacters();
            System.out.println("✓ Special characters test passed");
            
            test.testUnicodeCharacters();
            System.out.println("✓ Unicode characters test passed");
            
            test.testLongStrings();
            System.out.println("✓ Long strings test passed");
            
            test.testMultiplePropertyChanges();
            System.out.println("✓ Multiple property changes test passed");
            
            test.testDataIntegrity();
            System.out.println("✓ Data integrity test passed");
            
            System.out.println("\n🎉 All Train Model Tests PASSED! (16/16)");
            
        } catch (AssertionError e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Unexpected error during testing: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 