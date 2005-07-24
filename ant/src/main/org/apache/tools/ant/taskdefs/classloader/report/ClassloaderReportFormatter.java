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
 * Interface for ClassloaderReportFormatter.
 * @since Ant1.7
 */
public interface ClassloaderReportFormatter {
    /**
     * Formats the start of the attributes-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of (element-) formatting methods.
     * @return The formatted String that represents this element.
     */
    String beginAttributes(int num, String[] prefix);
    /**
     * Formats the start of the childs-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of (element-) formatting methods.
     * @return The formatted String that represents this element.
     */
    String beginChildLoaders(int num, String[] prefix);
    /**
     * Formats the start of a childloader-section.
     * @param name Handle of the classloader.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of (element-) formatting methods.
     * @return The formatted String that represents this element.
     */
    String beginClassloader(ClassloaderReportHandle name, String[] prefix);
    /**
     * Formats the start of the entries-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of (element-) formatting methods.
     * @return The formatted String that represents this element.
     */
    String beginEntries(int num, String[] prefix);
    /**
     * Formats the start of the errors-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of (element-) formatting methods.
     * @return The formatted String that represents this element.
     */
    String beginErrors(int num, String[] prefix);
    /**
     * Formats the start of packages-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of (element-) formatting methods.
     * @return The formatted String that represents this element.
     */
    String beginPackages(int num, String[] prefix);
    /**
     * Formats the start of the report.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of (element-) formatting methods.
     * @return The formatted String that represents this element.
     */
    String beginReport(String[] prefix);
    /**
     * Formats the start of the roles-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of (element-) formatting methods.
     * @return The formatted String that represents this element.
     */
    String beginRoles(int num, String[] prefix);
    /**
     * Formats the start of the unassigned-roles-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of (element-) formatting methods.
     * @return The formatted String that represents this element.
     */
    String beginUnassignedRoles(int num, String[] prefix);
    /**
     * Formats the end of the attributes-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    String endAttributes(int num, String[] prefix);
    /**
     * Formats the end of the childs-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    String endChildLoaders(int num, String[] prefix);
    /**
     * Formats the end of the classloader-section.
     * @param name Name of the classloader.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    String endClassloader(ClassloaderReportHandle name, String[] prefix);
    /**
     * Formats the end of the entries-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    String endEntries(int num, String[] prefix);
    /**
     * Formats the end of the errors-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    String endErrors(int num, String[] prefix);
    /**
     * Formats the end of the packages-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    String endPackages(int num, String[] prefix);
    /**
     * Formats the end of the report.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    String endReport(String[] prefix);
    /**
     * Formats the end of the roles-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    String endRoles(int num, String[] prefix);
    /**
     * Formats the end of the unassigned-roles-section.
     * @param num Number of elements.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    String endUnassignedRoles(int num, String[] prefix);
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
    String formatAttribute(String name, String value, String[] prefix);
    /**
     * Formats the class of a classloader.
     * @param cl The class of the classloader.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    String formatClass(Class cl, String[] prefix);
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
    String formatEntry(String type, String entry, String[] prefix);
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
    String formatEntry(URL url, String[] prefix);
    /**
     * Formats a single error message.
     * @param msg Error message.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    String formatError(String msg, String[] prefix);
    /**
     * Formats a classloader's explicite parent classloader.
     * @param name Handle of the parent classloader.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    String formatExpliciteParent(ClassloaderReportHandle name,
            String[] prefix);
    /**
     * Formats a classloader's implicite parent classloader.
     * @param name Handle of the parent classloader.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    String formatImpliciteParent(ClassloaderReportHandle name,
            String[] prefix);
    /**
     * Formats a single defined package.
     * @param pkg A defined package.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    String formatPackage(String pkg, String[] prefix);
    /**
     * Formats a role.
     * @param name Role of the classloader.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    String formatRole(ClassloaderReportHandle name, String[] prefix);
    /**
     * Formats an unassigned role.
     * @param name Unassigned role.
     * @param prefix An array containing exactly one element,
     *     that is a non-null String. This string is the prefix for every
     *     new line. The method might replace the string for subsequent calls
     *     of formatting methods.
     * @return The formatted String that represents this element.
     */
    String formatUnassignedRole(ClassloaderReportHandle name,
            String[] prefix);
}