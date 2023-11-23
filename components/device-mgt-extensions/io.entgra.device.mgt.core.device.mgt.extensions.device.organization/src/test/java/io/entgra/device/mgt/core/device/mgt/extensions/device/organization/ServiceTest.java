package io.entgra.device.mgt.core.device.mgt.extensions.device.organization;

import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceNode;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceOrganization;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.PaginationRequest;
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

    @Test(dependsOnMethods = "testAddMultipleDeviceOrganizations")
    public void testGetChildrenOf() throws DeviceOrganizationMgtPluginException {
        boolean exists = deviceOrganizationService.isDeviceIdExist(17);
        if (exists) {
            DeviceNode deviceNode = new DeviceNode();
            deviceNode.setDeviceId(17);
            int maxDepth = 10;
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
    public void testAddDeviceOrganizationWithoutMetaData() throws DeviceOrganizationMgtPluginException {

        DeviceOrganization deviceOrganizationOne = new DeviceOrganization();
        deviceOrganizationOne.setDeviceId(4);
        deviceOrganizationOne.setParentDeviceId(null);

        boolean result1 = deviceOrganizationService.addDeviceOrganization(deviceOrganizationOne);
        Assert.assertTrue(result1);
        DeviceOrganization organization1 = deviceOrganizationService.getDeviceOrganizationByUniqueKey(4, null);

        Assert.assertNotNull(organization1);

    }

    @Test()
    public void testAddDeviceOrganizationWithNullParent() throws DeviceOrganizationMgtPluginException {

        DeviceOrganization deviceOrganizationOne = new DeviceOrganization();
        deviceOrganizationOne.setDeviceId(3);
        deviceOrganizationOne.setParentDeviceId(null);
        deviceOrganizationOne.setDeviceOrganizationMeta("Physical Relationship");

        boolean result1 = deviceOrganizationService.addDeviceOrganization(deviceOrganizationOne);
        Assert.assertTrue(result1);
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
        DeviceOrganization organization = deviceOrganizationService.getDeviceOrganizationByUniqueKey(3, 4);

        Assert.assertTrue(result1 || organization != null);

    }

    @Test()
    public void testAddDeviceOrganizationForDelete() throws DeviceOrganizationMgtPluginException {

        DeviceOrganization deviceOrganizationOne = new DeviceOrganization();
        deviceOrganizationOne.setDeviceId(2);
        deviceOrganizationOne.setParentDeviceId(null);
        deviceOrganizationOne.setDeviceOrganizationMeta("Physical Relationship");

        boolean result1 = deviceOrganizationService.addDeviceOrganization(deviceOrganizationOne);
        DeviceOrganization organization = deviceOrganizationService.getDeviceOrganizationByUniqueKey(2, null);

        Assert.assertTrue(result1 || organization != null);

    }

    @Test()
    public void testAddDeviceOrganizationForDeleteAssociations() throws DeviceOrganizationMgtPluginException {

        DeviceOrganization deviceOrganizationOne = new DeviceOrganization();
        deviceOrganizationOne.setDeviceId(1);
        deviceOrganizationOne.setParentDeviceId(null);
        deviceOrganizationOne.setDeviceOrganizationMeta("Physical Relationship");

        boolean result1 = deviceOrganizationService.addDeviceOrganization(deviceOrganizationOne);
        DeviceOrganization organization = deviceOrganizationService.getDeviceOrganizationByUniqueKey(1, null);

        Assert.assertTrue(result1 || organization != null);

    }

    @Test()
    public void testAddMultipleDeviceOrganizations() throws DeviceOrganizationMgtPluginException {
        DeviceOrganizationService deviceOrganizationService = new DeviceOrganizationServiceImpl();

        int[] deviceIds = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};

        // Define specific combinations of deviceID and parentDeviceID
        int[][] combinations = {
                {20, 19}, {19, 18}, {18, 17}, {20, 5}, {20, 17}, {19, 16}, {17, 16}, {16, 17}
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

    @Test(dependsOnMethods = "testAddDeviceOrganizationWithNullParent")
    public void testUpdateDeviceOrganizationWithSameData() throws DeviceOrganizationMgtPluginException {
        DeviceOrganization deviceOrganization = new DeviceOrganization();
        deviceOrganization.setDeviceId(3);
        deviceOrganization.setParentDeviceId(null);
        deviceOrganization.setOrganizationId(1);

        DeviceOrganization organization = deviceOrganizationService.getDeviceOrganizationByID(1);
        if (organization != null) {
            boolean result = deviceOrganizationService.updateDeviceOrganization(deviceOrganization);
            Assert.assertTrue(result);
        }
    }


    @Test(dependsOnMethods = "testAddDeviceOrganizationWithNullParent")
    public void testUpdateDeviceOrganizationWithDifferentData() throws DeviceOrganizationMgtPluginException {
        DeviceOrganization deviceOrganization = new DeviceOrganization();
        deviceOrganization.setDeviceId(3);
        deviceOrganization.setParentDeviceId(4);
        deviceOrganization.setOrganizationId(1);

        DeviceOrganization organization = deviceOrganizationService.getDeviceOrganizationByID(1);
        if (organization != null) {
            boolean result = deviceOrganizationService.updateDeviceOrganization(deviceOrganization);
            Assert.assertTrue(result);
        }
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testGetDeviceOrganizationByID() throws DeviceOrganizationMgtPluginException {
        int organizationID = 1;
        DeviceOrganization deviceOrganization = deviceOrganizationService.getDeviceOrganizationByID(organizationID);
        if (deviceOrganization != null) {
            log.info("In Device Organization with organizationID = " + organizationID
                    + ", deviceID = " + deviceOrganization.getDeviceId()
                    + ", ParentDeviceID = " + deviceOrganization.getParentDeviceId()
                    + ", Meta Data = " + deviceOrganization.getDeviceOrganizationMeta()
            );
        }
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testDoesDeviceIdExist() throws DeviceOrganizationMgtPluginException {
        boolean deviceIdExist = deviceOrganizationService.isDeviceIdExist(4);
        Assert.assertTrue(deviceIdExist);
    }

    @Test(dependsOnMethods = "testAddDeviceOrganizationForDelete")
    public void testDeleteDeviceOrganizationByID() throws DeviceOrganizationMgtPluginException {
        boolean rs = deviceOrganizationService.deleteDeviceOrganizationByID(1);

    }

    @Test(dependsOnMethods = "testAddDeviceOrganizationForDeleteAssociations")
    public void testDeleteDeviceAssociations() throws DeviceOrganizationMgtPluginException {
        boolean rs = deviceOrganizationService.deleteDeviceAssociations(1);
        Assert.assertTrue(rs);
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
        Assert.assertNotNull(organizations, "List of organizations cannot be null");
        Assert.assertFalse(organizations.isEmpty(), "List of organizations should not be empty");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganizationWithNullParent")
    public void testGetRootOrganizations() throws DeviceOrganizationMgtPluginException {
        int offset  = 0;
        int limit = 10;
        PaginationRequest request = new PaginationRequest(offset, limit);
        List<DeviceOrganization> organizations = deviceOrganizationService.getDeviceOrganizationRoots(request);
        for (DeviceOrganization organization : organizations) {
            log.info("organizationID = " + organization.getOrganizationId());
            log.info("deviceID = " + organization.getDeviceId());
            log.info("parentDeviceID = " + organization.getParentDeviceId());
            log.info("updateTime = " + organization.getUpdateTime());
            log.info("----------------------------------------------");
        }
        Assert.assertNotNull(organizations, "List of organizations cannot be null");
        Assert.assertFalse(organizations.isEmpty(), "List of organizations should not be empty");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganizationWithNullParent")
    public void testGetLeafOrganizationsWithNullParents() throws DeviceOrganizationMgtPluginException {
        int offset  = 0;
        int limit = 10;
        PaginationRequest request = new PaginationRequest(offset, limit);
        List<DeviceOrganization> organizations = deviceOrganizationService.getDeviceOrganizationLeafs(request);
        for (DeviceOrganization organization : organizations) {
            log.info("organizationID = " + organization.getOrganizationId());
            log.info("deviceID = " + organization.getDeviceId());
            log.info("parentDeviceID = " + organization.getParentDeviceId());
            log.info("updateTime = " + organization.getUpdateTime());
            log.info("----------------------------------------------");
        }
        Assert.assertTrue(organizations.isEmpty(), "List of organizations should not be empty");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testGetLeafOrganizations() throws DeviceOrganizationMgtPluginException {
        int offset  = 0;
        int limit = 10;
        PaginationRequest request = new PaginationRequest(offset, limit);
        List<DeviceOrganization> organizations = deviceOrganizationService.getDeviceOrganizationLeafs(request);
        for (DeviceOrganization organization : organizations) {
            log.info("organizationID = " + organization.getOrganizationId());
            log.info("deviceID = " + organization.getDeviceId());
            log.info("parentDeviceID = " + organization.getParentDeviceId());
            log.info("updateTime = " + organization.getUpdateTime());
            log.info("----------------------------------------------");
        }
        Assert.assertNotNull(organizations, "List of organizations cannot be null");
    }

    @Test(dependsOnMethods = "testAddMultipleDeviceOrganizations")
    public void testGetDeviceOrganizationByUniqueKey() throws DeviceOrganizationMgtPluginException {
        int deviceID = 20;
        int parentDeviceID = 19;

        DeviceOrganization organization = deviceOrganizationService.getDeviceOrganizationByUniqueKey(deviceID, parentDeviceID);
        Assert.assertNotNull(organization, "Organization should not be null");
        Assert.assertEquals(organization.getDeviceId(), deviceID, "Device ID should match");
        Assert.assertEquals(organization.getParentDeviceId().intValue(), parentDeviceID, "Parent Device ID should match");
    }

}
