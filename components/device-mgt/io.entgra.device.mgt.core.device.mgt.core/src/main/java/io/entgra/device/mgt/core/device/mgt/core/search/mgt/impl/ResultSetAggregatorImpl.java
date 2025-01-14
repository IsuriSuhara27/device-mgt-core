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


package io.entgra.device.mgt.core.device.mgt.core.search.mgt.impl;

import io.entgra.device.mgt.core.device.mgt.common.Device;
import io.entgra.device.mgt.core.device.mgt.core.internal.DeviceManagementDataHolder;
import io.entgra.device.mgt.core.device.mgt.core.permission.mgt.PermissionUtils;
import io.entgra.device.mgt.core.device.mgt.core.search.mgt.Constants;
import io.entgra.device.mgt.core.device.mgt.core.search.mgt.ResultSetAggregator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultSetAggregatorImpl implements ResultSetAggregator {
    private static Log log = LogFactory.getLog(ResultSetAggregatorImpl.class);

    @Override
    public List<Device> aggregate(Map<String, List<Device>> devices) {
        Map<Integer, Device> generalQueryMap = this.convertToMap(devices.get(Constants.GENERAL));
        Map<Integer, Device> andMap = this.convertToMap(devices.get(Constants.PROP_AND));
        Map<Integer, Device> orMap = this.convertToMap(devices.get(Constants.PROP_OR));
        Map<Integer, Device> locationMap = this.convertToMap(devices.get(Constants.LOCATION));
        Map<Integer, Device> finalMap = new HashMap<>();
        List<Device> finalResult = new ArrayList<>();
        List<Device> ownDevices = new ArrayList<>();

        if (andMap.isEmpty()) {
            finalMap = generalQueryMap;
            finalResult = this.convertDeviceMapToList(generalQueryMap);
        } else {
            for (Integer a : andMap.keySet()) {
                if (generalQueryMap.isEmpty()) {
                    finalResult.add(andMap.get(a));
                    finalMap.put(a, andMap.get(a));
                } else if (generalQueryMap.containsKey(a)) {
                    if (!finalMap.containsKey(a)) {
                        finalResult.add(andMap.get(a));
                        finalMap.put(a, andMap.get(a));
                    }
                }
            }
        }
        for (Integer a : orMap.keySet()) {
            if (!finalMap.containsKey(a)) {
                finalResult.add(orMap.get(a));
                finalMap.put(a, orMap.get(a));
            }
        }

        for (Integer a : locationMap.keySet()) {
            if (!finalMap.containsKey(a)) {
                finalResult.add(locationMap.get(a));
                finalMap.put(a, locationMap.get(a));
            }
        }

        String username = PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername();

        try {
            if (isPermittedToViewAnyDevice(username)) {
                return finalResult;
            }
        } catch (UserStoreException e) {
            log.error("Unable to check permissions of the user: " + username, e);
        }

        for (Device device: finalResult) {
            if (username.equals(device.getEnrolmentInfo().getOwner())) {
                ownDevices.add(device);
            }
        }

        return ownDevices;
    }

    private Map<Integer, Device> convertToMap(List<Device> devices) {
        if (devices == null) {
            return null;
        }
        Map<Integer, Device> deviceWrapperMap = new HashMap<>();
        for (Device device : devices) {
            deviceWrapperMap.put(device.getEnrolmentInfo().getId(), device);
        }
        return deviceWrapperMap;
    }

    private List<Device> convertDeviceMapToList(Map<Integer, Device> map) {
        List<Device> list = new ArrayList<>();
        for (Integer a : map.keySet()) {
            list.add(map.get(a));
        }
        return list;
    }

    /**
     * Checks if the user has permissions to view all devices.
     *
     * @param username username
     * @return {@code true} if user is permitted
     * @throws UserStoreException If unable to check user permissions
     */
    private boolean isPermittedToViewAnyDevice(String username) throws UserStoreException {
        int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId(true);
        UserRealm userRealm = DeviceManagementDataHolder.getInstance().getRealmService().getTenantUserRealm(tenantId);
        return userRealm != null && userRealm.getAuthorizationManager() != null &&
                userRealm.getAuthorizationManager().isUserAuthorized(username,
                        PermissionUtils.getAbsolutePermissionPath(Constants.ANY_DEVICE_PERMISSION), 
                        Constants.UI_EXECUTE);
    }

}
