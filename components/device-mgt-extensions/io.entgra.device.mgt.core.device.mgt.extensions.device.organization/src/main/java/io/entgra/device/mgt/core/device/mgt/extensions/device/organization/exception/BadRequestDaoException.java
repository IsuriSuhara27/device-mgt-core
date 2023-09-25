package io.entgra.device.mgt.core.device.mgt.extensions.device.organization.exception;

public class BadRequestDaoException extends DeviceOrganizationMgtDAOException{

    private static final long serialVersionUID = -6275360486437601206L;

    public BadRequestDaoException(String message) {
        super(message);
    }

    public BadRequestDaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
