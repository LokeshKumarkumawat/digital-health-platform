create table patients (
    id bigint primary key default nextval('patients_seq'),
    first_name varchar(100),
    last_name varchar(100),
    date_of_birth date,
    phone varchar(20),
    known_allergies text,
    blood_group varchar(20),
    genotype varchar(20),
    user_id bigint unique,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    version bigint not null default 0,
    constraint fk_patients_user
        foreign key (user_id) references users(id)
);
