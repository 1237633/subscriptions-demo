-- liquibase formatted sql
-- changeset tgoro:1

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- changeset tgoro:2

CREATE TABLE users
(
    username varchar(45) PRIMARY KEY,
    password varchar(150),
    enabled  boolean
);

CREATE TABLE authorities
(
    id        uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    authority varchar(45),
    username  varchar(45)
);

-- changeset tgoro:3

CREATE TABLE subscriptions
(
    id   uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    owner varchar(45),
    renewal_url text,
    DTYPE varchar(45),
    type varchar(45),
    level varchar(45)
);
