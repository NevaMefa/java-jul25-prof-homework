CREATE SEQUENCE IF NOT EXISTS client_seq  START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS address_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS phone_seq   START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS address (
    id     BIGINT       PRIMARY KEY,
    street VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS client (
    id         BIGINT       PRIMARY KEY,
    name       VARCHAR(255),
    address_id BIGINT       NOT NULL UNIQUE,
    CONSTRAINT fk_client_address
        FOREIGN KEY (address_id) REFERENCES address (id)
);

CREATE TABLE IF NOT EXISTS phone (
    id        BIGINT       PRIMARY KEY,
    number    VARCHAR(255) NOT NULL,
    client_id BIGINT       NOT NULL,
    CONSTRAINT fk_phone_client
        FOREIGN KEY (client_id) REFERENCES client (id)
);

CREATE INDEX IF NOT EXISTS idx_phone_client_id ON phone (client_id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_client_address_id ON client (address_id);