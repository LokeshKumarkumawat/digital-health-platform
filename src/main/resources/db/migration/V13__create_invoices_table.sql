/* ============================================================
   INVOICES SEQUENCE
   ============================================================ */

create sequence if not exists invoices_seq
    start with 1
    increment by 1
    no minvalue
    no maxvalue
    cache 1;


/* ============================================================
   INVOICES TABLE
   ============================================================ */

create table if not exists invoices (
    id bigint primary key default nextval('invoices_seq'),

    invoice_number varchar(50) not null unique,

    subtotal numeric(19, 2) not null,
    tax numeric(19, 2) not null,
    total numeric(19, 2) not null,

    currency varchar(3) not null,

    status varchar(50) not null,

    user_id bigint not null,
    appointment_id bigint unique,
    payment_id bigint unique,

    description text,
    line_items text,

    issue_date timestamptz,
    due_date timestamptz,
    paid_date timestamptz,

    notes text,
    pdf_url text,

    version bigint not null default 0,

    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),

    constraint fk_invoices_user
        foreign key (user_id)
        references users(id),

    constraint fk_invoices_appointment
        foreign key (appointment_id)
        references appointments(id),

    constraint fk_invoices_payment
        foreign key (payment_id)
        references payments(id)
);


/* ============================================================
   INDEXES
   ============================================================ */

create index if not exists idx_invoices_invoice_number
    on invoices(invoice_number);

create index if not exists idx_invoices_user_id
    on invoices(user_id);

create index if not exists idx_invoices_status
    on invoices(status);

create index if not exists idx_invoices_issue_date
    on invoices(issue_date);

create index if not exists idx_invoices_due_date
    on invoices(due_date);

create index if not exists idx_invoices_created_at
    on invoices(created_at);


/* ============================================================
   CHECK CONSTRAINTS (ENUM SAFETY)
   ============================================================ */

/* InvoiceStatus enum */
alter table invoices
    drop constraint if exists chk_invoices_status;

alter table invoices
    add constraint chk_invoices_status
    check (
        status in (
            'DRAFT',
            'PENDING',
            'PROCESSING',
            'PAID',
            'PARTIALLY_PAID',
            'OVERDUE',
            'CANCELLED',
            'REFUNDED',
            'WRITTEN_OFF'
        )
    );
