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

package io.entgra.device.mgt.core.apimgt.extension.rest.api;

import com.google.gson.Gson;
import io.entgra.device.mgt.core.apimgt.extension.rest.api.bean.APIMConsumer.*;
import io.entgra.device.mgt.core.apimgt.extension.rest.api.constants.Constants;
import io.entgra.device.mgt.core.apimgt.extension.rest.api.dto.AccessTokenInfo;
import io.entgra.device.mgt.core.apimgt.extension.rest.api.dto.ApiApplicationInfo;
import io.entgra.device.mgt.core.apimgt.extension.rest.api.exceptions.APIServicesException;
import io.entgra.device.mgt.core.apimgt.extension.rest.api.exceptions.BadRequestException;
import io.entgra.device.mgt.core.apimgt.extension.rest.api.exceptions.UnexpectedResponseException;
import io.entgra.device.mgt.core.apimgt.extension.rest.api.util.HttpsTrustManagerUtils;
import okhttp3.*;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ConsumerRESTAPIServicesImpl implements ConsumerRESTAPIServices {

    private static final Log log = LogFactory.getLog(ConsumerRESTAPIServicesImpl.class);
    private static final OkHttpClient client = new OkHttpClient(HttpsTrustManagerUtils.getSSLClient().newBuilder());
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final Gson gson = new Gson();
    private static final String host = System.getProperty(Constants.IOT_CORE_HOST);
    private static final String port = System.getProperty(Constants.IOT_CORE_HTTPS_PORT);
    private static final String endPointPrefix = Constants.HTTPS_PROTOCOL + Constants.SCHEME_SEPARATOR + host
            + Constants.COLON + port;

    @Override
    public Application[] getAllApplications(ApiApplicationInfo applicationInfo, String appName)
            throws APIServicesException, BadRequestException, UnexpectedResponseException {

        String getAllApplicationsUrl = endPointPrefix + Constants.APPLICATIONS_API + "?query=" + appName;
        Request request = new Request.Builder()
                .url(getAllApplicationsUrl)
                .addHeader(Constants.AUTHORIZATION_HEADER_NAME, Constants.AUTHORIZATION_HEADER_PREFIX_BEARER
                        + applicationInfo.getAccess_token())
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (HttpStatus.SC_OK == response.code()) {
                JSONArray applicationList = (JSONArray) new JSONObject(response.body().string()).get("list");
                return gson.fromJson(applicationList.toString(), Application[].class);
            } else if (HttpStatus.SC_UNAUTHORIZED == response.code()) {
                APIApplicationServices apiApplicationServices = new APIApplicationServicesImpl();
                AccessTokenInfo refreshedAccessToken = apiApplicationServices.
                        generateAccessTokenFromRefreshToken(applicationInfo.getRefresh_token(),
                                applicationInfo.getClientId(), applicationInfo.getClientSecret());
                ApiApplicationInfo refreshedApiApplicationInfo = returnApplicationInfo(applicationInfo, refreshedAccessToken);
                //TODO: max attempt count
                return getAllApplications(refreshedApiApplicationInfo, appName);
            } else if (HttpStatus.SC_BAD_REQUEST == response.code()) {
                String msg = "Bad Request, Invalid request";
                log.error(msg);
                throw new BadRequestException(msg);
            } else {
                String msg = "Response : " + response.code() + response.body();
                throw new UnexpectedResponseException(msg);
            }
        } catch (IOException e) {
            String msg = "Error occurred while processing the response";
            log.error(msg, e);
            throw new APIServicesException(msg, e);
        }
    }

    @Override
    public Application getDetailsOfAnApplication(ApiApplicationInfo apiApplicationInfo, String applicationId)
            throws APIServicesException, BadRequestException, UnexpectedResponseException {

        String getAllApplicationsUrl = endPointPrefix + Constants.APPLICATIONS_API + Constants.SLASH + applicationId;
        Request request = new Request.Builder()
                .url(getAllApplicationsUrl)
                .addHeader(Constants.AUTHORIZATION_HEADER_NAME, Constants.AUTHORIZATION_HEADER_PREFIX_BEARER
                        + apiApplicationInfo.getAccess_token())
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (HttpStatus.SC_OK == response.code()) {
                return gson.fromJson(response.body().string(), Application.class);
            } else if (HttpStatus.SC_UNAUTHORIZED == response.code()) {
                APIApplicationServices apiApplicationServices = new APIApplicationServicesImpl();
                AccessTokenInfo refreshedAccessToken = apiApplicationServices.
                        generateAccessTokenFromRefreshToken(apiApplicationInfo.getRefresh_token(),
                                apiApplicationInfo.getClientId(), apiApplicationInfo.getClientSecret());
                ApiApplicationInfo refreshedApiApplicationInfo = returnApplicationInfo(apiApplicationInfo, refreshedAccessToken);
                //TODO: max attempt count
                return getDetailsOfAnApplication(refreshedApiApplicationInfo, applicationId);
            } else if (HttpStatus.SC_BAD_REQUEST == response.code()) {
                String msg = "Bad Request, Invalid request";
                log.error(msg);
                throw new BadRequestException(msg);
            } else {
                String msg = "Response : " + response.code() + response.body();
                throw new UnexpectedResponseException(msg);
            }
        } catch (IOException e) {
            String msg = "Error occurred while processing the response";
            log.error(msg, e);
            throw new APIServicesException(msg, e);
        }
    }

    @Override
    public Application createApplication(ApiApplicationInfo apiApplicationInfo, Application application)
            throws APIServicesException, BadRequestException, UnexpectedResponseException {

        String getAllScopesUrl = endPointPrefix + Constants.APPLICATIONS_API;

        String applicationInfo = "{\n" +
                "  \"name\": \"" + application.getName() + "\",\n" +
                "  \"throttlingPolicy\": \"" + application.getThrottlingPolicy() + "\",\n" +
                "  \"description\": \"" + application.getDescription() + "\",\n" +
                "  \"tokenType\": \"" + application.getTokenType() + "\",\n" +
                "  \"groups\": " + gson.toJson(application.getGroups()) + ",\n" +
                "  \"attributes\": " + application.getAttributes().toString() + ",\n" +
                "  \"subscriptionScopes\": " + gson.toJson(application.getSubscriptionScopes()) + "\n" +
                "}";

        RequestBody requestBody = RequestBody.create(JSON, applicationInfo);
        Request request = new Request.Builder()
                .url(getAllScopesUrl)
                .addHeader(Constants.AUTHORIZATION_HEADER_NAME, Constants.AUTHORIZATION_HEADER_PREFIX_BEARER
                        + apiApplicationInfo.getAccess_token())
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (HttpStatus.SC_CREATED == response.code()) {
                return gson.fromJson(response.body().string(), Application.class);
            } else if (HttpStatus.SC_UNAUTHORIZED == response.code()) {
                APIApplicationServices apiApplicationServices = new APIApplicationServicesImpl();
                AccessTokenInfo refreshedAccessToken = apiApplicationServices.
                        generateAccessTokenFromRefreshToken(apiApplicationInfo.getRefresh_token(),
                                apiApplicationInfo.getClientId(), apiApplicationInfo.getClientSecret());
                ApiApplicationInfo refreshedApiApplicationInfo = returnApplicationInfo(apiApplicationInfo, refreshedAccessToken);
                //TODO: max attempt count
                return createApplication(refreshedApiApplicationInfo, application);
            } else if (HttpStatus.SC_BAD_REQUEST == response.code()) {
                String msg = "Bad Request, Invalid request body";
                log.error(msg);
                throw new BadRequestException(msg);
            } else {
                String msg = "Response : " + response.code() + response.body();
                throw new UnexpectedResponseException(msg);
            }
        } catch (IOException e) {
            String msg = "Error occurred while processing the response";
            log.error(msg, e);
            throw new APIServicesException(msg, e);
        }
    }

    @Override
    public Subscription[] getAllSubscriptions(ApiApplicationInfo apiApplicationInfo, String applicationId)
            throws APIServicesException, BadRequestException, UnexpectedResponseException {

        String getAllScopesUrl = endPointPrefix + Constants.SUBSCRIPTION_API + "?applicationId=" + applicationId;
        Request request = new Request.Builder()
                .url(getAllScopesUrl)
                .addHeader(Constants.AUTHORIZATION_HEADER_NAME, Constants.AUTHORIZATION_HEADER_PREFIX_BEARER
                        + apiApplicationInfo.getAccess_token())
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (HttpStatus.SC_OK == response.code()) {
                JSONArray subscriptionList = (JSONArray) new JSONObject(response.body().string()).get("list");
                return gson.fromJson(subscriptionList.toString(), Subscription[].class);
            } else if (HttpStatus.SC_UNAUTHORIZED == response.code()) {
                APIApplicationServices apiApplicationServices = new APIApplicationServicesImpl();
                AccessTokenInfo refreshedAccessToken = apiApplicationServices.
                        generateAccessTokenFromRefreshToken(apiApplicationInfo.getRefresh_token(),
                                apiApplicationInfo.getClientId(), apiApplicationInfo.getClientSecret());
                ApiApplicationInfo rehreshedApiApplicationInfo = returnApplicationInfo(apiApplicationInfo, refreshedAccessToken);
                //TODO: max attempt count
                return getAllSubscriptions(rehreshedApiApplicationInfo, applicationId);
            } else if (HttpStatus.SC_BAD_REQUEST == response.code()) {
                String msg = "Bad Request, Invalid request";
                log.error(msg);
                throw new BadRequestException(msg);
            } else {
                String msg = "Response : " + response.code() + response.body();
                throw new UnexpectedResponseException(msg);
            }
        } catch (IOException e) {
            String msg = "Error occurred while processing the response";
            log.error(msg, e);
            throw new APIServicesException(msg, e);
        }
    }

    @Override
    public APIInfo[] getAllApis(ApiApplicationInfo applicationInfo, Map<String, String> queryParams,
                                Map<String, String> headerParams)
            throws APIServicesException, BadRequestException, UnexpectedResponseException {

        String getAPIsURL = endPointPrefix + Constants.DEV_PORTAL_API;

        for (Map.Entry<String, String> query : queryParams.entrySet()) {
            getAPIsURL = getAPIsURL + Constants.AMPERSAND + query.getKey() + Constants.EQUAL + query.getValue();
        }

        Request.Builder builder = new Request.Builder();
        builder.url(getAPIsURL);
        builder.addHeader(Constants.AUTHORIZATION_HEADER_NAME, Constants.AUTHORIZATION_HEADER_PREFIX_BEARER
                + applicationInfo.getAccess_token());
        for (Map.Entry<String, String> header : headerParams.entrySet()) {
            builder.addHeader(header.getKey(), header.getValue());
        }
        builder.get();
        Request request = builder.build();

        try {
            Response response = client.newCall(request).execute();
            if (HttpStatus.SC_OK == response.code()) {
                JSONArray apiList = (JSONArray) new JSONObject(response.body().string()).get("list");
                return gson.fromJson(apiList.toString(), APIInfo[].class);
            } else if (HttpStatus.SC_UNAUTHORIZED == response.code()) {
                APIApplicationServices apiApplicationServices = new APIApplicationServicesImpl();
                AccessTokenInfo refreshedAccessToken = apiApplicationServices.
                        generateAccessTokenFromRefreshToken(applicationInfo.getRefresh_token(),
                                applicationInfo.getClientId(), applicationInfo.getClientSecret());
                ApiApplicationInfo rehreshedApiApplicationInfo = returnApplicationInfo(applicationInfo, refreshedAccessToken);
                //TODO: max attempt count
                return getAllApis(rehreshedApiApplicationInfo, queryParams, headerParams);
            } else if (HttpStatus.SC_BAD_REQUEST == response.code()) {
                String msg = "Bad Request, Invalid request";
                log.error(msg);
                throw new BadRequestException(msg);
            } else {
                String msg = "Response : " + response.code() + response.body();
                throw new UnexpectedResponseException(msg);
            }
        } catch (IOException e) {
            String msg = "Error occurred while processing the response";
            log.error(msg, e);
            throw new APIServicesException(msg, e);
        }
    }

    @Override
    public Subscription createSubscription(ApiApplicationInfo applicationInfo, Subscription subscriptions)
            throws APIServicesException, BadRequestException, UnexpectedResponseException {

        String getAllScopesUrl = endPointPrefix + Constants.SUBSCRIPTION_API;

        String subscriptionObject = "{\n" +
                "  \"applicationId\": \"" + subscriptions.getApplicationId() + "\",\n" +
                "  \"apiId\": \"" + subscriptions.getApiId() + "\",\n" +
                "  \"throttlingPolicy\": \"" + subscriptions.getThrottlingPolicy() + "\",\n" +
                "  \"requestedThrottlingPolicy\": \"" + subscriptions.getRequestedThrottlingPolicy() + "\"\n" +
                "}";

        RequestBody requestBody = RequestBody.create(JSON, subscriptionObject);
        Request request = new Request.Builder()
                .url(getAllScopesUrl)
                .addHeader(Constants.AUTHORIZATION_HEADER_NAME, Constants.AUTHORIZATION_HEADER_PREFIX_BEARER
                        + applicationInfo.getAccess_token())
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (HttpStatus.SC_CREATED == response.code()) {
                return gson.fromJson(response.body().string(), Subscription.class);
            } else if (HttpStatus.SC_UNAUTHORIZED == response.code()) {
                APIApplicationServices apiApplicationServices = new APIApplicationServicesImpl();
                AccessTokenInfo refreshedAccessToken = apiApplicationServices.
                        generateAccessTokenFromRefreshToken(applicationInfo.getRefresh_token(),
                                applicationInfo.getClientId(), applicationInfo.getClientSecret());
                ApiApplicationInfo refreshedApiApplicationInfo = returnApplicationInfo(applicationInfo, refreshedAccessToken);
                //TODO: max attempt count
                return createSubscription(refreshedApiApplicationInfo, subscriptions);
            } else if (HttpStatus.SC_BAD_REQUEST == response.code()) {
                String msg = "Bad Request, Invalid request body";
                log.error(msg);
                throw new BadRequestException(msg);
            } else {
                String msg = "Response : " + response.code() + response.body();
                throw new UnexpectedResponseException(msg);
            }
        } catch (IOException e) {
            String msg = "Error occurred while processing the response";
            log.error(msg, e);
            throw new APIServicesException(msg, e);
        }
    }

    @Override
    public Subscription[] createSubscriptions(ApiApplicationInfo apiApplicationInfo, List<Subscription> subscriptions)
            throws APIServicesException, BadRequestException, UnexpectedResponseException {

        String getAllScopesUrl = endPointPrefix + Constants.SUBSCRIPTION_API + "/multiple";

        String subscriptionsList = gson.toJson(subscriptions);

        RequestBody requestBody = RequestBody.create(JSON, subscriptionsList);
        Request request = new Request.Builder()
                .url(getAllScopesUrl)
                .addHeader(Constants.AUTHORIZATION_HEADER_NAME, Constants.AUTHORIZATION_HEADER_PREFIX_BEARER
                        + apiApplicationInfo.getAccess_token())
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (HttpStatus.SC_OK == response.code()) {
                JSONArray subscriptionsArray = (JSONArray) new JSONObject(response.body().string()).get("list");
                return gson.fromJson(subscriptionsArray.toString(), Subscription[].class);
            } else if (HttpStatus.SC_UNAUTHORIZED == response.code()) {
                APIApplicationServices apiApplicationServices = new APIApplicationServicesImpl();
                AccessTokenInfo refreshedAccessToken = apiApplicationServices.
                        generateAccessTokenFromRefreshToken(apiApplicationInfo.getRefresh_token(),
                                apiApplicationInfo.getClientId(), apiApplicationInfo.getClientSecret());
                ApiApplicationInfo refreshedApiApplicationInfo = returnApplicationInfo(apiApplicationInfo, refreshedAccessToken);
                //TODO: max attempt count
                return createSubscriptions(refreshedApiApplicationInfo, subscriptions);
            } else if (HttpStatus.SC_BAD_REQUEST == response.code()) {
                String msg = "Bad Request, Invalid request body";
                log.error(msg);
                throw new BadRequestException(msg);
            } else {
                String msg = "Response : " + response.code() + response.body();
                throw new UnexpectedResponseException(msg);
            }
        } catch (IOException e) {
            String msg = "Error occurred while processing the response";
            log.error(msg, e);
            throw new APIServicesException(msg, e);
        }
    }

    @Override
    public ApplicationKey generateApplicationKeys(ApiApplicationInfo apiApplicationInfo, Application application)
            throws APIServicesException, BadRequestException, UnexpectedResponseException {

        String getAllScopesUrl = endPointPrefix + Constants.SUBSCRIPTION_API + Constants.SLASH +
                application.getApplicationId() + "/generate-keys";

        String keyInfo = "{\n" +
                "  \"keyType\": \"PRODUCTION\",\n" +
                "  \"keyManager\": \"Resident Key Manager\",\n" +
                "  \"grantTypesToBeSupported\": [\n" +
                "    \"password\",\n" +
                "    \"client_credentials\"\n" +
                "  ],\n" +
                "  \"callbackUrl\": \"http://sample.com/callback/url\",\n" +
                "  \"scopes\": [\n" +
                "    \"am_application_scope\",\n" +
                "    \"default\"\n" +
                "  ],\n" +
                "  \"validityTime\": 3600,\n" +
                "  \"additionalProperties\": {}\n" +
                "}";

        RequestBody requestBody = RequestBody.create(JSON, keyInfo);
        Request request = new Request.Builder()
                .url(getAllScopesUrl)
                .addHeader(Constants.AUTHORIZATION_HEADER_NAME, Constants.AUTHORIZATION_HEADER_PREFIX_BEARER
                        + apiApplicationInfo.getAccess_token())
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (HttpStatus.SC_OK == response.code()) {
                return gson.fromJson(response.body().string(), ApplicationKey.class);
            } else if (HttpStatus.SC_UNAUTHORIZED == response.code()) {
                APIApplicationServices apiApplicationServices = new APIApplicationServicesImpl();
                AccessTokenInfo refreshedAccessToken = apiApplicationServices.
                        generateAccessTokenFromRefreshToken(apiApplicationInfo.getRefresh_token(),
                                apiApplicationInfo.getClientId(), apiApplicationInfo.getClientSecret());
                ApiApplicationInfo refreshedApiApplicationKey = returnApplicationInfo(apiApplicationInfo, refreshedAccessToken);
                //TODO: max attempt count
                return generateApplicationKeys(refreshedApiApplicationKey, application);
            } else if (HttpStatus.SC_BAD_REQUEST == response.code()) {
                String msg = "Bad Request, Invalid request body";
                log.error(msg);
                throw new BadRequestException(msg);
            } else {
                String msg = "Response : " + response.code() + response.body();
                throw new UnexpectedResponseException(msg);
            }
        } catch (IOException e) {
            String msg = "Error occurred while processing the response";
            log.error(msg, e);
            throw new APIServicesException(msg, e);
        }
    }

    @Override
    public KeyManager[] getAllKeyManagers(ApiApplicationInfo apiApplicationInfo)
            throws APIServicesException, BadRequestException, UnexpectedResponseException {

        String getAllKeyManagersUrl = endPointPrefix + Constants.KEY_MANAGERS_API;
        Request request = new Request.Builder()
                .url(getAllKeyManagersUrl)
                .addHeader(Constants.AUTHORIZATION_HEADER_NAME, Constants.AUTHORIZATION_HEADER_PREFIX_BEARER
                        + apiApplicationInfo.getAccess_token())
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (HttpStatus.SC_OK == response.code()) {
                JSONArray keyManagerList = (JSONArray) new JSONObject(response.body().string()).get("list");
                return gson.fromJson(keyManagerList.toString(), KeyManager[].class);
            } else if (HttpStatus.SC_UNAUTHORIZED == response.code()) {
                APIApplicationServices apiApplicationServices = new APIApplicationServicesImpl();
                AccessTokenInfo refreshedAccessToken = apiApplicationServices.
                        generateAccessTokenFromRefreshToken(apiApplicationInfo.getRefresh_token(),
                                apiApplicationInfo.getClientId(), apiApplicationInfo.getClientSecret());
                ApiApplicationInfo refreshedApiApplicationInfo = returnApplicationInfo(apiApplicationInfo, refreshedAccessToken);
                //TODO: max attempt count
                return getAllKeyManagers(refreshedApiApplicationInfo);
            } else if (HttpStatus.SC_BAD_REQUEST == response.code()) {
                String msg = "Bad Request, Invalid request";
                log.error(msg);
                throw new BadRequestException(msg);
            } else {
                String msg = "Response : " + response.code() + response.body();
                throw new UnexpectedResponseException(msg);
            }
        } catch (IOException e) {
            String msg = "Error occurred while processing the response";
            log.error(msg, e);
            throw new APIServicesException(msg, e);
        }
    }

    private ApiApplicationInfo returnApplicationInfo(ApiApplicationInfo refreshedApplicationInfo, AccessTokenInfo refreshedToken) {

        ApiApplicationInfo applicationInfo = null;
        applicationInfo.setClientId(refreshedApplicationInfo.getClientId());
        applicationInfo.setClientSecret(refreshedApplicationInfo.getClientSecret());
        applicationInfo.setAccess_token(refreshedToken.getAccess_token());
        applicationInfo.setRefresh_token(refreshedToken.getRefresh_token());

        return applicationInfo;
    }
}
