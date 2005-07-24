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

import java.net.URL;

/**
 * Makes reporting destination transparent for reporting objects.
 */
public interface ClassloaderReporter {
    /**
     * Indicates start of attributes-section.
     * @param num Number of elements.
     */
    void beginAttributes(int num);
    /**
     * Indicates start of child-section.
     * @param num Number of elements.
     */
    void beginChildLoaders(int num);
    /**
     * Indicates start of classloader reporting.
     * @param name Handle of the classloader.
     */
    void beginClassloader(ClassloaderReportHandle name);
    /**
     * Indicates start of entries-section.
     * @param num Number of elements.
     */
    void beginEntries(int num);
    /**
     * Indicates start of error-section.
     * @param num Number of elements.
     */
    void beginErrors(int num);
    /**
     * Indicates start of packages-section.
     * @param num Number of elements.
     */
    void beginPackages(int num);
    /**
     * Indicates start of report.
     */
    void beginReport();
    /**
     * Indicates start of role-section.
     * @param num Number of elements.
     */
    void beginRoles(int num);
    /**
     * Indicates start of unassigned-roles-section.
     * @param num Number of elements.
     */
    void beginUnassignedRoles(int num);
    /**
     * Indicates end of attributes-section.
     * @param num Number of elements.
     */
    void endAttributes(int num);
    /**
     * Indicates end of child-section.
     * @param num Number of elements.
     */
    void endChildLoaders(int num);
    /**
     * Indicates end of classloader reporting.
     * @param name Handle of the classloader.
     */
    void endClassloader(ClassloaderReportHandle name);
    /**
     * Indicates end of entries-section.
     * @param num Number of elements.
     */
    void endEntries(int num);
    /**
     * Indicates end of errors-section.
     * @param num Number of elements.
     */
    void endErrors(int num);
    /**
     * Indicates end of packages-section.
     * @param num Number of elements.
     */
    void endPackages(int num);
    /**
     * Indicates end of report.
     */
    void endReport();
    /**
     * Indicates end of roles-section.
     * @param num Number of elements.
     */
    void endRoles(int num);
    /**
     * Indicates end of unassigned-roles-section.
     * @param num Number of elements.
     */
    void endUnassignedRoles(int num);
    /**
     * Reports a single attribute.
     * @param name Name of the attribute.
     * @param value Value of the attribute.
     */
    void reportAttribute(String name, String value);
    /**
     * Reports the classloader's class.
     * @param s Class of the classloader.
     */
    void reportClass(Class s);
    /**
     * Reports a single entry.
     * @param type Type of the entry (f.e. url or file).
     * @param entry The entry.
     */
    void reportEntry(String type, String entry);
    /**
     * Reports a single url entry.
     * Same as reportEntry("url", url.toString());
     * @param url The url.
     */
    void reportEntry(URL url);
    /**
     * Reports an error.
     * @param msg The error message.
     */
    void reportError(String msg);
    /**
     * Reports an explicit parent classloader.
     * @param handle The parent's handle.
     */
    void reportExlicitelyParent(ClassloaderReportHandle handle);
    /**
     * Reports an implicit parent classloader.
     * @param handle The parent's handle.
     */
    void reportImplicitelyParent(ClassloaderReportHandle handle);
    /**
     * Reports a single defined package.
     * @param pkg The package name.
     */
    void reportPackage(String pkg);
    /**
     * Reports a single role.
     * @param handle The role.
     */
    void reportRole(ClassloaderReportHandle handle);
    /**
     * Reports a single unassigned role.
     * @param handle The unassigned role.
     */
    void reportUnassignedRole(ClassloaderReportHandle handle);
}