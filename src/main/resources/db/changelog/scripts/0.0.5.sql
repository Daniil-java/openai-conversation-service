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

INSERT INTO models (provider, model_name)
VALUES ('OPENAI', 'gpt-4o');

INSERT INTO models (provider, model_name)
VALUES ('GEMINI', 'gemini-1.5-pro');

INSERT INTO models (provider, model_name)
VALUES ('GEMINI', 'gemini-2.0-flash-exp');

ALTER TABLE chat_messages
    ADD model_id INTEGER REFERENCES models(id);

