package io.entgra.device.mgt.core.device.mgt.extensions.device.organization;

import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.DeviceOrganizationDAO;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.DeviceOrganizationDAOFactory;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.mock.BaseDeviceOrganizationTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;

public class DAONegativeTest extends BaseDeviceOrganizationTest {

    private static final Log log = LogFactory.getLog(DAONegativeTest.class);

    private DeviceOrganizationDAO deviceOrganizationDAO;

    @BeforeClass
    public void init() {
        deviceOrganizationDAO = DeviceOrganizationDAOFactory.getDeviceOrganizationDAO();
        log.info("DAO test initialized");
    }

//    @Test(expectedExceptions = DeviceOrganizationMgtDAOException.class, description = "This method tests the addDeviceOrganization method under negative circumstances with null input")
//    public void testAddDeviceOrganizationWithNullInput() throws DeviceOrganizationMgtDAOException {
//        DeviceOrganization invalidDeviceOrg = null;
//        deviceOrganizationDAO.addDeviceOrganization(invalidDeviceOrg);
//    }

//    @Test(description = "Test with invalid input parameters (bad request)")
//    public void testGetChildrenOfWithInvalidInput() {
//        // Create an invalid input (e.g., null node and negative maxDepth)
//        DeviceNode invalidNode = null;
//        int invalidMaxDepth = -1;
//        boolean includeDevice = true;
//
//        try {
//            deviceOrganizationDAO.getChildrenOf(invalidNode, invalidMaxDepth, includeDevice);
//            assert false : "Expected exception for bad request was not thrown.";
//        } catch (DeviceOrganizationMgtDAOException e) {
//            log.info("Expected exception for bad request was thrown: " + e.getMessage());
//        }
//    }

//    @Test(expectedExceptions = DeviceOrganizationMgtDAOException.class, description = "This method tests the " +
//            "getParentsOf method under negative circumstances with invalid input")
//    public void testGetParentsOfWithInvalidInput() throws DeviceOrganizationMgtDAOException {
//        DeviceNode invalidNode = null;
//        int invalidMaxDepth = -1;
//        boolean includeDevice = true;
//        deviceOrganizationDAO.getParentsOf(invalidNode, invalidMaxDepth, includeDevice);
//    }


}
