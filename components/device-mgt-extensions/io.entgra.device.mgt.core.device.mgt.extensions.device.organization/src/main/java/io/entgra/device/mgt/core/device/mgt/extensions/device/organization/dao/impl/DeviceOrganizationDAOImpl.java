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
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
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
            if(!includeDevice){
                childNodes.add(node);
            }
            return childNodes;
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
                    node.getChildren().add(child);
                    if (includeDevice) {
                        childNodes.add(node); // Add the parent device if includeDevice is true.
                    }

                    getChildrenRecursive(child, maxDepth - 1, visited, conn, childNodes, includeDevice);
                }
            }
        }
    }

    @Override
    public List<DeviceNode> getParentsOf(DeviceNode node, int maxDepth, boolean includeDevice) throws DeviceOrganizationMgtDAOException {
        List<DeviceNode> parentNodes = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        try {
            Connection conn = ConnectionManagerUtil.getDBConnection();
            getParentsRecursive(node, maxDepth, visited, conn, parentNodes, includeDevice);
            if (!includeDevice) {
                parentNodes.add(node);
            }
            return parentNodes;
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining DB connection to retrieve parent devices for " +
                    "device ID " + node.getDeviceId();
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred while processing SQL to retrieve parent devices for " +
                    "device ID " + node.getDeviceId();
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        }
    }

    private void getParentsRecursive(DeviceNode node, int maxDepth, Set<Integer> visited, Connection conn,
                                     List<DeviceNode> parentNodes, boolean includeDevice) throws SQLException {
        if (maxDepth <= 0 || visited.contains(node.getDeviceId())) {
            return;
        }

        visited.add(node.getDeviceId());

        String sql = "SELECT D.ID, D.NAME, D.DESCRIPTION, D.DEVICE_IDENTIFICATION, DT.NAME AS DEVICE_TYPE_NAME " +
                "FROM DM_DEVICE D " +
                "JOIN DM_DEVICE_ORGANIZATION DO ON D.ID = DO.PARENT_DEVICE_ID " +
                "JOIN DM_DEVICE_TYPE DT ON D.DEVICE_TYPE_ID = DT.ID " +
                "WHERE DO.DEVICE_ID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, node.getDeviceId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DeviceNode parent = getDeviceFromResultSet(rs);
                    if (!includeDevice && parent.getDeviceId() == node.getDeviceId()) {
                        // Skip adding the current node as a parent when includeDevice is false
                        continue;
                    }

                    node.getParents().add(parent);

                    if (!parentNodes.contains(parent)) {
                        parentNodes.add(parent); // Add the parent device if it hasn't been added already.
                    }

                    getParentsRecursive(parent, maxDepth - 1, visited, conn, parentNodes, includeDevice);
                }
            }
        }
    }

    @Override
    public boolean addDeviceOrganization(DeviceOrganization deviceOrganization)
            throws DeviceOrganizationMgtDAOException {
        if (deviceOrganization == null) {
            return false;
        }

        if (deviceOrganization.getDeviceId() == 0 || deviceOrganization.getParentDeviceId() == 0) {
            return false;
        }
        try {
            String sql = "INSERT INTO DM_DEVICE_ORGANIZATION (DEVICE_ID, PARENT_DEVICE_ID, LAST_UPDATED_TIMESTAMP)" +
                    " VALUES (?, ?, ?)";

            Connection conn = ConnectionManagerUtil.getDBConnection();
            Calendar calendar = Calendar.getInstance();
            Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, deviceOrganization.getDeviceId());
                stmt.setInt(2, deviceOrganization.getParentDeviceId());
                stmt.setTimestamp(3, timestamp);
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
    public boolean updateDeviceOrganization(DeviceOrganization deviceOrganization)
            throws DeviceOrganizationMgtDAOException {
        DeviceOrganization organization = getDeviceOrganizationByID(deviceOrganization.getOrganizationId());
        if (deviceOrganization == null) {
            return false;
        }

        if (deviceOrganization.getDeviceId() == 0 || deviceOrganization.getParentDeviceId() == 0) {
            return false;
        }
        try {
            String sql = "UPDATE DM_DEVICE_ORGANIZATION SET DEVICE_ID = ? , PARENT_DEVICE_ID = ? , " +
                    "LAST_UPDATED_TIMESTAMP = ? WHERE ID = ? ";

            Connection conn = ConnectionManagerUtil.getDBConnection();
            Calendar calendar = Calendar.getInstance();
            Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, deviceOrganization.getDeviceId());
                stmt.setInt(2, deviceOrganization.getParentDeviceId());
                stmt.setTimestamp(3, timestamp);
                stmt.setInt(4, deviceOrganization.getOrganizationId());
                return stmt.executeUpdate() > 0;
            }

        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining DB connection to update device organization for " +
                    deviceOrganization.getOrganizationId();
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred while processing SQL to update device organization for " +
                    deviceOrganization.getOrganizationId();
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
