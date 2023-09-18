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
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.DBConnectionException;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.DeviceOrganizationMgtDAOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.util.DeviceOrganizationDaoUtil.generateParameterPlaceholders;
import static io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.util.DeviceOrganizationDaoUtil.getDeviceFromResultSet;

public class DeviceOrganizationDAOImpl implements DeviceOrganizationDAO {

    private static final Log log = LogFactory.getLog(DeviceOrganizationDAOImpl.class);

    @Override
    public List<Device> getChildDevices(int parentId) throws DeviceOrganizationMgtDAOException {
        List<Device> childDevices = null;
        try {
            Connection conn = ConnectionManagerUtil.getDBConnection();
            String sql = "SELECT d.ID, d.DEVICE_IDENTIFICATION, d.NAME, t.NAME AS DEVICE_TYPE " +
                    "FROM DM_DEVICE d, DM_DEVICE_TYPE t " +
                    "WHERE d.PARENT_DEVICE_ID = ? AND d.DEVICE_TYPE_ID = t.ID";

            try (PreparedStatement stmt = conn.prepareStatement(sql)){
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
            String sql = "SELECT d.ID, d.DEVICE_IDENTIFICATION, d.NAME, t.NAME AS DEVICE_TYPE " +
                    "FROM DM_DEVICE d, DM_DEVICE_TYPE t " +
                    "WHERE d.ID IN (SELECT PARENT_DEVICE_ID FROM DM_DEVICE WHERE ID = ?) " +
                    "AND d.DEVICE_TYPE_ID = t.ID";

            try (PreparedStatement stmt = conn.prepareStatement(sql)){
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


    // Simulated database storage

//    public void addDevice(Device device) {
//        devices.put(device.getId(), device);
//    }
//
//    public void addChildDevice(int parentId, int childId) {
//        deviceHierarchy.computeIfAbsent(parentId, k -> new ArrayList<>()).add(childId);
//    }
//
//    public Device getDeviceById(int deviceId) {
//        return devices.get(deviceId);
//    }
}
