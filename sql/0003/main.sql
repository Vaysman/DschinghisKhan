ALTER TABLE transport_companies ADD taxation_type VARCHAR(10) NULL;

ALTER TABLE drivers ADD mobile_app_number VARCHAR(20) NULL;
ALTER TABLE drivers
  MODIFY COLUMN mobile_app_number VARCHAR(20) AFTER has_mobile_app;
ALTER TABLE drivers ADD tracking_number VARCHAR(20) NULL;
ALTER TABLE drivers
  MODIFY COLUMN tracking_number VARCHAR(20) AFTER is_tracked;

ALTER TABLE route_points ADD prr_cost DECIMAL(11,2) NULL;
ALTER TABLE route_points MODIFY cost DECIMAL(11,2);
ALTER TABLE route_points
  MODIFY COLUMN prr_cost DECIMAL(11,2) AFTER cost;

