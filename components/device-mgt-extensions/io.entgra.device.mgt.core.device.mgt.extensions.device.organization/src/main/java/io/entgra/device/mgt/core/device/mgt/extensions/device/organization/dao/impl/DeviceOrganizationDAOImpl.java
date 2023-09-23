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
package io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.impl;

import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.DeviceOrganizationDAO;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.util.ConnectionManagerUtil;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.util.DeviceOrganizationDaoUtil;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceNode;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceOrganization;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.DBConnectionException;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.DeviceOrganizationMgtDAOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.util.DeviceOrganizationDaoUtil.getDeviceFromResultSet;

public class DeviceOrganizationDAOImpl implements DeviceOrganizationDAO {

    private static final Log log = LogFactory.getLog(DeviceOrganizationDAOImpl.class);

    @Override
    public List<DeviceNode> getChildrenOf(DeviceNode node, int maxDepth, boolean includeDevice) throws DeviceOrganizationMgtDAOException {
        List<DeviceNode> childNodes = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        try {
            Connection conn = ConnectionManagerUtil.getDBConnection();
            getChildrenRecursive(node, maxDepth, visited, conn, childNodes, includeDevice);
            return childNodes;
            // Prepare SQL query to retrieve children of the node using DM_DEVICE_ORGANIZATION
//            String sql = "SELECT D.ID, D.NAME, D.DESCRIPTION, D.DEVICE_IDENTIFICATION, DT.NAME AS DEVICE_TYPE_NAME " +
//                    "FROM DM_DEVICE D " +
//                    "JOIN DM_DEVICE_ORGANIZATION DO ON D.ID = DO.DEVICE_ID " +
//                    "JOIN DM_DEVICE_TYPE DT ON D.DEVICE_TYPE_ID = DT.ID " +
//                    "WHERE DO.PARENT_DEVICE_ID = ?";
//
//            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//                stmt.setInt(1, node.getDeviceId());
//                try (ResultSet rs = stmt.executeQuery()) {
//                    while (rs.next()) {
//                        DeviceNode child = getDeviceFromResultSet(rs);
//                        child.setChildren(childNodes);
//                        childNodes.add(child);
//
//                        if (maxDepth > 0) {
//                            List<DeviceNode> deviceChildren = getChildrenOf(child, maxDepth - 1, true);
//                            childNodes.addAll(deviceChildren);
//                        }
//                    }
//                    return childNodes;
//                }
//            }
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining DB connection to retrieve all child devices for " +
                    "parent device ID " +
                    node.getDeviceId();
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred while processing SQL to retrieve all child devices for " +
                    "parent device ID" + node.getDeviceId();
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        }
    }

    private void getChildrenRecursive(DeviceNode node, int maxDepth, Set<Integer> visited, Connection conn,
                                      List<DeviceNode> childNodes, boolean includeDevice) throws SQLException {
        if (maxDepth <= 0 || visited.contains(node.getDeviceId())) {
            return;
        }

        visited.add(node.getDeviceId());

        String sql = "SELECT D.ID, D.NAME, D.DESCRIPTION, D.DEVICE_IDENTIFICATION, DT.NAME AS DEVICE_TYPE_NAME " +
                "FROM DM_DEVICE D " +
                "JOIN DM_DEVICE_ORGANIZATION DO ON D.ID = DO.DEVICE_ID " +
                "JOIN DM_DEVICE_TYPE DT ON D.DEVICE_TYPE_ID = DT.ID " +
                "WHERE DO.PARENT_DEVICE_ID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, node.getDeviceId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DeviceNode child = getDeviceFromResultSet(rs);
                    child.setChildren(childNodes);
//                    if (includeDevice) {
//                        childNodes.add(node); // Add the parent device if includeDevice is true.
//                    }
                    childNodes.add(child);

                    getChildrenRecursive(child, maxDepth - 1, visited, conn, childNodes, includeDevice);
                }
            }
        }
    }

