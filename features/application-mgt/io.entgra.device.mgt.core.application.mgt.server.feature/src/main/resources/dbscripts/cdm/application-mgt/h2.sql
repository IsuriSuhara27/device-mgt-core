-- -----------------------------------------------------
-- Table AP_APP
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS AP_APP(
  ID INTEGER NOT NULL AUTO_INCREMENT,
  NAME VARCHAR(350) NOT NULL,
  DESCRIPTION CLOB NULL,
  TYPE VARCHAR(200) NOT NULL,
  TENANT_ID INTEGER NOT NULL,
  STATUS VARCHAR(45) NOT NULL DEFAULT 'ACTIVE',
  SUB_TYPE VARCHAR(45) NOT NULL,
  CURRENCY VARCHAR(45) NULL DEFAULT '$',
  RATING DOUBLE NULL DEFAULT NULL,
  DEVICE_TYPE_ID INTEGER NOT NULL,
PRIMARY KEY (ID)
);

-- -----------------------------------------------------
-- Table AP_APP_RELEASE
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS AP_APP_RELEASE(
  ID INTEGER NOT NULL AUTO_INCREMENT,
  DESCRIPTION CLOB NOT NULL,
  VERSION VARCHAR(70) NOT NULL,
  TENANT_ID INTEGER NOT NULL,
  UUID VARCHAR(200) NOT NULL,
  RELEASE_TYPE VARCHAR(45) NOT NULL,
  PACKAGE_NAME VARCHAR(150) NOT NULL,
  APP_PRICE DECIMAL(6, 2) NULL DEFAULT NULL,
  INSTALLER_LOCATION VARCHAR(100) NOT NULL,
  ICON_LOCATION VARCHAR(100) NOT NULL,
  BANNER_LOCATION VARCHAR(100) NULL DEFAULT NULL,
  SC_1_LOCATION VARCHAR(100) NOT NULL,
  SC_2_LOCATION VARCHAR(100) NULL DEFAULT NULL,
  SC_3_LOCATION VARCHAR(100) NULL DEFAULT NULL,
  APP_HASH_VALUE VARCHAR(1000) NOT NULL,
  SHARED_WITH_ALL_TENANTS BOOLEAN NOT NULL DEFAULT FALSE,
  APP_META_INFO CLOB NULL DEFAULT NULL,
  SUPPORTED_OS_VERSIONS VARCHAR(45) NOT NULL,
  RATING DOUBLE NULL DEFAULT NULL,
  CURRENT_STATE VARCHAR(45) NOT NULL,
  RATED_USERS INTEGER NULL,
  AP_APP_ID INTEGER NOT NULL,
  PRIMARY KEY (ID),
  CONSTRAINT fk_AP_APP_RELEASE_AP_APP1
  FOREIGN KEY (AP_APP_ID)
  REFERENCES AP_APP (ID) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_AP_APP_RELEASE_AP_APP1_idx ON AP_APP_RELEASE (AP_APP_ID ASC);

-- -----------------------------------------------------
-- Table AP_APP_REVIEW
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS AP_APP_REVIEW(
  ID INTEGER NOT NULL AUTO_INCREMENT,
  TENANT_ID INTEGER NOT NULL,
  COMMENT TEXT NOT NULL,
  ROOT_PARENT_ID INTEGER NOT NULL,
  IMMEDIATE_PARENT_ID INTEGER NOT NULL,
  CREATED_AT BIGINT NOT NULL,
  MODIFIED_AT BIGINT NOT NULL,
  RATING INTEGER NULL,
  USERNAME VARCHAR(45) NOT NULL,
  ACTIVE_REVIEW BOOLEAN NOT NULL DEFAULT TRUE,
  AP_APP_RELEASE_ID INTEGER NOT NULL,
  PRIMARY KEY (ID),
  CONSTRAINT fk_AP_APP_COMMENT_AP_APP_RELEASE1
  FOREIGN KEY (AP_APP_RELEASE_ID)
  REFERENCES AP_APP_RELEASE (ID) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_AP_APP_COMMENT_AP_APP_RELEASE1_idx ON AP_APP_REVIEW (AP_APP_RELEASE_ID ASC);

-- -----------------------------------------------------
-- Table AP_APP_LIFECYCLE_STATE
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS AP_APP_LIFECYCLE_STATE(
  ID INTEGER NOT NULL AUTO_INCREMENT,
  CURRENT_STATE VARCHAR(45) NOT NULL,
  PREVIOUS_STATE VARCHAR(45) NOT NULL,
  TENANT_ID INTEGER NOT NULL,
  UPDATED_BY VARCHAR(100) NOT NULL,
  UPDATED_AT BIGINT NOT NULL,
  AP_APP_RELEASE_ID INTEGER NOT NULL,
  REASON TEXT DEFAULT NULL,
  PRIMARY KEY (ID),
  CONSTRAINT fk_AP_APP_LIFECYCLE_STATE_AP_APP_RELEASE1
  FOREIGN KEY (AP_APP_RELEASE_ID)
  REFERENCES AP_APP_RELEASE (ID) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_AP_APP_LIFECYCLE_STATE_AP_APP_RELEASE1_idx ON AP_APP_LIFECYCLE_STATE( AP_APP_RELEASE_ID ASC);

-- -----------------------------------------------------
-- Table AP_APP_TAG
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS AP_APP_TAG(
  ID INTEGER NOT NULL AUTO_INCREMENT,
  TENANT_ID INTEGER NOT NULL,
  TAG VARCHAR(100) NOT NULL,
  PRIMARY KEY (ID)
);

-- -----------------------------------------------------
-- Table AP_DEVICE_SUBSCRIPTION
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS AP_DEVICE_SUBSCRIPTION(
  ID INTEGER NOT NULL AUTO_INCREMENT,
  TENANT_ID INTEGER NOT NULL,
  SUBSCRIBED_BY VARCHAR(100) NOT NULL,
  SUBSCRIBED_TIMESTAMP TIMESTAMP NOT NULL,
  UNSUBSCRIBED BOOLEAN NOT NULL DEFAULT false,
  UNSUBSCRIBED_BY VARCHAR(100) NULL DEFAULT NULL,
  UNSUBSCRIBED_TIMESTAMP TIMESTAMP NULL DEFAULT NULL,
  ACTION_TRIGGERED_FROM VARCHAR(45) NOT NULL,
  STATUS VARCHAR(45) NOT NULL,
  DM_DEVICE_ID INTEGER NOT NULL,
  AP_APP_RELEASE_ID INTEGER NOT NULL,
  PRIMARY KEY (ID),
  CONSTRAINT fk_AP_DEVICE_SUBSCRIPTION_AP_APP_RELEASE1
  FOREIGN KEY (AP_APP_RELEASE_ID)
  REFERENCES AP_APP_RELEASE (ID) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT uq_AP_DEVICE_SUBSCRIPTION UNIQUE (DM_DEVICE_ID, AP_APP_RELEASE_ID)
);
CREATE INDEX fk_AP_DEVICE_SUBSCRIPTION_AP_APP_RELEASE1_idx ON AP_DEVICE_SUBSCRIPTION (AP_APP_RELEASE_ID ASC);

-- -----------------------------------------------------
-- Table AP_GROUP_SUBSCRIPTION
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS AP_GROUP_SUBSCRIPTION(
  ID INTEGER NOT NULL AUTO_INCREMENT,
  TENANT_ID INTEGER NOT NULL,
  SUBSCRIBED_BY VARCHAR(100) NOT NULL,
  SUBSCRIBED_TIMESTAMP TIMESTAMP NOT NULL,
  UNSUBSCRIBED BOOLEAN NOT NULL DEFAULT false,
  UNSUBSCRIBED_BY VARCHAR(100) NULL DEFAULT NULL,
  UNSUBSCRIBED_TIMESTAMP TIMESTAMP NULL DEFAULT NULL,
  GROUP_NAME VARCHAR(100) NOT NULL,
  AP_APP_RELEASE_ID INTEGER NOT NULL,
  PRIMARY KEY (ID),
  CONSTRAINT fk_AP_GROUP_SUBSCRIPTION_AP_APP_RELEASE1
  FOREIGN KEY (AP_APP_RELEASE_ID)
  REFERENCES AP_APP_RELEASE (ID) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_AP_GROUP_SUBSCRIPTION_AP_APP_RELEASE1_idx ON AP_GROUP_SUBSCRIPTION (AP_APP_RELEASE_ID ASC);

-- -----------------------------------------------------
-- Table AP_ROLE_SUBSCRIPTION
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS AP_ROLE_SUBSCRIPTION(
  ID INTEGER NOT NULL AUTO_INCREMENT,
  TENANT_ID INTEGER NOT NULL,
  ROLE_NAME VARCHAR(100) NOT NULL,
  SUBSCRIBED_BY VARCHAR(100) NOT NULL,
  SUBSCRIBED_TIMESTAMP TIMESTAMP NOT NULL,
  UNSUBSCRIBED BOOLEAN NOT NULL DEFAULT false,
  UNSUBSCRIBED_BY VARCHAR(100) NULL DEFAULT NULL,
  UNSUBSCRIBED_TIMESTAMP TIMESTAMP NULL DEFAULT NULL,
  AP_APP_RELEASE_ID INTEGER NOT NULL,
  PRIMARY KEY (ID),
  CONSTRAINT fk_AP_ROLE_SUBSCRIPTION_AP_APP_RELEASE1
  FOREIGN KEY (AP_APP_RELEASE_ID)
  REFERENCES AP_APP_RELEASE (ID) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_AP_ROLE_SUBSCRIPTION_AP_APP_RELEASE1_idx ON AP_ROLE_SUBSCRIPTION (AP_APP_RELEASE_ID ASC);

-- -----------------------------------------------------
-- Table AP_UNRESTRICTED_ROLE
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS AP_UNRESTRICTED_ROLE(
  ID INTEGER NOT NULL AUTO_INCREMENT,
  TENANT_ID INTEGER NOT NULL,
  ROLE VARCHAR(45) NOT NULL,
  AP_APP_ID INTEGER NOT NULL,
  PRIMARY KEY (ID),
  CONSTRAINT fk_AP_APP_VISIBILITY_AP_APP1
  FOREIGN KEY (AP_APP_ID)
  REFERENCES AP_APP (ID) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_AP_APP_VISIBILITY_AP_APP1_idx ON AP_UNRESTRICTED_ROLE (AP_APP_ID ASC);

-- -----------------------------------------------------
-- Table AP_USER_SUBSCRIPTION
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS AP_USER_SUBSCRIPTION(
  ID INTEGER NOT NULL AUTO_INCREMENT,
  TENANT_ID INTEGER NOT NULL,
  USER_NAME VARCHAR(100) NOT NULL,
  SUBSCRIBED_BY VARCHAR(100) NOT NULL,
  SUBSCRIBED_TIMESTAMP TIMESTAMP NOT NULL,
  UNSUBSCRIBED BOOLEAN NOT NULL DEFAULT false,
  UNSUBSCRIBED_BY VARCHAR(100) NULL DEFAULT NULL,
  UNSUBSCRIBED_TIMESTAMP TIMESTAMP NULL DEFAULT NULL,
  AP_APP_RELEASE_ID INTEGER NOT NULL,
  PRIMARY KEY (ID),
  CONSTRAINT fk_AP_USER_SUBSCRIPTION_AP_APP_RELEASE1
  FOREIGN KEY (AP_APP_RELEASE_ID)
  REFERENCES AP_APP_RELEASE (ID) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_AP_USER_SUBSCRIPTION_AP_APP_RELEASE1_idx ON AP_USER_SUBSCRIPTION (AP_APP_RELEASE_ID ASC);

-- -----------------------------------------------------
-- Table AP_APP_CATEGORY
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS AP_APP_CATEGORY(
  ID INTEGER NOT NULL AUTO_INCREMENT,
  TENANT_ID INTEGER NOT NULL,
  CATEGORY VARCHAR(45) NOT NULL,
  CATEGORY_ICON VARCHAR(45) NULL,
  PRIMARY KEY (ID)
);

-- -----------------------------------------------------
-- Table AP_APP_TAG_MAPPING
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS AP_APP_TAG_MAPPING(
  ID INTEGER NOT NULL AUTO_INCREMENT,
  TENANT_ID INTEGER NOT NULL,
  AP_APP_TAG_ID INTEGER NOT NULL,
  AP_APP_ID INTEGER NOT NULL,
  PRIMARY KEY (ID),
  CONSTRAINT fk_AP_APP_TAG_copy1_AP_APP_TAG1
  FOREIGN KEY (AP_APP_TAG_ID)
  REFERENCES AP_APP_TAG (ID) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_AP_APP_TAG_copy1_AP_APP1
  FOREIGN KEY (AP_APP_ID)
  REFERENCES AP_APP (ID) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_AP_APP_TAG_copy1_AP_APP_TAG1_idx ON AP_APP_TAG_MAPPING (AP_APP_TAG_ID ASC);
CREATE INDEX fk_AP_APP_TAG_copy1_AP_APP1_idx ON AP_APP_TAG_MAPPING (AP_APP_ID ASC);

-- -----------------------------------------------------
-- Table AP_APP_CATEGORY_MAPPING
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS AP_APP_CATEGORY_MAPPING(
  ID INTEGER NOT NULL AUTO_INCREMENT,
  TENANT_ID INTEGER NOT NULL,
  AP_APP_CATEGORY_ID INTEGER NOT NULL,
  AP_APP_ID INTEGER NOT NULL,
  PRIMARY KEY (ID),
  CONSTRAINT fk_AP_APP_CATEGORY_copy1_AP_APP_CATEGORY1
  FOREIGN KEY (AP_APP_CATEGORY_ID)
  REFERENCES AP_APP_CATEGORY (ID) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT fk_AP_APP_CATEGORY_copy1_AP_APP1
  FOREIGN KEY (AP_APP_ID)
  REFERENCES AP_APP (ID) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_AP_APP_CATEGORY_copy1_AP_APP_CATEGORY1_idx ON AP_APP_CATEGORY_MAPPING (AP_APP_CATEGORY_ID ASC);
CREATE INDEX fk_AP_APP_CATEGORY_copy1_AP_APP1_idx ON AP_APP_CATEGORY_MAPPING (AP_APP_ID ASC);

-- -----------------------------------------------------
-- Table AP_APP_SUB_OP_MAPPING
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS AP_APP_SUB_OP_MAPPING (
  ID INTEGER NOT NULL AUTO_INCREMENT,
  TENANT_ID INTEGER NOT NULL,
  OPERATION_ID INTEGER NOT NULL,
  AP_DEVICE_SUBSCRIPTION_ID INTEGER NOT NULL,
  PRIMARY KEY (ID),
  CONSTRAINT fk_AP_APP_SUB_OP_MAPPING_AP_DEVICE_SUBSCRIPTION1
  FOREIGN KEY (AP_DEVICE_SUBSCRIPTION_ID)
  REFERENCES AP_DEVICE_SUBSCRIPTION (ID) ON DELETE NO ACTION ON UPDATE NO ACTION
);
CREATE INDEX fk_AP_APP_SUB_OP_MAPPING_AP_DEVICE_SUBSCRIPTION1_idx ON AP_APP_SUB_OP_MAPPING (AP_DEVICE_SUBSCRIPTION_ID ASC);

-- -----------------------------------------------------
-- Table AP_SCHEDULED_SUBSCRIPTION
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS AP_SCHEDULED_SUBSCRIPTION(
  ID INTEGER NOT NULL AUTO_INCREMENT,
  TASK_NAME VARCHAR(100) NOT NULL,
  APPLICATION_UUID VARCHAR(36) NOT NULL,
  SUBSCRIBER_LIST LONGVARCHAR NOT NULL,
  STATUS VARCHAR(15) NOT NULL,
  SCHEDULED_AT BIGINT NOT NULL,
  SCHEDULED_BY VARCHAR(100) NOT NULL,
  SCHEDULED_TIMESTAMP TIMESTAMP NOT NULL,
  DELETED BOOLEAN,
  PRIMARY KEY (ID),
  CONSTRAINT fk_AP_SCHEDULED_SUBSCRIPTION_AP_APP_RELEASE
  FOREIGN KEY (APPLICATION_UUID)
  REFERENCES AP_APP_RELEASE (UUID) ON DELETE NO ACTION ON UPDATE NO ACTION
);

-- -----------------------------------------------------
-- Table AP_IDENTITY_SERVER
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS AP_IDENTITY_SERVER (
  ID INTEGER NOT NULL AUTO_INCREMENT,
  NAME VARCHAR(255) NOT NULL,
  PROVIDER_NAME VARCHAR(255) NOT NULL,
  DESCRIPTION VARCHAR(255) NOT NULL,
  URL VARCHAR(255) NOT NULL,
  API_PARAMS VARCHAR(255) NOT NULL,
  USERNAME VARCHAR(255) NOT NULL,
  PASSWORD VARCHAR(255) NOT NULL,
  TENANT_ID INT NOT NULL,
  PRIMARY KEY(ID)
);

-- -----------------------------------------------------
-- Table AP_IS_SP_APP_MAPPING
-- -----------------------------------------------------;
CREATE TABLE IF NOT EXISTS AP_IS_SP_APP_MAPPING (
  ID INTEGER NOT NULL AUTO_INCREMENT,
  SP_UID VARCHAR(255) NOT NULL,
  AP_APP_ID INT NOT NULL,
  IS_ID INT NOT NULL,
  TENANT_ID INT NOT NULL,
  PRIMARY KEY(ID),
  CONSTRAINT AP_IS_SP_APP_MAPPING_AP_APP_ID_fk FOREIGN KEY (AP_APP_ID) REFERENCES AP_APP (ID)
  ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT AP_IS_SP_APP_MAPPING_AP_IDENTITY_SERVER_ID_fk FOREIGN KEY (IS_ID) REFERENCES AP_IDENTITY_SERVER (ID)
  ON DELETE NO ACTION ON UPDATE NO ACTION
);

-- -----------------------------------------------------
-- Table AP_APP_FAVOURITES
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS AP_APP_FAVOURITES(
    ID INTEGER NOT NULL AUTO_INCREMENT,
    AP_APP_ID INTEGER NOT NULL,
    USER_NAME VARCHAR(100) NOT NULL,
    TENANT_ID INTEGER NOT NULL,
    PRIMARY KEY(ID),
    CONSTRAINT AP_APP_FAVOURITES_AP_APP_ID_fk
    FOREIGN KEY (AP_APP_ID)
    REFERENCES AP_APP (ID) ON DELETE NO ACTION ON UPDATE NO ACTION
);

-- -----------------------------------------------------
-- Table AP_VPP_USER
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS AP_VPP_USER (
    ID INT NOT NULL AUTO_INCREMENT,
    CLIENT_USER_ID VARCHAR(255) NULL,
    DM_USERNAME VARCHAR(255) NOT NULL,
    TENANT_ID INT NOT NULL,
    EMAIL VARCHAR(255) NULL,
    INVITE_CODE VARCHAR(255) NULL,
    STATUS VARCHAR(255) NULL,
    CREATED_TIME BIGINT NULL,
    LAST_UPDATED_TIME BIGINT NULL,
    MANAGED_ID VARCHAR(255) NULL,
    TEMP_PASSWORD VARCHAR(255) NULL,
    PRIMARY KEY (ID)
);

-- -----------------------------------------------------
-- Table AP_ASSETS
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS AP_ASSETS (
    ID INT NOT NULL AUTO_INCREMENT,
    APP_ID INT NULL,
    TENANT_ID INT NOT NULL,
    CREATED_TIME BIGINT NULL,
    LAST_UPDATED_TIME BIGINT NULL,
    ADAM_ID VARCHAR(255) NULL,
    ASSIGNED_COUNT VARCHAR(255) NULL,
    DEVICE_ASSIGNABLE VARCHAR(255) NULL,
    PRICING_PARAMS VARCHAR(255) NULL,
    PRODUCT_TYPE VARCHAR(255) NULL,
    RETIRED_COUNT VARCHAR(255) NULL,
    REVOCABLE VARCHAR(255) NULL,
    SUPPORTED_PLATFORMS VARCHAR(255) NULL,
    PRIMARY KEY (ID)
);

-- -----------------------------------------------------
-- Table AP_VPP_ASSOCIATION
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS AP_VPP_ASSOCIATION (
    ID INT NOT NULL AUTO_INCREMENT,
    ASSET_ID INT,
    USER_ID INT,
    TENANT_ID INT NOT NULL,
    ASSOCIATION_TYPE VARCHAR(255) NOT NULL,
    PRICING_PARAMS VARCHAR(255) NULL,
    CREATED_TIME BIGINT NULL,
    LAST_UPDATED_TIME BIGINT NULL,
    PRIMARY KEY (ID),
    CONSTRAINT AP_VPP_ASSETS_fk FOREIGN KEY (ASSET_ID) REFERENCES AP_ASSETS (ID) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT AP_VPP_VPP_USER_fk FOREIGN KEY (USER_ID) REFERENCES AP_VPP_USER (ID) ON DELETE CASCADE ON UPDATE CASCADE
);
