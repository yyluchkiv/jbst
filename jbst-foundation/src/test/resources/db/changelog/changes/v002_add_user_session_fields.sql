ALTER TABLE jbst_users_sessions
    ADD "created_at" int8 NOT NULL DEFAULT(1695620000000);

ALTER TABLE jbst_users_sessions
    ADD "updated_at" int8 NOT NULL DEFAULT(1695620000000);

ALTER TABLE jbst_users_sessions
    ADD "metadata_renew_cron" bool NOT NULL DEFAULT(false);

ALTER TABLE jbst_users_sessions
    ADD "metadata_renew_manually" bool NOT NULL DEFAULT(false);
