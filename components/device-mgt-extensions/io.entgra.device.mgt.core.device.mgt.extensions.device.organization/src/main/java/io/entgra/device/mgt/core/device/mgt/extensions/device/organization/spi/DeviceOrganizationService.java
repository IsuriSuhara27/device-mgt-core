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
package io.entgra.device.mgt.core.device.mgt.extensions.device.organization.spi;

import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceNode;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceOrganization;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.DeviceOrganizationMgtPluginException;

import java.util.List;

/**
 * This interface defines operations for managing device organizations.
 */
public interface DeviceOrganizationService {

    /**
     * Adds a new device organization.
     *
     * @param deviceOrganization The device organization to add.
     * @return True if the operation was successful, false otherwise.
     * @throws DeviceOrganizationMgtPluginException If an error occurs during the operation.
     */
    boolean addDeviceOrganization(DeviceOrganization deviceOrganization)
            throws DeviceOrganizationMgtPluginException;

    /**
     * Retrieves a list of child nodes of a given device node, up to a specified depth.
     *
     * @param node          The parent device node.
     * @param maxDepth      The maximum depth of child nodes to retrieve.
     * @param includeDevice Indicates whether to include device information in the retrieved nodes.
     * @return A list of child device nodes.
     * @throws DeviceOrganizationMgtPluginException If an error occurs during the operation.
     */
    List<DeviceNode> getChildrenOf(DeviceNode node, int maxDepth, boolean includeDevice)
            throws DeviceOrganizationMgtPluginException;

    /**
     * Retrieves a list of parent nodes of a given device node, up to a specified depth.
     *
     * @param node          The child device node.
     * @param maxDepth      The maximum depth of parent nodes to retrieve.
     * @param includeDevice Indicates whether to include device information in the retrieved nodes.
     * @return A list of parent device nodes.
     * @throws DeviceOrganizationMgtPluginException If an error occurs during the operation.
     */
    List<DeviceNode> getParentsOf(DeviceNode node, int maxDepth, boolean includeDevice)
            throws DeviceOrganizationMgtPluginException;

    /**
     * Adds a list of device organizations.
     *
     * @param deviceOrganizations The list of device organizations to add.
     * @throws DeviceOrganizationMgtPluginException If an error occurs during the operation.
     */
    void addDeviceOrganizationList(List<DeviceOrganization> deviceOrganizations)
            throws DeviceOrganizationMgtPluginException;

    /**
     * Retrieves a list of all device organizations.
     *
     * @return A list of all device organizations.
     * @throws DeviceOrganizationMgtPluginException If an error occurs during the operation.
     */
    List<DeviceOrganization> getAllDeviceOrganizations() throws DeviceOrganizationMgtPluginException;

    /**
     * Retrieves a specific device organization by its organization ID.
     *
     * @param organizationId The organization ID of the device organization to retrieve.
     * @return The device organization with the specified ID.
     * @throws DeviceOrganizationMgtPluginException If an error occurs during the operation.
     */
    DeviceOrganization getDeviceOrganizationByID(int organizationId)
            throws DeviceOrganizationMgtPluginException;

    /**
     * Checks if a device organization with the specified device and parent device IDs already exists.
     *
     * @param deviceId       The ID of the device.
     * @param parentDeviceId The ID of the parent device.
     * @return True if the organization exists, false otherwise.
     * @throws DeviceOrganizationMgtPluginException If an error occurs during the operation.
     */
    boolean organizationExists(int deviceId, int parentDeviceId)
            throws DeviceOrganizationMgtPluginException;

    /**
     * Retrieve a device organization by its unique key (deviceId and parentDeviceId).
     *
     * @param deviceId       The ID of the device.
     * @param parentDeviceId The ID of the parent device.
     * @return The retrieved DeviceOrganization object, or null if not found.
     * @throws DeviceOrganizationMgtPluginException If an error occurs.
     */
    DeviceOrganization getDeviceOrganizationByUniqueKey(int deviceId, int parentDeviceId)
            throws DeviceOrganizationMgtPluginException;

    /**
     * Checks whether a record with the specified device ID exists either in the deviceID column or
     * parentDeviceID column in the device organization table.
     *
     * @param deviceId The ID of the device to check.
     * @return True if the device exists, false otherwise.
     * @throws DeviceOrganizationMgtPluginException If an error occurs during the operation.
     */
    boolean doesDeviceIdExist(int deviceId)
            throws DeviceOrganizationMgtPluginException;

    /**
     * Updates a device organization.
     *
     * @param organization The device organization to update.
     * @return True if the operation was successful, false otherwise.
     * @throws DeviceOrganizationMgtPluginException If an error occurs during the operation.
     */
    boolean updateDeviceOrganization(DeviceOrganization organization)
            throws DeviceOrganizationMgtPluginException;

    /**
     * Deletes a device organization by its organizationID.
     *
     * @param organizationId The organization ID of the device organization to delete.
     * @return True if the operation was successful, false otherwise.
     * @throws DeviceOrganizationMgtPluginException If an error occurs during the operation.
     */
    boolean deleteDeviceOrganizationByID(int organizationId)
            throws DeviceOrganizationMgtPluginException;

    /**
     * Deletes records associated with a particular device ID from the device organization table.
     * This method deletes records where the provided device ID matches either the deviceID column or
     * parentDeviceID column in the device organization table.
     *
     * @param deviceId The ID of the device for which associations should be deleted.
     * @return True if the operation was successful, false otherwise.
     * @throws DeviceOrganizationMgtPluginException If an error occurs during the operation.
     */
    boolean deleteDeviceAssociations(int deviceId)
            throws DeviceOrganizationMgtPluginException;

}
