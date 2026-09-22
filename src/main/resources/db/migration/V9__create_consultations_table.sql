create table consultations (
    id bigint primary key default nextval('consultations_seq'),
    consultation_date timestamptz,
    subjective_notes text,
    objective_findings text,
    assessment text,
    plan text,
    appointment_id bigint not null unique,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    version bigint not null default 0,
    constraint fk_consultations_appointment
        foreign key (appointment_id) references appointments(id)
);
