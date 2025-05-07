-- liquibase formatted sql
-- changeset tgoro:1

ALTER TABLE authorities
    ADD CONSTRAINT users_authorities_fk FOREIGN KEY (username) REFERENCES users (username) ON DELETE CASCADE ;

-- changeset tgoro:2

ALTER TABLE subscriptions
    ADD CONSTRAINT users_subscriptions_fk FOREIGN KEY (owner) REFERENCES users (username) ON DELETE CASCADE ;