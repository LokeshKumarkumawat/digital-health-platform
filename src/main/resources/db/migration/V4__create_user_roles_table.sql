create table user_roles (
    user_id bigint not null,
    role_id bigint not null,
    primary key (user_id, role_id),
    constraint fk_user_roles_user
        foreign key (user_id) references users(id) on delete cascade,
    constraint fk_user_roles_role
        foreign key (role_id) references roles(id) on delete cascade
);
