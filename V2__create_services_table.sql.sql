CREATE TABLE IF NOT EXISTS services (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    duration INT NOT NULL,
    icon VARCHAR(50),
    features JSON,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default services
INSERT INTO services (name, description, price, duration, icon, features) VALUES
('Basic Wash', 'Exterior hand wash with premium soap and tire shine.', 25.00, 30, 'bi-droplet', '["Exterior Hand Wash", "Tire Shine", "Spotless Drying"]'),
('Premium Wash', 'Complete exterior and interior cleaning for a spotless finish.', 45.00, 50, 'bi-brightness-high', '["Exterior Hand Wash", "Tire Shine", "Interior Vacuum", "Dash & Console Wipe"]'),
('Deluxe Wash', 'The ultimate pampering experience for your vehicle.', 70.00, 80, 'bi-star', '["Exterior Hand Wash", "Tire Shine", "Deep Interior Cleaning", "Premium Wax Protection"]');