/*
 *   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.wso2.carbon.device.application.mgt.core.impl;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSString;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;
import net.dongliu.apk.parser.bean.ApkMeta;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.application.mgt.common.ApplicationRelease;
import org.wso2.carbon.device.application.mgt.common.ApplicationType;
import org.wso2.carbon.device.application.mgt.common.DeviceTypes;
import org.wso2.carbon.device.application.mgt.common.exception.ApplicationStorageManagementException;
import org.wso2.carbon.device.application.mgt.common.exception.RequestValidatingException;
import org.wso2.carbon.device.application.mgt.common.exception.ResourceManagementException;
import org.wso2.carbon.device.application.mgt.common.services.ApplicationStorageManager;
import org.wso2.carbon.device.application.mgt.core.exception.ParsingException;
import org.wso2.carbon.device.application.mgt.core.internal.DataHolder;
import org.wso2.carbon.device.application.mgt.core.util.ArtifactsParser;
import org.wso2.carbon.device.application.mgt.core.util.Constants;
import org.wso2.carbon.device.application.mgt.core.util.StorageManagementUtil;
import org.xml.sax.SAXException;

import javax.activation.DataHandler;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.wso2.carbon.device.application.mgt.core.util.StorageManagementUtil.deleteDir;
import static org.wso2.carbon.device.application.mgt.core.util.StorageManagementUtil.saveFile;

/**
 * This class contains the default concrete implementation of ApplicationStorage Management.
 */
public class ApplicationStorageManagerImpl implements ApplicationStorageManager {
    private static final Log log = LogFactory.getLog(ApplicationStorageManagerImpl.class);
    private String storagePath;
    private int screenShotMaxCount;
    private static final int BUFFER_SIZE = 4096;

    /**
     * Create a new ApplicationStorageManager Instance
     *
     * @param storagePath        Storage Path to save the binary and image files.
     * @param screenShotMaxCount Maximum Screen-shots count
     */
    public ApplicationStorageManagerImpl(String storagePath, String screenShotMaxCount) {
        this.storagePath = storagePath;
        this.screenShotMaxCount = Integer.parseInt(screenShotMaxCount);
    }

    @Override
    public ApplicationRelease uploadImageArtifacts(ApplicationRelease applicationRelease, InputStream iconFileStream,
                                                   InputStream bannerFileStream, List<InputStream> screenShotStreams)
            throws ResourceManagementException {
        int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId(true);
        String artifactDirectoryPath;
        String iconStoredLocation;
        String bannerStoredLocation;
        String scStoredLocation;

        try {
            artifactDirectoryPath = storagePath + applicationRelease.getAppHashValue();
            StorageManagementUtil.createArtifactDirectory(artifactDirectoryPath);
            iconStoredLocation = artifactDirectoryPath + File.separator + Constants.IMAGE_ARTIFACTS[0];
            bannerStoredLocation = artifactDirectoryPath + File.separator + Constants.IMAGE_ARTIFACTS[1];

            if (iconFileStream != null) {
                saveFile(iconFileStream, iconStoredLocation);
                applicationRelease.setIconLoc(iconStoredLocation);
            }
            if (bannerFileStream != null) {
                saveFile(bannerFileStream, bannerStoredLocation);
                applicationRelease.setBannerLoc(bannerStoredLocation);
            }
            if (!screenShotStreams.isEmpty()) {
                if (screenShotStreams.size() > screenShotMaxCount) {
                    throw new ApplicationStorageManagementException("Maximum limit for the screen-shot exceeds");
                }
                int count = 1;
                for (InputStream screenshotStream : screenShotStreams) {
                    scStoredLocation = artifactDirectoryPath + File.separator + Constants.IMAGE_ARTIFACTS[2] + count;
                    if (count == 1) {
                        applicationRelease.setScreenshotLoc1(scStoredLocation);
                    }
                    if (count == 2) {
                        applicationRelease.setScreenshotLoc2(scStoredLocation);
                    }
                    if (count == 3) {
                        applicationRelease.setScreenshotLoc3(scStoredLocation);
                    }
                    saveFile(screenshotStream, scStoredLocation);
                    count++;
                }
            }
            return applicationRelease;
        } catch (IOException e) {
            throw new ApplicationStorageManagementException("IO Exception while saving the screens hots for " +
                    "the application " + applicationRelease.getUuid(), e);
        } catch (ApplicationStorageManagementException e) {
            throw new ApplicationStorageManagementException("Application Management DAO exception while trying to "
                    + "update the screen-shot count for the application " + applicationRelease.getUuid() +
                    " for the tenant id " + tenantId, e);
        }
    }

