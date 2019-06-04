/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.application.mgt.core.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.routines.UrlValidator;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.application.mgt.common.ApplicationType;
import org.wso2.carbon.device.application.mgt.common.dto.ApplicationDTO;
import org.wso2.carbon.device.application.mgt.common.dto.ApplicationReleaseDTO;
import org.wso2.carbon.device.application.mgt.common.response.Application;
import org.wso2.carbon.device.application.mgt.common.response.ApplicationRelease;
import org.wso2.carbon.device.application.mgt.common.services.*;
import org.wso2.carbon.device.application.mgt.common.ErrorResponse;
import org.wso2.carbon.device.application.mgt.common.wrapper.ApplicationReleaseWrapper;
import org.wso2.carbon.device.application.mgt.common.wrapper.ApplicationWrapper;
import org.wso2.carbon.device.application.mgt.common.wrapper.WebAppReleaseWrapper;
import org.wso2.carbon.device.application.mgt.common.wrapper.WebAppWrapper;
import org.wso2.carbon.device.application.mgt.core.config.ConfigurationManager;
import org.wso2.carbon.device.application.mgt.core.exception.BadRequestException;
import org.wso2.carbon.device.application.mgt.core.exception.UnexpectedServerErrorException;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.dto.DeviceType;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Holds util methods required for ApplicationDTO-Mgt API component.
 */
public class APIUtil {

    private static Log log = LogFactory.getLog(APIUtil.class);

    private static ApplicationManager applicationManager;
    private static ApplicationStorageManager applicationStorageManager;
    private static SubscriptionManager subscriptionManager;
    private static ReviewManager reviewManager;
    private static AppmDataHandler appmDataHandler;

    public static ApplicationManager getApplicationManager() {
        if (applicationManager == null) {
            synchronized (APIUtil.class) {
                if (applicationManager == null) {
                    PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                    applicationManager =
                            (ApplicationManager) ctx.getOSGiService(ApplicationManager.class, null);
                    if (applicationManager == null) {
                        String msg = "ApplicationDTO Manager service has not initialized.";
                        log.error(msg);
                        throw new IllegalStateException(msg);
                    }
                }
            }
        }
        return applicationManager;
    }

    /**
     * To get the ApplicationDTO Storage Manager from the osgi context.
     * @return ApplicationStoreManager instance in the current osgi context.
     */
    public static ApplicationStorageManager getApplicationStorageManager() {
        if (applicationStorageManager == null) {
            synchronized (APIUtil.class) {
                if (applicationStorageManager == null) {
                    PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                    applicationStorageManager = (ApplicationStorageManager) ctx
                            .getOSGiService(ApplicationStorageManager.class, null);
                    if (applicationStorageManager == null) {
                        String msg = "ApplicationDTO Storage Manager service has not initialized.";
                        log.error(msg);
                        throw new IllegalStateException(msg);
                    }
                }
            }
        }
        return applicationStorageManager;
    }

    public static Response getResponse(Exception ex, Response.Status status) {
        return getResponse(ex.getMessage(), status);
    }

    public static Response getResponse(String message, Response.Status status) {
        ErrorResponse errorMessage = new ErrorResponse();
        errorMessage.setMessage(message);
        if (status == null) {
            status = Response.Status.INTERNAL_SERVER_ERROR;
        }
        errorMessage.setCode(status.getStatusCode());
        return Response.status(status).entity(errorMessage).build();
    }

    /**
     * To get the Subscription Manager from the osgi context.
     * @return SubscriptionManager instance in the current osgi context.
     */
    public static SubscriptionManager getSubscriptionManager() {
        if (subscriptionManager == null) {
            synchronized (APIUtil.class) {
                if (subscriptionManager == null) {
                    PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                    subscriptionManager =
                            (SubscriptionManager) ctx.getOSGiService(SubscriptionManager.class, null);
                    if (subscriptionManager == null) {
                        String msg = "Subscription Manager service has not initialized.";
                        log.error(msg);
                        throw new IllegalStateException(msg);
                    }
                }
            }
        }

        return subscriptionManager;
    }

    /**
     * To get the Review Manager from the osgi context.
     * @return ReviewManager instance in the current osgi context.
     */
    public static ReviewManager getReviewManager() {
        if (reviewManager == null) {
            synchronized (APIUtil.class) {
                if (reviewManager == null) {
                    PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                    reviewManager =
                            (ReviewManager) ctx.getOSGiService(ReviewManager.class, null);
                    if (reviewManager == null) {
                        String msg = "Review Manager service has not initialized.";
                        log.error(msg);
                        throw new IllegalStateException(msg);
                    }
                }
            }
        }

        return reviewManager;
    }

    /**
     * To get the DataHandler from the osgi context.
     * @return AppmDataHandler instance in the current osgi context.
     */
    public static AppmDataHandler getDataHandler() {
        if (appmDataHandler == null) {
            synchronized (APIUtil.class) {
                if (appmDataHandler == null) {
                    PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
                    appmDataHandler =
                            (AppmDataHandler) ctx.getOSGiService(AppmDataHandler.class, null);
                    if (appmDataHandler == null) {
                        String msg = "Config Manager service has not initialized.";
                        log.error(msg);
                        throw new IllegalStateException(msg);
                    }
                }
            }
        }

        return appmDataHandler;
    }

