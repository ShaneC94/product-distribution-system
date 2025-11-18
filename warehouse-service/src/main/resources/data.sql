INSERT INTO products (id, product_code, name) VALUES (1, 'SKU-100', 'Widget A');
INSERT INTO products (id, product_code, name) VALUES (2, 'SKU-101', 'Widget B');

INSERT INTO inventories (id, warehouse_id, product_id, available_quantity, reserved_quantity, updated_at)
VALUES (1, 101, 1, 100, 0, NOW());

INSERT INTO inventories (id, warehouse_id, product_id, available_quantity, reserved_quantity, updated_at)
VALUES (2, 102, 1, 50, 0, NOW());

INSERT INTO inventories (id, warehouse_id, product_id, available_quantity, reserved_quantity, updated_at)
VALUES (3, 101, 2, 200, 0, NOW());
