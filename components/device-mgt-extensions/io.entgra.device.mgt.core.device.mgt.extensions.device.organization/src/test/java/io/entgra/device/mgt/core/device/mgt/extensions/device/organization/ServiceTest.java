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
import java.util.Random;

public class ServiceTest extends BaseDeviceOrganizationTest {

    private static final Log log = LogFactory.getLog(ServiceTest.class);

    private DeviceOrganizationService deviceOrganizationService;

    @BeforeClass
    public void init() {
        deviceOrganizationService = new DeviceOrganizationServiceImpl();
        log.info("Service test initialized");
    }

    @Test(dependsOnMethods = "testAddMultipleDeviceOrganizations")
    public void testGetChildrenOf() throws DeviceOrganizationMgtPluginException {
        boolean exists = deviceOrganizationService.isDeviceIdExist(17);
        if (exists){
            DeviceNode deviceNode = new DeviceNode();
            deviceNode.setDeviceId(17);
            int maxDepth = 2;
            boolean includeDevice = true;
            List<DeviceNode> childrenList = deviceOrganizationService.getChildrenOfDeviceNode(deviceNode, maxDepth, includeDevice);

            Assert.assertNotNull(childrenList, "Cannot be null");
        }
    }

    @Test(dependsOnMethods = "testAddMultipleDeviceOrganizations")
    public void testGetParentsOf() throws DeviceOrganizationMgtPluginException {
        boolean exists = deviceOrganizationService.isChildDeviceIdExist(20);
        if (exists) {
            DeviceNode deviceNode = new DeviceNode();
            deviceNode.setDeviceId(20);
            int maxDepth = 3;
            boolean includeDevice = false;
            List<DeviceNode> parentList = deviceOrganizationService.getParentsOfDeviceNode(deviceNode, maxDepth, includeDevice);

            Assert.assertNotNull(parentList, "Cannot be null");
        }
    }

    @Test()
    public void testAddDeviceOrganizationWithNullParent() throws DeviceOrganizationMgtPluginException {

        DeviceOrganization deviceOrganizationOne = new DeviceOrganization();
        deviceOrganizationOne.setDeviceId(3);
        deviceOrganizationOne.setParentDeviceId(null);
        deviceOrganizationOne.setDeviceOrganizationMeta("Physical Relationship");

        boolean result1 = deviceOrganizationService.addDeviceOrganization(deviceOrganizationOne);
        DeviceOrganization organization1 = deviceOrganizationService.getDeviceOrganizationByUniqueKey(3, null);

        Assert.assertNotNull(organization1);

    }
    @Test()
    public void testAddDeviceOrganization() throws DeviceOrganizationMgtPluginException {

        DeviceOrganization deviceOrganizationOne = new DeviceOrganization();
        deviceOrganizationOne.setDeviceId(3);
        deviceOrganizationOne.setParentDeviceId(4);
        deviceOrganizationOne.setDeviceOrganizationMeta("Physical Relationship");

        boolean result1 = deviceOrganizationService.addDeviceOrganization(deviceOrganizationOne);
        DeviceOrganization organization1 = deviceOrganizationService.getDeviceOrganizationByUniqueKey(3, 4);

        Assert.assertNotNull(organization1);

    }

    @Test()
    public void testAddMultipleRandomDeviceOrganizations() throws DeviceOrganizationMgtPluginException {
        DeviceOrganizationService deviceOrganizationService = new DeviceOrganizationServiceImpl();

        int[] deviceIds = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};

        // Initialize counters for tracking the number of organizations and iterations
        int organizationCount = 0;
        int iterations = 0;

        while (organizationCount < 100 && iterations < 1000) {
            // Randomly select two different device IDs from the array
            int parentDeviceId = deviceIds[new Random().nextInt(deviceIds.length)];
            int childDeviceId = deviceIds[new Random().nextInt(deviceIds.length)];

            // Check if the selected device IDs are different
            if (parentDeviceId != childDeviceId) {
                DeviceOrganization organization = new DeviceOrganization();
                organization.setDeviceId(childDeviceId);
                organization.setParentDeviceId(parentDeviceId);

                boolean result = deviceOrganizationService.addDeviceOrganization(organization);

                // Optionally, add assertions to check the results if needed
                if (result) {
                    organizationCount++;
                }
            }

            iterations++;
        }

