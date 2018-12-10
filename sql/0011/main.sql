CREATE TABLE clients
(
  id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  name VARCHAR(120),
  phone VARCHAR(30),
  contact VARCHAR(256),
  originator int,
  company_id int
);

CREATE INDEX clients_id_index ON clients (id);
CREATE INDEX clients_name_index ON clients (name);
CREATE INDEX clients_originator_index ON clients (originator);

ALTER TABLE contacts ADD client_id int NULL;
ALTER TABLE points ADD client_id int NULL;
ALTER TABLE route_points ADD client_id int NULL;
ALTER TABLE transport_companies ADD city VARCHAR(64) NULL;
ALTER TABLE files ADD url varchar(512) NULL;

ALTER TABLE drivers ADD photo_id int NULL;
ALTER TABLE drivers ADD license_id int NULL;
ALTER TABLE drivers ADD passport_id int NULL;

CREATE TABLE persistent_logins (
  username varchar(64) NOT NULL,
  series varchar(64) NOT NULL,
  token varchar(64) NOT NULL,
  last_used timestamp NOT NULL,
  PRIMARY KEY (series)
);


ALTER TABLE users ADD phone varchar(20) NULL;
