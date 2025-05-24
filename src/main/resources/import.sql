--basic amenities
INSERT INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (1, 'Comfortable double bed with fresh cotton linens', null, 'Bedding');
INSERT INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (2, 'Attached private bathroom with western toilet', null, 'Attached bathroom');
INSERT INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (3, '24/7 hot & cold water', null, 'Drinking water');
INSERT INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (4, 'High-speed Wi-Fi', null, 'Wifi');
INSERT INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (5, 'LED TV with cable', null, 'TV');
INSERT INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (6, 'Luggage space and basic furnishings', null, 'Storage');
INSERT INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (7, 'Ceiling fan', null, 'Fan');
INSERT INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (8, 'Air-conditioned room', null, 'AC');
INSERT INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (9, 'Sitting area with chairs and table', null, 'Sitting area');
INSERT INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (10, 'Complimentary bottled water', null, 'Bottled water');
INSERT INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (11, 'Toiletries (soap, shampoo)', null, 'Toiletries');
INSERT INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (12, 'Enhanced interior lighting', null, 'Enhanced lighting');
INSERT INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (13, 'Larger floor space', null, 'Larger space');
INSERT INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (14, 'Extra spacious layout', null, 'More larger space');
INSERT INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (15, 'Dedicated working desk', null, 'Working desk');
INSERT INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (16, 'Additional lighting and outlets', null, 'Additional lighting');
INSERT INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (17, 'Closet storage', null, 'Closet');


ALTER TABLE amenities ALTER COLUMN id RESTART WITH 10;
--room types
INSERT INTO room_types (id, type_name, description, capacity_adult, capacity_child, price_per_night, room_size_in_square_feet) VALUES (1, 'Non-AC Room', 'Simple, clean, and budget-friendly—perfect for short stays or solo travelers seeking essential comfort.', 2, 1, 2500, 152);
INSERT INTO room_types (id, type_name, description, capacity_adult, capacity_child, price_per_night, room_size_in_square_feet) VALUES (2, 'Deluxe Room', 'Upgrade your experience with added comfort and convenience, ideal for couples or longer stays.', 2, 1, 3500, 161);
INSERT INTO room_types (id, type_name, description, capacity_adult, capacity_child, price_per_night, room_size_in_square_feet) VALUES (3, 'Executive Room', 'Our most spacious option with work-friendly features—perfect for business travelers or families.', 2, 1, 4000, 321);

ALTER TABLE room_types ALTER COLUMN id RESTART WITH 4;

--room type amenities
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (1, 1);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (1, 2);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (1, 3);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (2, 1);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (2, 2);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (2, 3);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (3, 1);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (3, 2);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (3, 3);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (4, 1);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (4, 2);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (4, 3);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (5, 1);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (5, 2);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (5, 3);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (6, 1);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (6, 2);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (6, 3);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (7, 1);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (7, 2);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (7, 3);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (8, 2);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (8, 3);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (9, 2);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (9, 3);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (10, 2);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (10, 3);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (11, 2);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (11, 3);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (12, 2);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (12, 3);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (13, 2);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (13, 3);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (14, 3);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (15, 3);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (16, 3);
INSERT INTO room_type_amenities(amenity_id, room_type_id) VALUES (17, 3);

--user
INSERT INTO users(id, name, email, password, contact_number, role) VALUES (1, 'admin', 'admin@gmail.com', '$2a$10$gyOmCX4.JZR1xw3rfb5IA.E9W7OiXJArX71fvKeJkBzz77cteSsb2', '1234567890', 'ADMIN');
--password is 'admin'
INSERT INTO users(id, name, email, password, contact_number, role) VALUES (2, 'testuser', 'testuser@gmail.com', '', '1234567891', 'USER');

ALTER TABLE users ALTER COLUMN id RESTART WITH 3;

--location
INSERT INTO locations(id, city, state, country) VALUES (1, 'Mumbai', 'Maharashtra', 'India');

ALTER TABLE locations ALTER COLUMN id RESTART WITH 2;

--hotel
INSERT INTO hotels(id, location_id, name, email, contact_number, address) VALUES (1, 1, 'Hotel Pride', 'reservation@hotelpride.com', '9819914047', 'Hotel Pride, Lbs Marg, Rs, Dreams Mall Rd, next to icici bank, Bhandup West, Mumbai, Maharashtra 400078');

ALTER TABLE hotels ALTER COLUMN id RESTART WITH 2;

--hotel room types
INSERT INTO hotel_room_types(hotel_id, room_type_id) VALUES (1, 1);
INSERT INTO hotel_room_types(hotel_id, room_type_id) VALUES (1, 2);
INSERT INTO hotel_room_types(hotel_id, room_type_id) VALUES (1, 3);

