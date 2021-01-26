START TRANSACTION;

INSERT INTO users (username, password, salt, email, role) 
VALUES ('FakeUser', 'cookout123', 'qwerty', 'Fake@Fake.com', 'User');
INSERT INTO users (username, password, salt, email, role) 
VALUES ('FakeUser69', 'cookout456', 'fakesalt', 'Fake2@Fake.com', 'User');
INSERT INTO users (username, password, salt, email, role) 
VALUES ('FakeUser420', 'cookout789', 'fakesaltagain', 'User@Fake.com', 'User');
INSERT INTO users (username, password, salt, email, role) 
VALUES ('FakeUser42069', 'cookout1011', 'fakesaltoncemore', 'User2@Fake.com', 'User');

INSERT INTO address (street_address, city, state, zip, user_id)
VALUES ('123 Very Fake Street', 'Pittsburgh', 'PA', '15212', '1');
INSERT INTO address (street_address, city, state, zip, user_id)
VALUES ('456 Fake Street', 'Boston', 'MA', '02134', '2');

INSERT INTO event (event_name, event_date, event_time, description, deadline, address_id)
VALUES ('Christmas Cookout', '12-20-2019', '3pm', 'A fun get together for Christmas!', '12-10-2019', '1');
INSERT INTO event (event_name, event_date, event_time, description, deadline, address_id)
VALUES ('4th Of July', '07-04-2020', '5pm', 'Fireworks and fun!', '06-27-2020', '2' );

INSERT INTO food (food_name, vegetarian, vegan, gluten_free, nut_free, description, event_id, food_category)
VALUES ('Hamburger', 'false', 'false', 'false', 'true', 'Hamburger with all the fixings', '1', 'Entree');
INSERT INTO food (food_name, vegetarian, vegan, gluten_free, nut_free, description, event_id, food_category)
VALUES ('Hotdog', 'false', 'false', 'false', 'true', 'Hotdog with all the fixings', '1', 'Entree');
INSERT INTO food (food_name, vegetarian, vegan, gluten_free, nut_free, description, event_id, food_category)
VALUES ('Paneer Tikka Masala', 'true', 'false', 'true', 'true', 'Indian cheese in a spicy sauce', '2', 'Entree');

INSERT INTO orders (event_id, user_id, food_id, status, quantity)
VALUES ('1', '1', '2', 'waiting', '2');
INSERT INTO orders (event_id, user_id, food_id, status, quantity)
VALUES ('1', '2', '1', 'done', '1');
INSERT INTO orders (event_id, user_id, food_id, status, quantity)
VALUES ('2', '1', '3', 'waiting', '2');
INSERT INTO orders (event_id, user_id, food_id, status, quantity)
VALUES ('2', '2', '3', 'done', '1');

INSERT INTO invitees (email, event_id, role)
VALUES ('Fake2@Fake.com', '1', 'Guest');
INSERT INTO invitees (email, event_id, role)
VALUES ('Fake@Fake.com', '2', 'Guest');
INSERT INTO invitees (email, event_id, role)
VALUES ('User@Fake.com', '1', 'Chef');
INSERT INTO invitees (email, event_id, role)
VALUES ('User2@Fake.com', '2', 'Chef');

INSERT INTO event_attendees (event_id, user_id, is_host, is_chef, is_attending, first_name, last_name, adult_guests, child_guests)
VALUES ('1', '1', 'true', 'false', 'true', 'First', 'Last', '1', '0');
INSERT INTO event_attendees (event_id, user_id, is_host, is_chef, is_attending, first_name, last_name, adult_guests, child_guests, invite_id)
VALUES ('1', '2', 'false', 'false', null, 'Fake', 'Fakington', '0', '1', '1');
INSERT INTO event_attendees (event_id, user_id, is_host, is_chef, is_attending, first_name, last_name, adult_guests, child_guests, invite_id)
VALUES ('2', '1', 'false', 'false', 'false', 'First', 'Last', '0', '0', '2');
INSERT INTO event_attendees (event_id, user_id, is_host, is_chef, is_attending, first_name, last_name, adult_guests, child_guests)
VALUES ('2', '2', 'true', 'false', 'true', 'Fake', 'Fakington', '1', '0');
INSERT INTO event_attendees (event_id, user_id, is_host, is_chef, is_attending, first_name, last_name, adult_guests, child_guests, invite_id)
VALUES ('1', '3', 'false', 'true', 'true', 'Super', 'Fake', '0', '0', '3');
INSERT INTO event_attendees (event_id, user_id, is_host, is_chef, is_attending, first_name, last_name, adult_guests, child_guests, invite_id)
VALUES ('2', '4', 'false', 'true', 'true', 'Notareal', 'Person', '0', '0', '4');

COMMIT;

