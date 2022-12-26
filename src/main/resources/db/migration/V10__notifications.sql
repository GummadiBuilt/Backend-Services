CREATE TABLE notifications (
  id                    SERIAL         NOT NULL,
  notification_type     VARCHAR(100)   NOT NULL,
  message_header        VARCHAR(50)    NOT NULL,
  message_details       VARCHAR(255)   NOT NULL,
  tender_info_id        VARCHAR(255)   REFERENCES tender_info(id),
  created_by            VARCHAR(255)   NOT NULL,
  created_date          DATE           NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE user_notifications (
  id                   SERIAL         NOT NULL,
  application_user_id  VARCHAR(100)   NOT NULL REFERENCES application_user(id),
  notification_id      INT4           NOT NULL REFERENCES notifications(id),
  created_by           VARCHAR(255)   NOT NULL,
  created_date         TIMESTAMP      NOT NULL,
  modified_by          VARCHAR(255)   NOT NULL,
  modified_date        TIMESTAMP      NOT NULL,
  PRIMARY KEY (id)
);