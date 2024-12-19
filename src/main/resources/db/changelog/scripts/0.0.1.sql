--liquibase formatted sql

--changeset DanielK:1

CREATE TABLE IF NOT EXISTS chat_messages (
    id                          serial not null PRIMARY KEY,
    role                        varchar(20) not null,
    content                     text,
    model                       text,
    temperature                 decimal(2, 1),
    message_type                varchar(20) not null,
    status                      varchar(20),
    error_details               text,
    input_token                 decimal,
    output_token                decimal,
    created                     timestamp DEFAULT current_timestamp
);


CREATE INDEX idx_role ON chat_messages(role);
CREATE INDEX idx_message_type ON chat_messages(message_type);
CREATE INDEX idx_created ON chat_messages(created);
