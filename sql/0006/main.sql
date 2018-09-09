ALTER TABLE contacts ADD type VARCHAR(16) NULL;
ALTER TABLE order_offers ADD offer_datetime TIMESTAMP NULL;
CREATE INDEX order_offers_offer_datetime_index ON order_offers (offer_datetime);