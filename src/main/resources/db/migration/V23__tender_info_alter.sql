ALTER TABLE tender_info ALTER COLUMN tender_finance_info drop not null;

CREATE TABLE tender_client_document (
  id                   SERIAL       NOT NULL,
  file_name            VARCHAR(255) NOT NULL,
  file_size            INT8         NOT NULL,
  tender_info_id       VARCHAR(255) NOT NULL REFERENCES tender_info(id),
  application_user_id  VARCHAR(255)      NOT NULL REFERENCES application_user(id),
  created_by           VARCHAR(255) NOT NULL,
  created_date         TIMESTAMP    NOT NULL,
  modified_by          VARCHAR(255) NOT NULL,
  modified_date        TIMESTAMP    NOT NULL,
  PRIMARY KEY (id)
);