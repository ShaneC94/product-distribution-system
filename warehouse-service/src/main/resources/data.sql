DELETE FROM inventories;
DELETE FROM products;

INSERT INTO products (product_code, name)
VALUES
    (1001, 'Widget A'),
    (1002, 'Widget B');

INSERT INTO inventories (warehouse_id, product_id, available_quantity, reserved_quantity, updated_at)
VALUES
    (1, (SELECT id FROM products WHERE product_code = 1001), 100, 0, NOW()),
    (2, (SELECT id FROM products WHERE product_code = 1001), 50, 0, NOW()),
    (3, (SELECT id FROM products WHERE product_code = 1002), 200, 0, NOW());