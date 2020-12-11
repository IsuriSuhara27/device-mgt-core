-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema WSO2CDM
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema WSO2CDM
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `WSO2CDM` DEFAULT CHARACTER SET latin1 ;
USE `WSO2CDM` ;

-- -----------------------------------------------------
-- Table `WSO2CDM`.`DM_DEVICE_TYPE`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `WSO2CDM`.`DM_DEVICE_TYPE` ;

CREATE TABLE IF NOT EXISTS `WSO2CDM`.`DM_DEVICE_TYPE` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `NAME` VARCHAR(300) NULL DEFAULT NULL,
  `PROVIDER_TENANT_ID` INTEGER DEFAULT 0,
  `SHARED_WITH_ALL_TENANTS` BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WSO2CDM`.`DM_PROFILE`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `WSO2CDM`.`DM_PROFILE` ;

CREATE TABLE IF NOT EXISTS `WSO2CDM`.`DM_PROFILE` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `PROFILE_NAME` VARCHAR(45) NOT NULL,
  `TENANT_ID` INT(11) NOT NULL,
  `DEVICE_TYPE_ID` INT(11) NOT NULL,
  `CREATED_TIME` DATETIME NOT NULL,
  `UPDATED_TIME` DATETIME NOT NULL,
  PRIMARY KEY (`ID`),
  INDEX `DM_PROFILE_DEVICE_TYPE` (`DEVICE_TYPE_ID` ASC),
  CONSTRAINT `DM_PROFILE_DEVICE_TYPE`
    FOREIGN KEY (`DEVICE_TYPE_ID`)
    REFERENCES `WSO2CDM`.`DM_DEVICE_TYPE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WSO2CDM`.`DM_POLICY`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `WSO2CDM`.`DM_POLICY` ;

CREATE TABLE IF NOT EXISTS `WSO2CDM`.`DM_POLICY` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `NAME` VARCHAR(45) NULL DEFAULT NULL,
  `PAYLOAD_VERSION` VARCHAR(45) NULL DEFAULT NULL,
  `TENANT_ID` INT(11) NOT NULL,
  `PROFILE_ID` INT(11) NOT NULL,
  `COMPLIANCE` VARCHAR(100) NULL,
  `PRIORITY` INT(11) NOT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_DM_PROFILE_DM_POLICY` (`PROFILE_ID` ASC),
  CONSTRAINT `FK_DM_PROFILE_DM_POLICY`
    FOREIGN KEY (`PROFILE_ID`)
    REFERENCES `WSO2CDM`.`DM_PROFILE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WSO2CDM`.`DM_DATE`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `WSO2CDM`.`DM_DATE` ;