//    @Override
//    public List<DeviceNode> getParentDevices(DeviceNode node) throws DeviceOrganizationMgtDAOException {
//        List<DeviceNode> parentDevices = null;
//        try {
//            Connection conn = ConnectionManagerUtil.getDBConnection();
//            String sql = "SELECT do.PARENT_DEVICE_ID,d.ID, d.DESCRIPTION,d.DEVICE_IDENTIFICATION, d.NAME, t.NAME AS DEVICE_TYPE_NAME " +
//                    "FROM DM_DEVICE d, DM_DEVICE_TYPE t, DM_DEVICE_ORGANIZATION do " +
//                    "WHERE d.ID IN (SELECT ID FROM DM_DEVICE WHERE ID = ?) " +
//                    "AND d.ID = do.DEVICE_ID" +
//                    "AND d.DEVICE_TYPE_ID = t.ID";
//
//            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//                stmt.setInt(1, node.getDeviceId());
//                try (ResultSet rs = stmt.executeQuery()) {
//                    parentDevices = new ArrayList<>();
//                    while (rs.next()) {
//                        parentDevices.add(getDeviceFromResultSet(rs));
//                    }
//                    return parentDevices;
//                }
//            }
//        } catch (DBConnectionException e) {
//            String msg = "Error occurred while obtaining DB connection to retrieve all parent devices for " +
//                    "child device ID " +
//                    deviceID;
//            log.error(msg);
//            throw new DeviceOrganizationMgtDAOException(msg, e);
//        } catch (SQLException e) {
//            String msg = "Error occurred while processing SQL to retrieve all parent devices for " +
//                    "child device ID" + deviceID;
//            log.error(msg);
//            throw new DeviceOrganizationMgtDAOException(msg, e);
//        }
//    }

    @Override
    public boolean addDeviceOrganization(DeviceOrganization deviceOrganization) throws DeviceOrganizationMgtDAOException {
        try {
            String sql = "INSERT INTO DM_DEVICE_ORGANIZATION (DEVICE_ID, PARENT_DEVICE_ID, LAST_UPDATED_TIMESTAMP)" +
                    " VALUES (?, ?, ?)";

            Connection conn = ConnectionManagerUtil.getDBConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, deviceOrganization.getDeviceId());
                stmt.setInt(2, deviceOrganization.getParentDeviceId());
                stmt.setDate(3, deviceOrganization.getUpdateTime());
                return stmt.executeUpdate() > 0;
            }

        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining DB connection to insert device organization for " +
                    deviceOrganization.getDeviceId();
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred while processing SQL to insert device organization for " +
                    deviceOrganization.getDeviceId();
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        }
    }

    @Override
    public boolean updateDeviceOrganization(int deviceID, int parentDeviceID, Date timestamp, int organizationId)
            throws DeviceOrganizationMgtDAOException {
        try {
            String sql = "UPDATE DM_DEVICE_ORGANIZATION SET DEVICE_ID = ? , PARENT_DEVICE_ID = ? , " +
                    "LAST_UPDATED_TIMESTAMP = ? WHERE ID = ? ";

            Connection conn = ConnectionManagerUtil.getDBConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, deviceID);
                stmt.setInt(2, parentDeviceID);
                stmt.setDate(3, timestamp);
                stmt.setInt(4, organizationId);
                return stmt.executeUpdate() > 0;
            }

        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining DB connection to update device organization for " +
                    organizationId;
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred while processing SQL to update device organization for " +
                    organizationId;
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        }
    }

    @Override
    public DeviceOrganization getDeviceOrganizationByID(int organizationId) throws DeviceOrganizationMgtDAOException {
        try {
            String sql = "SELECT do.ID,do.DEVICE_ID, do.PARENT_DEVICE_ID, do.LAST_UPDATED_TIMESTAMP " +
                    "FROM DM_DEVICE_ORGANIZATION do WHERE do.ID = ? ";

            Connection conn = ConnectionManagerUtil.getDBConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, organizationId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return DeviceOrganizationDaoUtil.loadDeviceOrganization(rs);
                    }
                    log.info("No Device Organization found");
                    return null;
                }
            }

        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining DB connection to get device organization details for " +
                    organizationId;
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred while processing SQL to get device organization details for " +
                    organizationId;
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        }
    }

    @Override
    public boolean deleteDeviceOrganizationByID(int organizationId) throws DeviceOrganizationMgtDAOException {
        try {
            Connection conn = ConnectionManagerUtil.getDBConnection();
            String deleteOrganizationSql = "DELETE FROM DM_DEVICE_ORGANIZATION WHERE ID = ?";

            try (PreparedStatement deleteOrgStmt = conn.prepareStatement(deleteOrganizationSql)) {

                // Delete the organization
                deleteOrgStmt.setInt(1, organizationId);
                return deleteOrgStmt.executeUpdate() > 0;
            }

        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining DB connection to delete device organization for " +
                    organizationId;
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred while processing SQL to delete device organization details for " +
                    organizationId;
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        }
    }

    @Override
    public boolean deleteDeviceAssociations(int deviceId) throws DeviceOrganizationMgtDAOException {
        try {
            Connection conn = ConnectionManagerUtil.getDBConnection();
            String deleteByDeviceIdSql = "DELETE FROM DM_DEVICE_ORGANIZATION WHERE DEVICE_ID = ?";
            String deleteByParentDeviceIdSql = "DELETE FROM DM_DEVICE_ORGANIZATION WHERE PARENT_DEVICE_ID = ?";

            try (PreparedStatement deleteByDeviceIdStmt = conn.prepareStatement(deleteByDeviceIdSql);
                 PreparedStatement deleteByParentDeviceIdStmt = conn.prepareStatement(deleteByParentDeviceIdSql)) {

                // Delete device organizations where the device is the device_id
                deleteByDeviceIdStmt.setInt(1, deviceId);

                // Delete device organizations where the device is the parent_device_id
                deleteByParentDeviceIdStmt.setInt(1, deviceId);

                return deleteByDeviceIdStmt.executeUpdate() > 0 | deleteByParentDeviceIdStmt.executeUpdate() > 0;

            }
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining DB connection to delete device organization for device ID" +
                    deviceId;
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred while processing SQL to delete device organization details for device ID " +
                    deviceId;
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        }
    }

    @Override
    public boolean doesDeviceIdExist(int deviceId) throws DeviceOrganizationMgtDAOException {
        try {
            Connection conn = ConnectionManagerUtil.getDBConnection();
            String sql = "SELECT 1 " +
                    "FROM DM_DEVICE_ORGANIZATION " +
                    "WHERE device_id = ? OR parent_device_id = ? " +
                    "LIMIT 1";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, deviceId);
                stmt.setInt(2, deviceId);

                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next(); // Returns true if a match is found, false otherwise
                }
            }
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining DB connection to query device organization for device ID" +
                    deviceId;
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred while processing SQL to query device organization details for device ID " +
                    deviceId;
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        }
    }

}
