ALTER TABLE tender_applicants ADD COLUMN IF NOT EXISTS is_recommended boolean;

UPDATE tender_applicants set is_recommended = false;

CREATE TABLE tender_bid_info (
  id                                SERIAL       NOT NULL,
  application_user_id               VARCHAR(50)  NOT NULL REFERENCES application_user(id),
  tender_info_id                    VARCHAR(50)  NOT NULL REFERENCES tender_info(id),
  tender_document_name              VARCHAR(255) NOT NULL,
  tender_document_size              INT8         NOT NULL,
  tender_finance_info               JSONB        NOT NULL,
  action_taken                      VARCHAR(25)  NOT NULL,
  created_by                        VARCHAR(255) NOT NULL,
  created_date                      TIMESTAMP    NOT NULL,
  modified_by                       VARCHAR(255) NOT NULL,
  modified_date                     TIMESTAMP    NOT NULL,
  PRIMARY KEY (id)
);

CREATE UNIQUE INDEX tender_bid_info_unique ON tender_bid_info (application_user_id, tender_info_id);