    public ApplicationRelease uploadImageArtifactsTmp(ApplicationRelease applicationRelease,
            Attachment iconFile, Attachment bannerFile, List<Attachment> screenshots) throws ResourceManagementException{

        InputStream iconFileStream;
        InputStream bannerFileStream;
        List<InputStream> screenshotStreams = new ArrayList<>();
        List<String> scFileExtensions = new ArrayList<>();
        DataHandler iconFileDataHandler;
        DataHandler bannerFileDataHandler;
        String artifactDirectoryPath;
        String iconStoredLocation;
        String bannerStoredLocation;
        String scStoredLocation;

        int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId(true);
        try {
            iconFileDataHandler = iconFile.getDataHandler();
            bannerFileDataHandler = bannerFile.getDataHandler();

            iconFileStream = iconFileDataHandler.getInputStream();
            bannerFileStream = bannerFileDataHandler.getInputStream();
            for (Attachment screenshot : screenshots) {
                DataHandler scDataHandler = screenshot.getDataHandler();
                screenshotStreams.add(scDataHandler.getInputStream());
                scFileExtensions.add(scDataHandler.getName());
            }
            artifactDirectoryPath = storagePath + applicationRelease.getAppHashValue();
            StorageManagementUtil.createArtifactDirectory(artifactDirectoryPath);

            if (iconFileStream != null) {
                iconStoredLocation = artifactDirectoryPath + File.separator + iconFileDataHandler.getName();
                saveFile(iconFileStream, iconStoredLocation);
                applicationRelease.setIconLoc(iconStoredLocation);
            }
            if (bannerFileStream != null) {
                bannerStoredLocation = artifactDirectoryPath + File.separator + bannerFileDataHandler.getName();
                saveFile(bannerFileStream, bannerStoredLocation);
                applicationRelease.setBannerLoc(bannerStoredLocation);
            }
            if (!screenshotStreams.isEmpty()) {
                if (screenshotStreams.size() > screenShotMaxCount) {
                    throw new ApplicationStorageManagementException("Maximum limit for the screen-shot exceeds");
                }
                int count = 0;
                for (InputStream screenshotStream : screenshotStreams) {
                    scStoredLocation = artifactDirectoryPath + File.separator + scFileExtensions.get(count);
                    count ++;
                    if (count == 1) {
                        applicationRelease.setScreenshotLoc1(scStoredLocation);
                    }
                    if (count == 2) {
                        applicationRelease.setScreenshotLoc2(scStoredLocation);
                    }
                    if (count == 3) {
                        applicationRelease.setScreenshotLoc3(scStoredLocation);
                    }
                    saveFile(screenshotStream, scStoredLocation);
                }
            }
            return applicationRelease;
        } catch (IOException e) {
            throw new ApplicationStorageManagementException("IO Exception while saving the screens hots for " +
                    "the application " + applicationRelease.getUuid(), e);
        } catch (ApplicationStorageManagementException e) {
            throw new ApplicationStorageManagementException("Application Management DAO exception while trying to "
                    + "update the screen-shot count for the application " + applicationRelease.getUuid() +
                    " for the tenant id " + tenantId, e);
        }
    }

    @Override
     public ApplicationRelease updateImageArtifacts(ApplicationRelease applicationRelease, InputStream
            iconFileStream, InputStream bannerFileStream, List<InputStream> screenShotStreams)
            throws ResourceManagementException {

        int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId(true);

        try {
            if (iconFileStream != null) {
                deleteApplicationReleaseArtifacts(applicationRelease.getIconLoc());
            }
            if (bannerFileStream != null) {
                deleteApplicationReleaseArtifacts(applicationRelease.getBannerLoc());
            }
            if (!screenShotStreams.isEmpty()) {
                if (screenShotStreams.size() > screenShotMaxCount) {
                    throw new ApplicationStorageManagementException("Maximum limit for the screen-shot exceeds");
                }
                int count = 1;
                while (count < screenShotStreams.size()) {
                    if (count == 1) {
                        deleteApplicationReleaseArtifacts(applicationRelease.getScreenshotLoc1());
                    }
                    if (count == 2) {
                        deleteApplicationReleaseArtifacts(applicationRelease.getScreenshotLoc2());
                    }
                    if (count == 3) {
                        deleteApplicationReleaseArtifacts(applicationRelease.getScreenshotLoc3());
                    }
                    count++;
                }
            }
            applicationRelease = uploadImageArtifacts(applicationRelease, iconFileStream, bannerFileStream,
                    screenShotStreams);
            return applicationRelease;
        } catch (ApplicationStorageManagementException e) {
            throw new ApplicationStorageManagementException("Application Storage exception while trying to"
                    + " update the screen-shot count for the application Release " + applicationRelease.getUuid() +
                    " for the tenant " + tenantId, e);
        }
    }