    public static <T> DeviceType getDeviceTypeData(T deviceTypeAttr)
            throws BadRequestException, UnexpectedServerErrorException {
        List<DeviceType> deviceTypes;
        try {
            deviceTypes = DAOUtil.getDeviceManagementService().getDeviceTypes();

            if(deviceTypeAttr instanceof String){
                for (DeviceType dt : deviceTypes) {
                    if (dt.getName().equals(deviceTypeAttr)) {
                        return dt;
                    }
                }
            } else if (deviceTypeAttr instanceof  Integer){
                for (DeviceType dt : deviceTypes) {
                    if (dt.getId() == (Integer) deviceTypeAttr) {
                        return dt;
                    }
                }
            } else {
                String msg = "Invalid device type class is received. Device type class: " + deviceTypeAttr.getClass()
                        .getName();
                log.error(msg);
                throw new BadRequestException(msg);
            }

            String msg =
                    "Invalid device type Attribute is found with the request. Device Type attribute: " + deviceTypeAttr;
            log.error(msg);
            throw new BadRequestException(msg);

        } catch (DeviceManagementException e) {
            String msg = "Error occured when getting device types which are supported by the Entgra IoTS";
            log.error(msg);
            throw new UnexpectedServerErrorException(msg);
        }
    }

    public static <T> ApplicationDTO convertToAppDTO(T param)
            throws BadRequestException, UnexpectedServerErrorException {
        ApplicationDTO applicationDTO = new ApplicationDTO();

        if (param instanceof ApplicationWrapper){
            ApplicationWrapper applicationWrapper = (ApplicationWrapper) param;
            DeviceType deviceType = getDeviceTypeData(applicationWrapper.getDeviceType());
            applicationDTO.setName(applicationWrapper.getName());
            applicationDTO.setDescription(applicationWrapper.getDescription());
            applicationDTO.setAppCategories(applicationWrapper.getAppCategories());
            applicationDTO.setType(applicationWrapper.getType());
            applicationDTO.setSubType(applicationWrapper.getSubType());
            applicationDTO.setPaymentCurrency(applicationWrapper.getPaymentCurrency());
            applicationDTO.setTags(applicationWrapper.getTags());
            applicationDTO.setUnrestrictedRoles(applicationWrapper.getUnrestrictedRoles());
            applicationDTO.setDeviceTypeId(deviceType.getId());
            List<ApplicationReleaseDTO> applicationReleaseEntities = applicationWrapper.getApplicationReleaseWrappers()
                    .stream().map(APIUtil::releaseWrapperToReleaseDTO).collect(Collectors.toList());
            applicationDTO.setApplicationReleaseDTOs(applicationReleaseEntities);
        } else if (param instanceof WebAppWrapper){
            WebAppWrapper webAppWrapper = (WebAppWrapper) param;
            applicationDTO.setName(webAppWrapper.getName());
            applicationDTO.setDescription(webAppWrapper.getDescription());
            applicationDTO.setAppCategories(webAppWrapper.getCategories());
            applicationDTO.setSubType(webAppWrapper.getSubMethod());
            applicationDTO.setPaymentCurrency(webAppWrapper.getPaymentCurrency());
            applicationDTO.setType(webAppWrapper.getType());
            applicationDTO.setTags(webAppWrapper.getTags());
            applicationDTO.setUnrestrictedRoles(webAppWrapper.getUnrestrictedRoles());
            List<ApplicationReleaseDTO> applicationReleaseEntities = webAppWrapper.getWebAppReleaseWrappers()
                    .stream().map(APIUtil::releaseWrapperToReleaseDTO).collect(Collectors.toList());
            applicationDTO.setApplicationReleaseDTOs(applicationReleaseEntities);
        }

        return applicationDTO;
    }

