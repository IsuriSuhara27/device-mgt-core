/*
 * Copyright (c) 2020, Entgra Pvt Ltd. (http://www.wso2.org) All Rights Reserved.
 *
 * Entgra Pvt Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.entgra.server.bootup.heartbeat.beacon.dao;

import io.entgra.server.bootup.heartbeat.beacon.HeartBeatBeaconUtils;
import io.entgra.server.bootup.heartbeat.beacon.config.datasource.DataSourceConfig;
import io.entgra.server.bootup.heartbeat.beacon.config.datasource.JNDILookupDefinition;
import io.entgra.server.bootup.heartbeat.beacon.dao.impl.GenericHeartBeatDAOImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.exceptions.IllegalTransactionStateException;
import org.wso2.carbon.device.mgt.common.exceptions.TransactionManagementException;
import org.wso2.carbon.device.mgt.common.exceptions.UnsupportedDatabaseEngineException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;

/**
 * This class represents factory for group management data operations
 */
public class HeartBeatBeaconDAOFactory {

    private static final Log log = LogFactory.getLog(HeartBeatBeaconDAOFactory.class);
    private static DataSource dataSource;
    private static ThreadLocal<Connection> currentConnection = new ThreadLocal<>();

    /**
     * Get instance of GroupDAO
     *
     * @return instance of GroupDAO implementation
     */
    public static HeartBeatDAO getHeartBeatDAO() {
        return new GenericHeartBeatDAOImpl();
    }

    public static void init(DataSourceConfig config) {
        dataSource = resolveDataSource(config);
    }

    public static void init(DataSource dtSource) {
        dataSource = dtSource;
    }

    /**
     * Begin transaction with datasource for write data
     *
     * @throws TransactionManagementException
     */
    public static void beginTransaction() throws TransactionManagementException {
        Connection conn = currentConnection.get();
        if (conn != null) {
            throw new IllegalTransactionStateException("A transaction is already active within the context of " +
                                                       "this particular thread. Therefore, calling 'beginTransaction/openConnection' while another " +
                                                       "transaction is already active is a sign of improper transaction handling");
        }
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            currentConnection.set(conn);
        } catch (SQLException e) {
            throw new TransactionManagementException("Error occurred while retrieving config.datasource connection", e);
        }
    }

    /**
     * Open connection to the datasource for read data
     *
     * @throws SQLException
     */
    public static void openConnection() throws SQLException {
        Connection conn = currentConnection.get();
        if (conn != null) {
            throw new IllegalTransactionStateException("A transaction is already active within the context of " +
                                                       "this particular thread. Therefore, calling 'beginTransaction/openConnection' while another " +
                                                       "transaction is already active is a sign of improper transaction handling");
        }
        conn = dataSource.getConnection();
        currentConnection.set(conn);
    }

    /**
     * Get current connection to datasource
     *
     * @return current connection
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = currentConnection.get();
        if (conn == null) {
            throw new IllegalTransactionStateException("No connection is associated with the current transaction. " +
                                                       "This might have ideally been caused by not properly initiating the transaction via " +
                                                       "'beginTransaction'/'openConnection' methods");
        }
        return conn;
    }

    /**
     * Commit current transaction to the datasource
     */
    public static void commitTransaction() {
        Connection conn = currentConnection.get();
        if (conn == null) {
            throw new IllegalTransactionStateException("No connection is associated with the current transaction. " +
                                                       "This might have ideally been caused by not properly initiating " +
                    "the transaction via 'beginTransaction'/'openConnection' methods");
        }
        try {
            conn.commit();
        } catch (SQLException e) {
            log.error("Error occurred while committing the transaction", e);
        }
    }

    /**
     * Rollback current transaction on failure
     */
    public static void rollbackTransaction() {
        Connection conn = currentConnection.get();
        if (conn == null) {
            throw new IllegalTransactionStateException("No connection is associated with the current transaction. " +
                                                       "This might have ideally been caused by not properly initiating " +
                    "the transaction via 'beginTransaction'/'openConnection' methods");
        }
        try {
            conn.rollback();
        } catch (SQLException e) {
            log.warn("Error occurred while roll-backing the transaction", e);
        }
    }

    /**
     * Close data connection associated with current transaction
     */
    public static void closeConnection() {
        Connection conn = currentConnection.get();
        if (conn == null) {
            throw new IllegalTransactionStateException("No connection is associated with the current transaction. " +
                                                       "This might have ideally been caused by not properly " +
                                                       "initiating the transaction via " +
                                                       "'beginTransaction'/'openConnection' methods");
        }
        try {
            conn.close();
        } catch (SQLException e) {
            log.warn("Error occurred while close the connection");
        }
        currentConnection.remove();
    }

    /**
     * Resolve data source from the data source definition
     *
     * @param config data source configuration
     * @return data source resolved from the data source definition
     */
    private static DataSource resolveDataSource(DataSourceConfig config) {
        DataSource dataSource = null;
        if (config == null) {
            throw new RuntimeException(
                    "Device Management Repository data source configuration " + "is null and " +
                    "thus, is not initialized");
        }
        JNDILookupDefinition jndiConfig = config.getJndiLookupDefinition();
        if (jndiConfig != null) {
            if (log.isDebugEnabled()) {
                log.debug("Initializing Device Management Repository data source using the JNDI " +
                          "Lookup Definition");
            }
            List<JNDILookupDefinition.JNDIProperty> jndiPropertyList =
                    jndiConfig.getJndiProperties();
            if (jndiPropertyList != null) {
                Hashtable<Object, Object> jndiProperties = new Hashtable<Object, Object>();
                for (JNDILookupDefinition.JNDIProperty prop : jndiPropertyList) {
                    jndiProperties.put(prop.getName(), prop.getValue());
                }
                dataSource = HeartBeatBeaconUtils.lookupDataSource(jndiConfig.getJndiName(), jndiProperties);
            } else {
                dataSource = HeartBeatBeaconUtils.lookupDataSource(jndiConfig.getJndiName(), null);
            }
        }
        return dataSource;
    }

}
