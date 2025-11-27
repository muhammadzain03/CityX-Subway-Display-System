package ca.ucalgary.edu.ensf380.util;

import java.sql.*;

/**
 * Database utility class for managing MySQL database connections and operations
 * for the Subway Screen Application. Provides centralized database access with
 * proper resource management and error handling.
 * 
 * @author Subway Screen Development Team
 * @version 2.0
 */
public class DatabaseUtil {
    private Connection dbConnect;
    private ResultSet results;

    public DatabaseUtil() {}

    /**
     * Creates a connection to the database with improved error handling and logging.
     * 
     * @return true if connection successful, false otherwise
     */
    public boolean createConnection() {
        try {
            AppLogger.debug("Attempting to connect to database: " + AppConstants.DB_URL);
            
            // Set connection timeout
            DriverManager.setLoginTimeout(AppConstants.DB_CONNECTION_TIMEOUT / 1000);
            
            dbConnect = DriverManager.getConnection(
                AppConstants.DB_URL, 
                AppConstants.DB_USERNAME, 
                AppConstants.DB_PASSWORD
            );
            
            // Set connection properties for better performance
            dbConnect.setAutoCommit(true);
            dbConnect.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            
            AppLogger.data("Database", "Connection established successfully");
            return true;
            
        } catch (SQLException e) {
            AppLogger.error(AppConstants.ERROR_DATABASE_CONNECTION + ": " + e.getMessage(), e);
            return false;
        } catch (Exception e) {
            AppLogger.error("Unexpected error during database connection", e);
            return false;
        }
    }

