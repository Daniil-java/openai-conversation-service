--liquibase formatted sql

--changeset DanielK:3

CREATE TABLE IF NOT EXISTS models (
    id              serial not null PRIMARY KEY,
    provider        text,
    model           text,
    description     text,
    updated         timestamp,
    created         timestamp DEFAULT current_timestamp
);

INSERT INTO models (provider, model)
VALUES ('OPENAI', 'gpt-4o');

INSERT INTO models (provider, model)
VALUES ('GEMINI', 'gemini-1.5-pro');

