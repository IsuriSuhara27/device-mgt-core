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
package io.entgra.device.mgt.core.device.mgt.core.config;

import io.entgra.device.mgt.core.device.mgt.common.enrollment.notification.EnrollmentNotificationConfiguration;
import io.entgra.device.mgt.core.device.mgt.common.roles.config.DefaultRoles;
import io.entgra.device.mgt.core.device.mgt.core.config.analytics.OperationAnalyticsConfiguration;
import io.entgra.device.mgt.core.device.mgt.core.config.archival.ArchivalConfiguration;
import io.entgra.device.mgt.core.device.mgt.core.config.cache.BillingCacheConfiguration;
import io.entgra.device.mgt.core.device.mgt.core.config.cache.CertificateCacheConfiguration;
import io.entgra.device.mgt.core.device.mgt.core.config.cache.DeviceCacheConfiguration;
import io.entgra.device.mgt.core.device.mgt.core.config.cache.GeoFenceCacheConfiguration;
import io.entgra.device.mgt.core.device.mgt.core.config.enrollment.guide.EnrollmentGuideConfiguration;
import io.entgra.device.mgt.core.device.mgt.core.config.operation.timeout.OperationTimeoutConfiguration;
import io.entgra.device.mgt.core.device.mgt.core.config.metadata.mgt.MetaDataConfiguration;
import io.entgra.device.mgt.core.device.mgt.core.event.config.EventOperationTaskConfiguration;
import io.entgra.device.mgt.core.device.mgt.core.config.geo.location.GeoLocationConfiguration;
import io.entgra.device.mgt.core.device.mgt.core.config.identity.IdentityConfigurations;
import io.entgra.device.mgt.core.device.mgt.core.config.keymanager.KeyManagerConfigurations;
import io.entgra.device.mgt.core.device.mgt.core.config.pagination.PaginationConfiguration;
import io.entgra.device.mgt.core.device.mgt.core.config.policy.PolicyConfiguration;
import io.entgra.device.mgt.core.device.mgt.core.config.pull.notification.PullNotificationConfiguration;
import io.entgra.device.mgt.core.device.mgt.core.config.push.notification.PushNotificationConfiguration;
import io.entgra.device.mgt.core.device.mgt.core.config.remote.session.RemoteSessionConfiguration;
import io.entgra.device.mgt.core.device.mgt.core.config.status.task.DeviceStatusTaskConfig;
import io.entgra.device.mgt.core.device.mgt.core.config.task.TaskConfiguration;
import io.entgra.device.mgt.core.device.mgt.core.config.permission.DefaultPermissions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents Device Mgt configuration.
 */
@XmlRootElement(name = "DeviceMgtConfiguration")
@SuppressWarnings("unused")
public final class DeviceManagementConfig {

    private DeviceManagementConfigRepository deviceManagementConfigRepository;
    private TaskConfiguration taskConfiguration;
    private IdentityConfigurations identityConfigurations;
    private KeyManagerConfigurations keyManagerConfigurations;
    private PolicyConfiguration policyConfiguration;
    private PaginationConfiguration paginationConfiguration;
    private PushNotificationConfiguration pushNotificationConfiguration;
    private PullNotificationConfiguration pullNotificationConfiguration;
    private DeviceStatusTaskConfig deviceStatusTaskConfig;
    private DeviceCacheConfiguration deviceCacheConfiguration;
    private GeoFenceCacheConfiguration geoFenceCacheConfiguration;
    private BillingCacheConfiguration billingCacheConfiguration;
    private EventOperationTaskConfiguration eventOperationTaskConfiguration;
    private CertificateCacheConfiguration certificateCacheConfiguration;
    private OperationAnalyticsConfiguration operationAnalyticsConfiguration;
    private GeoLocationConfiguration geoLocationConfiguration;
    private String defaultGroupsConfiguration;
    private RemoteSessionConfiguration remoteSessionConfiguration;
    private ArchivalConfiguration archivalConfiguration;
    private EnrollmentNotificationConfiguration enrollmentNotificationConfiguration;
    private DefaultRoles defaultRoles;
    private OperationTimeoutConfiguration operationTimeoutConfiguration;
    private MetaDataConfiguration metaDataConfiguration;
    private EnrollmentGuideConfiguration enrollmentGuideConfiguration;

    private DefaultPermissions defaultPermissions;

    @XmlElement(name = "ManagementRepository", required = true)
    public DeviceManagementConfigRepository getDeviceManagementConfigRepository() {
        return deviceManagementConfigRepository;
    }

