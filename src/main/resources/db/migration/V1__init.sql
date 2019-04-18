CREATE SCHEMA IF NOT EXISTS af;

CREATE TYPE af.list_type AS ENUM('white', 'black');

-- wb.list --
CREATE TABLE af.wb_list_records(
  id                       CHARACTER VARYING NOT NULL,
  party_id                 CHARACTER VARYING NOT NULL,
  shop_id                  CHARACTER VARYING NOT NULL,
  list_type                af.list_type NOT NULL,
  list_name                CHARACTER VARYING NOT NULL,
  value                    CHARACTER VARYING NOT NULL,
  insert_time              TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  CONSTRAINT wb_list_records_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX wb_list_key on af.wb_list_records(party_id, shop_id, list_type, list_name, value);
