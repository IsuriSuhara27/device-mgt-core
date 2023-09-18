 /*
 * Copyright (c) 2018 - 2023, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */ -- -----------------------------------------------------
 -- Table `DM_DEVICE_ORGANIZATION`
 -- -----------------------------------------------------

 CREATE TABLE IF NOT EXISTS DM_DEVICE_ORGANIZATION (
     ID                     INTEGER auto_increment NOT NULL,
     DEVICE_ID              INT(11) DEFAULT NULL,
     PARENT_DEVICE_ID       INT(11) DEFAULT NULL,
     LAST_UPDATED_TIMESTAMP TIMESTAMP NOT NULL,
     PRIMARY KEY (ID),
     CONSTRAINT fk_DM_DEVICE_DM_ID FOREIGN KEY (DEVICE_ID)
     REFERENCES DM_DEVICE (ID) ON DELETE NO ACTION ON UPDATE NO ACTION
     CONSTRAINT fk_DM_DEVICE_DM_ID2 FOREIGN KEY (PARENT_DEVICE_ID)
     REFERENCES DM_DEVICE (ID) ON DELETE NO ACTION ON UPDATE NO ACTION
     );

-- -----------------------------------------------------
-- Sample data for test cases
-- -----------------------------------------------------
INSERT INTO DM_DEVICE_TYPE ()
INSERT INTO DM_DEVICE ()
INSERT INTO DM_DEVICE_ORGANIZATION (DEVICE_ID, PARENT_DEVICE_ID, LAST_UPDATED_TIMESTAMP) VALUES
(),
();

