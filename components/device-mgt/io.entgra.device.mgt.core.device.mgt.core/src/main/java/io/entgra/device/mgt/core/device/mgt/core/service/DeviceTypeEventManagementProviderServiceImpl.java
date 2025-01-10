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
package io.entgra.device.mgt.core.device.mgt.core.service;

import io.entgra.device.mgt.core.device.mgt.common.exceptions.TransactionManagementException;
import io.entgra.device.mgt.core.device.mgt.common.type.event.mgt.DeviceTypeEvent;
import io.entgra.device.mgt.core.device.mgt.core.dao.DeviceManagementDAOException;
import io.entgra.device.mgt.core.device.mgt.core.dao.DeviceManagementDAOFactory;
import io.entgra.device.mgt.core.device.mgt.core.dao.DeviceTypeEventDAO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;

import javax.ws.rs.core.Response;
import java.util.List;

public class DeviceTypeEventManagementProviderServiceImpl implements DeviceTypeEventManagementProviderService{

   private static final Log log = LogFactory.getLog(DeviceTypeEventManagementProviderServiceImpl.class);
   private final DeviceTypeEventDAO deviceTypeEventDAO;

   public DeviceTypeEventManagementProviderServiceImpl() {
      this.deviceTypeEventDAO = DeviceManagementDAOFactory.getDeviceTypeEventDAO();
   }

   @Override
   public Response getDeviceTypeEventDefinitions(String deviceType) throws DeviceManagementDAOException {
      try {
         DeviceManagementDAOFactory.beginTransaction();
         int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
         Response response = deviceTypeEventDAO.getDeviceTypeEventDefinitions(deviceType,tenantId);
         DeviceManagementDAOFactory.commitTransaction();
         return response;
      } catch (TransactionManagementException e) {
         String msg = "Error occurred while initiating transaction.";
         log.error(msg, e);
         throw new DeviceManagementDAOException(msg, e);
      } catch (DeviceManagementDAOException e) {
         DeviceManagementDAOFactory.rollbackTransaction();
         String msg = "Error occurred while retrieving tags.";
         log.error(msg, e);
         throw new DeviceManagementDAOException(msg, e);
      } catch (Exception e) {
         String msg = "Error occurred in retrieving tags.";
         log.error(msg, e);
         throw new DeviceManagementDAOException(msg, e);
      } finally {
         DeviceManagementDAOFactory.closeConnection();
      }
   }

   @Override
   public String getDeviceTypeEventDefinitionsAsJson(String deviceType) throws DeviceManagementDAOException {
      try {
         DeviceManagementDAOFactory.beginTransaction();
         int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
         String eventDefinitionsJson = deviceTypeEventDAO.getDeviceTypeEventDefinitionsAsJson(deviceType,tenantId);
         DeviceManagementDAOFactory.commitTransaction();
         return eventDefinitionsJson;
      } catch (TransactionManagementException e) {
         String msg = "Error occurred while initiating transaction.";
         log.error(msg, e);
         throw new DeviceManagementDAOException(msg, e);
      } catch (DeviceManagementDAOException e) {
         DeviceManagementDAOFactory.rollbackTransaction();
         String msg = "Error occurred while retrieving tags.";
         log.error(msg, e);
         throw new DeviceManagementDAOException(msg, e);
      } catch (Exception e) {
         String msg = "Error occurred in retrieving tags.";
         log.error(msg, e);
         throw new DeviceManagementDAOException(msg, e);
      } finally {
         DeviceManagementDAOFactory.closeConnection();
      }
   }

   @Override
   public Response updateDeviceTypeMetaWithEvents(String deviceType, List<DeviceTypeEvent> deviceTypeEvents) throws DeviceManagementDAOException {
      try {
         String eventDefinitionsJson = getDeviceTypeEventDefinitionsAsJson(deviceType);
         DeviceManagementDAOFactory.beginTransaction();
         int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
         Response response = deviceTypeEventDAO.updateDeviceTypeMetaWithEvents(deviceType,tenantId,eventDefinitionsJson,deviceTypeEvents);
         DeviceManagementDAOFactory.commitTransaction();
         return response;
      } catch (TransactionManagementException e) {
         String msg = "Error occurred while initiating transaction.";
         log.error(msg, e);
         throw new DeviceManagementDAOException(msg, e);
      } catch (DeviceManagementDAOException e) {
         DeviceManagementDAOFactory.rollbackTransaction();
         String msg = "Error occurred while retrieving tags.";
         log.error(msg, e);
         throw new DeviceManagementDAOException(msg, e);
      } catch (Exception e) {
         String msg = "Error occurred in retrieving tags.";
         log.error(msg, e);
         throw new DeviceManagementDAOException(msg, e);
      } finally {
         DeviceManagementDAOFactory.closeConnection();
      }
   }


}
