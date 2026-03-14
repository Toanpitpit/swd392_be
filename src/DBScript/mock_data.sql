-- ============================================================
-- CAR RENTAL MANAGEMENT - MOCK DATA FOR TESTING
-- ============================================================

-- 1. USERS (4 users - admin, car_owner, customer)
INSERT INTO users (name, password_hash, email, role, is_verified, status) VALUES
('Admin User', '$2a$10$N9qo8uLOickgx2ZMRZoMyu', 'admin@carental.com', 'ADMIN', 1, 'ACTIVE'),
('John Doe', '$2a$10$N9qo8uLOickgx2ZMRZoMyu', 'owner1@carental.com', 'CAR_OWNER', 1, 'ACTIVE'),
('Jane Smith', '$2a$10$N9qo8uLOickgx2ZMRZoMyu', 'customer1@carental.com', 'CUSTOMER', 1, 'ACTIVE'),
('Bob Johnson', '$2a$10$N9qo8uLOickgx2ZMRZoMyu', 'customer2@carental.com', 'CUSTOMER', 1, 'ACTIVE');

-- 2. IDENTITY_DOCUMENTS (2 documents)
INSERT INTO identity_documents (user_id, doc_type, image_url, verification_status) VALUES
(2, 'ID_CARD', 'https://s3.amazonaws.com/carental/id_cards/john_doe.jpg', 'APPROVED'),
(3, 'DRIVER_LICENSE', 'https://s3.amazonaws.com/carental/licenses/jane_smith.jpg', 'APPROVED');

-- 3. USER_SETTINGS (4 settings)
INSERT INTO user_settings (user_id, email_enabled, sms_enabled, push_enabled) VALUES
(1, 1, 1, 1),
(2, 1, 1, 1),
(3, 1, 0, 1),
(4, 1, 1, 0);

-- 4. VEHICLES (3 vehicles)
INSERT INTO vehicles (owner_id, vin, license_plate, make, model, year, transmission_type, fuel_type, seat_count, description, status, base_price) VALUES
(2, '1HGCV41JXMN109186', 'ABC1234', 'Honda', 'Civic', 2022, 'AUTOMATIC', 'GASOLINE', 5, 'Well-maintained Honda Civic', 'ACTIVE', 50.00),
(2, '2T1BURHE0JC048186', 'XYZ5678', 'Toyota', 'Corolla', 2023, 'AUTOMATIC', 'HYBRID', 5, 'New Toyota Corolla Hybrid', 'ACTIVE', 55.00),
(2, '5TPKRFAB1LS123456', 'LMN9012', 'Toyota', 'RAV4', 2021, 'AUTOMATIC', 'GASOLINE', 5, 'Spacious RAV4 SUV', 'ACTIVE', 75.00);

-- 5. VEHICLE_PHOTOS (6 photos)
INSERT INTO vehicle_photos (vehicle_id, url) VALUES
(1, 'https://s3.amazonaws.com/carental/photos/civic_1.jpg'),
(1, 'https://s3.amazonaws.com/carental/photos/civic_2.jpg'),
(2, 'https://s3.amazonaws.com/carental/photos/corolla_1.jpg'),
(2, 'https://s3.amazonaws.com/carental/photos/corolla_2.jpg'),
(3, 'https://s3.amazonaws.com/carental/photos/rav4_1.jpg'),
(3, 'https://s3.amazonaws.com/carental/photos/rav4_2.jpg');

-- 6. VEHICLE_DOCUMENTS (3 documents)
INSERT INTO vehicle_documents (vehicle_id, doc_type, verification_status) VALUES
(1, 'REGISTRATION', 'APPROVED'),
(2, 'REGISTRATION', 'APPROVED'),
(3, 'REGISTRATION', 'APPROVED');

-- 7. ADDRESS (3 addresses)
INSERT INTO address (vehicle_id, street, city, zip_code) VALUES
(1, '123 Main Street', 'Ho Chi Minh City', '700000'),
(2, '456 Oak Avenue', 'Ha Noi', '100000'),
(3, '789 Pine Road', 'Da Nang', '500000');

-- 8. AVAILABILITIES (6 availability records)
INSERT INTO availabilities (vehicle_id, start_date, end_date, type) VALUES
(1, '2026-03-15 08:00:00', '2026-12-31 23:59:59', 'AVAILABLE'),
(1, '2026-04-01 00:00:00', '2026-04-05 00:00:00', 'BLOCKED'),
(2, '2026-03-15 08:00:00', '2026-12-31 23:59:59', 'AVAILABLE'),
(2, '2026-05-10 00:00:00', '2026-05-15 00:00:00', 'BLOCKED'),
(3, '2026-03-15 08:00:00', '2026-12-31 23:59:59', 'AVAILABLE'),
(3, '2026-06-01 00:00:00', '2026-06-10 00:00:00', 'SOFT_BLOCKED');

-- 9. BOOKINGS (3 bookings)
INSERT INTO bookings (vehicle_id, customer_id, created_at, start_time, end_time, status, rejection_reason) VALUES
(1, 3, NOW(), '2026-03-20 09:00:00', '2026-03-25 18:00:00', 'APPROVED', NULL),
(2, 4, NOW(), '2026-03-18 10:00:00', '2026-03-20 17:00:00', 'COMPLETED', NULL),
(3, 3, NOW(), '2026-03-22 08:00:00', '2026-03-28 20:00:00', 'ACTIVE', NULL);

