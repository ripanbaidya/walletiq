-- Insert default roles
-- This migration is idempotent and safe to re-run

INSERT INTO roles (id, name, created_at, created_by)
SELECT gen_random_uuid()::varchar,
       'USER',
       now(),
       'system'
WHERE NOT EXISTS (SELECT 1
                  FROM roles
                  WHERE name = 'USER');

INSERT INTO roles (id, name, created_at, created_by)
SELECT gen_random_uuid()::varchar,
       'ADMIN',
       now(),
       'system'
WHERE NOT EXISTS (SELECT 1
                  FROM roles
                  WHERE name = 'ADMIN');
