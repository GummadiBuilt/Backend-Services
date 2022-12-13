CREATE TABLE pq_form_header (
  id                                SERIAL  NOT NULL,
  project_name                      VARCHAR(255) NOT NULL,
  work_package                      VARCHAR(50)  NOT NULL,
  type_of_structure                 VARCHAR(255) NOT NULL,
  contract_duration                 NUMERIC(5)   NOT NULL,
  duration_counter                  VARCHAR(50)  NOT NULL,
  pq_document_issue_date            DATE,
  pq_last_date_of_submission        DATE         NOT NULL,
  tentative_date_of_award           DATE         NOT NULL,
  scheduled_completion              DATE         NOT NULL,
  tender_info_id                    VARCHAR(50)  NOT NULL REFERENCES tender_info(id),
  created_by                        VARCHAR(255) NOT NULL,
  created_date                      TIMESTAMP    NOT NULL,
  modified_by                       VARCHAR(255) NOT NULL,
  modified_date                     TIMESTAMP    NOT NULL,
  PRIMARY KEY (id)
);

-- Disallow duplicate state names (case insensitive)
CREATE UNIQUE INDEX pq_form_header_unq_name ON pq_form_header (Lower(tender_info_id));