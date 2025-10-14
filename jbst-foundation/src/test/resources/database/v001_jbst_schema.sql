-- =================================================================================================================
-- jbst
-- =================================================================================================================
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE "jbst_users" (
    "id" varchar(36) PRIMARY KEY,
    "username" varchar(255) NOT NULL,
    "password" varchar(255) NOT NULL,
    "zone_id" varchar(255) NOT NULL,
    "authorities" varchar(1024) NOT NULL,
    "email" varchar(255),
    "name" varchar(255),
    "attributes" varchar(65535)
);

CREATE TABLE "jbst_users_sessions" (
    "id" varchar(36) PRIMARY KEY,
    "username" varchar(255) NOT NULL,
    "access_token" varchar(4096) NOT NULL,
    "refresh_token" varchar(4096) NOT NULL,
    "metadata" varchar(65535) NOT NULL
);

CREATE TABLE "jbst_invitations" (
    "id" varchar(36),
    "owner" varchar(255) NOT NULL,
    "authorities" varchar(1024) NOT NULL,
    "code" varchar(40) NOT NULL,
    "invited" varchar
);

