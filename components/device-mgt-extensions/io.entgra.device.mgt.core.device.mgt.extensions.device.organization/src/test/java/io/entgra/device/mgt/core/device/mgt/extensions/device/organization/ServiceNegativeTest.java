package io.entgra.device.mgt.core.device.mgt.extensions.device.organization;

import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceOrganization;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.DeviceOrganizationMgtPluginException;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.impl.DeviceOrganizationServiceImpl;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.mock.BaseDeviceOrganizationTest;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.spi.DeviceOrganizationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.Date;

public class ServiceNegativeTest extends BaseDeviceOrganizationTest {

    private static final Log log = LogFactory.getLog(ServiceNegativeTest.class);

    private DeviceOrganizationService deviceOrganizationService;

    @BeforeClass
    public void init() {
        deviceOrganizationService = new DeviceOrganizationServiceImpl();
        log.info("Service test initialized");
    }

    @Test(description = "This method tests Add Device Organization method under negative circumstances with null data")

    public void testAddDeviceOrganization() throws DeviceOrganizationMgtPluginException {

        DeviceOrganization deviceOrganization = new DeviceOrganization() {
        };
        boolean result = deviceOrganizationService.addDeviceOrganization(deviceOrganization);
    }

//    @Test(description = "This method tests Update Device Organization method under negative circumstances with " +
//            "invalid data")
//
//    public void testUpdateDeviceOrganization() throws DeviceOrganizationMgtPluginException {
//        DeviceOrganization deviceOrganization = new DeviceOrganization() {
//        };
//        deviceOrganization.setOrganizationId(2);
//        deviceOrganization.setDeviceId(7);
//        deviceOrganization.setParentDeviceId(6);
//        boolean result = deviceOrganizationService.updateDeviceOrganization(deviceOrganization);
//    }
}
