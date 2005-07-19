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
 * Actions for ClassLoaderAdapter.
 */
public final class ClassLoaderAdapterAction {
    private static final int IDAPPEND = 2;
    private static final int IDCREATE = 1;
    private static final int IDGETPATH = 3;
    private static final int IDREPORT = 4;
    /**
     * Append Path to an existing ClassLoader instance.
     */
    public static final ClassLoaderAdapterAction APPEND = new ClassLoaderAdapterAction(
            IDAPPEND);
    /**
     * Create a new ClassLoader instance.
     */
    public static final ClassLoaderAdapterAction CREATE = new ClassLoaderAdapterAction(
            IDCREATE);
    /**
     * Get the path of an existing ClassLoader instance.
     */
    public static final ClassLoaderAdapterAction GETPATH = new ClassLoaderAdapterAction(
            IDGETPATH);
    /**
     * Get additional Report information.
     */
    public static final ClassLoaderAdapterAction REPORT = new ClassLoaderAdapterAction(
            IDREPORT);

    private ClassLoaderAdapterAction(int value) {
    }
}