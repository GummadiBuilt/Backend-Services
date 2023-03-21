ALTER TABLE application_form ALTER COLUMN type_of_establishment TYPE VARCHAR(2500);
ALTER TABLE application_form ADD COLUMN IF NOT EXISTS esi_registration_file_name VARCHAR(255);
ALTER TABLE application_form ADD COLUMN IF NOT EXISTS epf_registration_file_name VARCHAR(255);
ALTER TABLE application_form ADD COLUMN IF NOT EXISTS gst_registration_file_name VARCHAR(255);
ALTER TABLE application_form ADD COLUMN IF NOT EXISTS pan_file_name VARCHAR(255);
