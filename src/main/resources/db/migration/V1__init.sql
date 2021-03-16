create table variants
(
    id    bigserial primary key,
    chrom varchar not null,
    pos   bigint  not null,
    ref   varchar,
    alt   varchar,
    unique (chrom, pos, ref, alt)
);

create table annotations
(
    id         bigserial primary key,
    variant_id bigint not null references variants,
    info       jsonb  not null,
    db_name    varchar
)
