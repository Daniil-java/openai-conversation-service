--liquibase formatted sql

--changeset DanielK:2

CREATE TABLE IF NOT EXISTS conversations (
    id                          serial not null PRIMARY KEY,
    name                        text,
    updated                     timestamp,
    created                     timestamp DEFAULT current_timestamp
);

ALTER TABLE chat_messages
    ADD conversation_id INTEGER REFERENCES conversations(id);

