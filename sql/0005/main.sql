ALTER TABLE points ADD work_time varchar(128) NULL;

ALTER TABLE orders ADD cargo_weight DECIMAL(11,4) NULL;
ALTER TABLE orders ADD cargo_volume DECIMAL(11,4) NULL;
ALTER TABLE orders ADD number_of_pallets int NULL;
ALTER TABLE orders ADD cargo_height DECIMAL(11,4) NULL;
ALTER TABLE orders ADD cargo_width DECIMAL(11,4) NULL;
ALTER TABLE orders ADD cargo_length DECIMAL(11,4) NULL;
ALTER TABLE orders ADD cargo_description VARCHAR(512) NULL;

ALTER TABLE routes ADD after_load BOOL NULL;
ALTER TABLE orders ADD after_load BOOL NULL;

CREATE UNIQUE INDEX transport_companies_inn_uindex ON transport_companies (inn);