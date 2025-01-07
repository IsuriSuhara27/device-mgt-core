/*
 * Copyright (C) 2018 - 2024 Entgra (Pvt) Ltd, Inc - All Rights Reserved.
 *
 * Unauthorised copying/redistribution of this file, via any medium is strictly prohibited.
 *
 * Licensed under the Entgra Commercial License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://entgra.io/licenses/entgra-commercial/1.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.entgra.device.mgt.core.device.mgt.core.dao;

import io.entgra.device.mgt.core.device.mgt.common.type.event.mgt.DeviceTypeEvent;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * This class represents the key dao operations associated with persisting and retrieving
 * device type event related information.
 */
public interface DeviceTypeEventDAO {

    Response getDeviceTypeEventDefinitions(String deviceType, int tenantId) throws DeviceManagementDAOException;

    void updateDeviceTypeMetaWithEvents(String deviceType, int tenantId, List<DeviceTypeEvent> deviceTypeEvents)
            throws DeviceManagementDAOException;
}
