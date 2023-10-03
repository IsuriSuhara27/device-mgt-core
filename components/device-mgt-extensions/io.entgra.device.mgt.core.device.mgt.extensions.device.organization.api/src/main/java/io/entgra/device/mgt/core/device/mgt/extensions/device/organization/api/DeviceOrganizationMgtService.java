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

import io.entgra.device.mgt.core.apimgt.annotations.Scope;
import io.entgra.device.mgt.core.apimgt.annotations.Scopes;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceOrganization;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

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

/**
 * This interface defines the RESTful web service endpoints for managing device organizations.
 */

@SwaggerDefinition(
        info = @Info(
                version = "1.0.0",
                title = "",
                extensions = {
                        @Extension(properties = {
                                @ExtensionProperty(name = "name", value = "DeviceOrganization Management"),
                                @ExtensionProperty(name = "context", value = "/api/device-mgt/v1.0/deviceOrganization"),
                        })
                }
        ),
        tags = {
                @Tag(name = "deviceOrganization_management", description = "DeviceOrganization management related REST-API. " +
                        "This can be used to manipulate device organization related details.")
        }
)
@Path("/deviceOrganization")
@Api(value = "DeviceOrganization Management", description = "This API carries all device Organization management " +
        "related operations.")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Scopes(scopes = {
        @Scope(
                name = "Device Organization",
                description = "Device Organization",
                key = "perm:devices:view",
                roles = {"Internal/devicemgt-user"},
                permissions = {"/device-mgt/devices/owning-device/view"}
        )
}
)
public interface DeviceOrganizationMgtService {

    String SCOPE = "scope";

    /**
     * Adds a new device organization.
     *
     * @param request The request containing the device organization information.
     * @return A response indicating the success or failure of the operation.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/add-device-organization")
    @ApiOperation(
            consumes = MediaType.TEXT_PLAIN,
            produces = MediaType.TEXT_PLAIN,
            httpMethod = "POST",
            value = "Add a new device Organization.",
            notes = "This will return a response to indicate when a new device organization is created.",
            tags = "Device Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:devices:view")
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message = "OK. \n Successfully fetched the device location.",
                            response = String.class),
            })
    Response addDeviceOrganization(DeviceOrganizationRequest request);

    /**
     * Retrieves a list of child nodes of a given device node, up to a specified depth.
     *
     * @param deviceId      The ID of the parent device node.
     * @param maxDepth      The maximum depth of child nodes to retrieve.
     * @param includeDevice Indicates whether to include device information in the retrieved nodes.
     * @return A response containing a list of child device nodes.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/children")
    Response getChildrenOfDeviceNode(
            @QueryParam("deviceId") int deviceId,
            @QueryParam("maxDepth") int maxDepth,
            @QueryParam("includeDevice") boolean includeDevice);

    /**
     * Retrieves a list of parent nodes of a given device node, up to a specified depth.
     *
     * @param deviceId      The ID of the child device node.
     * @param maxDepth      The maximum depth of parent nodes to retrieve.
     * @param includeDevice Indicates whether to include device information in the retrieved nodes.
     * @return A response containing a list of parent device nodes.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/parents")
    Response getParentsOfDeviceNode(
            @QueryParam("deviceId") int deviceId,
            @QueryParam("maxDepth") int maxDepth,
            @QueryParam("includeDevice") boolean includeDevice);

    /**
     * Retrieves a list of all device organizations.
     *
     * @return A response containing a list of all device organizations.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all")
    Response getAllDeviceOrganizations();

    /**
     * Retrieves a specific device organization by its organization ID.
     *
     * @param organizationId The organization ID of the device organization to retrieve.
     * @return A response containing the device organization with the specified ID.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{organizationId}")
    Response getDeviceOrganizationById(@PathParam("organizationId") int organizationId);

    /**
     * Checks if a device organization with the specified device and parent device IDs already exists.
     *
     * @param deviceId       The ID of the device.
     * @param parentDeviceId The ID of the parent device.
     * @return A response indicating whether the organization exists or not.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/exists")
    Response organizationExists(
            @QueryParam("deviceId") int deviceId,
            @QueryParam("parentDeviceId") int parentDeviceId);

    /**
     * Retrieve a device organization by its unique key (deviceId and parentDeviceId).
     *
     * @param deviceId       The ID of the device.
     * @param parentDeviceId The ID of the parent device.
     * @return A response containing the retrieved DeviceOrganization object, or null if not found.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/unique")
    Response getDeviceOrganizationByUniqueKey(
            @QueryParam("deviceId") int deviceId,
            @QueryParam("parentDeviceId") int parentDeviceId);

    /**
     * Checks whether a record with the specified device ID exists either in the deviceID column or
     * parentDeviceID column in the device organization table.
     *
     * @param deviceId The ID of the device to check.
     * @return A response indicating whether the device exists or not.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/device-exists")
    Response doesDeviceIdExist(@QueryParam("deviceId") int deviceId);

    /**
     * Updates a device organization.
     *
     * @param deviceOrganization The updated device organization.
     * @return A response indicating the success or failure of the operation.
     */
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/update")
    Response updateDeviceOrganization(DeviceOrganization deviceOrganization);

    /**
     * Deletes a device organization by its organization ID.
     *
     * @param organizationId The organization ID of the device organization to delete.
     * @return A response indicating the success or failure of the operation.
     */
    @DELETE
    @Path("/delete/{organizationId}")
    Response deleteDeviceOrganizationById(@PathParam("organizationId") int organizationId);

    /**
     * Deletes records associated with a particular device ID from the device organization table.
     * This method deletes records where the provided device ID matches either the deviceID column or
     * parentDeviceID column in the device organization table.
     *
     * @param deviceId The ID of the device for which associations should be deleted.
     * @return A response indicating the success or failure of the operation.
     */
    @DELETE
    @Path("/delete-associations/{deviceId}")
    Response deleteDeviceAssociations(@PathParam("deviceId") int deviceId);

}
