/*
 * Copyright  2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.tools.ant.taskdefs.classloader;

/**
 * Exception, internally used for searching for an adapter.
 */
public class ClassloaderAdapterException extends Exception {
    /**
     * The handler retrieves no adapter.
     */
    public static final int NO_ADAPTER = 2;
    /**
     * The handlerset retrieves no handler.
     */
    public static final int NO_HANDLER = 1;
    /**
     * No handlerset was found. (INTERNAL ERROR)
     */
    public static final int NO_HANDLERSET = 0;
    private static final long serialVersionUID = 1L;
    private final int reason;
    /**
     * Constructor.
     * @param reason One of NO_ADAPTER, NO_HANDLER or NO_HANDLERSET.
     */
    public ClassloaderAdapterException(int reason) {
        this.reason = reason;
    }
    /**
     * Gets the reason.
     * @return One of NO_ADAPTER, NO_HANDLER or NO_HANDLERSET.
     */
    public int getReason() {
        return reason;
    }
}