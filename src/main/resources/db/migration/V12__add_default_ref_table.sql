-- af.f_default_ref --
CREATE TABLE af.f_default_ref
(
    id          CHARACTER VARYING NOT NULL,
    party_id    CHARACTER VARYING,
    shop_id     CHARACTER VARYING,
    template_id CHARACTER VARYING NOT NULL,
    CONSTRAINT f_default_ref_pkey PRIMARY KEY (id),
    CONSTRAINT f_default_ref_uniq_party_shop UNIQUE (party_id, shop_id)
);

-- af.p2p_f_default_ref --
CREATE TABLE af.p2p_f_default_ref
(
    id          CHARACTER VARYING NOT NULL,
    identity_id CHARACTER VARYING,
    template_id CHARACTER VARYING NOT NULL,
    CONSTRAINT p2p_f_default_ref_pkey PRIMARY KEY (id),
    CONSTRAINT p2p_f_default_ref_uniq_identity UNIQUE (identity_id)
);

ALTER TABLE af.f_reference DROP COLUMN is_default;
