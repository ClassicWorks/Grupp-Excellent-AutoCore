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

INSERT INTO mechanics (name, phone, specialization, available) VALUES ('Sara Nilsson', '070-5552222', 'Brakes', 0);

INSERT INTO mechanics (name, phone, specialization, available) VALUES ('Mikael Berg', '070-5553333', 'Diagnostics', 0);


-- Service items
INSERT INTO service_item (name, description, price, estimated_minutes) VALUES ('Oil change', 'Engine oil and oil filter replacement', 1295.0, 45);

INSERT INTO service_item (name, description, price, estimated_minutes) VALUES ('Brake service', 'Inspection and replacement of front brake pads', 2495.0, 90);

INSERT INTO service_item (name, description, price, estimated_minutes) VALUES ('Diagnostics', 'Electronic fault code diagnostics', 995.0, 60);

INSERT INTO service_item (name, description, price, estimated_minutes) VALUES ('Annual service', 'Standard annual vehicle service', 3495.0, 120);

-- Bookings

INSERT INTO bookings (vehicle_id, date, description, status) VALUES (1, '2026-09-01', 'Oil change and routine inspection', 'WORK_ORDER_CREATED');

INSERT INTO bookings (vehicle_id, date, description, status) VALUES (2, '2026-09-02', 'Brake inspection and service', 'IN_PROGRESS');

INSERT INTO bookings (vehicle_id, date, description, status) VALUES (3, '2026-09-03', 'Electrical system diagnostics', 'COMPLETED');

INSERT INTO bookings (vehicle_id, date, description, status) VALUES (4, '2026-09-05', 'Engine performance inspection', 'BOOKED');

INSERT INTO bookings (vehicle_id, date, description, status) VALUES (5, '2026-09-06', 'Annual vehicle service', 'WORK_ORDER_CREATED');

INSERT INTO bookings (vehicle_id, date, description, status) VALUES (6, '2026-09-07', 'Brake pads inspection', 'BOOKED');

INSERT INTO bookings (vehicle_id, date, description, status) VALUES (7, '2026-09-08', 'Oil change and filter replacement', 'IN_PROGRESS');

INSERT INTO bookings (vehicle_id, date, description, status) VALUES (8, '2026-09-09', 'Annual service and safety inspection', 'BOOKED');

INSERT INTO bookings (vehicle_id, date, description, status) VALUES (9, '2026-09-12', 'Diagnostic scan and fault investigation', 'COMPLETED');

INSERT INTO bookings (vehicle_id, date, description, status) VALUES (1, '2026-09-14', 'Follow-up vehicle inspection', 'BOOKED');

-- Workorders
-- WorkOrder 1: En tjänst, ännu inte påbörjad
INSERT INTO work_orders (booking_id, mechanic_id, status) VALUES ( 1, 1, 'CREATED');

-- WorkOrder 2: Två tjänster, arbete pågår
INSERT INTO work_orders (booking_id, mechanic_id, status) VALUES ( 2, 2, 'IN_PROGRESS');

-- WorkOrder 3: Tre tjänster, arbetet är klart
INSERT INTO work_orders (booking_id, mechanic_id, status) VALUES (3, 3, 'COMPLETED');

-- WorkOrder 4: Två tjänster, ännu inte påbörjad
INSERT INTO work_orders (booking_id, mechanic_id, status) VALUES ( 5, 2, 'CREATED');

-- WorkOrder 5: Fyra tjänster, arbete pågår
INSERT INTO work_orders (booking_id, mechanic_id, status) VALUES ( 7, 3, 'IN_PROGRESS');

-- WorkOrder 6: En tjänst, arbetet är klart
INSERT INTO work_orders (booking_id, mechanic_id, status) VALUES ( 9, 1, 'COMPLETED');

-- WorkOrder 1: ServiceItem 1
INSERT INTO join_work_order_service_item (work_order_id, service_item_id) VALUES (1, 1);

-- WorkOrder 2: ServiceItem 2 och 3
INSERT INTO join_work_order_service_item (work_order_id, service_item_id) VALUES (2, 2);
INSERT INTO join_work_order_service_item (work_order_id, service_item_id) VALUES (2, 3);

