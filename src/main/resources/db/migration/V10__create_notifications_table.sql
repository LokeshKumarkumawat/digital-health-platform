create table notifications (
    id bigint primary key default nextval('notifications_seq'),
    subject varchar(200),
    recipient varchar(200),
    message text,
    type varchar(30),
    user_id bigint,
    created_at timestamptz not null default now(),
    constraint fk_notifications_user
        foreign key (user_id) references users(id)
);

create index idx_notifications_user on notifications(user_id);
create index idx_notifications_type on notifications(type);
