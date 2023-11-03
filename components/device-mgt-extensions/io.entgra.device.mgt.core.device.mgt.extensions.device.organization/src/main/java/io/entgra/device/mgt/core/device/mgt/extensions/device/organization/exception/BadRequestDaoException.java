package io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception;

/**
 * This exception is thrown when a bad request is encountered in the Device Organization Management DAO layer.
 * It typically indicates issues with the input parameters or data during DAO operations.
 */
public class BadRequestDaoException extends DeviceOrganizationMgtDAOException {

    private static final long serialVersionUID = -6275360486437601206L;

    public BadRequestDaoException(String message) {
        super(message);
    }

    public BadRequestDaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
