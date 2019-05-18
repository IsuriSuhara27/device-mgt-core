/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.device.application.mgt.core.dao.impl.subscription;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.application.mgt.common.dto.DeviceSubscriptionDTO;
import org.wso2.carbon.device.application.mgt.common.exception.DBConnectionException;
import org.wso2.carbon.device.application.mgt.core.dao.SubscriptionDAO;
import org.wso2.carbon.device.application.mgt.core.util.DAOUtil;
import org.wso2.carbon.device.application.mgt.core.dao.impl.AbstractDAOImpl;
import org.wso2.carbon.device.application.mgt.core.exception.ApplicationManagementDAOException;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.group.mgt.DeviceGroup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class GenericSubscriptionDAOImpl extends AbstractDAOImpl implements SubscriptionDAO {
    private static Log log = LogFactory.getLog(GenericSubscriptionDAOImpl.class);

    @Override
    public void subscribeDeviceToApplicationTmp(int tenantId, String subscribedBy, List<Device> deviceList, int appId,
            int releaseId, String installStatus) throws ApplicationManagementDAOException {
        Connection conn;
        PreparedStatement stmt = null;
        try {
            conn = this.getDBConnection();
            long time = System.currentTimeMillis() / 1000;
            String sql = "INSERT INTO AP_DEVICE_SUBSCRIPTION(TENANT_ID, SUBSCRIBED_BY, SUBSCRIBED_TIMESTAMP, "
                    + "DM_DEVICE_ID, AP_APP_RELEASE_ID, AP_APP_ID, INSTALL_STATUS) VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            for (Device device : deviceList) {
                stmt.setInt(1, tenantId);
                stmt.setString(2, subscribedBy);
                stmt.setLong(3, time);
                stmt.setInt(4, device.getId());
                stmt.setInt(5, releaseId);
                stmt.setInt(6, appId);
                stmt.setString(7, installStatus);
                stmt.addBatch();
                if (log.isDebugEnabled()) {
                    log.debug("Adding a mapping to device ID[" + device.getId() + "] to the application [" + appId
                            + "], release[" + releaseId + "]");
                }
            }
            stmt.executeBatch();
        } catch (SQLException | DBConnectionException e) {
            throw new ApplicationManagementDAOException("Error occurred while adding device application mapping to DB",
                    e);
        } finally {
            DAOUtil.cleanupResources(stmt, null);
        }
    }

    @Override
    public List<Integer> subscribeDeviceToApplication(String subscribedBy, List<Integer> deviceIds,
            String subscribedFrom, String installStatus, int releaseId, int tenantId)
            throws ApplicationManagementDAOException {
        Connection conn;
        try {
            String sql = "INSERT INTO "
                    + "AP_DEVICE_SUBSCRIPTION("
                    + "SUBSCRIBED_BY, "
                    + "SUBSCRIBED_TIMESTAMP, "
                    + "SUBSCRIBED_FROM, "
                    + "STATUS, "
                    + "DM_DEVICE_ID, "
                    + "AP_APP_RELEASE_ID,"
                    + "TENANT_ID) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            conn = this.getDBConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                Calendar calendar = Calendar.getInstance();
                Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
                for (Integer deviceId : deviceIds) {
                    stmt.setString(1, subscribedBy);
                    stmt.setTimestamp(2, timestamp);
                    stmt.setString(3, subscribedFrom);
                    stmt.setString(4, installStatus);
                    stmt.setInt(5, deviceId);
                    stmt.setInt(6, releaseId);
                    stmt.setInt(7, tenantId);
                    stmt.addBatch();
                    if (log.isDebugEnabled()) {
                        log.debug("Adding a mapping to device[" + deviceId + "] to the application release[" + releaseId
                                + "]");
                    }
                }
                stmt.executeBatch();
            }
        } catch (SQLException | DBConnectionException e) {
            throw new ApplicationManagementDAOException("Error occurred while adding device application mapping to DB",
                    e);
        }

        return deviceIds;
    }

    @Override
    public List<Integer> updateDeviceSubscription(String subscribedBy, List<Integer> deviceIds,
            String subscribedFrom, String installStatus, int releaseId, int tenantId) throws ApplicationManagementDAOException {

        Connection conn;
        try {
            conn = this.getDBConnection();
            String sql = "UPDATE AP_USER_SUBSCRIPTION "
                    + "SET "
                    + "SUBSCRIBED_BY = ?, "
                    + "SUBSCRIBED_TIMESTAMP = ?, "
                    + "SUBSCRIBED_FROM = ?, "
                    + "STATUS = ? "
                    + "WHERE "
                    + "DM_DEVICE_ID = ? AND "
                    + "AP_APP_RELEASE_ID = ? AND "
                    + "TENANT_ID = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                Calendar calendar = Calendar.getInstance();
                Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
                for (Integer deviceId : deviceIds) {
                    stmt.setString(1, subscribedBy);
                    stmt.setTimestamp(2, timestamp);
                    stmt.setString(3, subscribedFrom);
                    stmt.setString(4, installStatus);
                    stmt.setInt(5, deviceId);
                    stmt.setInt(6, releaseId);
                    stmt.setInt(7, tenantId);
                }
                stmt.executeBatch();
            }
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining the DB connection to update the device subscriptions of application.";
            log.error(msg);
            throw new ApplicationManagementDAOException(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred when obtaining database connection for updating the device subscriptions of application.";
            log.error(msg);
            throw new ApplicationManagementDAOException(msg, e);
        }


        return deviceIds;
    }

    @Override public void addOperationMapping(int operationId, List<Integer> deviceSubscriptionIds, int tenantId)
            throws ApplicationManagementDAOException {
        Connection conn;
        try {
            String sql = "INSERT INTO "
                    + "AP_APP_SUB_OP_MAPPING("
                    + "OPERATION_ID, "
                    + "AP_DEVICE_SUBSCRIPTION_ID, "
                    + "TENANT_ID) "
                    + "VALUES (?, ?, ?, ?, ?)";
            conn = this.getDBConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (Integer subId : deviceSubscriptionIds) {
                    stmt.setInt(1, operationId);
                    stmt.setInt(2, subId);
                    stmt.setInt(3, tenantId);
                    stmt.addBatch();
                    if (log.isDebugEnabled()) {
                        log.debug("Adding a operation mapping for subscription id " + subId);
                    }
                }
                stmt.executeBatch();
            }
        } catch (SQLException | DBConnectionException e) {
            throw new ApplicationManagementDAOException("Error occurred while adding operation subscription mapping to DB",
                    e);
        }

    }

    @Override
    public void subscribeUserToApplication(int tenantId, String subscribedBy, List<String> userList, int releaseId)
            throws ApplicationManagementDAOException {
        Connection conn;
        try {
            String sql = "INSERT INTO "
                    + "AP_USER_SUBSCRIPTION("
                    + "TENANT_ID, "
                    + "SUBSCRIBED_BY, "
                    + "SUBSCRIBED_TIMESTAMP, "
                    + "USER_NAME, "
                    + "AP_APP_RELEASE_ID) "
                    + "VALUES (?, ?, ?, ?, ?)";
            conn = this.getDBConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (String user : userList) {
                    Calendar calendar = Calendar.getInstance();
                    Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
                    stmt.setInt(1, tenantId);
                    stmt.setString(2, subscribedBy);
                    stmt.setTimestamp(3, timestamp);
                    stmt.setString(4, user);
                    stmt.setInt(5, releaseId);
                    stmt.addBatch();
                    if (log.isDebugEnabled()) {
                        log.debug("Adding a mapping to user[" + user + "] to the application release[" + releaseId + "]");
                    }
                }
                stmt.executeBatch();
            }
        } catch (SQLException | DBConnectionException e) {
            throw new ApplicationManagementDAOException("Error occurred while adding device application mapping to DB",
                    e);
        }
    }

    @Override
    public void subscribeRoleToApplication(int tenantId, String subscribedBy, List<String> roleList, int appId,
            int releaseId) throws ApplicationManagementDAOException {
        Connection conn;
        PreparedStatement stmt = null;
        try {
            conn = this.getDBConnection();
            long time = System.currentTimeMillis() / 1000;
            String sql = "INSERT INTO AP_ROLE_SUBSCRIPTION(TENANT_ID, SUBSCRIBED_BY, SUBSCRIBED_TIMESTAMP, "
                    + "ROLE_NAME, AP_APP_RELEASE_ID, AP_APP_ID) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            for (String role : roleList) {
                stmt.setInt(1, tenantId);
                stmt.setString(2, subscribedBy);
                stmt.setLong(3, time);
                stmt.setString(4, role);
                stmt.setInt(5, releaseId);
                stmt.setInt(6, appId);
                stmt.addBatch();
                if (log.isDebugEnabled()) {
                    log.debug("Adding a mapping to role[" + role + "] to the application [" + appId + "], release["
                            + releaseId + "]");
                }
            }
            stmt.executeBatch();
        } catch (SQLException | DBConnectionException e) {
            throw new ApplicationManagementDAOException("Error occurred while adding device application mapping to DB",
                    e);
        } finally {
            DAOUtil.cleanupResources(stmt, null);
        }
    }

    @Override
    public void subscribeGroupToApplication(int tenantId, String subscribedBy, List<DeviceGroup> groupList, int appId,
            int releaseId) throws ApplicationManagementDAOException {
        Connection conn;
        PreparedStatement stmt = null;
        try {
            conn = this.getDBConnection();
            long time = System.currentTimeMillis() / 1000;
            String sql = "INSERT INTO AP_GROUP_SUBSCRIPTION(TENANT_ID, SUBSCRIBED_BY, SUBSCRIBED_TIMESTAMP, "
                    + "DM_GROUP_ID, AP_APP_RELEASE_ID, AP_APP_ID) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            for (DeviceGroup group : groupList) {
                stmt.setInt(1, tenantId);
                stmt.setString(2, subscribedBy);
                stmt.setLong(3, time);
                stmt.setInt(4, group.getGroupId());
                stmt.setInt(5, releaseId);
                stmt.setInt(6, appId);
                stmt.addBatch();
                if (log.isDebugEnabled()) {
                    log.debug("Adding a mapping to group ID[" + group.getGroupId() + "] to the application [" + appId
                            + "], release[" + releaseId + "]");
                }
            }
            stmt.executeBatch();
        } catch (SQLException | DBConnectionException e) {
            throw new ApplicationManagementDAOException("Error occurred while adding device application mapping to DB",
                    e);
        } finally {
            DAOUtil.cleanupResources(stmt, null);
        }
    }

    @Override
    public List<DeviceSubscriptionDTO> getDeviceSubscriptions(int appReleaseId, int tenantId) throws
            ApplicationManagementDAOException {
        if (log.isDebugEnabled()) {
            log.debug("Getting device subscriptions for the application release id " + appReleaseId
                    + " from the database");
        }
        Connection conn;
        try {
            conn = this.getDBConnection();
            String sql = "SELECT "
                    + "DS.ID AS ID, "
                    + "DS.SUBSCRIBED_BY AS SUBSCRIBED_BY, "
                    + "DS.SUBSCRIBED_TIMESTAMP AS SUBSCRIBED_AT, "
                    + "DS.UNSUBSCRIBED AS IS_UNSUBSCRIBED, "
                    + "DS.UNSUBSCRIBED_BY AS UNSUBSCRIBED_BY, "
                    + "DS.UNSUBSCRIBED_TIMESTAMP AS UNSUBSCRIBED_AT, "
                    + "DS.SUBSCRIBED_FROM AS SUBSCRIBED_FROM, "
                    + "DS.DM_DEVICE_ID AS DEVICE_ID "
                    + "FROM AP_DEVICE_SUBSCRIPTION DS "
                    + "WHERE DS.AP_APP_RELEASE_ID = ? AND DS.TENANT_ID=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, appReleaseId);
                stmt.setInt(2, tenantId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Successfully retrieved device subscriptions for application release id "
                                + appReleaseId);
                    }
                    return DAOUtil.loadDeviceSubscriptions(rs);
                }
            }
        } catch (SQLException e) {
            String msg =
                    "Error occurred while getting device subscription data for application ID: " + appReleaseId + ".";
            log.error(msg);
            throw new ApplicationManagementDAOException(msg, e);
        } catch (DBConnectionException e) {
            String msg =
                    "Error occurred while obtaining the DB connection for getting device subscription for applicationID: "
                            + appReleaseId + ".";
            log.error(msg);
            throw new ApplicationManagementDAOException(msg, e);
        }
    }

    @Override
    public Map<Integer, DeviceSubscriptionDTO> getDeviceSubscriptions(List<Integer> deviceIds, int tenantId)
            throws ApplicationManagementDAOException {
        if (log.isDebugEnabled()) {
            log.debug("Request received in DAO Layer to get device subscriptions for given device ids.");
        }
        try {
            Connection conn = this.getDBConnection();
            int index = 1;
            Map<Integer, DeviceSubscriptionDTO> deviceSubscriptionDTOHashMap = new HashMap<>();
            StringJoiner joiner = new StringJoiner(",",
                    "SELECT "
                            + "DS.ID AS ID, "
                            + "DS.SUBSCRIBED_BY AS SUBSCRIBED_BY, "
                            + "DS.SUBSCRIBED_TIMESTAMP AS SUBSCRIBED_AT, "
                            + "DS.UNSUBSCRIBED AS IS_UNSUBSCRIBED, "
                            + "DS.UNSUBSCRIBED_BY AS UNSUBSCRIBED_BY, "
                            + "DS.UNSUBSCRIBED_TIMESTAMP AS UNSUBSCRIBED_AT, "
                            + "DS.SUBSCRIBED_FROM AS SUBSCRIBED_FROM, "
                            + "DS.DM_DEVICE_ID AS DEVICE_ID, "
                            + "DS.STATUS AS STATUS "
                            + "FROM AP_DEVICE_SUBSCRIPTION DS "
                            + "WHERE DS.DM_DEVICE_ID IN (", ") AND TENANT_ID = ?");
            deviceIds.stream().map(ignored -> "?").forEach(joiner::add);
            String query = joiner.toString();
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                for (Integer deviceId : deviceIds) {
                    ps.setObject(index++, deviceId);
                }
                ps.setInt(index, tenantId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        DeviceSubscriptionDTO deviceSubscriptionDTO = DAOUtil.constructDeviceSubscriptionDTO(rs);
                        deviceSubscriptionDTOHashMap.put(deviceSubscriptionDTO.getId(), deviceSubscriptionDTO);
                    }
                }
            }
            return deviceSubscriptionDTOHashMap;
        } catch (DBConnectionException e) {
            throw new ApplicationManagementDAOException(
                    "Error occurred while obtaining the DB connection when getting device subscriptions for given "
                            + "device Ids.", e);
        } catch (SQLException e) {
            throw new ApplicationManagementDAOException("SWL Error occurred while getting device subscriptions for given"
                    + " device Ids.", e);
        }
    }

    @Override
    public List<String> getSubscribedUsernames(List<String> users, int tenantId)
            throws ApplicationManagementDAOException {
        if (log.isDebugEnabled()) {
            log.debug("Request received in DAO Layer to get already subscribed users for given list of user names.");
        }
        try {
            Connection conn = this.getDBConnection();
            int index = 1;
            List<String> subscribedUsers = new ArrayList<>();
            StringJoiner joiner = new StringJoiner(",",
                    "SELECT US.USER_NAME AS USER "
                            + "FROM AP_USER_SUBSCRIPTION US "
                            + "WHERE US.USER_NAME IN (", ") AND TENANT_ID = ?");
            users.stream().map(ignored -> "?").forEach(joiner::add);
            String query = joiner.toString();
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                for (String username : users) {
                    ps.setObject(index++, username);
                }
                ps.setInt(index, tenantId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        subscribedUsers.add(rs.getString("USER"));
                    }
                }
            }
            return subscribedUsers;
        } catch (DBConnectionException e) {
            throw new ApplicationManagementDAOException(
                    "Error occurred while obtaining the DB connection when getting subscribed users for given "
                            + "user names.", e);
        } catch (SQLException e) {
            throw new ApplicationManagementDAOException("SWL Error occurred while getting suscribed users for given"
                    + " user names.", e);
        }
    }

    @Override public List<Integer> getSubscribedDeviceIds(List<Integer> deviceIds, int tenantId)
            throws ApplicationManagementDAOException {
        if (log.isDebugEnabled()) {
            log.debug("Request received to DAO Layer to get already subscribed dvice Ids for given list of device Ids.");
        }
        try {
            Connection conn = this.getDBConnection();
            int index = 1;
            List<Integer> subscribedDevices = new ArrayList<>();
            StringJoiner joiner = new StringJoiner(",",
                    "SELECT DS.DM_DEVICE_ID "
                            + "FROM AP_DEVICE_SUBSCRIPTION DS "
                            + "WHERE US.DM_DEVICE_ID IN (", ") AND TENANT_ID = ?");
            deviceIds.stream().map(ignored -> "?").forEach(joiner::add);
            String query = joiner.toString();
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                for (Integer deviceId : deviceIds) {
                    ps.setObject(index++, deviceId);
                }
                ps.setInt(index, tenantId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        subscribedDevices.add(rs.getInt("DM_DEVICE_I"));
                    }
                }
            }
            return subscribedDevices;
        } catch (DBConnectionException e) {
            throw new ApplicationManagementDAOException(
                    "Error occurred while obtaining the DB connection when getting subscribed device Ids for given "
                            + "device Id list.", e);
        } catch (SQLException e) {
            throw new ApplicationManagementDAOException("SWL Error occurred while getting subscribed device ids for "
                    + "given devie Id list.", e);
        }    }

    @Override
    public void updateUserSubscription(int tenantId, String updateBy, boolean isUnsubscribed,
            List<String> userList, int releaseId) throws ApplicationManagementDAOException {
        Connection conn;
        try {
            conn = this.getDBConnection();
            String sql = "UPDATE AP_USER_SUBSCRIPTION SET ";

            if (isUnsubscribed){
                sql += "UNSUBSCRIBED = true, UNSUBSCRIBED_BY = ?, UNSUBSCRIBED_TIMESTAMP ";
            } else {
                sql += "SUBSCRIBED_BY = ?, SUBSCRIBED_TIMESTAMP =? ";
            }

            sql += "WHERE USER_NAME = ? AND AP_APP_RELEASE_ID = ? AND AND TENANT_ID = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                Calendar calendar = Calendar.getInstance();
                Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
                for (String username : userList) {
                    stmt.setString(1, updateBy);
                    stmt.setTimestamp(2, timestamp);
                    stmt.setString(3, username);
                    stmt.setInt(4, releaseId);
                    stmt.setInt(5, tenantId);
                }
                stmt.executeBatch();
            }
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining the DB connection to update the user subscriptions of application.";
            log.error(msg);
            throw new ApplicationManagementDAOException(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred when obtaining database connection for updating the user subscriptions of application.";
            log.error(msg);
            throw new ApplicationManagementDAOException(msg, e);
        }
    }
}
