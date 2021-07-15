CREATE TABLE af.p2p_wb_list_records
(
    id          CHARACTER VARYING NOT NULL,
    identity_id CHARACTER VARYING,
    list_type   af.list_type      NOT NULL,
    list_name   CHARACTER VARYING NOT NULL,
    value       CHARACTER VARYING NOT NULL,
    row_info    text,
    insert_time TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
    CONSTRAINT p2p_wb_list_records_pkey PRIMARY KEY (id)
);

CREATE
UNIQUE INDEX p2p_wb_list_key on af.p2p_wb_list_records (identity_id, list_type, list_name, value);

CREATE TABLE af.p2p_f_template
(
    id       CHARACTER VARYING NOT NULL,
    template TEXT              NOT NULL,
    CONSTRAINT p2p_f_template_pkey PRIMARY KEY (id)
);

CREATE TABLE af.p2p_f_reference
(
    id          CHARACTER VARYING NOT NULL,
    identity_id CHARACTER VARYING,
    template_id CHARACTER VARYING NOT NULL,
    is_global   BOOLEAN           NOT NULL,
    CONSTRAINT p2p_f_reference_pkey PRIMARY KEY (id),
    CONSTRAINT p2p_f_reference_uniq_party_shop UNIQUE (identity_id, is_global)
);

CREATE
UNIQUE INDEX p2p_f_reference_key on af.p2p_f_reference (identity_id, is_global);

CREATE TABLE af.p2p_f_group
(
    id          BIGSERIAL         NOT NULL,
    group_id    CHARACTER VARYING NOT NULL,
    priority    BIGINT            NOT NULL,
    template_id CHARACTER VARYING NOT NULL,
    CONSTRAINT p2p_f_group_pkey PRIMARY KEY (id),
    CONSTRAINT p2p_f_group_uniq_group_template UNIQUE (group_id, template_id)
);

CREATE TABLE af.p2p_f_group_reference
(
    id          BIGSERIAL         NOT NULL,
    identity_id CHARACTER VARYING,
    group_id    CHARACTER VARYING NOT NULL,
    CONSTRAINT p2p_f_group_reference_pkey PRIMARY KEY (id),
    CONSTRAINT p2p_f_group_reference_uniq_party_shop UNIQUE (identity_id)
);

CREATE
UNIQUE INDEX p2p_f_group_reference_key on af.p2p_f_group_reference (identity_id);
