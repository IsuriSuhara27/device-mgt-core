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

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testGetChildrenOf() throws DeviceOrganizationMgtPluginException {

        DeviceNode deviceNode = new DeviceNode();
        deviceNode.setDeviceId(2);
        int maxDepth = 2;
        boolean includeDevice = false;
        List<DeviceNode> childrenList = deviceOrganizationService.getChildrenOf(deviceNode, maxDepth, includeDevice);

        Assert.assertNotNull(childrenList, "Cannot be null");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testGetParentsOf() throws DeviceOrganizationMgtPluginException {

        DeviceNode deviceNode = new DeviceNode();
        deviceNode.setDeviceId(4);
        int maxDepth = 2;
        boolean includeDevice = true;
        List<DeviceNode> parentList = deviceOrganizationService.getParentsOf(deviceNode, maxDepth, includeDevice);

        Assert.assertNotNull(parentList, "Cannot be null");
    }

    @Test(
//            expectedExceptions = {DeviceOrganizationMgtPluginException.class}
    )
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

        try {
            boolean result = deviceOrganizationService.addDeviceOrganization(deviceOrganization);
            boolean result1 = deviceOrganizationService.addDeviceOrganization(deviceOrganizationOne);
            boolean result2 = deviceOrganizationService.addDeviceOrganization(deviceOrganizationTwo);
            Assert.assertNotNull(result, "Cannot be null");
            Assert.assertNotNull(result1, "Cannot be null");
            Assert.assertNotNull(result2, "Cannot be null");
        } catch (DeviceOrganizationMgtPluginException e){
            // Clean up: Delete the added organization if it was successfully added to avoid conflicts in future tests
            deviceOrganizationService.deleteDeviceAssociations(deviceOrganization.getDeviceId());
        }

    }

//    @Test(description = "This method tests Concurrent Access to Add Device Organization",
//            expectedExceptions = {DeviceOrganizationMgtPluginException.class})
//    public void testConcurrentAddDeviceOrganization() throws InterruptedException {
//        ExecutorService executor = Executors.newFixedThreadPool(4);
//        final DeviceOrganization validOrganization = new DeviceOrganization(){};
//        validOrganization.setDeviceId(3);
//        validOrganization.setParentDeviceId(2);
//        validOrganization.setUpdateTime(new Date(System.currentTimeMillis()));
//
//        for (int i = 0; i < 4; i++) {
//            executor.execute(() -> {
//                try {
//                    deviceOrganizationService.addDeviceOrganization(validOrganization);
//                } catch (DeviceOrganizationMgtPluginException e) {
//                    // Handle the exception
//                }
//            });
//        }
//
//        executor.shutdown();
//        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//    }

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

        boolean deviceIdExist = deviceOrganizationService.doesDeviceIdExist(1);

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
