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