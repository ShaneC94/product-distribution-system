CREATE TABLE IF NOT EXISTS customer_order (
                                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                              customer_id BIGINT NOT NULL,
                                              delivery_address VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    assigned_warehouse_id BIGINT,
    created_at DATETIME
    );