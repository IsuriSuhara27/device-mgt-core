package io.entgra.device.mgt.core.device.mgt.extensions.device.organization;

import io.entgra.device.mgt.core.device.mgt.common.Device;
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

import java.sql.Date;
import java.util.List;

public class DAOTest extends BaseDeviceOrganizationTest {

    private static final Log log = LogFactory.getLog(DAOTest.class);

    private DeviceOrganizationDAO deviceOrganizationDAO;

    @BeforeClass
    public void init() {
        deviceOrganizationDAO = DeviceOrganizationDAOFactory.getDeviceOrganizationDAO();
        log.info("DAO test initialized");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testGetChildrenOf() throws DBConnectionException, DeviceOrganizationMgtDAOException {
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

//    @Test(dependsOnMethods = "testAddDeviceOrganization")
//    public void testGetParentsOf() throws DBConnectionException, DeviceOrganizationMgtDAOException {
//        ConnectionManagerUtil.openDBConnection();
//        List<Device> parentList = deviceOrganizationDAO.getParentDevices(4);
//        ConnectionManagerUtil.closeDBConnection();
//        Assert.assertNotNull(parentList, "Cannot be null");
//    }

    @Test
    public void testAddDeviceOrganization() throws DBConnectionException, DeviceOrganizationMgtDAOException {

        DeviceOrganization deviceOrganization = new DeviceOrganization() {
        };
        deviceOrganization.setDeviceId(4);
        deviceOrganization.setParentDeviceId(3);
        deviceOrganization.setUpdateTime(new Date(System.currentTimeMillis()));
        ConnectionManagerUtil.beginDBTransaction();
        boolean result = deviceOrganizationDAO.addDeviceOrganization(deviceOrganization);
        ConnectionManagerUtil.commitDBTransaction();
        ConnectionManagerUtil.closeDBConnection();
        DeviceOrganization deviceOrganization1 = new DeviceOrganization() {
        };
        deviceOrganization1.setDeviceId(3);
        deviceOrganization1.setParentDeviceId(2);
        deviceOrganization1.setUpdateTime(new Date(System.currentTimeMillis()));
        ConnectionManagerUtil.beginDBTransaction();
        boolean result1 = deviceOrganizationDAO.addDeviceOrganization(deviceOrganization1);
        ConnectionManagerUtil.closeDBConnection();

        Assert.assertNotNull(result, "Cannot be null");
        Assert.assertNotNull(result1, "Cannot be null");

    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testUpdateDeviceOrganization() throws DBConnectionException, DeviceOrganizationMgtDAOException {
        ConnectionManagerUtil.beginDBTransaction();
        boolean result = deviceOrganizationDAO.updateDeviceOrganization(4, 2, new Date(System.currentTimeMillis()), 1);
        ConnectionManagerUtil.commitDBTransaction();
        ConnectionManagerUtil.closeDBConnection();

        Assert.assertNotNull(result, "Cannot be null");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testGetDeviceOrganizationByID() throws DBConnectionException, DeviceOrganizationMgtDAOException {
        ConnectionManagerUtil.beginDBTransaction();
        DeviceOrganization deviceOrganization = deviceOrganizationDAO.getDeviceOrganizationByID(1);
        ConnectionManagerUtil.commitDBTransaction();
        ConnectionManagerUtil.closeDBConnection();
        if(deviceOrganization != null){
            log.info("Device Organization device ID : " + deviceOrganization.getDeviceId()+
                    " ,Device Organization Parent Device ID : "  + deviceOrganization.getParentDeviceId());
        }
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testDoesDeviceIdExist() throws DBConnectionException, DeviceOrganizationMgtDAOException {
        ConnectionManagerUtil.beginDBTransaction();
        boolean doesDeviceIdExist = deviceOrganizationDAO.doesDeviceIdExist(1);
        ConnectionManagerUtil.commitDBTransaction();
        ConnectionManagerUtil.closeDBConnection();

        Assert.assertNotNull(doesDeviceIdExist, "Cannot be null");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testDeleteDeviceOrganizationByID() throws DBConnectionException, DeviceOrganizationMgtDAOException {
        ConnectionManagerUtil.beginDBTransaction();
        boolean result = deviceOrganizationDAO.deleteDeviceOrganizationByID(1);
        ConnectionManagerUtil.commitDBTransaction();
        ConnectionManagerUtil.closeDBConnection();
        Assert.assertNotNull(result, "Cannot be null");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void deleteDeviceOrganizationsByDeviceId() throws DBConnectionException, DeviceOrganizationMgtDAOException {
        ConnectionManagerUtil.beginDBTransaction();
        boolean result = deviceOrganizationDAO.deleteDeviceAssociations(1);
        ConnectionManagerUtil.commitDBTransaction();
        ConnectionManagerUtil.closeDBConnection();
        Assert.assertNotNull(result, "Cannot be null");
    }
}
