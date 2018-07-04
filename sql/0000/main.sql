CREATE USER 'Khan'@'localhost' IDENTIFIED BY 'Diogenes-412';
CREATE DATABASE dshinghis_khan CHARACTER SET utf8 COLLATE utf8_general_ci;
GRANT ALL privileges ON dshinghis_khan TO 'Khan';
GRANT ALL privileges ON dshinghis_khan.* TO 'Khan';
FLUSH PRIVILEGES;

CREATE TABLE users
(
  id int PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(256),
  login VARCHAR(256),
  salt VARCHAR(256),
  pass_and_salt VARCHAR(256),
  user_role varchar(256)
);
CREATE INDEX users_id_index ON users (id);
CREATE INDEX users_login_index ON users (login);

CREATE TABLE points
(
  id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  name VARCHAR(256),
  address varchar(256)
);
CREATE INDEX points_id_index ON points (id);
CREATE INDEX points_name_index ON points (name);

CREATE TABLE transport_companies
(
  id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  name VARCHAR(256),
  short_name VARCHAR(64),
  point_id int,
  user_id int
);
CREATE INDEX transport_companies_id_index ON transport_companies (id);
CREATE INDEX transport_companies_user_id_index ON transport_companies (user_id);
CREATE INDEX transport_companies_point_id_index ON transport_companies (point_id);

CREATE TABLE routes
(
  id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  name VARCHAR(256)
);
CREATE INDEX routes_id_index ON routes (id);
CREATE INDEX routes_name_index ON routes (name);

CREATE TABLE route_points
(
  id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  point_id int NOT NULL,
  queue_number int NOT NULL,
  distance int,
  cost int
);
CREATE INDEX route_points_id_index ON route_points (id);
CREATE INDEX route_points_point_id_index ON route_points (point_id);
ALTER TABLE route_points
  ADD CONSTRAINT route_points_points_id_fk
FOREIGN KEY (point_id) REFERENCES points (id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE route_points ADD route_id int NULL;
ALTER TABLE route_points
  ADD CONSTRAINT route_points_routes_id_fk
FOREIGN KEY (route_id) REFERENCES routes (id) ON DELETE CASCADE ON UPDATE CASCADE;

CREATE TABLE routes_to_companies
(
  transport_company_id int NOT NULL,
  route_id int NOT NULL
);
CREATE INDEX routes_to_companies_transport_company_id_index ON routes_to_companies (transport_company_id);
CREATE INDEX routes_to_companies_route_id_index ON routes_to_companies (route_id);
ALTER TABLE routes_to_companies
  ADD CONSTRAINT routes_to_companies_transport_companies_id_fk
FOREIGN KEY (transport_company_id) REFERENCES transport_companies (id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE routes_to_companies
  ADD CONSTRAINT routes_to_companies_routes_id_fk
FOREIGN KEY (route_id) REFERENCES routes (id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE routes ADD transport_company_id int NULL;
ALTER TABLE routes
  ADD CONSTRAINT routes_transport_companies_id_fk
FOREIGN KEY (transport_company_id) REFERENCES transport_companies (id) ON DELETE CASCADE ON UPDATE CASCADE;

CREATE TABLE orders
(
  id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  number varchar(64) NOT NULL,
  status varchar(20) NOT NULL,
  route_id int,
  transport_company_id int,
  pickup_point_id int,
  drop_point_id int
);
CREATE UNIQUE INDEX orders_number_uindex ON orders (number);
CREATE INDEX orders_transport_company_id_index ON orders (transport_company_id);
CREATE INDEX orders_route_id_index ON orders (route_id);
CREATE INDEX orders_pickup_point_id_index ON orders (pickup_point_id);
CREATE INDEX orders_drop_point_id_index ON orders (drop_point_id);


ALTER TABLE transport_companies ADD inn VARCHAR(16) NULL;
ALTER TABLE transport_companies ADD number_of_transports int NULL;
ALTER TABLE transport_companies ADD ati_code varchar(64) NULL;
ALTER TABLE transport_companies ADD accountant_name VARCHAR(64) NULL;
ALTER TABLE transport_companies ADD ocved VARCHAR(64) NULL;
ALTER TABLE transport_companies ADD ocpo varchar(64) NULL;
ALTER TABLE transport_companies ADD ogrn varchar(64) NULL;
ALTER TABLE transport_companies ADD type VARCHAR(10) NULL;

CREATE TABLE contacts
(
  id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  taxation varchar(64),
  address varchar(128),
  fact_address varchar(128),
  phone varchar(16),
  number_of_transports int,
  transport_company_id int,
  CONSTRAINT contacts_transport_companies_id_fk FOREIGN KEY (transport_company_id) REFERENCES transport_companies (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE INDEX contacts_transport_company_id_index ON contacts (transport_company_id);

CREATE TABLE pending_orders
(
  order_id int NOT NULL,
  transport_company_id int NOT NULL,
  CONSTRAINT pending_orders_transport_companies_id_fk FOREIGN KEY (transport_company_id) REFERENCES transport_companies (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT pending_orders_orders_id_fk FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE INDEX pending_orders_order_id_index ON pending_orders (order_id);
CREATE INDEX pending_orders_transport_company_id_index ON pending_orders (transport_company_id);

ALTER TABLE routes ADD total_cost DECIMAL(11,2) NULL;
ALTER TABLE routes ADD cost_per_kilometer DECIMAL(11,2) NULL;
ALTER TABLE routes ADD cost_per_prr DECIMAL(11,2) NULL;
ALTER TABLE routes ADD cost_per_box DECIMAL(11,2) NULL;
ALTER TABLE routes ADD cost_per_pallet DECIMAL(11,2) NULL;
ALTER TABLE routes ADD vehicle_type VARCHAR(16) NULL;
ALTER TABLE routes ADD temp_from int NULL;
ALTER TABLE routes ADD temp_to int NULL;
ALTER TABLE routes ADD volume DECIMAL(11,3) NULL;
ALTER TABLE routes ADD tonnage DECIMAL(11,3) NULL;
ALTER TABLE routes ADD comment VARCHAR(256) NULL;
ALTER TABLE routes ADD total_cost_nds DECIMAL(11,2) NULL;
ALTER TABLE routes ADD cost_per_kilometer_nds DECIMAL(11,2) NULL;
ALTER TABLE routes ADD cost_per_prr_nds DECIMAL(11,2) NULL;
ALTER TABLE routes ADD cost_per_box_nds DECIMAL(11,2) NULL;
ALTER TABLE routes ADD cost_per_pallet_nds DECIMAL(11,2) NULL;
ALTER TABLE routes ADD loading_type VARCHAR(16) NULL;
ALTER TABLE routes ADD originator int NULL;

ALTER TABLE transport_companies ADD originator int NULL;

ALTER TABLE users ADD originator int NULL;