    public void setDeviceManagementConfigRepository(DeviceManagementConfigRepository deviceManagementConfigRepository) {
        this.deviceManagementConfigRepository = deviceManagementConfigRepository;
    }

    @XmlElement(name = "IdentityConfiguration", required = true)
    public IdentityConfigurations getIdentityConfigurations() {
        return identityConfigurations;
    }


    public void setIdentityConfigurations(IdentityConfigurations identityConfigurations) {
        this.identityConfigurations = identityConfigurations;
    }

    @XmlElement(name = "KeyManagerConfiguration", required = true)
    public KeyManagerConfigurations getKeyManagerConfigurations() {
        return keyManagerConfigurations;
    }

    public void setKeyManagerConfigurations(KeyManagerConfigurations keyManagerConfigurations) {
        this.keyManagerConfigurations = keyManagerConfigurations;
    }

    @XmlElement(name = "PolicyConfiguration", required = true)
    public PolicyConfiguration getPolicyConfiguration() {
        return policyConfiguration;
    }

    public void setPolicyConfiguration(PolicyConfiguration policyConfiguration) {
        this.policyConfiguration = policyConfiguration;
    }

    @XmlElement(name = "TaskConfiguration", required = true)
    public TaskConfiguration getTaskConfiguration() {
        return taskConfiguration;
    }

    public void setTaskConfiguration(TaskConfiguration taskConfiguration) {
        this.taskConfiguration = taskConfiguration;
    }

    @XmlElement(name = "PaginationConfiguration", required = true)
    public PaginationConfiguration getPaginationConfiguration() {
        return paginationConfiguration;
    }

    public void setPaginationConfiguration(PaginationConfiguration paginationConfiguration) {
        this.paginationConfiguration = paginationConfiguration;
    }

    @XmlElement(name = "PushNotificationConfiguration", required = true)
    public PushNotificationConfiguration getPushNotificationConfiguration() {
        return pushNotificationConfiguration;
    }

    public void setPushNotificationConfiguration(PushNotificationConfiguration pushNotificationConfiguration) {
        this.pushNotificationConfiguration = pushNotificationConfiguration;
    }

    @XmlElement(name = "PullNotificationConfiguration", required = true)
    public PullNotificationConfiguration getPullNotificationConfiguration() {
        return pullNotificationConfiguration;
    }

    public void setPullNotificationConfiguration(PullNotificationConfiguration pullNotificationConfiguration) {
        this.pullNotificationConfiguration = pullNotificationConfiguration;
    }

    @XmlElement(name = "DeviceStatusTaskConfig", required = true)
    public DeviceStatusTaskConfig getDeviceStatusTaskConfig() {
        return deviceStatusTaskConfig;
    }

    public void setDeviceStatusTaskConfig(DeviceStatusTaskConfig deviceStatusTaskConfig) {
        this.deviceStatusTaskConfig = deviceStatusTaskConfig;
    }

    @XmlElement(name = "DeviceCacheConfiguration", required = true)
    public DeviceCacheConfiguration getDeviceCacheConfiguration() {
        return deviceCacheConfiguration;
    }

    public void setDeviceCacheConfiguration(DeviceCacheConfiguration deviceCacheConfiguration) {
        this.deviceCacheConfiguration = deviceCacheConfiguration;
    }

    @XmlElement(name = "GeoFenceCacheConfiguration", required = true)
    public GeoFenceCacheConfiguration getGeoFenceCacheConfiguration() {
        return geoFenceCacheConfiguration;
    }

    public void setGeoFenceCacheConfiguration(GeoFenceCacheConfiguration geoFenceCacheConfiguration) {
        this.geoFenceCacheConfiguration = geoFenceCacheConfiguration;
    }

    @XmlElement(name = "BillingCacheConfiguration", required = true)
    public BillingCacheConfiguration getBillingCacheConfiguration() {
        return billingCacheConfiguration;
    }

    public void setBillingCacheConfiguration(BillingCacheConfiguration billingCacheConfiguration) {
        this.billingCacheConfiguration = billingCacheConfiguration;
    }

    @XmlElement(name = "EventOperationTaskConfiguration", required = true)
    public EventOperationTaskConfiguration getEventOperationTaskConfiguration() {
        return eventOperationTaskConfiguration;
    }

    public void setEventOperationTaskConfiguration(EventOperationTaskConfiguration eventOperationTaskConfiguration) {
        this.eventOperationTaskConfiguration = eventOperationTaskConfiguration;
    }

