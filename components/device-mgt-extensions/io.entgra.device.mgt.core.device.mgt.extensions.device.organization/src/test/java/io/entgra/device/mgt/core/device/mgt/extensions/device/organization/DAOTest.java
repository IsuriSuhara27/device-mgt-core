package io.entgra.device.mgt.core.device.mgt.extensions.device.organization;

import io.entgra.device.mgt.core.device.mgt.common.Device;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.DeviceOrganizationDAO;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.DeviceOrganizationDAOFactory;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.util.ConnectionManagerUtil;
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
        List<Device> childrenList = deviceOrganizationDAO.getChildDevices(3);
        ConnectionManagerUtil.closeDBConnection();
        Assert.assertNotNull(childrenList, "Cannot be null");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testGetParentsOf() throws DBConnectionException, DeviceOrganizationMgtDAOException {
        ConnectionManagerUtil.openDBConnection();
        List<Device> parentList = deviceOrganizationDAO.getParentDevices(4);
        ConnectionManagerUtil.closeDBConnection();
        Assert.assertNotNull(parentList, "Cannot be null");
    }

    @Test
    public void testAddDeviceOrganization() throws DBConnectionException, DeviceOrganizationMgtDAOException {

        DeviceOrganization.DeviceOrganizationStatus status = DeviceOrganization.DeviceOrganizationStatus.ACT;
        DeviceOrganization deviceOrganization = new DeviceOrganization() {
        };
        deviceOrganization.setDeviceId(4);
        deviceOrganization.setParentDeviceId(3);
        deviceOrganization.setUpdateTime(new Date(System.currentTimeMillis()));
        deviceOrganization.setStatus(status);
        ConnectionManagerUtil.beginDBTransaction();
        boolean result = deviceOrganizationDAO.addDeviceOrganization(deviceOrganization);
        ConnectionManagerUtil.commitDBTransaction();
        ConnectionManagerUtil.closeDBConnection();

        Assert.assertNotNull(result, "Cannot be null");

    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testUpdateDeviceOrganization() throws DBConnectionException, DeviceOrganizationMgtDAOException {
        ConnectionManagerUtil.beginDBTransaction();
        boolean result = deviceOrganizationDAO.updateDeviceOrganization(4, 2, new Date(System.currentTimeMillis()), "ACTIVE", 1);
        ConnectionManagerUtil.commitDBTransaction();
        ConnectionManagerUtil.closeDBConnection();

        Assert.assertNotNull(result, "Cannot be null");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testUpdateDeviceOrganizationInactivate() throws DBConnectionException, DeviceOrganizationMgtDAOException {
        ConnectionManagerUtil.beginDBTransaction();
        boolean result = deviceOrganizationDAO.updateDeviceOrganization(4, 2, new Date(System.currentTimeMillis()), "INACTIVE", 1);
        ConnectionManagerUtil.commitDBTransaction();
        ConnectionManagerUtil.closeDBConnection();

        Assert.assertNotNull(result, "Cannot be null");
    }

    @Test(dependsOnMethods = "testAddDeviceOrganization")
    public void testGetDeviceOrganization() throws DBConnectionException, DeviceOrganizationMgtDAOException {
        ConnectionManagerUtil.beginDBTransaction();
        DeviceOrganization deviceOrganization = deviceOrganizationDAO.getDeviceOrganizationByID(1);
        ConnectionManagerUtil.commitDBTransaction();
        ConnectionManagerUtil.closeDBConnection();

        Assert.assertNotNull(deviceOrganization, "Cannot be null");
    }
}
