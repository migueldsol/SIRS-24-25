DROP TABLE IF EXISTS owner CASCADE;
DROP TABLE IF EXISTS mechanic CASCADE;
DROP TABLE IF EXISTS manufacturer CASCADE;
DROP TABLE IF EXISTS car CASCADE;
DROP TABLE IF EXISTS configurations CASCADE;
DROP TABLE IF EXISTS maintenance_logs CASCADE;
DROP TABLE IF EXISTS firmware_updates CASCADE;

CREATE TABLE owner (
    ownerID VARCHAR PRIMARY KEY,
    name VARCHAR NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    owner_public_key BYTEA NOT NULL
);

CREATE TABLE mechanic (
    mechanicID VARCHAR PRIMARY KEY,
    name VARCHAR NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    mechanic_public_key BYTEA NOT NULL
);

CREATE TABLE manufacturer (
    manufacturerID VARCHAR PRIMARY KEY,
    password_hash VARCHAR(255) NOT NULL,
    manufacturer_public_key BYTEA NOT NULL
);

CREATE TABLE car (
    carID VARCHAR PRIMARY KEY,
    ownerID VARCHAR NOT NULL REFERENCES owner(ownerID),
    manufacturerID VARCHAR NOT NULL REFERENCES manufacturer(manufacturerID),
    mechanicID VARCHAR REFERENCES mechanic(mechanicID),
    atual_configuration INTEGER,
    battery_level INTEGER NOT NULL,
    firmware_version VARCHAR,
    is_maintenance_mode BOOLEAN DEFAULT FALSE,
    is_on BOOLEAN DEFAULT FALSE,
    last_maintenance_timestamp TIMESTAMP,
    last_config_timestamp TIMESTAMP
);

CREATE TABLE configurations (
    configID SERIAL PRIMARY KEY,
    carID VARCHAR NOT NULL REFERENCES car(carID) DEFERRABLE INITIALLY DEFERRED,
    is_default BOOLEAN NOT NULL,
    userID VARCHAR NOT NULL REFERENCES owner(ownerID),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    configuration_details VARCHAR NOT NULL
);

ALTER TABLE car
ADD CONSTRAINT fk_atual_configuration FOREIGN KEY (atual_configuration) 
REFERENCES configurations(configID) DEFERRABLE INITIALLY DEFERRED;

CREATE TABLE maintenance_logs (
    logID SERIAL PRIMARY KEY,
    carID VARCHAR NOT NULL REFERENCES car(carID),
    mechanicID VARCHAR NOT NULL REFERENCES mechanic(mechanicID),
    tests_performed JSONB NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE firmware_updates (
    updateID SERIAL PRIMARY KEY,
    carID VARCHAR NOT NULL REFERENCES car(carID),
    manufacturerID VARCHAR NOT NULL REFERENCES manufacturer(manufacturerID),
    firmware_version VARCHAR NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
