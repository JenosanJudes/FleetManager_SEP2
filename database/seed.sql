-- Testdata til Fleet Manager
-- Kør EFTER schema.sql

-- Afdelinger
INSERT INTO departments (name, department_no) VALUES
    ('ELHANDEL',  '100'),
    ('HOLDING',   '200'),
    ('SERVICE',   '300'),
    ('RÅDGIVNING','400');

-- Brugere (password er bare plain text til test)
INSERT INTO users (username, password, full_name, role) VALUES
    ('admin',  'admin123', 'Admin Bruger',      'ADMIN'),
    ('fleet',  'fleet123', 'Fleet Manager',     'FLEET_MANAGER');

-- Medarbejdere
INSERT INTO employees (employee_no, full_name, email, phone, department_id, role, status) VALUES
    ('E001', 'Rasmus Hansen',   'rasmus@elcon.dk',  '+45 20 11 22 33', 1, 'BILFOERER',     'ANSAT'),
    ('E002', 'Jakob Lindgren',  'jakob@elcon.dk',   '+45 20 33 44 55', 1, 'BILFOERER',     'ANSAT'),
    ('E003', 'Søren Johansen',  'soren@elcon.dk',   '+45 20 55 66 77', 4, 'BILFOERER',     'ANSAT'),
    ('E004', 'Helle Folkmann',  'helle@elcon.dk',   '+45 20 77 88 99', 2, 'BILFOERER',     'ANSAT'),
    ('E005', 'Michael Kruse',   'michael@elcon.dk', '+45 20 99 11 22', 2, 'BILANSVARLIG',  'ANSAT');

-- Biler
INSERT INTO vehicles (license_plate, brand, model, vehicle_type, plate_color, ownership, leasing_type, agreement_no, max_km, delivery_date, leasing_start, leasing_end, expiry_date, department_id, status) VALUES
    ('DT73085', 'Tesla',       'Model Y',   'PERSONBIL', 'HVID', 'Nordania',    'OPERATIONEL', 'NOR-2024-001', 90000,  '2024-05-08', '2024-05-08', '2027-05-01', '2027-06-01', 1, 'AKTIV'),
    ('DT73011', 'Tesla',       'Model Y',   'PERSONBIL', 'HVID', 'Nordania',    'OPERATIONEL', 'NOR-2024-002', 90000,  '2024-05-07', '2024-05-07', '2027-05-01', '2027-06-01', 1, 'AKTIV'),
    ('DY86762', 'Tesla',       'Model 3',   'PERSONBIL', 'HVID', 'Jyske Finans','OPERATIONEL', 'JF-2024-005',  120000, '2024-07-02', '2024-07-02', '2028-02-28', '2028-03-28', 4, 'AKTIV'),
    ('EC45760', 'BMW',         'iX3',       'PERSONBIL', 'HVID', 'Jyske Finans','OPERATIONEL', 'JF-2024-008',  135000, '2024-10-07', '2024-10-07', '2027-07-30', '2027-08-30', 1, 'AKTIV'),
    ('DP24641', 'Mercedes',    'C-Klasse',  'PERSONBIL', 'HVID', 'Nordania',    'OPERATIONEL', 'NOR-2022-010', 200000, '2022-12-02', '2023-03-02', '2028-01-01', '2028-02-01', 2, 'AKTIV'),
    ('DZ46979', 'Volvo',       'C40',       'PERSONBIL', 'HVID', 'Nordania',    'OPERATIONEL', 'NOR-2024-012', 90000,  '2024-02-28', '2024-02-28', '2027-03-01', '2027-04-01', 2, 'AKTIV'),
    ('EC64391', 'Mercedes',    'eCitan',    'VAREBIL',   'GUL',  'Nordania',    'OPERATIONEL', '81.745.717',   80000,  '2024-06-13', '2024-09-13', '2028-07-12', '2028-07-01', 3, 'AKTIV');

-- Tildelinger (hvem kører i hvilken bil)
INSERT INTO vehicle_assignments (vehicle_id, employee_id) VALUES
    (1, 1),  -- Rasmus kører DT73085
    (2, 2),  -- Jakob kører DT73011
    (3, 3),  -- Søren kører DY86762
    (4, 1),  -- Helle kører EC45760
    (5, 4);  -- Michael kører DP24641
