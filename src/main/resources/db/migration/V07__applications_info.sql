CREATE TABLE application_form (
  id                                SERIAL       NOT NULL,
  company_name                      VARCHAR(255) NOT NULL,
  year_of_establishment             VARCHAR(4)   NOT NULL,
  type_of_establishment             VARCHAR(255) NOT NULL,
  corp_office_address               VARCHAR(500),
  local_office_address              VARCHAR(500),
  telephone_num                     VARCHAR(10),
  fax_number                        VARCHAR(10),
  contact_name                      VARCHAR(255),
  contact_designation               VARCHAR(255),
  contact_phone_num                 VARCHAR(10),
  contact_email_id                  VARCHAR(100),
  regional_head_name                VARCHAR(255),
  regional_head_phone_num           VARCHAR(10),
  similar_projects                  JSONB,
  client_references                 JSONB,
  similar_project_nature            JSONB,
  esi_registration                  VARCHAR(255),
  epf_registration                  VARCHAR(255),
  gst_registration                  VARCHAR(255),
  pan_number                        VARCHAR(25),
  employees_strength                JSONB,
  capital_equipment                 JSONB,
  safety_policy_manual              VARCHAR(500),
  ppe_to_staff                      VARCHAR(500),
  ppe_to_work_men                   VARCHAR(500),
  safety_office_availability        VARCHAR(500),
  financial_information             JSONB,
  company_bankers                   JSONB,
  company_auditors                  JSONB,
  under_taking                      BOOLEAN DEFAULT TRUE,
  action_taken                      VARCHAR(25)  NOT NULL,
  tender_info_id                    VARCHAR(50)  NOT NULL REFERENCES tender_info(id),
  application_user_id               VARCHAR(50)  NOT NULL REFERENCES application_user(id),
  created_by                        VARCHAR(255) NOT NULL,
  created_date                      TIMESTAMP    NOT NULL,
  modified_by                       VARCHAR(255) NOT NULL,
  modified_date                     TIMESTAMP    NOT NULL,
  PRIMARY KEY (id)
);

-- Disallow duplicate state names (case insensitive)
CREATE UNIQUE INDEX application_form_unq_name ON application_form (Lower(tender_info_id),Lower(application_user_id));

CREATE TABLE tender_applicants (
  id                                SERIAL       NOT NULL,
  rank                              INT4         NOT NULL,
  tender_info_id                    VARCHAR(50)  NOT NULL REFERENCES tender_info(id),
  application_user_id               VARCHAR(50)  NOT NULL REFERENCES application_user(id),
  applicant_form_id                 INT4         NOT NULL REFERENCES application_form(id),
  created_by                        VARCHAR(255) NOT NULL,
  created_date                      TIMESTAMP    NOT NULL,
  modified_by                       VARCHAR(255) NOT NULL,
  modified_date                     TIMESTAMP    NOT NULL,
  PRIMARY KEY (id)
);

-- Disallow duplicate state names (case insensitive)
CREATE UNIQUE INDEX tender_applicant_rank_unq_name ON tender_applicants (rank,Lower(tender_info_id));
CREATE UNIQUE INDEX tender_applicant_unq_name ON tender_applicants (rank,Lower(tender_info_id),Lower(application_user_id),applicant_form_id);
