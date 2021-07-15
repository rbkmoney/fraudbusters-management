CREATE SCHEMA IF NOT EXISTS af;

CREATE TYPE af.list_type AS ENUM('white', 'black');

-- af.wb_list_records --
CREATE TABLE af.wb_list_records
(
    id          CHARACTER VARYING NOT NULL,
    party_id    CHARACTER VARYING NOT NULL,
    shop_id     CHARACTER VARYING NOT NULL,
    list_type   af.list_type      NOT NULL,
    list_name   CHARACTER VARYING NOT NULL,
    value       CHARACTER VARYING NOT NULL,
    insert_time TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
    CONSTRAINT wb_list_records_pkey PRIMARY KEY (id)
);

CREATE
UNIQUE INDEX wb_list_key on af.wb_list_records(party_id, shop_id, list_type, list_name, value);

-- af.template --
CREATE TABLE af.f_template
(
    id       CHARACTER VARYING NOT NULL,
    template TEXT              NOT NULL,
    CONSTRAINT f_template_pkey PRIMARY KEY (id)
);

-- af.reference --
CREATE TABLE af.f_reference
(
    id          CHARACTER VARYING NOT NULL,
    party_id    CHARACTER VARYING,
    shop_id     CHARACTER VARYING,
    template_id CHARACTER VARYING NOT NULL,
    is_global   BOOLEAN           NOT NULL,
    CONSTRAINT f_reference_pkey PRIMARY KEY (id),
    CONSTRAINT f_reference_uniq_party_shop UNIQUE (party_id, shop_id, is_global)
);

CREATE
UNIQUE INDEX f_reference_key on af.f_reference(party_id, shop_id, is_global);
