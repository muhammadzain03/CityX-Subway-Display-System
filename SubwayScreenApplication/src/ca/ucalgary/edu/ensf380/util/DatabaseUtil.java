package ca.ucalgary.edu.ensf380.util;

import java.sql.*;

public class DatabaseUtil {
    private Connection dbConnect;
    private ResultSet results;

    public DatabaseUtil() {
        // Default constructor
    }

    /**
     * Creates a connection to the database.
     */
    public void createConnection() {
        try {
            dbConnect = DriverManager.getConnection("jdbc:mysql://localhost:3306/subway_screen", "root", "Qwerty$455");
            System.out.println("Connection to MySQL database established.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Executes a SELECT query and returns the result set.
     *
     * @param query the SQL query to execute
     * @return ResultSet containing query results
     */
    public ResultSet selectQuery(String query) {
        ResultSet results = null;
        try {
            Statement statement = dbConnect.createStatement();
            results = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Inserts a new advertisement into the database.
     *
     * @param id the advertisement ID
     * @param mediaType the type of media
     * @param mediaPath the path to the media
     * @param displayDuration the display duration in seconds
     */
    public void insertAdvertisement(int id, String mediaType, String mediaPath, int displayDuration) {
        String query = "INSERT INTO advertisements (id, media_type, media_path, display_duration) VALUES (?,?,?,?)";
        try (PreparedStatement newStatement = dbConnect.prepareStatement(query)) {
            newStatement.setInt(1, id);
            newStatement.setString(2, mediaType);
            newStatement.setString(3, mediaPath);
            newStatement.setInt(4, displayDuration);

            int rowCount = newStatement.executeUpdate();
            System.out.println("Rows affected: " + rowCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes an advertisement from the database based on the media path.
     *
     * @param mediaPath the path of the media to delete
     */
    public void deleteAdvertisement(String mediaPath) {
        String query = "DELETE FROM advertisements WHERE media_path = ?";
        try (PreparedStatement preparedStatement = dbConnect.prepareStatement(query)) {
            preparedStatement.setString(1, mediaPath);

            int rowCount = preparedStatement.executeUpdate();
            System.out.println("Rows affected: " + rowCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the database connection and ResultSet.
     */
    public void close() {
        try {
            if (results != null) results.close();
            if (dbConnect != null) dbConnect.close();
            System.out.println("Connection to MySQL database closed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
