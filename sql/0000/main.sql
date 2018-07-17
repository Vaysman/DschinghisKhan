CREATE USER 'Khan'@'localhost' IDENTIFIED BY 'Diogenes-412';
CREATE DATABASE dshinghis_khan CHARACTER SET utf8 COLLATE utf8_general_ci;
GRANT ALL privileges ON dshinghis_khan TO 'Khan';
GRANT ALL privileges ON dshinghis_khan.* TO 'Khan';
FLUSH PRIVILEGES;
-- login as Khan@localhoset/Diogenes-412 for all further SQl