-- Fleet Manager database schema
-- Kør med: psql -d fleet_manager -f schema.sql

DROP TABLE IF EXISTS vehicle_assignments CASCADE;
DROP TABLE IF EXISTS vehicles            CASCADE;
DROP TABLE IF EXISTS employees           CASCADE;
DROP TABLE IF EXISTS departments         CASCADE;
DROP TABLE IF EXISTS users               CASCADE;

-- Afdelinger
CREATE TABLE departments (
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    department_no VARCHAR(20)
);

-- Brugere der kan logge ind
CREATE TABLE users (
    id        SERIAL PRIMARY KEY,
    username  VARCHAR(60)  NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    role      VARCHAR(20)  NOT NULL  -- 'ADMIN' eller 'FLEET_MANAGER'
);

-- Medarbejdere
CREATE TABLE employees (
    id            SERIAL PRIMARY KEY,
    employee_no   VARCHAR(20),
    full_name     VARCHAR(150) NOT NULL,
    email         VARCHAR(150),
    phone         VARCHAR(40),
    department_id INTEGER REFERENCES departments(id),
    role          VARCHAR(20) NOT NULL,  -- 'BILFOERER' eller 'BILANSVARLIG'
    status        VARCHAR(20) NOT NULL DEFAULT 'ANSAT'
);

-- Biler
CREATE TABLE vehicles (
    id            SERIAL PRIMARY KEY,
    license_plate VARCHAR(20)  NOT NULL UNIQUE,
    brand         VARCHAR(60),
    model         VARCHAR(60),
    vehicle_type  VARCHAR(20)  NOT NULL,  -- 'PERSONBIL', 'VAREBIL', 'LASTBIL'
    plate_color   VARCHAR(10)  NOT NULL,  -- 'HVID', 'GUL'
    ownership     VARCHAR(100),           -- leasingselskab f.eks. 'Nordania'
    leasing_type  VARCHAR(20),            -- 'OPERATIONEL', 'FINANSIEL', 'EJET'
    agreement_no  VARCHAR(60),
    max_km        INTEGER,
    delivery_date DATE,
    leasing_start DATE,
    leasing_end   DATE,
    expiry_date   DATE,
    department_id INTEGER REFERENCES departments(id),
    status        VARCHAR(20) NOT NULL DEFAULT 'AKTIV'
);

-- Kobling mellem biler og bilførere
CREATE TABLE vehicle_assignments (
    id            SERIAL PRIMARY KEY,
    vehicle_id    INTEGER NOT NULL REFERENCES vehicles(id),
    employee_id   INTEGER NOT NULL REFERENCES employees(id),
    assigned_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    unassigned_at TIMESTAMP  -- null = tildeling er stadig aktiv
);

-- En bil kan kun have én aktiv bilfører ad gangen
CREATE UNIQUE INDEX one_active_driver_per_vehicle
    ON vehicle_assignments(vehicle_id)
    WHERE unassigned_at IS NULL;
