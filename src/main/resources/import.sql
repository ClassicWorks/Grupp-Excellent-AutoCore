-- Kunder
INSERT INTO customers (name, email, phone, vip) VALUES ('Anna Andersson', 'anna@example.com', '0701234567', 1);
INSERT INTO customers (name, email, phone, vip) VALUES ('Erik Eriksson', 'erik@example.com', '0702345678', 0);
INSERT INTO customers (name, email, phone, vip) VALUES ('Maria Karlsson', 'maria@example.com', '0703456789', 1);
INSERT INTO customers (name, email, phone, vip) VALUES ('Johan Nilsson', 'johan@example.com', '0704567890', 0);

-- Bilar
-- Anna har 3 bilar
INSERT INTO vehicles (brand, model, registration_number, year, customer_id) VALUES ('Volvo', 'V60', 'ABC123', 2022, 1);
INSERT INTO vehicles (brand, model, registration_number, year, customer_id) VALUES ('Volkswagen', 'Golf', 'DEF456', 2019, 1);
INSERT INTO vehicles (brand, model, registration_number, year, customer_id) VALUES ('Tesla', 'Model 3', 'GHI789', 2024, 1);

-- Erik har 1 bil
INSERT INTO vehicles (brand, model, registration_number, year, customer_id) VALUES ('Saab', '9-3', 'JKL012', 2008, 2);

-- Maria har 3 bilar
INSERT INTO vehicles (brand, model, registration_number, year, customer_id) VALUES ('Toyota', 'Corolla', 'MNO345', 2021, 3);
INSERT INTO vehicles (brand, model, registration_number, year, customer_id) VALUES ('Kia', 'Ceed', 'PQR678', 2020, 3);
INSERT INTO vehicles (brand, model, registration_number, year, customer_id) VALUES ('BMW', '320i', 'STU901', 2018, 3);

-- Johan har 2 bilar
INSERT INTO vehicles (brand, model, registration_number, year, customer_id) VALUES ('Skoda', 'Octavia', 'VWX234', 2023, 4);
INSERT INTO vehicles (brand, model, registration_number, year, customer_id) VALUES ('Ford', 'Focus', 'YZA567', 2017, 4);

-- Mechanics
INSERT INTO mechanics (name, phone, specialization, available) VALUES ('Johan Karlsson', '070-5551111', 'General service', 1);

INSERT INTO mechanics (name, phone, specialization, available) VALUES ('Sara Nilsson', '070-5552222', 'Brakes', 1);

INSERT INTO mechanics (name, phone, specialization, available) VALUES ('Mikael Berg', '070-5553333', 'Diagnostics', 1);


-- Service items
INSERT INTO service_item (name, description, price, estimated_minutes) VALUES ('Oil change', 'Engine oil and oil filter replacement', 1295.0, 45);

INSERT INTO service_item (name, description, price, estimated_minutes) VALUES ('Brake service', 'Inspection and replacement of front brake pads', 2495.0, 90);

INSERT INTO service_item (name, description, price, estimated_minutes) VALUES ('Diagnostics', 'Electronic fault code diagnostics', 995.0, 60);

INSERT INTO service_item (name, description, price, estimated_minutes) VALUES ('Annual service', 'Standard annual vehicle service', 3495.0, 120);