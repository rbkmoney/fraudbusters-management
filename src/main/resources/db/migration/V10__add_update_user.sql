ALTER TABLE af.f_reference ADD COLUMN modified_by_user CHARACTER VARYING;
ALTER TABLE af.f_template ADD COLUMN modified_user CHARACTER VARYING;

ALTER TABLE af.p2p_f_template ADD COLUMN modified_by_user CHARACTER VARYING;
ALTER TABLE af.p2p_f_reference ADD COLUMN modified_by_user CHARACTER VARYING;

ALTER TABLE af.f_group ADD COLUMN modified_by_user CHARACTER VARYING;
ALTER TABLE af.f_group_reference ADD COLUMN modified_by_user CHARACTER VARYING;

ALTER TABLE af.p2p_f_group ADD COLUMN modified_by_user CHARACTER VARYING;
ALTER TABLE af.p2p_f_group_reference ADD COLUMN modified_by_user CHARACTER VARYING;

ALTER TABLE af.wb_list_records ADD COLUMN created_by_user CHARACTER VARYING;