--room
--for room type 1
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (1, 1, '101', 1, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (2, 1, '102', 1, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (3, 1, '103', 1, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (4, 1, '104', 1, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (5, 1, '105', 1, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (6, 1, '106', 1, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (7, 1, '107', 1, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (8, 1, '108', 1, 1);
--INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (9, 1, '109', 1, 1);
--INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (10, 1, '110', 1, 1);
--INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (11, 1, '111', 1, 1);

--for room type 2
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (12, 1, '201', 2, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (13, 1, '202', 2, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (14, 1, '203', 2, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (15, 1, '204', 2, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (16, 1, '205', 2, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (17, 1, '206', 2, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (18, 1, '207', 2, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (19, 1, '208', 2, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (20, 1, '209', 2, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (21, 1, '210', 2, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (22, 1, '211', 2, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (22, 1, '212', 2, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (22, 1, '213', 2, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (22, 1, '214', 2, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (22, 1, '215', 2, 1);

--for room type 3
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (23, 1, '301', 3, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (24, 1, '302', 3, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (25, 1, '303', 3, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (26, 1, '304', 3, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (27, 1, '305', 3, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (28, 1, '306', 3, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (29, 1, '307', 3, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (30, 1, '308', 3, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (31, 1, '309', 3, 1);
INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (32, 1, '310', 3, 1);
--INSERT INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (33, 1, '311', 3, 1);


ALTER TABLE rooms ALTER COLUMN id RESTART WITH 34;

--INSERT INTO assets (id, asset_thumb_url, asset_url, asset_type) VALUES ( 1, NULL, '/1743922849947-non_ac_img_1.jpeg', 'image');
--INSERT INTO assets (id, asset_thumb_url, asset_url, asset_type) VALUES ( 2, NULL, '/1743922849959-non_ac_img_2.JPG', 'image');
--INSERT INTO assets (id, asset_thumb_url, asset_url, asset_type) VALUES ( 3, NULL, '/1743922849995-non_ac_img_3.JPG', 'image');
--INSERT INTO assets (id, asset_thumb_url, asset_url, asset_type) VALUES ( 4, NULL, '/1743923045364-deluxe_room_1.jpeg', 'image');
--INSERT INTO assets (id, asset_thumb_url, asset_url, asset_type) VALUES ( 5, NULL, '/1743923045387-deluxe_room_2.JPG', 'image');
--INSERT INTO assets (id, asset_thumb_url, asset_url, asset_type) VALUES ( 6, NULL, '/1743923045425-deluxe_room_3.JPG', 'image');
--INSERT INTO assets (id, asset_thumb_url, asset_url, asset_type) VALUES ( 7, NULL, '/1743923045450-deluxe_room_4.JPG', 'image');
--INSERT INTO assets (id, asset_thumb_url, asset_url, asset_type) VALUES ( 8, NULL, '/1743923059725-executive_room_1.jpeg', 'image');
--INSERT INTO assets (id, asset_thumb_url, asset_url, asset_type) VALUES ( 9, NULL, '/1743923059734-executive_room_2.jpeg', 'image');
--INSERT INTO assets (id, asset_thumb_url, asset_url, asset_type) VALUES ( 10, NULL, '/1743923059745-executive_room_3.JPG', 'image');
--INSERT INTO assets (id, asset_thumb_url, asset_url, asset_type) VALUES ( 11, NULL, '/1743923059774-executive_room_4.JPG', 'image');
--
--ALTER TABLE assets ALTER COLUMN id RESTART WITH 12;

--INSERT INTO room_type_assets(asset_id, room_type_id) VALUES (1, 1);
--INSERT INTO room_type_assets(asset_id, room_type_id) VALUES (2, 1);
--INSERT INTO room_type_assets(asset_id, room_type_id) VALUES (3, 1);
--INSERT INTO room_type_assets(asset_id, room_type_id) VALUES (4, 2);
--INSERT INTO room_type_assets(asset_id, room_type_id) VALUES (5, 2);
--INSERT INTO room_type_assets(asset_id, room_type_id) VALUES (6, 2);
--INSERT INTO room_type_assets(asset_id, room_type_id) VALUES (7, 2);
--INSERT INTO room_type_assets(asset_id, room_type_id) VALUES (8, 3);
--INSERT INTO room_type_assets(asset_id, room_type_id) VALUES (9, 3);
--INSERT INTO room_type_assets(asset_id, room_type_id) VALUES (10, 3);
--INSERT INTO room_type_assets(asset_id, room_type_id) VALUES (11, 3);
