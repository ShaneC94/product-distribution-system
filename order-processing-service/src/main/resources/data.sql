-- Insert sample data into the customer_order table

-- Order 1: Received, assigned to a warehouse
INSERT INTO customer_order (customer_id, delivery_address, status, assigned_warehouse_id, created_at)
VALUES (
           101,
           '123 Oak St, Toronto, ON M1B 1B1',
           'RECEIVED',
           1,
           NOW() - INTERVAL 2 DAY
       );

-- Order 2: Stock reserved, ready for delivery scheduling
INSERT INTO customer_order (customer_id, delivery_address, status, assigned_warehouse_id, created_at)
VALUES (
           102,
           '456 Pine Ave, Montreal, QC H2Y 1W1',
           'STOCK_RESERVED',
           2,
           NOW() - INTERVAL 1 DAY
       );

-- Order 3: Already delivered
INSERT INTO customer_order (customer_id, delivery_address, status, assigned_warehouse_id, created_at)
VALUES (
           103,
           '789 Maple Rd, Vancouver, BC V6C 1C1',
           'DELIVERED',
           1,
           NOW() - INTERVAL 7 DAY
       );