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
package io.entgra.device.mgt.core.device.mgt.extensions.push.notification.provider.mqtt.internal;

import io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import org.wso2.carbon.event.output.adapter.core.OutputEventAdapterService;

@Component(
        name = "io.entgra.device.mgt.core.device.mgt.extensions.push.notification.provider.mqtt.internal.MQTTPushNotificationServiceComponent",
        immediate = true)
public class MQTTPushNotificationServiceComponent {

    private static final Log log = LogFactory.getLog(MQTTPushNotificationServiceComponent.class);

    @SuppressWarnings("unused")
    @Activate
    protected void activate(ComponentContext componentContext) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Initializing MQTT based push notification provider implementation bundle");
            }
            //Do nothing
            if (log.isDebugEnabled()) {
                log.debug("MQTT based push notification provider implementation bundle has been successfully " +
                        "initialized");
            }
        } catch (Throwable e) {
            log.error("Error occurred while initializing MQTT based push notification provider " +
                    "implementation bundle", e);
        }
    }
    @Deactivate
    protected void deactivate(ComponentContext componentContext) {
        //Do nothing
    }

    @Reference(
            name = "device.mgt.provider.service",
            service = io.entgra.device.mgt.core.device.mgt.core.service.DeviceManagementProviderService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetDeviceManagementProviderService")
    protected void setDeviceManagementProviderService(
            DeviceManagementProviderService deviceManagementProviderService) {
        MQTTDataHolder.getInstance().setDeviceManagementProviderService(deviceManagementProviderService);
    }

    protected void unsetDeviceManagementProviderService(
            DeviceManagementProviderService deviceManagementProviderService) {
        MQTTDataHolder.getInstance().setDeviceManagementProviderService(deviceManagementProviderService);
    }

    @Reference(
            name = "output.event.adaptor.service",
            service = org.wso2.carbon.event.output.adapter.core.OutputEventAdapterService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetOutputEventAdapterService")
    protected void setOutputEventAdapterService(OutputEventAdapterService outputEventAdapterService){
        MQTTDataHolder.getInstance().setOutputEventAdapterService(outputEventAdapterService);
    }

    protected void unsetOutputEventAdapterService(OutputEventAdapterService outputEventAdapterService){
        MQTTDataHolder.getInstance().setOutputEventAdapterService(null);
    }

}
