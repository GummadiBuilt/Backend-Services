ALTER TABLE application_form DROP COLUMN IF EXISTS year_one;
ALTER TABLE application_form DROP COLUMN IF EXISTS year_one_revenue;
ALTER TABLE application_form DROP COLUMN IF EXISTS year_one_file_name;
ALTER TABLE application_form DROP COLUMN IF EXISTS year_one_file_size;

ALTER TABLE application_form DROP COLUMN IF EXISTS year_two;
ALTER TABLE application_form DROP COLUMN IF EXISTS year_two_revenue;
ALTER TABLE application_form DROP COLUMN IF EXISTS year_two_file_name;
ALTER TABLE application_form DROP COLUMN IF EXISTS year_two_file_size;

ALTER TABLE application_form DROP COLUMN IF EXISTS year_three;
ALTER TABLE application_form DROP COLUMN IF EXISTS year_three_revenue;
ALTER TABLE application_form DROP COLUMN IF EXISTS year_three_file_name;
ALTER TABLE application_form DROP COLUMN IF EXISTS year_three_file_size;

ALTER TABLE application_form ADD COLUMN IF NOT EXISTS turn_over_details JSONB;
