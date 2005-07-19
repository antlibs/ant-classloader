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
package org.apache.tools.ant.taskdefs.classloader.report;

public final class ClassloaderReportHandle implements Comparable {
    public static final int BOOTSTRAP = 0;
    public static final int EXTENSION = 1;
    public static final int SYSTEM = 2;
    public static final int PROJECT = 3;
    public static final int CORE = 4;
    public static final int THREAD = 5;
    public static final int CURRENT = 6;
    public static final int REFERENCED = 7;
    public static final int DEFINED = 8;
    public static final int PARENT = 9;
    public static final int OTHER = 10;
    public static final ClassloaderReportHandle BOOTSTRAPHANDLE = new ClassloaderReportHandle(
            BOOTSTRAP, null);
    public static final ClassloaderReportHandle EXTENSIONHANDLE = new ClassloaderReportHandle(
            EXTENSION, null);
    public static final ClassloaderReportHandle SYSTEMHANDLE = new ClassloaderReportHandle(
            SYSTEM, null);
    public static final ClassloaderReportHandle PROJECTHANDLE = new ClassloaderReportHandle(
            PROJECT, null);
    public static final ClassloaderReportHandle COREHANDLE = new ClassloaderReportHandle(
            CORE, null);
    public static final ClassloaderReportHandle THREADHANDLE = new ClassloaderReportHandle(
            THREAD, null);
    public static final ClassloaderReportHandle CURRENTHANDLE = new ClassloaderReportHandle(
            CURRENT, null);
    private static final String[] names = {
        "BootstrapClassloader",
        "ExtensionClassloader",
        "SystemClassloader",
        "ProjectClassloader",
        "CoreLoader",
        "ThreadContextClassloader",
        "Current ClassloaderBase",
        "Referenced",
        "Defined TaskLoader",
        "Parent of ",
        "other"};
    String name;
    int type;
    public ClassloaderReportHandle(int type, String name) {
        this.type = type;
        this.name = name;
    }
    public int compareTo(Object to) {
        if (to == this) {
            return 0;
        }
        ClassloaderReportHandle h = (ClassloaderReportHandle) to;
        if (type != h.type) {
            return (type < h.type) ? -1 : 1;
        }
        if ((name == null) != (h.name == null)) {
            return (name == null) ? -1 : 1;
        }
        return (name == null) ? 0 : name.compareTo(h.name);
    }
    public boolean equals(Object to) {
        if (to == this) {
            return true;
        }
        if (to == null) {
            return false;
        }
        try {
            ClassloaderReportHandle h = (ClassloaderReportHandle) to;
            if (type != h.type) {
                return false;
            }
            if ((name == null) != (h.name == null)) {
                return false;
            }
            return (name == null) ? true : name.equals(h.name);
        } catch (ClassCastException e) {
            return false;
        }
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return names[type];
    }
    public int hashCode() {
        if (name == null)
            return type;
        return type ^ name.hashCode();
    }
    public boolean isPopular() {
        return type <= CURRENT;
    }
    public String toString() {
        if (name == null) {
            return getType();
        }
        return getType() + "=" + getName();
    }
}