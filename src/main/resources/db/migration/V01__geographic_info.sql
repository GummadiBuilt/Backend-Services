CREATE TABLE country (
   country_iso_code  VARCHAR(3),
   country_name      VARCHAR(100) NOT NULL,
   created_by        VARCHAR(255) NOT NULL,
   created_date      TIMESTAMP    NOT NULL,
   modified_by       VARCHAR(255) NOT NULL,
   modified_date     TIMESTAMP    NOT NULL,
   PRIMARY KEY (country_iso_code)
);

-- Disallow duplicate country names (case insensitive)
CREATE UNIQUE INDEX country_name_unq_name ON country (Lower(country_name));

CREATE TABLE state (
   state_iso_code        VARCHAR(3),
   state_name            VARCHAR(150)  NOT NULL,
   country_id            VARCHAR(3)    NOT NULL REFERENCES country(country_iso_code),
   created_by            VARCHAR(255)  NOT NULL,
   created_date          TIMESTAMP     NOT NULL,
   modified_by           VARCHAR(255)  NOT NULL,
   modified_date         TIMESTAMP     NOT NULL,
   PRIMARY KEY (state_iso_code)
);

-- Disallow duplicate state names (case insensitive)
CREATE UNIQUE INDEX state_name_unq_name ON state (Lower(state_name));


CREATE TABLE city (
  id                SERIAL       NOT NULL,
  city_name         VARCHAR(100) NOT NULL,
  state_id          VARCHAR(3)   NOT NULL REFERENCES state(state_iso_code),
  created_by        VARCHAR(255) NOT NULL,
  created_date      TIMESTAMP    NOT NULL,
  modified_by       VARCHAR(255) NOT NULL,
  modified_date     TIMESTAMP    NOT NULL,
  PRIMARY KEY (id)
);

CREATE UNIQUE INDEX city_unique_const ON city (city_name, state_id);