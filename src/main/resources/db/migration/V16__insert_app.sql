-- ============================================================
-- Fix: Add expires_at column to appointments table
-- This column was expected by the JPA entity but missing
-- from the original schema definition
-- ============================================================

ALTER TABLE appointments
    ADD COLUMN IF NOT EXISTS expires_at timestamptz;

-- Create index for expires_at if needed for queries
CREATE INDEX IF NOT EXISTS idx_appointments_expires_at
    ON appointments (expires_at)
    WHERE expires_at IS NOT NULL;

-- Populate expires_at for existing appointments
-- Set to 24 hours after end_time for completed appointments
UPDATE appointments
SET expires_at = end_time + INTERVAL '24 hours'
WHERE status = 'COMPLETED'
  AND end_time IS NOT NULL
  AND expires_at IS NULL;

-- Set expires_at for scheduled appointments
-- (end_time + grace period for cancellation)
UPDATE appointments
SET expires_at = start_time + INTERVAL '48 hours'
WHERE status IN ('SCHEDULED', 'PENDING')
  AND expires_at IS NULL;

-- Update sequences to avoid conflicts
SELECT setval('appointments_seq',
              (SELECT COALESCE(MAX(id), 1) FROM appointments));