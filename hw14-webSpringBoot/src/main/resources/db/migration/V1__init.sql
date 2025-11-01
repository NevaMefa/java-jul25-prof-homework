-- V1__init.sql

CREATE TABLE IF NOT EXISTS client (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    address_street VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS phone (
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(64) NOT NULL,
    client_id BIGINT NOT NULL REFERENCES client(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_phone_client_id ON phone (client_id);