    public static <T> ApplicationReleaseDTO releaseWrapperToReleaseDTO(T param){
        ApplicationReleaseDTO applicationReleaseDTO = new ApplicationReleaseDTO();
        if (param instanceof ApplicationReleaseWrapper){
            ApplicationReleaseWrapper applicationReleaseWrapper = (ApplicationReleaseWrapper) param;
            applicationReleaseDTO.setDescription(applicationReleaseWrapper.getDescription());
            applicationReleaseDTO.setReleaseType(applicationReleaseWrapper.getReleaseType());
            applicationReleaseDTO.setPrice(applicationReleaseWrapper.getPrice());
            applicationReleaseDTO.setIsSharedWithAllTenants(applicationReleaseWrapper.getIsSharedWithAllTenants());
            applicationReleaseDTO.setMetaData(applicationReleaseWrapper.getMetaData());
            applicationReleaseDTO.setSupportedOsVersions(applicationReleaseWrapper.getSupportedOsVersions());
        } else if (param instanceof WebAppReleaseWrapper){
            WebAppReleaseWrapper webAppReleaseWrapper = (WebAppReleaseWrapper) param;
            applicationReleaseDTO.setDescription(webAppReleaseWrapper.getDescription());
            applicationReleaseDTO.setReleaseType(webAppReleaseWrapper.getReleaseType());
            applicationReleaseDTO.setVersion(webAppReleaseWrapper.getVersion());
            applicationReleaseDTO.setPrice(webAppReleaseWrapper.getPrice());
            applicationReleaseDTO.setInstallerName(webAppReleaseWrapper.getUrl());
            applicationReleaseDTO.setIsSharedWithAllTenants(webAppReleaseWrapper.getIsSharedWithAllTenants());
            applicationReleaseDTO.setMetaData(webAppReleaseWrapper.getMetaData());
        }
        return applicationReleaseDTO;
    }

    public static Application appDtoToAppResponse(ApplicationDTO applicationDTO)
            throws BadRequestException, UnexpectedServerErrorException {

        Application application = new Application();
        if (!ApplicationType.WEB_CLIP.toString().equals(applicationDTO.getType())) {
            DeviceType deviceType = getDeviceTypeData(applicationDTO.getDeviceTypeId());
            application.setDeviceType(deviceType.getName());
        } else {
            application.setDeviceType("ANY");
        }
        application.setId(applicationDTO.getId());
        application.setName(applicationDTO.getName());
        application.setDescription(applicationDTO.getDescription());
        application.setAppCategories(applicationDTO.getAppCategories());
        application.setType(applicationDTO.getType());
        application.setSubType(applicationDTO.getSubType());
        application.setPaymentCurrency(applicationDTO.getPaymentCurrency());
        application.setTags(applicationDTO.getTags());
        application.setUnrestrictedRoles(applicationDTO.getUnrestrictedRoles());
        application.setRating(applicationDTO.getAppRating());
        List<ApplicationRelease> applicationReleases = applicationDTO.getApplicationReleaseDTOs()
                .stream().map(APIUtil::releaseDtoToRelease).collect(Collectors.toList());
        application.setApplicationReleases(applicationReleases);
        return application;
    }

    public static ApplicationRelease releaseDtoToRelease(ApplicationReleaseDTO applicationReleaseDTO){
        String artifactDownloadEndpoint = ConfigurationManager.getInstance().getConfiguration()
                .getArtifactDownloadEndpoint();
        String basePath = artifactDownloadEndpoint + Constants.FORWARD_SLASH + applicationReleaseDTO.getUuid()
                + Constants.FORWARD_SLASH;

        List<String> screenshotPaths = new ArrayList<>();
        ApplicationRelease applicationRelease = new ApplicationRelease();
        UrlValidator urlValidator = new UrlValidator();

        applicationRelease.setDescription(applicationReleaseDTO.getDescription());
        applicationRelease.setVersion(applicationReleaseDTO.getVersion());
        applicationRelease.setUuid(applicationReleaseDTO.getUuid());
        applicationRelease.setReleaseType(applicationReleaseDTO.getReleaseType());
        applicationRelease.setPrice(applicationReleaseDTO.getPrice());
        applicationRelease.setIsSharedWithAllTenants(applicationReleaseDTO.getIsSharedWithAllTenants());
        applicationRelease.setMetaData(applicationReleaseDTO.getMetaData());
        applicationRelease.setCurrentStatus(applicationReleaseDTO.getCurrentState());
        applicationRelease.setIsSharedWithAllTenants(applicationReleaseDTO.getIsSharedWithAllTenants());
        applicationRelease.setSupportedOsVersions(applicationReleaseDTO.getSupportedOsVersions());
        applicationRelease.setRating(applicationReleaseDTO.getRating());
        applicationRelease.setIconPath(basePath + applicationReleaseDTO.getIconName());
        applicationRelease.setBannerPath(basePath + applicationReleaseDTO.getBannerName());

        if (urlValidator.isValid(applicationReleaseDTO.getInstallerName())){
            applicationRelease
                    .setInstallerPath(applicationReleaseDTO.getInstallerName());
        } else {
            applicationRelease
                    .setInstallerPath(basePath + applicationReleaseDTO.getInstallerName());
        }

        if (!StringUtils.isEmpty(applicationReleaseDTO.getScreenshotName1())) {
            screenshotPaths.add(basePath + applicationReleaseDTO.getScreenshotName1());
        }
        if (!StringUtils.isEmpty(applicationReleaseDTO.getScreenshotName2())) {
            screenshotPaths.add(basePath + applicationReleaseDTO.getScreenshotName2());
        }
        if (!StringUtils.isEmpty(applicationReleaseDTO.getScreenshotName3())) {
            screenshotPaths.add(basePath + applicationReleaseDTO.getScreenshotName3());
        }
        applicationRelease.setScreenshots(screenshotPaths);
        return applicationRelease;
    }

}
