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
            if (maxDepth <= 0) {
                return children;
            }
            children = deviceOrganizationDao.getChildrenOf(node, maxDepth, includeDevice);
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

//    @Override
//    public List<DeviceNode> getParentsOf(DeviceNode node, int maxDepth, boolean includeDevice)
//            throws DeviceOrganizationMgtPluginException {
//        try {
//            // Open a database connection
//            ConnectionManagerUtil.openDBConnection();
//
//            List<DeviceNode> parents = new ArrayList<>();
//            if (includeDevice) {
//                parents.add(node);
//            }
//            retrieveParents(node, parents, 1, maxDepth);
//            return parents;
//        } catch (DBConnectionException e) {
//            String msg = "Error occurred while obtaining the database connection to retrieve parent devices";
//            log.error(msg);
//            throw new DeviceOrganizationMgtPluginException(msg, e);
//        } catch (DeviceOrganizationMgtDAOException e) {
//            String msg = "Error occurred in the database level while retrieving parent devices";
//            log.error(msg);
//            throw new DeviceOrganizationMgtPluginException(msg, e);
//        } finally {
//            // Close the database connection
//            ConnectionManagerUtil.closeDBConnection();
//        }
//    }

//    private void retrieveParents(DeviceNode node, List<DeviceNode> result, int currentDepth, int maxDepth)
//            throws DeviceOrganizationMgtDAOException {
//        if (currentDepth > maxDepth) {
//            return;
//        }
//
//        List<Device> parentDevices = deviceOrganizationDao.getParentDevices(node);
//
//        for (Device parentDevice : parentDevices) {
//            DeviceNode parentNode = new DeviceNode();
//            parentNode.setDeviceId(parentDevice.getId());
//            parentNode.setDevice(parentDevice);
//
//            result.add(parentNode);
//
//            if (currentDepth < maxDepth) {
//                retrieveParents(parentNode, result, currentDepth + 1, maxDepth);
//            }
//        }
//    }

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
    public boolean updateDeviceOrganization(int deviceID, int parentDeviceID, Date timestamp,
                                            int organizationId) throws DeviceOrganizationMgtPluginException {
        String msg = "";
        DeviceOrganization deviceOrganization = getDeviceOrganizationByID(organizationId);
        if (deviceOrganization == null) {
            String errorMsg = "Cannot find device organization for organization ID" + organizationId;
            log.error(errorMsg);
            return false;
        }

        try {
            ConnectionManagerUtil.beginDBTransaction();
            boolean result = deviceOrganizationDao.updateDeviceOrganization(deviceID, parentDeviceID, timestamp,
                    organizationId);
            if (result) {
                msg = "Device organization updated successfully,for " + organizationId;
                if (log.isDebugEnabled()) {
                    log.debug(msg);
                }
            } else {
                ConnectionManagerUtil.rollbackDBTransaction();
                msg = "Device organization failed to update,for " + organizationId;
                throw new DeviceOrganizationMgtPluginException(msg);
            }
            ConnectionManagerUtil.commitDBTransaction();
            return true;
        } catch (DBConnectionException e) {
            msg = "Error occurred while obtaining the database connection to update device organization for " + organizationId;
            log.error(msg);
            throw new DeviceOrganizationMgtPluginException(msg, e);
        } catch (DeviceOrganizationMgtDAOException e) {
            ConnectionManagerUtil.rollbackDBTransaction();
            msg = "Error occurred in the database level while updating device organization for " + organizationId;
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

    @Override
    public boolean deleteDeviceOrganizationByID(int organizationId)
            throws DeviceOrganizationMgtPluginException {
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
