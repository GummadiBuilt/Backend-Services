CREATE SEQUENCE tender_id_seq INCREMENT BY 1 MINVALUE 1	MAXVALUE 2147483647	START 1	CACHE 1	NO CYCLE;

CREATE TABLE type_of_contract (
  id                                SERIAL       NOT NULL,
  type_of_contract                  VARCHAR(100) NOT NULL,
  contract_short_code               VARCHAR(3) NOT NULL,
  is_active                         BOOLEAN,
  created_by                        VARCHAR(255) NOT NULL,
  created_date                      TIMESTAMP    NOT NULL,
  modified_by                       VARCHAR(255) NOT NULL,
  modified_date                     TIMESTAMP    NOT NULL,
  PRIMARY KEY (id)
);

CREATE UNIQUE INDEX type_of_contract_const ON type_of_contract (Lower(type_of_contract));
CREATE UNIQUE INDEX contract_short_code_const ON type_of_contract (Lower(contract_short_code));

INSERT INTO type_of_contract (type_of_contract,contract_short_code,is_active,created_by,created_date,modified_by,modified_date) VALUES
	 ('Itemised Fixed Rate','IFR',true,'sriharsha','2022-11-15 15:16:48.123','sriharsha','2022-11-15 15:16:48.123'),
	 ('Fixed Lumpsum Price','FLP',true,'sriharsha','2022-11-15 15:16:48.123','sriharsha','2022-11-15 15:16:48.123'),
	 ('Design & Build','D&B',true,'sriharsha','2022-11-15 15:16:48.123','sriharsha','2022-11-15 15:16:48.123'),
	 ('GMP','GMP',true,'sriharsha','2022-11-15 15:16:48.123','sriharsha','2022-11-15 15:16:48.123'),
	 ('Cost Plus Percentage','CPP',true,'sriharsha','2022-11-15 15:16:48.123','sriharsha','2022-11-15 15:16:48.123');

CREATE TABLE tender_info (
  id                                VARCHAR(50)  NOT NULL,
  application_user_id               VARCHAR(255) NOT NULL REFERENCES application_user(id),
  type_of_establishment_desc        VARCHAR(255) NOT NULL REFERENCES type_of_establishment(establishment_description),
  work_description                  VARCHAR(50)  NOT NULL,
  project_location                  VARCHAR(50)  NOT NULL,
  type_of_contract_id               INT4         NOT NULL REFERENCES type_of_contract(id),
  contract_duration                 NUMERIC(5)   NOT NULL,
  duration_counter                  VARCHAR(50)  NOT NULL,
  last_date_of_submission           DATE         NOT NULL,
  estimated_budget                  NUMERIC(20),
  workflow_step                     VARCHAR(255) NOT NULL,
  tender_document_name              VARCHAR(255) NOT NULL,
  tender_document_size              INT8         NOT NULL,
  tender_finance_info               JSONB        NOT NULL,
  created_by                        VARCHAR(255) NOT NULL,
  created_date                      TIMESTAMP    NOT NULL,
  modified_by                       VARCHAR(255) NOT NULL,
  modified_date                     TIMESTAMP    NOT NULL,
  PRIMARY KEY (id)
);