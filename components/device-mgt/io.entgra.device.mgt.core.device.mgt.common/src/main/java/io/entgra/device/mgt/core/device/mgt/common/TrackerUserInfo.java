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

package io.entgra.device.mgt.core.device.mgt.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "User", description = "This class carries all information related to a managed Traccar User.")
public class TrackerUserInfo implements Serializable {

    private static final long serialVersionUID = -6808358733610879805L;

    @ApiModelProperty(name = "userName", value = "The user's name that can be set on the device by the device user.",
            required = true)
    private String userName;

    public TrackerUserInfo() {
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String UserName) {
        this.userName = UserName;
    }
}
