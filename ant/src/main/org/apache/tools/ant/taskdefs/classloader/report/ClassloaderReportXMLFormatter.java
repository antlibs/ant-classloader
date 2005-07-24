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

public class ClassloaderReportXMLFormatter implements ClassloaderReportFormatter {
    private String TAB = "  ";
    public String beginAttributes(int num, String[] prefix) {
        String result = prefix[0] + "<attributes count=\"" + num + "\">";
        incPrefix(prefix);
        return result;
    }
    public String beginChildLoaders(int num, String[] prefix) {
        String result = prefix[0] + "<childs count=\"" + num + "\">";
        incPrefix(prefix);
        return result;
    }
    public String beginClassloader(ClassloaderReportHandle name, String[] prefix) {
        String result = prefix[0]
                + "<classloader type=\""
                + name.getType()
                + ((name.getName() != null) ? "\" name=\"" + name.getName()
                        : "") + "\">";
        incPrefix(prefix);
        return result;
    }
    public String beginEntries(int num, String[] prefix) {
        String result = prefix[0] + "<entries count=\"" + num + "\">";
        incPrefix(prefix);
        return result;
    }
    public String beginErrors(int num, String[] prefix) {
        String result = prefix[0] + "<errors count=\"" + num + "\">";
        incPrefix(prefix);
        return result;
    }
    public String beginPackages(int num, String[] prefix) {
        String result = prefix[0] + "<packages count=\"" + num + "\">";
        incPrefix(prefix);
        return result;
    }
    public String beginReport(String[] prefix) {
        String result = prefix[0] + "<classloaderreport>";
        incPrefix(prefix);
        return result;
    }
    public String beginRoles(int num, String[] prefix) {
        String result = prefix[0] + "<roles count=\"" + num + "\">";
        incPrefix(prefix);
        return result;
    }
    public String beginUnassignedRoles(int num, String[] prefix) {
        String result = prefix[0] + "<unassigned-roles count=\"" + num + "\">";
        incPrefix(prefix);
        return result;
    }
    private void decPrefix(String[] prefix) {
        prefix[0] = prefix[0].substring(0, prefix[0].length() - TAB.length());
    }
    public String endAttributes(int num, String[] prefix) {
        decPrefix(prefix);
        return prefix[0] + "</attributes>";
    }
    public String endChildLoaders(int num, String[] prefix) {
        decPrefix(prefix);
        return prefix[0] + "</childs>";
    }
    public String endClassloader(ClassloaderReportHandle name, String[] prefix) {
        decPrefix(prefix);
        return prefix[0] + "</classloader>";
    }
    public String endEntries(int num, String[] prefix) {
        decPrefix(prefix);
        return prefix[0] + "</entries>";
    }
    public String endErrors(int num, String[] prefix) {
        decPrefix(prefix);
        return prefix[0] + "</errors>";
    }
    public String endPackages(int num, String[] prefix) {
        decPrefix(prefix);
        return prefix[0] + "</packages>";
    }
    public String endReport(String[] prefix) {
        decPrefix(prefix);
        return prefix[0] + "</classloaderreport>";
    }
    public String endRoles(int num, String[] prefix) {
        decPrefix(prefix);
        return prefix[0] + "</roles>";
    }

    public String endUnassignedRoles(int num, String[] prefix) {
        decPrefix(prefix);
        return prefix[0] + "</unassigned-roles>";
    }
    public String formatAttribute(String name, String value, String[] prefix) {
        return prefix[0] + "<attribute name=\"" + name + "\" value=\"" + value
                + "\"/>";
    }
    public String formatClass(Class cl, String[] prefix) {
        return prefix[0] + "<class name=\"" + cl.getName() + "\"/>";
    }
    public String formatEntry(String type, String entry, String[] prefix) {
        return prefix[0] + "<entry " + type + "=\"" + entry + "\"/>";
    }
    public String formatEntry(URL url, String[] prefix) {
        return prefix[0] + "<entry url=\"" + url + "\"/>";
    }
    public String formatError(String msg, String[] prefix) {
        return prefix[0] + "<error msg=\"" + msg + "\"/>";
    }
    public String formatExpliciteParent(ClassloaderReportHandle name,
            String[] prefix) {
        return prefix[0]
                + "<parent definition=\"explicitely\" type=\""
                + name.getType()
                + ((name.getName() != null) ? "\" name=\"" + name.getName()
                        : "") + "\"/>";
    }
    public String formatImpliciteParent(ClassloaderReportHandle name,
            String[] prefix) {
        return prefix[0]
                + "<parent definition=\"default\" type=\""
                + name.getType()
                + ((name.getName() != null) ? "\" name=\"" + name.getName()
                        : "") + "\"/>";
    }
    public String formatPackage(String pkg, String[] prefix) {
        return prefix[0] + "<package name=\"" + pkg + "\"/>";
    }
    public String formatRole(ClassloaderReportHandle name, String[] prefix) {
        return prefix[0]
                + "<role type=\""
                + name.getType()
                + ((name.getName() != null) ? "\" name=\"" + name.getName()
                        : "") + "\"/>";
    }
    public String formatUnassignedRole(ClassloaderReportHandle name,
            String[] prefix) {
        return prefix[0]
                + "<role type=\""
                + name.getType()
                + ((name.getName() != null) ? "\" name=\"" + name.getName()
                        : "") + "\"/>";

    }
    private void incPrefix(String[] prefix) {
        prefix[0] += TAB;
    }
}