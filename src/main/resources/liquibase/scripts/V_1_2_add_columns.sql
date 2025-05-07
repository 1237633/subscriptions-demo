-- liquibase formatted sql
-- changeset tgoro:1

ALTER TABLE users
    ADD COLUMN age    SMALLINT,
    ADD COLUMN gender VARCHAR(10),
    ADD COLUMN email  VARCHAR(100);
