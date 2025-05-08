-- liquibase formatted sql
-- changeset tgoro:1

ALTER TABLE users
    ADD CONSTRAINT gender_check CHECK ( gender IN ('MALE', 'FEMALE'));

ALTER table authorities
    ADD CONSTRAINT authorities_check CHECK ( authority IN ( 'READ', 'WRITE', 'EDIT', 'DELETE', 'EDIT_AUTHORITIES', 'DISABLE_USERS', 'DELETE_USERS'));

-- changeset tgoro:2
ALTER table authorities
    DROP CONSTRAINT authorities_check;

ALTER table authorities
    ADD CONSTRAINT authorities_check CHECK ( authority IN ( 'READ', 'WRITE', 'EDIT', 'DELETE', 'EDIT_AUTHORITIES', 'DISABLE_USERS', 'DELETE_USERS', 'MODERATE'));