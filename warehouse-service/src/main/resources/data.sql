DELETE FROM inventories;
DELETE FROM products;

INSERT INTO products (product_code, name)
VALUES
    (1000, 'T-Shirt'),
    (1001, 'Jeans'),
    (1002, 'Hoodie'),
    (1003, 'Jacket'),
    (1004, 'Sweater'),
    (1005, 'Dress Shirt'),
    (1006, 'Polo Shirt'),
    (1007, 'Shorts'),
    (1008, 'Sweatpants'),
    (1009, 'Dress Pants'),
    (1010, 'Blazer'),
    (1011, 'Cardigan'),
    (1012, 'Tank Top'),
    (1013, 'Skirt'),
    (1014, 'Dress')
    ;

INSERT INTO inventories (warehouse_id, product_id, available_quantity, reserved_quantity, updated_at)
VALUES
    (1, (SELECT id FROM products WHERE product_code = 1001), 100, 0, NOW()),
    (2, (SELECT id FROM products WHERE product_code = 1001), 50, 0, NOW()),
    (3, (SELECT id FROM products WHERE product_code = 1002), 200, 0, NOW());