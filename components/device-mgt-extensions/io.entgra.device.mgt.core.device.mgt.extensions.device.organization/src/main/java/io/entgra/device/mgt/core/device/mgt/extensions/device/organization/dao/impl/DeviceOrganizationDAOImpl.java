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

import io.entgra.device.mgt.core.device.mgt.common.Device;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.DeviceOrganizationDAO;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.util.ConnectionManagerUtil;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.util.DeviceOrganizationDaoUtil;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceOrganization;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.DBConnectionException;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.DeviceOrganizationMgtDAOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.util.DeviceOrganizationDaoUtil.getDeviceFromResultSet;

public class DeviceOrganizationDAOImpl implements DeviceOrganizationDAO {

    private static final Log log = LogFactory.getLog(DeviceOrganizationDAOImpl.class);

    @Override
    public List<Device> getChildDevices(int parentId) throws DeviceOrganizationMgtDAOException {
        List<Device> childDevices = null;
        try {
            Connection conn = ConnectionManagerUtil.getDBConnection();
            String sql = "SELECT d.ID, d.DESCRIPTION, d.DEVICE_IDENTIFICATION, d.NAME, t.NAME AS DEVICE_TYPE_NAME " +
                    "FROM DM_DEVICE d, DM_DEVICE_TYPE t " +
                    "WHERE d.ID = ? AND d.DEVICE_TYPE_ID = t.ID";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, parentId);
                try (ResultSet rs = stmt.executeQuery()) {
                    childDevices = new ArrayList<>();
                    while (rs.next()) {
                        childDevices.add(getDeviceFromResultSet(rs));
                    }
                    return childDevices;
                }
            }
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining DB connection to retrieve all child devices for " +
                    "parent device ID " +
                    parentId;
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred while processing SQL to retrieve all child devices for " +
                    "parent device ID" + parentId;
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        }
    }

    @Override
    public List<Device> getParentDevices(Integer deviceID) throws DeviceOrganizationMgtDAOException {
        List<Device> parentDevices = null;
        try {
            Connection conn = ConnectionManagerUtil.getDBConnection();
            String sql = "SELECT d.ID, d.DESCRIPTION,d.DEVICE_IDENTIFICATION, d.NAME, t.NAME AS DEVICE_TYPE_NAME " +
                    "FROM DM_DEVICE d, DM_DEVICE_TYPE t " +
                    "WHERE d.ID IN (SELECT ID FROM DM_DEVICE WHERE ID = ?) " +
                    "AND d.DEVICE_TYPE_ID = t.ID";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, deviceID);
                try (ResultSet rs = stmt.executeQuery()) {
                    parentDevices = new ArrayList<>();
                    while (rs.next()) {
                        parentDevices.add(getDeviceFromResultSet(rs));
                    }
                    return parentDevices;
                }
            }
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining DB connection to retrieve all parent devices for " +
                    "child device ID " +
                    deviceID;
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred while processing SQL to retrieve all parent devices for " +
                    "child device ID" + deviceID;
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        }
    }

    @Override
    public boolean addDeviceOrganization(DeviceOrganization deviceOrganization) throws DeviceOrganizationMgtDAOException {
        try {
            String sql = "INSERT INTO DM_DEVICE_ORGANIZATION (DEVICE_ID, PARENT_DEVICE_ID, LAST_UPDATED_TIMESTAMP, STATUS)" +
                    " VALUES (?, ?, ?, ?)";

            Connection conn = ConnectionManagerUtil.getDBConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, deviceOrganization.getDeviceId());
                stmt.setInt(2, deviceOrganization.getParentDeviceId());
                stmt.setDate(3, deviceOrganization.getUpdateTime());
                stmt.setString(4, deviceOrganization.getStatus().toString());
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
    public boolean updateDeviceOrganization(int deviceID, int parentDeviceID, Date timestamp, String status, int organizationId)
            throws DeviceOrganizationMgtDAOException {
        try {
            String sql = "UPDATE DM_DEVICE_ORGANIZATION SET DEVICE_ID = ? , PARENT_DEVICE_ID = ? , " +
                    "LAST_UPDATED_TIMESTAMP = ? , STATUS = ? WHERE ID = ? ";

            Connection conn = ConnectionManagerUtil.getDBConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, deviceID);
                stmt.setInt(2, parentDeviceID);
                stmt.setDate(3, timestamp);
                stmt.setString(4, status);
                stmt.setInt(5, organizationId);
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
            String sql = "SELECT do.ID,do.DEVICE_ID, do.PARENT_DEVICE_ID, do.LAST_UPDATED_TIMESTAMP, do.STATUS " +
                    "FROM DM_DEVICE_ORGANIZATION do WHERE do.ID = ? ";

            Connection conn = ConnectionManagerUtil.getDBConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, organizationId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return DeviceOrganizationDaoUtil.loadDeviceOrganization(rs);
                    }
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

}
