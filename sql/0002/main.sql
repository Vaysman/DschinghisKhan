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

ALTER TABLE pending_orders ADD id int(15) NOT NULL PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE pending_orders ADD COLUMN proposed_price DECIMAL(11,2);
ALTER TABLE orders ADD COLUMN payment_type VARCHAR(16);
ALTER TABLE orders MODIFY document_return_date int;
ALTER TABLE orders MODIFY payment_date int;
ALTER TABLE orders MODIFY dispatcher_price DECIMAL(11,2);
ALTER TABLE orders MODIFY route_price DECIMAL(11,2);
ALTER TABLE orders MODIFY proposed_price DECIMAL(11,2);