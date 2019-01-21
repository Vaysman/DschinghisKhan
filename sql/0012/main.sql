CREATE TABLE additional_transports_for_orders
(
  order_id int,
  transport_id int
);
CREATE INDEX additional_transports_for_orders_order_id_index ON additional_transports_for_orders (order_id);


CREATE TABLE additional_drivers_for_orders
(
  order_id int,
  driver_id int
);
CREATE INDEX additional_drivers_for_orders_order_id_index ON additional_drivers_for_orders (order_id);

CREATE TABLE additional_transports_for_offers
(
  offer_id int,
  transport_id int
);
CREATE INDEX additional_transports_for_offers_offer_id_index ON additional_transports_for_offers (offer_id);


CREATE TABLE additional_drivers_for_offers
(
  offer_id int,
  driver_id int
);
CREATE INDEX additional_drivers_for_offers_offer_id_index ON additional_drivers_for_offers (offer_id);

ALTER TABLE users ADD surname VARCHAR(64) NULL;
ALTER TABLE users ADD patronym VARCHAR(64) NULL;
ALTER TABLE transports ADD model VARCHAR(64) NULL;


#93
ALTER TABLE clients ADD inn VARCHAR(64) NULL;
ALTER TABLE clients ADD full_name VARCHAR(256) NULL;
ALTER TABLE clients ADD email VARCHAR(120) NULL;
ALTER TABLE clients ADD mail_address VARCHAR(512) NULL;
ALTER TABLE clients ADD address VARCHAR(512) NULL;
ALTER TABLE clients ADD contact_id int NULL;
ALTER TABLE clients ADD comment VARCHAR(512) NULL;

ALTER TABLE users ADD has_accepted_cookies TINYINT DEFAULT 0 NOT NULL;