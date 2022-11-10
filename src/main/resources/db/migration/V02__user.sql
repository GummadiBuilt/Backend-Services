CREATE TABLE type_of_establishment (
  establishment_description         VARCHAR(255) NOT NULL,
  is_active                         BOOLEAN,
  created_by                        VARCHAR(255) NOT NULL,
  created_date                      TIMESTAMP    NOT NULL,
  modified_by                       VARCHAR(255) NOT NULL,
  modified_date                     TIMESTAMP    NOT NULL,
  PRIMARY KEY (establishment_description)
);

-- Disallow duplicate establishment_description's (case insensitive)
CREATE UNIQUE INDEX establishment_desc_unq_name ON type_of_establishment (Lower(establishment_description));

CREATE TABLE application_role (
  id                SERIAL NOT NULL,
  role_name         VARCHAR(25),
  role_description  VARCHAR(255),
  display_to_all    BOOLEAN,
  created_by        VARCHAR(255) NOT NULL,
  created_date      TIMESTAMP    NOT NULL,
  modified_by       VARCHAR(255) NOT NULL,
  modified_date     TIMESTAMP    NOT NULL,
  PRIMARY KEY (id)
);

-- Disallow duplicate application_role names (case insensitive)
CREATE UNIQUE INDEX role_name_unq_name ON application_role (Lower(role_name));

CREATE TABLE application_user (
  id                        SERIAL          NOT NULL,
  contact_email_address     VARCHAR(100)    NOT NULL,
  contact_first_name        VARCHAR(75)     NOT NULL,
  contact_last_name         VARCHAR(75)     NOT NULL,
  contact_designation       VARCHAR(150)    NOT NULL,
  contact_phone_number      VARCHAR(10)     NOT NULL,
  application_role_id       INT4            NOT NULL REFERENCES application_role(id),
  company_name              VARCHAR(100)    NOT NULL,
  year_of_establishment     INTEGER         NOT NULL,
  type_of_establishment     VARCHAR[],
  address                   VARCHAR(500)    NOT NULL,
  country_id                VARCHAR(3)      NOT NULL REFERENCES country (country_iso_code),
  state_id                  VARCHAR(3)      NOT NULL REFERENCES state (state_iso_code),
  city_id                   INT4            NOT NULL REFERENCES city (id),
  is_active                 BOOLEAN         NOT NULL,
  credentials_expired       BOOLEAN         NOT NULL,
  created_by                VARCHAR(255)    NOT NULL,
  created_date              TIMESTAMP       NOT NULL,
  modified_by               VARCHAR(255)    NOT NULL,
  modified_date             TIMESTAMP       NOT NULL,
  PRIMARY KEY (id)
);

-- Disallow duplicate application_users email (case insensitive)
CREATE UNIQUE INDEX app_user_unq_email ON application_user (Lower(contact_email_address));

CREATE TABLE user_registration
(
  id                        SERIAL          NOT NULL,
  contact_email_address     VARCHAR(100)    NOT NULL,
  contact_first_name        VARCHAR(75)     NOT NULL,
  contact_last_name         VARCHAR(75)     NOT NULL,
  contact_designation       VARCHAR(150)    NOT NULL,
  contact_phone_number      VARCHAR(10)     NOT NULL,
  approve_reject            VARCHAR(255)    NOT NULL,
  application_role_id       INT4            NOT NULL REFERENCES application_role (id),
  company_name              VARCHAR(100)    NOT NULL,
  year_of_establishment     INTEGER         NOT NULL,
  type_of_establishment     VARCHAR[],
  address                   VARCHAR(500)    NOT NULL,
  country_id                VARCHAR(3)      NOT NULL REFERENCES country (country_iso_code),
  state_id                  VARCHAR(3)      NOT NULL REFERENCES state (state_iso_code),
  city_id                   INT4            NOT NULL REFERENCES city (id),
  created_by                VARCHAR(255)    NOT NULL,
  created_date              TIMESTAMP       NOT NULL,
  modified_by               VARCHAR(255)    NOT NULL,
  modified_date             TIMESTAMP       NOT NULL,
  PRIMARY KEY (id)
);