-- 10. PAYMENTS (3 payments)
INSERT INTO payments (booking_id, payer_id, amount, type, status) VALUES
(1, 3, 250.00, 'SECURITY_DEPOSIT', 'COMPLETED'),
(1, 3, 500.00, 'RENTAL_FARE', 'COMPLETED'),
(2, 4, 110.00, 'RENTAL_FARE', 'COMPLETED');

-- 11. PAYOUTS (2 payouts)
INSERT INTO payouts (owner_id, amount, status) VALUES
(2, 450.00, 'APPROVED'),
(2, 105.00, 'PENDING');

-- 12. INSPECTIONS (3 inspections)
INSERT INTO inspections (booking_id, inspector_id, type, comments, date, car_status) VALUES
(1, 1, 'PICKUP', 'Vehicle in excellent condition', '2026-03-20 09:15:00', 'EXCELLENT'),
(2, 1, 'RETURN', 'Minor scratches on bumper', '2026-03-20 17:30:00', 'GOOD'),
(3, 1, 'PICKUP', 'Fuel full, no issues', '2026-03-22 08:10:00', 'EXCELLENT');

-- 13. INSPECTION_PHOTOS (4 photos)
INSERT INTO inspection_photos (inspection_id, url) VALUES
(1, 'https://s3.amazonaws.com/carental/inspections/booking1_pickup_1.jpg'),
(1, 'https://s3.amazonaws.com/carental/inspections/booking1_pickup_2.jpg'),
(2, 'https://s3.amazonaws.com/carental/inspections/booking2_return_1.jpg'),
(3, 'https://s3.amazonaws.com/carental/inspections/booking3_pickup_1.jpg');

-- 14. DISPUTES (1 dispute)
INSERT INTO disputes (booking_id, opened_by_id, reason, status) VALUES
(2, 4, 'Minor scratches found on return inspection', 'RESOLVED');

-- 15. INCIDENTS (1 incident)
INSERT INTO incidents (booking_id, type, description, status) VALUES
(1, 'MINOR_DAMAGE', 'Small dent on the door, does not affect functionality', 'REPORTED');

-- 16. REVIEWS (2 reviews)
INSERT INTO reviews (booking_id, author_id, target_user_id, rating, comment, status) VALUES
(2, 4, 2, 5, 'Great car and excellent service!', 'VISIBLE'),
(1, 3, 2, 4, 'Good experience, very professional', 'VISIBLE');

-- 17. MODERATION_LOGS (2 logs)
INSERT INTO moderation_logs (moderator_id, target_entity_id, action_taken) VALUES
(1, 1, 'APPROVED_LISTING'),
(1, 2, 'APPROVED_LISTING');

-- 18. MESSAGES (3 messages)
INSERT INTO messages (booking_id, sender_id, receiver_id, content, sent_at) VALUES
(1, 3, 2, 'When can I pick up the vehicle?', '2026-03-19 14:00:00'),
(1, 2, 3, 'You can pick it up from 9 AM tomorrow', '2026-03-19 14:30:00'),
(3, 3, 2, 'Is there a delivery service available?', '2026-03-21 10:00:00');

-- 19. INVOICES (2 invoices)
INSERT INTO invoices (booking_id, invoice_number, pdf_url) VALUES
(1, 'INV-2026-001', 'https://s3.amazonaws.com/carental/invoices/INV-2026-001.pdf'),
(2, 'INV-2026-002', 'https://s3.amazonaws.com/carental/invoices/INV-2026-002.pdf');

-- 20. BANK_ACCOUNTS (1 bank account)
INSERT INTO bank_accounts (user_id, account_number_masked, bank_name) VALUES
(2, '****1234', 'Vietcombank');

-- 21. PAYMENT_METHODS (2 payment methods)
INSERT INTO payment_methods (user_id, provider, tokenized_data) VALUES
(3, 'STRIPE', 'tok_visa_xxxxxxxxx'),
(4, 'PAYPAL', 'paypal_account_xxxxxxxxx');

-- 22. WISHLISTS (2 wishlist entries)
INSERT INTO wishlists (customer_id, vehicle_id) VALUES
(3, 2),
(4, 1);

-- 23. PLATFORM_CONTENT (3 content items)
INSERT INTO platform_content (`key`, title, body_text, updated_on, updated_by) VALUES
('terms_of_service', 'Terms of Service', 'Lorem ipsum dolor sit amet...', NOW(), 1),
('privacy_policy', 'Privacy Policy', 'We respect your privacy...', NOW(), 1),
('faq', 'Frequently Asked Questions', 'Q1: How do I rent a car? A: Follow these steps...', NOW(), 1);

-- 24. NOTIFICATIONS (3 notifications)
INSERT INTO notifications (user_id, type, channel, status, sent_at) VALUES
(3, 'BOOKING_CONFIRMED', 'EMAIL', 'SENT', '2026-03-19 15:00:00'),
(4, 'BOOKING_REMINDER', 'SMS', 'SENT', '2026-03-17 10:00:00'),
(2, 'PAYMENT_RECEIVED', 'EMAIL', 'SENT', '2026-03-20 12:00:00');

-- Display summary
SELECT '=== MOCK DATA INSERTED ===' as Status;
SELECT COUNT(*) as 'Users' FROM users;
SELECT COUNT(*) as 'Vehicles' FROM vehicles;
SELECT COUNT(*) as 'Bookings' FROM bookings;
SELECT COUNT(*) as 'Payments' FROM payments;
SELECT COUNT(*) as 'Inspections' FROM inspections;
SELECT COUNT(*) as 'Messages' FROM messages;
SELECT COUNT(*) as 'Reviews' FROM reviews;

