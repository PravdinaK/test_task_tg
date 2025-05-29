CREATE TABLE IF NOT EXISTS users (
   id BIGINT PRIMARY KEY,
   first_name VARCHAR(255),
   last_name VARCHAR(255),
   username VARCHAR(255),
   language_code VARCHAR(10),
   photo_url TEXT,
   allows_write_to_pm BOOLEAN,
   auth_date TIMESTAMP
);