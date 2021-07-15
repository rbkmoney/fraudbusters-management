CREATE TYPE af.object_type AS ENUM('template', 'reference', 'group_reference', 'group', 'p2p_group_reference', 'p2p_reference',
'black', 'white', 'grey', 'naming');
CREATE TYPE af.command_type AS ENUM('CREATE', 'DELETE');

-- af.command_audit --
CREATE TABLE af.command_audit(
                                 id                   BIGSERIAL         NOT NULL,
                                 initiator            CHARACTER VARYING NOT NULL,
                                 object_type          af.object_type       NOT NULL,
                                 command_type         af.command_type      NOT NULL,
                                 object               CHARACTER VARYING NOT NULL,
                                 insert_time          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
                                 CONSTRAINT command_audit_pkey PRIMARY KEY (id)
);
