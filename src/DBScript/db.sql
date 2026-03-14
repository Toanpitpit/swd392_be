CREATE DATABASE IF NOT EXISTS carrentaldb;
USE carrentaldb;

-- 1. Users
CREATE TABLE Users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       role ENUM('ADMIN', 'OPERATION_STAFF', 'CAR_OWNER', 'CUSTOMER') DEFAULT 'CUSTOMER',
                       is_verified TINYINT(1) DEFAULT 0,
                       status ENUM('ACTIVE', 'SUSPENDED', 'PENDING_VERIFICATION') DEFAULT 'PENDING_VERIFICATION'
);

-- 2. Identity_Documents
CREATE TABLE Identity_Documents (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    user_id INT NOT NULL,
                                    doc_type ENUM('ID_CARD', 'PASSPORT', 'DRIVER_LICENSE'),
                                    image_url VARCHAR(255),
                                    verification_status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
                                    CONSTRAINT fk_id_docs_user FOREIGN KEY (user_id) REFERENCES Users(id)
);

-- 3. User_Settings
CREATE TABLE User_Settings (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               user_id INT UNIQUE NOT NULL,
                               email_enabled TINYINT(1) DEFAULT 1,
                               sms_enabled TINYINT(1) DEFAULT 1,
                               push_enabled TINYINT(1) DEFAULT 1,
                               CONSTRAINT fk_user_settings_user FOREIGN KEY (user_id) REFERENCES Users(id)
);

-- 4. Vehicles
CREATE TABLE Vehicles (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          owner_id INT NOT NULL,
                          vin VARCHAR(17) UNIQUE NOT NULL,
                          license_plate VARCHAR(20) NOT NULL,
                          make VARCHAR(50) NOT NULL,
                          model VARCHAR(50) NOT NULL,
                          year INT,
                          transmission_type ENUM('MANUAL', 'AUTOMATIC'),
                          fuel_type ENUM('GASOLINE', 'DIESEL', 'ELECTRIC', 'HYBRID'),
                          seat_count INT,
                          description TEXT,
                          status ENUM('DRAFT', 'PENDING_VERIFICATION', 'ACTIVE', 'REJECTED', 'MAINTENANCE', 'HIDDEN') DEFAULT 'DRAFT',
                          base_price DECIMAL(10,2) NOT NULL,
                          CONSTRAINT fk_vehicles_owner FOREIGN KEY (owner_id) REFERENCES Users(id)
);

-- 5. Address
CREATE TABLE Address (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         vehicle_id INT UNIQUE NOT NULL,
                         street VARCHAR(255),
                         city VARCHAR(100),
                         zip_code VARCHAR(20),
                         CONSTRAINT fk_address_vehicle FOREIGN KEY (vehicle_id) REFERENCES Vehicles(id)
);

-- 6. Availabilities
CREATE TABLE Availabilities (
                                id INT AUTO_INCREMENT PRIMARY KEY,
                                vehicle_id INT NOT NULL,
                                start_date DATETIME NOT NULL,
                                end_date DATETIME NOT NULL,
                                type ENUM('AVAILABLE', 'BLOCKED', 'SOFT_BLOCKED') DEFAULT 'AVAILABLE',
                                CONSTRAINT fk_availabilities_vehicle FOREIGN KEY (vehicle_id) REFERENCES Vehicles(id)
);

-- 7. Vehicle_Photos
CREATE TABLE Vehicle_Photos (
                                id INT AUTO_INCREMENT PRIMARY KEY,
                                vehicle_id INT NOT NULL,
                                url VARCHAR(2048) NOT NULL,
                                CONSTRAINT fk_vehicle_photos_vehicle FOREIGN KEY (vehicle_id) REFERENCES Vehicles(id)
);

-- 8. Vehicle_Documents
CREATE TABLE Vehicle_Documents (
                                   id INT AUTO_INCREMENT PRIMARY KEY,
                                   vehicle_id INT NOT NULL,
                                   doc_type VARCHAR(50) NOT NULL,
                                   verification_status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
                                   CONSTRAINT fk_vehicle_docs_vehicle FOREIGN KEY (vehicle_id) REFERENCES Vehicles(id)
);

