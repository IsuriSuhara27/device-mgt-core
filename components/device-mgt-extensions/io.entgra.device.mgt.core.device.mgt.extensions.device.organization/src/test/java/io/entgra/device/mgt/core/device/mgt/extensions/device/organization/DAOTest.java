package io.entgra.device.mgt.core.device.mgt.extensions.device.organization;

import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.DeviceOrganizationDAO;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.DeviceOrganizationDAOFactory;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.util.ConnectionManagerUtil;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceNode;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceOrganization;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.DBConnectionException;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.DeviceOrganizationMgtDAOException;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.mock.BaseDeviceOrganizationTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

public class DAOTest extends BaseDeviceOrganizationTest {

    private static final Log log = LogFactory.getLog(DAOTest.class);

    private DeviceOrganizationDAO deviceOrganizationDAO;

    @BeforeClass
    public void init() {
        deviceOrganizationDAO = DeviceOrganizationDAOFactory.getDeviceOrganizationDAO();
        log.info("DAO test initialized");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganizationDAO")
    public void testGetChildrenOfDAO() throws DBConnectionException, DeviceOrganizationMgtDAOException {
        ConnectionManagerUtil.openDBConnection();
        DeviceNode node = new DeviceNode();
        node.setDeviceId(2);
        int maxDepth = 4;
        boolean includeDevice = true;
        List<DeviceNode> childrenList = deviceOrganizationDAO.getChildrenOf(node, maxDepth, includeDevice);
        ConnectionManagerUtil.closeDBConnection();
        log.info(childrenList.size());
        Assert.assertNotNull(childrenList, "Cannot be null");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganizationDAO")
    public void testGetParentsOfDAO() throws DBConnectionException, DeviceOrganizationMgtDAOException {
        ConnectionManagerUtil.openDBConnection();
        DeviceNode node = new DeviceNode();
        node.setDeviceId(4);
        int maxDepth = 4;
        boolean includeDevice = false;
        List<DeviceNode> parentList = deviceOrganizationDAO.getParentsOf(node, maxDepth, includeDevice);
        ConnectionManagerUtil.closeDBConnection();
        log.info(parentList.size());
        Assert.assertNotNull(parentList, "Cannot be null");
    }

    @Test
    public void testAddDeviceOrganizationDAO() throws DBConnectionException, DeviceOrganizationMgtDAOException {

        ConnectionManagerUtil.beginDBTransaction();
        deviceOrganizationDAO.deleteDeviceAssociations(1);
        ConnectionManagerUtil.commitDBTransaction();
        ConnectionManagerUtil.closeDBConnection();
        ConnectionManagerUtil.beginDBTransaction();
        deviceOrganizationDAO.deleteDeviceAssociations(2);
        ConnectionManagerUtil.commitDBTransaction();
        ConnectionManagerUtil.closeDBConnection();
        DeviceOrganization deviceOrganization = new DeviceOrganization() {
        };
        deviceOrganization.setDeviceId(2);
        deviceOrganization.setParentDeviceId(null);
        deviceOrganization.setUpdateTime(new Date(System.currentTimeMillis()));
        ConnectionManagerUtil.beginDBTransaction();
        boolean result = deviceOrganizationDAO.addDeviceOrganization(deviceOrganization);
        ConnectionManagerUtil.commitDBTransaction();
        ConnectionManagerUtil.closeDBConnection();
        DeviceOrganization deviceOrganization1 = new DeviceOrganization() {
        };
        deviceOrganization1.setDeviceId(4);
        deviceOrganization1.setParentDeviceId(1);
        deviceOrganization1.setUpdateTime(new Date(System.currentTimeMillis()));
        ConnectionManagerUtil.beginDBTransaction();
        boolean result1 = deviceOrganizationDAO.addDeviceOrganization(deviceOrganization1);
        ConnectionManagerUtil.commitDBTransaction();
        ConnectionManagerUtil.closeDBConnection();

        DeviceOrganization deviceOrganization2 = new DeviceOrganization() {
        };
        deviceOrganization1.setDeviceId(3);
        deviceOrganization1.setParentDeviceId(1);
        deviceOrganization1.setUpdateTime(new Date(System.currentTimeMillis()));
        ConnectionManagerUtil.beginDBTransaction();
        boolean result2 = deviceOrganizationDAO.addDeviceOrganization(deviceOrganization1);
        ConnectionManagerUtil.commitDBTransaction();
        ConnectionManagerUtil.closeDBConnection();

        Assert.assertNotNull(result, "Cannot be null");
        Assert.assertNotNull(result1, "Cannot be null");

    }

    @Test(dependsOnMethods = "testAddDeviceOrganizationDAO")
    public void testUpdateDeviceOrganizationDAO() throws DBConnectionException, DeviceOrganizationMgtDAOException {
        ConnectionManagerUtil.beginDBTransaction();
        DeviceOrganization deviceOrganization = new DeviceOrganization() {
        };
        deviceOrganization.setDeviceId(2);
        deviceOrganization.setParentDeviceId(1);
        deviceOrganization.setOrganizationId(1);
        boolean result = deviceOrganizationDAO.updateDeviceOrganization(deviceOrganization);
        ConnectionManagerUtil.commitDBTransaction();
        ConnectionManagerUtil.closeDBConnection();

        Assert.assertNotNull(result, "Cannot be null");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganizationDAO")
    public void testGetDeviceOrganizationByIDDAO() throws DBConnectionException, DeviceOrganizationMgtDAOException {
        ConnectionManagerUtil.beginDBTransaction();
        DeviceOrganization deviceOrganization = deviceOrganizationDAO.getDeviceOrganizationByID(1);
        ConnectionManagerUtil.closeDBConnection();
        if (deviceOrganization != null) {
            log.info("Device Organization device ID : " + deviceOrganization.getDeviceId() +
                    " ,Device Organization Parent Device ID : " + deviceOrganization.getParentDeviceId());
        }
    }

    @Test(dependsOnMethods = "testAddDeviceOrganizationDAO")
    public void testDoesDeviceIdExistDAO() throws DBConnectionException, DeviceOrganizationMgtDAOException {
        ConnectionManagerUtil.beginDBTransaction();
        boolean doesDeviceIdExist = deviceOrganizationDAO.doesDeviceIdExist(1);
        ConnectionManagerUtil.closeDBConnection();

        Assert.assertNotNull(doesDeviceIdExist, "Cannot be null");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganizationDAO")
    public void testDeleteDeviceOrganizationByIDDAO() throws DBConnectionException, DeviceOrganizationMgtDAOException {
        ConnectionManagerUtil.beginDBTransaction();
        boolean result = deviceOrganizationDAO.deleteDeviceOrganizationByID(1);
        ConnectionManagerUtil.commitDBTransaction();
        ConnectionManagerUtil.closeDBConnection();
        Assert.assertNotNull(result, "Cannot be null");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganizationDAO")
    public void deleteDeviceOrganizationsByDeviceIdDAO() throws DBConnectionException, DeviceOrganizationMgtDAOException {
        ConnectionManagerUtil.beginDBTransaction();
        boolean result = deviceOrganizationDAO.deleteDeviceAssociations(1);
        ConnectionManagerUtil.commitDBTransaction();
        ConnectionManagerUtil.closeDBConnection();
        Assert.assertNotNull(result, "Cannot be null");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganizationDAO")
    public void testGetAllOrganizations() throws DBConnectionException, DeviceOrganizationMgtDAOException {
        ConnectionManagerUtil.beginDBTransaction();
        List<DeviceOrganization> organizations = deviceOrganizationDAO.getAllDeviceOrganizations();
        for (DeviceOrganization organization : organizations) {
            log.info("organizationID = " + organization.getOrganizationId());
            log.info("deviceID = " + organization.getDeviceId());
            log.info("parentDeviceID = " + organization.getParentDeviceId());
            log.info("updateTime = " + organization.getUpdateTime());
            log.info("----------------------------------------------");
        }
        ConnectionManagerUtil.closeDBConnection();
    }

}
