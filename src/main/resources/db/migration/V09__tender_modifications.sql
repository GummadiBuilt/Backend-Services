ALTER TABLE pq_form_header DROP COLUMN IF EXISTS project_name;
ALTER TABLE pq_form_header DROP COLUMN IF EXISTS work_package;
ALTER TABLE pq_form_header DROP COLUMN IF EXISTS type_of_structure;
ALTER TABLE pq_form_header DROP COLUMN IF EXISTS contract_duration;
ALTER TABLE pq_form_header DROP COLUMN IF EXISTS duration_counter;

ALTER TABLE tender_info ADD COLUMN IF NOT EXISTS project_name varchar(50);
UPDATE tender_info SET project_name ='DEFAULT';
ALTER TABLE tender_info ALTER COLUMN project_name SET NOT NULL;
ALTER TABLE tender_info ALTER COLUMN work_description TYPE VARCHAR(2500);