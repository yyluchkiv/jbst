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
