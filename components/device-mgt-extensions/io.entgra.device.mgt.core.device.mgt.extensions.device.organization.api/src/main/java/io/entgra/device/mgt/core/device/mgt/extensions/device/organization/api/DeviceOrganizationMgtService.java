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
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.api.beans.ErrorResponse;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceOrganization;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
                key = "dm:device-organization",
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
    @Path("/add-device-organization")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Add a new device Organization.",
            notes = "This endpoint allows you to add a new device organization.",
            tags = "Device Organization Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "dm:device-organization")
                    })
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message = "OK. \n Successfully created the device organization.",
                            response = String.class),
                    @ApiResponse(
                            code = 201,
                            message = "Created. Successfully created a new resource.",
                            response = Response.class),
                    @ApiResponse(
                            code = 400,
                            message = "Bad Request. Invalid input data.",
                            response = Response.class),
                    @ApiResponse(
                            code = 500,
                            message = "Internal Server Error. An error occurred while processing the request.",
                            response = Response.class)
            })
    Response addDeviceOrganization(DeviceOrganization request);

    /**
     * Retrieves a list of child nodes of a given device node, up to a specified depth.
     *
     * @param deviceId      The ID of the parent device node.
     * @param maxDepth      The maximum depth of child nodes to retrieve.
     * @param includeDevice Indicates whether to include device information in the retrieved nodes.
     * @return A response containing a list of child device nodes.
     */
    @GET
    @Path("/children")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Get Child Nodes of a Device Node",
            notes = "This endpoint allows you to retrieve a list of child nodes of a given device node up to a specified depth.",
            tags = "Device Organization Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "dm:device-organization")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK. Successfully retrieved the list of child nodes."
            ),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. Invalid input data.",
                    response = ErrorResponse.class
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. An error occurred while processing the request.",
                    response = ErrorResponse.class
            )
    })
    Response getChildrenOfDeviceNode(
            @ApiParam(value = "The ID of the parent device node.", required = true)
            @QueryParam("deviceId") int deviceId,
            @ApiParam(value = "The maximum depth of child nodes to retrieve.", required = true)
            @QueryParam("maxDepth") int maxDepth,
            @ApiParam(value = "Indicates whether to include device information in the retrieved nodes.", required = true)
            @QueryParam("includeDevice") boolean includeDevice
    );


    /**
     * Retrieves a list of parent nodes of a given device node, up to a specified depth.
     *
     * @param deviceId      The ID of the child device node.
     * @param maxDepth      The maximum depth of parent nodes to retrieve.
     * @param includeDevice Indicates whether to include device information in the retrieved nodes.
     * @return A response containing a list of parent device nodes.
     */
    @GET
    @Path("/parents")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Retrieve Parent Nodes of a Device Node",
            notes = "Get a list of parent nodes of a specified child device node, up to a specified depth.",
            tags = "Device Organization Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "dm:device-organization")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK. Successfully retrieved the list of parent nodes."),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. Invalid input data.",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. An error occurred while processing the request.",
                    response = ErrorResponse.class)
    })
    Response getParentsOfDeviceNode(
            @ApiParam(value = "The ID of the child device node.", required = true)
            @QueryParam("deviceId") int deviceId,
            @ApiParam(value = "The maximum depth of parent nodes to retrieve.", required = true)
            @QueryParam("maxDepth") int maxDepth,
            @ApiParam(value = "Indicates whether to include device information in the retrieved nodes.", required = true)
            @QueryParam("includeDevice") boolean includeDevice);


    /**
     * Retrieves a list of all device organizations.
     *
     * @return A response containing a list of all device organizations.
     */
    @GET
    @Path("/all")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Retrieve All Device Organizations",
            notes = "Get a list of all device organizations.",
            tags = "Device Organization Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "dm:device-organization")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK. Successfully retrieved the list of all device organizations."),
            @ApiResponse(
                    code = 404,
                    message = "Not Found. \n No organizations found.",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. An error occurred while processing the request.",
                    response = ErrorResponse.class)
    })
    Response getAllDeviceOrganizations();


    /**
     * Retrieves a specific device organization by its organization ID.
     *
     * @param organizationId The organization ID of the device organization to retrieve.
     * @return A response containing the device organization with the specifi
     * ed ID.
     */
    @GET
    @Path("/{organizationId}")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Retrieve Device Organization by ID",
            notes = "Get a specific device organization by its ID.",
            tags = "Device Organization Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "dm:device-organization")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK. Successfully retrieved the device organization by ID."),
            @ApiResponse(
                    code = 404,
                    message = "Not Found. The specified organization does not exist.",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. An error occurred while processing the request.",
                    response = ErrorResponse.class)
    })
    Response getDeviceOrganizationById(@PathParam("organizationId") int organizationId);


    /**
     * Checks if a device organization with the specified device and parent device IDs already exists.
     *
     * @param deviceId       The ID of the device.
     * @param parentDeviceId The ID of the parent device.
     * @return A response indicating whether the organization exists or not.
     */
    @GET
    @Path("/exists")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Check Device Organization Existence",
            notes = "Check if a device organization with the specified device and parent device IDs exists.",
            tags = "Device Organization Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "dm:device-organization")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK. The organization exists.",
                    response = Response.class),
            @ApiResponse(
                    code = 404,
                    message = "Not Found. The organization does not exist.",
                    response = Response.class),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. An error occurred while processing the request.",
                    response = ErrorResponse.class)
    })
    Response isDeviceOrganizationExist(
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
    @Path("/unique")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Get Device Organization by Unique Key",
            notes = "Retrieve a device organization by its unique key, which is a combination of deviceId and parentDeviceId.",
            tags = "Device Organization Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "dm:device-organization")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK. The organization exists.",
                    response = DeviceOrganization.class),
            @ApiResponse(
                    code = 404,
                    message = "Not Found. The specified organization does not exist."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. An error occurred while processing the request.",
                    response = ErrorResponse.class)
    })
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
    @Path("/device-exists")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Check if Device ID Exists in Device Organization",
            notes = "Checks whether a record with the specified device ID exists in the device organization table.",
            tags = "Device Organization Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "dm:device-organization")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK. The device exists in the device organization."),
            @ApiResponse(
                    code = 404,
                    message = "Not Found. The device does not exist in the device organization."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. An error occurred while processing the request.",
                    response = ErrorResponse.class)
    })
    Response doesDeviceIdExist(
            @ApiParam(value = "The ID of the device to check.", required = true)
            @QueryParam("deviceId") int deviceId);

    /**
     * Checks if a child device with the specified device ID already exists.
     *
     * @param deviceID The ID of the child device to check.
     * @return A response indicating whether the child device exists or not.
     */
    @GET
    @Path("/child-exists")
    @ApiOperation(
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Check if Child Device ID Exists in Device Organization",
            notes = "Checks whether a child device with the specified device ID exists in the device organization.",
            tags = "Device Organization Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "dm:device-organization")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK. The child device exists in the device organization."),
            @ApiResponse(
                    code = 404,
                    message = "Not Found. The child device does not exist in the device organization."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. An error occurred while processing the request.",
                    response = ErrorResponse.class)
    })
    Response isChildDeviceIdExist(
            @ApiParam(value = "The ID of the child device to check.", required = true)
            @QueryParam("deviceID") int deviceID);


    /**
     * Updates a device organization.
     *
     * @param deviceOrganization The updated device organization.
     * @return A response indicating the success or failure of the operation.
     */
    @PUT
    @Path("/update")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "PUT",
            value = "Update a Device Organization",
            notes = "This endpoint allows you to update a device organization.",
            tags = "Device Organization Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "dm:device-organization")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK. Successfully updated the device organization.",
                    response = Response.class),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. Invalid input data.",
                    response = Response.class),
            @ApiResponse(
                    code = 404,
                    message = "Not Found. The specified device organization does not exist.",
                    response = Response.class),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. An error occurred while processing the request.",
                    response = Response.class)
    })
    Response updateDeviceOrganization(
            @ApiParam(value = "The updated device organization.", required = true)
            DeviceOrganization deviceOrganization);


    /**
     * Deletes a device organization by its organization ID.
     *
     * @param organizationId The organization ID of the device organization to delete.
     * @return A response indicating the success or failure of the operation.
     */
    @DELETE
    @Path("/delete/{organizationId}")
    @ApiOperation(
            httpMethod = "DELETE",
            value = "Delete a Device Organization",
            notes = "This endpoint allows you to delete a device organization by its organization ID.",
            tags = "Device Organization Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "dm:device-organization")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK. Successfully deleted the device organization.",
                    response = Response.class),
            @ApiResponse(
                    code = 404,
                    message = "Not Found. The specified device organization does not exist.",
                    response = Response.class),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. An error occurred while processing the request.",
                    response = Response.class)
    })
    Response deleteDeviceOrganizationById(
            @ApiParam(value = "The organization ID of the device organization to delete.", required = true)
            @PathParam("organizationId") int organizationId);

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
    @ApiOperation(
            httpMethod = "DELETE",
            value = "Delete Device Associations",
            notes = "This endpoint allows you to delete records associated with a particular device ID from the device organization table.",
            tags = "Device Organization Management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "dm:device-organization")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK. Successfully deleted the device associations.",
                    response = Response.class),
            @ApiResponse(
                    code = 404,
                    message = "Not Found. No associations found for the specified device ID.",
                    response = Response.class),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. An error occurred while processing the request.",
                    response = Response.class)
    })
    Response deleteDeviceAssociations(
            @ApiParam(value = "The ID of the device for which associations should be deleted.", required = true)
            @PathParam("deviceId") int deviceId);


}
