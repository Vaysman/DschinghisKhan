CREATE TABLE files
(
  id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  file_name VARCHAR(256),
  path VARCHAR(512)
);
CREATE UNIQUE INDEX files_id_uindex ON files (id);

CREATE TABLE files_to_transport
(
  transport_id int,
  file_id int
);
CREATE INDEX files_to_transport_transport_id_index ON files_to_transport (transport_id);
CREATE INDEX files_to_transport_file_id_index ON files_to_transport (file_id);

CREATE TABLE files_to_drivers
(
  file_id int,
  driver_id int
);
CREATE INDEX files_to_drivers_file_id_index ON files_to_drivers (file_id);
CREATE INDEX files_to_drivers_driver_id_index ON files_to_drivers (driver_id);