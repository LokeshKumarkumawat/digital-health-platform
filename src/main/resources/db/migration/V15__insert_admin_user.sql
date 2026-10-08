-- ============================================================
-- V16: INSERT ADMIN USER
-- Login: admin@digitalhealth.in / health247@123
-- ============================================================

-- ============================================================
-- 1. ADMIN USER
-- ============================================================

INSERT INTO users (
    id,
    name,
    email,
    password,
    auth_provider,
    has_profile_complete,
    created_at,
    updated_at,
    version
)
VALUES (
           1,
           'System Administrator',
           'admin@digitalhealth.in',
           '$2a$10$PwzCvdqTz2txsDF8Uy91ve/76hRh4rW9cyobl4iolR./q7h52hquu',
           'LOCAL',
           true,
           NOW(),
           NOW(),
           0
       )
    ON CONFLICT (email) DO NOTHING;

-- ============================================================
-- 2. ASSIGN ADMIN ROLE (role_id = 3 for ROLE_ADMIN based on V11)
-- ============================================================

INSERT INTO user_roles (user_id, role_id)
VALUES (1, 3)
    ON CONFLICT DO NOTHING;

-- ============================================================
-- 3. UPDATE SEQUENCE
-- ============================================================

SELECT setval('users_seq', (SELECT COALESCE(MAX(id), 1) FROM users));