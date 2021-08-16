CREATE TABLE af.test_data_set
(
    id                          BIGSERIAL         NOT NULL,
    name                        CHARACTER VARYING NOT NULL,
    last_modification_initiator CHARACTER VARYING NOT NULL,
    last_modification_time      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
    CONSTRAINT testing_data_set_pkey PRIMARY KEY (id),
    UNIQUE (name)
);

CREATE TABLE af.test_payment
(
    id                          BIGSERIAL         NOT NULL,
    test_data_set_id            bigint         NOT NULL,
    last_modification_initiator CHARACTER VARYING NOT NULL,
    last_modification_time      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),

    payment_id                  CHARACTER VARYING NOT NULL,
    event_time                  TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),

    amount                      INT               NOT NULL,
    currency                    CHARACTER VARYING NOT NULL,

    card_token                  CHARACTER VARYING NOT NULL,
    status                      CHARACTER VARYING NOT NULL,

    payer_type                  CHARACTER VARYING,

    payment_system              CHARACTER VARYING,
    payment_country             CHARACTER VARYING,
    payment_tool                CHARACTER VARYING,

    mobile                      boolean,
    recurrent                   boolean,

    party_id                    CHARACTER VARYING NOT NULL,
    shop_id                     CHARACTER VARYING NOT NULL,

    ip                          CHARACTER VARYING,
    fingerprint                 CHARACTER VARYING,
    email                       CHARACTER VARYING,

    error_code                  CHARACTER VARYING,
    error_reason                CHARACTER VARYING,

    provider_id                 CHARACTER VARYING,
    terminal_id                 CHARACTER VARYING,
    country                     CHARACTER VARYING,

    CONSTRAINT data_set_payment_pkey PRIMARY KEY (id),
    CONSTRAINT fk_test_data_set FOREIGN KEY (test_data_set_id) REFERENCES af.test_data_set (id)
);

