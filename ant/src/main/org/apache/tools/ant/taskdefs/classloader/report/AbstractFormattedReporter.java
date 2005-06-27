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
 * makes reporting destination transparent for reporting objects.
 */
public abstract class AbstractFormattedReporter implements ClassloaderReporter {
    private ClassloaderReportFormatter fmt;
    private String[] prefix = {""};
    public AbstractFormattedReporter(ClassloaderReportFormatter fmt) {
        this.fmt = fmt;
    }
    /**
     * writes a message line to the reporting dest.
     * @param s the message line to report.
     */
    protected abstract void report(String s);
    /* (non-Javadoc)
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#reportClass(java.lang.Class)
     */
    public void reportClass(Class s) {
        report(fmt.formatClass(s, prefix));
    }
    /* (non-Javadoc)
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#beginReport()
     */
    public void beginReport() {
        report(fmt.beginReport(prefix));
    }
    /* (non-Javadoc)
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#endReport()
     */
    public void endReport() {
        report(fmt.endReport(prefix));
    }
    /* (non-Javadoc)
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#beginClassloader(org.apache.tools.ant.taskdefs.ClassloaderBase.ReportHandle)
     */
    public void beginClassloader(ClassloaderReportHandle name) {
        report(fmt.beginClassloader(name, prefix));
    }
    /* (non-Javadoc)
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#endClassloader(org.apache.tools.ant.taskdefs.ClassloaderBase.ReportHandle)
     */
    public void endClassloader(ClassloaderReportHandle name) {
        report(fmt.endClassloader(name, prefix));
    }
    /* (non-Javadoc)
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#beginEntries(int)
     */
    public void beginEntries(int num) {
        report(fmt.beginEntries(num, prefix));
    }
    /* (non-Javadoc)
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#endEntries(int)
     */
    public void endEntries(int num) {
        report(fmt.endEntries(num, prefix));
    }
    /* (non-Javadoc)
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#reportEntry(java.net.URL)
     */
    public void reportEntry(URL url) {
        report(fmt.formatEntry(url, prefix));
    }
    /* (non-Javadoc)
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#reportEntry(java.lang.String, java.lang.String)
     */
    public void reportEntry(String type, String entry) {
        report(fmt.formatEntry(type, entry, prefix));
    }
    /* (non-Javadoc)
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#beginPackages(int)
     */
    public void beginPackages(int num) {
        report(fmt.beginPackages(num, prefix));
    }
    /* (non-Javadoc)
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#endPackages(int)
     */
    public void endPackages(int num) {
        report(fmt.endPackages(num, prefix));
    }
    /* (non-Javadoc)
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#reportPackage(java.lang.String)
     */
    public void reportPackage(String pkg) {
        report(fmt.formatPackage(pkg, prefix));
    }
    /* (non-Javadoc)
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#reportAttribute(java.lang.String, java.lang.String)
     */
    public void reportAttribute(String name, String value) {
        report(fmt.formatAttribute(name, value, prefix));
    }
    /* (non-Javadoc)
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#reportExlicitelyParent(org.apache.tools.ant.taskdefs.ClassloaderBase.ReportHandle)
     */
    public void reportExlicitelyParent(ClassloaderReportHandle handle) {
        report(fmt.formatExpliciteParent(handle, prefix));
    }
    /* (non-Javadoc)
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#reportImplicitelyParent(org.apache.tools.ant.taskdefs.ClassloaderBase.ReportHandle)
     */
    public void reportImplicitelyParent(ClassloaderReportHandle handle) {
        report(fmt.formatImpliciteParent(handle, prefix));
    }
    /* (non-Javadoc)
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#reportError(java.lang.String)
     */
    public void reportError(String msg) {
        report(fmt.formatError(msg, prefix));
    }
    /* (non-Javadoc)
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#reportUnassignedPopularLoader(org.apache.tools.ant.taskdefs.ClassloaderBase.ReportHandle)
     */
    public void reportUnassignedRole(ClassloaderReportHandle handle) {
        report(fmt.formatUnassignedRole(handle, prefix));
    }
    /* (non-Javadoc)
     * @see org.apache.tools.ant.taskdefs.ClassLoaderReporter#reportRole(org.apache.tools.ant.taskdefs.ClassloaderBase.ReportHandle)
     */
    public void reportRole(ClassloaderReportHandle handle) {
        report(fmt.formatRole(handle, prefix));
    }
    public void beginAttributes(int num) {
        report(fmt.beginAttributes(num, prefix));
        
    }
    public void endAttributes(int num) {
        report(fmt.endAttributes(num, prefix));
    }
    public void beginChildLoaders(int num) {
        report(fmt.beginChildLoaders(num, prefix));
    }
    public void endChildLoaders(int num) {
        report(fmt.endChildLoaders(num, prefix));
    }
    public void beginErrors(int num) {
        report(fmt.beginErrors(num, prefix));
    }
    public void endErrors(int num) {
        report(fmt.endErrors(num, prefix));
    }
    public void beginUnassignedRoles(int num) {
        report(fmt.beginUnassignedRoles(num, prefix));
    }
    public void endUnassignedRoles(int num) {
        report(fmt.endUnassignedRoles(num, prefix));
    }
    public void beginRoles(int num) {
        report(fmt.beginRoles(num, prefix));
    }
    public void endRoles(int num) {
        report(fmt.endRoles(num, prefix));
    }
}