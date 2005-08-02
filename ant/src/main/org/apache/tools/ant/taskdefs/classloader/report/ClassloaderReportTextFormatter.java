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
 * Writes a report in text format.
 * @since Ant1.7
 */
public class ClassloaderReportTextFormatter implements ClassloaderReportFormatter {
    private static final String PREFIX_ATTRIBUTES = "  - ";
    private static final String PREFIX_CHILDS = "  - ";
    private static final String PREFIX_CLASSLOADER = "  ";
    private static final String PREFIX_ENTRIES = "  - ";
    private static final String PREFIX_ERRORS = "  - ";
    private static final String PREFIX_PACKAGES = "  - ";
    private static final String PREFIX_REPORT = "";
    private static final String PREFIX_ROLES = "  - ";
    private static final String PREFIX_UNASSIGNED_ROLES = "  - ";
    /**
     * Formats the start of the attributes-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of (element-) formatting methods.
     * @return The formatted String that represents this element.
     */
    public String beginAttributes(int num, String[] prefix) {
        String result = prefix[0] + "attributes: " + num + " entries";
        incPrefix(prefix, PREFIX_ATTRIBUTES);
        return result;
    }
    /**
     * Formats the start of the childs-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of (element-) formatting methods.
     * @return The formatted String that represents this element.
     */
    public String beginChildLoaders(int num, String[] prefix) {
        String result = prefix[0] + "childs:     " + num + " entries";
        incPrefix(prefix, PREFIX_CHILDS);
        return result;
    }
    /**
     * Formats the start of a childloader-section.
     * @param name Handle of the classloader.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of (element-) formatting methods.
     * @return The formatted String that represents this element.
     */
    public String beginClassloader(ClassloaderReportHandle name, String[] prefix) {
        String result = prefix[0] + "classloader: " + getRole(name);
        incPrefix(prefix, PREFIX_CLASSLOADER);
        return result;
    }
    /**
     * Formats the start of the entries-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of (element-) formatting methods.
     * @return The formatted String that represents this element.
     */
    public String beginEntries(int num, String[] prefix) {
        String result = prefix[0] + "entries:    " + num + " entries";
        incPrefix(prefix, PREFIX_ENTRIES);
        return result;
    }
    /**
     * Formats the start of the errors-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of (element-) formatting methods.
     * @return The formatted String that represents this element.
     */
    public String beginErrors(int num, String[] prefix) {
        String result = prefix[0] + "errors: " + num + " entries";
        incPrefix(prefix, PREFIX_ERRORS);
        return result;
    }
    /**
     * Formats the start of packages-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of (element-) formatting methods.
     * @return The formatted String that represents this element.
     */
    public String beginPackages(int num, String[] prefix) {
        String result = prefix[0] + "packages:   " + num + " entries";
        incPrefix(prefix, PREFIX_PACKAGES);
        return result;
    }
    /**
     * Formats the start of the report.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of (element-) formatting methods.
     * @return The formatted String that represents this element.
     */
    public String beginReport(String[] prefix) {
        String result = prefix[0] + "classloaderreport";
        incPrefix(prefix, PREFIX_REPORT);
        return result;
    }
    /**
     * Formats the start of the roles-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of (element-) formatting methods.
     * @return The formatted String that represents this element.
     */
    public String beginRoles(int num, String[] prefix) {
        String result = prefix[0] + "roles:      " + num + " entries";
        incPrefix(prefix, PREFIX_ROLES);
        return result;
    }
    /**
     * Formats the start of the unassigned-roles-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of (element-) formatting methods.
     * @return The formatted String that represents this element.
     */
    public String beginUnassignedRoles(int num, String[] prefix) {
        String result = prefix[0] + "unassigned roles: " + num + " entries";
        incPrefix(prefix, PREFIX_UNASSIGNED_ROLES);
        return result;
    }
    private void decPrefix(String[] prefix, String pref) {
        prefix[0] = prefix[0].substring(0, prefix[0].length() - pref.length());
    }
    /**
     * Formats the end of the attributes-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    public String endAttributes(int num, String[] prefix) {
        decPrefix(prefix, PREFIX_ATTRIBUTES);
        return null;
    }
    /**
     * Formats the end of the childs-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    public String endChildLoaders(int num, String[] prefix) {
        decPrefix(prefix, PREFIX_CHILDS);
        return null;
    }
    /**
     * Formats the end of the classloader-section.
     * @param name Name of the classloader.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    public String endClassloader(ClassloaderReportHandle name, String[] prefix) {
        decPrefix(prefix, PREFIX_CLASSLOADER);
        return prefix[0] + "----- end of " + getRole(name);
    }
    /**
     * Formats the end of the entries-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    public String endEntries(int num, String[] prefix) {
        decPrefix(prefix, PREFIX_ENTRIES);
        return null;
    }
    /**
     * Formats the end of the errors-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    public String endErrors(int num, String[] prefix) {
        decPrefix(prefix, PREFIX_ERRORS);
        return null;
    }
    /**
     * Formats the end of the packages-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    public String endPackages(int num, String[] prefix) {
        decPrefix(prefix, PREFIX_PACKAGES);
        return null;
    }
    /**
     * Formats the end of the report.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    public String endReport(String[] prefix) {
        decPrefix(prefix, PREFIX_REPORT);
        return prefix[0] + "end of classloaderreport";
    }
    /**
     * Formats the end of the roles-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    public String endRoles(int num, String[] prefix) {
        decPrefix(prefix, PREFIX_ROLES);
        return null;
    }
    /**
     * Formats the end of the unassigned-roles-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    public String endUnassignedRoles(int num, String[] prefix) {
        decPrefix(prefix, PREFIX_UNASSIGNED_ROLES);
        return null;
    }
    /**
     * Formats a single attribute.
     * @param name Name of the attribute.
     * @param value Value of the attribute.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    public String formatAttribute(String name, String value, String[] prefix) {
        return prefix[0] + name + " = " + value;
    }
    /**
     * Formats a single child entry.
     * @param name Role of the child.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    public String formatChild(ClassloaderReportHandle name, String[] prefix) {
        return prefix[0] + getRole(name);
    }
    /**
     * Formats the class of a classloader.
     * @param cl The class of the classloader.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    public String formatClass(Class cl, String[] prefix) {
        return prefix[0] + "class:      " + cl.getName();
    }
    /**
     * Formats a single entry.
     * @param type Type (protocol) of the entry.
     * @param entry Value of the entry.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    public String formatEntry(String type, String entry, String[] prefix) {
        return prefix[0] + type + "=" + entry;
    }
    /**
     * Formats a single url-entry.
     * Should be same as formatEntry("url", url.toString, prefix).
     * @param url Url of the entry.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    public String formatEntry(URL url, String[] prefix) {
        return formatEntry("url", String.valueOf(url), prefix);
    }
    /**
     * Formats a single error message.
     * @param msg Error message.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    public String formatError(String msg, String[] prefix) {
        return prefix[0] + msg;
    }
    /**
     * Formats a classloader's explicite parent classloader.
     * @param name Handle of the parent classloader.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    public String formatExpliciteParent(ClassloaderReportHandle name,
            String[] prefix) {
        return prefix[0] + "parent:     " + getRole(name);
    }
    /**
     * Formats a classloader's implicite parent classloader.
     * @param name Handle of the parent classloader.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    public String formatImpliciteParent(ClassloaderReportHandle name,
            String[] prefix) {
        return prefix[0] + "parent:     NULL (Default: " + getRole(name) + ")";
    }
    /**
     * Formats a single defined package.
     * @param pkg A defined package.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    public String formatPackage(String pkg, String[] prefix) {
        return prefix[0] + pkg;
    }
    /**
     * Formats a role.
     * @param name Role of the classloader.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    public String formatRole(ClassloaderReportHandle name, String[] prefix) {
        return prefix[0] + getRole(name);
    }
    /**
     * Formats an unassigned role.
     * @param name Unassigned role.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    public String formatUnassignedRole(ClassloaderReportHandle name,
            String[] prefix) {
        return prefix[0] + getRole(name);
    }
    private String getRole(ClassloaderReportHandle name) {
        return name.getType()
            + ((name.getName() != null) ? " " + name.getName() : "");
    }
    private void incPrefix(String[] prefix, String inc) {
        prefix[0] += inc ;
    }
}