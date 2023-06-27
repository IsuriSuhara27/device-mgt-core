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

package io.entgra.device.mgt.core.ui.request.interceptor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.entgra.device.mgt.core.ui.request.interceptor.beans.AuthData;
import io.entgra.device.mgt.core.ui.request.interceptor.beans.ProxyResponse;
import io.entgra.device.mgt.core.ui.request.interceptor.util.HandlerConstants;
import io.entgra.device.mgt.core.ui.request.interceptor.util.HandlerUtil;
import io.entgra.device.mgt.core.device.mgt.extensions.logger.spi.EntgraLogger;
import io.entgra.device.mgt.core.notification.logger.UserLogContext;
import io.entgra.device.mgt.core.notification.logger.impl.EntgraUserLoggerImpl;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import io.entgra.device.mgt.core.device.mgt.core.config.DeviceConfigurationManager;
import io.entgra.device.mgt.core.device.mgt.core.config.DeviceManagementConfig;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Base64;

@MultipartConfig
@WebServlet("/user")
public class UserHandler extends HttpServlet {
    private static final EntgraLogger log = new EntgraUserLoggerImpl(UserHandler.class);
    UserLogContext.Builder userLogContextBuilder = new UserLogContext.Builder();
    private static final long serialVersionUID = 9050048549140517002L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String keymanagerUrl =
                    req.getScheme() + HandlerConstants.SCHEME_SEPARATOR +
                            System.getProperty(HandlerConstants.IOT_KM_HOST_ENV_VAR)
                            + HandlerConstants.COLON + HandlerUtil.getKeyManagerPort(req.getScheme());
            HttpSession httpSession = req.getSession(false);
            if (httpSession == null) {
                HandlerUtil.sendUnAuthorizeResponse(resp);
                return;
            }

            AuthData authData = (AuthData) httpSession.getAttribute(HandlerConstants.SESSION_AUTH_DATA_KEY);
            if (authData == null) {
                HandlerUtil.sendUnAuthorizeResponse(resp);
                return;
            }

            String accessToken = authData.getAccessToken();
            String accessTokenWithoutPrefix = accessToken.substring(accessToken.indexOf("_") + 1);

            HttpPost tokenEndpoint = new HttpPost(keymanagerUrl + HandlerConstants.INTROSPECT_ENDPOINT);
            tokenEndpoint.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.toString());
            DeviceManagementConfig dmc = DeviceConfigurationManager.getInstance().getDeviceManagementConfig();
            String adminUsername = dmc.getKeyManagerConfigurations().getAdminUsername();
            String adminPassword = dmc.getKeyManagerConfigurations().getAdminPassword();
            tokenEndpoint.setHeader(HttpHeaders.AUTHORIZATION, HandlerConstants.BASIC + Base64.getEncoder()
                    .encodeToString((adminUsername + HandlerConstants.COLON + adminPassword).getBytes()));
            StringEntity tokenEPPayload = new StringEntity("token=" + accessTokenWithoutPrefix,
                    ContentType.APPLICATION_FORM_URLENCODED);
            tokenEndpoint.setEntity(tokenEPPayload);
            ProxyResponse tokenStatus = HandlerUtil.execute(tokenEndpoint);

            if (tokenStatus.getExecutorResponse().contains(HandlerConstants.EXECUTOR_EXCEPTION_PREFIX)) {
                if (tokenStatus.getCode() == HttpStatus.SC_UNAUTHORIZED) {
                    tokenStatus = HandlerUtil.retryRequestWithRefreshedToken(req, tokenEndpoint, keymanagerUrl);
                    if(!HandlerUtil.isResponseSuccessful(tokenStatus)) {
                        HandlerUtil.handleError(resp, tokenStatus);
                        return;
                    }
                } else {
                    log.error("Error occurred while invoking the API to get token status.");
                    HandlerUtil.handleError(resp, tokenStatus);
                    return;
                }
            }
            String tokenData = tokenStatus.getData();
            if (tokenData == null) {
                log.error("Invalid token data is received.");
                HandlerUtil.handleError(resp, tokenStatus);
                return;
            }
            JsonParser jsonParser = new JsonParser();
            JsonElement jTokenResult = jsonParser.parse(tokenData);
            if (jTokenResult.isJsonObject()) {
                JsonObject jTokenResultAsJsonObject = jTokenResult.getAsJsonObject();
                if (!jTokenResultAsJsonObject.get("active").getAsBoolean()) {
                    HandlerUtil.sendUnAuthorizeResponse(resp);
                    return;
                }
                ProxyResponse proxyResponse = new ProxyResponse();
                proxyResponse.setStatus(ProxyResponse.Status.SUCCESS);
                proxyResponse.setCode(HttpStatus.SC_OK);
                proxyResponse.setData(
                        jTokenResultAsJsonObject.get("username").getAsString().replaceAll("@carbon.super", ""));
                HandlerUtil.handleSuccess(resp, proxyResponse);
                httpSession.setAttribute(HandlerConstants.USERNAME_WITH_DOMAIN, jTokenResultAsJsonObject.get("username").getAsString());
                log.info("Customer login", userLogContextBuilder.setUserName(proxyResponse.getData()).setUserRegistered(true).build());
            }
        } catch (IOException e) {
            log.error("Error occurred while sending the response into the socket. ", e);
        } catch (JsonSyntaxException e) {
            log.error("Error occurred while parsing the response. ", e);
        }
    }
}
