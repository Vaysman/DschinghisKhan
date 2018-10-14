CREATE TABLE route_reviews
(
  id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  route_id int,
  company_id int,
  status varchar(20)
);
CREATE INDEX route_reviews_id_index ON route_reviews (id);
CREATE INDEX route_reviews_route_id_index ON route_reviews (route_id);
CREATE INDEX route_reviews_company_id_index ON route_reviews (company_id);

CREATE TABLE route_review_opinions
(
  id int PRIMARY KEY NOT NULL auto_increment,
  review_id int,
  company_id int,
  price DECIMAL(11,2),
  comment varchar(2000)
);
CREATE INDEX route_review_opinions_id_index ON route_review_opinions (id);
CREATE INDEX route_review_opinions_review_id_index ON route_review_opinions (review_id);
CREATE INDEX route_review_opinions_company_id_index ON route_review_opinions (company_id);


create table persistent_logins (
  username varchar(64) not null,
  series varchar(64) primary key,
  token varchar(64) not null,
  last_used timestamp not null
);

CREATE TABLE files_to_company
(
  file_id int,
  company_id int,
  initiative_company_id int
);
CREATE INDEX files_to_company_file_id_index ON files_to_company (file_id);
CREATE INDEX files_to_company_company_id_index ON files_to_company (company_id);
CREATE INDEX files_to_company_initiative_company_id_index ON files_to_company (initiative_company_id);

CREATE TABLE contracts
(
  id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  file_id int,
  company_id int,
  initiative_company_id int
);
CREATE INDEX contracts_file_id_index ON contracts (file_id);
CREATE INDEX contracts_company_id_index ON contracts (company_id);
CREATE INDEX contracts_initiative_company_id_index ON contracts (initiative_company_id);