-- 9. Bookings
CREATE TABLE Bookings (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          vehicle_id INT NOT NULL,
                          customer_id INT NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          start_time DATETIME NOT NULL,
                          end_time DATETIME NOT NULL,
                          status ENUM('PENDING_APPROVAL', 'AWAITING_PAYMENT', 'APPROVED', 'ACTIVE', 'UNDER_INSPECTION', 'COMPLETED', 'REJECTED', 'EXPIRED', 'CANCELLED') DEFAULT 'PENDING_APPROVAL',
                          rejection_reason TEXT,
                          CONSTRAINT fk_bookings_vehicle FOREIGN KEY (vehicle_id) REFERENCES Vehicles(id),
                          CONSTRAINT fk_bookings_customer FOREIGN KEY (customer_id) REFERENCES Users(id)
);

-- 10. Payments
CREATE TABLE Payments (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          booking_id INT NOT NULL,
                          payer_id INT NOT NULL,
                          amount DECIMAL(10,2) NOT NULL,
                          type ENUM('SECURITY_DEPOSIT', 'RENTAL_FARE', 'FINE', 'REFUND'),
                          status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
                          CONSTRAINT fk_payments_booking FOREIGN KEY (booking_id) REFERENCES Bookings(id),
                          CONSTRAINT fk_payments_payer FOREIGN KEY (payer_id) REFERENCES Users(id)
);

-- 11. Payouts
CREATE TABLE Payouts (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         owner_id INT NOT NULL,
                         amount DECIMAL(10,2) NOT NULL,
                         status ENUM('PENDING', 'APPROVED', 'PROCESSED', 'FAILED') DEFAULT 'PENDING',
                         CONSTRAINT fk_payouts_owner FOREIGN KEY (owner_id) REFERENCES Users(id)
);

-- 12. Inspections
CREATE TABLE Inspections (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             booking_id INT NOT NULL,
                             inspector_id INT NOT NULL,
                             type ENUM('PICKUP', 'RETURN'),
                             comments TEXT,
                             date DATETIME NOT NULL,
                             car_status ENUM('EXCELLENT', 'GOOD', 'FAIR', 'DAMAGED'),
                             CONSTRAINT fk_inspections_booking FOREIGN KEY (booking_id) REFERENCES Bookings(id),
                             CONSTRAINT fk_inspections_inspector FOREIGN KEY (inspector_id) REFERENCES Users(id)
);

-- 13. Inspection_Photos
CREATE TABLE Inspection_Photos (
                                   id INT AUTO_INCREMENT PRIMARY KEY,
                                   inspection_id INT NOT NULL,
                                   url VARCHAR(2048) NOT NULL,
                                   CONSTRAINT fk_inspection_photos_inspection FOREIGN KEY (inspection_id) REFERENCES Inspections(id)
);

-- 14. Disputes
CREATE TABLE Disputes (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          booking_id INT NOT NULL,
                          opened_by_id INT NOT NULL,
                          reason TEXT NOT NULL,
                          status ENUM('OPEN', 'UNDER_REVIEW', 'RESOLVED', 'ESCALATED', 'WITHDRAWN') DEFAULT 'OPEN',
                          CONSTRAINT fk_disputes_booking FOREIGN KEY (booking_id) REFERENCES Bookings(id),
                          CONSTRAINT fk_disputes_opener FOREIGN KEY (opened_by_id) REFERENCES Users(id)
);

-- 15. Reviews
CREATE TABLE Reviews (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         booking_id INT NOT NULL,
                         author_id INT NOT NULL,
                         target_user_id INT NOT NULL,
                         rating INT NOT NULL,
                         comment TEXT,
                         status ENUM('VISIBLE', 'FLAGGED', 'REMOVED') DEFAULT 'VISIBLE',
                         CONSTRAINT fk_reviews_booking FOREIGN KEY (booking_id) REFERENCES Bookings(id),
                         CONSTRAINT fk_reviews_author FOREIGN KEY (author_id) REFERENCES Users(id),
                         CONSTRAINT fk_reviews_target FOREIGN KEY (target_user_id) REFERENCES Users(id)
);