-- WorkOrder 3: ServiceItem 1, 3 och 4
INSERT INTO join_work_order_service_item (work_order_id, service_item_id) VALUES (3, 1);
INSERT INTO join_work_order_service_item (work_order_id, service_item_id) VALUES (3, 3);
INSERT INTO join_work_order_service_item (work_order_id, service_item_id) VALUES (3, 4);

-- WorkOrder 4: ServiceItem 1 och 4
INSERT INTO join_work_order_service_item (work_order_id, service_item_id) VALUES (4, 1);
INSERT INTO join_work_order_service_item (work_order_id, service_item_id) VALUES (4, 4);

-- WorkOrder 5: Alla fyra ServiceItems
INSERT INTO join_work_order_service_item (work_order_id, service_item_id) VALUES (5, 1);
INSERT INTO join_work_order_service_item (work_order_id, service_item_id) VALUES (5, 2);
INSERT INTO join_work_order_service_item (work_order_id, service_item_id) VALUES (5, 3);
INSERT INTO join_work_order_service_item (work_order_id, service_item_id) VALUES (5, 4);

-- WorkOrder 6: ServiceItem 4
INSERT INTO join_work_order_service_item (work_order_id, service_item_id) VALUES (6, 4);

-- Work order items (orderrader med fryst pris)
-- WorkOrder 1: Oil change
INSERT INTO work_order_items (work_order_id, service_item_id, price_at_order) VALUES (1, 1, 1295.0);

-- WorkOrder 2: Brake service, Diagnostics
INSERT INTO work_order_items (work_order_id, service_item_id, price_at_order) VALUES (2, 2, 2495.0);
INSERT INTO work_order_items (work_order_id, service_item_id, price_at_order) VALUES (2, 3, 995.0);

-- WorkOrder 3: Oil change, Diagnostics, Annual service
INSERT INTO work_order_items (work_order_id, service_item_id, price_at_order) VALUES (3, 1, 1295.0);
INSERT INTO work_order_items (work_order_id, service_item_id, price_at_order) VALUES (3, 3, 995.0);
INSERT INTO work_order_items (work_order_id, service_item_id, price_at_order) VALUES (3, 4, 3495.0);

-- WorkOrder 4: Oil change, Annual service
-- Oil change bokades till ett äldre pris (1195 kr) innan prishöjningen till 1295 kr
INSERT INTO work_order_items (work_order_id, service_item_id, price_at_order) VALUES (4, 1, 1195.0);
INSERT INTO work_order_items (work_order_id, service_item_id, price_at_order) VALUES (4, 4, 3495.0);

-- WorkOrder 5: Alla fyra tjänster
INSERT INTO work_order_items (work_order_id, service_item_id, price_at_order) VALUES (5, 1, 1295.0);
INSERT INTO work_order_items (work_order_id, service_item_id, price_at_order) VALUES (5, 2, 2495.0);
INSERT INTO work_order_items (work_order_id, service_item_id, price_at_order) VALUES (5, 3, 995.0);
INSERT INTO work_order_items (work_order_id, service_item_id, price_at_order) VALUES (5, 4, 3495.0);

-- WorkOrder 6: Annual service
INSERT INTO work_order_items (work_order_id, service_item_id, price_at_order) VALUES (6, 4, 3495.0);

-- Invoices
-- Invoice för WorkOrder 3: Betald, 10 % rabatt
INSERT INTO invoices (work_order_id, invoice_date, amount, discount, total_amount, paid) VALUES (3, '2026-09-29', 5785.00, 578.50, 5206.50, TRUE);

-- Invoice för WorkOrder 6: Obetald, 0 % rabatt
INSERT INTO invoices (work_order_id, invoice_date, amount, discount, total_amount, paid) VALUES (6, '2026-09-29', 3495.00, 0.00, 3495.00, FALSE);

-- Payment för invoice 2
INSERT INTO payments (invoice_id, amount, payment_type, payment_date, successful) VALUES (1, 3495.00, 'CARD', '2026-09-30', FALSE);
INSERT INTO payments (invoice_id, amount, payment_type, payment_date, successful) VALUES (1, 3495.00, 'CASH', '2026-09-30', TRUE);