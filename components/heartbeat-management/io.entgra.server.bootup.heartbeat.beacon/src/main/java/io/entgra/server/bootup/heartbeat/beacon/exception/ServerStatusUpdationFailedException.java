/*
 * Copyright (c) 2020, Entgra Pvt Ltd. (http://www.wso2.org) All Rights Reserved.
 *
 * Entgra Pvt Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.entgra.server.bootup.heartbeat.beacon.exception;

public class ServerStatusUpdationFailedException extends Exception {

    private static final long serialVersionUID = -2610630531027402610L;

    public ServerStatusUpdationFailedException(String msg, Exception nestedEx) {
        super(msg, nestedEx);
    }

    public ServerStatusUpdationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerStatusUpdationFailedException(String msg) {
        super(msg);
    }

    public ServerStatusUpdationFailedException() {
        super();
    }

    public ServerStatusUpdationFailedException(Throwable cause) {
        super(cause);
    }

}
