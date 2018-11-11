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