    @XmlElement(name = "CertificateCacheConfiguration", required = true)
    public CertificateCacheConfiguration getCertificateCacheConfiguration() {
        return certificateCacheConfiguration;
    }

    public void setCertificateCacheConfiguration(CertificateCacheConfiguration certificateCacheConfiguration) {
        this.certificateCacheConfiguration = certificateCacheConfiguration;
    }

    @XmlElement(name = "OperationAnalyticsConfiguration", required = true)
    public OperationAnalyticsConfiguration getOperationAnalyticsConfiguration() {
        return operationAnalyticsConfiguration;
    }

    public void setOperationAnalyticsConfiguration(OperationAnalyticsConfiguration operationAnalyticsConfiguration) {
        this.operationAnalyticsConfiguration = operationAnalyticsConfiguration;
    }

    @XmlElement(name = "GeoLocationConfiguration", required = true)
    public GeoLocationConfiguration getGeoLocationConfiguration() {
        return geoLocationConfiguration;
    }

    public void setGeoLocationConfiguration(GeoLocationConfiguration geoLocationConfiguration) {
        this.geoLocationConfiguration = geoLocationConfiguration;
    }

    @XmlElement(name = "DefaultGroupsConfiguration", required = true)
    public String getDefaultGroupsConfiguration() {
        return defaultGroupsConfiguration;
    }

    public void setDefaultGroupsConfiguration(String defaultGroupsConfiguration) {
        this.defaultGroupsConfiguration = defaultGroupsConfiguration;
    }

    @XmlElement(name = "ArchivalConfiguration", required = true)
    public ArchivalConfiguration getArchivalConfiguration() {
        return archivalConfiguration;
    }

    public void setArchivalConfiguration(ArchivalConfiguration archivalConfiguration) {
        this.archivalConfiguration = archivalConfiguration;
    }
    @XmlElement(name = "RemoteSessionConfiguration", required = true)
    public RemoteSessionConfiguration getRemoteSessionConfiguration() {
        return remoteSessionConfiguration;
    }

    public void setRemoteSessionConfiguration(RemoteSessionConfiguration remoteSessionConfiguration) {
        this.remoteSessionConfiguration = remoteSessionConfiguration;
    }

    @XmlElement(name = "EnrolmentNotificationConfiguration", required = true)
    public EnrollmentNotificationConfiguration getEnrollmentNotificationConfiguration() {
        return enrollmentNotificationConfiguration;
    }

    public void setEnrollmentNotificationConfiguration(
            EnrollmentNotificationConfiguration enrollmentNotificationConfiguration) {
        this.enrollmentNotificationConfiguration = enrollmentNotificationConfiguration;
    }

    @XmlElement(name = "DefaultRoles", required = true)
    public DefaultRoles getDefaultRoles() { return defaultRoles; }

    public void setDefaultRoles(DefaultRoles defaultRoles) { this.defaultRoles = defaultRoles; }

    @XmlElement(name = "OperationTimeoutConfigurations", required = true)
    public OperationTimeoutConfiguration getOperationTimeoutConfiguration() {
        return operationTimeoutConfiguration;
    }

    public void setOperationTimeoutConfiguration(OperationTimeoutConfiguration operationTimeoutConfiguration) {
        this.operationTimeoutConfiguration = operationTimeoutConfiguration;
    }

    @XmlElement(name = "MetaDataConfiguration", required = true)
    public MetaDataConfiguration getMetaDataConfiguration() {
        return metaDataConfiguration;
    }

    public void setMetaDataConfiguration(MetaDataConfiguration metaDataConfiguration) {
        this.metaDataConfiguration = metaDataConfiguration;
    }

    @XmlElement(name = "EnrollmentGuideConfiguration", required = true)
    public EnrollmentGuideConfiguration getEnrollmentGuideConfiguration() {
        return enrollmentGuideConfiguration;
    }

    public void setEnrollmentGuideConfiguration(EnrollmentGuideConfiguration enrollmentGuideConfiguration) {
        this.enrollmentGuideConfiguration = enrollmentGuideConfiguration;
    }

    @XmlElement(name = "DefaultPermissions", required = true)
    public DefaultPermissions getDefaultPermissions() {
        return defaultPermissions;
    }

    public void setDefaultPermissions(DefaultPermissions defaultPermissions) {
        this.defaultPermissions = defaultPermissions;
    }
}

