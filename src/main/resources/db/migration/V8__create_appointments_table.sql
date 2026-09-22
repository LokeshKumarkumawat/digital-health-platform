create table appointments (
    id bigint primary key default nextval('appointments_seq'),
    start_time timestamptz not null,
    end_time timestamptz,
    meeting_link varchar(500),
    purpose_of_consultation text,
    initial_symptoms text,
    status varchar(30) not null,
    doctor_id bigint not null,
    patient_id bigint not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    version bigint not null default 0,
    constraint fk_appointments_doctor
        foreign key (doctor_id) references doctors(id),
    constraint fk_appointments_patient
        foreign key (patient_id) references patients(id)
);

create index idx_appointments_doctor on appointments(doctor_id);
create index idx_appointments_patient on appointments(patient_id);
create index idx_appointments_status on appointments(status);
