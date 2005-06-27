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


public interface ClassloaderReportFormatter {
    public String beginReport(String[] prefix);
    public String endReport(String[] prefix);
    public String beginClassloader(ClassloaderReportHandle name, String[] prefix);
    public String endClassloader(ClassloaderReportHandle name, String[] prefix);
    public String formatClass(Class cl, String[] prefix);
    public String beginEntries(int num, String[] prefix);
    public String endEntries(int num, String[] prefix);
    public String beginAttributes(int num, String[] prefix);
    public String endAttributes(int num, String[] prefix);
    public String beginChildLoaders(int num, String[] prefix);
    public String endChildLoaders(int num, String[] prefix);
    public String beginRoles(int num, String[] prefix);
    public String endRoles(int num, String[] prefix);
    public String beginUnassignedRoles(int num, String[] prefix);
    public String endUnassignedRoles(int num, String[] prefix);
    public String beginErrors(int num, String[] prefix);
    public String endErrors(int num, String[] prefix);
    public String formatEntry(URL url, String[] prefix);
    public String formatEntry(String type, String entry, String[] prefix);
    public String beginPackages(int num, String[] prefix);
    public String endPackages(int num, String[] prefix);
    public String formatPackage(String pkg, String[] prefix);
    public String formatAttribute(String name, String value, String[] prefix);
    public String formatImpliciteParent(ClassloaderReportHandle name, String[] prefix);
    public String formatExpliciteParent(ClassloaderReportHandle name, String[] prefix);
    public String formatError(String msg, String[] prefix);
    public String formatUnassignedRole(ClassloaderReportHandle name, String[] prefix);
    public String formatRole(ClassloaderReportHandle name, String[] prefix);
}