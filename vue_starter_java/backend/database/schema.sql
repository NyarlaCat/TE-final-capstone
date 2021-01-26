BEGIN TRANSACTION;

DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS event CASCADE;
DROP TABLE IF EXISTS event_attendees CASCADE;
DROP TABLE IF EXISTS address CASCADE;
DROP TABLE IF EXISTS food CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS invitees CASCADE;

DROP SEQUENCE IF EXISTS seq_user_id;
DROP SEQUENCE IF EXISTS seq_event_id;
DROP SEQUENCE IF EXISTS seq_address_id;
DROP SEQUENCE IF EXISTS seq_orders_id;

CREATE SEQUENCE seq_user_id
START 1
INCREMENT 1;

CREATE SEQUENCE seq_event_id
START 1
INCREMENT 1;

CREATE SEQUENCE seq_address_id
START 1
INCREMENT 1;

CREATE SEQUENCE seq_orders_id
START 1
INCREMENT 1;


CREATE TABLE users (
  user_id INTEGER DEFAULT NEXTVAL('seq_user_id') PRIMARY KEY,
  username varchar(32) UNIQUE,     -- Username
  password varchar(32) NOT NULL,      -- Password
  salt varchar(256) NOT NULL,          -- Password Salt
  role varchar(255) NOT NULL default('user'),
  email varchar(255) NOT NULL UNIQUE,
  date_registered timestamp DEFAULT Now()
);

CREATE TABLE event (
  event_id INTEGER DEFAULT NEXTVAL('seq_event_id') PRIMARY KEY,
  event_name varchar(255) NOT NULL,
  event_date date NOT NULL,
  event_time varchar(10) NOT NULL,
  description varchar(255),
  deadline date NOT NULL,
  address_id int NOT NULL
  );
  
CREATE TABLE invitees (
 invite_id serial PRIMARY KEY,
 email varchar(255) NOT NULL,
 event_id int NOT NULL,
 role varchar(20) NOT NULL
);
  
CREATE TABLE event_attendees (
 event_id int NOT NULL,
 user_id int NOT NULL,
 is_host boolean DEFAULT false,
 is_chef boolean DEFAULT false,
 is_attending boolean,
 first_name varchar(25),
 last_name varchar(50),
 adult_guests int,
 child_guests int,
 invite_id int,
 PRIMARY KEY (event_id, user_id),
 FOREIGN KEY (event_id) REFERENCES event (event_id),
 FOREIGN KEY (user_id) REFERENCES users (user_id),
 FOREIGN KEY (invite_id) REFERENCES invitees (invite_id)
 );
 
CREATE TABLE address (
 address_id INTEGER DEFAULT NEXTVAL('seq_address_id') PRIMARY KEY,
 street_address varchar(100) NOT NULL,
 city varchar(50) NOT NULL,
 state varchar(30) NOT NULL,
 zip varchar(10) NOT NULL,
 user_id int NOT NULL
);

CREATE TABLE food (
 food_id serial NOT NULL UNIQUE,
 food_name varchar(50) NOT NULL,
 vegetarian boolean DEFAULT false,
 vegan boolean DEFAULT false,
 gluten_free boolean DEFAULT false,
 nut_free boolean DEFAULT false,
 description varchar(255),
 event_id int NOT NULL,
 food_category varchar(32) NOT NULL,
 FOREIGN KEY (event_id) REFERENCES event (event_id)
);

CREATE TABLE orders (
 order_id INTEGER DEFAULT NEXTVAL('seq_orders_id') PRIMARY KEY,
 event_id int NOT NULL,
 user_id int NOT NULL,
 food_id int NOT NULL,
 status varchar(20) NOT NULL,
 quantity int NOT NULL,
 FOREIGN KEY (event_id) REFERENCES event (event_id),
 FOREIGN KEY (user_id) REFERENCES users (user_id),
 FOREIGN KEY (food_id) REFERENCES food (food_id)
); 

ALTER TABLE event
ADD CONSTRAINT fk_address_id
FOREIGN KEY (address_id) 
REFERENCES address (address_id);

ALTER TABLE address
ADD CONSTRAINT fk_user_id
FOREIGN KEY (user_id)
REFERENCES users (user_id);

COMMIT;
