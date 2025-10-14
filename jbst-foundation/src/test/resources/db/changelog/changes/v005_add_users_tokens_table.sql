CREATE TABLE IF NOT EXISTS "jbst_users_tokens"
(
    "id"               varchar(36) PRIMARY KEY,
    "username"         varchar(255) NOT NULL,
    "value"            varchar(36)  NOT NULL,
    "type"             varchar(255) NOT NULL,
    "expiry_timestamp" int8         NOT NULL,
    "used"             bool         NOT NULL
);