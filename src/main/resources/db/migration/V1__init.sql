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
)
