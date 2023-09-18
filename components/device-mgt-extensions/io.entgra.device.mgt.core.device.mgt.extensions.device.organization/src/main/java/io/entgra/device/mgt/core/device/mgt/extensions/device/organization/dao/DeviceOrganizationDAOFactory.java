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
 */
package io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao;

import io.entgra.device.mgt.core.device.mgt.core.config.datasource.DataSourceConfig;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.impl.DeviceOrganizationDAOImpl;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.util.ConnectionManagerUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DeviceOrganizationDAOFactory {
    private static final Log log = LogFactory.getLog(DeviceOrganizationDAOFactory.class);
    private static String databaseEngine;

    public static void init(DataSourceConfig dataSourceConfiguration) {
        if (log.isDebugEnabled()) {
            log.debug("Initializing Device Organization Data Source");
        }
        ConnectionManagerUtil.resolveDataSource(dataSourceConfiguration);
        databaseEngine = ConnectionManagerUtil.getDatabaseType();
    }

    public static DeviceOrganizationDAO getDeviceOrganizationDAO() {
        if (databaseEngine != null) {
            //noinspection SwitchStatementWithTooFewBranches
            switch (databaseEngine) {
                default:
                    return new DeviceOrganizationDAOImpl();
            }
        }
        throw new IllegalStateException("Database engine has not initialized properly.");
    }
}