        Assert.assertEquals(organizationCount, 100, "Inserted 100 organizations");
    }

    @Test()
    public void testAddMultipleDeviceOrganizations() throws DeviceOrganizationMgtPluginException {
        DeviceOrganizationService deviceOrganizationService = new DeviceOrganizationServiceImpl();

        int[] deviceIds = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};

        // Define specific combinations of deviceID and parentDeviceID
        int[][] combinations = {
                {20, 19}, {19, 18}, {18, 17},{20,5},{20,17}
                // Add more combinations as needed
        };

        // Initialize counters for tracking the number of organizations and iterations
        int organizationCount = 0;
        int iterationCount = 0;

        // Iterate through the defined combinations
        for (int[] combination : combinations) {
            int childDeviceId = combination[0];
            int parentDeviceId = combination[1];

            DeviceOrganization organization = new DeviceOrganization();
            organization.setDeviceId(childDeviceId);
            organization.setParentDeviceId(parentDeviceId);

            boolean result = deviceOrganizationService.addDeviceOrganization(organization);

            // Optionally, add assertions to check the results if needed
            if (result) {
                organizationCount++;
            }

            iterationCount++;
        }

        // Optionally, you can assert that the correct number of organizations were inserted
        Assert.assertEquals(organizationCount, combinations.length, "Inserted organizations count mismatch");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testUpdateDeviceOrganization() throws DeviceOrganizationMgtPluginException {
        DeviceOrganization deviceOrganization = new DeviceOrganization();
        deviceOrganization.setDeviceId(4);
        deviceOrganization.setParentDeviceId(3);
        deviceOrganization.setOrganizationId(1);

        boolean result = deviceOrganizationService.updateDeviceOrganization(deviceOrganization);
//        DeviceOrganization organization = deviceOrganizationService.getDeviceOrganizationByUniqueKey(4, 3);
//        Assert.assertNotNull(organization);
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testGetDeviceOrganizationByID() throws DeviceOrganizationMgtPluginException {

        DeviceOrganization deviceOrganization = deviceOrganizationService.getDeviceOrganizationByID(1);
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testDoesDeviceIdExist() throws DeviceOrganizationMgtPluginException {
        boolean deviceIdExist = deviceOrganizationService.isDeviceIdExist(4);
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testDeleteDeviceOrganizationByID() throws DeviceOrganizationMgtPluginException {
        boolean rs = deviceOrganizationService.deleteDeviceOrganizationByID(1);
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testDeleteDeviceAssociations() throws DeviceOrganizationMgtPluginException {
        boolean rs = deviceOrganizationService.deleteDeviceAssociations(4);
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testGetAllOrganizations() throws DeviceOrganizationMgtPluginException {
        List<DeviceOrganization> organizations = deviceOrganizationService.getAllDeviceOrganizations();
        for (DeviceOrganization organization : organizations) {
            log.info("organizationID = " + organization.getOrganizationId());
            log.info("deviceID = " + organization.getDeviceId());
            log.info("parentDeviceID = " + organization.getParentDeviceId());
            log.info("updateTime = " + organization.getUpdateTime());
            log.info("----------------------------------------------");
        }
//        Assert.assertNotNull(organizations, "List of organizations cannot be null");
//        Assert.assertFalse(organizations.isEmpty(), "List of organizations should not be empty");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testGetDeviceOrganizationByUniqueKey() throws DeviceOrganizationMgtPluginException {
        int deviceID = 20;
        int parentDeviceID = 19;

        DeviceOrganization organization = deviceOrganizationService.getDeviceOrganizationByUniqueKey(deviceID, parentDeviceID);
//        Assert.assertNotNull(organization, "Organization should not be null");
//        Assert.assertEquals(organization.getDeviceId(), deviceID, "Device ID should match");
//        Assert.assertEquals(organization.getParentDeviceId().intValue(), parentDeviceID, "Parent Device ID should match");
    }

}
