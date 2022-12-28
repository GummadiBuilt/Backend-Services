ALTER TABLE application_form ADD COLUMN IF NOT EXISTS year_one varchar(4);
ALTER TABLE application_form ADD COLUMN IF NOT EXISTS year_one_revenue INT8;
ALTER TABLE application_form ADD COLUMN IF NOT EXISTS year_one_file_name varchar(50);
ALTER TABLE application_form ADD COLUMN IF NOT EXISTS year_one_file_size INT8;

ALTER TABLE application_form ADD COLUMN IF NOT EXISTS year_two varchar(4);
ALTER TABLE application_form ADD COLUMN IF NOT EXISTS year_two_revenue INT8;
ALTER TABLE application_form ADD COLUMN IF NOT EXISTS year_two_file_name varchar(50);
ALTER TABLE application_form ADD COLUMN IF NOT EXISTS year_two_file_size INT8;

ALTER TABLE application_form ADD COLUMN IF NOT EXISTS year_three varchar(4);
ALTER TABLE application_form ADD COLUMN IF NOT EXISTS year_three_revenue INT8;
ALTER TABLE application_form ADD COLUMN IF NOT EXISTS year_three_file_name varchar(50);
ALTER TABLE application_form ADD COLUMN IF NOT EXISTS year_three_file_size INT8;