    @Override
    public ApplicationRelease uploadReleaseArtifact(ApplicationRelease applicationRelease, String appType,
            String deviceType, InputStream binaryFile) throws ResourceManagementException, RequestValidatingException {
        try {
            if (ApplicationType.WEB_CLIP.toString().equals(appType)) {
                applicationRelease.setVersion(Constants.DEFAULT_VERSION);
                UrlValidator urlValidator = new UrlValidator();
                if (applicationRelease.getUrl() == null || !urlValidator.isValid(applicationRelease.getUrl())) {
                    String msg = "Request payload doesn't contains Web Clip URL with application release object or Web Clip URL is invalid";
                    log.error(msg);
                    throw new RequestValidatingException(msg);
                }
                applicationRelease.setAppStoredLoc(applicationRelease.getUrl());
                applicationRelease.setAppHashValue(null);
                return applicationRelease;
            }
            String artifactDirectoryPath;
            String md5OfApp;
            String artifactPath;
            InputStream[] cloneInputStream = cloneInputStream(binaryFile);
            md5OfApp = getMD5(binaryFile);
            if (md5OfApp == null) {
                String msg =
                        "Error occurred while md5sum value retrieving process: application UUID " + applicationRelease
                                .getUuid();
                log.error(msg);
                throw new ApplicationStorageManagementException(msg);
            }

            artifactDirectoryPath = storagePath + md5OfApp;
            if (DeviceTypes.ANDROID.toString().equalsIgnoreCase(deviceType)) {
                ApkMeta apkMeta = ArtifactsParser.readAndroidManifestFile(cloneInputStream[2]);
                applicationRelease.setVersion(apkMeta.getVersionName());
                applicationRelease.setPackageName(apkMeta.getPackageName());
                artifactPath = artifactDirectoryPath + File.separator + Constants.RELEASE_ARTIFACT
                        + Constants.ANDROID_INSTALLER_EXT;
            } else if (DeviceTypes.IOS.toString().equalsIgnoreCase(deviceType)) {
                NSDictionary plistInfo = ArtifactsParser.readiOSManifestFile(binaryFile);
                applicationRelease
                        .setVersion(plistInfo.objectForKey(ArtifactsParser.IPA_BUNDLE_VERSION_KEY).toString());
                applicationRelease
                        .setPackageName(plistInfo.objectForKey(ArtifactsParser.IPA_BUNDLE_IDENTIFIER_KEY).toString());
                artifactPath = artifactDirectoryPath + File.separator + Constants.RELEASE_ARTIFACT
                        + Constants.IOS_INSTALLER_EXT;
            } else {
                String msg = "Application Type doesn't match with supporting application types " + applicationRelease
                        .getUuid();
                log.error(msg);
                throw new ApplicationStorageManagementException(msg);
            }

            if (log.isDebugEnabled()) {
                log.debug("Artifact Directory Path for saving the application release related artifacts related with "
                        + "application UUID " + applicationRelease.getUuid() + " is " + artifactDirectoryPath);
            }

            StorageManagementUtil.createArtifactDirectory(artifactDirectoryPath);
            saveFile(cloneInputStream[1], artifactPath);
            applicationRelease.setAppStoredLoc(artifactPath);
            applicationRelease.setAppHashValue(md5OfApp);
        } catch (IOException e) {
            String msg = "IO Exception while saving the release artifacts in the server for the application UUID "
                    + applicationRelease.getUuid();
            log.error(msg);
            throw new ApplicationStorageManagementException( msg, e);
        } catch (ParsingException e) {
            String msg =
                    "Error occurred while parsing the artifact file. Application release UUID is " + applicationRelease
                            .getUuid();
            log.error(msg);
            throw new ApplicationStorageManagementException(msg, e);
        }
        return applicationRelease;
    }

