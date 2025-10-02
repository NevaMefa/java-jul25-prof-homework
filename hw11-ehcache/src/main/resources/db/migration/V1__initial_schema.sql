-- SEQUENCES (совместимы с @SequenceGenerator)
create sequence if not exists client_seq  start 1 increment 1;
create sequence if not exists address_seq start 1 increment 1;
create sequence if not exists phone_seq   start 1 increment 1;

-- ADDRESS
create table if not exists address (
    id     bigint primary key default nextval('address_seq'),
    street varchar(255) not null
);

-- CLIENT
create table if not exists client (
    id         bigint primary key default nextval('client_seq'),
    name       varchar(255) not null,
    address_id bigint not null unique,
    constraint fk_client_address
        foreign key (address_id) references address(id)
);

-- PHONE
create table if not exists phone (
    id        bigint primary key default nextval('phone_seq'),
    number    varchar(255) not null,
    client_id bigint not null,
    constraint fk_phone_client
        foreign key (client_id) references client(id)
);

-- helpful indexes
create index if not exists idx_phone_client_id on phone(client_id);
create unique index if not exists uq_client_address_id on client(address_id);
