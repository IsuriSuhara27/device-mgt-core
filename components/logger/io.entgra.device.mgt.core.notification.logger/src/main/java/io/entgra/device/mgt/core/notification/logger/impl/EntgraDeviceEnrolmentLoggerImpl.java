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
package io.entgra.device.mgt.core.notification.logger.impl;

import io.entgra.device.mgt.core.device.mgt.extensions.logger.LogContext;
import io.entgra.device.mgt.core.device.mgt.extensions.logger.spi.EntgraLogger;
import io.entgra.device.mgt.core.notification.logger.DeviceEnrolmentLogContext;
import io.entgra.device.mgt.core.notification.logger.util.MDCContextUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.MDC;

public class EntgraDeviceEnrolmentLoggerImpl implements EntgraLogger {

    private static Log log = null;

    public EntgraDeviceEnrolmentLoggerImpl(Class<?> clazz) {
        log = LogFactory.getLog(clazz);
    }

    public void info(String message) {
        log.info(message);
    }

    public void info(String message, Throwable t) {
        log.info(message, t);
    }

    @Override
    public void info(Object o) {
        log.info(o);
    }

    @Override
    public void info(Object o, Throwable throwable) {
        log.info(o, throwable);
    }

    @Override
    public void info(Object object, LogContext logContext) {
        DeviceEnrolmentLogContext deviceEnrolmentLogContext = (DeviceEnrolmentLogContext) logContext;
        MDCContextUtil.populateDeviceEnrolmentMDCContext(deviceEnrolmentLogContext);
        log.info(object);
    }

    @Override
    public void info(Object object, Throwable t, LogContext logContext) {
        DeviceEnrolmentLogContext deviceEnrolmentLogContext = (DeviceEnrolmentLogContext) logContext;
        MDCContextUtil.populateDeviceEnrolmentMDCContext(deviceEnrolmentLogContext);
        log.info(object, t);
    }

    public void debug(String message) {
        log.debug(message);
    }

    public void debug(String message, Throwable t) {
        log.debug(message, t);
    }

    @Override
    public void debug(Object o) {
        log.debug(o);
    }

    @Override
    public void debug(Object o, Throwable throwable) {
        log.debug(o, throwable);
    }

    @Override
    public void debug(Object object, LogContext logContext) {
        DeviceEnrolmentLogContext deviceEnrolmentLogContext = (DeviceEnrolmentLogContext) logContext;
        MDCContextUtil.populateDeviceEnrolmentMDCContext(deviceEnrolmentLogContext);
        log.debug(object);
    }

    @Override
    public void debug(Object object, Throwable t, LogContext logContext) {
        DeviceEnrolmentLogContext deviceEnrolmentLogContext = (DeviceEnrolmentLogContext) logContext;
        MDCContextUtil.populateDeviceEnrolmentMDCContext(deviceEnrolmentLogContext);
        log.debug(object, t);
    }

    public void error(String message) {
        log.error(message);
    }

    public void error(String message, Throwable t) {
        log.error(message, t);
    }

    @Override
    public void error(Object o) {
        log.error(o);
    }

    @Override
    public void error(Object o, Throwable throwable) {
        log.error(o, throwable);
    }

    @Override
    public void error(Object object, LogContext logContext) {
        DeviceEnrolmentLogContext deviceEnrolmentLogContext = (DeviceEnrolmentLogContext) logContext;
        MDCContextUtil.populateDeviceEnrolmentMDCContext(deviceEnrolmentLogContext);
        log.error(object);
    }

    @Override
    public void error(Object object, Throwable t, LogContext logContext) {
        DeviceEnrolmentLogContext deviceEnrolmentLogContext = (DeviceEnrolmentLogContext) logContext;
        MDCContextUtil.populateDeviceEnrolmentMDCContext(deviceEnrolmentLogContext);
        log.error(object, t);
    }

    public void warn(String message) {
        log.warn(message);
    }

    public void warn(String message, Throwable t) {
        log.warn(message, t);
    }

    @Override
    public void warn(Object o) {
        log.warn(o);
    }

    @Override
    public void warn(Object o, Throwable throwable) {
        log.warn(o, throwable);
    }

    @Override
    public void warn(Object object, LogContext logContext) {
        DeviceEnrolmentLogContext deviceEnrolmentLogContext = (DeviceEnrolmentLogContext) logContext;
        MDCContextUtil.populateDeviceEnrolmentMDCContext(deviceEnrolmentLogContext);
        log.warn(object);
    }

    @Override
    public void warn(Object object, Throwable t, LogContext logContext) {
        DeviceEnrolmentLogContext deviceEnrolmentLogContext = (DeviceEnrolmentLogContext) logContext;
        MDCContextUtil.populateDeviceEnrolmentMDCContext(deviceEnrolmentLogContext);
        log.warn(object, t);
    }

    public void trace(String message) {
        log.trace(message);
    }

    public void trace(String message, Throwable t) {
        log.trace(message, t);
    }

    @Override
    public void trace(Object o) {
        log.trace(o);
    }

    @Override
    public void trace(Object o, Throwable throwable) {
        log.trace(o, throwable);
    }

    @Override
    public void trace(Object object, LogContext logContext) {
        DeviceEnrolmentLogContext deviceEnrolmentLogContext = (DeviceEnrolmentLogContext) logContext;
        MDCContextUtil.populateDeviceEnrolmentMDCContext(deviceEnrolmentLogContext);
        log.trace(object);
    }

    @Override
    public void trace(Object object, Throwable t, LogContext logContext) {
        DeviceEnrolmentLogContext deviceEnrolmentLogContext = (DeviceEnrolmentLogContext) logContext;
        MDCContextUtil.populateDeviceEnrolmentMDCContext(deviceEnrolmentLogContext);
        log.trace(object, t);
    }

    public void fatal(String message) {
        log.fatal(message);
    }

    public void fatal(String message, Throwable t) {
        log.fatal(message, t);
    }

    @Override
    public void fatal(Object o) {
        log.fatal(0);
    }

    @Override
    public void fatal(Object o, Throwable throwable) {
        log.fatal(0, throwable);
    }

    @Override
    public void fatal(Object object, LogContext logContext) {
        DeviceEnrolmentLogContext deviceEnrolmentLogContext = (DeviceEnrolmentLogContext) logContext;
        MDCContextUtil.populateDeviceEnrolmentMDCContext(deviceEnrolmentLogContext);
        log.fatal(object);
    }

    @Override
    public void fatal(Object object, Throwable t, LogContext logContext) {
        DeviceEnrolmentLogContext deviceEnrolmentLogContext = (DeviceEnrolmentLogContext) logContext;
        MDCContextUtil.populateDeviceEnrolmentMDCContext(deviceEnrolmentLogContext);
        log.fatal(object, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    @Override
    public boolean isFatalEnabled() {
        return log.isFatalEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    @Override
    public void clearLogContext() {
        MDC.clear();
    }
}
