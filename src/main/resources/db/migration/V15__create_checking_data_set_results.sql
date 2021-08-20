CREATE TABLE af.test_data_set_checking_result
(
    id                 BIGSERIAL         NOT NULL,
    test_data_set_id   BIGINT            NOT NULL,
    initiator          CHARACTER VARYING NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),

    template           TEXT              NOT NULL,
    party_id           CHARACTER VARYING,
    shop_id            CHARACTER VARYING,
    checking_timestamp TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT test_data_set_checking_result_pkey PRIMARY KEY (id),
    CONSTRAINT fk_test_data_set FOREIGN KEY (test_data_set_id) REFERENCES af.test_data_set (id)
);

CREATE TABLE af.test_payment_checking_result
(
    id                               BIGSERIAL NOT NULL,
    test_data_set_checking_result_id BIGINT    NOT NULL,
    test_payment_id                  CHARACTER VARYING    NOT NULL,

    checked_template                 CHARACTER VARYING,
    result_status                    CHARACTER VARYING,
    rule_checked                     CHARACTER VARYING,
    notifications_rule               TEXT[],

    CONSTRAINT test_payment_checking_result_pkey PRIMARY KEY (id),
    CONSTRAINT fk_test_data_set_checking_result FOREIGN KEY (test_data_set_checking_result_id) REFERENCES af.test_data_set_checking_result (id),
    CONSTRAINT fk_test_payment_id FOREIGN KEY (test_payment_id) REFERENCES af.test_payment (payment_id)
);
