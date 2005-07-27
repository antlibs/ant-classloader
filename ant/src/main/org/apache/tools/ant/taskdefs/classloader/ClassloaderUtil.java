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

import java.net.URL;
import java.util.List;

import org.apache.tools.ant.taskdefs.classloader.report.ClassloaderReporter;
/**
 * Provides some utility methods for ClassloaderTask and ClassloaderReport.
 * @since Ant1.7
 */
public interface ClassloaderUtil {
    /**
     * Indicates whether a classloader or it's delegation parents
     * contains an entry.
     * @param ctx The context.
     * @param cl The classloader.
     * @param url The entry as an url.
     * @return True if the entry was found, false if not.
     */
    boolean containsEntry(ClassloaderContext ctx, ClassLoader cl,
            String url);
    /**
     * Gets the adapter for the specified classloader and action.
     * @param ctx The context.
     * @param cl The classloader to get an adapter for.
     * @param action The action to get an adapter for or null if
     *     the adapter is needed for common methods.
     * @return The adapter.
     * @throws ClassloaderAdapterException if no adapter can found.
     */
    ClassLoaderAdapter findAdapter(ClassloaderContext ctx,
            ClassLoader cl, ClassLoaderAdapterAction action)
            throws ClassloaderAdapterException;
    /**
     * Gets the adapter for the specified classloader and action.
     * @param ctx The context.
     * @param cl The classloader to get an adapter for.
     * @param action The action to get an adapter for or null if
     *     the adapter is needed for common methods.
     * @param to The reporter to report errors against.
     * @param errPrefix A prefix for error messages.
     * @param errSuffix A suffix for error messages.
     * @return The adapter or null if an error occured.
     */
    ClassLoaderAdapter findAdapter(ClassloaderContext ctx,
            ClassLoader cl, ClassLoaderAdapterAction action,
            ClassloaderReporter to, String errPrefix, String errSuffix);
    /**
     * Gets the adapter for the specified classloader and action.
     * @param ctx The context.
     * @param cl The classloader to get an adapter for.
     * @param action The action to get an adapter for or null if
     *     the adapter is needed for common methods.
     * @param errors A list to add a message if an error occurs.
     * @param errPrefix A prefix for error messages.
     * @param errSuffix A suffix for error messages.
     * @return The adapter or null if an error occured.
     */
    ClassLoaderAdapter findAdapter(ClassloaderContext ctx,
            ClassLoader cl, ClassLoaderAdapterAction action, List errors,
            String errPrefix, String errSuffix);
    /**
     * Gets the Urls of the bootstrap classpath.
     * @return The urls of the bootstrap classpath or null if they
     *    can not determined.
     */
    URL[] getBootstrapClasspathURLs();

}