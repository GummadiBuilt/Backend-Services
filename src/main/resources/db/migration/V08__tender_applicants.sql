DROP table if exists tender_applicants CASCADE;
CREATE TABLE tender_applicants (
  id                                SERIAL       NOT NULL,
  applicant_rank                    INT4         NOT NULL,
  tender_info_id                    VARCHAR(50)  NOT NULL REFERENCES tender_info(id),
  application_user_id               VARCHAR(50)  NOT NULL REFERENCES application_user(id),
  applicant_form_id                 INT4         NOT NULL REFERENCES application_form(id),
  justification_note                VARCHAR(2500),
  created_by                        VARCHAR(255) NOT NULL,
  created_date                      TIMESTAMP    NOT NULL,
  modified_by                       VARCHAR(255) NOT NULL,
  modified_date                     TIMESTAMP    NOT NULL,
  PRIMARY KEY (id)
);

-- Disallow duplicate state names (case insensitive)
CREATE UNIQUE INDEX tender_applicant_rank_unq_name ON tender_applicants (applicant_rank,Lower(tender_info_id));
CREATE UNIQUE INDEX tender_applicant_unq_name ON tender_applicants (applicant_rank,Lower(tender_info_id),Lower(application_user_id),applicant_form_id);
