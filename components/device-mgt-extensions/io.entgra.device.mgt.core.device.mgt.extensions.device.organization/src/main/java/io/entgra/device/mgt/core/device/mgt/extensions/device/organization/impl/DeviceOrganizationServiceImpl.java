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
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.util.LockManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
        if (node == null || node.getDeviceId() <= 0 || maxDepth < 0) {
            throw new BadRequestException("Invalid input parameters.");
        }
        try {
            // Open a database connection
            ConnectionManagerUtil.openDBConnection();
            return deviceOrganizationDao.getChildrenOf(node, maxDepth, includeDevice);
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
        if (node == null || node.getDeviceId() <= 0 || maxDepth < 0) {
            throw new BadRequestException("Invalid input parameters.");
        }
        try {
            // Open a database connection
            ConnectionManagerUtil.openDBConnection();
            return deviceOrganizationDao.getParentsOf(node, maxDepth, includeDevice);
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

    @Override
    public boolean addDeviceOrganization(DeviceOrganization deviceOrganization)
            throws DeviceOrganizationMgtPluginException {
        if (deviceOrganization == null || deviceOrganization.getDeviceId() == 0
                || deviceOrganization.getParentDeviceId() == 0) {
            throw new BadRequestException("Invalid input parameters.");
        }
        String msg = "";
        // Check if an organization with the same deviceId and parentDeviceId already exists
        if (organizationExists(deviceOrganization.getDeviceId(), deviceOrganization.getParentDeviceId())) {
            msg = "Device organization with the same deviceId and parentDeviceId already exists.";
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg);
        }

        // Use LockManager to get a lock object based on deviceId and parentDeviceId
//        LockManager lockManager = LockManager.getInstance();
//        Object lock = lockManager.getLock(deviceOrganization.getDeviceId(), deviceOrganization.getParentDeviceId());
//
//        synchronized (lock) {

            try {
                ConnectionManagerUtil.beginDBTransaction();
                boolean result = deviceOrganizationDao.addDeviceOrganization(deviceOrganization);
                if (result) {
                    msg = "Device organization added successfully, for device ID " + deviceOrganization.getDeviceId() +
                    " and parent device ID " + deviceOrganization.getParentDeviceId();
                    if (log.isDebugEnabled()) {
                        log.debug(msg);
                    }
                } else {
                    ConnectionManagerUtil.rollbackDBTransaction();
                    msg = "Device organization failed to add, for device ID " + deviceOrganization.getDeviceId() +
                            " and parent device ID " + deviceOrganization.getParentDeviceId();
                    throw new DeviceOrganizationMgtPluginException(msg);
                }
                ConnectionManagerUtil.commitDBTransaction();
                return true;
            } catch (DBConnectionException e) {
                msg = "Error occurred while obtaining the database connection to add device organization for device ID "
                        + deviceOrganization.getDeviceId() + " and parent device ID"
                        + deviceOrganization.getParentDeviceId();
                log.error(msg);
                throw new DeviceOrganizationMgtPluginException(msg, e);
            } catch (DeviceOrganizationMgtDAOException e) {
                ConnectionManagerUtil.rollbackDBTransaction();
                msg = "Error occurred in the database level while adding device organization for device ID " +
                        deviceOrganization.getDeviceId() + " and parent device ID"
                        + deviceOrganization.getParentDeviceId();
                log.error(msg);
                throw new DeviceOrganizationMgtPluginException(msg, e);
            } finally {
                ConnectionManagerUtil.closeDBConnection();
            }
//        }
    }

    // Helper method to check if an organization with the same deviceId and parentDeviceId already exists
    @Override
    public boolean organizationExists(int deviceId, int parentDeviceId) throws DeviceOrganizationMgtPluginException {
        try {
            ConnectionManagerUtil.openDBConnection();
            boolean exists = deviceOrganizationDao.organizationExists(deviceId, parentDeviceId);
            return exists;
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining the database connection to check organization existence.";
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg, e);
        } catch (DeviceOrganizationMgtDAOException e) {
            String msg = "Error occurred in the database level while checking organization existence.";
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg, e);
        } finally {
            ConnectionManagerUtil.closeDBConnection();
        }
    }

    @Override
    public boolean updateDeviceOrganization(DeviceOrganization organization)
            throws DeviceOrganizationMgtPluginException {
        if (organization == null || organization.getOrganizationId() <= 0) {
            throw new BadRequestException("Invalid input parameters.");
        }
        String msg = "";
        DeviceOrganization deviceOrganization = getDeviceOrganizationByID(organization.getOrganizationId());
        if (deviceOrganization == null) {
            String errorMsg = "Cannot find device organization for organization ID " + organization.getOrganizationId();
            log.error(errorMsg);
            return false;
        }

        try {
            ConnectionManagerUtil.beginDBTransaction();
            boolean result = deviceOrganizationDao.updateDeviceOrganization(organization);
            if (result) {
                msg = "Device organization updated successfully,for " + organization.getOrganizationId();
                if (log.isDebugEnabled()) {
                    log.debug(msg);
                }
            } else {
                ConnectionManagerUtil.rollbackDBTransaction();
                msg = "Device organization failed to update,for " + organization.getOrganizationId();
                throw new DeviceOrganizationMgtPluginException(msg);
            }
            ConnectionManagerUtil.commitDBTransaction();
            return true;
        } catch (DBConnectionException e) {
            msg = "Error occurred while obtaining the database connection to update device organization for " +
                    organization.getOrganizationId();
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg, e);
        } catch (DeviceOrganizationMgtDAOException e) {
            ConnectionManagerUtil.rollbackDBTransaction();
            msg = "Error occurred in the database level while updating device organization for " +
                    organization.getOrganizationId();
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg, e);
        } finally {
            ConnectionManagerUtil.closeDBConnection();
        }
    }

    @Override
    public DeviceOrganization getDeviceOrganizationByID(int organizationId)
            throws DeviceOrganizationMgtPluginException {
        if (organizationId <= 0) {
            throw new BadRequestException("Invalid input parameters.");
        }
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

    @Override
    public boolean deleteDeviceOrganizationByID(int organizationId)
            throws DeviceOrganizationMgtPluginException {
        if (organizationId <= 0) {
            throw new BadRequestException("Invalid input parameters.");
        }
        String msg = "";

        DeviceOrganization deviceOrganization = getDeviceOrganizationByID(organizationId);
        if (deviceOrganization == null) {
            msg = "Cannot find device organization for organization ID " + organizationId;
            log.error(msg);
            return false;
        }

        try {
            ConnectionManagerUtil.beginDBTransaction();
            boolean result = deviceOrganizationDao.deleteDeviceOrganizationByID(organizationId);
            if (result) {
                msg = "Device organization record deleted successfully,for " + organizationId;
                if (log.isDebugEnabled()) {
                    log.debug(msg);
                }
            } else {
                ConnectionManagerUtil.rollbackDBTransaction();
                msg = "Device organization failed to delete,for " + organizationId;
                throw new DeviceOrganizationMgtPluginException(msg);
            }
            ConnectionManagerUtil.commitDBTransaction();
            return true;
        } catch (DBConnectionException e) {
            msg = "Error occurred while obtaining the database connection to delete device organization for " + organizationId;
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg, e);
        } catch (DeviceOrganizationMgtDAOException e) {
            ConnectionManagerUtil.rollbackDBTransaction();
            msg = "Error occurred in the database level while deleting device organization for " + organizationId;
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg, e);
        } finally {
            ConnectionManagerUtil.closeDBConnection();

        }
    }

    @Override
    public boolean deleteDeviceAssociations(int deviceId)
            throws DeviceOrganizationMgtPluginException {
        if (deviceId <= 0) {
            throw new BadRequestException("Invalid input parameters.");
        }
        String msg = "";

        boolean deviceIdExist = doesDeviceIdExist(deviceId);
        if (!deviceIdExist) {
            msg = "Cannot find device organization associated with device ID " + deviceId;
            log.error(msg);
            return false;
        }

        try {
            ConnectionManagerUtil.beginDBTransaction();
            boolean result = deviceOrganizationDao.deleteDeviceAssociations(deviceId);
            if (result) {
                msg = "Device organization records deleted successfully,for " + deviceId;
                if (log.isDebugEnabled()) {
                    log.debug(msg);
                }
            } else {
                ConnectionManagerUtil.rollbackDBTransaction();
                msg = "Device organization failed to delete,for " + deviceId;
                throw new DeviceOrganizationMgtPluginException(msg);
            }
            ConnectionManagerUtil.commitDBTransaction();
            return true;
        } catch (DBConnectionException e) {
            msg = "Error occurred while obtaining the database connection to delete device organization for " + deviceId;
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg, e);
        } catch (DeviceOrganizationMgtDAOException e) {
            ConnectionManagerUtil.rollbackDBTransaction();
            msg = "Error occurred in the database level while deleting device organization for " + deviceId;
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg, e);
        } finally {
            ConnectionManagerUtil.closeDBConnection();

        }
    }

    @Override
    public boolean doesDeviceIdExist(int deviceId)
            throws DeviceOrganizationMgtPluginException {
        if (deviceId <= 0) {
            throw new BadRequestException("Invalid input parameters.");
        }
        try {
            // Open a database connection
            ConnectionManagerUtil.openDBConnection();
            boolean deviceIdExist = deviceOrganizationDao.doesDeviceIdExist(deviceId);
            return deviceIdExist;
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining the database connection to check deviceID exists";
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg, e);
        } catch (DeviceOrganizationMgtDAOException e) {
            String msg = "Error occurred in the database level while checking the existence of deviceID";
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg, e);
        } finally {
            // Close the database connection
            ConnectionManagerUtil.closeDBConnection();
        }
    }

}
