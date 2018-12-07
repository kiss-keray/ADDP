/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nix.jingxun.addp.rpc.remoting.netty;


import io.netty.util.internal.logging.InternalLogLevel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

public class NettyLogger {

    private static AtomicBoolean nettyLoggerSeted = new AtomicBoolean(false);
    
    private static InternalLogLevel nettyLogLevel = InternalLogLevel.ERROR;

    public static void initNettyLogger() {
        if (!nettyLoggerSeted.get()) {
            try {
                io.netty.util.internal.logging.InternalLoggerFactory.setDefaultFactory(new NettyBridgeLoggerFactory());
            } catch (Throwable e) {
                //ignore
            }
            nettyLoggerSeted.set(true);
        }
    }

    private static class NettyBridgeLoggerFactory extends io.netty.util.internal.logging.InternalLoggerFactory {
        @Override
        protected io.netty.util.internal.logging.InternalLogger newInstance(String s) {
            return new NettyBridgeLogger(s);
        }
    }
    @Slf4j
    private static class NettyBridgeLogger implements io.netty.util.internal.logging.InternalLogger {


        public NettyBridgeLogger(String name) {
        }

        @Override
        public String name() {
            return log.getName();
        }

        @Override
        public boolean isEnabled(InternalLogLevel internalLogLevel) {
            return nettyLogLevel.ordinal() <= internalLogLevel.ordinal();
        }

        @Override
        public void log(InternalLogLevel internalLogLevel, String s) {
            if (internalLogLevel.equals(InternalLogLevel.DEBUG)) {
                log.debug(s);
            }
            if (internalLogLevel.equals(InternalLogLevel.TRACE)) {
                log.info(s);
            }
            if (internalLogLevel.equals(InternalLogLevel.INFO)) {
                log.info(s);
            }
            if (internalLogLevel.equals(InternalLogLevel.WARN)) {
                log.warn(s);
            }
            if (internalLogLevel.equals(InternalLogLevel.ERROR)) {
                log.error(s);
            }
        }

        @Override
        public void log(InternalLogLevel internalLogLevel, String s, Object o) {
            if (internalLogLevel.equals(InternalLogLevel.DEBUG)) {
                log.debug(s, o);
            }
            if (internalLogLevel.equals(InternalLogLevel.TRACE)) {
                log.info(s, o);
            }
            if (internalLogLevel.equals(InternalLogLevel.INFO)) {
                log.info(s, o);
            }
            if (internalLogLevel.equals(InternalLogLevel.WARN)) {
                log.warn(s, o);
            }
            if (internalLogLevel.equals(InternalLogLevel.ERROR)) {
                log.error(s, o);
            }
        }

        @Override
        public void log(InternalLogLevel internalLogLevel, String s, Object o, Object o1) {
            if (internalLogLevel.equals(InternalLogLevel.DEBUG)) {
                log.debug(s, o, o1);
            }
            if (internalLogLevel.equals(InternalLogLevel.TRACE)) {
                log.info(s, o, o1);
            }
            if (internalLogLevel.equals(InternalLogLevel.INFO)) {
                log.info(s, o, o1);
            }
            if (internalLogLevel.equals(InternalLogLevel.WARN)) {
                log.warn(s, o, o1);
            }
            if (internalLogLevel.equals(InternalLogLevel.ERROR)) {
                log.error(s, o, o1);
            }
        }

        @Override
        public void log(InternalLogLevel internalLogLevel, String s, Object... objects) {
            if (internalLogLevel.equals(InternalLogLevel.DEBUG)) {
                log.debug(s, objects);
            }
            if (internalLogLevel.equals(InternalLogLevel.TRACE)) {
                log.info(s, objects);
            }
            if (internalLogLevel.equals(InternalLogLevel.INFO)) {
                log.info(s, objects);
            }
            if (internalLogLevel.equals(InternalLogLevel.WARN)) {
                log.warn(s, objects);
            }
            if (internalLogLevel.equals(InternalLogLevel.ERROR)) {
                log.error(s, objects);
            }
        }

        @Override
        public void log(InternalLogLevel internalLogLevel, String s, Throwable throwable) {
            if (internalLogLevel.equals(InternalLogLevel.DEBUG)) {
                log.debug(s, throwable);
            }
            if (internalLogLevel.equals(InternalLogLevel.TRACE)) {
                log.info(s, throwable);
            }
            if (internalLogLevel.equals(InternalLogLevel.INFO)) {
                log.info(s, throwable);
            }
            if (internalLogLevel.equals(InternalLogLevel.WARN)) {
                log.warn(s, throwable);
            }
            if (internalLogLevel.equals(InternalLogLevel.ERROR)) {
                log.error(s, throwable);
            }
        }

        @Override
        public boolean isTraceEnabled() {
            return isEnabled(InternalLogLevel.TRACE);
        }

        @Override
        public void trace(String var1) {
            log.info(var1);
        }

        @Override
        public void trace(String var1, Object var2) {
            log.info(var1, var2);
        }

        @Override
        public void trace(String var1, Object var2, Object var3) {
            log.info(var1, var2, var3);
        }

        @Override
        public void trace(String var1, Object... var2) {
            log.info(var1, var2);
        }

        @Override
        public void trace(String var1, Throwable var2) {
            log.info(var1, var2);
        }

        @Override
        public boolean isDebugEnabled() {
            return isEnabled(InternalLogLevel.DEBUG);
        }

        @Override
        public void debug(String var1) {
            log.debug(var1);
        }

        @Override
        public void debug(String var1, Object var2) {
            log.debug(var1, var2);
        }

        @Override
        public void debug(String var1, Object var2, Object var3) {
            log.debug(var1, var2, var3);
        }

        @Override
        public void debug(String var1, Object... var2) {
            log.debug(var1, var2);
        }

        @Override
        public void debug(String var1, Throwable var2) {
            log.debug(var1, var2);
        }

        @Override
        public boolean isInfoEnabled() {
            return isEnabled(InternalLogLevel.INFO);
        }

        @Override
        public void info(String var1) {
            log.info(var1);
        }

        @Override
        public void info(String var1, Object var2) {
            log.info(var1, var2);
        }

        @Override
        public void info(String var1, Object var2, Object var3) {
            log.info(var1, var2, var3);
        }

        @Override
        public void info(String var1, Object... var2) {
            log.info(var1, var2);
        }

        @Override
        public void info(String var1, Throwable var2) {
            log.info(var1, var2);
        }

        @Override
        public boolean isWarnEnabled() {
            return isEnabled(InternalLogLevel.WARN);
        }

        @Override
        public void warn(String var1) {
            log.warn(var1);
        }

        @Override
        public void warn(String var1, Object var2) {
            log.warn(var1, var2);
        }

        @Override
        public void warn(String var1, Object... var2) {
            log.warn(var1, var2);
        }

        @Override
        public void warn(String var1, Object var2, Object var3) {
            log.warn(var1, var2, var3);
        }

        @Override
        public void warn(String var1, Throwable var2) {
            log.warn(var1, var2);
        }

        @Override
        public boolean isErrorEnabled() {
            return isEnabled(InternalLogLevel.ERROR);
        }

        @Override
        public void error(String var1) {
            log.error(var1);
        }

        @Override
        public void error(String var1, Object var2) {
            log.error(var1, var2);
        }

        @Override
        public void error(String var1, Object var2, Object var3) {
            log.error(var1, var2, var3);
        }

        @Override
        public void error(String var1, Object... var2) {
            log.error(var1, var2);
        }

        @Override
        public void error(String var1, Throwable var2) {
            log.error(var1, var2);
        }
    }

}
