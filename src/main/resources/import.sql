--basic amenities
REPLACE INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (1, 'Attached bathroom', null, 'Attached bathroom');
REPLACE INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (2, 'Luggage storage', null, 'Luggage storage');
REPLACE INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (3, 'Cotton linens', null, 'Cotton linens');
REPLACE INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (4, 'Air conditioning', null, 'Air conditioning');
REPLACE INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (5, 'Designated sitting area with table and chair', null, 'Designated sitting area with table and chair');
REPLACE INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (6, 'Bottled water', null, 'Bottled water');
REPLACE INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (7, 'Toiletries', null, 'Toiletries');
REPLACE INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (8, 'Larger room size', null, 'Larger room size');
REPLACE INTO amenities (id, amenity_description, amenity_icon_url, amenity_name) VALUES (9, 'Working desk', null, 'Working desk');

--room types
REPLACE INTO room_types (id, type_name, description, capacity_adult, capacity_child, price_per_night, room_size_in_square_feet) VALUES (1, 'Non-AC Room', 'Comfortable and functional, ideal for a simple and bidget-friendly stay.', 2, 1, 1200, null);
REPLACE INTO room_types (id, type_name, description, capacity_adult, capacity_child, price_per_night, room_size_in_square_feet) VALUES (2, 'Deluxe Room', 'Thoughtfully designed with added comfort and convenience for a relaxing experience.', 2, 1, 1500, null);
REPLACE INTO room_types (id, type_name, description, capacity_adult, capacity_child, price_per_night, room_size_in_square_feet) VALUES (3, 'Executive Room', 'Spacious and well-equipped, perfect for business travelers seeking extra comfort and functionality.', 2, 1, 1800, null);

--room type amenities
REPLACE INTO room_type_amenities(amenity_id, room_type_id) VALUES (1, 1);
REPLACE INTO room_type_amenities(amenity_id, room_type_id) VALUES (1, 2);
REPLACE INTO room_type_amenities(amenity_id, room_type_id) VALUES (1, 3);
REPLACE INTO room_type_amenities(amenity_id, room_type_id) VALUES (2, 1);
REPLACE INTO room_type_amenities(amenity_id, room_type_id) VALUES (2, 2);
REPLACE INTO room_type_amenities(amenity_id, room_type_id) VALUES (2, 3);
REPLACE INTO room_type_amenities(amenity_id, room_type_id) VALUES (3, 1);
REPLACE INTO room_type_amenities(amenity_id, room_type_id) VALUES (3, 2);
REPLACE INTO room_type_amenities(amenity_id, room_type_id) VALUES (3, 3);
REPLACE INTO room_type_amenities(amenity_id, room_type_id) VALUES (4, 2);
REPLACE INTO room_type_amenities(amenity_id, room_type_id) VALUES (4, 3);
REPLACE INTO room_type_amenities(amenity_id, room_type_id) VALUES (5, 2);
REPLACE INTO room_type_amenities(amenity_id, room_type_id) VALUES (5, 3);
REPLACE INTO room_type_amenities(amenity_id, room_type_id) VALUES (6, 2);
REPLACE INTO room_type_amenities(amenity_id, room_type_id) VALUES (6, 3);
REPLACE INTO room_type_amenities(amenity_id, room_type_id) VALUES (7, 2);
REPLACE INTO room_type_amenities(amenity_id, room_type_id) VALUES (7, 3);
REPLACE INTO room_type_amenities(amenity_id, room_type_id) VALUES (8, 3);
REPLACE INTO room_type_amenities(amenity_id, room_type_id) VALUES (9, 3);

--user
REPLACE INTO users(id, name, email, password, contact_number, role) VALUES (1, 'admin', 'admin@gmail.com', '$2a$10$gyOmCX4.JZR1xw3rfb5IA.E9W7OiXJArX71fvKeJkBzz77cteSsb2', '1234567890', 'ADMIN');
--password is 'admin'
REPLACE INTO users(id, name, email, password, contact_number, role) VALUES (2, 'testuser', 'testuser@gmail.com', '', '1234567891', 'USER');

--location
REPLACE INTO locations(id, city, state, country) VALUES (1, 'Mumbai', 'Maharashtra', 'India');

--hotel
REPLACE INTO hotels(id, location_id, name, email, contact_number, address) VALUES (1, 1, 'Hotel Pride', '', '9819914047', 'Hotel Pride, Lbs Marg, Rs, Dreams Mall Rd, next to icici bank, Bhandup West, Mumbai, Maharashtra 400078');

--hotel room types
REPLACE INTO hotel_room_types(hotel_id, room_type_id) VALUES (1, 1);
REPLACE INTO hotel_room_types(hotel_id, room_type_id) VALUES (1, 2);
REPLACE INTO hotel_room_types(hotel_id, room_type_id) VALUES (1, 3);

--room
REPLACE INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (1, 1, '101', 1, 1);
REPLACE INTO rooms(id, hotel_id, room_number, room_type_id, available) VALUES (2, 1, '102', 1, 1);
