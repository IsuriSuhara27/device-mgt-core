package io.entgra.device.mgt.core.device.mgt.core.service;

import io.entgra.device.mgt.core.device.mgt.core.common.BaseDeviceManagementTest;
import io.entgra.device.mgt.core.device.mgt.common.type.event.mgt.DeviceTypeEvent;
import io.entgra.device.mgt.core.device.mgt.core.TestUtils;
import io.entgra.device.mgt.core.device.mgt.core.common.TestDataHolder;
import io.entgra.device.mgt.core.device.mgt.core.dao.DeviceManagementDAOException;
import io.entgra.device.mgt.core.device.mgt.core.dao.DeviceManagementDAOFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.util.List;

import static org.testng.Assert.*;

public class DeviceTypeEventProviderServiceTests extends BaseDeviceManagementTest {

    private static final Log log = LogFactory.getLog(DeviceTypeEventProviderServiceTests.class);
    private DeviceTypeEventManagementProviderServiceImpl deviceTypeEventManagementProviderService;

    @BeforeClass
    @Override
    public void init() throws Exception {
        initDataSource(); // Initialize the database connection
        deviceTypeEventManagementProviderService = new DeviceTypeEventManagementProviderServiceImpl();

        try {
            // Insert initial test data into DM_DEVICE_TYPE table
            DeviceManagementDAOFactory.beginTransaction();
            executeUpdate("INSERT INTO DM_DEVICE_TYPE " +
                    "(ID, NAME, DEVICE_TYPE_META, LAST_UPDATED_TIMESTAMP, PROVIDER_TENANT_ID, SHARED_WITH_ALL_TENANTS) " +
                    "VALUES " +
                    "(1, 'air_quality', NULL, CURRENT_TIMESTAMP, " + TestDataHolder.ALTERNATE_TENANT_ID + ", FALSE)");
            DeviceManagementDAOFactory.commitTransaction();
        }  finally {
            DeviceManagementDAOFactory.closeConnection();
        }

        try {
            // Insert initial test data into DM_DEVICE_TYPE_META table
            DeviceManagementDAOFactory.beginTransaction();
            executeUpdate(
                    "INSERT INTO DM_DEVICE_TYPE_META " +
                            "(ID, DEVICE_TYPE_ID, EVENT_DEFINITIONS, LAST_UPDATED_TIMESTAMP, TENANT_ID) " +
                            "VALUES " +
                            "(1, 1, '{\"eventDefinitions\":[{\"eventName\":\"event1\",\"eventTopicStructure\":\"topic1/structure\",\"eventAttributes\":{\"attributes\":[{\"name\":\"t\",\"type\":\"FLOAT\"}]}}]}', " +
                            System.currentTimeMillis() + ", " + TestDataHolder.ALTERNATE_TENANT_ID + ")"
            );
            DeviceManagementDAOFactory.commitTransaction();
        } finally {
            DeviceManagementDAOFactory.closeConnection();
        }


    }

    @Test
    public void updateDeviceTypeMetaWithEvents_JSONProcessingTest() throws Exception {
        // Arrange
        String deviceType = "air_quality";
        List<DeviceTypeEvent> deviceTypeEvents = TestUtils.getDeviceTypeEvents();
        try {
            // Get the initial event definitions from the database
            Response initialResponse = deviceTypeEventManagementProviderService.updateDeviceTypeMetaWithEvents(deviceType, deviceTypeEvents);
            assertNotNull(initialResponse, "Initial response should not be null");
            assertEquals(initialResponse.getStatus(), 200, "Initial response status should be 200");
        } catch (DeviceManagementDAOException e) {
            throw new DeviceManagementDAOException("Error occurred while retrieving the device type event definitions" + e);
        }
    }

    @Test
    public void getDeviceTypeEventDefinitionsTest() throws Exception {
        // Arrange
        String deviceType = "air_quality";
        try {
        Response response = deviceTypeEventManagementProviderService.getDeviceTypeEventDefinitions(deviceType);
        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(response.getStatus(), 200, "Response status should be 200");
        String eventDefinitionsJson = response.getEntity().toString();
        assertNotNull(eventDefinitionsJson, "Event definitions JSON should not be null");
        assertTrue(eventDefinitionsJson.contains("eventDefinitions"), "Response should contain event definitions");
        } catch (DeviceManagementDAOException e) {
            throw new DeviceManagementDAOException("Error occurred while retrieving the device type event definitions" + e);
        }
    }
}
