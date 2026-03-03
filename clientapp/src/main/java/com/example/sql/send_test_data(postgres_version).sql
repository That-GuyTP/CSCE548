-- send_test_data_postgres.sql
-- Run this inside your existing Render Postgres database

BEGIN;

-- Optional: wipe existing data (keeps schema intact)
-- Because of ON DELETE CASCADE, deleting clients clears person/employment automatically.
-- TRUNCATE is fastest; CASCADE handles dependent tables.
TRUNCATE TABLE client RESTART IDENTITY CASCADE;

WITH inserted_clients AS (
  INSERT INTO client (first_name, last_name, employment) VALUES
  ('Ava','Johnson','W2'),
  ('Liam','Smith','1099'),
  ('Noah','Williams','W2'),
  ('Emma','Brown','Unemployed'),
  ('Olivia','Jones','W2'),
  ('Elijah','Garcia','1099'),
  ('Sophia','Miller','W2'),
  ('Lucas','Davis','W2'),
  ('Mia','Rodriguez','1099'),
  ('Charlotte','Martinez','W2'),
  ('Amelia','Hernandez','W2'),
  ('Ethan','Lopez','1099'),
  ('Harper','Gonzalez','W2'),
  ('Mason','Wilson','W2'),
  ('Evelyn','Anderson','Unemployed'),
  ('Logan','Thomas','W2'),
  ('Abigail','Taylor','1099'),
  ('Alexander','Moore','W2'),
  ('Emily','Jackson','W2'),
  ('Benjamin','Martin','1099'),
  ('Elizabeth','Lee','W2'),
  ('Henry','Perez','W2'),
  ('Sofia','Thompson','W2'),
  ('Jackson','White','1099'),
  ('Avery','Harris','W2'),
  ('Sebastian','Sanchez','W2'),
  ('Ella','Clark','Unemployed'),
  ('Daniel','Ramirez','W2'),
  ('Scarlett','Lewis','1099'),
  ('Matthew','Robinson','W2'),
  ('Victoria','Walker','W2'),
  ('Joseph','Young','1099'),
  ('Aria','Allen','W2'),
  ('Samuel','King','W2'),
  ('Grace','Wright','W2'),
  ('David','Scott','1099'),
  ('Chloe','Torres','W2'),
  ('Owen','Nguyen','W2'),
  ('Penelope','Hill','Unemployed'),
  ('Wyatt','Flores','W2'),
  ('Riley','Green','1099'),
  ('John','Adams','W2'),
  ('Lily','Nelson','W2'),
  ('Gabriel','Baker','1099'),
  ('Hannah','Hall','W2'),
  ('Carter','Rivera','W2'),
  ('Zoey','Campbell','W2'),
  ('Isaac','Mitchell','1099'),
  ('Nora','Carter','W2'),
  ('Julian','Roberts','W2')
  RETURNING client_id, first_name, last_name, employment
),
ins_person AS (
  INSERT INTO person (client_id, first_name, last_name, date_of_birth, address, legal_sex)
  SELECT
    c.client_id,
    c.first_name,
    c.last_name,
    (DATE '1975-01-01' + (c.client_id % 15000))::date AS date_of_birth,
    ((100 + (c.client_id % 9000))::text
      || ' Main St, Columbia, SC '
      || LPAD((29000 + (c.client_id % 999))::text, 5, '0')
    ) AS address,
    CASE WHEN (c.client_id % 2) = 0 THEN 'Female' ELSE 'Male' END AS legal_sex
  FROM inserted_clients c
  RETURNING client_id
)
INSERT INTO employment (client_id, business_name, position_name, salary)
SELECT
  c.client_id,
  CASE (c.client_id % 10)
    WHEN 0 THEN 'Palmetto Tech LLC'
    WHEN 1 THEN 'Garnet Health Group'
    WHEN 2 THEN 'Midlands Logistics'
    WHEN 3 THEN 'Carolina Retail Co.'
    WHEN 4 THEN 'Congaree Finance'
    WHEN 5 THEN 'Blue Ridge Energy'
    WHEN 6 THEN 'Soda City Consulting'
    WHEN 7 THEN 'Riverwalk Hospitality'
    WHEN 8 THEN 'Sandhills Manufacturing'
    ELSE 'Capital City Services'
  END AS business_name,
  CASE (c.client_id % 8)
    WHEN 0 THEN 'Analyst'
    WHEN 1 THEN 'Developer'
    WHEN 2 THEN 'Manager'
    WHEN 3 THEN 'Technician'
    WHEN 4 THEN 'Associate'
    WHEN 5 THEN 'Coordinator'
    WHEN 6 THEN 'Administrator'
    ELSE 'Specialist'
  END AS position_name,
  ROUND((40000 + (c.client_id % 35) * 1750)::numeric, 2) AS salary
FROM inserted_clients c;

COMMIT;

-- Sanity check
SELECT COUNT(*) AS clients FROM client;
SELECT COUNT(*) AS persons FROM person;
SELECT COUNT(*) AS employments FROM employment;