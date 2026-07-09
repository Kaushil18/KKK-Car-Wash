CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    booking_date DATE NOT NULL,
    booking_time TIME NOT NULL,
    vehicle VARCHAR(100),
    notes TEXT,
    status ENUM('pending', 'confirmed', 'completed', 'cancelled') DEFAULT 'pending',
    total DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('card', 'cash') DEFAULT 'cash',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(id),
    INDEX idx_user_id (user_id),
    INDEX idx_booking_date (booking_date),
    INDEX idx_status (status),
    INDEX idx_user_service (user_id, service_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample bookings
INSERT INTO bookings (user_id, service_id, booking_date, booking_time, vehicle, status, total, payment_method) VALUES
(2, 1, CURDATE(), '10:00:00', 'Sedan', 'confirmed', 25.00, 'card'),
(2, 2, CURDATE() + INTERVAL 1 DAY, '14:00:00', 'SUV', 'pending', 45.00, 'cash'),
(2, 3, CURDATE() - INTERVAL 2 DAY, '09:30:00', 'Hatchback', 'completed', 70.00, 'card');