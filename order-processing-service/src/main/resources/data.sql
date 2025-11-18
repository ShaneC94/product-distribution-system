-- Insert sample data into the customer_order table (Master Table)
-- Note: assigned_warehouse_id column is REMOVED from the INSERT

-- Order 1: RECEIVED - Just created, processing has begun (2 days ago)
INSERT INTO customer_order (id, customer_id, delivery_address, status, created_at)
VALUES (
           1,
           101,
           '123 Oak St, Toronto, ON M1B 1B1',
           'RECEIVED',
           NOW() - INTERVAL 2 DAY
       );

-- Order 2: STOCK_RESERVED - All items found and reserved (1 day ago)
INSERT INTO customer_order (id, customer_id, delivery_address, status, created_at)
VALUES (
           2,
           102,
           '456 Pine Ave, Montreal, QC H2Y 1W1',
           'STOCK_RESERVED',
           NOW() - INTERVAL 1 DAY
       );

-- Order 3: DELIVERED - Completed order (7 days ago)
INSERT INTO customer_order (id, customer_id, delivery_address, status, created_at)
VALUES (
           3,
           103,
           '789 Maple Rd, Vancouver, BC V6C 1C1',
           'DELIVERED',
           NOW() - INTERVAL 7 DAY
       );

-- Insert sample data into the order_item table (Child Table)
-- FULFILLMENT IS TRACKED HERE

-- Order 1 Items (Status: RECEIVED - Items should be PENDING)
-- Item 1 (Order 1): 10 Widgets
INSERT INTO order_item (order_id, product_code, quantity, fulfilled_by_warehouse_id, item_status)
VALUES (1, 'WIDGET_A', 10, NULL, 'PENDING');

-- Item 2 (Order 1): 5 Gadgets
INSERT INTO order_item (order_id, product_code, quantity, fulfilled_by_warehouse_id, item_status)
VALUES (1, 'GADGET_B', 5, NULL, 'PENDING');


-- Order 2 Items (Status: STOCK_RESERVED - Items should be RESERVED)
-- Item 3 (Order 2): 20 Blue Shirts, reserved by Warehouse 10
INSERT INTO order_item (order_id, product_code, quantity, fulfilled_by_warehouse_id, item_status)
VALUES (2, 'SHIRT_BLUE', 20, 10, 'RESERVED');

-- Item 4 (Order 2): 5 Green Hats, reserved by Warehouse 12 (Example of Split Shipment)
INSERT INTO order_item (order_id, product_code, quantity, fulfilled_by_warehouse_id, item_status)
VALUES (2, 'HAT_GREEN', 5, 12, 'RESERVED');


-- Order 3 Items (Status: DELIVERED - Items should be RESERVED/Completed)
-- Item 5 (Order 3): 1 Laptop, reserved by Warehouse 10
INSERT INTO order_item (order_id, product_code, quantity, fulfilled_by_warehouse_id, item_status)
VALUES (3, 'LAPTOP_XYZ', 1, 10, 'RESERVED');