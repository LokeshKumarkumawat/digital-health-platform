/* ============================================================
   PAYMENT SEQUENCE
   ============================================================ */

create sequence if not exists payments_seq
    start with 1
    increment by 1
    no minvalue
    no maxvalue
    cache 1;


/* ============================================================
   PAYMENTS TABLE
   ============================================================ */

create table if not exists payments (
    id bigint primary key default nextval('payments_seq'),

    payment_intent_id varchar(255) not null unique,
    stripe_charge_id varchar(255) unique,

    amount numeric(19, 2) not null,
    currency varchar(3) not null,

    status varchar(50) not null,
    payment_method varchar(50),

    description text,
    receipt_url text,
    failure_reason text,

    user_id bigint not null,
    appointment_id bigint,

    paid_at timestamptz,
    refunded_at timestamptz,

    metadata text,

    version bigint not null default 0,

    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),

    constraint fk_payments_user
        foreign key (user_id)
        references users(id),

    constraint fk_payments_appointment
        foreign key (appointment_id)
        references appointments(id)
);


/* ============================================================
   INDEXES
   ============================================================ */

create index if not exists idx_payments_user_id
    on payments(user_id);

create index if not exists idx_payments_appointment_id
    on payments(appointment_id);

create index if not exists idx_payments_status
    on payments(status);

create index if not exists idx_payments_payment_method
    on payments(payment_method);

create index if not exists idx_payments_created_at
    on payments(created_at);


/* ============================================================
   CHECK CONSTRAINTS (ENUM SAFETY)
   ============================================================ */

/* PaymentStatus enum */
alter table payments
    drop constraint if exists chk_payments_status;

alter table payments
    add constraint chk_payments_status
    check (
        status in (
            'PENDING',
            'PROCESSING',
            'REQUIRES_ACTION',
            'REQUIRES_CONFIRMATION',
            'REQUIRES_PAYMENT_METHOD',
            'SUCCEEDED',
            'FAILED',
            'CANCELLED',
            'REFUNDED',
            'DISPUTED'
        )
    );


/* PaymentMethod enum */
alter table payments
    drop constraint if exists chk_payments_method;

alter table payments
    add constraint chk_payments_method
    check (
        payment_method in (
            'CARD',
            'BANK_TRANSFER',
            'ACH_DEBIT',
            'WALLET',
            'CASH',
            'INSURANCE',
            'OTHER'
        )
    );

