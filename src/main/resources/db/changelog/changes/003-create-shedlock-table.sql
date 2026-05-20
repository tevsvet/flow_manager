--liquibase formatted sql

--changeset tevsvet:003-create-shedlock-table
CREATE TABLE shedlock (
    name VARCHAR(64) PRIMARY KEY NOT NULL,
    lock_until TIMESTAMP NOT NULL,
    locked_at TIMESTAMP NOT NULL DEFAULT NOW(),
    locked_by VARCHAR(255) NOT NULL
);