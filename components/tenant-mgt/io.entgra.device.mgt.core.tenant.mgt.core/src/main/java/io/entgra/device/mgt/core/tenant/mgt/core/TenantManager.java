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
package io.entgra.device.mgt.core.tenant.mgt.core;

import io.entgra.device.mgt.core.tenant.mgt.common.exception.TenantMgtException;
import org.wso2.carbon.stratos.common.beans.TenantInfoBean;

public interface TenantManager {

    /**
     * Add default roles to a tenant described by the tenant info bean
     * @param tenantInfoBean The info bean that provides tenant info
     * @throws TenantMgtException Throws when error occurred while adding
     *                            a role into user store or adding default white label theme to created tenant
     */
    void addDefaultRoles(TenantInfoBean tenantInfoBean) throws TenantMgtException;

    /**
     * Add default application categories to a tenant described by the tenant info bean
     * @param tenantInfoBean The info bean that provides tenant info
     * @throws TenantMgtException Throws when error occurred while adding default application categories
     */
    void addDefaultAppCategories(TenantInfoBean tenantInfoBean) throws TenantMgtException;

    /**
     * Add default device status filters to a tenant described by the tenant info bean
     * @param tenantInfoBean The info bean that provides tenant info
     * @throws TenantMgtException Throws when error occurred while adding default application categories
     */
    void addDefaultDeviceStatusFilters(TenantInfoBean tenantInfoBean) throws TenantMgtException;
}
