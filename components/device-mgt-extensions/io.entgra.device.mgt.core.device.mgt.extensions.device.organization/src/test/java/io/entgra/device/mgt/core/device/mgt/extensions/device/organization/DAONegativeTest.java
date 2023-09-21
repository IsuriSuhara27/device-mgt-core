package io.entgra.device.mgt.core.device.mgt.extensions.device.organization;

import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.DeviceOrganizationDAO;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.DeviceOrganizationDAOFactory;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.util.ConnectionManagerUtil;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceOrganization;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.DBConnectionException;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.DeviceOrganizationMgtDAOException;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.mock.BaseDeviceOrganizationTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DAONegativeTest extends BaseDeviceOrganizationTest {

    private static final Log log = LogFactory.getLog(DAONegativeTest.class);

    private DeviceOrganizationDAO deviceOrganizationDAO;

    @BeforeClass
    public void init() {
        deviceOrganizationDAO = DeviceOrganizationDAOFactory.getDeviceOrganizationDAO();
        log.info("DAO test initialized");
    }

    @Test(description = "This method tests the add device organization method under negative circumstances with null data",
            expectedExceptions = {NullPointerException.class}
    )
    public void testAddDeviceOrganization() throws DeviceOrganizationMgtDAOException {
        DeviceOrganization deviceOrganization = new DeviceOrganization() {
        };
        try {
            ConnectionManagerUtil.beginDBTransaction();
            deviceOrganizationDAO.addDeviceOrganization(deviceOrganization);
            ConnectionManagerUtil.commitDBTransaction();
        } catch (DeviceOrganizationMgtDAOException e) {
            ConnectionManagerUtil.rollbackDBTransaction();
            String msg = "Error occurred while processing SQL to insert device organization";
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        } catch (DBConnectionException e) {
            String msg = "Error occurred while obtaining DB connection to insert device organization";
            log.error(msg);
            throw new DeviceOrganizationMgtDAOException(msg, e);
        } finally {
            ConnectionManagerUtil.closeDBConnection();
        }
    }


}
