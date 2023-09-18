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
package io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.util;

import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.DBConnectionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.IllegalTransactionStateException;
import io.entgra.device.mgt.core.device.mgt.core.config.datasource.DataSourceConfig;
import io.entgra.device.mgt.core.device.mgt.core.config.datasource.JNDILookupDefinition;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;

public class ConnectionManagerUtil {

    private static final Log log = LogFactory.getLog(ConnectionManagerUtil.class);
    private static final ThreadLocal<Connection> currentConnection = new ThreadLocal<>();
    private static DataSource dataSource;

    public static void openDBConnection() throws DBConnectionException {
        Connection conn = currentConnection.get();
        if (conn != null) {
            throw new IllegalTransactionStateException("Database connection has already been obtained.");
        }
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            throw new DBConnectionException("Failed to get a database connection.", e);
        }
        currentConnection.set(conn);
    }

    public static Connection getDBConnection() throws DBConnectionException {
        Connection conn = currentConnection.get();
        if (conn == null) {
            try {
                conn = dataSource.getConnection();
                currentConnection.set(conn);
            } catch (SQLException e) {
                throw new DBConnectionException("Failed to get database connection.", e);
            }
        }
        return conn;
    }

    public static void beginDBTransaction() throws DBConnectionException {
        Connection conn = currentConnection.get();
        if (conn == null) {
            conn = getDBConnection();
        } else if (inTransaction(conn)) {
            throw new IllegalTransactionStateException("Transaction has already been started.");
        }

        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DBConnectionException("Error occurred while starting a database transaction.", e);
        }
    }

    public static void endDBTransaction() throws DBConnectionException {
        Connection conn = currentConnection.get();
        if (conn == null) {
            throw new IllegalTransactionStateException("Database connection is not active.");
        }

        if (!inTransaction(conn)) {
            throw new IllegalTransactionStateException("Transaction has not been started.");
        }

        try {
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new DBConnectionException("Error occurred while ending database transaction.", e);
        }
    }

    public static void commitDBTransaction() {
        Connection conn = currentConnection.get();
        if (conn == null) {
            throw new IllegalTransactionStateException("Database connection is not active.");
        }

        if (!inTransaction(conn)) {
            throw new IllegalTransactionStateException("Transaction has not been started.");
        }

        try {
            conn.commit();
        } catch (SQLException e) {
            log.error("Error occurred while committing the transaction", e);
        }
    }

    public static void rollbackDBTransaction() {
        Connection conn = currentConnection.get();
        if (conn == null) {
            throw new IllegalTransactionStateException("Database connection is not active.");
        }

        if (!inTransaction(conn)) {
            throw new IllegalTransactionStateException("Transaction has not been started.");
        }

        try {
            conn.rollback();
        } catch (SQLException e) {
            log.warn("Error occurred while roll-backing the transaction", e);
        }
    }

    public static void closeDBConnection() {
        Connection conn = currentConnection.get();
        if (conn == null) {
            throw new IllegalTransactionStateException("Database connection is not active.");
        }
        try {
            conn.close();
        } catch (SQLException e) {
            log.error("Error occurred while closing the connection", e);
        }
        currentConnection.remove();
    }

    private static boolean inTransaction(Connection conn) {
        boolean inTransaction = true;
        try {
            if (conn.getAutoCommit()) {
                inTransaction = false;
            }
        } catch (SQLException e) {
            throw new IllegalTransactionStateException("Failed to get transaction state.");
        }
        return inTransaction;
    }

    public static boolean isTransactionStarted() throws DBConnectionException {
        Connection connection = getDBConnection();
        return inTransaction(connection);
    }

    /**
     * Resolve data source from the data source definition.
     *
     * @param config Data source configuration
     * @return data source resolved from the data source definition
     */
    public static DataSource resolveDataSource(DataSourceConfig config) {
        if (config == null) {
            throw new RuntimeException("Device Management Repository data source configuration " +
                    "is null and thus, is not initialized");
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
                Hashtable<Object, Object> jndiProperties = new Hashtable<>();
                for (JNDILookupDefinition.JNDIProperty prop : jndiPropertyList) {
                    jndiProperties.put(prop.getName(), prop.getValue());
                }
                dataSource = ConnectionManagerUtil.lookupDataSource(jndiConfig.getJndiName(), jndiProperties);
            } else {
                dataSource = ConnectionManagerUtil.lookupDataSource(jndiConfig.getJndiName(), null);
            }
        }
        return dataSource;
    }

    public static DataSource lookupDataSource(String dataSourceName,
                                              final Hashtable<Object, Object> jndiProperties) {

        try {
            if (jndiProperties == null || jndiProperties.isEmpty()) {
                return (DataSource) InitialContext.doLookup(dataSourceName);
            }
            final InitialContext context = new InitialContext(jndiProperties);
            return (DataSource) context.lookup(dataSourceName);
        } catch (Exception e) {
            String msg = "Error in looking up data source: " + e.getMessage();
            log.error(msg, e);
            throw new RuntimeException(msg + e.getMessage(), e);
        }
    }

    public static String getDatabaseType() {
        try {
            return dataSource.getConnection().getMetaData().getDatabaseProductName();
        } catch (SQLException e) {
            log.error("Error occurred while retrieving config.datasource connection", e);
        }
        return null;
    }
}
