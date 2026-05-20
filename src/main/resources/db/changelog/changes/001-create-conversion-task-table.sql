--liquibase formatted sql

--changeset tevsvet:001-create-conversion-task-table
CREATE TABLE conversion_task (
    id UUID PRIMARY KEY NOT NULL,
    status VARCHAR(32) NOT NULL,
    source_bucket VARCHAR(255) NOT NULL,
    source_object_key VARCHAR(1024) NOT NULL,
    source_file_type VARCHAR(32) NOT NULL,
    result_bucket VARCHAR(255),
    result_object_key VARCHAR(1024),
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
