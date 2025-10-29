CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS registered_people (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  badge_id VARCHAR(64) UNIQUE NOT NULL,
  full_name TEXT NOT NULL,
  role TEXT NOT NULL,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ DEFAULT NOW()
);

INSERT INTO registered_people (badge_id, full_name, role, is_active) VALUES
  ('B-0001', 'Alice Martin', 'Engineer', TRUE),
  ('B-0002', 'Bilal Ahmed', 'Security', TRUE),
  ('B-0003', 'Chloé Dupont', 'HR', FALSE),
  ('B-0004', 'Diego García', 'Operations', TRUE)
ON CONFLICT DO NOTHING;

