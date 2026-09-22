create sequence if not exists users_seq start with 1 increment by 1;
create sequence if not exists roles_seq start with 1 increment by 1;
create sequence if not exists password_reset_code_seq start with 1 increment by 1;

create sequence if not exists patients_seq start with 1 increment by 1;
create sequence if not exists doctors_seq start with 1 increment by 1;

create sequence if not exists appointments_seq start with 1 increment by 1;
create sequence if not exists consultations_seq start with 1 increment by 1;
create sequence if not exists notifications_seq start with 1 increment by 1;