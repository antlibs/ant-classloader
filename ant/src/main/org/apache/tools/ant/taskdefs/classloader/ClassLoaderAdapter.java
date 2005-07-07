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

import java.util.Map;

import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReportHandle;
import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReporter;

/**
 * ClassLoaderAdapter used to define classloader interaction.
 */
public interface ClassLoaderAdapter {
    /**
     * add classloader to the report queue.
     * the adapter should call task.addLoaderToReport to add a loader.
     * @param task the calling ClassloaderBase-task.
     * @param classloader the classloader to analyze.
     * @param name the name of the classloader instance.
     * @param loaderStack loaderStack to pass to ClassloaderBase.addLoaderToReport.
     * @param loaderNames loaderNames to pass to ClassloaderBase.addLoaderToReport.
     */
    void addReportable(
        ClassLoaderAdapterContext.Report task,
        ClassLoader classloader,
        ClassloaderReportHandle role,
        Map loaderStack,
        Map loaderNames);
    /**
     * Appends a classpath to an existing classloader instance.
     * @param task the calling ClassloaderBase-task.
     * @param classloader the classloader instance to append the path to.
     * @return The ClassLoader instance or null if an error occured.
     */
    boolean appendClasspath(ClassLoaderAdapterContext.CreateModify task, ClassLoader classloader);
    /**
     * Creates a classloader instance.
     * @param task the calling ClassloaderBase-task.
     * @return the newly created ClassLoader instance or null if an error occured.
     */
    ClassLoader createClassLoader(ClassLoaderAdapterContext.CreateModify task);
    /**
     * Returns the actual classpath of a classloader instance.
     * @param task the calling ClassloaderBase-task.
     * @param classloader the classloader instance to get the path from.
     * @param defaultToFile if true, returned url-elements with file protocol
     *         should trim the leading 'file:/' prefix.
     * @return the path or null if an error occured
     */
    String[] getClasspath(
        ClassLoaderAdapterContext task,
        ClassLoader classloader,
        boolean defaultToFile);
    /**
     * Checks whether the adapter supports an action.
     * @param action the action to check.
     * @return true, if action is supported.
     */
    boolean isSupported(ClassLoaderAdapterAction action);
    /**
     * performs additional reporting.
     * @param to the Reporter Object to report to.
     * @param task the calling ClassloaderBase-task.
     * @param classloader the classloader instance to report about.
     * @param name the name of the classloader instance.
     */
    void report(
        ClassloaderReporter to,
        ClassLoaderAdapterContext.Report task,
        ClassLoader classloader,
        ClassloaderReportHandle name);
    /**
     * Gets the default parent classloader in the case, this classloader's
     * <code>getParent()</code> method returns <code>null</code>. 
     * @return The classloader implicitely used as parent loader or null
     * if the bootstrap loader is the implicitely parent loader. 
     */
    ClassLoader getDefaultParent();
    /**
     * Gets the parent classloader. 
     * Necessary, because AntClassLoader's <code>getParent()</code> method
     * returns an invalid result. 
     * @param classLoader The classloader to get the parent from.
     * @return The classloader explicitely used as parent loader or <code>null</code>.
     */
    ClassLoader getParent(ClassLoader classLoader);
    /**
     * Gets the packages, defined by this classloader or its parents. 
     * @param context The context of this method call.
     * @param classLoader The classloader to get the packages from.
     * @param name The symbolic name of this classloader.
     * @return The packages defined by this classloader or null if an error occurs.
     */
    Package[] getPackages(
            ClassLoaderAdapterContext.Report context,
            ClassLoader classLoader,
            ClassloaderReportHandle name);
}