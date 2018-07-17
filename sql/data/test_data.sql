-- users
INSERT INTO dshinghis_khan.users (id, username, login, salt, pass_and_salt, user_role, originator, email, company_id) VALUES (1, 'Sir Psycho', 'test', 'nvuritneg4785231', '02ace43368e804b8b1fc624d01a050eb', 'ROLE_ADMIN', 1, 'spiritblossom@icloud.com', 1);
INSERT INTO dshinghis_khan.users (id, username, login, salt, pass_and_salt, user_role, originator, email, company_id) VALUES (5, 'Umbrella', 'UMB', 'SS7LUHU8PFDU1C8P', '82d7b29653fb39f7e77b305cbe869231', 'ROLE_TRANSPORT_COMPANY', 1, null, 2);

-- company
INSERT INTO dshinghis_khan.transport_companies (id, name, short_name, point_id, user_id, inn, number_of_transports, ati_code, accountant_name, ocved, ocpo, ogrn, type, originator, email) VALUES (1, 'Umbrella', 'UMB', 1, null, '634523534', 12, 'dfasdf', 'Anton', '123456', '1123456', '123567', 'TRANSPORT', 1, 'spiritblossom@icloud.com');
INSERT INTO dshinghis_khan.transport_companies (id, name, short_name, point_id, user_id, inn, number_of_transports, ati_code, accountant_name, ocved, ocpo, ogrn, type, originator, email) VALUES (2, 'Test', 'test', 1, null, '1234567', 0, null, 'Sir Psycho', '123456', '1123456', '123567', 'DISPATCHER', 1, 'spiritblossom@icloud.com');