package io.entgra.device.mgt.core.device.mgt.core.dao;

import io.entgra.device.mgt.core.device.mgt.core.common.BaseDeviceManagementTest;
import io.entgra.device.mgt.core.device.mgt.core.dao.impl.DeviceTypeEventDAOImpl;
import io.entgra.device.mgt.core.device.mgt.common.type.event.mgt.DeviceTypeEvent;
import io.entgra.device.mgt.core.device.mgt.core.TestUtils;
import io.entgra.device.mgt.core.device.mgt.core.common.TestDataHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.util.List;

import static org.testng.Assert.*;

public class DeviceTypeEventTests extends BaseDeviceManagementTest {

    private static final Log log = LogFactory.getLog(DeviceTypeEventTests.class);
    private DeviceTypeEventDAOImpl deviceTypeEventDAO;

    @BeforeClass
    @Override
    public void init() throws Exception {
        initDataSource(); // Initialize the database connection
        deviceTypeEventDAO = new DeviceTypeEventDAOImpl();

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
            executeUpdate("INSERT INTO DM_DEVICE_TYPE_META " +
                    "(ID, DEVICE_TYPE_ID, EVENT_DEFINITIONS, LAST_UPDATED_TIMESTAMP, TENANT_ID) " +
                    "VALUES " +
                    "(1, 1, '{\"eventDefinitions\": []}', " + System.currentTimeMillis() + ", " + TestDataHolder.ALTERNATE_TENANT_ID + ")");
            DeviceManagementDAOFactory.commitTransaction();
        }  finally {
            DeviceManagementDAOFactory.closeConnection();
        }

    }

    @Test
    public void updateDeviceTypeMetaWithEvents_JSONProcessingTest() throws Exception {
        // Arrange
        String deviceType = "air_quality";
        int tenantId = TestDataHolder.ALTERNATE_TENANT_ID;
        String initialMetaJson;
        try {
            // Get the initial event definitions from the database
            DeviceManagementDAOFactory.beginTransaction();
            Response initialResponse = deviceTypeEventDAO.getDeviceTypeEventDefinitions(deviceType, tenantId);
            DeviceManagementDAOFactory.commitTransaction();
            assertNotNull(initialResponse, "Initial response should not be null");
            assertEquals(initialResponse.getStatus(), 200, "Initial response status should be 200");
            initialMetaJson = initialResponse.getEntity().toString();
            assertNotNull(initialMetaJson, "Initial event definitions should not be null");
        } catch (DeviceManagementDAOException e) {
            throw new DeviceManagementDAOException("Error occurred while retrieving the device type event definitions" + e);
        } finally {
            DeviceManagementDAOFactory.closeConnection();
        }
        // Generate new event definitions
        List<DeviceTypeEvent> deviceTypeEvents = TestUtils.getDeviceTypeEvents();

        // Act
        try {
            DeviceManagementDAOFactory.beginTransaction();
            deviceTypeEventDAO.updateDeviceTypeMetaWithEvents(deviceType, tenantId, deviceTypeEvents);
            DeviceManagementDAOFactory.commitTransaction();
        } catch (DeviceManagementDAOException e) {
            throw new DeviceManagementDAOException("Error occurred while updating the device type event definitions" + e);
        } finally {
            DeviceManagementDAOFactory.closeConnection();
        }

        try {
            DeviceManagementDAOFactory.beginTransaction();
            Response updatedResponse = deviceTypeEventDAO.getDeviceTypeEventDefinitions(deviceType, tenantId);
            DeviceManagementDAOFactory.commitTransaction();
            assertNotNull(updatedResponse, "Updated response should not be null");
            assertEquals(updatedResponse.getStatus(), 200, "Updated response status should be 200");
            String updatedMetaJson = updatedResponse.getEntity().toString();
            assertNotNull(updatedMetaJson, "Updated event definitions should not be null");
            assertNotEquals(updatedMetaJson, initialMetaJson, "Updated event definitions should differ from the initial");
            // Verify that the updated metadata contains the new event definitions
            assertTrue(updatedMetaJson.contains(deviceTypeEvents.get(0).getEventName()),
                    "Updated event definitions should contain the new event name");
        } catch (DeviceManagementDAOException e) {
            throw new DeviceManagementDAOException("Error occurred while retrieving the device type event definitions" + e);
        } finally {
            DeviceManagementDAOFactory.closeConnection();
        }
    }

    @Test
    public void getDeviceTypeEventDefinitionsTest() throws Exception {
        // Arrange
        String deviceType = "air_quality";
        int tenantId = TestDataHolder.ALTERNATE_TENANT_ID;
        try {
        DeviceManagementDAOFactory.beginTransaction();
        Response response = deviceTypeEventDAO.getDeviceTypeEventDefinitions(deviceType, tenantId);
        DeviceManagementDAOFactory.commitTransaction();
        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(response.getStatus(), 200, "Response status should be 200");
        String eventDefinitionsJson = response.getEntity().toString();
        assertNotNull(eventDefinitionsJson, "Event definitions JSON should not be null");
        assertTrue(eventDefinitionsJson.contains("eventDefinitions"), "Response should contain event definitions");
        } catch (DeviceManagementDAOException e) {
            throw new DeviceManagementDAOException("Error occurred while retrieving the device type event definitions" + e);
        } finally {
            DeviceManagementDAOFactory.closeConnection();
        }
    }
}
