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

import java.sql.Date;
import java.util.List;

public interface DeviceOrganizationService {

    boolean addDeviceOrganization(DeviceOrganization deviceOrganization)
            throws DeviceOrganizationMgtPluginException;

    List<DeviceNode> getChildrenOf(DeviceNode node, int maxDepth, boolean includeDevice)
            throws DeviceOrganizationMgtPluginException;

    List<DeviceNode> getParentsOf(DeviceNode node, int maxDepth, boolean includeDevice)
            throws DeviceOrganizationMgtPluginException;

    DeviceOrganization getDeviceOrganizationByID(int organizationId)
            throws DeviceOrganizationMgtPluginException;

    boolean doesDeviceIdExist(int deviceId)
            throws DeviceOrganizationMgtPluginException;

    boolean updateDeviceOrganization(int deviceID, int parentDeviceID, Date timestamp, int organizationId)
            throws DeviceOrganizationMgtPluginException;

    boolean deleteDeviceOrganizationByID(int organizationId)
            throws DeviceOrganizationMgtPluginException;

    boolean deleteDeviceAssociations(int deviceId)
            throws DeviceOrganizationMgtPluginException;

}
