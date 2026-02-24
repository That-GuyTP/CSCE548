USE clientdb;  -- change if your DB name is different

START TRANSACTION;

-- Optional: wipe existing data (keeps schema intact)
-- Because of ON DELETE CASCADE, deleting clients clears person/employment automatically.
DELETE FROM client;

-- Create a temporary staging table to store generated client_ids
DROP TEMPORARY TABLE IF EXISTS tmp_client_ids;
CREATE TEMPORARY TABLE tmp_client_ids (
  client_id INT PRIMARY KEY
);

-- Insert 50 clients
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
('Julian','Roberts','W2');

-- Capture the client_ids that were just inserted.
-- Assumes client_id is AUTO_INCREMENT and no other inserts happened concurrently.
-- This grabs the last 50 IDs allocated in this session.
SET @max_id := (SELECT MAX(client_id) FROM client);
SET @min_id := @max_id - 49;

INSERT INTO tmp_client_ids (client_id)
SELECT client_id
FROM client
WHERE client_id BETWEEN @min_id AND @max_id
ORDER BY client_id;

-- Insert matching person rows (1:1)
-- date_of_birth and address are generated deterministically
INSERT INTO person (client_id, first_name, last_name, date_of_birth, address, legal_sex)
SELECT
  c.client_id,
  c.first_name,
  c.last_name,
  DATE_ADD('1975-01-01', INTERVAL (c.client_id % 15000) DAY) AS date_of_birth,
  CONCAT( (100 + (c.client_id % 9000)), ' Main St, Columbia, SC ', LPAD((29000 + (c.client_id % 999)), 5, '0') ) AS address,
  CASE WHEN (c.client_id % 2) = 0 THEN 'Female' ELSE 'Male' END AS legal_sex
FROM client c
JOIN tmp_client_ids t ON t.client_id = c.client_id;

-- Insert matching employment rows (1:1)
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
  ROUND(40000 + (c.client_id % 35) * 1750, 2) AS salary
FROM client c
JOIN tmp_client_ids t ON t.client_id = c.client_id;

COMMIT;

-- Sanity check
SELECT COUNT(*) AS clients FROM client;
SELECT COUNT(*) AS persons FROM person;
SELECT COUNT(*) AS employments FROM employment;