--liquibase formatted sql

--changeset DanielK:4

CREATE TABLE IF NOT EXISTS users (
    id              serial not null PRIMARY KEY,
    name            text,
    balance         decimal,
    updated         timestamp,
    created         timestamp DEFAULT current_timestamp
);

ALTER TABLE conversations
    ADD user_id INTEGER REFERENCES users(id);

ALTER TABLE chat_messages
    ADD native_tokens_sum decimal,
    ADD general_tokens_sum decimal;

ALTER TABLE models
    ADD input_multiplier decimal(8, 4) not null,
    ADD output_multiplier decimal(8, 4) not null,
    ADD cached_multiplier decimal(8, 4) not null;

INSERT INTO models (provider, model_name, input_multiplier, output_multiplier, cached_multiplier)
VALUES ('OPENAI', 'gpt-4o', 2.50, 1.25, 10.00);

INSERT INTO models (provider, model_name, input_multiplier, output_multiplier, cached_multiplier)
VALUES ('GEMINI', 'gemini-1.5-pro', 1.25, 5, 0.3125);



