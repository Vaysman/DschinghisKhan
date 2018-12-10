
INSERT INTO points (id, name, address, originator) VALUES (1, 'point1', 'Тюмень 2', 2);
INSERT INTO points (id, name, address, originator) VALUES (2, 'point2', 'Екатеринбург', 2);

INSERT INTO transport_companies (id, name, short_name, point_id, inn, number_of_transports, ati_code, accountant_name, ocved, ocpo, ogrn, type, originator, email) VALUES (1, 'Umbrella', 'UMB', 1,  '634523534', 12, 'dfasdf', 'Anton', '123456', '1123456', '123567', 'TRANSPORT', 2, 'spiritblossom@icloud.com');
INSERT INTO transport_companies (id, name, short_name, point_id, inn, number_of_transports, ati_code, accountant_name, ocved, ocpo, ogrn, type, originator, email) VALUES (2, 'Test', 'test', 1, '1234567', 0, null, 'Sir Psycho', '123456', '1123456', '123567', 'DISPATCHER', 2, 'spiritblossom@icloud.com');

INSERT INTO users (id, username, login, salt, pass_and_salt, user_role, originator, email, company_id) VALUES (1, 'Sir Psycho', 'test', 'nvuritneg4785231', '02ace43368e804b8b1fc624d01a050eb', 'ROLE_ADMIN', 2, 'spiritblossom@icloud.com', 2);
INSERT INTO users (id, username, login, salt, pass_and_salt, user_role, originator, email, company_id) VALUES (5, 'Umbrella', 'UMB', 'SS7LUHU8PFDU1C8P', '82d7b29653fb39f7e77b305cbe869231', 'ROLE_TRANSPORT_COMPANY', 1, null, 1);

INSERT INTO routes (id, name, transport_company_id, total_cost, cost_per_kilometer,  cost_per_box, cost_per_pallet, vehicle_type, temp_from, temp_to, volume, tonnage, comment, total_cost_nds, cost_per_kilometer_nds,  cost_per_box_nds, cost_per_pallet_nds, loading_type, originator) VALUES (1, 'Тюмень-ЕКБ', 1, 500.00, 500.00,  500.00, 500.00, 'TENT', 10, 15, 100.000, 100.000, 'dfasdf', 100.00, 100.00,  100.00, 100.00, 'BACK', 2);

INSERT INTO route_points (id, point_id, queue_number, distance, cost, route_id, loading_time) VALUES (1, 1, 0, 100, 100, 1, 30);
INSERT INTO route_points (id, point_id, queue_number, distance, cost, route_id, loading_time) VALUES (2, 2, 1, 200, 300, 1, 30);

INSERT INTO transports (id, number, is_gps, type, body_type, tonnage, volume, loading_type, conics, comment, hydrobort, originator, wialon_id) VALUES (1, 'LSS-01', 0, 'TRANSPORT', 'THERMOS', 10, 10, 'BACK,SIDE,UPPER', 1, 'Commentary', 1, 1, null);
INSERT INTO drivers (id, name, phone, passport_number, license_number, rating, has_mobile_app, is_tracked, is_hired, payment_type, originator) VALUES (1, 'Курбанов Р.Н.', '9829340294', '1245543545', '5235232', 120, 1, 1, 0, 'CASH', 1);

CREATE TABLE persistent_logins (
  username varchar(64) NOT NULL,
  series varchar(64) NOT NULL,
  token varchar(64) NOT NULL,
  last_used timestamp NOT NULL,
  PRIMARY KEY (series)
) DEFAULT CHARSET=utf8