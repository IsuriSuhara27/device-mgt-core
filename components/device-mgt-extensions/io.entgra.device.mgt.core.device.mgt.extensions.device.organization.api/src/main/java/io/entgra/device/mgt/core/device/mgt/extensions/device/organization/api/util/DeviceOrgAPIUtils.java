package io.entgra.device.mgt.core.device.mgt.extensions.device.organization.api.util;


import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.spi.DeviceOrganizationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;

/**
 * DeviceOrgAPIUtils class provides utility function used by Device Organization REST - API classes.
 */
public class DeviceOrgAPIUtils {
    private static DeviceOrganizationService deviceOrganizationService;

    private static final Log log = LogFactory.getLog(DeviceOrgAPIUtils.class);

    /**
     * Initializing and accessing method for DeviceOrganizationService.
     *
     * @return DeviceOrganizationService instance
     * @throws IllegalStateException if deviceOrganizationService cannot be initialized
     */
    public static DeviceOrganizationService getDeviceOrganizationService() {
        if (deviceOrganizationService == null) {
            synchronized (DeviceOrgAPIUtils.class) {
                if (deviceOrganizationService == null) {
                    PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                    deviceOrganizationService = (DeviceOrganizationService) ctx.getOSGiService(
                            DeviceOrganizationService.class, null);
                    if (deviceOrganizationService == null) {
                        throw new IllegalStateException("Device Organization Management service not initialized.");
                    }
                }
            }
        }
        return deviceOrganizationService;
    }


}
