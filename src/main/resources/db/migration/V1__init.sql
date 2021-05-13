create table variants
(
    id           bigserial primary key,
    chrom        varchar not null,
    pos          bigint  not null,
    ref          varchar,
    alt          varchar,
    variant_code varchar not null,
    unique (chrom, pos, ref, alt),
    unique (variant_code)
);

create table annotations
(
    id         bigserial primary key,
    variant_id bigint not null references variants,
    info       jsonb  not null,
    db_name    varchar
);


create table users (
  id bigserial primary key,
  username varchar(100) not null,
  password varchar(100) not null
);

create table roles (
  id bigserial primary key,
  name varchar(100) not null
);

create table users_roles (
  user_id bigserial not null,
  roles_id bigserial not null,
  constraint user_fk foreign key (user_id) references users (id),
  constraint role_fk foreign key (roles_id) references roles (id)
);

INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

INSERT INTO users (username, password) VALUES ('admin', '$2a$10$a7OsO0RhFaKziKginvn1.ON95coPosmBE0InUuZB1OLmDzps6t6lu');
INSERT INTO users_roles (user_id, roles_id) VALUES (1, 2);
