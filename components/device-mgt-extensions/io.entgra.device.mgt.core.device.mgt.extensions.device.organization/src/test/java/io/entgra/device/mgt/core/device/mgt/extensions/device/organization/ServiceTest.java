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

import java.util.List;

public class ServiceTest extends BaseDeviceOrganizationTest {

    private static final Log log = LogFactory.getLog(ServiceTest.class);

    private DeviceOrganizationService deviceOrganizationService;

    @BeforeClass
    public void init() {
        deviceOrganizationService = new DeviceOrganizationServiceImpl();
        log.info("Service test initialized");
    }

    @Test(priority = 4, dependsOnMethods = "testAddDeviceOrganization")
    public void testGetChildrenOf() throws DeviceOrganizationMgtPluginException {

        DeviceNode deviceNode = new DeviceNode();
        deviceNode.setDeviceId(2);
        int maxDepth = 2;
        boolean includeDevice = false;
        List<DeviceNode> childrenList = deviceOrganizationService.getChildrenOf(deviceNode, maxDepth, includeDevice);

        Assert.assertNotNull(childrenList, "Cannot be null");
    }

    @Test(priority = 5, dependsOnMethods = "testAddDeviceOrganization")
    public void testGetParentsOf() throws DeviceOrganizationMgtPluginException {

        DeviceNode deviceNode = new DeviceNode();
        deviceNode.setDeviceId(4);
        int maxDepth = 2;
        boolean includeDevice = true;
        List<DeviceNode> parentList = deviceOrganizationService.getParentsOf(deviceNode, maxDepth, includeDevice);

        Assert.assertNotNull(parentList, "Cannot be null");
    }

    @Test(priority = 1)
    public void testAddDeviceOrganization() throws DeviceOrganizationMgtPluginException {

        DeviceOrganization deviceOrganization = new DeviceOrganization() {
        };
        deviceOrganization.setDeviceId(4);
        deviceOrganization.setParentDeviceId(3);
        DeviceOrganization deviceOrganizationOne = new DeviceOrganization() {
        };
        deviceOrganizationOne.setDeviceId(3);
        deviceOrganizationOne.setParentDeviceId(2);
        DeviceOrganization deviceOrganizationTwo = new DeviceOrganization() {
        };
        deviceOrganizationTwo.setDeviceId(4);
        deviceOrganizationTwo.setParentDeviceId(2);

        boolean result = deviceOrganizationService.addDeviceOrganization(deviceOrganization);
        boolean result1 = deviceOrganizationService.addDeviceOrganization(deviceOrganizationOne);
        boolean result2 = deviceOrganizationService.addDeviceOrganization(deviceOrganizationTwo);
        Assert.assertTrue(result);
        Assert.assertTrue(result1);
        Assert.assertTrue(result2);

    }

    @Test(priority = 6, dependsOnMethods = "testAddDeviceOrganization")
    public void testUpdateDeviceOrganization() throws DeviceOrganizationMgtPluginException {
        DeviceOrganization deviceOrganization = new DeviceOrganization() {
        };
        deviceOrganization.setDeviceId(4);
        deviceOrganization.setParentDeviceId(3);
        deviceOrganization.setOrganizationId(1);
        boolean result = deviceOrganizationService.updateDeviceOrganization(deviceOrganization);
    }

    @Test(priority = 2, dependsOnMethods = "testAddDeviceOrganization")
    public void testGetDeviceOrganizationByID() throws DeviceOrganizationMgtPluginException {

        DeviceOrganization deviceOrganization = deviceOrganizationService.getDeviceOrganizationByID(1);
    }

    @Test(priority = 3, dependsOnMethods = "testAddDeviceOrganization")
    public void testDoesDeviceIdExist() throws DeviceOrganizationMgtPluginException {
        boolean deviceIdExist = deviceOrganizationService.doesDeviceIdExist(4);
        Assert.assertTrue(deviceIdExist);
    }

    @Test(priority = 7, dependsOnMethods = "testAddDeviceOrganization")
    public void testDeleteDeviceOrganizationByID() throws DeviceOrganizationMgtPluginException {
        boolean rs = deviceOrganizationService.deleteDeviceOrganizationByID(1);
    }

    @Test(priority = 8, dependsOnMethods = "testAddDeviceOrganization")
    public void testDeleteDeviceAssociations() throws DeviceOrganizationMgtPluginException {
        boolean rs = deviceOrganizationService.deleteDeviceAssociations(4);
        Assert.assertTrue(rs);
    }
    @Test(priority = 9, dependsOnMethods = "testAddDeviceOrganization")
    public void testGetAllOrganizations() throws DeviceOrganizationMgtPluginException {
        List<DeviceOrganization> organizations = deviceOrganizationService.getAllDeviceOrganizations();
        for (DeviceOrganization organization : organizations) {
            log.info("organizationID = "+organization.getOrganizationId());
            log.info("deviceID = "+organization.getDeviceId());
            log.info("parentDeviceID = "+organization.getParentDeviceId());
            log.info("updateTime = "+organization.getUpdateTime());
            log.info("----------------------------------------------");
        }
        Assert.assertNotNull(organizations, "List of organizations cannot be null");
        Assert.assertFalse(organizations.isEmpty(), "List of organizations should not be empty");
    }

}
