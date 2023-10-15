/*
 * Copyright (c) 2018 - 2023, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.entgra.device.mgt.core.device.mgt.extensions.device.organization.api;

import com.google.gson.Gson;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceNode;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceOrganization;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.DeviceOrganizationMgtPluginException;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.impl.DeviceOrganizationServiceImpl;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.spi.DeviceOrganizationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/deviceOrganization")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviceOrganizationMgtServiceImpl implements DeviceOrganizationMgtService {

    private static final Log log = LogFactory.getLog(DeviceOrganizationMgtServiceImpl.class);
    Gson gson = new Gson();

    @POST
    @Override
    @Path("/add-device-organization")
    public Response addDeviceOrganization(DeviceOrganization deviceOrganizationRequest) {
        try {
            DeviceOrganizationService deviceOrganizationService = new DeviceOrganizationServiceImpl();
//            DeviceOrganization deviceOrganization = new DeviceOrganization();
//            deviceOrganization.setDeviceId(DeviceOrganization.getDeviceId());
//            deviceOrganization.setParentDeviceId(DeviceOrganization.getParentDeviceId());
            boolean resp = deviceOrganizationService.addDeviceOrganization(deviceOrganizationRequest);
            return Response.status(Response.Status.OK).entity(gson.toJson(resp)).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Override
    @Path("/children")
    public Response getChildrenOfDeviceNode(
            @QueryParam("deviceId") int deviceId,
            @QueryParam("maxDepth") int maxDepth,
            @QueryParam("includeDevice") boolean includeDevice) {
        try {
            DeviceOrganizationService deviceOrganizationService = new DeviceOrganizationServiceImpl();
            DeviceNode deviceNode = new DeviceNode();
            deviceNode.setDeviceId(deviceId);
            List<DeviceNode> children = deviceOrganizationService.getChildrenOfDeviceNode(deviceNode, maxDepth, includeDevice);
            return Response.status(Response.Status.OK).entity(gson.toJson(children)).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Override
    @Path("/parents")
    public Response getParentsOfDeviceNode(
            @QueryParam("deviceId") int deviceId,
            @QueryParam("maxDepth") int maxDepth,
            @QueryParam("includeDevice") boolean includeDevice) {
        try {
            DeviceOrganizationService deviceOrganizationService = new DeviceOrganizationServiceImpl();
            DeviceNode deviceNode = new DeviceNode();
            deviceNode.setDeviceId(deviceId);
            List<DeviceNode> parents = deviceOrganizationService.getParentsOfDeviceNode(deviceNode, maxDepth, includeDevice);
            return Response.status(Response.Status.OK).entity(gson.toJson(parents)).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Override
    @Path("/all")
    public Response getAllDeviceOrganizations() {
        try {
            DeviceOrganizationService deviceOrganizationService = new DeviceOrganizationServiceImpl();
            List<DeviceOrganization> organizations = deviceOrganizationService.getAllDeviceOrganizations();
            return Response.status(Response.Status.OK).entity(gson.toJson(organizations)).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Override
    @Path("/{organizationId}")
    public Response getDeviceOrganizationById(@PathParam("organizationId") int organizationId) {
        try {
            DeviceOrganizationService deviceOrganizationService = new DeviceOrganizationServiceImpl();
            DeviceOrganization organization = deviceOrganizationService.getDeviceOrganizationByID(organizationId);
            return Response.status(Response.Status.OK).entity(gson.toJson(organization)).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Override
    @Path("/exists")
    public Response isDeviceOrganizationExist(
            @QueryParam("deviceId") int deviceId,
            @QueryParam("parentDeviceId") int parentDeviceId) {
        try {
            DeviceOrganizationService deviceOrganizationService = new DeviceOrganizationServiceImpl();
            boolean exists = deviceOrganizationService.isDeviceOrganizationExist(deviceId, parentDeviceId);
            return Response.status(Response.Status.OK).entity(gson.toJson(exists)).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Override
    @Path("/child-exists")
    public Response isChildDeviceIdExist(@QueryParam("deviceId") int deviceID) {
        try {
            DeviceOrganizationService deviceOrganizationService = new DeviceOrganizationServiceImpl();
            boolean exists = deviceOrganizationService.isChildDeviceIdExist(deviceID);
            return Response.status(Response.Status.OK).entity(gson.toJson(exists)).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }


    @GET
    @Override
    @Path("/unique")
    public Response getDeviceOrganizationByUniqueKey(
            @QueryParam("deviceId") int deviceId,
            @QueryParam("parentDeviceId") int parentDeviceId) {
        try {
            DeviceOrganizationService deviceOrganizationService = new DeviceOrganizationServiceImpl();
            DeviceOrganization organization = deviceOrganizationService.getDeviceOrganizationByUniqueKey(deviceId, parentDeviceId);
            return Response.status(Response.Status.OK).entity(gson.toJson(organization)).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Override
    @Path("/device-exists")
    public Response doesDeviceIdExist(@QueryParam("deviceId") int deviceId) {
        try {
            DeviceOrganizationService deviceOrganizationService = new DeviceOrganizationServiceImpl();
            boolean exists = deviceOrganizationService.isDeviceIdExist(deviceId);
            return Response.status(Response.Status.OK).entity(gson.toJson(exists)).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Override
    @Path("/update")
    public Response updateDeviceOrganization(DeviceOrganization deviceOrganization) {
        try {
            DeviceOrganizationService deviceOrganizationService = new DeviceOrganizationServiceImpl();
            boolean resp = deviceOrganizationService.updateDeviceOrganization(deviceOrganization);
            return Response.status(Response.Status.OK).entity(gson.toJson(resp)).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Override
    @Path("/delete/{organizationId}")
    public Response deleteDeviceOrganizationById(@PathParam("organizationId") int organizationId) {
        try {
            DeviceOrganizationService deviceOrganizationService = new DeviceOrganizationServiceImpl();
            boolean resp = deviceOrganizationService.deleteDeviceOrganizationByID(organizationId);
            return Response.status(Response.Status.OK).entity(gson.toJson(resp)).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Override
    @Path("/delete-associations/{deviceId}")
    public Response deleteDeviceAssociations(@PathParam("deviceId") int deviceId) {
        try {
            DeviceOrganizationService deviceOrganizationService = new DeviceOrganizationServiceImpl();
            boolean resp = deviceOrganizationService.deleteDeviceAssociations(deviceId);
            return Response.status(Response.Status.OK).entity(gson.toJson(resp)).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
