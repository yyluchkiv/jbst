ALTER TABLE jbst_invitations
ALTER COLUMN id TYPE uuid USING id::uuid;

ALTER TABLE jbst_users
ALTER COLUMN id TYPE uuid USING id::uuid;

ALTER TABLE jbst_users_sessions
ALTER COLUMN id TYPE uuid USING id::uuid;

ALTER TABLE jbst_users_tokens
ALTER COLUMN id TYPE uuid USING id::uuid;