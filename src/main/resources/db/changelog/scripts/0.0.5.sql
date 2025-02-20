--liquibase formatted sql

--changeset DanielK:3

CREATE TABLE IF NOT EXISTS models (
    id              serial not null PRIMARY KEY,
    provider        text,
    model_name      text,
    description     text,
    updated         timestamp,
    created         timestamp DEFAULT current_timestamp
);

ALTER TABLE chat_messages
    ADD model_id INTEGER REFERENCES models(id);

