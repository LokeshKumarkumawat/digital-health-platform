create table users (
    id bigint primary key default nextval('users_seq'),
    name varchar(100),
    email varchar(150) not null unique,
    password varchar(255),
    auth_provider varchar(30) not null default 'LOCAL',
    profile_picture_url varchar(500),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    version bigint not null default 0
);

create index idx_users_email on users(email);
create index idx_users_auth_provider on users(auth_provider);
