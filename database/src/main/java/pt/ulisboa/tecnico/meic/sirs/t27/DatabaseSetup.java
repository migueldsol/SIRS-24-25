package main.java.pt.ulisboa.tecnico.meic.sirs.t27;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;

public class DatabaseSetup {
    public static void main(String[] args) {
        String[] dropTableStatements = {
                "DROP TABLE IF EXISTS firmware_updates CASCADE",
                "DROP TABLE IF EXISTS maintenance_logs CASCADE",
                "DROP TABLE IF EXISTS configurations CASCADE",
                "DROP TABLE IF EXISTS car CASCADE",
                "DROP TABLE IF EXISTS manufacturer CASCADE",
                "DROP TABLE IF EXISTS mechanic CASCADE",
                "DROP TABLE IF EXISTS owner CASCADE"
        };

        String createOwnerTable = "CREATE TABLE owner (\n"
                + "    ownerID VARCHAR PRIMARY KEY,\n"
                + "    name VARCHAR NOT NULL,\n"
                + "    owner_public_key BYTEA NOT NULL\n"
                + ");";

        String createMechanicTable = "CREATE TABLE mechanic (\n"
                + "    mechanicID VARCHAR PRIMARY KEY,\n"
                + "    name VARCHAR NOT NULL,\n"
                + "    mechanic_public_key BYTEA NOT NULL\n"
                + ");";

        String createManufacturerTable = "CREATE TABLE manufacturer (\n"
                + "    manufacturerID VARCHAR PRIMARY KEY,\n"
                + "    manufacturer_public_key BYTEA NOT NULL\n"
                + ");";

        String createCarTable = "CREATE TABLE car (\n"
                + "    carID VARCHAR PRIMARY KEY,\n"
                + "    ownerID VARCHAR NOT NULL REFERENCES owner(ownerID),\n"
                + "    manufacturerID VARCHAR NOT NULL REFERENCES manufacturer(manufacturerID),\n"
                + "    mechanicID VARCHAR REFERENCES mechanic(mechanicID),\n"
                + "    atual_configuration INTEGER,\n"
                + "    battery_level INTEGER NOT NULL,\n"
                + "    firmware_version VARCHAR,\n"
                + "    is_maintenance_mode BOOLEAN DEFAULT FALSE,\n"
                + "    is_on BOOLEAN DEFAULT FALSE,\n"
                + "    last_maintenance_timestamp TIMESTAMP,\n"
                + "    last_config_timestamp TIMESTAMP\n"
                + ");";

        String createConfigurationsTable = "CREATE TABLE configurations (\n"
                + "    configID SERIAL PRIMARY KEY,\n"
                + "    carID VARCHAR NOT NULL REFERENCES car(carID) DEFERRABLE INITIALLY DEFERRED,\n"
                + "    is_default BOOLEAN NOT NULL,\n"
                + "    userID VARCHAR NOT NULL REFERENCES owner(ownerID),\n"
                + "    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n"
                + "    configuration_details JSONB NOT NULL,\n"
                + "    signature BYTEA NOT NULL\n"
                + ");";

        String alterCarTable = "ALTER TABLE car\n"
                + "ADD CONSTRAINT fk_atual_configuration FOREIGN KEY (atual_configuration) \n"
                + "REFERENCES configurations(configID) DEFERRABLE INITIALLY DEFERRED;";

        String createMaintenanceLogsTable = "CREATE TABLE maintenance_logs (\n"
                + "    logID SERIAL PRIMARY KEY,\n"
                + "    carID VARCHAR NOT NULL REFERENCES car(carID),\n"
                + "    mechanicID VARCHAR NOT NULL REFERENCES mechanic(mechanicID),\n"
                + "    tests_performed JSONB NOT NULL,\n"
                + "    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n"
                + "    signature BYTEA NOT NULL\n"
                + ");";

        String createFirmwareUpdatesTable = "CREATE TABLE firmware_updates (\n"
                + "    updateID SERIAL PRIMARY KEY,\n"
                + "    carID VARCHAR NOT NULL REFERENCES car(carID),\n"
                + "    manufacturerID VARCHAR NOT NULL REFERENCES manufacturer(manufacturerID),\n"
                + "    firmware_version VARCHAR NOT NULL,\n"
                + "    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n"
                + "    signature BYTEA NOT NULL\n"
                + ");";

        try (Connection conn = DatabaseConnection.connect();
                Statement stmt = conn.createStatement()) {

            for (String dropStatement : dropTableStatements) {
                stmt.executeUpdate(dropStatement);
            }

            stmt.executeUpdate(createOwnerTable);
            stmt.executeUpdate(createMechanicTable);
            stmt.executeUpdate(createManufacturerTable);
            stmt.executeUpdate(createCarTable);
            stmt.executeUpdate(createConfigurationsTable);
            stmt.executeUpdate(alterCarTable);
            stmt.executeUpdate(createMaintenanceLogsTable);
            stmt.executeUpdate(createFirmwareUpdatesTable);

            System.out.println("Database tables created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating database tables: " + e.getMessage());
        }
    }
}
