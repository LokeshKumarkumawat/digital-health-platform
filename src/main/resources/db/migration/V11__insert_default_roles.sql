insert into roles (name)
values
    ('ROLE_PATIENT'),
    ('ROLE_DOCTOR'),
    ('ROLE_ADMIN')
on conflict (name) do nothing;
