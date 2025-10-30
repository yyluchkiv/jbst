-- =================================================================================================================
-- jbst
-- =================================================================================================================
CREATE TABLE "jbst_settings" (
    "id"                                uuid PRIMARY KEY,
    "created_by"                        varchar(255) NOT NULL,
    "created_at"                        int8 NOT NULL,
    "updated_by"                        varchar(255) NOT NULL,
    "updated_at"                        int8 NOT NULL,
    "hardware_monitoring_thresholds"    jsonb NOT NULL
);

INSERT INTO "jbst_settings" (
    "id",
    "created_by",
    "created_at",
    "updated_by",
    "updated_at",
    "hardware_monitoring_thresholds"
)
VALUES (
    gen_random_uuid(),
    'ops',
    EXTRACT(EPOCH FROM NOW()) * 1000,
    'ops',
    EXTRACT(EPOCH FROM NOW()) * 1000,
    '{"values": {"CPU": 80, "Heap": 80, "Swap": 95, "Server": 85, "Virtual": 85}, "enabled": true}'::jsonb
);

CREATE TABLE "jbst_users" (
    "id"                                varchar(36) PRIMARY KEY,
    "creation_option"                   varchar(255) NOT NULL,
    "username"                          varchar(255) NOT NULL,
    "password"                          varchar(255) NOT NULL,
    "enabled"                           bool NOT NULL,
    "zone_id"                           varchar(255) NOT NULL,
    "authorities"                       varchar(1024) NOT NULL,
    "email"                             varchar(255),
    "name"                              varchar(255),
    "password_change_required"          bool NOT NULL,
    "email_details"                     jsonb NOT NULL,
    "attributes"                        varchar(65535)
);

CREATE TABLE "jbst_users_sessions" (
    "id"                                varchar(36) PRIMARY KEY,
    "created_at"                        int8 NOT NULL,
    "updated_at"                        int8 NOT NULL,
    "username"                          varchar(255) NOT NULL,
    "access_token"                      varchar(4096) NOT NULL,
    "refresh_token"                     varchar(4096) NOT NULL,
    "metadata"                          varchar(65535) NOT NULL,
    "metadata_renew_cron"               bool NOT NULL,
    "metadata_renew_manually"           bool NOT NULL
);

CREATE TABLE "jbst_users_tokens" (
    "id"                                varchar(36) PRIMARY KEY,
    "email"                             varchar(255) NOT NULL,
    "username"                          varchar(255) NOT NULL,
    "value"                             varchar(255) NOT NULL,
    "type"                              varchar(255) NOT NULL,
    "expiry_timestamp"                  int8 NOT NULL,
    "used"                              bool NOT NULL
);

CREATE TABLE "jbst_invitations" (
    "id"                                varchar(36) PRIMARY KEY,
    "owner"                             varchar(255) NOT NULL,
    "authorities"                       varchar(1024) NOT NULL,
    "code"                              varchar(40) NOT NULL,
    "invited"                           varchar(255)
);

-- =================================================================================================================
-- SERVER
-- =================================================================================================================
CREATE TABLE "anything" (
    "id"                                varchar(36) PRIMARY KEY,
    "username"                          varchar(255) NOT NULL,
    "value"                             varchar
);