-- 16. Incidents
CREATE TABLE Incidents (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           booking_id INT NOT NULL,
                           type VARCHAR(50) NOT NULL,
                           description TEXT NOT NULL,
                           status ENUM('REPORTED', 'UNDER_INVESTIGATION', 'RESOLVED') DEFAULT 'REPORTED',
                           CONSTRAINT fk_incidents_booking FOREIGN KEY (booking_id) REFERENCES Bookings(id)
);

-- 17. Messages
CREATE TABLE Messages (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          booking_id INT NOT NULL,
                          sender_id INT NOT NULL,
                          receiver_id INT NOT NULL,
                          content TEXT NOT NULL,
                          sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT fk_messages_booking FOREIGN KEY (booking_id) REFERENCES Bookings(id),
                          CONSTRAINT fk_messages_sender FOREIGN KEY (sender_id) REFERENCES Users(id),
                          CONSTRAINT fk_messages_receiver FOREIGN KEY (receiver_id) REFERENCES Users(id)
);

-- 18. Invoices
CREATE TABLE Invoices (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          booking_id INT NOT NULL,
                          invoice_number VARCHAR(50) UNIQUE NOT NULL,
                          pdf_url VARCHAR(2048),
                          CONSTRAINT fk_invoices_booking FOREIGN KEY (booking_id) REFERENCES Bookings(id)
);

-- 19. Bank_Accounts
CREATE TABLE Bank_Accounts (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               user_id INT NOT NULL,
                               account_number_masked VARCHAR(50) NOT NULL,
                               bank_name VARCHAR(255) NOT NULL,
                               CONSTRAINT fk_bank_accounts_user FOREIGN KEY (user_id) REFERENCES Users(id)
);

-- 20. Payment_Methods
CREATE TABLE Payment_Methods (
                                 id INT AUTO_INCREMENT PRIMARY KEY,
                                 user_id INT NOT NULL,
                                 provider VARCHAR(100) NOT NULL,
                                 tokenized_data VARCHAR(255) NOT NULL,
                                 CONSTRAINT fk_payment_methods_user FOREIGN KEY (user_id) REFERENCES Users(id)
);

-- 21. Wishlists
CREATE TABLE Wishlists (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           customer_id INT NOT NULL,
                           vehicle_id INT NOT NULL,
                           CONSTRAINT fk_wishlist_customer FOREIGN KEY (customer_id) REFERENCES Users(id),
                           CONSTRAINT fk_wishlist_vehicle FOREIGN KEY (vehicle_id) REFERENCES Vehicles(id)
);

-- 22. Platform_Content (Đã sửa lỗi `key`)
CREATE TABLE Platform_Content (
                                  id INT AUTO_INCREMENT PRIMARY KEY,
                                  `key` VARCHAR(100) UNIQUE NOT NULL,
                                  title VARCHAR(255) NOT NULL,
                                  body_text TEXT NOT NULL,
                                  updated_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  updated_by INT,
                                  CONSTRAINT fk_content_updater FOREIGN KEY (updated_by) REFERENCES Users(id)
);

-- 23. Moderation_Logs
CREATE TABLE Moderation_Logs (
                                 id INT AUTO_INCREMENT PRIMARY KEY,
                                 moderator_id INT NOT NULL,
                                 target_entity_id INT NOT NULL,
                                 action_taken ENUM('APPROVED_LISTING', 'REJECTED_LISTING', 'SUSPENDED_USER', 'REMOVED_REVIEW', 'RESOLVED_DISPUTE'),
                                 timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 CONSTRAINT fk_mod_logs_moderator FOREIGN KEY (moderator_id) REFERENCES Users(id)
);

-- 24. Notifications
CREATE TABLE Notifications (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               user_id INT NOT NULL,
                               type VARCHAR(50) NOT NULL,
                               channel ENUM('EMAIL', 'SMS', 'PUSH'),
                               status ENUM('SENT', 'PENDING', 'FAILED') DEFAULT 'PENDING',
                               sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES Users(id)
);