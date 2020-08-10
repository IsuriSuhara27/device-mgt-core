/* Copyright (c) 2020, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
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

package org.wso2.carbon.device.mgt.common.spi;

import org.wso2.carbon.device.mgt.common.exceptions.BadRequestException;
import org.wso2.carbon.device.mgt.common.exceptions.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.exceptions.OTPManagementException;
import org.wso2.carbon.device.mgt.common.otp.mgt.dto.OTPMailDTO;
import org.wso2.carbon.device.mgt.common.otp.mgt.wrapper.OTPWrapper;

public interface OTPManagementService {

    /**
     * Create OTP token and store tenant details in the DB
     * @param otpWrapper OTP Mail Wrapper object which contains tenant details of registering user
     * @throws OTPManagementException if error occurs while creating OTP token and storing tenant details.
     * @throws BadRequestException if found and incompatible payload to create OTP token.
     */
    void sendUserVerifyingMail(OTPWrapper otpWrapper) throws OTPManagementException, DeviceManagementException;

    /**
     * Check the validity of the OTP
     * @param oneTimeToken OTP
     * @return The OTP data
     * @throws OTPManagementException if error occurred whle verifying validity of the OPT
     * @throws BadRequestException if found an null value for OTP
     */
    OTPMailDTO isValidOTP(String oneTimeToken) throws OTPManagementException, BadRequestException;
}
