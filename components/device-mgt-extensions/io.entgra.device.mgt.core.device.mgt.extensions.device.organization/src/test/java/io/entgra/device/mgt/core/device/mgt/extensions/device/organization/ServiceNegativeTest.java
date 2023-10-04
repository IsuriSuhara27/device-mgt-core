package io.entgra.device.mgt.core.device.mgt.extensions.device.organization;

import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceNode;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceOrganization;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.BadRequestException;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.DeviceOrganizationMgtPluginException;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.impl.DeviceOrganizationServiceImpl;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.mock.BaseDeviceOrganizationTest;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.spi.DeviceOrganizationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ServiceNegativeTest extends BaseDeviceOrganizationTest {

    private static final Log log = LogFactory.getLog(ServiceNegativeTest.class);

    private DeviceOrganizationService deviceOrganizationService;

    @BeforeClass
    public void init() {
        deviceOrganizationService = new DeviceOrganizationServiceImpl();
        log.info("Service test initialized");
    }

    @Test(description = "This method tests Get Children Of method under negative circumstances with null data",
            expectedExceptions = {DeviceOrganizationMgtPluginException.class})
    public void testGetChildrenOfWithInvalidInput() throws DeviceOrganizationMgtPluginException {
        DeviceNode invalidNode = null;
        int maxDepth = -1;
        boolean includeDevice = true;
        deviceOrganizationService.getChildrenOfDeviceNode(invalidNode, maxDepth, includeDevice);
    }

    @Test(description = "This method tests Get Children Of method under negative circumstances with an invalid DeviceNode",
            expectedExceptions = {DeviceOrganizationMgtPluginException.class})
    public void testGetChildrenOfWithInvalidDeviceNode() throws DeviceOrganizationMgtPluginException {
        DeviceNode invalidNode = new DeviceNode(); // Provide an invalid DeviceNode
        int maxDepth = 2;
        boolean includeDevice = true;
        deviceOrganizationService.getChildrenOfDeviceNode(invalidNode, maxDepth, includeDevice);
    }

    @Test(description = "This method tests Get Parents Of method under negative circumstances with null data",
            expectedExceptions = {DeviceOrganizationMgtPluginException.class})
    public void testGetParentsOfWithInvalidInput() throws DeviceOrganizationMgtPluginException {
        DeviceNode invalidNode = null;
        int maxDepth = -1;
        boolean includeDevice = true;
        deviceOrganizationService.getParentsOfDeviceNode(invalidNode, maxDepth, includeDevice);
    }

    @Test(description = "This method tests Get Parents Of method under negative circumstances with an invalid DeviceNode"
            , expectedExceptions = {DeviceOrganizationMgtPluginException.class})
    public void testGetParentsOfWithInvalidDeviceNode() throws DeviceOrganizationMgtPluginException {
        DeviceNode invalidNode = new DeviceNode(); // Provide an invalid DeviceNode
        int maxDepth = 2;
        boolean includeDevice = true;
        deviceOrganizationService.getParentsOfDeviceNode(invalidNode, maxDepth, includeDevice);
    }

    @Test(description = "This method tests Get Parents Of method under negative circumstances with an invalid DeviceNode"
            , expectedExceptions = {DeviceOrganizationMgtPluginException.class}
    )
    public void testGetParentsOfWithNullDeviceNode() throws DeviceOrganizationMgtPluginException {
        DeviceNode invalidNode = null; // Provide an invalid DeviceNode
        int maxDepth = 2;
        boolean includeDevice = true;
        deviceOrganizationService.getParentsOfDeviceNode(invalidNode, maxDepth, includeDevice);
    }


    @Test(description = "This method tests Add Device Organization method under negative circumstances with null data",
            expectedExceptions = {DeviceOrganizationMgtPluginException.class})
    public void testAddDeviceOrganizationWithInvalidInput() throws DeviceOrganizationMgtPluginException {
        DeviceOrganization invalidOrganization = new DeviceOrganization();
        deviceOrganizationService.addDeviceOrganization(invalidOrganization);
    }

    @Test(description = "This method tests isDeviceOrganizationExist method under negative circumstances with an organization that doesn't exist")
    public void testOrganizationDoesNotExist() throws DeviceOrganizationMgtPluginException {
        int nonExistentDeviceId = 9999; // An ID that doesn't exist
        int nonExistentParentDeviceId = 8888; // An ID that doesn't exist
        boolean exists = deviceOrganizationService.isDeviceOrganizationExist(nonExistentDeviceId, nonExistentParentDeviceId);
        Assert.assertFalse(exists, "Organization should not exist for non-existent IDs.");
    }

    @Test(description = "This method tests Exception Handling when adding a duplicate Device Organization",
            expectedExceptions = {DeviceOrganizationMgtPluginException.class})
    public void testAddDuplicateDeviceOrganization() throws DeviceOrganizationMgtPluginException {
        // Create a valid organization
        DeviceOrganization validOrganization = new DeviceOrganization();
        validOrganization.setDeviceId(1);
        validOrganization.setParentDeviceId(0);

        try {
            // Add the organization once
            deviceOrganizationService.addDeviceOrganization(validOrganization);

            // Attempt to add the same organization again, which should throw an exception
            deviceOrganizationService.addDeviceOrganization(validOrganization);
        } finally {
            // Clean up: Delete the added organization if it was successfully added to avoid conflicts in future tests
            deviceOrganizationService.deleteDeviceAssociations(validOrganization.getDeviceId());
        }
    }

    @Test(description = "This method tests Update Device Organization method under negative circumstances with null " +
            "data", expectedExceptions = {DeviceOrganizationMgtPluginException.class})
    public void testUpdateDeviceOrganizationWithInvalidInput() throws DeviceOrganizationMgtPluginException {
        DeviceOrganization invalidOrganization = new DeviceOrganization();
        deviceOrganizationService.updateDeviceOrganization(invalidOrganization);
    }

    @Test(description = "This method tests Update Device Organization method under negative circumstances with an invalid organization ID",
            expectedExceptions = {DeviceOrganizationMgtPluginException.class})
    public void testUpdateDeviceOrganizationWithInvalidID() throws DeviceOrganizationMgtPluginException {
        DeviceOrganization invalidOrganization = new DeviceOrganization();
        invalidOrganization.setOrganizationId(-1); // Provide an invalid organization ID
        deviceOrganizationService.updateDeviceOrganization(invalidOrganization);
    }


    @Test(description = "This method tests Get Device Organization By ID method under negative circumstances with " +
            "invalid input",
            expectedExceptions = {DeviceOrganizationMgtPluginException.class})
    public void testGetDeviceOrganizationByIDWithInvalidInput() throws DeviceOrganizationMgtPluginException {
        int invalidOrganizationId = 0;
        deviceOrganizationService.getDeviceOrganizationByID(invalidOrganizationId);
    }

    @Test(description = "This method tests Delete Device Organization By ID method under negative circumstances with " +
            "invalid input", expectedExceptions = {DeviceOrganizationMgtPluginException.class})
    public void testDeleteDeviceOrganizationByIDWithInvalidInput() throws DeviceOrganizationMgtPluginException {
        int invalidOrganizationId = 0;
        deviceOrganizationService.deleteDeviceOrganizationByID(invalidOrganizationId);
    }

    @Test(description = "This method tests Does Device ID Exist method under negative circumstances with invalid input",
            expectedExceptions = {BadRequestException.class})
    public void testDoesDeviceIdExistWithInvalidInput() throws DeviceOrganizationMgtPluginException {
        int invalidDeviceId = 0;
        deviceOrganizationService.isDeviceIdExist(invalidDeviceId);
    }

    @Test(description = "This method tests Delete Device Associations method under negative circumstances with invalid " +
            "input", expectedExceptions = {BadRequestException.class})
    public void testDeleteDeviceAssociationsWithInvalidInput() throws DeviceOrganizationMgtPluginException {
        int invalidDeviceId = 0;
        deviceOrganizationService.deleteDeviceAssociations(invalidDeviceId);
    }

}
