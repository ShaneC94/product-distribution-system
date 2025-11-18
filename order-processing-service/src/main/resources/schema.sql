CREATE TABLE IF NOT EXISTS customer_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    delivery_address VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME
);

CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,

    -- Foreign Key linking back to the parent order
    order_id BIGINT NOT NULL,

    product_code VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,

    -- Tracks which warehouse fulfilled this specific item (Split Shipment)
    fulfilled_by_warehouse_id BIGINT,

    -- Tracks the reservation status for this item (PENDING, RESERVED, NOT_AVAILABLE)
    item_status VARCHAR(50) NOT NULL,

    FOREIGN KEY (order_id) REFERENCES customer_order(id)
);