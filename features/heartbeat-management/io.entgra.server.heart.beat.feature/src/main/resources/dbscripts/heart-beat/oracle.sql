-- -----------------------------------------------------
-- Table `SERVER_HEART_BEAT_EVENTS`
-- -----------------------------------------------------

CREATE TABLE SERVER_HEART_BEAT_EVENTS (
    ID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
    HOST_NAME VARCHAR(100)  NOT NULL,
    UUID VARCHAR(100)  NOT NULL,
    SERVER_PORT INTEGER NOT NULL,
    LAST_UPDATED_TIMESTAMP TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT PK_SERVER_HEART_BEAT_EVENTS PRIMARY KEY (ID)
)
/

CREATE TABLE ELECTED_LEADER_META_INFO (
    UUID VARCHAR(100)  NOT NULL,
    ELECTED_TIME TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ACKNOWLEDGED_TASK_LIST BLOB DEFAULT NULL,
  CONSTRAINT PK_SERVER_HEART_BEAT_EVENTS PRIMARY KEY (UUID)
)
/
