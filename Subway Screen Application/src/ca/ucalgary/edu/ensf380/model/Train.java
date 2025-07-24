/**
 * Train Data Model
 * 
 * Represents a train in the subway system with its current position and movement
 * direction. Used throughout the application to track and display real-time
 * train information on the subway screen.
 * 
 * This model encapsulates:
 * - Train identification number
 * - Current station position
 * - Movement direction (forward/backward)
 * 
 * @author Subway Screen Development Team
 * @version 2.0
 */
package ca.ucalgary.edu.ensf380.model;

public class Train {
    private String id;
    private String position;
    private String direction;

    public Train(String id, String position, String direction) {
        this.id = id;
        this.position = position;
        this.direction = direction;
    }

    public String getId() {
        return id;
    }

    public String getPosition() {
        return position;
    }

    public String getDirection() {
        return direction;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
