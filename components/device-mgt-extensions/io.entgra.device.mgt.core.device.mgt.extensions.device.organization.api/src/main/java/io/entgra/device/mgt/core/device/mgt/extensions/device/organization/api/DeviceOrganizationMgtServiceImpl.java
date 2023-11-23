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
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.api.util.DeviceOrgAPIUtils;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.api.util.RequestValidationUtil;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceNode;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceOrganization;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.PaginationRequest;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception.DeviceOrganizationMgtPluginException;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.spi.DeviceOrganizationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviceOrganizationMgtServiceImpl implements DeviceOrganizationMgtService {

    private static final Log log = LogFactory.getLog(DeviceOrganizationMgtServiceImpl.class);
    Gson gson = new Gson();

    @POST
    @Override
    public Response addDeviceOrganization(DeviceOrganization deviceOrganizationRequest) {
        if (deviceOrganizationRequest == null) {
            String errorMessage = "The payload of the device organization is incorrect.";
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
        }
        try {
            if (
                    deviceOrganizationRequest.getDeviceId() <= 0 ||
                            !(deviceOrganizationRequest.getParentDeviceId() == null ||
                                    deviceOrganizationRequest.getParentDeviceId() >= 0)
            ) {
                String errorMessage = "The payload of the device organization is incorrect.";
                return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
            }
            DeviceOrganizationService deviceOrganizationService = DeviceOrgAPIUtils.getDeviceOrganizationService();
            boolean resp = deviceOrganizationService.addDeviceOrganization(deviceOrganizationRequest);
            return Response.status(Response.Status.OK).entity(resp).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Override
    @Path("children")
    public Response getChildrenOfDeviceNode(
            @QueryParam("deviceId") int deviceId,
            @QueryParam("maxDepth") int maxDepth,
            @QueryParam("includeDevice") boolean includeDevice) {
        try {
            DeviceOrganizationService deviceOrganizationService = DeviceOrgAPIUtils.getDeviceOrganizationService();
            DeviceNode deviceNode = new DeviceNode();
            deviceNode.setDeviceId(deviceId);
            List<DeviceNode> children = deviceOrganizationService.getChildrenOfDeviceNode(deviceNode, maxDepth, includeDevice);
            return Response.status(Response.Status.OK).entity(children).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Override
    @Path("parents")
    public Response getParentsOfDeviceNode(
            @QueryParam("deviceId") int deviceId,
            @QueryParam("maxDepth") int maxDepth,
            @QueryParam("includeDevice") boolean includeDevice) {
        try {
            DeviceOrganizationService deviceOrganizationService = DeviceOrgAPIUtils.getDeviceOrganizationService();
            DeviceNode deviceNode = new DeviceNode();
            deviceNode.setDeviceId(deviceId);
            List<DeviceNode> parents = deviceOrganizationService.getParentsOfDeviceNode(deviceNode, maxDepth, includeDevice);
            return Response.status(Response.Status.OK).entity(parents).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Override
    @Path("/leafs")
    public Response getDeviceOrganizationLeafs(
            @QueryParam("offset") int offset,
            @QueryParam("limit") int limit) {
        RequestValidationUtil.validatePaginationParameters(offset, limit);
        try {
            DeviceOrganizationService deviceOrganizationService = DeviceOrgAPIUtils.getDeviceOrganizationService();
            PaginationRequest request = new PaginationRequest(offset, limit);
            List<DeviceOrganization> organizations = deviceOrganizationService.getDeviceOrganizationLeafs(request);
            return Response.status(Response.Status.OK).entity(organizations).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/roots")
    @Override
    public Response getDeviceOrganizationRoots(
            @QueryParam("offset") int offset,
            @QueryParam("limit") int limit) {
        RequestValidationUtil.validatePaginationParameters(offset, limit);
        try {
            DeviceOrganizationService deviceOrganizationService = DeviceOrgAPIUtils.getDeviceOrganizationService();
            PaginationRequest request = new PaginationRequest(offset, limit);
            List<DeviceOrganization> organizations = deviceOrganizationService.getDeviceOrganizationRoots(request);
            return Response.status(Response.Status.OK).entity(organizations).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Override
    @Path("{organizationId}")
    public Response getDeviceOrganizationById(@PathParam("organizationId") int organizationId) {
        try {
            DeviceOrganizationService deviceOrganizationService = DeviceOrgAPIUtils.getDeviceOrganizationService();
            DeviceOrganization organization = deviceOrganizationService.getDeviceOrganizationByID(organizationId);
            return Response.status(Response.Status.OK).entity(organization).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Override
    @Path("exists")
    public Response isDeviceOrganizationExist(
            @QueryParam("deviceId") int deviceId,
            @QueryParam("parentDeviceId") String parentDeviceId) {
        try {
            DeviceOrganizationService deviceOrganizationService = DeviceOrgAPIUtils.getDeviceOrganizationService();
            boolean exists;
            if (parentDeviceId.equals("null")) {
                exists = deviceOrganizationService.isDeviceOrganizationExist(deviceId, null);
            } else {
                exists = deviceOrganizationService.isDeviceOrganizationExist(deviceId, Integer.valueOf(parentDeviceId));
            }
            return Response.status(Response.Status.OK).entity(exists).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }


    @GET
    @Override
    @Path("organization")
    public Response getDeviceOrganizationByUniqueKey(
            @QueryParam("deviceId") int deviceId,
            @QueryParam("parentDeviceId") String parentDeviceId) {
        try {
            DeviceOrganizationService deviceOrganizationService = DeviceOrgAPIUtils.getDeviceOrganizationService();
            DeviceOrganization organization;
            if (parentDeviceId.equals("null")) {
                organization = deviceOrganizationService.getDeviceOrganizationByUniqueKey(deviceId, null);
            } else {
                organization = deviceOrganizationService.getDeviceOrganizationByUniqueKey(deviceId, Integer.valueOf(parentDeviceId));
            }
            return Response.status(Response.Status.OK).entity(organization).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Override
    public Response updateDeviceOrganization(DeviceOrganization deviceOrganization) {
        try {
            DeviceOrganizationService deviceOrganizationService = DeviceOrgAPIUtils.getDeviceOrganizationService();
            boolean resp = deviceOrganizationService.updateDeviceOrganization(deviceOrganization);
            return Response.status(Response.Status.OK).entity(resp).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Override
    @Path("{organizationId}")
    public Response deleteDeviceOrganizationById(@PathParam("organizationId") int organizationId) {
        try {
            DeviceOrganizationService deviceOrganizationService = DeviceOrgAPIUtils.getDeviceOrganizationService();
            boolean resp = deviceOrganizationService.deleteDeviceOrganizationByID(organizationId);
            return Response.status(Response.Status.OK).entity(resp).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Override
    @Path("associations/{deviceId}")
    public Response deleteDeviceAssociations(@PathParam("deviceId") int deviceId) {
        try {
            DeviceOrganizationService deviceOrganizationService = DeviceOrgAPIUtils.getDeviceOrganizationService();
            boolean resp = deviceOrganizationService.deleteDeviceAssociations(deviceId);
            return Response.status(Response.Status.OK).entity(resp).build();
        } catch (DeviceOrganizationMgtPluginException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
