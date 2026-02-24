CREATE DATABASE IF NOT EXISTS clientdb;
USE clientdb;

-- Drop in dependency order
DROP TABLE IF EXISTS employment;
DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS client;

CREATE TABLE client (
  client_id INT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(50) NOT NULL,
  last_name  VARCHAR(50) NOT NULL,
  employment VARCHAR(100) NOT NULL
);

-- 1:1 row per client (client_id is both PK and FK)
CREATE TABLE person (
  client_id INT PRIMARY KEY,
  first_name VARCHAR(50) NOT NULL,
  last_name  VARCHAR(50) NOT NULL,
  date_of_birth DATE NOT NULL,
  address VARCHAR(255) NOT NULL,
  legal_sex VARCHAR(20) NOT NULL,
  CONSTRAINT fk_person_client
    FOREIGN KEY (client_id) REFERENCES client(client_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

-- 1:1 row per client (client_id is both PK and FK)
CREATE TABLE employment (
  client_id INT PRIMARY KEY,
  business_name VARCHAR(100) NOT NULL,
  position_name VARCHAR(100) NOT NULL,
  salary DECIMAL(12,2) NOT NULL,
  CONSTRAINT fk_employment_client
    FOREIGN KEY (client_id) REFERENCES client(client_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);