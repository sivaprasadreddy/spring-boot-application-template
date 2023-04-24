create sequence user_id_seq start with 1 increment by 10;

create table users
(
    id       bigint DEFAULT nextval('user_id_seq') not null,
    email    varchar(255)                          not null,
    password varchar(255)                          not null,
    name     varchar(255)                          not null,
    primary key (id),
    constraint user_email_unique unique (email)
);
