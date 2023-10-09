package io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dao.util;


import io.entgra.device.mgt.core.device.mgt.common.Device;
import io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto.DeviceNode;
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

    /**
     * Helper method to create a Device Organization object from a ResultSet
     * @param rs The ResultSet containing the organization data.
     * @return A DeviceOrganization object.
     * @throws SQLException If there's an issue reading data from the ResultSet.
     */
    public static DeviceOrganization loadDeviceOrganization(ResultSet rs) throws SQLException {
        DeviceOrganization deviceOrganization = new DeviceOrganization();
        deviceOrganization.setOrganizationId(rs.getInt("ID"));
        deviceOrganization.setDeviceId(rs.getInt("DEVICE_ID"));
        if (rs.getInt("PARENT_DEVICE_ID") != 0) {
            deviceOrganization.setParentDeviceId(rs.getInt("PARENT_DEVICE_ID"));
        } else {
            deviceOrganization.setParentDeviceId(null);
        }
        deviceOrganization.setDeviceOrganizationMeta(rs.getString("DEVICE_ORGANIZATION_META"));
        deviceOrganization.setUpdateTime(rs.getDate("LAST_UPDATED_TIMESTAMP"));
        return deviceOrganization;
    }

    /**
     * Helper method to create a DeviceNode object from a ResultSet
     * @param rs The ResultSet containing device data.
     * @return A DeviceNode object.
     * @throws SQLException If there's an issue reading data from the ResultSet.
     */
    public static DeviceNode getDeviceFromResultSet(ResultSet rs) throws SQLException {
        DeviceNode node = new DeviceNode();
        node.setDeviceId(rs.getInt("ID"));
        Device device = new Device();
        device.setId(rs.getInt("ID"));
        device.setDescription(rs.getString("DESCRIPTION"));
        device.setName(rs.getString("NAME"));
        device.setType(rs.getString("DEVICE_TYPE_NAME"));
        device.setDeviceIdentifier(rs.getString("DEVICE_IDENTIFICATION"));
        node.setDevice(device);
        return node;
    }

}
