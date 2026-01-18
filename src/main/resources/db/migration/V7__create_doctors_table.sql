create table doctors (
    id bigint primary key default nextval('doctors_seq'),
    first_name varchar(100),
    last_name varchar(100),
    specialization varchar(50),
    license_number varchar(100) not null unique,
    user_id bigint not null unique,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    version bigint not null default 0,
    constraint fk_doctors_user
        foreign key (user_id) references users(id)
);
