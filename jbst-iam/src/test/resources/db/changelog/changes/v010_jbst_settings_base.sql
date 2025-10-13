CREATE TABLE "jbst_settings" (
    "id"                                uuid PRIMARY KEY,
    "created_by"                        varchar(255) NOT NULL,
    "created_at"                        int8 NOT NULL,
    "updated_by"                        varchar(255) NOT NULL,
    "updated_at"                        int8 NOT NULL,
    "hardware_monitoring_thresholds"    jsonb NOT NULL
);
