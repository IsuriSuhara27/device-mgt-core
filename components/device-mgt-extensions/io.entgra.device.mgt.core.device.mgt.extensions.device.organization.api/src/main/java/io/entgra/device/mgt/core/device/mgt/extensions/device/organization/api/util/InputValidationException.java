package io.entgra.device.mgt.core.device.mgt.extensions.device.organization.api.util;

import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.api.beans.ErrorResponse;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import java.io.Serializable;

public class InputValidationException extends BadRequestException implements Serializable {

    private static final long serialVersionUID = 147843579458906890L;

    public InputValidationException(ErrorResponse error) {
        super(Response.status(Response.Status.BAD_REQUEST).entity(error).build());
    }
}
