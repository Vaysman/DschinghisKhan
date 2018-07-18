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