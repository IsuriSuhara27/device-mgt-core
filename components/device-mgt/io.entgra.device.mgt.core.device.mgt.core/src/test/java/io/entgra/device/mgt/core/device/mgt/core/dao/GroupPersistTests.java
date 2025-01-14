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

package io.entgra.device.mgt.core.device.mgt.core.dao;

import io.entgra.device.mgt.core.device.mgt.common.Device;
import io.entgra.device.mgt.core.device.mgt.common.EnrolmentInfo;
import io.entgra.device.mgt.core.device.mgt.common.GroupPaginationRequest;
import io.entgra.device.mgt.core.device.mgt.common.PaginationRequest;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.TransactionManagementException;
import io.entgra.device.mgt.core.device.mgt.common.group.mgt.DeviceGroup;
import io.entgra.device.mgt.core.device.mgt.core.common.BaseDeviceManagementTest;
import io.entgra.device.mgt.core.device.mgt.core.common.TestDataHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupPersistTests extends BaseDeviceManagementTest {

    private static final Log log = LogFactory.getLog(GroupPersistTests.class);
    private int groupId = -1;
    private GroupDAO groupDAO;

    @BeforeClass
    @Override
    public void init() throws Exception {
        initDataSource();
        groupDAO = GroupManagementDAOFactory.getGroupDAO();
    }

    @Test
    public void addGroupTest() {
        DeviceGroup deviceGroup = TestDataHolder.generateDummyGroupData();
        try {
            GroupManagementDAOFactory.beginTransaction();
            groupId = groupDAO.addGroup(deviceGroup, TestDataHolder.SUPER_TENANT_ID);
            GroupManagementDAOFactory.commitTransaction();
            log.debug("Group added to database. ID: " + groupId);
        } catch (GroupManagementDAOException e) {
            GroupManagementDAOFactory.rollbackTransaction();
            String msg = "Error occurred while adding device type '" + deviceGroup.getName() + "'.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while initiating transaction.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } finally {
            GroupManagementDAOFactory.closeConnection();
        }
        DeviceGroup group = getGroupById(groupId);
        if (!isMock()) {
            Assert.assertNotNull(group, "Group is null");
            log.debug("Group name: " + group.getName());
        }
    }

    @Test(dependsOnMethods = {"addGroupTest"})
    public void getGroupTest() {
        try {
            GroupManagementDAOFactory.openConnection();
            GroupPaginationRequest request = new GroupPaginationRequest(0, 10);
            request.setGroupName(null);
            request.setOwner(null);
            List<DeviceGroup> groups = groupDAO.getGroups(request, TestDataHolder.SUPER_TENANT_ID);
            if (!isMock()) {
                Assert.assertNotEquals(groups.size(), 0, "No groups found");
                Assert.assertNotNull(groups.get(0), "Group is null");
                log.debug("No of Groups found: " + groups.size());
            }
        } catch (GroupManagementDAOException e) {
            String msg = "Error occurred while find group by name.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred while opening a connection to the data source.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } finally {
            GroupManagementDAOFactory.closeConnection();
        }
    }

    @Test(dependsOnMethods = {"addGroupTest"})
    public void shareGroupTest() {
        try {
            GroupManagementDAOFactory.beginTransaction();
            List<String> addedRoles = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                String role = "role-" + i;
                groupDAO.addRole(groupId, role, TestDataHolder.SUPER_TENANT_ID);
                addedRoles.add(role);
            }
            GroupManagementDAOFactory.commitTransaction();
            List<String> roles = groupDAO.getRoles(groupId, TestDataHolder.SUPER_TENANT_ID);
            if (!isMock()) {
                Assert.assertEquals(roles, addedRoles, "Added roles are not equal to returned roles.");
            }
            log.debug("Group shared with roles.");
        } catch (GroupManagementDAOException e) {
            String msg = "Error occurred while find group by name.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while opening a connection to the data source.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } finally {
            GroupManagementDAOFactory.closeConnection();
        }
    }

    @Test(dependsOnMethods = {"shareGroupTest"})
    public void getShareGroupTest() {
        try {
            GroupManagementDAOFactory.openConnection();
            List<String> roles = groupDAO.getRoles(groupId, TestDataHolder.SUPER_TENANT_ID);
            if (!isMock()) {
                roles.remove(0);
            }
            List<DeviceGroup> deviceGroups = groupDAO.getGroups(roles.toArray(new String[roles.size()]), TestDataHolder.SUPER_TENANT_ID);
            if (!isMock()) {
                Assert.assertEquals(deviceGroups.size(), 1, "Unexpected number of device groups found with role.");
                Assert.assertEquals(deviceGroups.get(0).getGroupId(), groupId, "Unexpected groupId found with role.");
            }
            log.debug("Group found for given roles.");
        } catch (GroupManagementDAOException e) {
            String msg = "Error occurred while getting groups shared with roles.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred while opening a connection to the data source.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } finally {
            GroupManagementDAOFactory.closeConnection();
        }
    }

    @Test(dependsOnMethods = {"getShareGroupTest"})
    public void unshareGroupTest() {
        try {
            GroupManagementDAOFactory.beginTransaction();
            List<String> rolesToRemove = groupDAO.getRoles(groupId, TestDataHolder.SUPER_TENANT_ID);
            for (String role : rolesToRemove) {
                groupDAO.removeRole(groupId, role, TestDataHolder.SUPER_TENANT_ID);
            }
            GroupManagementDAOFactory.commitTransaction();
            List<String> roles = groupDAO.getRoles(groupId, TestDataHolder.SUPER_TENANT_ID);
            GroupManagementDAOFactory.closeConnection();
            if (!isMock()) {
                Assert.assertNotEquals(roles, rolesToRemove, "Roles not removed.");
            }
            log.debug("Group unshared with given roles.");
        } catch (GroupManagementDAOException e) {
            GroupManagementDAOFactory.closeConnection();
            String msg = "Error occurred while find group by name.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (TransactionManagementException e) {
            GroupManagementDAOFactory.closeConnection();
            String msg = "Error occurred while opening a connection to the data source.";
            log.error(msg, e);
            Assert.fail(msg, e);
        }
    }

    @Test(dependsOnMethods = {"addGroupTest"})
    public void addDeviceToGroupTest() {
        Device initialTestDevice = TestDataHolder.initialTestDevice;
        DeviceGroup deviceGroup = getGroupById(groupId);
        Assert.assertNotNull(deviceGroup, "Group is null");
        try {
            GroupManagementDAOFactory.beginTransaction();
            groupDAO.addDevice(deviceGroup.getGroupId(), initialTestDevice.getId(), TestDataHolder.SUPER_TENANT_ID);
            GroupManagementDAOFactory.commitTransaction();
            GroupManagementDAOFactory.closeConnection();
            log.debug("Device added to group.");
        } catch (GroupManagementDAOException e) {
            GroupManagementDAOFactory.rollbackTransaction();
            GroupManagementDAOFactory.closeConnection();
            String msg = "Error occurred while adding device '" + initialTestDevice.getName() + "'.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (TransactionManagementException e) {
            GroupManagementDAOFactory.closeConnection();
            String msg = "Error occurred while initiating transaction.";
            log.error(msg, e);
            Assert.fail(msg, e);
        }

        try {
            GroupManagementDAOFactory.openConnection();
            List<Device> groupedDevices = groupDAO.getDevices(deviceGroup.getGroupId(), 0, 10, TestDataHolder.SUPER_TENANT_ID);
            GroupManagementDAOFactory.closeConnection();
            if (!isMock()) {
                Assert.assertNotEquals(groupedDevices.size(), 0, "No device found");
                Assert.assertNotNull(groupedDevices.get(0), "Device is null");
                Assert.assertEquals(groupedDevices.get(0).getId(), initialTestDevice.getId(), "Device ids not matched");
            }
        } catch (GroupManagementDAOException e) {
            GroupManagementDAOFactory.closeConnection();
            String msg = "Error occurred while retrieving group details.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (SQLException e) {
            GroupManagementDAOFactory.closeConnection();
            String msg = "Error occurred while opening a connection to the data source.";
            log.error(msg, e);
            Assert.fail(msg, e);
        }
    }

    @Test(dependsOnMethods = {"addDeviceToGroupTest"})
    public void removeDeviceFromGroupTest() {
        Device initialTestDevice = TestDataHolder.initialTestDevice;
        DeviceGroup deviceGroup = getGroupById(groupId);
        Assert.assertNotNull(deviceGroup, "Group is null");
        try {
            GroupManagementDAOFactory.beginTransaction();
            groupDAO.removeDevice(deviceGroup.getGroupId(), initialTestDevice.getId(), TestDataHolder.SUPER_TENANT_ID);
            GroupManagementDAOFactory.commitTransaction();
            GroupManagementDAOFactory.closeConnection();
            log.debug("Device added to group.");
        } catch (GroupManagementDAOException e) {
            GroupManagementDAOFactory.rollbackTransaction();
            GroupManagementDAOFactory.closeConnection();
            String msg = "Error occurred while adding device '" + initialTestDevice.getDeviceIdentifier() + "'.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (TransactionManagementException e) {
            GroupManagementDAOFactory.closeConnection();
            String msg = "Error occurred while initiating transaction.";
            log.error(msg, e);
            Assert.fail(msg, e);
        }
    }

    @Test(dependsOnMethods = {"removeDeviceFromGroupTest", "unshareGroupTest"})
    public void updateGroupTest() {
        String name = "Test Updated";
        String desc = "Desc updated";
        DeviceGroup group = getGroupById(groupId);
        Assert.assertNotNull(group, "Group is null");
        group.setName(name);
        group.setDescription(desc);
        try {
            GroupManagementDAOFactory.beginTransaction();
            groupDAO.updateGroup(group, groupId, TestDataHolder.SUPER_TENANT_ID);
            GroupManagementDAOFactory.commitTransaction();
            log.debug("Group updated");
        } catch (GroupManagementDAOException e) {
            GroupManagementDAOFactory.rollbackTransaction();
            String msg = "Error occurred while updating group details.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while initiating transaction.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } finally {
            GroupManagementDAOFactory.closeConnection();
        }
        if (!isMock()) {
            group = getGroupById(groupId);
            Assert.assertNotNull(group, "Group is null");
            Assert.assertEquals(group.getName(), name, "Group name");
            Assert.assertEquals(group.getDescription(), desc, "Group description");
        }
    }

    @Test(dependsOnMethods = {"updateGroupTest"})
    public void deleteGroupTest() {
        DeviceGroup group = getGroupById(groupId);
        try {
            Assert.assertNotNull(group, "Group is null");
            GroupManagementDAOFactory.beginTransaction();
            groupDAO.deleteGroup(group.getGroupId(), TestDataHolder.SUPER_TENANT_ID);
            GroupManagementDAOFactory.commitTransaction();
            log.debug("Group deleted");
        } catch (GroupManagementDAOException e) {
            GroupManagementDAOFactory.rollbackTransaction();
            String msg = "Error occurred while updating group details.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while initiating transaction.";
            log.error(msg, e);
            Assert.fail(msg, e);
        }
        finally {
            GroupManagementDAOFactory.closeConnection();
        }
        group = getGroupById(groupId);
        if (!isMock()) {
            Assert.assertNull(group, "Group is not deleted");
        }
    }

    private DeviceGroup getGroupById(int groupId) {
        try {
            GroupManagementDAOFactory.openConnection();
            DeviceGroup deviceGroup = groupDAO.getGroup(groupId, TestDataHolder.SUPER_TENANT_ID);
            if (deviceGroup == null && isMock()) {
                deviceGroup = new DeviceGroup();
                deviceGroup.setGroupId(groupId);
            }
            return deviceGroup;
        } catch (GroupManagementDAOException e) {
            String msg = "Error occurred while retrieving group details.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (SQLException e) {
            String msg = "Error occurred while opening a connection to the data source.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } finally {
            GroupManagementDAOFactory.closeConnection();
        }
        return null;
    }

    @Test(dependsOnMethods = {"addDeviceToGroupTest"})
    public void getAllDevicesOfGroupWithStatus() {
        DeviceGroup deviceGroup = getGroupById(groupId);
        Assert.assertNotNull(deviceGroup, "Group is null");
        List<String> deviceStatus = new ArrayList<>();
        deviceStatus.add(EnrolmentInfo.Status.ACTIVE.name());
        try {
            GroupManagementDAOFactory.beginTransaction();
            groupDAO.getAllDevicesOfGroup(deviceGroup.getName(), deviceStatus, TestDataHolder.SUPER_TENANT_ID);
            GroupManagementDAOFactory.commitTransaction();
        } catch (GroupManagementDAOException e) {
            GroupManagementDAOFactory.rollbackTransaction();
            String msg = "Error occurred while getting devices of group '" + groupId + "'.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while initiating transaction.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } finally {
            GroupManagementDAOFactory.closeConnection();
        }
    }

    @Test(dependsOnMethods = {"addDeviceToGroupTest"})
    public void getAllDevicesOfGroup() {
        DeviceGroup deviceGroup = getGroupById(groupId);
        Assert.assertNotNull(deviceGroup, "Group is null");
        try {
            GroupManagementDAOFactory.beginTransaction();
            groupDAO.getAllDevicesOfGroup(deviceGroup.getName(), TestDataHolder.SUPER_TENANT_ID);
            GroupManagementDAOFactory.commitTransaction();
        } catch (GroupManagementDAOException e) {
            GroupManagementDAOFactory.rollbackTransaction();
            String msg = "Error occurred while getting devices of group '" + groupId + "'.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while initiating transaction.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } finally {
            GroupManagementDAOFactory.closeConnection();
        }
    }

    @Test(dependsOnMethods = {"addDeviceToGroupTest"})
    public void getGroupUnassignedDevices() {
        DeviceGroup deviceGroup = getGroupById(groupId);
        Device device = TestDataHolder.initialTestDevice;
        Assert.assertNotNull(deviceGroup, "Group is null");
        PaginationRequest pr = new PaginationRequest(0,10);
        pr.setDeviceType(device.getType());
        List<String> groupNames = new ArrayList<>();
        groupNames.add(deviceGroup.getName());
        try {
            GroupManagementDAOFactory.beginTransaction();
            groupDAO.getGroupUnassignedDevices(pr, groupNames);
            GroupManagementDAOFactory.commitTransaction();
        } catch (GroupManagementDAOException e) {
            GroupManagementDAOFactory.rollbackTransaction();
            String msg = "Error occurred while getting devices of group '" + groupId + "'.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while initiating transaction.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } finally {
            GroupManagementDAOFactory.closeConnection();
        }
    }

    @Test(dependsOnMethods = {"addDeviceToGroupTest"})
    public void getOwnGroupsCount() {
        try {
            GroupManagementDAOFactory.beginTransaction();
            groupDAO.getOwnGroupsCount(TestDataHolder.OWNER, TestDataHolder.SUPER_TENANT_ID, "/");
            GroupManagementDAOFactory.commitTransaction();
        } catch (GroupManagementDAOException e) {
            GroupManagementDAOFactory.rollbackTransaction();
            String msg = "Error occurred while getting own group count for '" + TestDataHolder.OWNER + "'.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while initiating transaction.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } finally {
            GroupManagementDAOFactory.closeConnection();
        }
    }

    @Test(dependsOnMethods = {"addDeviceToGroupTest"})
    public void getOwnGroups() {
        try {
            GroupManagementDAOFactory.beginTransaction();
            groupDAO.getOwnGroups(TestDataHolder.OWNER, TestDataHolder.SUPER_TENANT_ID);
            GroupManagementDAOFactory.commitTransaction();
        } catch (GroupManagementDAOException e) {
            GroupManagementDAOFactory.rollbackTransaction();
            String msg = "Error occurred while getting own groups for '" + TestDataHolder.OWNER + "'.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while initiating transaction.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } finally {
            GroupManagementDAOFactory.closeConnection();
        }
    }

    @Test(dependsOnMethods = {"addDeviceToGroupTest"})
    public void getOwnGroupIds() {
        try {
            GroupManagementDAOFactory.beginTransaction();
            groupDAO.getOwnGroupIds(TestDataHolder.OWNER, TestDataHolder.SUPER_TENANT_ID);
            GroupManagementDAOFactory.commitTransaction();
        } catch (GroupManagementDAOException e) {
            GroupManagementDAOFactory.rollbackTransaction();
            String msg = "Error occurred while getting own group Ids for '" + TestDataHolder.OWNER + "'.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while initiating transaction.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } finally {
            GroupManagementDAOFactory.closeConnection();
        }
    }

    @Test(dependsOnMethods = {"addDeviceToGroupTest"})
    public void getDeviceCount() {
        DeviceGroup deviceGroup = getGroupById(groupId);
        Assert.assertNotNull(deviceGroup, "Group is null");
        try {
            GroupManagementDAOFactory.beginTransaction();
            groupDAO.getDeviceCount(deviceGroup.getGroupId(), TestDataHolder.SUPER_TENANT_ID);
            GroupManagementDAOFactory.commitTransaction();
        } catch (GroupManagementDAOException e) {
            GroupManagementDAOFactory.rollbackTransaction();
            String msg = "Error occurred while getting device count for '" +groupId+ "'.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while initiating transaction.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } finally {
            GroupManagementDAOFactory.closeConnection();
        }
    }

    @Test(dependsOnMethods = {"addDeviceToGroupTest"})
    public void isDeviceMappedToGroup() {
        DeviceGroup deviceGroup = getGroupById(groupId);
        Device device = TestDataHolder.initialTestDevice;
        Assert.assertNotNull(deviceGroup, "Group is null");
        try {
            GroupManagementDAOFactory.beginTransaction();
            groupDAO.isDeviceMappedToGroup(deviceGroup.getGroupId(), Integer.parseInt(device.getDeviceIdentifier()), TestDataHolder.SUPER_TENANT_ID);
            GroupManagementDAOFactory.commitTransaction();
        } catch (GroupManagementDAOException e) {
            GroupManagementDAOFactory.rollbackTransaction();
            String msg = "Error occurred while checking device map to group for '" +groupId+ "'.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while initiating transaction.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } finally {
            GroupManagementDAOFactory.closeConnection();
        }
    }

    @Test(dependsOnMethods = {"addDeviceToGroupTest"})
    public void getGroupCount() {
        DeviceGroup deviceGroup = getGroupById(groupId);
        GroupPaginationRequest pr = new GroupPaginationRequest(0,10);
        pr.setGroupName(deviceGroup.getName());
        Assert.assertNotNull(deviceGroup, "Group is null");
        try {
            GroupManagementDAOFactory.beginTransaction();
            groupDAO.getGroupCount(pr, TestDataHolder.SUPER_TENANT_ID);
            GroupManagementDAOFactory.commitTransaction();
        } catch (GroupManagementDAOException e) {
            GroupManagementDAOFactory.rollbackTransaction();
            String msg = "Error occurred while getting group count for '" +TestDataHolder.SUPER_TENANT_ID+ "'.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while initiating transaction.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } finally {
            GroupManagementDAOFactory.closeConnection();
        }
    }

    @Test(dependsOnMethods = {"addDeviceToGroupTest"})
    public void getGroupCountWithStatus() {
        DeviceGroup deviceGroup = getGroupById(groupId);
        Assert.assertNotNull(deviceGroup, "Group is null");
        try {
            GroupManagementDAOFactory.beginTransaction();
            groupDAO.getGroupCount(TestDataHolder.SUPER_TENANT_ID, EnrolmentInfo.Status.ACTIVE.name());
            GroupManagementDAOFactory.commitTransaction();
        } catch (GroupManagementDAOException e) {
            GroupManagementDAOFactory.rollbackTransaction();
            String msg = "Error occurred while getting group count for" + TestDataHolder.SUPER_TENANT_ID;
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while initiating transaction.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } finally {
            GroupManagementDAOFactory.closeConnection();
        }
    }

    @Test(dependsOnMethods = {"addDeviceToGroupTest"})
    public void getRootGroups() {
        DeviceGroup deviceGroup = getGroupById(groupId);
        Assert.assertNotNull(deviceGroup, "Group is null");
        try {
            GroupManagementDAOFactory.beginTransaction();
            groupDAO.getRootGroups(TestDataHolder.SUPER_TENANT_ID);
            GroupManagementDAOFactory.commitTransaction();
        } catch (GroupManagementDAOException e) {
            GroupManagementDAOFactory.rollbackTransaction();
            String msg = "Error occurred while getting group count for " + TestDataHolder.SUPER_TENANT_ID;
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while initiating transaction.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } finally {
            GroupManagementDAOFactory.closeConnection();
        }
    }

    @Test(dependsOnMethods = {"addDeviceToGroupTest"})
    public void getAllGroupProperties() {
        DeviceGroup deviceGroup = getGroupById(groupId);
        Assert.assertNotNull(deviceGroup, "Group is null");
        try {
            GroupManagementDAOFactory.beginTransaction();
            groupDAO.getAllGroupProperties(groupId, TestDataHolder.SUPER_TENANT_ID);
            GroupManagementDAOFactory.commitTransaction();
        } catch (GroupManagementDAOException e) {
            GroupManagementDAOFactory.rollbackTransaction();
            String msg = "Error occurred while getting groups for " + TestDataHolder.SUPER_TENANT_ID;
            log.error(msg, e);
            Assert.fail(msg, e);
        } catch (TransactionManagementException e) {
            String msg = "Error occurred while initiating transaction.";
            log.error(msg, e);
            Assert.fail(msg, e);
        } finally {
            GroupManagementDAOFactory.closeConnection();
        }
    }
}
