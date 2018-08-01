ALTER TABLE routes ADD cost_per_hour DECIMAL(11,2) NULL;
ALTER TABLE routes ADD cost_per_hour_nds DECIMAL(11,2) NULL;
ALTER TABLE routes ADD cost_per_point DECIMAL(11,2) NULL;
ALTER TABLE routes ADD cost_per_point_nds DECIMAL(11,2) NULL;
ALTER TABLE route_points ADD contact_id int NULL;
CREATE INDEX contacts_name_index ON contacts (name);
ALTER TABLE contacts ADD point_id int NULL;
ALTER TABLE order_offers ADD proposed_price_comment VARCHAR(512) NULL;
ALTER TABLE contacts ADD position VARCHAR(64) NULL;
ALTER TABLE contacts ADD email VARCHAR(64) NULL;
ALTER TABLE contacts MODIFY name varchar(128);
ALTER TABLE contacts DROP taxation;
ALTER TABLE contacts DROP address;
ALTER TABLE contacts DROP fact_address;
ALTER TABLE contacts DROP number_of_transports;
ALTER TABLE contacts DROP transport_company_id;

ALTER TABLE points ADD comment VARCHAR(256) NULL;
ALTER TABLE points ADD full_address VARCHAR(256) NULL;
ALTER TABLE points
  MODIFY COLUMN originator int(11) AFTER full_address;
ALTER TABLE points ADD x DECIMAL(20,12) NULL;
ALTER TABLE points ADD y DECIMAL(20,12) NULL;


ALTER TABLE orders ADD dispatch_date DATETIME NULL;

ALTER TABLE contacts ADD company_id int NULL;
