CREATE TABLE af.test_data_set
(
    id                          BIGSERIAL         NOT NULL,
    name                        CHARACTER VARYING NOT NULL,
    last_modification_initiator CHARACTER VARYING NOT NULL,
    last_modification_time      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
    CONSTRAINT testing_data_set_pkey PRIMARY KEY (id)
);

CREATE TABLE af.test_payment
(
    id                          BIGSERIAL         NOT NULL,
    test_data_set_id            BIGSERIAL         NOT NULL,
    last_modification_initiator CHARACTER VARYING NOT NULL,
    last_modification_time      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),

    payment_id                  CHARACTER VARYING NOT NULL,
    eventTime                   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),

    amount                      INT               NOT NULL,
    currency                    CHARACTER VARYING NOT NULL,

    cardToken                   CHARACTER VARYING NOT NULL,
    status                      CHARACTER VARYING NOT NULL,

    payerType                   CHARACTER VARYING NOT NULL,

    paymentSystem               CHARACTER VARYING NOT NULL,
    paymentCountry              CHARACTER VARYING NOT NULL,
    paymentTool                 CHARACTER VARYING NOT NULL,

    mobile                      boolean           NOT NULL,
    recurrent                   boolean           NOT NULL,

    partyId                     CHARACTER VARYING NOT NULL,
    shopId                      CHARACTER VARYING NOT NULL,

    ip                          CHARACTER VARYING NOT NULL,
    fingerprint                 CHARACTER VARYING NOT NULL,
    email                       CHARACTER VARYING NOT NULL,

    errorCode                   CHARACTER VARYING NOT NULL,
    errorReason                 CHARACTER VARYING NOT NULL,

    providerId                  CHARACTER VARYING NOT NULL,
    terminalId                  CHARACTER VARYING NOT NULL,
    country                     CHARACTER VARYING NOT NULL,

    CONSTRAINT data_set_payment_pkey PRIMARY KEY (id),
    CONSTRAINT fk_test_data_set FOREIGN KEY (test_data_set_id) REFERENCES af.test_data_set (id)
);

