-- af.group --
CREATE TABLE af.f_group
(
    id          BIGSERIAL         NOT NULL,
    group_id    CHARACTER VARYING NOT NULL,
    priority    BIGINT            NOT NULL,
    template_id CHARACTER VARYING NOT NULL,
    CONSTRAINT f_group_pkey PRIMARY KEY (id),
    CONSTRAINT f_group_uniq_group_template UNIQUE (group_id, template_id)
);

-- af.group_reference --
CREATE TABLE af.f_group_reference
(
    id       BIGSERIAL         NOT NULL,
    party_id CHARACTER VARYING,
    shop_id  CHARACTER VARYING,
    group_id CHARACTER VARYING NOT NULL,
    CONSTRAINT f_group_reference_pkey PRIMARY KEY (id),
    CONSTRAINT f_group_reference_uniq_party_shop UNIQUE (party_id, shop_id)
);

CREATE
UNIQUE INDEX f_group_reference_key on af.f_group_reference (party_id, shop_id);
