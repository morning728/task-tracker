CREATE TABLE "users" (
   id         SERIAL PRIMARY KEY,
   username   VARCHAR(64)   NOT NULL UNIQUE,
   -- password   VARCHAR(2048) NOT NULL,
   --role       VARCHAR(32)   NOT NULL,
   first_name VARCHAR(64),
   last_name  VARCHAR(64) ,
   status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
   created_at TIMESTAMP,
   updated_at TIMESTAMP
);