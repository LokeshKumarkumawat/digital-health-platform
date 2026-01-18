create table password_reset_code (
    id bigint primary key default nextval('password_reset_code_seq'),
    code varchar(100) not null unique,
    used boolean not null default false,
    expiry_date timestamptz not null,
    user_id bigint not null,
    constraint fk_password_reset_user
        foreign key (user_id) references users(id) on delete cascade
);

create index idx_password_reset_code_user on password_reset_code(user_id);
