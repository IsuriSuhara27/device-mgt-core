/*
 * Copyright (C) 2018 - 2024 Entgra (Pvt) Ltd, Inc - All Rights Reserved.
 *
 * Unauthorised copying/redistribution of this file, via any medium is strictly prohibited.
 *
 * Licensed under the Entgra Commercial License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://entgra.io/licenses/entgra-commercial/1.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.entgra.device.mgt.core.device.mgt.core.dao.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.entgra.device.mgt.core.device.mgt.core.dao.DeviceManagementDAOException;
import io.entgra.device.mgt.core.device.mgt.core.dao.DeviceManagementDAOFactory;
import io.entgra.device.mgt.core.device.mgt.core.dao.DeviceTypeEventDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.entgra.device.mgt.core.device.mgt.common.type.event.mgt.DeviceTypeEvent;

public class DeviceTypeEventDAOImpl implements DeviceTypeEventDAO {

   private static Log log = LogFactory.getLog(DeviceTypeEventDAOImpl.class);

   @Override
   public Response getDeviceTypeEventDefinitions(String deviceType, int tenantId) throws DeviceManagementDAOException {
      String selectSQL = "SELECT EVENT_DEFINITIONS FROM DM_DEVICE_TYPE_META WHERE TENANT_ID = ? AND ID = " +
              "(SELECT ID FROM DM_DEVICE_TYPE WHERE NAME = ? AND PROVIDER_TENANT_ID = ?)";
      try (Connection connection = this.getConnection();
           PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {

         preparedStatement.setInt(1, tenantId);
         preparedStatement.setString(2, deviceType);
         preparedStatement.setInt(3, tenantId);

         try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
               String eventDefinitionsJson = resultSet.getString("EVENT_DEFINITIONS");
               if (eventDefinitionsJson != null) {
                  ObjectMapper objectMapper = new ObjectMapper();
                  Object eventDefinitions = objectMapper.readValue(eventDefinitionsJson, Object.class);
                  return Response.ok(objectMapper.writeValueAsString(eventDefinitions)).build();
               }
            }
         } catch (JsonMappingException e) {
            throw new RuntimeException(e);
         } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
         }
         return Response.status(Response.Status.NOT_FOUND).build();
      } catch (SQLException e) {
         log.error("Failed to retrieve EVENT_DEFINITIONS for device type: " + deviceType, e);
         return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
      }
   }

   @Override
   public void updateDeviceTypeMetaWithEvents(String deviceType, int tenantId, List<DeviceTypeEvent> deviceTypeEvents)
           throws DeviceManagementDAOException {
      try {
         // Retrieve existing event definitions
         String existingEventDefinitionsJson = getDeviceTypeEventDefinitionsAsJson(deviceType, tenantId);
         // Initialize ObjectMapper for Jackson processing
         ObjectMapper objectMapper = new ObjectMapper();
         // Parse existing event definitions
         List<Map<String, Object>> existingEvents = parseExistingEventDefinitions(existingEventDefinitionsJson, objectMapper);
         // Add new event definitions
         addNewEventDefinitions(existingEvents, deviceTypeEvents);
         // Serialize updated event definitions
         String updatedEventDefinitionsJson = objectMapper.writeValueAsString(existingEvents);
         // Update the database with the new event definitions
         updateEventDefinitionsInDB(deviceType, tenantId, updatedEventDefinitionsJson);
      } catch (SQLException e) {
         log.error("Error while updating EVENT_DEFINITIONS for device type: " + deviceType, e);
         throw new DeviceManagementDAOException("Error updating EVENT_DEFINITIONS in the database.", e);
      } catch (IOException e) {
         log.error("Error processing JSON for device type: " + deviceType, e);
         throw new DeviceManagementDAOException("Error processing JSON for EVENT_DEFINITIONS.", e);
      }
   }

   private String getDeviceTypeEventDefinitionsAsJson(String deviceType, int tenantId) throws SQLException {
      String selectSQL = "SELECT EVENT_DEFINITIONS FROM DM_DEVICE_TYPE_META WHERE TENANT_ID = ? AND ID = " +
              "(SELECT ID FROM DM_DEVICE_TYPE WHERE NAME = ? AND PROVIDER_TENANT_ID = ?)";
      try (Connection connection = this.getConnection();
           PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {

         preparedStatement.setInt(1, tenantId);
         preparedStatement.setString(2, deviceType);
         preparedStatement.setInt(3, tenantId);

         try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
               return resultSet.getString("EVENT_DEFINITIONS");
            }
         }
      }
      return null;
   }

   private List<Map<String, Object>> parseExistingEventDefinitions(String existingEventDefinitionsJson, ObjectMapper objectMapper)
           throws IOException {
      if (existingEventDefinitionsJson != null) {
         try {
            return objectMapper.readValue(existingEventDefinitionsJson, new TypeReference<>() {});
         } catch (JsonParseException | JsonMappingException e) {
            log.error("Error parsing JSON: " + existingEventDefinitionsJson, e);
            throw new IOException("Error parsing JSON", e);
         }
      }
      return new ArrayList<>();
   }

   private void addNewEventDefinitions(List<Map<String, Object>> existingEvents, List<DeviceTypeEvent> deviceTypeEvents) {
      for (DeviceTypeEvent event : deviceTypeEvents) {
         Map<String, Object> eventMap = new HashMap<>();
         eventMap.put("eventName", event.getEventName());
         eventMap.put("transportType", event.getTransportType().name());
         eventMap.put("eventAttributes", event.getEventAttributeList().getList().stream()
                 .map(attr -> Map.of("name", attr.getName(), "type", attr.getType().name()))
                 .collect(Collectors.toList()));
         eventMap.put("topicStructure", event.getEventTopicStructure());
         existingEvents.add(eventMap);
      }
   }

   private void updateEventDefinitionsInDB(String deviceType, int tenantId, String updatedEventDefinitionsJson)
           throws SQLException {
      String updateSQL = "UPDATE DM_DEVICE_TYPE_META " +
              "SET EVENT_DEFINITIONS = ?, LAST_UPDATED_TIMESTAMP = ? " +
              "WHERE TENANT_ID = ? AND DEVICE_TYPE_ID = " +
              "(SELECT ID FROM DM_DEVICE_TYPE WHERE NAME = ? AND PROVIDER_TENANT_ID = ?)";
      try (Connection connection = this.getConnection();
           PreparedStatement updateStmt = connection.prepareStatement(updateSQL)) {

         // Set the parameters
         updateStmt.setString(1, updatedEventDefinitionsJson);
         updateStmt.setLong(2, System.currentTimeMillis()); // Set LAST_UPDATED_TIMESTAMP as Unix time in milliseconds
         updateStmt.setInt(3, tenantId);
         updateStmt.setString(4, deviceType);
         updateStmt.setInt(5, tenantId);

         // Execute the update
         updateStmt.executeUpdate();
      }
   }



   private Connection getConnection() throws SQLException {
      return DeviceManagementDAOFactory.getConnection();
   }
}