    public ApplicationRelease uploadReleaseArtifactTmp(ApplicationRelease applicationRelease, String appType, String deviceType,
            Attachment binaryFileAttachment) throws ResourceManagementException, RequestValidatingException{

        try {
            if (ApplicationType.WEB_CLIP.toString().equals(appType)) {
                applicationRelease.setVersion(Constants.DEFAULT_VERSION);
                UrlValidator urlValidator = new UrlValidator();
                if (applicationRelease.getUrl() == null || !urlValidator.isValid(applicationRelease.getUrl())) {
                    String msg = "Request payload doesn't contains Web Clip URL with application release object or Web Clip URL is invalid";
                    log.error(msg);
                    throw new RequestValidatingException(msg);
                }
                applicationRelease.setAppStoredLoc(applicationRelease.getUrl());
                applicationRelease.setAppHashValue(null);
                return applicationRelease;
            }
            String artifactDirectoryPath;
            String md5OfApp;
            String artifactPath;
            DataHandler binaryDataHandler = binaryFileAttachment.getDataHandler();
            String fileName = binaryDataHandler.getName();
            InputStream binaryFile = binaryDataHandler.getInputStream();
            InputStream[] cloneInputStream = cloneInputStream(binaryFile);
            md5OfApp = getMD5(binaryFile);
            if (md5OfApp == null) {
                String msg =
                        "Error occurred while md5sum value retrieving process: application UUID " + applicationRelease
                                .getUuid();
                log.error(msg);
                throw new ApplicationStorageManagementException(msg);
            }

            artifactDirectoryPath = storagePath + md5OfApp;
            if (DeviceTypes.ANDROID.toString().equalsIgnoreCase(deviceType)) {
                ApkMeta apkMeta = ArtifactsParser.readAndroidManifestFile(cloneInputStream[2]);
                applicationRelease.setVersion(apkMeta.getVersionName());
                applicationRelease.setPackageName(apkMeta.getPackageName());
            } else if (DeviceTypes.IOS.toString().equalsIgnoreCase(deviceType)) {
                NSDictionary plistInfo = ArtifactsParser.readiOSManifestFile(binaryFile);
                applicationRelease
                        .setVersion(plistInfo.objectForKey(ArtifactsParser.IPA_BUNDLE_VERSION_KEY).toString());
                applicationRelease
                        .setPackageName(plistInfo.objectForKey(ArtifactsParser.IPA_BUNDLE_IDENTIFIER_KEY).toString());

            } else {
                String msg = "Application Type doesn't match with supporting application types " + applicationRelease
                        .getUuid();
                log.error(msg);
                throw new ApplicationStorageManagementException(msg);
            }

            artifactPath = artifactDirectoryPath + File.separator + fileName;
            if (log.isDebugEnabled()) {
                log.debug("Artifact Directory Path for saving the application release related artifacts related with "
                        + "application UUID " + applicationRelease.getUuid() + " is " + artifactDirectoryPath);
            }

            StorageManagementUtil.createArtifactDirectory(artifactDirectoryPath);
            saveFile(cloneInputStream[1], artifactPath);
            applicationRelease.setAppStoredLoc(artifactPath);
            applicationRelease.setAppHashValue(md5OfApp);
        } catch (IOException e) {
            String msg = "IO Exception while saving the release artifacts in the server for the application UUID "
                    + applicationRelease.getUuid();
            log.error(msg);
            throw new ApplicationStorageManagementException( msg, e);
        } catch (ParsingException e) {
            String msg =
                    "Error occurred while parsing the artifact file. Application release UUID is " + applicationRelease
                            .getUuid();
            log.error(msg);
            throw new ApplicationStorageManagementException(msg, e);
        }
        return applicationRelease;
    }

