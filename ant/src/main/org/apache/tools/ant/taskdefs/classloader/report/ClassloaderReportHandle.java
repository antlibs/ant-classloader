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
/**
 * Role of a classloader.
 * @since Ant 1.7
 */
public final class ClassloaderReportHandle implements Comparable {
    private static final int BOOTSTRAP = 0;
    private static final int EXTENSION = 1;
    private static final int SYSTEM = 2;
    private static final int ANT_PROJECT = 3;
    private static final int ANT_CORE = 4;
    private static final int THREAD = 5;
    private static final int CURRENT = 6;
    /**
     * Annotates a Classloader referenced in the Ant references.
     */
    public static final int ANT_REFERENCED = 7;
    /**
     * Annotates a classloader used as the classloader of a defined
     * (and initialised) type or task.
     */
    public static final int ANT_DEFINED = 8;
    /**
     * Annotates a classloader referenced by another classloader as
     * it's parent classloader.
     */
    public static final int PARENT = 9;
    /**
     * Annotates a classloader role, not falling in the predefined
     * categories.
     */
    public static final int OTHER = 10;
    /**
     * Singleton for bootstrap classloader.
     */
    public static final ClassloaderReportHandle BOOTSTRAPHANDLE = new ClassloaderReportHandle(
            BOOTSTRAP, null);
    /**
     * Singleton for extension classloader.
     */
    public static final ClassloaderReportHandle EXTENSIONHANDLE = new ClassloaderReportHandle(
            EXTENSION, null);
    /**
     * Singleton for system classloader.
     */
    public static final ClassloaderReportHandle SYSTEMHANDLE = new ClassloaderReportHandle(
            SYSTEM, null);
    /**
     * Singleton for the Ant project classloader.
     */
    public static final ClassloaderReportHandle PROJECTHANDLE = new ClassloaderReportHandle(
            ANT_PROJECT, null);
    /**
     * Singleton for the Ant coreloader.
     */
    public static final ClassloaderReportHandle COREHANDLE = new ClassloaderReportHandle(
            ANT_CORE, null);
    /**
     * Singleton for the current threadcontext classloader.
     */
    public static final ClassloaderReportHandle THREADHANDLE = new ClassloaderReportHandle(
            THREAD, null);
    /**
     * Singleton for the current class' classloader.
     */
    public static final ClassloaderReportHandle CURRENTHANDLE = new ClassloaderReportHandle(
            CURRENT, null);
    private static final String[] NAMES = {
        "BootstrapClassloader",
        "ExtensionClassloader",
        "SystemClassloader",
        "ProjectClassloader",
        "CoreLoader",
        "ThreadContextClassloader",
        "Current ClassloaderBase",
        "Referenced as",
        "Loader for defined Task/Type",
        "Parent of",
        "other"};
    private String name;
    private int type;
    /**
     * Constructor.
     * @param type Type of the role.
     * @param name Name of the classloader.
     */
    public ClassloaderReportHandle(int type, String name) {
        this.type = type;
        this.name = name;
    }
    /**
     * Compares this object with the specified object for order.
     * @param to The specified object.
     * @return a negative integer, zero, or a positive integer as
     *     this object is less than, equal to, or greater than the
     *     specified object.
     */
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
    /**
     * Indicates whether some other object is "equal to" this one.
     * @param to Some object.
     * @return True, if to is a handle with the same name and type
     *     as this.
     */
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
    /**
     * Gets the name of the classloader.
     * @return The name.
     */
    public String getName() {
        return name;
    }
    /**
     * Gets the type of the classloader as a String.
     * @return The type.
     */
    public String getType() {
        return NAMES[type];
    }
    /**
     * Gets the hashcode.
     * @return Hashcode compatible to equals.
     */
    public int hashCode() {
        if (name == null) {
            return type;
        }
        return type ^ name.hashCode();
    }
    /**
     * Indicates whether this handle describes a singleton role.
     * @return True, if this handle describes a singleton role; false
     *     otherwise.
     */
    public boolean isPopular() {
        return type <= CURRENT;
    }
    /**
     * Returns a string representation of the object.
     * @return A string representation of the object.
     */
    public String toString() {
        if (name == null) {
            return getType();
        }
        return getType() + " " + getName();
    }
}