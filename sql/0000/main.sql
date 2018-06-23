CREATE USER 'Khan'@'localhost' IDENTIFIED BY 'Diogenes-412';
CREATE DATABASE dshinghis_khan CHARACTER SET utf8 COLLATE utf8_general_ci;
GRANT ALL privileges ON dshinghis_khan TO 'Khan';
GRANT ALL privileges ON dshinghis_khan.* TO 'Khan';
FLUSH PRIVILEGES;

CREATE TABLE users
(
  id int PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(256),
  login VARCHAR(256),
  salt VARCHAR(256),
  pass_and_salt VARCHAR(256),
  user_role varchar(256)
);
CREATE INDEX users_id_index ON users (id);
CREATE INDEX users_login_index ON users (login);

