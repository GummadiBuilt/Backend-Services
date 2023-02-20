CREATE TABLE enquiry (
  id                        SERIAL       NOT NULL,
  application_role_id       INT4         NOT NULL REFERENCES application_role(id),
  user_name                 VARCHAR(25)  NOT NULL,
  email_address             VARCHAR(50)  NOT NULL,
  mobile_number             VARCHAR(10)  NOT NULL,
  enquiry_description       VARCHAR(500) NOT NULL,
  created_by                VARCHAR(255) NOT NULL,
  created_date              TIMESTAMP    NOT NULL,
  modified_by               VARCHAR(255) NOT NULL,
  modified_date             TIMESTAMP    NOT NULL,
  PRIMARY KEY (id)
);