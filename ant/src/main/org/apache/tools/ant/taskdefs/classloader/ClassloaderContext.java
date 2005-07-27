/*
 * Copyright  2004-2005 The Apache Software Foundation
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
/**
 * Context for Classloader-related action.
 * @since Ant1.7
 */
public interface ClassloaderContext {
    /**
     * Context for Classloader-related create/modify action.
     * @since Ant1.7
     */
    public static interface CreateModify extends ClassloaderContext {
        /**
         * Gets the ant project.
         * NOTE: This method should only be used for
         * Ant specific Classloaders.
         * @return The Ant project or null if not available.
         */
        Object getAntProject();
        /**
         * Gets the classpath to create/append.
         * @return The classpath to create/append.
         */
        String[] getClasspathFiles();
        /**
         * Gets the classpath to create/append.
         * @return The classpath to create/append.
         */
        String[] getClasspathURLs();
        /**
         * Gets the name of the loader.
         * @return The name of the loader.
         */
        String getLoaderName();
        /**
         * Gets the parameters for a new classloader.
         * @return The paraemeters.
         */
        ClassLoaderParameters getParameters();
        /**
         * Gets the loader used as parent for a new classloader.
         * @return Classloader or null if not defined.
         */
        ClassLoader getParentLoader();
        /**
         * Gets the loader used as superloader for a new classloader.
         * @return Classloader or null if not defined.
         */
        ClassLoader getSuperLoader();
        /**
         * Handles a classpath entry.
         * @param cl The Classloader.
         * @param entryUrl The entry.
         * @return Indicates, whether the adapter should add the duplicate entry to
         *         the existing classloader or not.
         */
        boolean handleClasspathEntry(ClassLoader cl, String entryUrl);
    }
    /**
     * Context for ClassloaderAdapter-related report action.
     * @since Ant1.7
     */
    public static interface Report extends ClassloaderContext {
        /**
         * Indicates whether defined packages should be reported.
         * @return True, if defined packages should be reported;
         *   false otherwise.
         */
        boolean isReportPackages();
    }
    /**
     * Gets the handlerset.
     * @return The handlerset.
     */
    ClassLoaderHandlerSet getHandlerSet();
    /**
     * Gets the utilities.
     * @return The utilities.
     */
    ClassloaderUtil getUtil();
    /**
     * Gets the URL utilities.
     * @return The URL utilities.
     */
    ClassloaderURLUtil getURLUtil();
    /**
     * Handles a debug message.
     * @param msg The message.
     */
    void handleDebug(String msg);
    /**
     * Handles an error message.
     * @param msg The message.
     */
    void handleError(String msg);
    /**
     * Handles an error message.
     * @param msg The message.
     * @param ex The causing exception.
     */
    void handleError(String msg, Throwable ex);
    /**
     * Handles a warning message.
     * @param msg The message.
     */
    void handleWarning(String msg);
}
