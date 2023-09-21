package io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.util;


import io.entgra.device.mgt.core.device.mgt.common.Device;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceOrganization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class includes the utility methods required by DeviceOrganizationMgt functionalities.
 */
public class DeviceOrganizationDaoUtil {

    private static final Log log = LogFactory.getLog(DeviceOrganizationDaoUtil.class);

    public static DeviceOrganization loadDeviceOrganization(ResultSet rs) throws SQLException {
        DeviceOrganization deviceOrganization = new DeviceOrganization() {
        };
        deviceOrganization.setOrganizationId(rs.getInt("ID"));
        deviceOrganization.setDeviceId(rs.getInt("DEVICE_ID"));
        deviceOrganization.setParentDeviceId(rs.getInt("PARENT_DEVICE_ID"));
        deviceOrganization.setUpdateTime(rs.getDate("LAST_UPDATED_TIMESTAMP"));
        deviceOrganization.setStatus(DeviceOrganization.DeviceOrganizationStatus.valueOf(rs.getString("STATUS")));
        return deviceOrganization;
    }

    // Helper method to create a Device object from a ResultSet
    public static Device getDeviceFromResultSet(ResultSet rs) throws SQLException {
        Device device = new Device();
        device.setId(rs.getInt("ID"));
        device.setDescription(rs.getString("DESCRIPTION"));
        device.setName(rs.getString("NAME"));
        device.setType(rs.getString("DEVICE_TYPE_NAME"));
        device.setDeviceIdentifier(rs.getString("DEVICE_IDENTIFICATION"));
        return device;
    }

}