CREATE TABLE IF NOT EXISTS `WSO2CDM`.`DM_DATE` (
  `START_DATE` DATE NOT NULL,
  `END_DATE` DATE NOT NULL,
  `POLICY_ID` INT(11) NOT NULL,
  INDEX `DM_DATE_POLICY` (`POLICY_ID` ASC),
  CONSTRAINT `DM_DATE_POLICY`
    FOREIGN KEY (`POLICY_ID`)
    REFERENCES `WSO2CDM`.`DM_POLICY` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WSO2CDM`.`DM_DEVICE`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `WSO2CDM`.`DM_DEVICE` ;

CREATE TABLE IF NOT EXISTS `WSO2CDM`.`DM_DEVICE` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `DESCRIPTION` TEXT NULL DEFAULT NULL,
  `NAME` VARCHAR(100) NULL DEFAULT NULL,
  `DATE_OF_ENROLLMENT` BIGINT(20) NULL DEFAULT NULL,
  `DATE_OF_LAST_UPDATE` BIGINT(20) NULL DEFAULT NULL,
  `OWNERSHIP` VARCHAR(45) NULL DEFAULT NULL,
  `STATUS` VARCHAR(15) NULL DEFAULT NULL,
  `DEVICE_TYPE_ID` INT(11) NULL DEFAULT NULL,
  `DEVICE_IDENTIFICATION` VARCHAR(300) NULL DEFAULT NULL,
  `OWNER` VARCHAR(45) NULL DEFAULT NULL,
  `TENANT_ID` INT(11) NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  INDEX `fk_DM_DEVICE_DM_DEVICE_TYPE2` (`DEVICE_TYPE_ID` ASC),
  CONSTRAINT `fk_DM_DEVICE_DM_DEVICE_TYPE2`
    FOREIGN KEY (`DEVICE_TYPE_ID`)
    REFERENCES `WSO2CDM`.`DM_DEVICE_TYPE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WSO2CDM`.`DM_DEVICE_POLICY`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `WSO2CDM`.`DM_DEVICE_POLICY` ;

CREATE TABLE IF NOT EXISTS `WSO2CDM`.`DM_DEVICE_POLICY` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `DEVICE_ID` INT(11) NOT NULL,
  `POLICY_ID` INT(11) NOT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_POLICY_DEVICE_POLICY` (`POLICY_ID` ASC),
  INDEX `FK_DEVICE_DEVICE_POLICY` (`DEVICE_ID` ASC),
  CONSTRAINT `FK_DEVICE_DEVICE_POLICY`
    FOREIGN KEY (`DEVICE_ID`)
    REFERENCES `WSO2CDM`.`DM_DEVICE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `FK_POLICY_DEVICE_POLICY`
    FOREIGN KEY (`POLICY_ID`)
    REFERENCES `WSO2CDM`.`DM_POLICY` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WSO2CDM`.`DM_DEVICE_POLICY_APPLIED`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `WSO2CDM`.`DM_DEVICE_POLICY_APPLIED` ;

CREATE TABLE IF NOT EXISTS `WSO2CDM`.`DM_DEVICE_POLICY_APPLIED` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `DEVICE_ID` INT(11) NOT NULL,
  `POLICY_ID` INT(11) NOT NULL,
  `POLICY_CONTENT` BLOB NULL DEFAULT NULL,
  `APPLIED` TINYINT(1) NULL DEFAULT NULL,
  `CREATED_TIME` TIMESTAMP NULL DEFAULT NULL,
  `UPDATED_TIME` TIMESTAMP NULL DEFAULT NULL,
  `APPLIED_TIME` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_DM_POLICY_DEVCIE_APPLIED` (`DEVICE_ID` ASC),
  INDEX `FK_DM_POLICY_DEVICE_APPLIED_POLICY` (`POLICY_ID` ASC),
  CONSTRAINT `FK_DM_POLICY_DEVCIE_APPLIED`
    FOREIGN KEY (`DEVICE_ID`)
    REFERENCES `WSO2CDM`.`DM_DEVICE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `FK_DM_POLICY_DEVICE_APPLIED_POLICY`
    FOREIGN KEY (`POLICY_ID`)
    REFERENCES `WSO2CDM`.`DM_POLICY` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;



-- -----------------------------------------------------
-- Table `WSO2CDM`.`DM_PROFILE_FEATURES`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `WSO2CDM`.`DM_PROFILE_FEATURES` ;

CREATE TABLE IF NOT EXISTS `WSO2CDM`.`DM_PROFILE_FEATURES` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `PROFILE_ID` INT(11) NOT NULL,
  `FEATURE_CODE` VARCHAR(10) NOT NULL,
  `DEVICE_TYPE_ID` INT NOT NULL,
  `CONTENT` BLOB NULL DEFAULT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_DM_PROFILE_DM_POLICY_FEATURES` (`PROFILE_ID` ASC),
  CONSTRAINT `FK_DM_PROFILE_DM_POLICY_FEATURES`
    FOREIGN KEY (`PROFILE_ID`)
    REFERENCES `WSO2CDM`.`DM_PROFILE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;

-- -----------------------------------------------------
-- Table `WSO2CDM`.`DM_POLICY_CORRECTIVE_ACTION`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `WSO2CDM`.`DM_POLICY_CORRECTIVE_ACTION` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `ACTION_TYPE` VARCHAR(45) NOT NULL,
  `CORRECTIVE_POLICY_ID` INT(11) DEFAULT NULL,
  `POLICY_ID` INT(11) NOT NULL,
  `FEATURE_ID` INT(11) DEFAULT NULL,
  `IS_REACTIVE` BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (ID),
  CONSTRAINT FK_DM_POLICY_DM_POLICY_CORRECTIVE_ACTION
    FOREIGN KEY (POLICY_ID)
    REFERENCES DM_POLICY (ID)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;

-- -----------------------------------------------------
-- Table `WSO2CDM`.`DM_ROLE_POLICY`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `WSO2CDM`.`DM_ROLE_POLICY` ;

CREATE TABLE IF NOT EXISTS `WSO2CDM`.`DM_ROLE_POLICY` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `ROLE_NAME` VARCHAR(45) NOT NULL,
  `POLICY_ID` INT(11) NOT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_ROLE_POLICY_POLICY` (`POLICY_ID` ASC),
  CONSTRAINT `FK_ROLE_POLICY_POLICY`
    FOREIGN KEY (`POLICY_ID`)
    REFERENCES `WSO2CDM`.`DM_POLICY` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;



-- -----------------------------------------------------
-- Table `WSO2CDM`.`DM_USER_POLICY`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `WSO2CDM`.`DM_USER_POLICY` ;

CREATE TABLE IF NOT EXISTS `WSO2CDM`.`DM_USER_POLICY` (
  `ID` INT(11) NOT NULL AUTO_INCREMENT,
  `POLICY_ID` INT(11) NOT NULL,
  `USERNAME` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`ID`),
  INDEX `DM_POLICY_USER_POLICY` (`POLICY_ID` ASC),
  CONSTRAINT `DM_POLICY_USER_POLICY`
    FOREIGN KEY (`POLICY_ID`)
    REFERENCES `WSO2CDM`.`DM_POLICY` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `WSO2CDM`.`DM_CRITERIA`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `WSO2CDM`.`DM_CRITERIA` ;

CREATE TABLE IF NOT EXISTS `WSO2CDM`.`DM_CRITERIA` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `TENANT_ID` INT NOT NULL,
  `NAME` VARCHAR(50) NULL,
  PRIMARY KEY (`ID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `WSO2CDM`.`DM_POLICY_CRITERIA`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `WSO2CDM`.`DM_POLICY_CRITERIA` ;

CREATE TABLE IF NOT EXISTS `WSO2CDM`.`DM_POLICY_CRITERIA` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `CRITERIA_ID` INT NOT NULL,
  `POLICY_ID` INT NOT NULL,
  PRIMARY KEY (`ID`),
  INDEX `FK_CRITERIA_POLICY_CRITERIA_idx` (`CRITERIA_ID` ASC),
  INDEX `FK_POLICY_POLICY_CRITERIA_idx` (`POLICY_ID` ASC),
  CONSTRAINT `FK_CRITERIA_POLICY_CRITERIA`
    FOREIGN KEY (`CRITERIA_ID`)
    REFERENCES `WSO2CDM`.`DM_CRITERIA` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `FK_POLICY_POLICY_CRITERIA`
    FOREIGN KEY (`POLICY_ID`)
    REFERENCES `WSO2CDM`.`DM_POLICY` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `WSO2CDM`.`DM_POLICY_CRITERIA_PROPERTIES`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `WSO2CDM`.`DM_POLICY_CRITERIA_PROPERTIES` ;

CREATE TABLE IF NOT EXISTS `WSO2CDM`.`DM_POLICY_CRITERIA_PROPERTIES` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `POLICY_CRITERION_ID` INT NOT NULL,
  `PROP_KEY` VARCHAR(45) NULL,
  `PROP_VALUE` VARCHAR(100) NULL,
  `CONTENT` BLOB NULL COMMENT 'This is used to ',
  PRIMARY KEY (`ID`),
  INDEX `FK_POLICY_CRITERIA_PROPERTIES_idx` (`POLICY_CRITERION_ID` ASC),
  CONSTRAINT `FK_POLICY_CRITERIA_PROPERTIES`
    FOREIGN KEY (`POLICY_CRITERION_ID`)
    REFERENCES `WSO2CDM`.`DM_POLICY_CRITERIA` (`ID`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
