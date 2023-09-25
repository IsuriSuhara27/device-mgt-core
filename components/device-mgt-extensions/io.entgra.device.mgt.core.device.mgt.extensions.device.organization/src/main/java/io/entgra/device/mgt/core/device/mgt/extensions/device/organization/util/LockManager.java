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
package io.entgra.device.mgt.core.device.mgt.extensions.device.organization.util;

import java.util.HashMap;
import java.util.Map;

/**
 * This class handle locks to manage concurrency issues when adding device organization
 */
public class LockManager {
    private static final LockManager INSTANCE = new LockManager();
    private final Map<String, Object> lockMap = new HashMap<>();

    private LockManager() {
    }

    public static LockManager getInstance() {
        return INSTANCE;
    }

    public Object getLock(int deviceId, int parentDeviceId) {
        String lockKey = deviceId + "-" + parentDeviceId;
        synchronized (lockMap) {
            if (!lockMap.containsKey(lockKey)) {
                lockMap.put(lockKey, new Object());
            }
            return lockMap.get(lockKey);
        }
    }
}