    private InputStream[] cloneInputStream(InputStream inputStream) throws ApplicationStorageManagementException {

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = inputStream.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            byteArrayOutputStream.flush();

            InputStream stream1 = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            InputStream stream2 = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            InputStream stream3 = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

            return new InputStream[] { stream1, stream2, stream3 };
        } catch (IOException e) {
            throw new ApplicationStorageManagementException("Error occurred while cloning input stream ", e);
        }
    }

    @Override
    public ApplicationRelease updateReleaseArtifacts(ApplicationRelease applicationRelease, String appType,
            String deviceType, InputStream binaryFile) throws ApplicationStorageManagementException,
            RequestValidatingException {

        try {
            deleteApplicationReleaseArtifacts(applicationRelease.getAppStoredLoc());
            applicationRelease = uploadReleaseArtifact(applicationRelease, appType, deviceType, binaryFile);
        } catch (ApplicationStorageManagementException e) {
            throw new ApplicationStorageManagementException("Application Artifact doesn't contains in the System", e);
        } catch (ResourceManagementException e) {
            throw new ApplicationStorageManagementException("Application Artifact Updating failed", e);
        }

        return applicationRelease;

    }


    @Override
    public void deleteApplicationReleaseArtifacts(String directoryPath) throws ApplicationStorageManagementException {
        File artifact = new File(directoryPath);

        if (artifact.exists()) {
            try {
                StorageManagementUtil.deleteDir(artifact);
            } catch (IOException e) {
                throw new ApplicationStorageManagementException(
                        "Error occured while deleting application release artifacts", e);
            }
        } else {
            throw new ApplicationStorageManagementException(
                    "Tried to delete application release, but it doesn't exist in the system");
        }
    }

    @Override public void deleteAllApplicationReleaseArtifacts(List<String> directoryPaths)
            throws ApplicationStorageManagementException {
        for (String directoryBasePath : directoryPaths) {
            deleteApplicationReleaseArtifacts(storagePath + directoryBasePath);
        }
    }

    private String getMD5(InputStream binaryFile) throws ApplicationStorageManagementException {
        String md5;
        try {
            md5 = DigestUtils.md5Hex(IOUtils.toByteArray(binaryFile));
        } catch (IOException e) {
            throw new ApplicationStorageManagementException
                    ("IO Exception while trying to get the md5sum value of application");
        }
        return md5;
    }

    private synchronized Map<String, String> getIPAInfo(File ipaFile) throws ApplicationStorageManagementException {
        Map<String, String> ipaInfo = new HashMap<>();

        String ipaDirectory = null;
        try {
            String ipaAbsPath = ipaFile.getAbsolutePath();
            ipaDirectory = new File(ipaAbsPath).getParent();

            if (new File(ipaDirectory + File.separator + Constants.PAYLOAD).exists()) {
                deleteDir(new File(ipaDirectory + File.separator + Constants.PAYLOAD));
            }

            // unzip ipa zip file
            unzip(ipaAbsPath, ipaDirectory);

            // fetch app file name, after unzip ipa
            String appFileName = "";
            for (File file : Objects.requireNonNull(
                    new File(ipaDirectory + File.separator + Constants.PAYLOAD).listFiles()
            )) {
                if (file.toString().endsWith(Constants.APP_EXTENSION)) {
                    appFileName = new File(file.toString()).getAbsolutePath();
                    break;
                }
            }

            String plistFilePath = appFileName + File.separator + Constants.PLIST_NAME;

            // parse info.plist
            File plistFile = new File(plistFilePath);
            NSDictionary rootDict;
            rootDict = (NSDictionary) PropertyListParser.parse(plistFile);

            // get version
            NSString parameter = (NSString) rootDict.objectForKey(Constants.CF_BUNDLE_VERSION);
            ipaInfo.put(Constants.CF_BUNDLE_VERSION, parameter.toString());

            if (ipaDirectory != null) {
                // remove unzip folder
                deleteDir(new File(ipaDirectory + File.separator + Constants.PAYLOAD));
            }

        } catch (ParseException e) {
            String msg = "Error occurred while parsing the plist data";
            log.error(msg);
            throw new ApplicationStorageManagementException(msg, e);
        } catch (IOException e) {
            String msg = "Error occurred while accessing the ipa file";
            log.error(msg);
            throw new ApplicationStorageManagementException(msg, e);
        } catch (SAXException | ParserConfigurationException | PropertyListFormatException e) {
            log.error(e);
            throw new ApplicationStorageManagementException(e.getMessage(), e);
        } catch (ApplicationStorageManagementException e) {
            String msg = "Error occurred while unzipping the ipa file";
            log.error(msg);
            throw new ApplicationStorageManagementException(msg, e);
        }
        return ipaInfo;
    }

    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     *
     * @param zipFilePath   file path of the zip
     * @param destDirectory destination directory path
     */
    private void unzip(String zipFilePath, String destDirectory)
            throws IOException, ApplicationStorageManagementException {
        File destDir = new File(destDirectory);
        boolean isDirCreated;

        if (!destDir.exists()) {
            isDirCreated = destDir.mkdir();
            if (!isDirCreated) {
                throw new ApplicationStorageManagementException("Directory Creation Is Failed while iOS app vertion " +
                                                                        "retrieval");
            }
        }
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {

            ZipEntry entry = zipIn.getNextEntry();
            // iterates over entries in the zip file
            while (entry != null) {
                String filePath = destDirectory + File.separator + entry.getName();

                if (!entry.isDirectory()) {
                    // if the entry is a file, extracts it
                    extractFile(zipIn, filePath);
                } else {
                    // if the entry is a directory, make the directory
                    File dir = new File(filePath);
                    isDirCreated = dir.mkdir();
                    if (!isDirCreated) {
                        throw new ApplicationStorageManagementException(
                                "Directory Creation Is Failed while iOS app vertion " + "retrieval");
                    }

                }

                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
    }

    /**
     * Extracts a zip entry (file entry)
     *
     * @param zipIn    zip input stream
     * @param filePath file path
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }
}
