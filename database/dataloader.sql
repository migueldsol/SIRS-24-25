-- Insert manufacturers
INSERT INTO manufacturer (manufacturerID, password_hash, manufacturer_public_key) VALUES
('manufacturer1', '$2a$10$Hxitjy9CzEbXMz/7WrEeSuvfAdax4FQCZryCyuctE2/BZCDMHC5Hy', decode('abc123', 'hex')), -- replace with correct public key and hash
('manufacturer2', 'd3d9446802a44259755d38e6d163e820', decode('def456', 'hex')); -- replace with correct public key and hash

-- Insert mechanics
INSERT INTO mechanic (mechanicID, name, password_hash, mechanic_public_key) VALUES
('mechanic1', 'Carlos Sousa', '$2a$10$MApF.Di3jb74qabzyKwBg.kjuvepQ7AA.5gr0co20bPnuSn35ZWXS', decode('f0f1f2f3f4f5f6f7', 'hex')), -- replace with correct hash and public key
('mechanic2', 'Ana Pereira', 'd3d9446802a44259755d38e6d163e820', decode('a1b2c3d4e5f67890', 'hex')); -- replace with correct hash and public key

-- Insert owners
INSERT INTO owner (ownerID, name, password_hash, owner_public_key) VALUES
('owner1', 'João Silva', '$2a$10$eMatFjbBuBaP7Tpw.ACaYOKqANYL0A/WESyeeDwFrMEvSaR75BvsC', decode('1234567890abcdef', 'hex')), -- replace with correct hash and public key
('owner2', 'Maria Oliveira', 'd3d9446802a44259755d38e6d163e820', decode('abcdef1234567890', 'hex')); -- replace with correct hash and public key

-- Insert cars
INSERT INTO car (carID, ownerID, manufacturerID, mechanicID, atual_configuration, battery_level, firmware_version, is_maintenance_mode, is_on, last_maintenance_timestamp, last_config_timestamp) VALUES
('car1', 'owner1', 'manufacturer1', 'mechanic1', NULL, 80, '{"firmwareVersion":"1.0.0"}', FALSE, FALSE, '2024-12-01 08:00:00', '2024-12-01 08:05:00'),
('car2', 'owner2', 'manufacturer2', 'mechanic2', NULL, 60, '{"firmwareVersion":"1.0.1"}', FALSE, FALSE, '2024-12-05 10:00:00', '2024-12-05 10:15:00');
-- note: atual_configuration is NULL because it depends on the configurations table

-- Insert default configurations for each car
INSERT INTO configurations (carID, is_default, userID, timestamp, configuration_details) VALUES
('car1', TRUE, 'owner1', '2024-12-01 08:05:00',  '{"configuration":"Moadj8ErGD86DWuZ7O4k2wzcqXsRPIKE9hM6gpesyE98fciPbyb+8P87dSw5BLNUTEE4VKcWJsNzaBSMbBHmXbt7lG+w7Eg3Mt+FRwRHaKI="}'), -- replace with correct signature
('car2', TRUE, 'owner2', '2024-12-05 10:15:00',  '{"configuration":"Moadj8ErGD86DWuZ7O4k2wzcqXsRPIKE9hM6gpesyE98fciPbyb+8P87dSw5BLNUTEE4VKcWJsNzaBSMbBHmXbt7lG+w7Eg3Mt+FRwRHaKI="}'); -- replace with correct signature

-- Insert maintenance logs
INSERT INTO maintenance_logs (carID, mechanicID, tests_performed, timestamp) VALUES
('car1', 'mechanic1',  '{"configuration":"tUlfaTMJ+PR2uKyBY2Mg0TTyt0/G+REXN7rQTLArGDlxTkNGZUEMW+6Zgb2ZvAe+lB8IoF7Jy11tjBGnyXoobg=="}', '2024-12-01 08:30:00'), -- replace with correct signature
('car2', 'mechanic2',  '{"configuration":"Moadj8ErGD86DWuZ7O4k2+gNHYqCZsVID1tBCJGg+UNe16QIAQ3IwDRodvjn/b5r"}', '2024-12-05 10:45:00'); -- replace with correct signature

-- Insert firmware updates
INSERT INTO firmware_updates (carID, manufacturerID, firmware_version, timestamp) VALUES
('car1', 'manufacturer1', '{"signature":"YY5gTcOpnese6SxCW1/qTdndVT6ItEzbYgdWGSkU8TxZMTPyh/JTjHt0Dg4+vNLfzGjwqLSDD2gVAlG1ZStiGO5BTJcfYgO3fvdooOG4BYgkdunHJYsyjpfpQHBYpoV1AMq7G9GDpfEsO6E/QBXBhjogcCe+Ck7wq+CxaqQGL+UaOie5PjDn/BHnFGtiYTiWq4HpnyQU3oj9pI5ctG7UQHa0QcIhRtf/xb5xRKJh/rcfQ3Svqqys6dy6WHG1MH7ekmaIphiWAqrYmqz+E5/M+kwuQ68qdhd7WY9BJZQXnp6rkggNqiEZWicw6sEcJZln+bS+GObD/EHVgiMaUxzVNw==","firmwareVersion":"1.0.0"}', '2024-12-01 09:00:00'), -- replace with correct signature
('car2', 'manufacturer2', '{"signature":"S7ripPGanDkXALXxGqtfybfpkuSJQerykW1PyGlvOl+PFExMC41cFqPK/QI47MWbirra6S5xDg1MmJGXr+rrrX+p2NDWVHBhWao13u8g3bN8K0TWYfP4lxyKAu6cOJRcK8jhht6jSCMeKa9Cko+yj6cIHDJt0UXlX1DPbPxSbqYnDKFdGKDT93DxKnGX//Gemhf3x2yr+i94ebs/SrziVaSBGDXKDfDM62Id2bzg0zuDaWgYDwYvuNY9vy+tQh3CxS+xAhTpY0nrJFd/uqny3ro4Ef4AC+/nQDYe+ds2pnh2gAoYErMQQsgErC9bZBrHMmg3z6OzXvuIbA8Mitl/0g==","firmwareVersion":"1.0.1"}', '2024-12-05 11:00:00'); -- replace with correct signature

-- Update the atual_configuration field in the Car table with the default configuration for each car
UPDATE car SET atual_configuration = (
    SELECT configID
    FROM configurations
    WHERE configurations.carID = Car.carID
    ORDER BY timestamp DESC
    LIMIT 1
) WHERE EXISTS (
    SELECT 1
    FROM configurations
    WHERE configurations.carID = Car.carID
);
