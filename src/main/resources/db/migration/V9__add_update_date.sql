ALTER TABLE af.f_reference ADD COLUMN last_update_date TIMESTAMP NOT NULL DEFAULT (now() at time zone 'utc');
ALTER TABLE af.f_template ADD COLUMN last_update_date TIMESTAMP NOT NULL DEFAULT (now() at time zone 'utc');

ALTER TABLE af.p2p_f_template ADD COLUMN last_update_date TIMESTAMP NOT NULL DEFAULT (now() at time zone 'utc');
ALTER TABLE af.p2p_f_reference ADD COLUMN last_update_date TIMESTAMP NOT NULL DEFAULT (now() at time zone 'utc');

ALTER TABLE af.f_group ADD COLUMN last_update_date TIMESTAMP NOT NULL DEFAULT (now() at time zone 'utc');
ALTER TABLE af.f_group_reference ADD COLUMN last_update_date TIMESTAMP NOT NULL DEFAULT (now() at time zone 'utc');

ALTER TABLE af.p2p_f_group ADD COLUMN last_update_date TIMESTAMP NOT NULL DEFAULT (now() at time zone 'utc');
ALTER TABLE af.p2p_f_group_reference ADD COLUMN last_update_date TIMESTAMP NOT NULL DEFAULT (now() at time zone 'utc');
