CREATE TABLE order_history
(
  id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  order_id int,
  order_number varchar(64),
  route_price int,
  dispatcher_price int,
  company_price int,
  action VARCHAR(64),
  order_status VARCHAR(64),
  action_user VARCHAR(64),
  user_id int NULL,
  action_company VARCHAR(256),
  company_id int NULL,
  date DATETIME null
);

CREATE INDEX order_history_id_index ON order_history (id);
CREATE INDEX order_history_order_id_index ON order_history (order_id);
CREATE INDEX order_history_user_id_index ON order_history (user_id);
CREATE INDEX order_history_company_id_index ON order_history (company_id);

ALTER TABLE orders ADD COLUMN payment_type VARCHAR(16);
ALTER TABLE orders MODIFY document_return_date int;
ALTER TABLE orders MODIFY payment_date int;
ALTER TABLE orders MODIFY dispatcher_price DECIMAL(11,2);
ALTER TABLE orders MODIFY route_price DECIMAL(11,2);
ALTER TABLE orders MODIFY proposed_price DECIMAL(11,2);
ALTER TABLE pending_orders CHANGE transport_company_id company_id int(11) NOT NULL;

DROP TABLE IF EXISTS order_offers;
CREATE TABLE order_offers

(
  id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  order_number VARCHAR(64),
  order_id int,
  company_id int,
  proposed_price DECIMAL(11,2),
  dispatcher_pricer DECIMAL(11,2),
  driver_id int NULL,
  transport_id int NULL,
  manager_company_id int NULL
);
CREATE INDEX order_offers_id_index ON order_offers (id);
CREATE INDEX order_offers_order_id_company_id_index ON order_offers (order_id, company_id);
CREATE INDEX order_offers_manager_company_id_index ON order_offers (manager_company_id);

ALTER TABLE transport_companies DROP user_id;

drop table routes_to_companies;

drop table drop_points;

ALTER TABLE orders ADD status_change_date DATETIME null;

