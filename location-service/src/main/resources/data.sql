INSERT INTO warehouses (name, address, latitude, longitude) VALUES
                                                                ('Oshawa Central', '2000 Simcoe St N, Oshawa, ON', 43.945, -78.896),
                                                                ('Whitby East', '300 Taunton Rd E, Whitby, ON', 43.930, -78.928),
                                                                ('Pickering Depot', '1234 Bayly St, Pickering, ON', 43.834, -79.087),
                                                                ('Ajax South', '22 Harwood Ave S, Ajax, ON', 43.852, -79.024),
                                                                ('Scarborough North', '100 Progress Ave, Scarborough, ON', 43.774, -79.259),
                                                                ('Courtice Yard', '1800 Courtice Rd, Courtice, ON', 43.924, -78.805),
                                                                ('Bowmanville West', '150 King St W, Bowmanville, ON', 43.911, -78.693),
                                                                ('Oshawa South', '1 Ritson Rd S, Oshawa, ON', 43.894, -78.860),
                                                                ('Brooklin Depot', '5 Baldwin St N, Whitby, ON', 43.969, -78.948),
                                                                ('Port Perry Hub', '100 Water St, Port Perry, ON', 44.102, -78.940)
ON DUPLICATE KEY UPDATE
                     address = VALUES(address),
                     latitude = VALUES(latitude),
                     longitude = VALUES(longitude);
