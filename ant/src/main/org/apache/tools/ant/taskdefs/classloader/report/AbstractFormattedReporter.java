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
 * Base implementation for formatted reporters.
 * @since Ant1.7
 */
public abstract class AbstractFormattedReporter implements ClassloaderReporter {
    private ClassloaderReportFormatter fmt;
    private String[] prefix = {""};
    /**
     * Constructor for derived implementations.
     * @param fmt The formatter to use.
     */
    protected AbstractFormattedReporter(ClassloaderReportFormatter fmt) {
        this.fmt = fmt;
    }
    /**
     * Indicates start of attributes-section.
     * @param num Number of elements.
     */
    public void beginAttributes(int num) {
        report(fmt.beginAttributes(num, prefix));
    }
    /**
     * Indicates start of child-section.
     * @param num Number of elements.
     */
    public void beginChildLoaders(int num) {
        report(fmt.beginChildLoaders(num, prefix));
    }
    /**
     * Indicates start of classloader reporting.
     * @param name Handle of the classloader.
     */
    public void beginClassloader(ClassloaderReportHandle name) {
        report(fmt.beginClassloader(name, prefix));
    }
    /**
     * Indicates start of entries-section.
     * @param num Number of elements.
     */
    public void beginEntries(int num) {
        report(fmt.beginEntries(num, prefix));
    }
    /**
     * Indicates start of error-section.
     * @param num Number of elements.
     */
    public void beginErrors(int num) {
        report(fmt.beginErrors(num, prefix));
    }
    /**
     * Indicates start of packages-section.
     * @param num Number of elements.
     */
    public void beginPackages(int num) {
        report(fmt.beginPackages(num, prefix));
    }
    /**
     * Indicates start of report.
     */
    public void beginReport() {
        report(fmt.beginReport(prefix));
    }
    /**
     * Indicates start of role-section.
     * @param num Number of elements.
     */
    public void beginRoles(int num) {
        report(fmt.beginRoles(num, prefix));
    }
    /**
     * Indicates start of unassigned-roles-section.
     * @param num Number of elements.
     */
    public void beginUnassignedRoles(int num) {
        report(fmt.beginUnassignedRoles(num, prefix));
    }
    /**
     * Indicates end of attributes-section.
     * @param num Number of elements.
     */
    public void endAttributes(int num) {
        report(fmt.endAttributes(num, prefix));
    }
    /**
     * Indicates end of child-section.
     * @param num Number of elements.
     */
    public void endChildLoaders(int num) {
        report(fmt.endChildLoaders(num, prefix));
    }
    /**
     * Indicates end of classloader reporting.
     * @param name Handle of the classloader.
     */
    public void endClassloader(ClassloaderReportHandle name) {
        report(fmt.endClassloader(name, prefix));
    }
    /**
     * Indicates end of entries-section.
     * @param num Number of elements.
     */
    public void endEntries(int num) {
        report(fmt.endEntries(num, prefix));
    }
    /**
     * Indicates end of errors-section.
     * @param num Number of elements.
     */
    public void endErrors(int num) {
        report(fmt.endErrors(num, prefix));
    }
    /**
     * Indicates end of packages-section.
     * @param num Number of elements.
     */
    public void endPackages(int num) {
        report(fmt.endPackages(num, prefix));
    }
    /**
     * Indicates end of report.
     */
    public void endReport() {
        report(fmt.endReport(prefix));
    }
    /**
     * Indicates end of roles-section.
     * @param num Number of elements.
     */
    public void endRoles(int num) {
        report(fmt.endRoles(num, prefix));
    }
    /**
     * Indicates end of unassigned-roles-section.
     * @param num Number of elements.
     */
    public void endUnassignedRoles(int num) {
        report(fmt.endUnassignedRoles(num, prefix));
    }
    /**
     * Writes a message line to the reporting dest.
     * @param s The message line to report.
     */
    protected abstract void report(String s);
    /**
     * Reports a single attribute.
     * @param name Name of the attribute.
     * @param value Value of the attribute.
     */
    public void reportAttribute(String name, String value) {
        report(fmt.formatAttribute(name, value, prefix));
    }
    /**
     * Reports the classloader's class.
     * @param s Class of the classloader.
     */
    public void reportClass(Class s) {
        report(fmt.formatClass(s, prefix));
    }
    /**
     * Reports a single entry.
     * @param type Type of the entry (f.e. url or file).
     * @param entry The entry.
     */
    public void reportEntry(String type, String entry) {
        report(fmt.formatEntry(type, entry, prefix));
    }
    /**
     * Reports a single url entry.
     * Same as reportEntry("url", url.toString());
     * @param url The url.
     */
    public void reportEntry(URL url) {
        report(fmt.formatEntry(url, prefix));
    }
    /**
     * Reports an error.
     * @param msg The error message.
     */
    public void reportError(String msg) {
        report(fmt.formatError(msg, prefix));
    }
    /**
     * Reports an explicit parent classloader.
     * @param handle The parent's handle.
     */
    public void reportExlicitelyParent(ClassloaderReportHandle handle) {
        report(fmt.formatExpliciteParent(handle, prefix));
    }
    /**
     * Reports an implicit parent classloader.
     * @param handle The parent's handle.
     */
    public void reportImplicitelyParent(ClassloaderReportHandle handle) {
        report(fmt.formatImpliciteParent(handle, prefix));
    }
    /**
     * Reports a single defined package.
     * @param pkg The package name.
     */
    public void reportPackage(String pkg) {
        report(fmt.formatPackage(pkg, prefix));
    }
    /**
     * Reports a single role.
     * @param handle The role.
     */
    public void reportRole(ClassloaderReportHandle handle) {
        report(fmt.formatRole(handle, prefix));
    }
    /**
     * Reports a single unassigned role.
     * @param handle The unassigned role.
     */
    public void reportUnassignedRole(ClassloaderReportHandle handle) {
        report(fmt.formatUnassignedRole(handle, prefix));
    }
}