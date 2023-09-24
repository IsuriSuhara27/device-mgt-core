package io.entgra.device.mgt.core.device.mgt.extensions.device.organization;

import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceNode;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceOrganization;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.DeviceOrganizationMgtPluginException;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.impl.DeviceOrganizationServiceImpl;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.mock.BaseDeviceOrganizationTest;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.spi.DeviceOrganizationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

public class ServiceTest extends BaseDeviceOrganizationTest {

    private static final Log log = LogFactory.getLog(ServiceTest.class);

    private DeviceOrganizationService deviceOrganizationService;

    @BeforeClass
    public void init() {
        deviceOrganizationService = new DeviceOrganizationServiceImpl();
        log.info("Service test initialized");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testGetChildrenOf() throws DeviceOrganizationMgtPluginException {

        DeviceNode deviceNode = new DeviceNode();
        deviceNode.setDeviceId(2);
        List<DeviceNode> childrenList = deviceOrganizationService.getChildrenOf(deviceNode, 2, true);

        Assert.assertNotNull(childrenList, "Cannot be null");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testGetParentsOf() throws DeviceOrganizationMgtPluginException {

        DeviceNode deviceNode = new DeviceNode();
        deviceNode.setDeviceId(4);
        List<DeviceNode> parentList = deviceOrganizationService.getParentsOf(deviceNode, 2, true);

        Assert.assertNotNull(parentList, "Cannot be null");
    }

    @Test
    public void testAddDeviceOrganization() throws DeviceOrganizationMgtPluginException {


        DeviceOrganization deviceOrganization = new DeviceOrganization() {
        };
        deviceOrganization.setDeviceId(4);
        deviceOrganization.setParentDeviceId(3);
        deviceOrganization.setUpdateTime(new Date(System.currentTimeMillis()));
        boolean result = deviceOrganizationService.addDeviceOrganization(deviceOrganization);
        DeviceOrganization deviceOrganization1 = new DeviceOrganization() {
        };
        deviceOrganization.setDeviceId(3);
        deviceOrganization.setParentDeviceId(2);
        deviceOrganization.setUpdateTime(new Date(System.currentTimeMillis()));
        boolean result1 = deviceOrganizationService.addDeviceOrganization(deviceOrganization);
        DeviceOrganization deviceOrganization2 = new DeviceOrganization() {
        };
        deviceOrganization.setDeviceId(4);
        deviceOrganization.setParentDeviceId(2);
        deviceOrganization.setUpdateTime(new Date(System.currentTimeMillis()));
        boolean result2 = deviceOrganizationService.addDeviceOrganization(deviceOrganization);

        Assert.assertNotNull(result, "Cannot be null");

    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testUpdateDeviceOrganization() throws DeviceOrganizationMgtPluginException {

        DeviceOrganization deviceOrganization = new DeviceOrganization() {
        };
        deviceOrganization.setDeviceId(4);
        deviceOrganization.setParentDeviceId(3);
        deviceOrganization.setOrganizationId(1);
        boolean result = deviceOrganizationService.updateDeviceOrganization(deviceOrganization);

        Assert.assertNotNull(result, "Cannot be null");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testGetDeviceOrganizationByID() throws DeviceOrganizationMgtPluginException {

        DeviceOrganization deviceOrganization = deviceOrganizationService.getDeviceOrganizationByID(5);
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testDoesDeviceIdExist() throws DeviceOrganizationMgtPluginException {

        boolean deviceIdExist =  deviceOrganizationService.doesDeviceIdExist(1);

        Assert.assertNotNull(deviceIdExist, "Cannot be null");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testDeleteDeviceOrganizationByID() throws DeviceOrganizationMgtPluginException {
        boolean result = deviceOrganizationService.deleteDeviceOrganizationByID(5);
        Assert.assertNotNull(result, "Cannot be null");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void deleteDeviceAssociations() throws DeviceOrganizationMgtPluginException {
        boolean result = deviceOrganizationService.deleteDeviceAssociations(1);
        Assert.assertNotNull(result, "Cannot be null");
    }
}
