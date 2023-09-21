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
package io.entgra.device.mgt.core.device.mgt.extensions.device.organization.impl;

import io.entgra.device.mgt.core.device.mgt.common.Device;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.DeviceOrganizationDAO;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.DeviceOrganizationDAOFactory;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.util.ConnectionManagerUtil;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceNode;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceOrganization;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.BadRequestException;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.DBConnectionException;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.DeviceOrganizationMgtDAOException;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.DeviceOrganizationMgtPluginException;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.spi.DeviceOrganizationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class DeviceOrganizationServiceImpl implements DeviceOrganizationService {

    private static final Log log = LogFactory.getLog(DeviceOrganizationServiceImpl.class);

    private final DeviceOrganizationDAO deviceOrganizationDao;

    public DeviceOrganizationServiceImpl() {
        this.deviceOrganizationDao = DeviceOrganizationDAOFactory.getDeviceOrganizationDAO();
    }

    @Override
    public List<DeviceNode> getChildrenOf(DeviceNode node, int maxDepth, boolean includeDevice)
            throws DeviceOrganizationMgtPluginException {
        try {
            // Open a database connection
            ConnectionManagerUtil.openDBConnection();

            List<DeviceNode> children = new ArrayList<>();
            if (includeDevice) {
                children.add(node);
            }
            retrieveChildren(node, children, 1, maxDepth);
            return children;
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining the database connection to retrieve child devices";
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg, e);
        } catch (DeviceOrganizationMgtDAOException e) {
            String msg = "Error occurred in the database level while retrieving child devices";
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg, e);
        } finally {
            // Close the database connection
            ConnectionManagerUtil.closeDBConnection();
        }
    }

    @Override
    public List<DeviceNode> getParentsOf(DeviceNode node, int maxDepth, boolean includeDevice)
            throws DeviceOrganizationMgtPluginException {
        try {
            // Open a database connection
            ConnectionManagerUtil.openDBConnection();

            List<DeviceNode> parents = new ArrayList<>();
            if (includeDevice) {
                parents.add(node);
            }
            retrieveParents(node, parents, 1, maxDepth);
            return parents;
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining the database connection to retrieve parent devices";
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg, e);
        } catch (DeviceOrganizationMgtDAOException e) {
            String msg = "Error occurred in the database level while retrieving parent devices";
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg, e);
        } finally {
            // Close the database connection
            ConnectionManagerUtil.closeDBConnection();
        }
    }

    private void retrieveParents(DeviceNode node, List<DeviceNode> result, int currentDepth, int maxDepth)
            throws DeviceOrganizationMgtDAOException {
        if (currentDepth > maxDepth) {
            return;
        }

        List<Device> parentDevices = deviceOrganizationDao.getParentDevices(node.getDeviceId());

        for (Device parentDevice : parentDevices) {
            DeviceNode parentNode = new DeviceNode();
            parentNode.setDeviceId(parentDevice.getId());
            parentNode.setDevice(parentDevice);

            result.add(parentNode);

            if (currentDepth < maxDepth) {
                retrieveParents(parentNode, result, currentDepth + 1, maxDepth);
            }
        }
    }

    private void retrieveChildren(DeviceNode node, List<DeviceNode> result, int currentDepth, int maxDepth)
            throws DeviceOrganizationMgtDAOException {
        if (currentDepth > maxDepth) {
            return;
        }

        List<Device> childDevices = deviceOrganizationDao.getChildDevices(node.getDeviceId());

        for (Device childDevice : childDevices) {
            DeviceNode childNode = new DeviceNode();
            childNode.setDeviceId(childDevice.getId());
            childNode.setDevice(childDevice);

            result.add(childNode);

            if (currentDepth < maxDepth) {
                retrieveChildren(childNode, result, currentDepth + 1, maxDepth);
            }
        }
    }

    @Override
    public boolean addDeviceOrganization(DeviceOrganization deviceOrganization)
            throws DeviceOrganizationMgtPluginException {
        String msg = "";

        try {
            ConnectionManagerUtil.beginDBTransaction();
            boolean result = deviceOrganizationDao.addDeviceOrganization(deviceOrganization);
            if (result) {
                msg = "Device organization added successfully,for " + deviceOrganization.getDeviceId();
                if (log.isDebugEnabled()) {
                    log.debug(msg);
                }
            } else {
                ConnectionManagerUtil.rollbackDBTransaction();
                msg = "Device organization failed to add,for " + deviceOrganization.getDeviceId();
                throw new DeviceOrganizationMgtPluginException(msg);
            }
            ConnectionManagerUtil.commitDBTransaction();
            return true;
        } catch (DBConnectionException e) {
            msg = "Error occurred while obtaining the database connection to add device organization for " +
                    deviceOrganization.getDeviceId();
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg, e);
        } catch (DeviceOrganizationMgtDAOException e) {
            ConnectionManagerUtil.rollbackDBTransaction();
            msg = "Error occurred in the database level while adding device organization for " +
                    deviceOrganization.getDeviceId();
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg, e);
        } finally {
            ConnectionManagerUtil.closeDBConnection();
        }
    }

    @Override
    public boolean updateDeviceOrganization(int deviceID, int parentDeviceID, Date timestamp, String status,
                                            int organizationId) throws DeviceOrganizationMgtPluginException {
        String msg = "";
        DeviceOrganization deviceOrganization = getDeviceOrganizationByID(organizationId);
        if (deviceOrganization == null) {
            String errorMsg = "Cannot find device organization for organization ID" + organizationId;
            log.error(errorMsg);
            throw new NullPointerException(errorMsg);
//            throw new BadRequestException(errorMsg);
        }

        try {
            ConnectionManagerUtil.beginDBTransaction();
            boolean result = deviceOrganizationDao.updateDeviceOrganization(deviceID, parentDeviceID, timestamp, status,
                    organizationId);
            if (result) {
                msg = "Device organization updated successfully,for " + deviceID;
                if (log.isDebugEnabled()) {
                    log.debug(msg);
                }
            } else {
                ConnectionManagerUtil.rollbackDBTransaction();
                msg = "Device organization failed to update,for " + deviceID;
                throw new DeviceOrganizationMgtPluginException(msg);
            }
            ConnectionManagerUtil.commitDBTransaction();
            return true;
        } catch (DBConnectionException e) {
            msg = "Error occurred while obtaining the database connection to update device organization for " + deviceID;
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg, e);
        } catch (DeviceOrganizationMgtDAOException e) {
            ConnectionManagerUtil.rollbackDBTransaction();
            msg = "Error occurred in the database level while updating device organization for " + deviceID;
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg, e);
        } finally {
            ConnectionManagerUtil.closeDBConnection();

        }
    }

    @Override
    public DeviceOrganization getDeviceOrganizationByID(int organizationId)
            throws DeviceOrganizationMgtPluginException {
        try {
            // Open a database connection
            ConnectionManagerUtil.openDBConnection();
            DeviceOrganization deviceOrganization = deviceOrganizationDao.getDeviceOrganizationByID(organizationId);
            return deviceOrganization;
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining the database connection to retrieve child devices";
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg, e);
        } catch (DeviceOrganizationMgtDAOException e) {
            String msg = "Error occurred in the database level while retrieving child devices";
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg, e);
        } finally {
            // Close the database connection
            ConnectionManagerUtil.closeDBConnection();
        }
    }

}
