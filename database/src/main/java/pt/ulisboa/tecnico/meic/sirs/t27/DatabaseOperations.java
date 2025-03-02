package main.java.pt.ulisboa.tecnico.meic.sirs.t27;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseOperations {
    /**
     * Retrieves the current configuration details for a specific car.
     * 
     * @param carID The unique identifier of the car
     * @return A string containing the configuration details from the current
     *         configuration
     */
    public static String getCarConfigurations(String carID) {
        String query = "SELECT c.configuration_details FROM car ca JOIN configurations c ON ca.atual_configuration = c.configID WHERE ca.carID = ?";
        String result = "";

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, carID);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    result = rs.getString("configuration_details");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Retrieves the default configuration details for a specific car.
     * 
     * @param carID The unique identifier of the car
     * @return A string containing the default configuration details
     */
    public static String getDefaultCarConfigurations(String carID) {
        String query = "SELECT c.configuration_details FROM configurations c WHERE c.carID = ? AND c.is_default = true";
        String result = "";

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, carID);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    result = rs.getString("configuration_details");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Retrieves all tests performed on a specific car, ordered by timestamp in
     * descending order.
     * 
     * @param carID The unique identifier of the car
     * @return A list of strings containing the test details
     */
    public static List<String> getTests(String carID) {
        String query = "SELECT mechanicID, tests_performed FROM maintenance_logs WHERE carID = ? ORDER BY timestamp DESC";
        List<String> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, carID);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String mechanicID = rs.getString("mechanicID");
                    String testsPerformed = rs.getString("tests_performed");
                    TestRecord record = new TestRecord(mechanicID, testsPerformed);
                    result.add(record.toString());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Retrieves the firmware update history for a specific car, ordered by
     * timestamp in descending order.
     * 
     * @param carID The unique identifier of the car
     * @return A list of FirmwareUpdate objects containing update details <updateID,
     *         firmware_version>
     */
    public static List<String> getFirmwareUpdates(String carID) {
        String query = "SELECT firmware_version FROM firmware_updates WHERE carID = ? ORDER BY timestamp DESC ";
        List<String> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, carID);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    String firmwareVersion = rs.getString("firmware_version");
                    result.add(firmwareVersion.toString());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Checks if a specific car is in maintenance mode.
     * 
     * @param carID The unique identifier of the car
     * @return Boolean indicating whether the car is in maintenance mode
     */
    public static Boolean getMaintenanceMode(String carID) {
        String query = "SELECT is_maintenance_mode FROM car WHERE carID = ?";
        Boolean result = false;

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, carID);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    result = rs.getBoolean("is_maintenance_mode");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Checks if a specific car is on or off.
     * 
     * @param carID The unique identifier of the car
     * @return Boolean indicating whether the car is on (true) or off (false)
     */
    public static Boolean getCarOnOff(String carID) {
        String query = "SELECT is_on FROM car WHERE carID = ?";
        Boolean result = false;

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, carID);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    result = rs.getBoolean("is_on");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Checks a specific car's battery level.
     * 
     * @param carID The unique identifier of the car
     * @return int indicating the battery level
     */
    public static int getCarBatteryLevel(String carID) {
        String query = "SELECT battery_level FROM car WHERE carID = ?";
        int result = -1;

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, carID);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    result = rs.getInt("battery_level");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Sets a new configuration for a specific car.
     * 
     * @param carID         The unique identifier of the car
     * @param userID        The ID of the user setting the configuration
     * @param configuration The new configuration details in JSON format
     * @param signature     The digital signature of the configuration
     */
    public static void setCarConfigurations(String carID, String userID, String configuration) {
        // Atualizamos a query para incluir o campo is_default
        String insertConfigQuery = "INSERT INTO configurations (carID, is_default, userID, configuration_details) VALUES (?, FALSE, ?, ?)";
        String updateCarQuery = "UPDATE car SET atual_configuration = ?, last_config_timestamp = ? WHERE carID = ?";

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement insertConfigStatement = conn.prepareStatement(insertConfigQuery,
                        Statement.RETURN_GENERATED_KEYS);
                PreparedStatement updateCarStatement = conn.prepareStatement(updateCarQuery)) {

            conn.setAutoCommit(false); // Inicia uma transação

            insertConfigStatement.setString(1, carID);
            insertConfigStatement.setString(2, userID);
            insertConfigStatement.setString(3, configuration);

            insertConfigStatement.executeUpdate();
            try (ResultSet rs = insertConfigStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    int configID = rs.getInt(1);

                    updateCarStatement.setInt(1, configID);
                    updateCarStatement.setObject(2, LocalDateTime.now());
                    updateCarStatement.setString(3, carID);

                    updateCarStatement.executeUpdate();

                    System.out.println("Car Configuration updated.");
                    conn.commit(); // Confirma a transação
                } else {
                    throw new SQLException("Failed to retrieve configuration ID.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the maintenance mode status for a specific car.
     * 
     * @param carID       The unique identifier of the car
     * @param maintenance Boolean value indicating whether to enable or disable
     *                    maintenance mode
     */
    public static void setMaintenanceMode(String carID, Boolean maintenance) {
        String query = "UPDATE car SET is_maintenance_mode = ?, last_maintenance_timestamp = ? WHERE carID = ?";

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setBoolean(1, maintenance);
            statement.setObject(2, maintenance ? LocalDateTime.now() : null);
            statement.setString(3, carID);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No car found with carID: " + carID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Turns a specific car on or off.
     * 
     * @param carID  The unique identifier of the car
     * @param on_off Boolean value indicating to set the car to
     *               on (true) or off (false)
     */
    public static void setCarOnOff(String carID, Boolean on_off) {
        String query = "UPDATE car SET is_on = ? WHERE carID = ?";

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setBoolean(1, on_off);
            statement.setString(2, carID);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No car found with carID: " + carID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Records a maintenance test performed on a specific car.
     * 
     * @param carID          The unique identifier of the car
     * @param mechanicID     The ID of the mechanic who performed the test
     * @param testsPerformed The details of the tests performed in JSON format
     * @param signature      The digital signature of the manufacturer that
     *                       performed the test
     */
    public static void setTest(String carID, String mechanicID, String testsPerformed) {
        String query = "INSERT INTO maintenance_logs (carID, mechanicID, tests_performed, signature) "
                + "VALUES (?, ?, ?::jsonb, ?)";

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, carID);
            statement.setString(2, mechanicID);
            statement.setString(3, testsPerformed);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Falha ao inserir log de manutenção para carID: " + carID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Manages firmware updates for a specific car.
     * 
     * @param carID           The unique identifier of the car
     * @param firmwareVersion The version of the firmware
     * @param manufacturerID  The ID of the manufacturer providing the update
     * @param signature       The digital signature of the firmware update
     * @param isInstall       Boolean indicating whether to install (true) or
     *                        download (false) the update
     */
    public static void setFirmwareUpdate(String carID, String firmwareVersion, String manufacturerID,
            boolean isInstall) {
        if (isInstall) {
            installFirmwareUpdate(carID, firmwareVersion);
        } else {
            downloadFirmwareUpdate(carID, firmwareVersion, manufacturerID);
        }
    }

    /**
     * Downloads a new firmware update for a specific car.
     * 
     * @param carID           The unique identifier of the car
     * @param firmwareVersion The version of the firmware to download
     * @param manufacturerID  The ID of the manufacturer providing the update
     * @param signature       The digital signature of the firmware update
     */
    private static void downloadFirmwareUpdate(String carID, String firmwareVersion, String manufacturerID) {
        String query = "INSERT INTO firmware_updates (carID, manufacturerID, firmware_version) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, carID);
            statement.setString(2, manufacturerID);
            statement.setString(3, firmwareVersion);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to download firmware in car: " + carID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Installs a firmware update on a specific car.
     * 
     * @param carID           The unique identifier of the car
     * @param firmwareVersion The version of the firmware to install
     */
    private static void installFirmwareUpdate(String carID, String firmwareVersion) {
        String query = "UPDATE car SET firmware_version = ?, last_maintenance_timestamp = ? WHERE carID = ?";

        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setString(1, firmwareVersion);
            statement.setObject(2, LocalDateTime.now());
            statement.setString(3, carID);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to install firmware in car: " + carID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Authenticates a user based on the provided username and password.
     * 
     * @param query    The SQL query to execute
     * @param password The password provided by the user
     * @param user     The username of the user
     * @return A string indicating the result of the authentication process
     */
    private static String authenticate(String query, String password, String user) {
        try (Connection conn = DatabaseConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the username parameter
            stmt.setString(1, user);

            // Execute the query
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    System.out.println(storedHash);

                    // Verify the provided password against the stored hash
                    if (BCrypt.checkpw(password, storedHash)) {
                        return "OK";
                    } else {
                        return "Invalid username or password";
                    }
                } else {
                    return "Invalid username or password";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error during authentication: " + e.getMessage();
        }
    }

    public static String authenticateCarOwner(String user, String password) {
        String query = "SELECT password_hash FROM owner WHERE ownerID = ?";
        return authenticate(query, password, user);
    }

    public static String authenticateManufcaturer(String user, String password) {
        String query = "SELECT password_hash FROM manufacturer WHERE manufacturerID = ?";
        return authenticate(query, password, user);
    }

    public static String authenticateMechanic(String user, String password) {
        String query = "SELECT password_hash FROM mechanic WHERE mechanicID = ?";
        return authenticate(query, password, user);
    }

}
