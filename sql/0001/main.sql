# login as Khan@localhoset/Diogenes-412 before executing this

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
ALTER TABLE transport_companies ADD email VARCHAR(64) NULL;

CREATE TABLE contacts
(
  id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  taxation varchar(64),
  address varchar(128),
  fact_address varchar(128),
  phone varchar(20),
  number_of_transports int,
  name varchar(64)
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

ALTER TABLE points ADD originator int NULL;

CREATE INDEX users_originator_index ON users (originator);
CREATE INDEX transport_companies_originator_index ON transport_companies (originator);
CREATE INDEX routes_originator_index ON routes (originator);
ALTER TABLE contacts ADD originator int NULL;
CREATE INDEX contacts_originator_index ON contacts (originator);
ALTER TABLE route_points ADD loading_time int NULL;

ALTER TABLE orders ADD requirements VARCHAR(512) NULL;
ALTER TABLE orders ADD cargo TEXT NULL;
ALTER TABLE orders ADD payment_date DATE NULL;
ALTER TABLE orders ADD document_return_date DATE NULL;
ALTER TABLE orders ADD rating int NULL;
ALTER TABLE orders ADD order_obligation VARCHAR(16) NULL;
ALTER TABLE orders ADD originator int NULL;

CREATE TABLE cargo_types
(
  id int PRIMARY KEY NOT NULL,
  name VARCHAR(128) NOT NULL
);
CREATE UNIQUE INDEX cargo_types_id_uindex ON cargo_types (id);
CREATE UNIQUE INDEX cargo_types_name_uindex ON cargo_types (name);

CREATE TABLE drop_points
(
  order_id int NOT NULL,
  point_id int,
  CONSTRAINT drop_points_orders_id_fk FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT drop_points_points_id_fk FOREIGN KEY (point_id) REFERENCES points (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE INDEX drop_points_order_id_index ON drop_points (order_id);
CREATE INDEX drop_points_point_id_index ON drop_points (point_id);
ALTER TABLE users ADD email VARCHAR(64) NULL;

CREATE TABLE transports
(
  id int PRIMARY KEY AUTO_INCREMENT,
  number VARCHAR(16) NOT NULL,
  is_gps boolean,
  type varchar(16),
  body_type varchar(16),
  tonnage int,
  volume int,
  loading_type VARCHAR(128),
  conics boolean,
  comment varchar(512),
  hydrobort boolean,
  originator int,
  CONSTRAINT transports_users_id_fk FOREIGN KEY (originator) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE UNIQUE INDEX transports_id_uindex ON transports (id);
CREATE INDEX transports_number_index ON transports (number);
CREATE INDEX transports_originator_index ON transports (originator);

CREATE TABLE drivers
(
  id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  name varchar(64),
  phone varchar(20),
  passport_number VARCHAR(16),
  license_number VARCHAR(16),
  rating int,
  has_mobile_app boolean,
  is_tracked boolean,
  is_hired boolean,
  payment_type varchar(16),
  originator int,
  CONSTRAINT drivers_users_id_fk FOREIGN KEY (originator) REFERENCES users (id) ON DELETE SET NULL ON UPDATE SET NULL
);
CREATE UNIQUE INDEX drivers_id_uindex ON drivers (id);
CREATE INDEX drivers_name_index ON drivers (name);
CREATE INDEX drivers_originator_index ON drivers (originator);

ALTER TABLE orders ADD driver_id int NULL;
ALTER TABLE orders
  ADD CONSTRAINT orders_drivers_id_fk
FOREIGN KEY (driver_id) REFERENCES drivers (id) ON DELETE SET NULL ON UPDATE SET NULL;
ALTER TABLE orders ADD transport_id int NULL;
ALTER TABLE orders
  ADD CONSTRAINT orders_transports_id_fk
FOREIGN KEY (transport_id) REFERENCES transports (id) ON DELETE SET NULL ON UPDATE SET NULL;
ALTER TABLE orders DROP pickup_point_id;
ALTER TABLE orders DROP drop_point_id;
ALTER TABLE transports ADD wialon_id VARCHAR(64) NULL;
ALTER TABLE users ADD company_id int NULL;
CREATE INDEX users_company_id_index ON users (company_id);
ALTER TABLE orders ADD route_price DECIMAL(11,2) NULL;
ALTER TABLE orders ADD dispatcher_price DECIMAL(11,2) NULL;
ALTER TABLE orders ADD proposed_price DECIMAL(11,2) NULL;

ALTER TABLE drivers DROP FOREIGN KEY drivers_transport_companies_id_fk;

ALTER TABLE transports DROP FOREIGN KEY transports_users_id_fk;

ALTER TABLE routes DROP FOREIGN KEY routes_transport_companies_id_fk;
ALTER TABLE routes
  ADD CONSTRAINT routes_transport_companies_id_fk
FOREIGN KEY (transport_company_id) REFERENCES transport_companies (id) ON DELETE SET NULL ON UPDATE SET NULL;

ALTER TABLE route_points DROP FOREIGN KEY route_points_points_id_fk;
ALTER TABLE route_points DROP FOREIGN KEY route_points_routes_id_fk;
ALTER TABLE contacts DROP FOREIGN KEY contacts_transport_companies_id_fk;