create table roles (
    id bigint primary key default nextval('roles_seq'),
    name varchar(50) not null unique
);
