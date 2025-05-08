-- liquibase formatted sql
-- changeset tgoro:1

ALTER TABLE users
    ADD COLUMN age    SMALLINT,
    ADD COLUMN gender VARCHAR(10),
    ADD COLUMN email  VARCHAR(100);

-- changeset tgoro:2
ALTER TABLE subscriptions
    ADD COLUMN expires_at TIMESTAMP;

-- changeset tgoro:3
ALTER TABLE subscriptions
    ADD COLUMN key VARCHAR(150);
