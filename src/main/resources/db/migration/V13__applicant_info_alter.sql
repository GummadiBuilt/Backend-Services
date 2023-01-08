ALTER TABLE tender_applicants ADD COLUMN IF NOT EXISTS application_status varchar(50) DEFAULT 'UNDER_PROCESS';

DROP INDEX IF EXISTS tender_applicant_rank_unq_name;