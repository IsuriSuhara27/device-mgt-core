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
package io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao;

import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceNode;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceOrganization;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.DeviceOrganizationMgtDAOException;

import java.util.List;

/**
 * This is responsible for DeviceOrganization related DAO operations.
 */
public interface DeviceOrganizationDAO {

    /**
     * retrieve child devices per particular device ID
     *
     * @param node
     * @param maxDepth
     * @param includeDevice
     * @return
     * @throws DeviceOrganizationMgtDAOException
     */
    List<DeviceNode> getChildrenOf(DeviceNode node, int maxDepth, boolean includeDevice) throws DeviceOrganizationMgtDAOException;

    /**
     * @param node
     * @param maxDepth
     * @param includeDevice
     * @return
     * @throws DeviceOrganizationMgtDAOException
     */
    List<DeviceNode> getParentsOf(DeviceNode node, int maxDepth, boolean includeDevice) throws DeviceOrganizationMgtDAOException;

    /**
     * add a new reocrd to device organization table
     *
     * @param deviceOrganization
     * @return
     * @throws DeviceOrganizationMgtDAOException
     */
    boolean addDeviceOrganization(DeviceOrganization deviceOrganization) throws DeviceOrganizationMgtDAOException;

    /**
     * update a record in device organization table
     *
     * @param deviceOrganization
     * @return
     * @throws DeviceOrganizationMgtDAOException
     */
    boolean updateDeviceOrganization(DeviceOrganization deviceOrganization)
            throws DeviceOrganizationMgtDAOException;

    /**
     * @param organizationId
     * @return
     * @throws DeviceOrganizationMgtDAOException
     */
    DeviceOrganization getDeviceOrganizationByID(int organizationId) throws DeviceOrganizationMgtDAOException;

    /**
     * delete a record from device organization table
     *
     * @param organizationId
     * @throws DeviceOrganizationMgtDAOException
     */
    boolean deleteDeviceOrganizationByID(int organizationId) throws DeviceOrganizationMgtDAOException;

    /**
     * delete a record associated with a particular device ID from device organization table
     * delete a record if the param ID is either device_ID OR parent_device_ID in the device organization table
     *
     * @param deviceId
     * @return
     * @throws DeviceOrganizationMgtDAOException
     */
    boolean deleteDeviceAssociations(int deviceId) throws DeviceOrganizationMgtDAOException;

    boolean doesDeviceIdExist(int deviceId) throws DeviceOrganizationMgtDAOException;
}