    /**
     * Executes a SELECT query and returns the result set with proper validation.
     *
     * @param query the SQL query to execute
     * @return ResultSet containing query results, or null if query failed
     */
    public ResultSet selectQuery(String query) {
        if (!isConnectionValid()) {
            AppLogger.error("Database connection is not available for query execution");
            return null;
        }
        
        if (query == null || query.trim().isEmpty()) {
            AppLogger.error("Query cannot be null or empty");
            return null;
        }
        
        // Basic SQL injection protection - ensure it's a SELECT statement
        String trimmedQuery = query.trim().toLowerCase();
        if (!trimmedQuery.startsWith("select")) {
            AppLogger.error("Only SELECT queries are allowed in selectQuery method");
            return null;
        }
        
        try {
            AppLogger.debug("Executing query: " + query);
            long startTime = System.currentTimeMillis();
            
            Statement statement = dbConnect.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
            );
            results = statement.executeQuery(query);
            
            long duration = System.currentTimeMillis() - startTime;
            AppLogger.performance("Database query", duration);
            AppLogger.data("Database", "Query executed successfully");
            
            return results;
            
        } catch (SQLException e) {
            AppLogger.error("Database query failed: " + query, e);
            return null;
        }
    }

    /**
     * Inserts a new advertisement into the database with validation and proper error handling.
     *
     * @param id the advertisement ID
     * @param mediaType the type of media
     * @param mediaPath the path to the media
     * @param displayDuration the display duration in seconds
     * @return true if insertion successful, false otherwise
     */
    public boolean insertAdvertisement(int id, String mediaType, String mediaPath, int displayDuration) {
        if (!isConnectionValid()) {
            AppLogger.error("Database connection is not available for insert operation");
            return false;
        }
        
        // Validate input parameters
        if (!isValidMediaType(mediaType)) {
            AppLogger.error("Invalid media type: " + mediaType);
            return false;
        }
        
        if (mediaPath == null || mediaPath.trim().isEmpty()) {
            AppLogger.error("Media path cannot be null or empty");
            return false;
        }
        
        if (displayDuration <= 0) {
            AppLogger.error("Display duration must be positive: " + displayDuration);
            return false;
        }
        
        String query = "INSERT INTO advertisements (id, media_type, media_path, display_duration) VALUES (?,?,?,?)";
        
        try (PreparedStatement newStatement = dbConnect.prepareStatement(query)) {
            AppLogger.debug("Inserting advertisement: ID=" + id + ", Type=" + mediaType + ", Path=" + mediaPath);
            
            newStatement.setInt(1, id);
            newStatement.setString(2, mediaType);
            newStatement.setString(3, mediaPath);
            newStatement.setInt(4, displayDuration);

            int rowCount = newStatement.executeUpdate();
            
            if (rowCount > 0) {
                AppLogger.data("Database", "Advertisement inserted successfully (ID: " + id + ")");
                return true;
            } else {
                AppLogger.warning("No rows affected during advertisement insertion");
                return false;
            }
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // MySQL duplicate key error
                AppLogger.warning("Advertisement with ID " + id + " already exists");
            } else {
                AppLogger.error("Failed to insert advertisement (ID: " + id + ")", e);
            }
            return false;
        }
    }

    /**
     * Deletes an advertisement from the database based on the media path with proper validation.
     *
     * @param mediaPath the path of the media to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteAdvertisement(String mediaPath) {
        if (!isConnectionValid()) {
            AppLogger.error("Database connection is not available for delete operation");
            return false;
        }
        
        if (mediaPath == null || mediaPath.trim().isEmpty()) {
            AppLogger.error("Media path cannot be null or empty for deletion");
            return false;
        }
        
        String query = "DELETE FROM advertisements WHERE media_path = ?";
        
        try (PreparedStatement preparedStatement = dbConnect.prepareStatement(query)) {
            AppLogger.debug("Deleting advertisement with path: " + mediaPath);
            
            preparedStatement.setString(1, mediaPath);
            int rowCount = preparedStatement.executeUpdate();
            
            if (rowCount > 0) {
                AppLogger.data("Database", "Advertisement deleted successfully (Path: " + mediaPath + ")");
                return true;
            } else {
                AppLogger.warning("No advertisement found with path: " + mediaPath);
                return false;
            }
            
        } catch (SQLException e) {
            AppLogger.error("Failed to delete advertisement (Path: " + mediaPath + ")", e);
            return false;
        }
    }
    
    /**
     * Validates if the media type is supported
     */
    private boolean isValidMediaType(String mediaType) {
        if (mediaType == null) return false;
        
        for (String supportedType : AppConstants.SUPPORTED_MEDIA_TYPES) {
            if (supportedType.equalsIgnoreCase(mediaType)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if the database connection is valid and available
     */
    private boolean isConnectionValid() {
        try {
            return dbConnect != null && !dbConnect.isClosed() && dbConnect.isValid(2);
        } catch (SQLException e) {
            AppLogger.debug("Connection validation failed", e);
            return false;
        }
    }
    
    /**
     * Tests the database connection
     * 
     * @return true if connection test successful, false otherwise
     */
    public boolean testConnection() {
        if (!isConnectionValid()) {
            return false;
        }
        
        try (Statement stmt = dbConnect.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT 1");
            boolean hasResult = rs.next();
            AppLogger.debug("Database connection test " + (hasResult ? "passed" : "failed"));
            return hasResult;
        } catch (SQLException e) {
            AppLogger.error("Database connection test failed", e);
            return false;
        }
    }
    
    /**
     * Gets the current connection status
     * 
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return isConnectionValid();
    }

    /**
     * Closes the database connection and ResultSet with proper error handling.
     */
    public void close() {
        try {
            // Close ResultSet if exists
            if (results != null && !results.isClosed()) {
                results.close();
                results = null;
                AppLogger.debug("ResultSet closed");
            }
            
            // Close Connection if exists
            if (dbConnect != null && !dbConnect.isClosed()) {
                dbConnect.close();
                dbConnect = null;
                AppLogger.data("Database", "Connection closed successfully");
            }
            
        } catch (SQLException e) {
            AppLogger.error("Error occurred while closing database connection", e);
        }
    }
    

}

